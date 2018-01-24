package us.parr.bookish

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStreamRewriter
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.stringtemplate.v4.ST
import us.parr.bookish.model.OutputModelObject
import us.parr.bookish.parse.BookishLexer
import us.parr.bookish.parse.BookishParser
import us.parr.bookish.translate.ModelConverter
import us.parr.bookish.translate.ProcessDoc
import us.parr.bookish.translate.ProcessEqn
import us.parr.bookish.translate.Translator

fun main(args: Array<String>) {
//    var lexer = BookishLexer(CharStreams.fromFileName("/Users/parrt/github/autodx/images/matrix-calculus.md"))
    var lexer = BookishLexer(CharStreams.fromFileName("/tmp/t.md"))
    var tokens = CommonTokenStream(lexer)
    var rewriter = TokenStreamRewriter(tokens)
    var parser = BookishParser(tokens)
    var doc = parser.document()
//    println(doc.toStringTree(parser))
//    var eqn = ProcessDoc(rewriter)
//    ParseTreeWalker.DEFAULT.walk(eqn, doc)
//    println(rewriter.getText())

    val trans = Translator()
    val omo = trans.visitDocument(doc)
    val converter = ModelConverter(trans.templates)
    val outputST = converter.walk(omo)
    println(outputST.render())
}