package us.parr.bookish.translate

import us.parr.bookish.parse.BookishParser
import us.parr.bookish.parse.BookishParserBaseListener

class ProcessEqn : BookishParserBaseListener() {
    override fun enterEqn_element(ctx: BookishParser.Eqn_elementContext?) {
        println(ctx?.text)
        super.enterEqn_element(ctx)
    }
}