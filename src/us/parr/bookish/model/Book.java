package us.parr.bookish.model;

import java.util.ArrayList;
import java.util.List;

public class Book {
	public String author;
	public String title;
	/** The chapter model objects (not templates) */
	public List<Chapter> chapters = new ArrayList<>();

	public Book(String title, String author) {
		this.author = author;
		this.title = title;
	}

	public void addChapter(Chapter chapter) {
		chapters.add(chapter);
	}
}
