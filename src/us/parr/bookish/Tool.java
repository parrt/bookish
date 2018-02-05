package us.parr.bookish;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.stringtemplate.v4.ST;
import us.parr.bookish.model.Book;
import us.parr.bookish.model.Document;
import us.parr.bookish.model.OutputModelObject;
import us.parr.bookish.model.entity.EntityDef;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static us.parr.lib.ParrtIO.basename;
import static us.parr.lib.ParrtIO.stripFileExtension;
import static us.parr.lib.ParrtStrings.stripQuotes;

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
	public enum Target { HTML, LATEX }

	public Map<String,Object> options = new HashMap<>();

	public static final Set<String> validOptions =
		new HashSet<String>() {{
			add("-o");          // output dir
			add("-target");     // html or latex
		}};

	public static void main(String[] args) throws Exception {
		Tool tool = new Tool();
		tool.process(args);
	}

	public void process(String[] args) throws Exception {
		options = handleArgs(args);
		String metadataFilename = option("metadataFilename");
		String inputDir = new File(metadataFilename).getParent();
		String outputDir = option("o");

		String outFilename;
		Translator trans;
		Target target = (Target)optionO("target");

		ParrtIO.mkdir(outputDir+"/images");

		if ( metadataFilename.endsWith(".md") ) { // just one file (legacy stuff)
			Book book = new Book("","");
			book.entities = new HashMap<>();
			trans = new Translator(book, book.entities, target, outputDir);
			if ( target==Target.HTML ) {
				outFilename = "index.html";
			}
			else {
				outFilename = stripFileExtension(basename(metadataFilename))+".tex";
			}
			Pair<Document, String> results = legacy_translate(trans, metadataFilename);
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
		Book book = new Book(title, null);
		String author = metadata.getString("author");
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
		List<String> filenames = new ArrayList<>();
		List<Map<String, EntityDef>> entities = new ArrayList<>();
		JsonArray markdownFilenames = metadata.getJsonArray("chapters");
		for (JsonValue f : markdownFilenames) {
			String fname = stripQuotes(f.toString());
			filenames.add(fname);
			Pair<BookishParser.DocumentContext, Map<String, EntityDef>> results =
				parseChapter(inputDir+"/"+fname);
			trees.add(results.a);
			entities.add(results.b);
		}

		// now walk all trees and translate
		List<Document> documents = new ArrayList<>();
		for (int i = 0; i<filenames.size(); i++) {
			String fname = filenames.get(i);
			BookishParser.DocumentContext tree = trees.get(i);
			Map<String, EntityDef> thisDocsEntities = entities.get(i);
			trans = new Translator(book, thisDocsEntities, target, outputDir);
			Document doc = (Document) trans.visit(tree); // get doc for single chapter
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
			book.addChapterDocument(doc);
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
	}

	public String translateString(Translator trans, String markdown, String startRule) throws Exception {
		CharStream input = CharStreams.fromString(markdown);
		BookishLexer lexer = new BookishLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		BookishParser parser = new BookishParser(tokens);
		Method startMethod = BookishParser.class.getMethod(startRule, (Class[])null);
		ParseTree doctree = (ParseTree)startMethod.invoke(parser, (Object[])null);

		OutputModelObject omo = trans.visit(doctree); // get single chapter

		ModelConverter converter = new ModelConverter(trans.templates);
		ST outputST = converter.walk(omo);
		return outputST.render();
	}

	public Pair<BookishParser.DocumentContext,Map<String,EntityDef>> parseChapter(String inputFilename) throws IOException {
		CharStream input = CharStreams.fromFileName(inputFilename);
		BookishLexer lexer = new BookishLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		BookishParser parser = new BookishParser(tokens);
		BookishParser.DocumentContext doctree = parser.document();
		return new Pair<>(doctree,parser.entities);
	}

	// legacy single-doc translation
	public Pair<Document,String> legacy_translate(Translator trans, String inputFilename) throws IOException {
		Pair<BookishParser.DocumentContext,Map<String,EntityDef>> results = parseChapter(inputFilename);
		Document doc = (Document)trans.visit(results.a); // get single chapter
		doc.chapter.connectContainerTree();
		doc.entities = results.b;

		ModelConverter converter = new ModelConverter(trans.templates);
		ST outputST = converter.walk(doc);
		return new Pair<>(doc,outputST.render());
	}

	// SUPPORT

	/** Copy images/ subdir to outputDir/images */
	public void copyImages(String inputDir, String outputDir) {
		String src = inputDir+"/images";
		String trg = outputDir+"/images";
		for (File f : new File(src).listFiles()) {
			String cmd = String.format("cp %s/%s %s", src, f.getName(), trg);
			String[] exec = ParrtSys.exec(cmd);
			if ( exec[2]!=null && exec[2].length()>0 ) {
				System.err.println(exec[2]);
			}
		}
		System.out.printf("Copied %s to %s\n", src, trg);
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
				}
			}
			arg = arg.substring(1); // strip '-'
			options.put(arg,value);
			i++;
		}
		return options;
	}
}
