package hu.qgears.rtemplate.ast;

import hu.qgears.rtemplate.LinePart;
import hu.qgears.rtemplate.LinePartCode;

public class TemplateToJavaAST extends AbstractAST {

	private int javaOffset;
	
	public void addTemplateLinePart(LinePart tempalte, LinePartCode java){
		//the offset and length of template parts are already initialized
		java.setFrom(javaOffset);
		javaOffset+= java.getLength();
		addLinePartMapping(tempalte, java);
	}
	
}
