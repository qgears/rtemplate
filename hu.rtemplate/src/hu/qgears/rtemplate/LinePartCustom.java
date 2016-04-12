package hu.qgears.rtemplate;

import org.eclipse.swt.graphics.Color;

import hu.qgears.rtemplate.editor.ColorManager;

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
	public Color getColor(ColorManager colorManager) {
		try {
			String[] rgba = tagType.getColor().split(",");
			return colorManager.getColor(Integer.parseInt(rgba[0]), Integer.parseInt(rgba[1]), Integer.parseInt(rgba[2]));
		} catch (Exception e){
			return colorManager.getColor(0, 0,255);
		}
	}

}
