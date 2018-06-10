package us.parr.bookish.entity;

import us.parr.bookish.parse.BookishParser;

/** These only appear to right of paragraphs like sidenotes. Must be
 *  referenced to appear but ref itself is not shown in paragraph. it is
 *  just there to identify which paragraph to associate with callout.
 */
public class CalloutDef extends SideNoteDef {
	public CalloutDef(int index, BookishParser.AttrsContext attrsCtx, String note) {
		super(index, attrsCtx, note);
	}
}
