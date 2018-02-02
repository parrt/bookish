package us.parr.bookish.model;

import java.util.ArrayList;
import java.util.List;

public class Book {
	public String author;
	public String title;
	/** The chapter model objects (not templates) */
	public List<Document> chapterDocuments = new ArrayList<>();

	public Book(String title, String author) {
		this.author = author;
		this.title = title;
	}

	/** Add doc wrapper for a chapter */
	public void addChapterDocument(Document chapter) {
		chapterDocuments.add(chapter);
	}
}
