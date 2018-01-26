package us.parr.bookish.model;

import java.util.ArrayList;
import java.util.List;

public class Document extends OutputModelObject {
	@ModelElement
	public List<Chapter> chapters = new ArrayList<>();

	@ModelElement
	public OutputModelObject TOC;


	public void addChapter(Chapter c) {
		chapters.add(c);
	}

//	public OutputModelObject createTOCModel() {
//		TOC = chapters.get(0).createTOCModel();
//		return TOC;
//	}
}
