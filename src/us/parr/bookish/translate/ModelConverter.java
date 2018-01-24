package us.parr.bookish.translate;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.compiler.FormalArgument;
import us.parr.bookish.model.ModelElement;
import us.parr.bookish.model.OutputModelObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/** Convert an output model tree to template hierarchy by walking
 *  the output model. Each output model object has a corresponding template
 *  of the same name.  An output model object can have nested objects.
 *  We identify those nested objects by the list of arguments in the template
 *  definition. For example, here is the definition of the parser template:
 *
 *  Parser(parser, scopes, funcs) ::= <<...>>
 *
 *  The first template argument is always the output model object from which
 *  this walker will create the template. Any other arguments identify
 *  the field names within the output model object of nested model objects.
 *  So, in this case, template Parser is saying that output model object
 *  Parser has two fields the walker should chase called scopes and funcs.
 *
 *  This simple mechanism means we don't have to include code in every
 *  output model object that says how to create the corresponding template.
 */
public class ModelConverter {
	public STGroup templates;

	public ModelConverter(STGroup templates) {
		this.templates = templates;
	}

	public ST walk(OutputModelObject omo) {
		// CREATE TEMPLATE FOR THIS OUTPUT OBJECT
		Class<? extends OutputModelObject> cl = omo.getClass();
		String templateName = cl.getSimpleName();
		if ( templateName == null ) {
			System.err.println("no model to template mapping for " + cl.getSimpleName());
			return new ST("["+templateName+" invalid]");
		}
		ST st = templates.getInstanceOf(templateName);
		if ( st == null ) {
			System.err.println("no model to template mapping for " + cl.getSimpleName());
			return new ST("["+templateName+" invalid]");
		}
		if ( st.impl.formalArguments == null ) {
			System.err.println("no formal arguments for " + templateName);
			return st;
		}

		Map<String,FormalArgument> formalArgs = st.impl.formalArguments; // LinkedHashMap

		// PASS IN OUTPUT MODEL OBJECT TO TEMPLATE AS FIRST ARG
		Set<String> argNames = formalArgs.keySet();
		Iterator<String> arg_it = argNames.iterator();
		String modelArgName = arg_it.next(); // ordered so this is first arg
		st.add(modelArgName, omo);

		Field[] allAnnotatedFields = getAllAnnotatedFields(cl);
		List<Field> modelFields = new ArrayList<>();
		for (Field fi : allAnnotatedFields) {
			ModelElement annotation = fi.getAnnotation(ModelElement.class);
			if ( annotation != null ) {
				modelFields.add(fi);
			}
		}

		// ERROR CHECKS
		// Make sure all @ModelElement fields are public (ST won't see otherwise)
		for (Field fi : allAnnotatedFields) {
			ModelElement annotation = fi.getAnnotation(ModelElement.class);
			if ( annotation != null && !Modifier.isPublic(fi.getModifiers()) ) {
				System.err.println("non-public @ModelElement in "+templateName+": "+fi.getName());
			}
		}
		// Ensure that @ModelElement fields match up with the parameters
		{
			List<String> fieldNames = map(modelFields, Field::getName);
			Set<String> t = new HashSet<>(argNames);
			t.remove(modelArgName);
			if (!t.equals(new HashSet<>(fieldNames))) {
				System.err.println("mismatch in template " + templateName + " between arguments and @ModelElement fields: " +
					                   t + "!=" + fieldNames);
			}
		}

		// COMPUTE STs FOR EACH NESTED MODEL OBJECT MARKED WITH @ModelElement AND MAKE ST ATTRIBUTE
		Set<String> usedFieldNames = new HashSet<>();
		for (Field fi : modelFields) {
			ModelElement annotation = fi.getAnnotation(ModelElement.class);
			if (annotation == null) {
				continue;
			}

			String fieldName = fi.getName();

			if (!usedFieldNames.add(fieldName)) {
				System.err.println("Model object " + omo.getClass().getSimpleName() + " has multiple fields named '" + fieldName + "'");
				continue;
			}

			// Just don't set @ModelElement fields w/o formal arg in target ST
			if ( formalArgs.get(fieldName)==null ) continue;

			try {
				Object o = fi.get(omo);
				if ( o instanceof OutputModelObject ) {  // SINGLE MODEL OBJECT?
					OutputModelObject nestedOmo = (OutputModelObject)o;
					ST nestedST = walk(nestedOmo);
//					System.out.println("set ModelElement "+fieldName+"="+nestedST+" in "+templateName);
					st.add(fieldName, nestedST);
				}
				else if ( o instanceof Collection || o instanceof OutputModelObject[] ) {
					// LIST OF MODEL OBJECTS?
					if ( o instanceof OutputModelObject[] ) {
						o = Arrays.asList((OutputModelObject[]) o);
					}
					Collection<?> nestedOmos = (Collection<?>)o;
					for (Object nestedOmo : nestedOmos) {
						if ( nestedOmo==null ) continue;
						ST nestedST = walk((OutputModelObject)nestedOmo);
//						System.out.println("set ModelElement "+fieldName+"="+nestedST+" in "+templateName);
						st.add(fieldName, nestedST);
					}
				}
				else if ( o instanceof Map ) {
					Map<?, ?> nestedOmoMap = (Map<?, ?>)o;
					Map<Object, ST> m = new LinkedHashMap<Object, ST>();
					for (Map.Entry<?, ?> entry : nestedOmoMap.entrySet()) {
						ST nestedST = walk((OutputModelObject)entry.getValue());
//						System.out.println("set ModelElement "+fieldName+"="+nestedST+" in "+templateName);
						m.put(entry.getKey(), nestedST);
					}
					st.add(fieldName, m);
				}
				else if ( o!=null ) {
					System.err.println("type of "+fieldName+"'s model element isn't recognized: "+o.getClass().getSimpleName());
				}
			}
			catch (IllegalAccessException iae) {
				System.err.printf("code generation template <%s> has missing, misnamed, or incomplete arg list; missing <%s>\n", templateName, fieldName);
			}
		}
		//st.impl.dump();
		return st;
	}

	public static Field[] getAllAnnotatedFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		while ( clazz!=null && clazz != Object.class ) {
			for (Field f : clazz.getDeclaredFields()) {
				if ( f.getAnnotations().length>0 ) {
					fields.add(f);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return fields.toArray(new Field[fields.size()]);
	}

	public static <T,R> List<R> map(Collection<T> data, Function<T,R> getter) {
		List<R> output = new ArrayList<>();
		if ( data!=null ) for (T x : data) {
			output.add(getter.apply(x));
		}
		return output;
	}

	public static <T,R> List<R> map(T[] data, Function<T,R> getter) {
		List<R> output = new ArrayList<>();
		if ( data!=null ) for (T x : data) {
			output.add(getter.apply(x));
		}
		return output;
	}

}
