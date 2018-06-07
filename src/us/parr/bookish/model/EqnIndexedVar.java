package us.parr.bookish.model;

public class EqnIndexedVar extends EqnVar {
	public String indexname;

	public EqnIndexedVar(String varname, String indexname) {
		super(varname);
		this.indexname = indexname;
	}
}
