package hu.qgears.rtemplate;

public class LinePartCustom extends LinePart {
	RTemplateTagType tagType;
	public RTemplateTagType getTagType() {
		return tagType;
	}
	public LinePartCustom(RTemplateTagType tagType, String content,
			int from, int length) {
		super(content, from, length);
		this.tagType=tagType;
	}

}
