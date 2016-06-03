package hu.qgears.rtemplate.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import hu.qgears.rtemplate.LinePart;
import hu.qgears.rtemplate.RTemplate;
import hu.qgears.rtemplate.ast.TemplateToJavaAST;

/**
 * Add hyperlink to the template view of template code (extends RTemplate
 * editor) that point to the corresponding line within the Java view of template
 * file.
 * 
 * @author agostoni
 *
 */
public class HyperLinkFromTemplateToJava extends AbstractRTemplateHyperLinkDetector {

	public HyperLinkFromTemplateToJava() {
		//the ctor is needed to instantiate this class via extension point
	}
	
	@Override
	protected IHyperlink[] createLinks(IRegion region, IDocument doc, IFile from, IFile to, RTemplate parser) {
		TemplateToJavaAST ast = parser.parseTemplateToJava(doc.get());
		LinePart l = ast.getMappingForOffset(region.getOffset());
		if (l != null){
			return new IHyperlink[]{
					new OpenInEclipseLink(region,to,l.getFrom(),l.getLength())	
			};
		}
		return null;
	}

}
