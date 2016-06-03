package hu.qgears.rtemplate.ast;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import hu.qgears.rtemplate.LinePart;

/**
 * Basic AST of the template documents. Maps {@link LinePart}s in source
 * template file to {@link LinePart}s in target template file (Java and
 * RTemaplte view of the template).
 * 
 * @author agostoni
 *
 */
public abstract class AbstractAST {
	
	private static class OffsetComparator implements Comparator<LinePart>{
		@Override
		public int compare(LinePart o1, LinePart o2) {
			int c = Integer.compare(o1.getFrom(), o2.getFrom());
			if (c == 0){
				c = Integer.compare(o1.getLength(), o2.getLength());
			}
			return c;
		}
	}
	
	private Map<LinePart,LinePart> mapping = new TreeMap<LinePart, LinePart>(new OffsetComparator());
	
	protected void addLinePartMapping(LinePart javaLine,LinePart lp) {
		mapping.put(javaLine, lp);
	}
	
	public LinePart getMappingForOffset(int offset){
		for (LinePart lp : mapping.keySet()){
			int end = lp.getFrom()+lp.getLength();
			if (offset >= lp.getFrom() && offset < end){
				return mapping.get(lp);
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		for (LinePart lp : mapping.keySet()){
			bld.append(mapping.get(lp).getContent());
		}
		return bld.toString();
	}
}
