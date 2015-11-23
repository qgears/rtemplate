package hu.qgears.rtemplate.runtime.html;

/**
 * DTO to store a HTML reference and popup window data.
 * @author rizsi
 *
 */
public class DecorationData {
	/**
	 * Attach the decoration to the source code at this offset.
	 */
	public int offset;
	/**
	 * Length of the decorated part of the generated code in characters.
	 */
	public int length;
	/**
	 * The HTML content of the popup window annotation connected to the source code.
	 */
	public String html;
	/**
	 * This link can be used to reference (HTML anchor) this decoration in the HTML.
	 */
	public String referenceLink;

	public DecorationData(int offset, int length, String referenceLink, String html) {
		this.offset=offset;
		this.length = length;
		this.html = html;
		this.referenceLink=referenceLink;
	}
}
