package hu.qgears.rtemplate.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import hu.qgears.rtemplate.RTemplate;
import hu.qgears.rtemplate.builder.RTemplateBuilder;

/**
 * {@link AbstractRTemplateHyperLinkDetector} that initializes an
 * {@link RTemplateBuilder} on given project.
 * <p>
 * Using this base class new hyperlinks can be added to RTemaplte editor and
 * Java editor to navigate into each other.
 * 
 * 
 * @author agostoni
 *
 */
public abstract class AbstractRTemplateHyperLinkDetector extends AbstractHyperlinkDetector {
	
	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (textEditor != null){
			IEditorInput editorInput = textEditor.getEditorInput();
			if (editorInput instanceof IFileEditorInput){
				IFile file = ((IFileEditorInput) editorInput).getFile();
				if (file != null) {
					if (textEditor.isDirty()){
						textEditor.doSave(new NullProgressMonitor());
					}
					RTemplateBuilder bld = RTemplateBuilder.createBuilderOn(file);
					if (bld != null){
						IDocument doc = getDocument(textEditor);
						if (doc != null){
							IFile target = bld.getPair(file);
							RTemplate parser = new RTemplate(bld.getTemplateSequences());
							return createLinks(region, doc, file,target, parser);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Create links based on given parameters.
	 * 
	 * @param region
	 *            The region in source file, that describes the current text
	 *            selection.
	 * @param doc
	 *            The source file content
	 * @param from
	 *            The source file
	 * @param to
	 *            The target file calculated by
	 *            {@link RTemplateBuilder#getPair(IFile)}
	 * @param parser
	 *            The parser that can be used to process file content.
	 * @return
	 */
	protected abstract IHyperlink[] createLinks(IRegion region, IDocument doc, IFile from, IFile to, RTemplate parser);
	
	private IDocument getDocument(ITextEditor editor) {
		return editor.getDocumentProvider().getDocument(editor.getEditorInput());
	}
}
