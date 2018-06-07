package us.parr.bookish.model;

import us.parr.bookish.entity.ExecutableCodeDef;
import us.parr.bookish.translate.Translator;

import java.util.Map;

public class PyFig extends PyDo {
	public PyFig(Translator translator, ExecutableCodeDef codeDef, String stdout, String stderr, Map<String, String> args) {
		super(codeDef, stdout, stderr, args);
		String width = args.get("width");
		width = translator.processImageWidth(width);
		if ( width==null ) {
			width = "100";
		}
		args.put("width", width);
	}
}
