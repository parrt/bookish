package us.parr.bookish;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import us.parr.bookish.model.Book;
import us.parr.bookish.model.Document;
import us.parr.bookish.model.OutputModelObject;
import us.parr.bookish.model.entity.EntityDef;
import us.parr.bookish.model.entity.ExecutableCodeDef;
import us.parr.bookish.model.entity.PyEvalDef;
import us.parr.bookish.model.entity.PyFigDef;
import us.parr.bookish.parse.BookishLexer;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.translate.ModelConverter;
import us.parr.bookish.translate.Translator;
import us.parr.lib.ParrtIO;
import us.parr.lib.ParrtSys;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static us.parr.lib.ParrtIO.basename;
import static us.parr.lib.ParrtIO.stripFileExtension;
import static us.parr.lib.ParrtStrings.md5hash;
import static us.parr.lib.ParrtStrings.stripQuotes;
import static us.parr.lib.ParrtSys.execCommandLine;

/**
 * java us.parr.bookish.Tool -target latex -o /tmp/mybook book.json
 * java us.parr.bookish.Tool -target html -o /tmp/mybook book.json
 *
 * Assumes images/ subdir (and these are copied to target dir).
 *
 * metadata in json. e.g.,
 *
 *      https://github.com/parrt/bookish/blob/master/examples/matrix-calculus/matrix-calculus.json
 */
public class Tool {
	public static final String BUILD_DIR = "/tmp/build";

	public enum Target { HTML, LATEX, LATEX_BOOK }

	public Map<String,Object> options = new HashMap<>();

	public static final Set<String> validOptions =
		new HashSet<String>() {{
			add("-o");          // output dir
			add("-target");     // html or latex
		}};

	public String inputDir;
	public String dataDir;
	public String outputDir;

	public static void main(String[] args) throws Exception {
		Tool tool = new Tool();
		tool.process(args);
	}

	public void process(String[] args) throws Exception {
		options = handleArgs(args);
		String metadataFilename = option("metadataFilename");
		inputDir = new File(metadataFilename).getParent();
		outputDir = option("o");

		String outFilename;
		Translator trans;
		Target target = (Target)optionO("target");

		ParrtIO.mkdir(outputDir+"/images");
		String snippetsDir = BUILD_DIR+"/snippets";
		ParrtIO.mkdir(snippetsDir);

		if ( metadataFilename.endsWith(".md") ) { // just one file (legacy stuff)
			String inputFilename = metadataFilename;
			Book book = new Book(this, "","");
			book.entities = new HashMap<>();
			trans = new Translator(book, book.entities, target, outputDir);
			if ( target==Target.HTML ) {
				outFilename = "index.html";
			}
			else {
				outFilename = stripFileExtension(basename(inputFilename))+".tex";
			}
			Pair<Document, String> results = legacy_translate(trans, inputDir, basename(inputFilename));
			String output = results.b;
			ParrtIO.save(outputDir+"/"+outFilename, output);
			System.out.println("Wrote "+outputDir+"/"+outFilename);
			copyImages(inputDir, outputDir);
			return;
		}

		// otherwise, read and use metadata
		JsonReader jsonReader = Json.createReader(new FileReader(metadataFilename));
		JsonObject metadata = jsonReader.readObject();
//		System.out.println(metadata);

		String title = metadata.getString("title");
		Book book = new Book(this, title, null);
		String author = metadata.getString("author");
		dataDir = metadata.getString("data");
		author = "\n\n"+author; // Rule paragraph needs blank line on the front
		trans = new Translator(book, null, target, outputDir);
		book.author = translateString(trans, author, "paragraph");

		String mainOutFilename;
		if ( target==Target.HTML ) {
			mainOutFilename = "index.html";
		}
		else {
			mainOutFilename = "book.tex";
		}

		// parse all documents first to get entity defs
		List<BookishParser.DocumentContext> trees = new ArrayList<>();
		List<Map<String, EntityDef>> entities = new ArrayList<>();
		List<List<ExecutableCodeDef>> codeBlocks = new ArrayList<>();
		JsonArray markdownFilenames = metadata.getJsonArray("chapters");
		for (JsonValue f : markdownFilenames) {
			String fname = stripQuotes(f.toString());
			book.filenames.add(fname);
			Pair<BookishParser.DocumentContext, BookishParser> results =
				parseChapter(inputDir, fname, book.chapCounter);
			book.chapCounter++;
			trees.add(results.a);
			entities.add(results.b.entities);
			codeBlocks.add(results.b.codeBlocks);
		}

		executeCodeSnippets(book, BUILD_DIR, codeBlocks);

		// now walk all trees and translate
		List<Document> documents = new ArrayList<>();
		for (int i = 0; i<book.filenames.size(); i++) {
			String fname = book.filenames.get(i);
			BookishParser.DocumentContext tree = trees.get(i);
			Map<String, EntityDef> thisDocsEntities = entities.get(i);
			trans = new Translator(book, thisDocsEntities, target, outputDir);
			Document doc = (Document)trans.visit(tree); // get doc for single chapter
			book.addChapterDocument(doc);
			doc.chapter.connectContainerTree();

			ModelConverter converter = new ModelConverter(trans.templates);
			ST outputST = converter.walk(doc);

			// walk all OutputModelObjects created as labeled entities to convert those entities
			// unlabeled entities are done in-line
			ArrayList<String> labels = new ArrayList<>(thisDocsEntities.keySet());
			for (String label : labels) {
				EntityDef def = thisDocsEntities.get(label);
				def.template = converter.walk(def.model);
				if ( def.isGloballyVisible() ) { // move to global space
					book.entities.put(label, def);
					thisDocsEntities.remove(label);
				}
			}

			String output = outputST.render();
			doc.markdownFilename = fname;
			documents.add(doc);
			if ( target==Target.HTML ) {
				outFilename = stripFileExtension(fname)+".html";
			}
			else {
				outFilename = stripFileExtension(fname)+".tex";
			}
			ParrtIO.save(outputDir+"/"+outFilename, output);
			doc.generatedFilename = outFilename;
			System.out.println("Wrote "+outputDir+"/"+outFilename);
		}

		ST bookTemplate = trans.templates.getInstanceOf("Book");
		bookTemplate.add("model", book);
		ParrtIO.save(outputDir+"/"+mainOutFilename, bookTemplate.render());
		System.out.println("Wrote "+outputDir+"/"+mainOutFilename);
		copyImages(inputDir, outputDir);
		execCommandLine(String.format("cp -r %s/css %s", inputDir, outputDir));
//		copyImages(BUILD_DIR, outputDir);
	}

