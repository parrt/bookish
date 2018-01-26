package us.parr.bookish.translate

import org.antlr.v4.runtime.misc.Pair
import us.parr.bookish.Tool
import us.parr.bookish.util.StreamVacuum
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

fun tex2svg(equation : String, display : Boolean, fontsize : Int) : String {
    val eqntext = equation.trim()
    val tex = """\documentclass[fontsize=${fontsize}pt]{scrartcl}
\usepackage{graphicx}
\usepackage{epstopdf}
\usepackage{amsmath}
\usepackage{amssymb}
\usepackage{amsfonts}
\DeclareSymbolFont{operators}   {OT1}{ztmcm}{m}{n}
\DeclareSymbolFont{letters}     {OML}{ztmcm}{m}{it}
\DeclareSymbolFont{symbols}     {OMS}{ztmcm}{m}{n}
\DeclareSymbolFont{largesymbols}{OMX}{ztmcm}{m}{n}
\DeclareSymbolFont{bold}        {OT1}{ptm}{bx}{n}
\DeclareSymbolFont{italic}      {OT1}{ptm}{m}{it}
\begin{document}
\thispagestyle{empty}
$$eqntext$
\end{document}
"""

    val displaytex = """\documentclass[fontsize=${fontsize}pt]{scrartcl}
\usepackage{graphicx}
\usepackage{epstopdf}
\usepackage{amsmath}
\usepackage{amssymb}
\usepackage{amsfonts}
\DeclareSymbolFont{operators}   {OT1}{ztmcm}{m}{n}
\DeclareSymbolFont{letters}     {OML}{ztmcm}{m}{it}
\DeclareSymbolFont{symbols}     {OMS}{ztmcm}{m}{n}
\DeclareSymbolFont{largesymbols}{OMX}{ztmcm}{m}{n}
\DeclareSymbolFont{bold}        {OT1}{ptm}{bx}{n}
\DeclareSymbolFont{italic}      {OT1}{ptm}{m}{it}
\begin{document}
\thispagestyle{empty}
\[$eqntext\]
\end{document}
"""

    val tmpdir = File(System.getProperty("java.io.tmpdir")+"/bookish").getAbsolutePath()

    if ( !Files.exists(Paths.get(tmpdir)) ) {
        Files.createDirectories(Paths.get(tmpdir))
    }
    if ( !Files.exists(Paths.get(tmpdir+"/images")) ) {
        Files.createSymbolicLink(Paths.get(tmpdir + "/images"), Paths.get(Tool.rootDir + "/images"))
    }

    val texfilename = tmpdir + "/temp.tex"
    if ( display ) {
        Files.write(Paths.get(texfilename), displaytex.toByteArray())
    }
    else {
        Files.write(Paths.get(texfilename), tex.toByteArray())
    }

//    println("wrote $texfilename")

    val runtime = Runtime.getRuntime()

    var results = runProcess(tmpdir, "pdflatex", "-shell-escape", "-interaction=nonstopmode", "temp.tex")
//    println(results.a)

    for (line in results.a.split("\n")) {
        if ( line.startsWith('!') || line.startsWith("l.") ) {
            System.err.println(line)
            System.err.println(eqntext)
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
    val svg = tex2svg("\\frac{\\partial}{\\partial x}f(x^2) = 3+4", false,16)
    Files.write(Paths.get("/tmp/t.svg"), svg.toByteArray())
}