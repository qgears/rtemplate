package hu.qgears.rtemplate;

import hu.qgears.rtemplate.apache.EscapeString;
import hu.qgears.rtemplate.ast.JavaToTemplateAST;
import hu.qgears.rtemplate.ast.TemplateToJavaAST;
import hu.qgears.rtemplate.util.UtilString;

import java.util.ArrayList;
import java.util.List;


/**
 * The template compiler class;
 * 
 * Compiles template to Java and Java to template.
 * @author rizsi
 *
 */
public class RTemplate {
	TemplateSequences sequences;
	/**
	 * Create template compiler instance.
	 * @param sequences use these escape sequences 
	 */
	public RTemplate(TemplateSequences sequences) {
		this.sequences=sequences;
	}
	
	int lastTemplatePrefixTabs = 0;

	/**
	 * Parse a java file and generate template file.
	 * @param java
	 * @return
	 */
	public String javaToTemplate(String java) {
		return parseJavaToTemplate(java).toString();
	}
	public JavaToTemplateAST parseJavaToTemplate(String java) {
		JavaToTemplateAST ast = new JavaToTemplateAST();
		List<Integer> lineIndexes=new ArrayList<Integer>();
		List<String> lines = UtilString.splitLines(java, lineIndexes);
		for (String line : lines) {
			LinePartCode javaLine = new LinePartCode(line+"\n");
			int prefixTabs = getPrefixTabs(line);
			boolean processed=false;
			for(RTemplateTagType tagType: sequences.tagTypes)
			{
				boolean isTagType=isCodeOut(line, tagType.getJavaPre(), 
						tagType.getJavaPost(),
						prefixTabs);
				if(isTagType)
				{
					String templateContent = line.substring(prefixTabs
							+ tagType.getJavaPre().length(), line.length()
							- tagType.getJavaPost().length());
					printPrefixTabsNumberIfNecessary(
							prefixTabs, ast);
					String content = tagType.getTemplatePre()+
							templateContent+tagType.getTemplatePost();
					ast.addJavaLinePart(javaLine,new LinePartCustom(tagType, content));
					processed=true;
					break;
				}
			}
			if(!processed)
			{
				boolean isTemplateprefix=getTemplatePrefixTabs(line)>-1;
				if (isTemplateprefix) {
					String templateContent = line.substring(prefixTabs
							+ sequences.jTemplatePre.length(), line.length()
							- sequences.jTemplatePost.length());
					templateContent = EscapeString
							.unescapeJava(templateContent);
					printPrefixTabsNumberIfNecessary(
							prefixTabs, ast);
					ast.addJavaLinePart(javaLine,new LinePartTemplate(templateContent));
				}else if(isCodeOut(line, sequences.jOutPre,
						sequences.jOutPost,
						prefixTabs))
				{
					String templateContent = line.substring(prefixTabs
							+ sequences.jOutPre.length(), line.length()
							- sequences.jOutPost.length());
					printPrefixTabsNumberIfNecessary(
							prefixTabs, ast);
					ast.addJavaLinePart(javaLine,new LinePartCodeOut(sequences.tTagPrint+templateContent+sequences.tCloseTag));
				} else {
					if (line.endsWith(sequences.jLineNonBreakPost)) {
						line = line.substring(prefixTabs, line.length()
								- sequences.jLineNonBreakPost.length());
						printPrefixTabsNumberIfNecessary(
								prefixTabs, ast);
						String content = sequences.tTagNonBreak + line + sequences.tCloseTag;
						ast.addJavaLinePart(javaLine,new LinePartCode(content));
					} else {
						String content = sequences.tJavaLinePre + line + '\n';
						ast.addJavaLinePart(javaLine,new LinePartCode(content));
					}
				}
			}
		}
		return ast;
	}

