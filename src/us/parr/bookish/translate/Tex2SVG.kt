package us.parr.bookish.translate

import org.antlr.v4.runtime.misc.Pair
import us.parr.bookish.util.StreamVacuum
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun tex2svg(equation : String, fontsize : Int) : String {
    val tex = """\documentclass[xetex,fontsize=${fontsize}pt]{scrartcl}
\usepackage{amsmath}
\usepackage{amssymb}
\usepackage{amsfonts}
%\usepackage{%FONT%}
\begin{document}
\thispagestyle{empty}
$$equation$
\end{document}
"""

    val tmpdir = File(System.getProperty("java.io.tmpdir")).getAbsolutePath()

    val texfilename = tmpdir + "/temp.tex"
    Files.write(Paths.get(texfilename), tex.toByteArray())
    println("wrote $texfilename")

    val runtime = Runtime.getRuntime()

    val cmds = listOf(
            "xelatex -shell-escape temp.tex",
            "pdfcrop temp.pdf",
            "pdf2svg temp-crop.pdf temp.svg")

    var results = runProcess(tmpdir, "xelatex", "-shell-escape", "-interaction=nonstopmode", "temp.tex")
    println(results.a)

    for (line in results.a.split("\n")) {
        if ( line.startsWith('!') || line.startsWith("l.") ) {
            System.err.println(line)
        }
    }
    if (results.b.length>0) {
        System.err.println(results.b)
    }

    results = runProcess(tmpdir, "pdfcrop", "temp.pdf")
    if (results.b.length>0) {
        System.err.println(results.b)
    }

    results = runProcess(tmpdir, "pdf2svg", "temp-crop.pdf", "temp.svg")
    if (results.b.length>0) {
        System.err.println(results.b)
    }

    val svgfilename = tmpdir + "/temp.svg"
    val svg = Files.readAllBytes(Paths.get(svgfilename))
    return String(svg)
}

@Throws(IOException::class, InterruptedException::class)
private fun runProcess(execPath: String, vararg args: String): Pair<String, String> {
    val process = Runtime.getRuntime().exec(args, null, File(execPath))
    val stdoutVacuum = StreamVacuum(process.inputStream)
    val stderrVacuum = StreamVacuum(process.errorStream)
    stdoutVacuum.start()
    stderrVacuum.start()
    process.waitFor()
    stdoutVacuum.join()
    stderrVacuum.join()
    return Pair(stdoutVacuum.toString(), stderrVacuum.toString())
}

fun main(args: Array<String>) {
    println(tex2svg("x^2 = 3+4",16))
}