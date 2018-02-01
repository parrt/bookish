package us.parr.bookish;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.stringtemplate.v4.ST;
import us.parr.bookish.model.Chapter;
import us.parr.bookish.model.Document;
import us.parr.bookish.parse.BookishLexer;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.translate.ModelConverter;
import us.parr.bookish.translate.Translator;
import us.parr.lib.ParrtIO;
import us.parr.lib.ParrtSys;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
		String inputFilename = option("inputFilename");
		String inputDir = new File(inputFilename).getParent();
		BookishLexer lexer = new BookishLexer(CharStreams.fromFileName(inputFilename));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		BookishParser parser = new BookishParser(tokens);
		BookishParser.DocumentContext doctree = parser.document();

		String outputDir = option("o");
		ParrtIO.mkdir(outputDir+"/images");
		String outFilename;
		Translator trans;
		Target target = (Target) optionO("target");
		if ( target==Target.HTML ) {
			trans = new Translator(target, outputDir);
			outFilename = "index.html";
		}
		else {
			trans = new Translator(target, outputDir);
			outFilename = "book.tex";
		}
		Chapter chapter = (Chapter)trans.visit(doctree); // get single chapter
		chapter.connectContainerTree();
		Document doc = new Document();
		doc.addChapter(chapter);

		ModelConverter converter = new ModelConverter(trans.templates);
		ST outputST = converter.walk(doc);
		String output = outputST.render();
		ParrtIO.save(outputDir+"/"+outFilename, output);
		System.out.println("Wrote "+outputDir+"/"+outFilename);
		copyImages(inputDir, outputDir);
	}

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
		System.out.printf("Copyied %s to %s\n", src, trg);
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
				options.put("inputFilename", arg);
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
