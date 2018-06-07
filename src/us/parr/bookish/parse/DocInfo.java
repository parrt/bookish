package us.parr.bookish.parse;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import us.parr.bookish.entity.EntityDef;
import us.parr.bookish.semantics.Artifact;
import us.parr.lib.ParrtIO;

import java.util.HashMap;
import java.util.Map;

public class DocInfo {
	public Artifact artifact;
	public Parser parser;
	public ParserRuleContext tree;

	/** For root doc, these are global labeled entities such as
	 *  chap,sec,figures,citations. Collected from all input chap files.
	 *  Does not include locally-scoped labels like sidenote labels.
	 *  Those are stored in same field but for {@link ChapDocInfo} subclass.
	 */
	public Map<String,EntityDef> entities = new HashMap<>();

	public DocInfo(Artifact artifact, Parser parser, ParserRuleContext tree) {
		this.artifact = artifact;
		this.parser = parser;
		this.tree = tree;
	}

	public String getGeneratedFilename(String target) {
		String outFilename = getSourceBaseName()+".html";
		if ( target.equals("latex") ) {
			outFilename = getSourceBaseName()+".tex";
		}
		return outFilename;
	}

	public String getSourceBaseName() {
		return ParrtIO.stripFileExtension(getSourceName());
	}

	public String getSourcePathName() {
		return parser.getInputStream().getSourceName();
	}

	public String getSourceName() {
		return ParrtIO.basename(parser.getInputStream().getSourceName());
	}

	public EntityDef getEntity(String label) {
		return entities.get(label);
	}

	public void defEntity(String label, EntityDef d) {
		entities.put(label, d);
	}
}
