package us.parr.bookish.model;

import us.parr.bookish.entity.ChapterDef;
import us.parr.bookish.semantics.Artifact;

import java.util.List;
import java.util.Map;

public class Chapter extends ContainerWithTitle {
	@ModelElement
	public PreAbstract preabstract;
	@ModelElement
	public Abstract abstract_;

	public Artifact artifact; // book or article

	public String copyright;

	public String generatedFilename;

	public Map<String,String> attributes;

	public Chapter(Artifact artifact,
	               ChapterDef def,
	               PreAbstract preabstract,
	               Abstract abstract_,
	               OutputModelObject content,
	               List<ContainerWithTitle> sections)
	{
		super(def, def.title, def.label, content);
		this.artifact = artifact;
		this.abstract_ = abstract_;
		this.preabstract = preabstract;
		this.subcontainers = sections;
		this.attributes = def.attributes;
	}
}
