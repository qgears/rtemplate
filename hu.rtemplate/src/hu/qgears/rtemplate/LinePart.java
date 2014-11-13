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
