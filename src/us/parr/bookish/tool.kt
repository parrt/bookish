package us.parr.bookish

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import us.parr.bookish.parse.BookishLexer
import us.parr.bookish.parse.BookishParser
import us.parr.bookish.translate.ProcessEqn

fun main(args: Array<String>) {
    var lexer = BookishLexer(CharStreams.fromFileName("/tmp/t.md"))
    var tokens = CommonTokenStream(lexer)
    var parser = BookishParser(tokens)
    var doc = parser.document()
    println(doc.toStringTree(parser))
    var eqn = ProcessEqn()
    ParseTreeWalker.DEFAULT.walk(eqn, doc)
}