package us.parr.bookish.translate

import org.antlr.v4.runtime.TokenStreamRewriter
import us.parr.bookish.parse.BookishParser
import us.parr.bookish.parse.BookishParserBaseListener

class ProcessDoc(var rewriter : TokenStreamRewriter) : BookishParserBaseListener() {
    override fun enterBlock_eqn_content(ctx: BookishParser.Block_eqn_contentContext?) {
        val svg = tex2svg(ctx!!.text, true,14)
        val sourceInterval = ctx.parent.sourceInterval
        rewriter.replace(sourceInterval.a, sourceInterval.b, svg)
    }

    override fun enterEqn_content(ctx: BookishParser.Eqn_contentContext?) {
        val svg = tex2svg(ctx!!.text, false,14)
        val sourceInterval = ctx.parent.sourceInterval // parent has the delimiters which we should delete also
        rewriter.replace(sourceInterval.a, sourceInterval.b, svg)
    }
}