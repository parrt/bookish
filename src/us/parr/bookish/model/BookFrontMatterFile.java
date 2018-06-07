package us.parr.bookish.model;

import us.parr.bookish.parse.BookishParser;
import us.parr.bookish.semantics.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookFrontMatterFile extends OutputModelObject {
	public Book book;

	public Map<String,String> attributes;

	/** The chapter model objects (not templates) */
	public List<Chapter> chapterDocuments = new ArrayList<>();

	public BookFrontMatterFile(Book book) {
		this.book = book;
		BookishParser.BookContext b = book.rootdoc.getTreeAsRoot().book();
		this.attributes = b.attrs().attributes;
	}

	public String getTitle() { return book.title; }
}
