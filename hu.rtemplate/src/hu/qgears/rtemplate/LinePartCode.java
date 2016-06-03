package hu.qgears.rtemplate;

public class LinePartCode extends LinePart {
	boolean isAlradyPrefixed;
	public LinePartCode(String content,
			int from, int length,
			boolean isAlradyPrefixed) {
		super(content, from, length);
		this.isAlradyPrefixed=isAlradyPrefixed;
	}
	
	public LinePartCode(String content){
		super(content);
	} 

}
