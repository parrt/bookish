package us.parr.bookish.semantics;

import us.parr.bookish.parse.BookishParser;

public class ArtifactListener extends BookishBaseListener {
	public Artifact artifact;

	public ArtifactListener(Artifact artifact) {
		this.artifact = artifact;
	}

	@Override
	public void enterCopyright(BookishParser.CopyrightContext ctx) {
		String location = artifact.rootdoc.getSourceName()+" "+
			ctx.start.getLine()+":"+
			ctx.start.getCharPositionInLine();
		artifact.copyright = artifact.tool.translateString(artifact.rootdoc, ctx.content().getText(), location);
	}

	@Override
	public void enterAbstract_(BookishParser.Abstract_Context ctx) {
		String location = artifact.rootdoc.getSourceName()+" "+
			ctx.start.getLine()+":"+
			ctx.start.getCharPositionInLine();
		artifact.abstract_ = artifact.tool.translateString(artifact.rootdoc, ctx.content().getText(), location);
	}

	@Override
	public void enterNotebook_support(BookishParser.Notebook_supportContext ctx) {
		artifact.notebookResources.add(ctx.attrs().attributes.get("file") );
	}


	@Override
	public void enterDataBuild(BookishParser.DataBuildContext ctx) {
		artifact.dataDir = ctx.attrs().attributes.get("dir");
	}

	@Override
	public void enterDataCopy(BookishParser.DataCopyContext ctx) {
		artifact.dataFilesToCopy.add( ctx.attrs().attributes.get("file") );
	}
}
