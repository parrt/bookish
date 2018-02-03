package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;

import java.util.ArrayList;
import java.util.List;

public class Book extends OutputModelObject {
	public String author;
	public String title;

	/** The chapter model objects (not templates) */
	public List<Document> chapterDocuments = new ArrayList<>();

	/** Global labeled entities such as citations, figures, websites.
	 *  Collected from all input markdown files.
	 */
//	public Map<String,OutputModelObject> entities = new HashMap<>();

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
		for (Document document : chapterDocuments) {
			EntityDef def = document.getEntity(label);
			if ( def!=null ) {
				return def;
			}
		}
		return null;
	}
}
