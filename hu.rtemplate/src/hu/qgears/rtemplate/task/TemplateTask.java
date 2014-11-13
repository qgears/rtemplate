package hu.qgears.rtemplate.task;

import hu.qgears.rtemplate.TemplateSequences;

import java.io.File;
import java.lang.reflect.Field;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


public class TemplateTask extends Task {
	File template;
	File java;
	String direction;
	public String jTemplatePre;
	public String jTemplatePost;
	public String jOutPre;
	public String jOutPost;
	public String jLineNonBreakPost;
	
	public String tJavaLinePre;

	public String tTagNonBreak;
	public String tTagTabs;
	public String tCloseTag;
	public String tTagPrint;
	
	public String getJTemplatePre() {
		return jTemplatePre;
	}

	public void setJTemplatePre(String templatePre) {
		jTemplatePre = templatePre;
	}

	public String getJTemplatePost() {
		return jTemplatePost;
	}

	public void setJTemplatePost(String templatePost) {
		jTemplatePost = templatePost;
	}

	public String getJOutPre() {
		return jOutPre;
	}

	public void setJOutPre(String outPre) {
		jOutPre = outPre;
	}

	public String getJOutPost() {
		return jOutPost;
	}

	public void setJOutPost(String outPost) {
		jOutPost = outPost;
	}

	public String getJLineNonBreakPost() {
		return jLineNonBreakPost;
	}

	public void setJLineNonBreakPost(String lineNonBreakPost) {
		jLineNonBreakPost = lineNonBreakPost;
	}

	public String getTJavaLinePre() {
		return tJavaLinePre;
	}

	public void setTJavaLinePre(String javaLinePre) {
		tJavaLinePre = javaLinePre;
	}

	public String getTTagNonBreak() {
		return tTagNonBreak;
	}

	public void setTTagNonBreak(String tagNonBreak) {
		tTagNonBreak = tagNonBreak;
	}

	public String getTTagTabs() {
		return tTagTabs;
	}

	public void setTTagTabs(String tagTabs) {
		tTagTabs = tagTabs;
	}

	public String getTCloseTag() {
		return tCloseTag;
	}

	public void setTCloseTag(String closeTag) {
		tCloseTag = closeTag;
	}

	public String getTTagPrint() {
		return tTagPrint;
	}

	public void setTTagPrint(String tagPrint) {
		tTagPrint = tagPrint;
	}


	@Override
	public void execute() throws BuildException {
		try {
			TemplateSequences sequences=getTemplateSequences();
			new TransformDirectory(java, template, direction, sequences)
				.execute();
		} catch (Exception e1) {
			throw new BuildException(e1.getMessage(), e1);
		}
	}

	private TemplateSequences getTemplateSequences() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
		TemplateSequences sequences=new TemplateSequences();
		Field[] fields=sequences.getClass().getFields();
		for(Field f:fields)
		{
			String name=f.getName();
			Field tf=this.getClass().getField(name);
			Object value=tf.get(this);
			if(value!=null)
			{
				f.set(sequences, ""+value);
			}
		}
		return sequences;
	}

	public File getTemplate() {
		return template;
	}

	public void setTemplate(File template) {
		this.template = template;
	}

	public File getJava() {
		return java;
	}

	public void setJava(File java) {
		this.java = java;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
}
