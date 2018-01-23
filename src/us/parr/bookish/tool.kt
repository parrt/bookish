package us.parr.bookish

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import us.parr.bookish.parse.BookishLexer
import us.parr.bookish.parse.BookishParser

fun main(args: Array<String>) {
    var lexer = BookishLexer(CharStreams.fromStream(System.`in`))
    var tokens = CommonTokenStream(lexer)
    var parser = BookishParser(tokens)
    var doc = parser.document()
    println(doc.toStringTree(parser))
}