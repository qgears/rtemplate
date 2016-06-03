package hu.qgears.rtemplate;

public class LinePartSetTabsPrefix extends LinePart {
	int newTab=0;
	public LinePartSetTabsPrefix(String content, int from, int length) {
		super(content, from, length);
		try{
			newTab=Integer.parseInt(content);
		}catch(Exception e){}
	}

	public LinePartSetTabsPrefix(String content) {
		super(content);
		try{
			newTab=Integer.parseInt(content);
		}catch(Exception e){}
	}

}
