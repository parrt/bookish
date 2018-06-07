package us.parr.bookish;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.stringtemplate.v4.ST;
import us.parr.bookish.entity.EntityDef;
import us.parr.bookish.model.BookFrontMatterFile;
import us.parr.bookish.model.Chapter;
import us.parr.bookish.model.OutputModelObject;
import us.parr.bookish.parse.BookishLexer;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.ChapDocInfo;
import us.parr.bookish.parse.DocInfo;
import us.parr.bookish.parse.RootDocInfo;
import us.parr.bookish.semantics.Article;
import us.parr.bookish.semantics.Artifact;
import us.parr.bookish.semantics.Book;
import us.parr.bookish.semantics.ConvertLatexToImageListener;
import us.parr.bookish.semantics.DefEntitiesListener;
import us.parr.bookish.semantics.DefPythonEntitiesListener;
import us.parr.bookish.semantics.PySnippetExecutor;
import us.parr.bookish.translate.ModelConverter;
import us.parr.bookish.translate.Translator;
import us.parr.lib.ParrtCollections;
import us.parr.lib.ParrtIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static us.parr.lib.ParrtIO.stripFileExtension;
import static us.parr.lib.ParrtStrings.stripQuotes;
import static us.parr.lib.ParrtSys.execCommandLine;

/**
 *
 * Sample invocation:
 *
 * java us.parr.bookist.Tool -target html -o /tmp/gradient-boosting /Users/parrt/github/ml-articles/gradient-boosting/article.xml
 *
 * java us.parr.bookist.Tool -target html -o /tmp/mlbook /Users/parrt/github/mlbook-private/content/book.xml
 *
 * java us.parr.bookist.Tool -target html \
 *                           -o /tmp/simple
 *                           /Users/parrt/github/bookish/examples/simple/book.xml
 */
public class Tool {
	public static final String BUILD_DIR = "/tmp/build";

	public static BaseErrorListener getErrorListener() {
		return getErrorListener(null);
	}

