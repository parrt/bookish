package us.parr.bookish.semantics;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import us.parr.bookish.Tool;
import us.parr.bookish.entity.ExecutableCodeDef;
import us.parr.bookish.entity.PyFigDef;
import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.parse.ChapDocInfo;
import us.parr.bookish.translate.HTMLEscaper;
import us.parr.lib.ParrtCollections;
import us.parr.lib.ParrtIO;
import us.parr.lib.ParrtSys;
import us.parr.lib.collections.MultiMap;
import us.parr.lib.collections.MultiMapOfLists;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static us.parr.lib.ParrtStrings.md5hash;
import static us.parr.lib.ParrtSys.execCommandLine;

public class PySnippetExecutor {
	public STGroup pycodeTemplates = new STGroupFile("templates/pyeval.stg");

	public Tool tool;

	public PySnippetExecutor(Tool tool) {
		this.tool = tool;
		pycodeTemplates = new STGroupFile("templates/pyeval.stg");
		pycodeTemplates.registerRenderer(String.class, new HTMLEscaper());
	}

	public void prepExecutionEnv(ChapDocInfo doc) {
		String snippetsDir = tool.getBuildDir()+"/snippets";

		// prepare snippets, images, and notebooks directories
		String basename = doc.getSourceBaseName();
		String chapterSnippetsDir = snippetsDir+"/"+basename;
		ParrtIO.mkdir(chapterSnippetsDir);
		ParrtIO.mkdir(tool.outputDir+"/images/"+basename);
		String outputChapNotebooksDir = tool.outputDir+"/notebooks/"+basename;
		ParrtIO.mkdir(outputChapNotebooksDir);

		// every chapter snippets/notebooks dir gets a data link to book data directory
		if ( !Files.isSymbolicLink(Paths.get(chapterSnippetsDir+"/data")) ) {
//			System.err.println("ln -s "+doc.artifact.dataDir+" "+chapterSnippetsDir+"/data");
			execCommandLine("ln -s "+doc.artifact.dataDir+" "+chapterSnippetsDir+"/data");
		}
		if ( !Files.isSymbolicLink(Paths.get(outputChapNotebooksDir+"/data")) ) {
//			System.err.println("ln -s "+doc.artifact.dataDir+" "+outputChapNotebooksDir+"/data");
			execCommandLine("ln -s ../../data "+outputChapNotebooksDir+"/data"); // relative link
		}

		// Copy resource to output notebook dir
		for (String notebookResource : doc.artifact.notebookResources) {
			execCommandLine(String.format("cp %s/%s %s", tool.inputDir, notebookResource, chapterSnippetsDir));
			execCommandLine(String.format("cp %s/%s %s", tool.inputDir, notebookResource, outputChapNotebooksDir));
		}
	}

	// combine list of code snippets for each label into file
	public void execSnippets(ChapDocInfo doc) {
		String basename = doc.getSourceBaseName();

		MultiMap<String, ExecutableCodeDef> labelToDefs = collectSnippetsByLabel(doc);

		// for each group of code with same label, create executable py file
		for (String label : labelToDefs.keySet()) {
			List<ExecutableCodeDef> defs = (List<ExecutableCodeDef>)labelToDefs.get(label);
			List<ST> snippets = getSnippetTemplates(pycodeTemplates, defs);
			ST file = pycodeTemplates.getInstanceOf("pyfile");
			file.add("snippets", snippets);
			file.add("buildDir", tool.getBuildDir());
			file.add("outputDir", tool.outputDir);
			file.add("basename", basename);
			file.add("label", label);
			String pycode = file.render();

			execAndSaveOutput(doc, label, pycode);
		}
	}

	public void execAndSaveOutput(ChapDocInfo doc, String label, String pycode) {
		String snippetsDir = tool.getBuildDir()+"/snippets";
		String basename = doc.getSourceBaseName();

		String chapterSnippetsDir = snippetsDir+"/"+basename;
		String snippetHashFilename = chapterSnippetsDir+"/"+basename+"_"+label+"-"+md5hash(pycode)+".hash";
		String snippetFilename = basename+"_"+label+".py";
		if ( !Files.exists(Paths.get(snippetHashFilename)) ) { // check whether we've generated before
			System.err.println("BUILDING "+snippetFilename);
			ParrtIO.save(snippetHashFilename, ""); // save empty hash marker file
			ParrtIO.save(chapterSnippetsDir+"/"+snippetFilename, pycode);
			// EXEC!
			String[] result = ParrtSys.execInDir(chapterSnippetsDir, "pythonw", snippetFilename);
			if ( result[1]!=null && result[1].length()>0 ) {
				System.err.println(result[1]); // errors during python compilation not exec
			}
		}
	}

	public void saveNotebooks(ChapDocInfo doc) {
		MultiMap<String, ExecutableCodeDef> labelToDefs = collectSnippetsByLabel(doc);
		for (String label : labelToDefs.keySet()) {
			List<ExecutableCodeDef> defs = (List<ExecutableCodeDef>)labelToDefs.get(label);
			saveNotebook(doc, label, defs);
		}
	}