	/** generate python files to execute \pyfig, \pyeval blocks */
	public void executeCodeSnippets(Book book, String buildDir,
	                                List<List<ExecutableCodeDef>> codeBlocks)
	{
		String snippetsDir = buildDir+"/snippets";
		// combine list of code snippets for each label into file
		STGroup pycodeTemplates = new STGroupFile("templates/pyeval.stg");

		for (int i = 0; i<book.filenames.size(); i++) { // for each chapter
			List<ExecutableCodeDef> codeDefs = codeBlocks.get(i);
			if ( codeDefs.size()==0 ) {
				continue;
			}
			String basename = stripFileExtension(codeDefs.get(0).inputFilename);

			// prepare directories
			String chapterSnippetsDir = snippetsDir+"/"+basename;
			ParrtIO.mkdir(chapterSnippetsDir);
			ParrtIO.mkdir(outputDir+"/images/"+basename);
			// every chapter snippets dir gets a data link to book data directory
			if ( !new File(chapterSnippetsDir+"/data").exists() ) {
				execCommandLine("ln -s "+dataDir+" "+chapterSnippetsDir+"/data");
			}

			// get mapping from label (or index if no label) to list of snippets
			MultiMap<String, ExecutableCodeDef> labelToDefs = new MultiMap<>();
			List<String> labels = new ArrayList<>();
			for (ExecutableCodeDef codeDef : codeDefs) { // for each code blob
				String label = codeDef.label!=null ? codeDef.label : String.valueOf(codeDef.index);
				if ( !labels.contains(label) ) labels.add(label);
				labelToDefs.map(label, codeDef);
			}

			// track snippet label order
			for (String label : labels) { // for each group of code with same label
				List<ExecutableCodeDef> defs = labelToDefs.get(label);
				String snippetFilename = basename+"_"+label+".py";
				List<ST> snippets = new ArrayList<>();
				for (ExecutableCodeDef def : defs) {
					String tname = def.isOutputVisible ? "pyeval" : "pyfig";
					ST snippet = pycodeTemplates.getInstanceOf(tname);
					snippet.add("def",def);
					snippets.add(snippet);
				}
				ST file = pycodeTemplates.getInstanceOf("pyfile");
				file.add("snippets", snippets);
				file.add("buildDir", buildDir);
				file.add("outputDir", outputDir);
//				file.add("imagesDir", inputDir+"/images");
				file.add("basename", basename);
				file.add("label", label);
				String pycode = file.render();
				String snippetHashFilename = chapterSnippetsDir+"/"+basename+"_"+label+"-"+md5hash(pycode)+".hash";
				if ( !Files.exists(Paths.get(snippetHashFilename)) ) {
					ParrtIO.save(snippetHashFilename, ""); // save empty hash marker file
					ParrtIO.save(chapterSnippetsDir+"/"+snippetFilename, pycode);
					// EXEC!
					ParrtSys.execInDir(chapterSnippetsDir, "python3", snippetFilename);
				}

				for (ExecutableCodeDef def : defs) {
					String stderr = ParrtIO.load(chapterSnippetsDir+"/"+basename+"_"+label+"_"+def.index+".err");
					if ( def instanceof PyFigDef ) {
						((PyFigDef) def).generatedFilename = outputDir+"/images/"+basename+"/"+basename+"_"+label+"_"+def.index+".svg";
					}
					if ( stderr.trim().length()>0 ) {
						System.err.println(stderr);
					}
					if ( def.isOutputVisible ) {
						BookishParser.PyevalContext tree = ((PyEvalDef) def).tree;
						tree.stdout = ParrtIO.load(chapterSnippetsDir+"/"+basename+"_"+label+"_"+def.index+".out").trim();
						tree.stderr = stderr.trim();
						if ( tree.stdout.length()==0 ) tree.stdout = null;
						if ( tree.stderr.length()==0 ) tree.stderr = null;
//						System.out.println("stdout: "+stdout);
//						System.out.println("stderr: "+stderr);
					}
					if ( def.displayExpr!=null ) {
						BookishParser.PyevalContext tree = ((PyEvalDef) def).tree;
						String dataFilename = basename+"_"+label+"_"+def.index+".csv";
						tree.displayData = ParrtIO.load(chapterSnippetsDir+"/"+dataFilename);
						System.out.println("data: "+tree.displayData);
					}
				}
			}
		}
	}

