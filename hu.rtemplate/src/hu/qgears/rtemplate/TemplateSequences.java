package hu.qgears.rtemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Escape sequence constants for template compilation.
 * 
 * The constants can be overridden in
 *  * rtemplace.conf file when Eclipse integration is used
 *  * ant task parameters when ant task is used
 * Both Eclipse and ant task parameters have the same name as the fields in
 * this class.
 * @author rizsi
 *
 */
public class TemplateSequences {
	/**
	 * Template lines are compiled to Java lines that start with this
	 */
	public String jTemplatePre = "rtout.write(\"";
	/**
	 * Template lines are compiled to Java lines that end with this
	 */
	public String jTemplatePost = "\");";
	/**
	 * Template Java argument outputs are compiled to Java lines that start with this
	 */
	public String jOutPre = "rtcout.write(";
	/**
	 * Template Java argument outputs are compiled to Java lines that end with this
	 */
	public String jOutPost = ");";
	/**
	 * Java lines that end with this line comment will be include in template without line break
	 */
	public String jLineNonBreakPost = "//NB";
	/**
	 * Template line content after this is compiled to a Java source line
	 */
	public String tJavaLinePre = "////";
	/**
	 * Template Non break Java line tag open. Tag's content is compiled to a Java source line that ends with //NB
	 */
	public String tTagNonBreak = "##";
	/**
	 * Template set tabulators tag open. Tag's content is an integer. Generated Java source code will be indented with this number of tabulators
	 */
	public String tTagTabs = "#T";
	/**
	 * Template tag close. All tags included in template are closed with this token
	 */
	public String tCloseTag = "#";
	/**
	 * Template print tag. Tag's content is a Java expression that is written to output.
	 */
	public String tTagPrint = "#O";
	
	/**
	 * Custom tag descriptors. The template language can be extended with
	 * custom tags.
	 */
	public List<RTemplateTagType> tagTypes=new ArrayList<RTemplateTagType>();
	
	public String codeFileNameSuffix=".java";
	/**
	 * Check whether the filename matches our filter and it is 
	 * a templatable file.
	 * TODO current implementation only supports .java files 
	 * @param f
	 * @return
	 */
	public boolean fileNameMatches(File f) {
		return f.getName().endsWith(codeFileNameSuffix);
	}
}
