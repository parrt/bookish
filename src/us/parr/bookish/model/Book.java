package us.parr.bookish.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Book {
	public String author;
	public String title;

	/** The chapter model objects (not templates) */
	public List<Document> chapterDocuments = new ArrayList<>();

	/** Global labels such as citations, figures, websites.
	 *  Collected from all input markdown files.
	 */
	public Map<String,OutputModelObject> labels = new HashMap<>();

	public Book(String title, String author) {
		this.author = author;
		this.title = title;
	}

	/** Add doc wrapper for a chapter */
	public void addChapterDocument(Document chapter) {
		chapterDocuments.add(chapter);
	}
}
