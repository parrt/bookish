package us.parr.bookish.translate

import org.antlr.v4.runtime.TokenStreamRewriter
import us.parr.bookish.parse.BookishLexer.*
import us.parr.bookish.parse.BookishParser
import us.parr.bookish.parse.BookishParserBaseListener

class ProcessEqn(var rewriter : TokenStreamRewriter) : BookishParserBaseListener() {
    override fun enterBlock_eqn_element(ctx: BookishParser.Block_eqn_elementContext?) {
        when ( ctx?.start?.type ) {
//            AMP -> rewriter.replace(ctx.start, "\\&")
            BLOCK_EQN_UNDERSCORE -> rewriter.replace(ctx.start, "\\_")
            BLOCK_EQN_END_ROW -> rewriter.replace(ctx.start, "\\\\\\\\")
        }
    }

    override fun enterEqn(ctx: BookishParser.EqnContext?) {
//        println("Enter equation at "+ctx?.start?.line+":"+ctx?.start?.charPositionInLine)
    }

    override fun enterEqn_element(ctx: BookishParser.Eqn_elementContext?) {
        when ( ctx?.start?.type ) {
            EQN_UNDERSCORE -> rewriter.replace(ctx.start, "\\_")
        }
    }
}
