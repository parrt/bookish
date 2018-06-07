grammar XML;

@header {
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Arrays;

import us.parr.lib.ParrtStrings;
import us.parr.lib.ParrtCollections;
import us.parr.lib.ParrtIO;
import us.parr.bookish.model.entity.*;
import static us.parr.bookish.translate.Translator.splitSectionTitle;
}


attrs returns [Map<String,String> attrMap = new LinkedHashMap<>()]
 	:	attr_assignment[$attrMap]*
 	;

attr_assignment[Map<String,String> attrMap]
	:	name=XML_ATTR XML_EQ value=(XML_ATTR_VALUE|XML_ATTR|XML_NUM)
		{
		String v = $value.text;
		if ( v.startsWith("\"") ) {
			v = ParrtStrings.stripQuotes(v);
		}
		$attrMap.put($name.text,v);
		}
	;

XML_NUM : [0-9]+ ('.' [0-9]*)? ;
XML_ATTR : [a-zA-Z]+ ;
XML_EQ : '=' ;
XML_ATTR_VALUE : '"' ('\\"'|~'"')* '"' ;

WS : [ \t\n\r]+ -> skip ;