	public String translateString(Translator trans, String markdown, String startRule) throws Exception {
		CharStream input = CharStreams.fromString(markdown);
		BookishLexer lexer = new BookishLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		BookishParser parser = new BookishParser(tokens,null, 0);
		Method startMethod = BookishParser.class.getMethod(startRule, (Class[])null);
		ParseTree doctree = (ParseTree)startMethod.invoke(parser, (Object[])null);

		OutputModelObject omo = trans.visit(doctree); // get single chapter

		ModelConverter converter = new ModelConverter(trans.templates);
		ST outputST = converter.walk(omo);
		return outputST.render();
	}

	public Pair<BookishParser.DocumentContext,BookishParser> parseChapter(String inputDir,
	                                                                      String inputFilename,
	                                                                      int chapNumber)
		throws IOException
	{
		CharStream input = CharStreams.fromFileName(inputDir+"/"+inputFilename);
		BookishLexer lexer = new BookishLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		BookishParser parser = new BookishParser(tokens, inputFilename, chapNumber);
		BookishParser.DocumentContext doctree = parser.document();
		return new Pair<>(doctree, parser);
	}

	// legacy single-doc translation
	public Pair<Document,String> legacy_translate(Translator trans,
	                                              String inputDir,
	                                              String inputFilename)
		throws IOException
	{
		Pair<BookishParser.DocumentContext,BookishParser> results =
			parseChapter(inputDir, inputFilename,0);
		trans.entities = results.b.entities;
		Document doc = (Document)trans.visit(results.a); // get single chapter
		doc.chapter.connectContainerTree();

		ModelConverter converter = new ModelConverter(trans.templates);
		ST outputST = converter.walk(doc);
		return new Pair<>(doc,outputST.render());
	}

	// SUPPORT

	/** Copy images/ subdir to outputDir/images */
	public void copyImages(String inputDir, String outputDir) {
		execCommandLine(String.format("cp -r %s %s", inputDir+"/images", outputDir));
	}

	public String option(String name) { return (String)options.get(name); }
	public Object optionO(String name) { return options.get(name); }

	protected Map<String,Object> handleArgs(String[] args) {
		Map<String,Object> options = new HashMap<>();
		// Set the option defaults
		options.put("target", Target.HTML);
		options.put("o", ".");

		int i=0;
		while ( args!=null && i<args.length ) {
			String arg = args[i];
			i++;
			if ( arg.charAt(0)!='-' ) { // must be file name
				options.put("metadataFilename", arg);
				continue;
			}
			if ( !validOptions.contains(arg) ) {
				System.err.printf("Unknown option '%s'\n", arg);
				continue;
			}
			Object value = args[i];
			if ( arg.equals("-target") ) {
				switch ( (String)value ) {
					case "html":
					case "HTML" :
						value = Target.HTML;
						break;
					case "latex" :
						value = Target.LATEX;
						break;
					case "latex-book" :
						value = Target.LATEX_BOOK;
						break;
				}
			}
			arg = arg.substring(1); // strip '-'
			options.put(arg,value);
			i++;
		}
		return options;
	}
}
