package us.parr.bookish.parse;

import org.antlr.v4.runtime.Parser;
import us.parr.bookish.entity.EntityDef;
import us.parr.bookish.entity.ExecutableCodeDef;
import us.parr.bookish.semantics.Artifact;

import java.util.ArrayList;
import java.util.List;

/** A parsed input file. Track the parse tree and the parser that created it.
 *  Also track semantic information such as the definitions within this document.
 *  Every document exists within an artifact.
 */
public class ChapDocInfo extends DocInfo {
	public Artifact artifact;
	public int docNumber; // from 1

	public List<ExecutableCodeDef> codeBlocks = new ArrayList<>();

	public ChapDocInfo(Artifact artifact, Parser parser, BookishParser.ChapterContext tree) {
		super(parser, tree);
		this.artifact = artifact;
		this.parser = parser;
		this.tree = tree;
	}

	public EntityDef getEntity(String label) {
		EntityDef d = entities.get(label);
		if ( d!=null ) return d;
		return artifact.getEntity(label);
	}

	public BookishParser.ChapterContext getTreeAsChapter() { return (BookishParser.ChapterContext)tree; }
}