	public static BaseErrorListener getErrorListener(String location) {
		return new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
				String srcName = ParrtIO.basename(recognizer.getInputStream().getSourceName());
				if ( srcName.startsWith("<") && location!=null ) {
					srcName = location;
				}
				System.err.println(srcName+" line "+line+":"+charPositionInLine+" "+msg);
			}
		};
	}

	public Map<String, Object> options = new HashMap<>();

	public static final Set<String> validOptions =
		new HashSet<String>() {{
			add("-o");          // output dir
			add("-target");     // html or latex
		}};

	public String target;
	public String inputDir;
	public String outputDir;
	public String rootfile;

	public static void main(String[] args) throws Exception {
		Tool tool = new Tool();
		tool.process(args);
		System.out.println("DONE");
		System.out.println();
	}

	public void process(String[] args) throws Exception {
		options = handleArgs(args);
		rootfile = option("rootfile");

		inputDir = new File(rootfile).getParent();
		outputDir = option("o");
		target = option("target");

		ParrtIO.mkdir(outputDir+"/images");
		String snippetsDir = getBuildDir()+"/snippets";
		ParrtIO.mkdir(snippetsDir);

		processAllDocuments(rootfile);
	}

	public void processAllDocuments(String rootfile) throws IOException {
		Artifact artifact = parseAllFiles(rootfile);

		defineAllEntities(artifact);
		verifyAllEntityRefs(artifact);
		convertAllLatexToImages(artifact);
		execAllPythonSnippets(artifact);

		translateAndGenerate(artifact);

		execCommandLine(String.format("cp -r %s/css %s", inputDir, outputDir));
		copyImages(artifact, inputDir, outputDir);
	}

	public void translateAndGenerate(Artifact artifact) {
		ModelConverter converter = new ModelConverter(artifact.templates);

		// FIRST: CREATE MODEL TREE FROM PARSE TREE

		BookFrontMatterFile mainFile = null;
		if ( artifact instanceof Book ) {
			Translator translator = new Translator(artifact.rootdoc);
			translator.translateXMLAttributes(artifact.rootdoc);
			mainFile = new BookFrontMatterFile((Book)artifact);
		}

		List<Chapter> chapterModels = new ArrayList<>();
		for (ChapDocInfo doc : artifact.docs) {
			Translator translator = new Translator(doc);
			translator.translateXMLAttributes(doc);
			Chapter chapModel = (Chapter)translator.visit(doc.tree); // get model for single chapter
			chapModel.generatedFilename = doc.getGeneratedFilename(target);
			if ( mainFile!=null ) {
				mainFile.chapterDocuments.add(chapModel);
			}
			chapterModels.add(chapModel);
		}

		// SECOND: CONVERT MODEL OBJECTS TO TEMPLATE TREE

		// convert all chapter model trees to file template trees
		List<ST> fileSTs = new ArrayList<>();
		for (Chapter chapter : chapterModels) {
			ST chapST = converter.walk(chapter);
			ST outputFileST = artifact.templates.getInstanceOf("OutputFile");
			outputFileST.add("body", chapST);
			fileSTs.add(outputFileST);
		}

		// handle non-inlined models ref'd from paragraphs
		for (ChapDocInfo doc : artifact.docs) {   // convert any non-inlined models
			convertNonInlinedDefModels(converter, doc);
		}

		ST mainFileST = converter.walk(mainFile); // convert main root file if book

		// Some entities are in global scope, such as chapters, and so
		// held in rootdoc's entity list but they are not defined by
		// translator until after we translate all files. So, do this
		// after processing all files.
		convertNonInlinedDefModels(converter, artifact.rootdoc);

		// THIRD: RENDER TEMPLATE TREE TO STRING AND SAVE

		if ( mainFile!=null ) {
			String mainFileOutput = mainFileST.render();
			saveOutput(target, artifact.rootdoc, mainFileOutput);
		}

		for (int i = 0; i<fileSTs.size(); i++) {
			ST fileST = fileSTs.get(i);
			ChapDocInfo doc = artifact.docs.get(i);
			String output = fileST.render();
			saveOutput(target, doc, output);
		}
	}

	/** walk all Def OutputModelObjects that were not inserted inline into
	 *  the output doc by the translator. We define things like sidefigs and
	 *  such that are ref'd within paragraphs but defined before/after.
	 *  We want these to appear to right of paragraphs so they are not
	 *  added to model tree by translator.
	 */
	public void convertNonInlinedDefModels(ModelConverter converter, DocInfo doc) {
		for (String label : doc.entities.keySet()) {
			EntityDef def = doc.entities.get(label);
			if ( def.model==null ){
				throw new IllegalStateException("Def "+def+" has no model!");
			}
			if ( def.template==null ) {
				def.template = converter.walk(def.model);
			}
		}
	}

	public void saveOutput(String target, DocInfo docInfo, String output) {
		String outFilename = docInfo.getGeneratedFilename(target);
		ParrtIO.save(outputDir+"/"+outFilename, output);
		System.out.println("Wrote "+outputDir+"/"+outFilename);
	}

	/** define all entities in all files */
	public void defineAllEntities(Artifact artifact) {
		for (ChapDocInfo doc : artifact.docs) {
			DefEntitiesListener defPhase = new DefEntitiesListener(doc);
			ParseTreeWalker walker = new ParseTreeWalker();
			walker.walk(defPhase, doc.tree);

			// Copy all global entities to root artifact
			for (String label : defPhase.entities.keySet()) {
				EntityDef d = defPhase.entities.get(label);
				if ( d.isGloballyVisible() ) { // move to global space
					artifact.defGlobalEntity(label, d);
				}
				else {
					doc.defEntity(label, d); // define locally to this doc
				}
			}
		}
	}

	/** check all references to entities in all files
	 * track list of entity refs for each paragraph
	 */
	public void verifyAllEntityRefs(Artifact artifact) {
		for (ChapDocInfo doc : artifact.docs) {
			Collection<ParseTree> refNodes =
				XPath.findAll(doc.tree, "//REF", doc.parser);
			for (ParseTree t : refNodes) {
				String label = stripQuotes(t.getText());
				EntityDef d = doc.getEntity(label);
				if ( d==null ) {
					Token tok = ((TerminalNode) t).getSymbol();
					System.err.printf("%s line %d:%d unknown label '%s'\n",
					                  doc.getSourceName(),
					                  tok.getLine(),
					                  tok.getCharPositionInLine(),
					                  label);
					continue;
				}
				d.refCount++;
				List<? extends Tree> ancestors = Trees.getAncestors(t);
				Tree p = ParrtCollections.findLast(ancestors, (Tree a) -> a instanceof BookishParser.ParagraphContext);
				// track all side items ref'd within this paragraph
				if ( d.refCount==1 && p!=null && d.isSideItem() ) {
					// for first ref, annotate paragraph if this ref is inside a paragraph
					((BookishParser.ParagraphContext)p).entitiesRefd.add(d);
				}
			}
		}
	}

	/** Collect all latex snippets and annotate tree with file names of gen'd images */
	public void convertAllLatexToImages(Artifact artifact) {
		if ( isHTMLTarget() ) {
			for (ChapDocInfo doc : artifact.docs) {
				ConvertLatexToImageListener eqnPhase = new ConvertLatexToImageListener(this, doc);
				ParseTreeWalker walker = new ParseTreeWalker();
				walker.walk(eqnPhase, doc.tree);
			}
		}
	}

	/** Collect all Python snippets and annotate tree with entity defs
	 * Execute all .py files, annotate tree with stdout/stderr
	 * Write out notebooks derived from snippets.
	 */
	public void execAllPythonSnippets(Artifact artifact) {
		for (ChapDocInfo doc : artifact.docs) {
			DefPythonEntitiesListener pyPhase = new DefPythonEntitiesListener(doc);
			ParseTreeWalker walker = new ParseTreeWalker();
			walker.walk(pyPhase, doc.tree);

			PySnippetExecutor executor = new PySnippetExecutor(this);
			executor.prepExecutionEnv(doc);
			executor.execSnippets(doc);
			executor.saveNotebooks(doc);
			executor.annotateTreesWithOutput(doc);
		}
	}

	/** Parse root doc and any subordinate files. Result is an
	 *  appropriate artifact (book or article). No processing is
	 *  done on trees.
	 */
	public Artifact parseAllFiles(String rootfile) throws IOException {
		Artifact artifact = createArtifact(rootfile);

		List<String> includes = collectIncludeFilenames(artifact.rootdoc);
		int n = 1;
		for (String subordinateFilename : includes) {
			try {
				ChapDocInfo doc = parseChapter(artifact, inputDir+"/"+subordinateFilename);
				doc.docNumber = n++;
				artifact.addDoc(doc);
			}
			catch (NoSuchFileException nsfe) {
				System.err.println("No such subordinate file: "+inputDir+"/"+subordinateFilename+" ref'd from "+rootfile);
			}
		}
		return artifact;
	}

	public Artifact createArtifact(String rootFile) throws IOException {
		RootDocInfo rootdoc = parseRoot(rootfile);

		// book or article?
		Artifact artifact = null;
		String title = getAttr(rootdoc.getTreeAsRoot().book(), "title");

		if ( rootdoc.getTreeAsRoot().book()!=null ) {
			artifact = new Book(this, title);
		}
		else if ( rootdoc.getTreeAsRoot().article()!=null ) {
			artifact = new Article(this, title);
		}
		else {
			System.err.println("Bad root doc");
			return null;
		}

		artifact.addRootDoc(rootdoc);

		// Get specific tags like data, copyright, ...
		Collection<ParseTree> dataNodes =
			XPath.findAll(artifact.rootdoc.getTreeAsRoot(), "//data", artifact.rootdoc.parser);
		if ( !dataNodes.isEmpty() ) {
			BookishParser.DataContext dataNode = (BookishParser.DataContext) dataNodes.iterator().next();
			artifact.dataDir = dataNode.attrs().attributes.get("dir");
		}

		Collection<ParseTree> supportNodes =
			XPath.findAll(artifact.rootdoc.getTreeAsRoot(), "//NOTEBOOK_SUPPORT", artifact.rootdoc.parser);
		if ( !supportNodes.isEmpty() ) {
			TerminalNode tok = (TerminalNode) supportNodes.iterator().next();
			BookishParser.Notebook_supportContext node = (BookishParser.Notebook_supportContext)tok.getParent();
			artifact.notebookResources.add(node.attrs().attributes.get("file") );
		}

		Collection<ParseTree> copyrightNodes =
			XPath.findAll(artifact.rootdoc.getTreeAsRoot(), "//copyright", artifact.rootdoc.parser);
		if ( !copyrightNodes.isEmpty() ) {
			BookishParser.CopyrightContext copyNode =
				(BookishParser.CopyrightContext) copyrightNodes.iterator().next();
			String location = artifact.rootdoc.getSourceName()+" "+
				copyNode.start.getLine()+":"+
				copyNode.start.getCharPositionInLine();
			artifact.copyright = translateString(artifact.rootdoc, copyNode.content().getText(), location);
		}

		return artifact;
	}

	public List<String> collectIncludeFilenames(RootDocInfo doc) {
		List<String> includes = new ArrayList<>();
		Collection<ParseTree> includeNodes = XPath.findAll(doc.tree, "//include", doc.parser);
		for (ParseTree includeNode : includeNodes) {
			includes.add(getAttr(includeNode, "file"));
		}
		return includes;
	}

	public RootDocInfo parseRoot(String fileName) throws IOException {
		return (RootDocInfo)parse(null, fileName, true);
	}

	public ChapDocInfo parseChapter(Artifact artifact, String fileName) throws IOException {
		return (ChapDocInfo)parse(artifact, fileName, false);
	}

	/** Parse a file and also traverse parse tree to set XML attributes Map
	 *  for any attrs rule refs.
	 */
	public Object parse(Artifact artifact, String fileName, boolean isRoot) throws IOException {
		CharStream input = CharStreams.fromFileName(fileName);
		BookishLexer lexer = new BookishLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		BookishParser parser = new BookishParser(tokens);

		lexer.removeErrorListeners();
		lexer.addErrorListener(getErrorListener());
		parser.removeErrorListeners();
		parser.addErrorListener(getErrorListener());

		ParserRuleContext doctree;
		if ( isRoot ) doctree = parser.rootdocument();
		else doctree = parser.chapter();

		verifyAndGatherXMLAttributes(parser, doctree);

//		System.out.println();
//		System.out.println(doctree.toStringTree(Arrays.asList(BookishParser.ruleNames)));

		if ( isRoot ) {
			return new RootDocInfo(artifact, parser, (BookishParser.RootdocumentContext)doctree);
		}
		return new ChapDocInfo(artifact, parser, (BookishParser.ChapterContext)doctree);
	}

	/** For each attrs node in tree, set attributes field to dictionary with
	 *  all x=y attribute pairs.
	 *
	 *  Verify that attribute names are in the list of valid attributes for
	 *  that invocation of attrs rule.
	 */
	public static void verifyAndGatherXMLAttributes(Parser parser, ParserRuleContext tree) {
		Collection<ParseTree> attrsNodes = XPath.findAll(tree, "//attrs", parser);
		for (ParseTree attrsNode : attrsNodes) {
			BookishParser.AttrsContext ctx = (BookishParser.AttrsContext) attrsNode;
			Map<String,String> attributes = new HashMap<>();
			for (int i = 0; i<attrsNode.getChildCount(); i++) {
				ParseTree assignment = attrsNode.getChild(i);
				TerminalNode key = (TerminalNode)assignment.getChild(0);
				ParseTree value = assignment.getChild(2);
				// verify attr name is valid
				if ( !ctx.valid.contains(key.getText()) ) {
					System.err.printf("%s line %d:%d attribute name '%s' is not in valid list: %s\n",
					                  ParrtIO.basename(parser.getInputStream().getSourceName()),
					                  key.getSymbol().getLine(),
					                  key.getSymbol().getCharPositionInLine(),
					                  key.getText(),
					                  ctx.valid);
					continue; // don't set key-value pair
				}
				// set key-value pair into attributes list
				if ( key!=null && value!=null ) {
					String v = value.getText();
					if ( v.charAt(0)=='"' || v.charAt(0)=='{' ) {
						v = stripQuotes(v);
					}
					attributes.put(key.getText(), v);
				}
			}
			ctx.attributes = attributes;
		}
	}

	public static BookishParser.ContentContext parseString(String inputStr, String location) {
		CharStream input = CharStreams.fromString(inputStr);
		BookishLexer lexer = new BookishLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		BookishParser parser = new BookishParser(tokens);

		lexer.removeErrorListeners();
		lexer.addErrorListener(getErrorListener(location));
		parser.removeErrorListeners();
		parser.addErrorListener(getErrorListener(location));

		BookishParser.ContentContext tree = parser.content();
		return tree;
	}

	public OutputModelObject translateStringToModel(DocInfo docInfo, String bookishString, String location) {
		Translator subtranslator = new Translator(docInfo);
		return subtranslator.visit(Tool.parseString(bookishString, location));
	}

	public String translateString(DocInfo docInfo, String bookishString, String location) {
		OutputModelObject m = translateStringToModel(docInfo, bookishString, location);
		ModelConverter converter = new ModelConverter(docInfo.artifact.templates);
		ST st = converter.walk(m);
		return st.render();
	}

	public String getBuildDir() {
		return BUILD_DIR+"-"+stripFileExtension(ParrtIO.basename(rootfile));
	}

	public boolean isHTMLTarget() { return target.equals("html"); }

	public boolean isLatexTarget() { return target.equals("latex"); }

	public String option(String name) { return (String)options.get(name); }
	public Object optionO(String name) { return options.get(name); }

	protected Map<String,Object> handleArgs(String[] args) {
		Map<String,Object> options = new HashMap<>();
		// Set the option defaults
		options.put("o", ".");

		int i=0;
		while ( args!=null && i<args.length ) {
			String arg = args[i];
			i++;
			if ( arg.charAt(0)!='-' ) { // must be file name
				options.put("rootfile", arg);
				continue;
			}
			if ( !validOptions.contains(arg) ) {
				System.err.printf("Unknown option '%s'\n", arg);
				continue;
			}
			Object value = args[i];
			if ( arg.equals("-target") ) {
				if ( !(value.equals("html") || value.equals("latex")) ) {
					System.err.println("-target must be html or latex, not "+value);
					i++;
					continue;
				}
			}
			arg = arg.substring(1); // strip '-'
			options.put(arg, value);
			i++;
		}
		return options;
	}

	/** Given any XML tag like <chapter> or <pyeval>, return value associated
	 *  with the attribute for key.
	 */
	public static String getAttr(ParseTree t, String key) {
		ParserRuleContext ctx = (ParserRuleContext)t;
		if ( ctx==null ) {
			return null; // no attributes
		}
		BookishParser.AttrsContext attrsCtx = null;
		if ( ctx instanceof BookishParser.AttrsContext ) {
			attrsCtx = (BookishParser.AttrsContext)ctx;
		}
		else {
			attrsCtx = ctx.getRuleContext(BookishParser.AttrsContext.class, 0);
		}
		if ( attrsCtx!=null ) {
			if ( attrsCtx.valid.contains(key) ) {
				return attrsCtx.attributes.get(key);
			}
			else {
				throw new IllegalArgumentException("No such valid attribute name: '"+key+"'");
			}
		}
		return null;
	}

	/** Copy images/ subdirs to outputDir/images */
	public void copyImages(Artifact artifact, String inputDir, String outputDir) {
		if ( artifact instanceof Article ) {
			execCommandLine(String.format("cp -r %s/images %s", inputDir, outputDir));
			return;
		}
		for (ChapDocInfo doc : artifact.docs) {
			String label = doc.getSourceBaseName();
			if ( new File(inputDir+"/images/"+label).exists() ) {
				execCommandLine(String.format("cp -r %s/images/%s %s/images", inputDir, label, outputDir));
			}
		}
	}
}