	// Generate and exec .py file that creates .ipynb file
	public void saveNotebook(ChapDocInfo doc, String label, List<ExecutableCodeDef> defs) {
		String basename = doc.getSourceBaseName();
		String snippetsDir = tool.getBuildDir()+"/snippets";
		String chapterSnippetsDir = snippetsDir+"/"+basename;

		ST nbwriter = pycodeTemplates.getInstanceOf("noteBookWriter");
		List<String> codeBlks = ParrtCollections.map(defs, d -> d.code);
		nbwriter.add("snippets", codeBlks);
		nbwriter.add("outputDir", tool.outputDir);
		nbwriter.add("basename", basename);
		nbwriter.add("label", label);
		nbwriter.add("title", "Notebook "+label+" from Chap "+doc.docNumber+" "+defs.get(0).enclosingChapter.title);
		String nbcode = nbwriter.render();
		String nbWriterFilename = "mk_ipynb_"+basename+"_"+label+".py";
		ParrtIO.save(chapterSnippetsDir+"/"+nbWriterFilename, nbcode);

//		System.err.println("### "+chapterSnippetsDir+"/"+nbWriterFilename);
		String[] result = ParrtSys.execInDir(chapterSnippetsDir, "pythonw", nbWriterFilename);
		if ( result[1]!=null && result[1].length()>0 ) {
			System.err.println(result[1]); // errors during python compilation not exec
		}
	}

	public void createNotebooksIndexFile() {
		ST indexFile = tool.artifact.templates.getInstanceOf("NotebooksIndexFile");
		indexFile.add("file", tool.artifact);
		indexFile.add("booktitle", tool.artifact.title);

		for (ChapDocInfo doc : tool.artifact.docs) {
			String basename = doc.getSourceBaseName();

			MultiMap<String, ExecutableCodeDef> labelToDefs = collectSnippetsByLabel(doc);
			for (String label : labelToDefs.keySet()) {
				indexFile.add("names", basename+"/"+label);
			}
		}
		ParrtIO.save(tool.outputDir+"/notebooks/index.html", indexFile.render());
	}

	public void annotateTreesWithOutput(ChapDocInfo doc) {
		String basename = doc.getSourceBaseName();
		String snippetsDir = tool.getBuildDir()+"/snippets";
		String chapterSnippetsDir = snippetsDir+"/"+basename;

		MultiMap<String, ExecutableCodeDef> labelToDefs = collectSnippetsByLabel(doc);

		// For each group of snippets associated with a label
		for (String label : labelToDefs.keySet()) {
			List<ExecutableCodeDef> defs = (List<ExecutableCodeDef>) labelToDefs.get(label);
			// For each snippet
			for (ExecutableCodeDef def : defs) {
				String stderr = ParrtIO.load(chapterSnippetsDir+"/"+basename+"_"+label+"_"+def.index+".err");
				if ( def instanceof PyFigDef ) {
					((PyFigDef) def).generatedFilenameNoSuffix = "images/"+basename+"/"+basename+"_"+label+"_"+def.index;
				}
				if ( stderr.trim().length()>0 ) {
					System.err.println(stderr);
				}
				if ( def.isOutputVisible ) {
					if ( def.tree instanceof BookishParser.PyevalContext ) {
						BookishParser.PyevalContext tree = (BookishParser.PyevalContext) def.tree;
						tree.stdout = ParrtIO.load(chapterSnippetsDir+"/"+basename+"_"+label+"_"+def.index+".out");
						tree.stderr = stderr.trim();
						if ( tree.stdout.length()==0 ) tree.stdout = null;
						if ( tree.stderr.length()==0 ) tree.stderr = null;
//						System.out.println("stdout: "+tree.stdout);
//						System.out.println("stderr: "+tree.stderr);
						if ( def.displayExpr!=null ) {
							String dataFilename = basename+"_"+label+"_"+def.index+".csv";
							tree.displayData = ParrtIO.load(chapterSnippetsDir+"/"+dataFilename);
//							System.out.println("data: "+tree.displayData);
						}
					}
					else {
						BookishParser.Inline_pyContext tree = (BookishParser.Inline_pyContext) def.tree;
						tree.stdout = ParrtIO.load(chapterSnippetsDir+"/"+basename+"_"+label+"_"+def.index+".out");
						tree.stderr = stderr.trim();
						if ( tree.stdout.length()==0 ) tree.stdout = null;
						if ( tree.stderr.length()==0 ) tree.stderr = null;
						String dataFilename = basename+"_"+label+"_"+def.index+".csv";
						tree.displayData = ParrtIO.load(chapterSnippetsDir+"/"+dataFilename);
					}
				}
			}
		}
	}

	public List<ST> getSnippetTemplates(STGroup pycodeTemplates, List<ExecutableCodeDef> defs) {
		List<ST> snippets = new ArrayList<>();
		for (ExecutableCodeDef def : defs) {
			if ( !def.isEnabled ) continue;
			String tname = def.isOutputVisible ? "pyeval" : "pyfig";
			ST snippet = pycodeTemplates.getInstanceOf(tname);
			snippet.add("def",def);
			// Don't allow "plt.show()" to execute, strip it
			String code = null;
			if ( def.code!=null ) {
				code = def.code.replace("plt.show()", "");
			}
			if ( code!=null && code.trim().length()==0 ) {
				code = null;
			}
			code = HTMLEscaper.stripBangs(code);
			snippet.add("code", code);
			snippets.add(snippet);
		}
		return snippets;
	}

	// get mapping from label (or index if no label) to list of snippets
	public MultiMap<String, ExecutableCodeDef> collectSnippetsByLabel(ChapDocInfo doc) {
		MultiMap<String, ExecutableCodeDef> labelToDefs = new MultiMapOfLists<>();
		for (ExecutableCodeDef codeDef : doc.codeBlocks) { // for each code blob
			String label = codeDef.label!=null ? codeDef.label : String.valueOf(codeDef.index);
			labelToDefs.put(label, codeDef);
		}
		return labelToDefs;
	}
}
