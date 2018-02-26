package us.parr.bookish.model;

import us.parr.bookish.Tool;
import us.parr.bookish.model.entity.EntityDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Book extends OutputModelObject {
	public Tool tool;
	public String author;
	public String title;

	public List<String> filenames = new ArrayList<>();

	/** The chapter model objects (not templates) */
	public List<Document> chapterDocuments = new ArrayList<>();

	/** Global labeled entities such as chap,sec,figures,citations.
	 *  Collected from all input markdown files. Does not include
	 *  locally-scoped labels like sidenote labels.
	 */
	public Map<String,EntityDef> entities = new HashMap<>();

	/** Track which figures we have already displayed; don't show them more than once. */
	public Set<EntityDef> entitiesRendered = new HashSet<>();

	public int chapCounter = 1;

	public Book(Tool tool, String title, String author) {
		this.tool = tool;
		this.author = author;
		this.title = title;
	}

	/** Add doc wrapper for a chapter */
	public void addChapterDocument(Document doc) {
		doc.book = this;
		chapterDocuments.add(doc);
	}

	public EntityDef getEntity(String label) {
		return entities.get(label);
	}
}
