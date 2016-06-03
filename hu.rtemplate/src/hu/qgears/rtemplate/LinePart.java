package hu.qgears.rtemplate;

abstract public class LinePart {
	String content;
	int from, length;

	public LinePart(String content, int from, int length) {
		super();
		this.content = content;
		this.from=from;
		this.length=length;
	}

	public LinePart(String content) {
		this.content = content;
		length = content.length();
	}
	
	public void setFrom(int from) {
		this.from = from;
	}
	
	public String getContent() {
		return content;
	}
	@Override
	public String toString() {
		return content;
	}
	public int getFrom() {
		return from;
	}

	public int getLength() {
		return length;
	}

}
