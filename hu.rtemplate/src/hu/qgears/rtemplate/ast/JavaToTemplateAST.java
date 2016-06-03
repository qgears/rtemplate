package hu.qgears.rtemplate.ast;

import hu.qgears.rtemplate.LinePart;
import hu.qgears.rtemplate.LinePartCode;

public class JavaToTemplateAST extends AbstractAST {
	
	private int javaOffset;
	private int targetOffset;
	
	public void addJavaLinePart(LinePartCode javaLine, LinePart templateLine) {
		javaLine.setFrom(javaOffset);
		javaOffset+= javaLine.getLength();
		
		templateLine.setFrom(targetOffset);
		targetOffset+= templateLine.getLength();
		addLinePartMapping(javaLine, templateLine);
	}
	
}
