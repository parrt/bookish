package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;
import us.parr.bookish.parse.BookishParser;

import java.util.HashMap;
import java.util.Map;

public class Document extends OutputModelObject {
	@ModelElement
	public Chapter chapter;

	public String markdownFilename;
	public String generatedFilename;

	/** All the labeled entities defined in this document */
	public Map<String,EntityDef> entities = new HashMap<>();

	public BookishParser.DocumentContext tree;

	public Document() {
	}

	public Document(Chapter chapter) {
		this.chapter = chapter;
	}
}
