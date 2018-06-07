package us.parr.bookish.semantics;

import org.antlr.v4.runtime.misc.Triple;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import us.parr.lib.ParrtIO;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static us.parr.lib.ParrtSys.execInDir;

public class Tex2SVG {
	public static STGroupFile templates = new STGroupFile("templates/eqntex.stg");

	public enum LatexType {EQN, BLOCKEQN, LATEX}

	public String outputDir;

	public Tex2SVG(String outputDir) {
		this.outputDir = outputDir;
	}

	public Triple<String,Float,Float> tex2svg(String latex, LatexType type, int fontsize) {
		try {
			latex = latex.trim();
			String tmpdir = new File(outputDir).getAbsolutePath();

			if ( !Files.exists(Paths.get(tmpdir)) ) {
				Files.createDirectories(Paths.get(tmpdir));
			}
			if ( !Files.exists(Paths.get(tmpdir+"/images")) ) {
				Files.createSymbolicLink(Paths.get(tmpdir+"/images"), Paths.get(outputDir+"/images"));
			}

			String texfilename = tmpdir+"/temp.tex";
			ST template = null;
			switch ( type ) {
				case EQN:
					template = templates.getInstanceOf("eqntex");
					break;
				case BLOCKEQN:
					template = templates.getInstanceOf("blockeqntex");
					break;
				case LATEX:
					template = templates.getInstanceOf("blocktex");
					break;
			}
			template.add("text", latex);
			template.add("fontsize", fontsize);
			Files.write(Paths.get(texfilename), template.render().getBytes());

//			System.out.println("wrote "+texfilename);

			String[] results = execInDir(tmpdir, "xelatex", "-shell-escape", "-interaction=nonstopmode", "temp.tex");
//    println(results.a)

			String metricsRegex = "// bookish metrics: ([0-9]*[.][0-9]+)pt, ([0-9]*[.][0-9]+)pt";
			Pattern metricsPattern =  Pattern.compile("metricsRegex");
			String log = ParrtIO.load(tmpdir+"/temp.log");

			float height=0, depth=0;

			for (String line : results[0].split("\n")) {
				String prefix = "// bookish metrics: ";
				if ( line.startsWith(prefix) ) {
					int first = prefix.length();
					int comma = line.indexOf(',');
					String heightS = line.substring(first,comma-"pt".length());
					String depthS = line.substring(comma+1,line.indexOf('p',comma));
					height = Float.parseFloat(heightS);
					depth = Float.parseFloat(depthS);
				}
				if ( line.startsWith("!") || line.startsWith("l.") ) {
					System.err.println(line);
					System.err.println(latex);
				}
			}
			if ( results[1].length()>0 ) {
				System.err.println(results[1]);
			}

			results = execInDir(tmpdir, "pdfcrop", "temp.pdf");
			if ( results[1].length()>0 ) {
				System.err.println(results[1]);
			}

			results = execInDir(tmpdir, "pdf2svg", "temp-crop.pdf", "temp.svg");
			if ( results[1].length()>0 ) {
				System.err.println(results[1]);
			}

			String svgfilename = tmpdir+"/temp.svg";
			return new Triple<>(new String(Files.readAllBytes(Paths.get(svgfilename))),height,depth);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}