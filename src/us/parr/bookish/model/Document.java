package us.parr.bookish.model;

import us.parr.bookish.model.entity.EntityDef;
import us.parr.bookish.parse.BookishParser;

import java.util.HashMap;
import java.util.Map;

public class Document extends OutputModelObject {
	@ModelElement
	public Chapter chapter;

	public Book book; // parent
	public String markdownFilename;
	public String generatedFilename;

	/** All the labeled entities defined in this document */
	public Map<String,EntityDef> entities = new HashMap<>();

	public BookishParser.DocumentContext tree;

	public Document(BookishParser.DocumentContext tree) {
		this.tree = tree;
	}

	/** Look in this doc scope first, which has locally-visible symbols only.
	 *  If not found, look in global scope, which has chap,sec,figures,citations.
	 *  Symbols initial defined in this scope during the parse then moved
	 *  to global scope in Book.
	 */
	public EntityDef getEntity(String label) {
		EntityDef def = entities.get(label);
		if ( def==null ) {
			def = book.getEntity(label);
		}
		return def;
	}
}
