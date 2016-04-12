package hu.qgears.rtemplate;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
	
	/**
	 * Suffix of source code files.
	 */
	public String codeFileNameSuffix=".java";
	/**
	 * Do automatic tabulation of source code (will omit #Tx# tags from the template side)
	 */
	public boolean autoTab=false;
	/**
	 * Check whether the filename matches our filter and it is 
	 * a templatable file.
	 * @param f
	 * @return
	 */
	public boolean fileNameMatches(File f) {
		return f.getName().endsWith(codeFileNameSuffix);
	}
	public static TemplateSequences parseProperties(Properties props) throws IllegalArgumentException, IllegalAccessException {
		TemplateSequences sequences=new TemplateSequences();
		Field[] fields=sequences.getClass().getFields();
		for(Field f:fields)
		{
			if(String.class.equals(f.getType()))
			{
				String name=f.getName();
				Object value=props.get(name);
				if(value!=null)
				{
					f.set(sequences, ""+value);
				}
			}
			if(boolean.class.equals(f.getType()))
			{
				String name=f.getName();
				Object value=props.get(name);
				if(value!=null)
				{
					f.set(sequences, Boolean.parseBoolean(""+value));
				}
			}
		}
		int i=0;
		RTemplateTagType type;
		while((type=parseTagType(props, i))!=null)
		{
			sequences.tagTypes.add(type);
			i++;
		}
		return sequences;
	}
	/**
	 * Parse a custom tag type from a templates configuration file.
	 * @param props
	 * @param i
	 * @return
	 */
	static private RTemplateTagType parseTagType(Properties props, int i) {
		String jPre=props.getProperty("jPre"+i);
		String jPost=props.getProperty("jPost"+i);
		String tPre=props.getProperty("tPre"+i);
		String tPost=props.getProperty("tPost"+i);
		String color = props.getProperty("color"+i,"0,0,255");
		if(notEmpty(jPre)&&notEmpty(jPost)&&
				notEmpty(tPre)&&notEmpty(tPost))
		{
			return new RTemplateTagType(jPre, jPost, tPre, tPost,color);
		}
		return null;
	}
	static private boolean notEmpty(String post) {
		return post!=null&&post.length()>0;
	}
}
