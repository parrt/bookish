package us.parr.bookish.semantics;

import org.stringtemplate.v4.STGroup;
import us.parr.bookish.Tool;
import us.parr.bookish.entity.EntityDef;
import us.parr.bookish.parse.ChapDocInfo;
import us.parr.bookish.parse.RootDocInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Track all information about an artifact, which typically consists
 *  of multiple files.  Track the list of documents, where the first
 *  is the root document.  Track the global defined entities such
 *  as figures and sections. Local entities such as side notes
 *  are stored directly within {@link ChapDocInfo}.
 */
public abstract class Artifact {
	public Tool tool;

	// track title just for completeness here at the root artifact
	public String title;

	/** A symlink to where data is for building/exec'ing python code */
	public String dataDir;

	public String cssFile;

	/** Copy these files from dataDir into generated artifact */
	public List<String> dataFilesToCopy = new ArrayList<>();

	public String copyright; // translated to output target

	/** Abstract tag in book or article root file, not chapter file(s) */
	public String abstract_; // translated to output target

	public List<String> notebookResources = new ArrayList<>();

	public RootDocInfo rootdoc;
	public List<ChapDocInfo> docs = new ArrayList<>();

	/** Attributes of the book or article tag in root file */
	public Map<String,String> attributes;

	public STGroup templates;

	public Artifact(Tool tool, String title) {
		this.tool = tool;
		this.title = title;
		templates = loadTemplates();
	}

	public void addRootDoc(RootDocInfo rootdoc) {
		this.rootdoc = rootdoc;
		this.rootdoc.artifact = this;
		this.attributes = rootdoc.attributes;
	}

	public void addDoc(ChapDocInfo doc) {
		docs.add(doc);
		doc.artifact = this;
	}

	public EntityDef getEntity(String label) {
		return rootdoc.getEntity(label);
	}

	/** Define global entity */
	public void defGlobalEntity(String label, EntityDef d) {
		rootdoc.defEntity(label, d);
	}

	public abstract STGroup loadTemplates();
}