	private void printPrefixTabsNumberIfNecessary(
			int prefixTabs,
			JavaToTemplateAST ast) {
		if (prefixTabs != lastTemplatePrefixTabs&&!sequences.autoTab) {
			lastTemplatePrefixTabs = prefixTabs;
			
			String content = sequences.tTagTabs + prefixTabs + sequences.tCloseTag;
			ast.addJavaLinePart(new LinePartCode(""),new LinePartCode(content));
		}
	}


	/**
	 * Check whether the Java line is a template line.
	 * in case it is a template line then return the number of tab prefixes on the line.  
	 * @param line the java line to be parsed
	 * @return number of tab prefixes for template lines and -1 for non-template lines
	 */
	private int getTemplatePrefixTabs(String line) {
		int ptr = 0;
		int l = line.length();
		while (l > ptr && line.charAt(ptr) == '\t') {
			ptr++;
		}
		if (line.substring(ptr).startsWith(sequences.jTemplatePre)
				&& line.endsWith(sequences.jTemplatePost)) {
			return ptr;
		}
		return -1;
	}
	/**
	 * Check whether the Java line is a code out template line.
	 * in case it is a template line then return the number of tab prefixes on the line.  
	 * @param line the java line to be parsed
	 * @return number of tab prefixes for template lines and -1 for non-template lines
	 */
	private boolean isCodeOut(String line, String jPre,
			String jPost,
			int ptr) {
		return line.substring(ptr).startsWith(jPre)
				&& line.endsWith(jPost);
	}

	/**
	 * Get the number of prefixing tabulators on the line
	 * @param line
	 * @return the number of tabs as prefix on the line.
	 */
	private int getPrefixTabs(String line) {
		int ptr = 0;
		int l = line.length();
		while (l > ptr && line.charAt(ptr) == '\t') {
			ptr++;
		}
		return ptr;
	}

	/**
	 * Parse template and generate Java file.
	 * @param temp
	 * @return
	 */
	public String templateToJava(String temp) {
		TemplateToJavaAST ast = parseTemplateToJava(temp);
		return ast.toString();
	}
	public TemplateToJavaAST parseTemplateToJava(String temp) {
		List<LinePart> parts = parseTemplate(temp);
		TemplateToJavaAST ast = new TemplateToJavaAST();
		int currentPrefixTabs = 0;
		for (LinePart part : parts) {
			if (part instanceof LinePartTemplate) {
				StringBuilder ret = new StringBuilder();
				for (int i = 0; i < currentPrefixTabs; ++i) {
					ret.append('\t');
				}
				ret.append(sequences.jTemplatePre);
				ret.append(EscapeString.escapeJava(part.getContent()));
				ret.append(sequences.jTemplatePost);
				ret.append("\n");
				ast.addTemplateLinePart(part, new LinePartCode(ret.toString()));
			} else if (part instanceof LinePartSetTabsPrefix && !sequences.autoTab) {
				currentPrefixTabs = ((LinePartSetTabsPrefix) part).newTab;
				ast.addTemplateLinePart(part, new LinePartCode(""));
			} else if (part instanceof LinePartCode) {
				StringBuilder ret = new StringBuilder();
				if (!((LinePartCode) part).isAlradyPrefixed) {
					for (int i = 0; i < currentPrefixTabs; ++i) {
						ret.append('\t');
					}
				}else if(sequences.autoTab)
				{
					currentPrefixTabs=getPrefixTabs(part.content);
				}
				currentPrefixTabs=incrementPrefixTabsIfBracketOpen(currentPrefixTabs, part.content);
				ret.append(part.content + "\n");
				ast.addTemplateLinePart(part, new LinePartCode(ret.toString()));
			} else if (part instanceof LinePartCustom)
			{
				StringBuilder ret = new StringBuilder();
				LinePartCustom custom=(LinePartCustom) part;
				for (int i = 0; i < currentPrefixTabs; ++i) {
					ret.append('\t');
				}
				ret.append(custom.getTagType().getJavaPre());
				ret.append(part.content);
				ret.append(custom.getTagType().getJavaPost());
				ret.append("\n");
				ast.addTemplateLinePart(part, new LinePartCode(ret.toString()));
			} else if (part instanceof LinePartCodeOut) {
				StringBuilder ret = new StringBuilder();
				for (int i = 0; i < currentPrefixTabs; ++i) {
					ret.append('\t');
				}
				ret.append(sequences.jOutPre);
				ret.append(part.content);
				ret.append(sequences.jOutPost);
				ret.append("\n");
				ast.addTemplateLinePart(part, new LinePartCode(ret.toString()));
			}
		}
		return ast;
	}
	
