package us.parr.bookish.semantics;

import org.antlr.v4.runtime.misc.Triple;
import us.parr.bookish.Tool;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.ChapDocInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static us.parr.bookish.semantics.Tex2SVG.LatexType.BLOCKEQN;
import static us.parr.bookish.translate.Translator.BLOCK_EQN_FONT_SIZE;
import static us.parr.bookish.translate.Translator.INLINE_EQN_FONT_SIZE;
import static us.parr.lib.ParrtStrings.md5hash;
import static us.parr.lib.ParrtStrings.stripQuotes;

/** Walk the parse tree and convert equations and other latex snippets
 *  to images. Annotate the tree with appropriate image file references
 *  after converting latex to image files.
 *
 *  Only used for HTML output as latex output does this stuff natively.
 */
public class ConvertLatexToImageListener extends BookishBaseListener {
	public Tool tool;
	public ChapDocInfo doc;

	public String codefileBasename;

	public Tex2SVG texConverter;

	public static Pattern eqnVarPattern;
	public static Pattern eqnVecVarPattern, eqnVecVarPattern2;
	public static Pattern eqnIndexedVarPattern;
	public static Pattern eqnIndexedVecVarPattern, eqnIndexedVecVarPattern2;

	static {
		eqnVarPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)");
		eqnIndexedVarPattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)_([a-zA-Z][a-zA-Z0-9]*)");
		eqnVecVarPattern = Pattern.compile("\\\\mathbf\\{([a-zA-Z][a-zA-Z0-9]*)\\}");
		eqnVecVarPattern2 = Pattern.compile("\\\\vec\\{([a-zA-Z][a-zA-Z0-9]*)\\}");
		eqnIndexedVecVarPattern = Pattern.compile("\\\\mathbf\\{([a-zA-Z][a-zA-Z0-9]*)\\}_([a-zA-Z][a-zA-Z0-9]*)");
		eqnIndexedVecVarPattern2 = Pattern.compile("\\\\vec\\{([a-zA-Z][a-zA-Z0-9]*)\\}_([a-zA-Z][a-zA-Z0-9]*)");
	}

	public ConvertLatexToImageListener(Tool tool, ChapDocInfo doc) {
		this.tool = tool;
		this.doc = doc;
		this.codefileBasename = doc.getSourceBaseName();

		texConverter = new Tex2SVG(tool.outputDir);
	}

	@Override
	public void enterEqn(BookishParser.EqnContext ctx) {
		String eqn = stripQuotes(ctx.getText());

		// check for special cases like $w$ and $\mathbf{w}_i$.
		// Do not convert these to images. Code generator will
		// convert to HTML refs directly.
		List<String> elements = extract(eqnVarPattern, eqn);
		if ( elements.size()>0 ) return;
		elements = extract(eqnVecVarPattern, eqn);
		if ( elements.size()>0 ) return;
		elements = extract(eqnVecVarPattern2, eqn);
		if ( elements.size()>0 ) return;
		elements = extract(eqnIndexedVarPattern, eqn);
		if ( elements.size()>0 ) return;
		elements = extract(eqnIndexedVecVarPattern, eqn);
		if ( elements.size()>0 ) return;
		elements = extract(eqnIndexedVecVarPattern2, eqn);
		if ( elements.size()>0 ) return;

		// find files that start with this hash; files have depth as part of name
		// like eqn-...-depth000.14.svg
		String prefix = String.format("eqn-%s", md5hash(eqn));
		File[] files =
			new File(tool.outputDir+"/images")
				.listFiles((dir, name) -> name.startsWith(prefix));
		if ( files!=null && files.length>0 ) {
			// already done, just annotate tree
			String existing = files[0].getName();
			ctx.relativeImageFilename = "images/"+existing;
			int i = existing.indexOf("-depth");
			int j = existing.indexOf(".svg", i);
			String depthS = existing.substring(i+"-depth".length(), j);
			ctx.depth = Float.parseFloat(depthS);
			ctx.height = -1; // unknown
			return;
		}

		// Not there, create and annotate tree
		Triple<String,Float,Float> results =
			texConverter.tex2svg(eqn, Tex2SVG.LatexType.EQN, INLINE_EQN_FONT_SIZE);
		String svg = results.a;
		ctx.height = results.b;
		ctx.depth = results.c;
		try {
			String src = String.format("%s/images/%s-depth%06.2f.svg",tool.outputDir,prefix,ctx.depth);
			Path outpath = Paths.get(src);
			System.out.println("inline eqn output file: "+outpath);
			Files.write(outpath, svg.getBytes());
			ctx.relativeImageFilename = String.format("images/%s-depth%06.2f.svg",prefix,ctx.depth);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void enterBlock_eqn(BookishParser.Block_eqnContext ctx) {
		String eqn = stripQuotes(ctx.getText(), 3);

		String relativePath = "images/blkeqn-"+md5hash(eqn)+".svg";
		String src = tool.outputDir+"/"+relativePath;
		Path outpath = Paths.get(src);
		if ( !Files.exists(outpath) ) {
			Triple<String,Float,Float> results = texConverter.tex2svg(eqn, BLOCKEQN, BLOCK_EQN_FONT_SIZE);
			String svg = results.a;

			try {
				System.out.println("blk eqn output file: "+outpath);
				Files.write(outpath, svg.getBytes());
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		ctx.relativeImageFilename = relativePath;
	}

	@Override
	public void enterLatex(BookishParser.LatexContext ctx) {
		String text = ctx.LATEX_CONTENT().getText().trim();

		String relativePath = "images/latex-"+md5hash(text)+".svg";
		String src = tool.outputDir+"/"+relativePath;
		Path outpath = Paths.get(src);
		if ( !Files.exists(outpath) ) {
			Triple<String,Float,Float> results =
				texConverter.tex2svg(text, Tex2SVG.LatexType.LATEX, BLOCK_EQN_FONT_SIZE);
			String svg = results.a;
			try {
				System.out.println("latex output file: "+outpath);
				Files.write(outpath, svg.getBytes());
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		ctx.relativeImageFilename = relativePath;
	}

	public static List<String> extract(Pattern pattern, String text) {
		Matcher m = pattern.matcher(text);
		List<String> elements = new ArrayList<>();
		if ( m.matches() ) {
			for (int i = 1; i <= m.groupCount(); i++) {
				elements.add(m.group(i));
			}
		}
		return elements;
	}
}
