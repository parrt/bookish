package us.parr.bookish;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.stringtemplate.v4.ST;
import us.parr.bookish.model.Chapter;
import us.parr.bookish.model.Document;
import us.parr.bookish.parse.BookishLexer;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.translate.ModelConverter;
import us.parr.bookish.translate.Translator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Tool {
	public static String outputDir = "/tmp/bookish/calculus";
	public static String rootDir = "/Users/parrt/github/autodx";

	public static void main(String[] args) throws Exception {
		BookishLexer lexer = new BookishLexer(CharStreams.fromFileName("/Users/parrt/github/autodx/matrix-calculus.md"));
//		BookishLexer lexer = new BookishLexer(CharStreams.fromFileName("/tmp/t.md"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TokenStreamRewriter rewriter = new TokenStreamRewriter(tokens);
		BookishParser parser = new BookishParser(tokens);
		BookishParser.DocumentContext doctree = parser.document();

		new File(outputDir+"/images").mkdirs();
		Translator trans = new Translator();
		Chapter chapter = (Chapter)trans.visit(doctree); // get single chapter
		chapter.connectContainerTree();
		Document doc = new Document();
		doc.addChapter(chapter);
//		doc.createTOCModel();

//		chapter.createTOCModel();

		ModelConverter converter = new ModelConverter(trans.templates);
		ST outputST = converter.walk(doc);
		String output = outputST.render();
		Files.write(Paths.get(outputDir+"/index.html"), output.getBytes());
//		System.out.println(output);
	}
}
