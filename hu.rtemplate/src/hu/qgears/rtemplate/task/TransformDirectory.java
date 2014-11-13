package hu.qgears.rtemplate.task;

import hu.qgears.rtemplate.RTemplate;
import hu.qgears.rtemplate.TemplateSequences;
import hu.qgears.rtemplate.util.UtilFile;

import java.io.File;

/**
 * Ant task to transform a whole directory (template to Java or Java to template).
 * @author rizsi
 *
 */
public class TransformDirectory {
	File template;
	File java;
	String direction;
	
	TemplateSequences sequences;
	
	/**
	 * 
	 * @param java
	 * @param template
	 * @param direction "toJava" or "toTemplate" 
	 * @param sequences
	 */
	public TransformDirectory(File java, File template, String direction,
			TemplateSequences sequences) {
		super();
		this.java = java;
		this.template = template;
		this.direction = direction;
		this.sequences=sequences;
	}
	public void execute() throws Exception {
		if ("toJava".equals(direction)) {
			new FileVisitor() {
				@Override
				public void visit(File f, String prefix) {
					try {
						if(f.getName().endsWith(".rt"))
						{
							String name=f.getName();
							name=name.substring(0,name.length()-".rt".length());
							File target = new File(java, prefix + name);
							String temp = UtilFile.loadAsString(f);
							temp = new RTemplate(sequences).templateToJava(temp);
							target.getParentFile().mkdirs();
							UtilFile.saveAsFile(target, temp);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.visitDir(template, "");

		} else if ("toTemplate".equals(direction)) {
			new FileVisitor() {
				@Override
				public void visit(File f, String prefix) {
					try {
						if(sequences.fileNameMatches(f))
						{
							File target = new File(template, prefix + f.getName()+".rt");
							String temp = UtilFile.loadAsString(f);
							temp = new RTemplate(sequences).javaToTemplate(temp);
							target.getParentFile().mkdirs();
							UtilFile.saveAsFile(target, temp);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.visitDir(java, "");
		} else {
			throw new RuntimeException(
					"direction must be set to either 'toJava' or 'toTemplate'");
		}
	}
}
