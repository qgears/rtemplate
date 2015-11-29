package hu.qgears.rtemplate.runtime.html;

import hu.qgears.commons.EscapeString;
import hu.qgears.commons.MultiMapTreeImpl;
import hu.qgears.commons.UtilString;
import hu.qgears.rtemplate.runtime.DummyCodeGeneratorContext;
import hu.qgears.rtemplate.runtime.RAbstractTemplatePart;

import java.io.IOException;
import java.util.List;

/**
 * Weave source code and annotations into a single HTML file.
 * The resulted HTML file will show the source code in preformatted tags.
 * 
 * Parts of the source code are annotated with {@link DecorationData} decoration objects.
 * 
 * Annotated pieces of the source code work as links that pop-up a window with the
 * annotated HTML content.
 * 
 * @author rizsi
 *
 */
public class TextWithTooltipLinks extends RAbstractTemplatePart
{
	private int lineNumber=1;
	private String src;
	private MultiMapTreeImpl<Integer, DecorationData> decorations=new MultiMapTreeImpl<>();
	private MultiMapTreeImpl<Integer, DecorationData> endings=new MultiMapTreeImpl<>();
	/**
	 * Create a source decorator instance with the given source code as input to be decorated.
	 * @param src
	 */
	public TextWithTooltipLinks(String src) {
		super(new DummyCodeGeneratorContext());
		this.src = src;
	}
	/**
	 * Add a decoration object to the annotated text.
	 * @param decoration
	 */
	public void addDecoration(DecorationData decoration)
	{
		decorations.putSingle(decoration.offset, decoration);
	}
	/**
	 * Generate the CSS scripts that drive the popup window implementation of the
	 * generated HTML.
	 * This must be included into the generated HTML in the head part.
	 * @param output
	 * @throws IOException
	 */
	public static void generateScripts(StringBuilder output) throws IOException
	{
		new TextWithTooltipLinks("").generateScripts_(output);
	}
	private void generateScripts_(StringBuilder output) throws IOException {
		templateState.out=output;
		write("<script>\nfunction toggle(elementId, toggleButtonId) {\n\tvar ele = document.getElementById(elementId);\n\tif(ele.style.display == \"block\") {\n\t\tele.style.display = \"none\";\n\t}\n\telse {\n\t\tele.style.display = \"block\";\n\t}\n\tvar s = document.getElementById(toggleButtonId).innerHTML;\n\tif (s.search(\"show\") > 0) {\n\t\ts = s.replace(\"show\",\"hide\");\n\t} else {\n\t\ts = s.replace(\"hide\",\"show\");\n\t}\n\tdocument.getElementById(toggleButtonId).innerHTML = s;\n}\n</script>\n<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>\n<script>\n\t$(function()\n\t{\n\t\t$('.tooltipButton').click(function(){\n\t\t\tvar visible = $(this).next().is(':visible');\n\t\t\t$('.tooltipContent').hide();\n\t\t\tif (visible) {\n\t\t\t\t$(this).next().hide();\n\t\t\t} else {\n\t\t\t\t$(this).next().show();\n\t\t\t}\n\t\t});\n\t\t$('.tooltipClose').click(\n\t\t\tfunction()\n\t\t\t{\n\t\t\t\tvar a=$(this).closest('div[class^=\"tooltipContent\"]');\n\t\t\t\ta.hide();\n\t\t\t});\n\t});\n</script>\n");
	}
	/**
	 * Generate the CSS scripts that drive the popup window implementation of the
	 * generated HTML.
	 * This must be included into the generated HTML in the head part.
	 * @param output
	 * @throws IOException
	 */
	public static void generateCSS(StringBuilder output) throws IOException
	{
		new TextWithTooltipLinks("").generateCSS_(output);
	}
	private void generateCSS_(StringBuilder output) throws IOException
	{
		templateState.out=output;
		write("<style>\na {\n    color: #005B81;\n    text-decoration: none;\n}\na:hover {\n    color: #E32E00;\n    text-decoration: underline;\n    cursor: pointer;\n}\n.tooltip {\n\twhite-space:pre;\n\toutline:none;\n}\n.tooltipContent {\n\twhite-space: normal;\n   z-index:10;\n  \tline-height:16px;\n\tdisplay:inline;\n\tpadding:14px 20px;\n\twidth: auto;\n\tposition:absolute;\n\tcolor:#111;\n\tborder:1px solid #DCA;\n\tbackground:#fffAF0;\n}\n</style>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>\n");
	}
	/**
	 * Generate the HTML output. Weaves all decorations into the source code and writes it into the given
	 * writer object.
	 * @param output
	 * @throws IOException
	 */
	public void generateString(StringBuilder output) throws IOException
	{
		lineNumber=1;
		templateState.out=output;
		write("<pre>");
		printLineNumberAndIncrement();
		for(int i=0;i<src.length();++i)
		{
			doEnding(i);
			endings.remove(i);
			List<DecorationData> decs=decorations.get(i);
			for(DecorationData dec:decs)
			{
				write("<span class=\"tooltip\"><a class=\"tooltipButton\">");
				
				if(dec.length==0)
				{
					doEnding(dec);
				}else
				{
					int endat=dec.length+i;
					endings.putSingle(endat, dec);
				}
			}
			decorations.remove(i);
			char c=src.charAt(i);
			if(c=='\n')
			{
				EscapeString.escapeHtml(output, "¶");
			}
			EscapeString.escapeHtml(output, ""+c);
			if(c=='\n')
			{
				printLineNumberAndIncrement();
			}
		}
		// Finish unclosed tags
		for(Integer key:endings.keySet())
		{
			doEnding(key);
		}
		write("</pre>");
	}
	private void printLineNumberAndIncrement() {
		writeObject(UtilString.padLeft(""+lineNumber, 5, '0'));
		write("|");
		lineNumber++;
	}
	private void doEnding(int i) throws IOException {
		List<DecorationData> l=endings.get(i);
		for(DecorationData d: l)
		{
			doEnding(d);
		}
	}
	private void doEnding(DecorationData d) throws IOException {
		write("</a><div class=\"tooltipContent tooltipClose\" style=\"display: none\">");
		writeObject(""+d.html);
		write("<br/><br/><a class=\"tooltipClose\">Close</a></div></span>");
	}
}
