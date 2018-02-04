package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Book extends OutputModelObject {
	public String author;
	public String title;

	/** The chapter model objects (not templates) */
	public List<Document> chapterDocuments = new ArrayList<>();

	/** Global labeled entities such as chap,sec,figures,citations.
	 *  Collected from all input markdown files. Does not include
	 *  locally-scoped labels like sidenote labels.
	 */
	public Map<String,EntityDef> entities = new HashMap<>();

	public Book(String title, String author) {
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
