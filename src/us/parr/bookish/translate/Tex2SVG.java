package us.parr.bookish.translate;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import us.parr.bookish.Tool;
import us.parr.bookish.util.StreamVacuum;
import us.parr.lib.ParrtIO;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Tex2SVG {
	public static STGroupFile templates = new STGroupFile("templates/eqntex.stg");

	enum LatexType {EQN, BLOCKEQN, LATEX}

	public static Triple<String,Float,Float> tex2svg(String latex, LatexType type, int fontsize) {
		try {
			latex = latex.trim();
			String tmpdir = new File(System.getProperty("java.io.tmpdir")+"/bookish").getAbsolutePath();

			if ( !Files.exists(Paths.get(tmpdir)) ) {
				Files.createDirectories(Paths.get(tmpdir));
			}
			if ( !Files.exists(Paths.get(tmpdir+"/images")) ) {
				Files.createSymbolicLink(Paths.get(tmpdir+"/images"), Paths.get(Tool.rootDir+"/images"));
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

//    println("wrote $texfilename")

			Runtime runtime = Runtime.getRuntime();

			Pair<String, String> results = runProcess(tmpdir, "xelatex", "-shell-escape", "-interaction=nonstopmode", "temp.tex");
//    println(results.a)

			String metricsRegex = "// bookish metrics: ([0-9]*[.][0-9]+)pt, ([0-9]*[.][0-9]+)pt";
			Pattern metricsPattern =  Pattern.compile("metricsRegex");
			String log = ParrtIO.load(tmpdir+"/temp.log");

			float height=0, depth=0;

			for (String line : results.a.split("\n")) {
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
			if ( results.b.length()>0 ) {
				System.err.println(results.b);
			}

			results = runProcess(tmpdir, "pdfcrop", "temp.pdf");
			if ( results.b.length()>0 ) {
				System.err.println(results.b);
			}

			results = runProcess(tmpdir, "pdf2svg", "temp-crop.pdf", "temp.svg");
			if ( results.b.length()>0 ) {
				System.err.println(results.b);
			}

			String svgfilename = tmpdir+"/temp.svg";
			return new Triple<>(new String(Files.readAllBytes(Paths.get(svgfilename))),height,depth);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Pair<String, String> runProcess(String execPath, String... args) throws Exception {
		Process process = Runtime.getRuntime().exec(args, null, new File(execPath));
		StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
		StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
		stdoutVacuum.start();
		stderrVacuum.start();
		process.waitFor();
		stdoutVacuum.join();
		stderrVacuum.join();
		return new Pair<>(stdoutVacuum.toString(), stderrVacuum.toString());
	}

	public static void main(String[] args) throws Exception {
		Triple<String,Float,Float> results = tex2svg("\\frac{\\partial}{\\partial x}f(x^2) = 3+4", LatexType.LATEX, 16);
		Files.write(Paths.get("/tmp/t.svg"), results.a.getBytes());
		System.out.println(results.a+" "+results.b);
	}
}