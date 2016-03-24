package hu.qgears.rtemplate.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class UtilString {
	public static List<String> splitLines(String str, List<Integer> lineIndexes)
	{
		StringBuilder currentLine=new StringBuilder();
		List<String> ret=new ArrayList<String>();
		lineIndexes.add(0);
		for(int i=0;i<str.length();++i)
		{
			char ch=str.charAt(i);
			if(ch=='\r')
			{
				
			}
			else if(ch=='\n')
			{
				ret.add(currentLine.toString());
				lineIndexes.add(i+1);
				currentLine.delete(0, currentLine.length());
			}else
			{
				currentLine.append(ch);
			}
		}
		if(currentLine.length()>0)
		{
			ret.add(currentLine.toString());
		}
		return ret;
	}
	public static List<String> split(String s, String delim)
	{
		List<String> ret=new ArrayList<String>();
		StringTokenizer tok=new StringTokenizer(s, delim);
		while(tok.hasMoreTokens())
		{
			ret.add(tok.nextToken());
		}
		return ret;
	}
	public static String concat(List<String> l, String pre, String delim, String post)
	{
		StringBuilder ret=new StringBuilder();
		ret.append(pre);
		boolean first=true;
		for(String s:l)
		{
			if(!first)
			{
				ret.append(delim);
			}
			ret.append(s);
			first=false;
		}
		ret.append(post);
		return ret.toString();
	}
}