	private int incrementPrefixTabsIfBracketOpen(int currentPrefixTabs,
			String line) {
		if(sequences.autoTab)
		{
			int ptr = line.length()-1;
			while (ptr>=0 && Character.isWhitespace(line.charAt(ptr))) {
				ptr--;
			}
			if(ptr>=0 && line.charAt(ptr)=='{')
			{
				currentPrefixTabs++;
			}
		}
		return currentPrefixTabs;
	}

	public List<LinePart> parseTemplate(String temp)
	{
		List<Integer> lineIndexes=new ArrayList<Integer>();
		List<String> lines = UtilString.splitLines(temp, lineIndexes);
		List<LinePart> parts = new ArrayList<LinePart>();
		for(int i=0;i<lines.size();++i)
		{
			String line=lines.get(i);
			int from=lineIndexes.get(i);
			if (line.startsWith(sequences.tJavaLinePre)) {
				parts.add(new LinePartCode(line
						.substring(sequences.tJavaLinePre.length()),
						from, line.length(),
						true));
			} else {
				parts.addAll(parseLine(line, from));
			}
		}
		parts=filter(parts);
		return parts;
	}

	private List<LinePart> filter(List<LinePart> parts) {
		List<LinePart> ret=new ArrayList<LinePart>();
		StringBuilder temp=null;
		int from=-1;
		int to=0;
		for(LinePart p:parts)
		{
			if( p instanceof LinePartTemplate)
			{
				if(temp==null)
				{
					temp=new StringBuilder();
					from=p.from;
				}
				temp.append(p.content);
				to=p.from+p.length;
			}else
			{
				if(temp!=null)
				{
					ret.add(new LinePartTemplate(temp.toString(), from, to-from));
					temp=null;
				}
				ret.add(p);
			}
		}
		if(temp!=null)
		{
			ret.add(new LinePartTemplate(temp.toString(), from, to-from));
			temp=null;
		}
		return ret;
	}

