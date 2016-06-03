package hu.qgears.rtemplate.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import hu.qgears.rtemplate.LinePart;
import hu.qgears.rtemplate.RTemplate;
import hu.qgears.rtemplate.ast.JavaToTemplateAST;

/**
 * Add hyper links to Java view of template code (extends Java editor), that
 * point to the corresponding line of the tempalte file.
 * 
 * @author agostoni
 *
 */
public class HyperLinkFromJavaToTemplate extends AbstractRTemplateHyperLinkDetector {
	
	public HyperLinkFromJavaToTemplate() {
		//the ctor is needed to instantiate this class via extension point
	}

	@Override
	protected IHyperlink[] createLinks(IRegion region, IDocument doc, IFile from, IFile target, RTemplate parser) {
		JavaToTemplateAST ast = parser.parseJavaToTemplate(doc.get());
		LinePart lp = ast.getMappingForOffset(region.getOffset());
		if (lp != null){
			return new IHyperlink[]{
					new OpenInEclipseLink(region,target,lp.getFrom(),lp.getLength())	
			};
		}
		return null;
	}
	
}
