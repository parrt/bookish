package us.parr.bookish.model;

import us.parr.bookish.entity.ChapterDef;

import java.util.List;
import java.util.Map;

public class Chapter extends ContainerWithTitle {
	@ModelElement
	public PreAbstract preabstract;
	@ModelElement
	public Abstract abstract_;

	public String generatedFilename;

	public Map<String,String> attributes;

	public Chapter(ChapterDef def,
	               PreAbstract preabstract,
	               Abstract abstract_,
	               OutputModelObject content,
	               List<ContainerWithTitle> sections)
	{
		super(def, def.title, def.label, content);
		this.abstract_ = abstract_;
		this.preabstract = preabstract;
		this.subcontainers = sections;
		this.attributes = def.attributes;
	}
}
