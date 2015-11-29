package hu.qgears.rtemplate.runtime;

import hu.qgears.rtemplate.runtime.html.DecorationData;
import hu.qgears.rtemplate.runtime.html.TextWithTooltipLinks;

import java.io.IOException;

/**
 * Create a HTML report by weaving source code with {@link TemplateTracker} annotations.
 * @author rizsi
 *
 */
public class HTMLReportOutputTemplate extends RAbstractTemplatePart
{
	private String content;
	private TemplateTracker tt;
	/**
	 * Create a HTML template for the source code content and the {@link TemplateTracker} state.
	 * @param content
	 * @param tt
	 */
	public HTMLReportOutputTemplate(String content, TemplateTracker tt)
	{
		super(new DummyCodeGeneratorContext());
		this.content=content;
		this.tt=tt;
	}
	/**
	 * Generate HTML from the stored state with the given HTML title.
	 * @param title
	 * @return the HTML content as a string string
	 */
	public String generateHTML(String title) {
		TextWithTooltipLinks twt=new TextWithTooltipLinks(content);
		for(DecorationData d: tt.decorations)
		{
			twt.addDecoration(d);
		}
		try {
			write("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<style>\n");
			TextWithTooltipLinks.generateCSS(templateState.out);
			TextWithTooltipLinks.generateScripts(templateState.out);
			write("\n<title>");
			writeObject(title);
			write("</title>\n</head>\n<body>\n<h1>");
			writeObject(title);
			write("</h1>\n");
			twt.generateString(templateState.out);
			write("</body>\n</html>\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return templateState.out.toString();
	}
}