	/**
	 * Parse a the template file line to template parts.
	 * @param line
	 * @param from
	 * @return
	 */
	private List<LinePart> parseLine(String line, int from) {
		line = line + "\n";
		List<LinePart> ret = new ArrayList<LinePart>();
		int lastPartEnd = 0;
		int ptr = 0;
		int tagLength;
		while (ptr < line.length()) {
			boolean processed=false;
			for(RTemplateTagType tagType: sequences.tagTypes)
			{
				TagMatch tagMatch=findTag(line, ptr, tagType);
				if(tagMatch!=null)
				{
					addLastPart(line, from, lastPartEnd, ptr, ret);
					ret.add(new LinePartCustom(tagType,
							tagMatch.getContent(), 
							from+ptr, tagMatch.getLength()));
					ptr = lastPartEnd = ptr + tagMatch.getLength();
					processed=true;
					break;
				}
			}
			if(!processed)
			{
			if (line.startsWith(sequences.tJavaLinePre, ptr)) {
				addLastPart(line, from, lastPartEnd, ptr, ret);
				String javaPart = line.substring(ptr + sequences.tJavaLinePre.length(),
						line.length() - 1);
				ret.add(new LinePartCode(javaPart, from+ptr, line.length()-ptr, true));
				ptr = lastPartEnd = ptr + javaPart.length()
						+ sequences.tJavaLinePre.length();
				return ret;
			} else if (line.startsWith(sequences.tTagNonBreak, ptr)
					&& (tagLength = getTagLength(line, ptr, sequences.tTagNonBreak, sequences.tCloseTag)) >= 0) {
				addLastPart(line, from, lastPartEnd, ptr, ret);
				String javaPart = line.substring(ptr
						+ sequences.tTagNonBreak.length(), ptr
						+ sequences.tTagNonBreak.length() + tagLength);
				ret.add(new LinePartCode(javaPart + sequences.jLineNonBreakPost,
						from+ptr, sequences.tTagNonBreak.length()+sequences.tCloseTag.length()+tagLength,
						false));
				ptr = lastPartEnd = ptr + sequences.tTagNonBreak.length()
						+ tagLength + sequences.tCloseTag.length();
			} else if (line.startsWith(sequences.tTagTabs, ptr)
					&& (tagLength = getTagLength(line, ptr, sequences.tTagTabs, sequences.tCloseTag)) >= 0) {
				addLastPart(line, from, lastPartEnd, ptr, ret);
				String javaPart = line.substring(ptr
						+ sequences.tTagTabs.length(), ptr
						+ sequences.tTagTabs.length() + tagLength);
				ret.add(new LinePartSetTabsPrefix(javaPart, from+ptr, sequences.tTagTabs.length()+tagLength+sequences.tCloseTag.length()));
				ptr = lastPartEnd = ptr + sequences.tTagTabs.length()
						+ tagLength + sequences.tCloseTag.length();
			} else if (line.startsWith(sequences.tTagPrint, ptr)
					&& (tagLength = getTagLength(line, ptr, sequences.tTagPrint, sequences.tCloseTag)) >= 0) {
				addLastPart(line, from, lastPartEnd, ptr, ret);
				String javaPart = line.substring(ptr
						+ sequences.tTagPrint.length(), ptr
						+ sequences.tTagPrint.length() + tagLength);
				ret.add(new LinePartCodeOut(javaPart, from+ptr, sequences.tTagPrint.length()+
						tagLength+sequences.tCloseTag.length()));
				ptr = lastPartEnd = ptr + sequences.tTagPrint.length()
						+ tagLength + sequences.tCloseTag.length();
			} else {
				ptr++;
			}
			}
		}
		addLastPart(line, from, lastPartEnd, ptr, ret);
		return ret;
	}

	/**
	 * Match a tag to the current position on the line.
	 * @param line
	 * @param ptr
	 * @param tagType
	 * @return
	 */
	private TagMatch findTag(String line, int ptr, RTemplateTagType tagType) {
		int tagLength;
		if(line.startsWith(tagType.getTemplatePre(), ptr)
				&& 
				(tagLength = getTagLength(line, ptr, tagType.getTemplatePre(),
						tagType.getTemplatePost())) >= 0)
		{
			String content = line.substring(ptr
					+ tagType.getTemplatePre().length(), ptr
					+ tagType.getTemplatePre().length() + tagLength);
			return new TagMatch(tagType, content);
		}
		return null;
	}


	/**
	 * When parsing a line after finding the next tag on the line
	 * the letters before the tag added to the line parts list by this method.
	 * @param line the line that is currently parsed
	 * @param from the pointer of the line's first character in the template file.
	 * @param lastPartEnd the pointer of the last tag's end. So this is the pointer of the letters of this line part.
	 * @param ptr the pointer of the next tag that is after this part.
	 * @param ret add the letters wrapped as a line part object to this list
	 */
	private void addLastPart(String line, int from, int lastPartEnd, int ptr,
			List<LinePart> ret) {
		if (ptr > lastPartEnd) {
			ret.add(new LinePartTemplate(line.substring(lastPartEnd, ptr), from+lastPartEnd, ptr-lastPartEnd));
		}
	}

	private int getTagLength(String line, int ptr, String pre, String post) {
		int l = 0;
		ptr = ptr + pre.length();
		while (ptr + l < line.length()) {
			if (line.startsWith(post, ptr + l)) {
				return l;
			}
			++l;
		}
		return -1;
	}
}
