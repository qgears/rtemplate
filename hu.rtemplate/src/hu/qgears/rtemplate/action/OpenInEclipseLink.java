package hu.qgears.rtemplate.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import hu.qgears.rtemplate.internal.Activator;

/**
 * {@link IHyperlink} that is able to open an editor on a given {@link IFile},
 * and set initial text selection.
 * 
 * @author agostoni
 *
 */
public class OpenInEclipseLink implements IHyperlink{

	private IRegion region;
	private IFile targetFile;
	private int offset;
	private int length;

	/**
	 * @param region
	 *            The region in source file, that behave as a hyperlink
	 * @param targetFile
	 *            The target file to open when the link is browsed
	 * @param offset
	 *            The offset of the text selection to set up. Should not be
	 *            negative.
	 * @param length
	 *            The length of the text selection. Should not be negative.
	 */
	public OpenInEclipseLink(IRegion region, IFile targetFile, int offset, int length) {
		super();
		this.region = region;
		this.targetFile = targetFile;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return getHyperlinkText();
	}

	@Override
	public String getHyperlinkText() {
		return "Browse to "+targetFile.getName();
	}

	@Override
	public void open() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IEditorPart openend = IDE.openEditor(page, targetFile);
			if (openend instanceof ITextEditor){
				ITextEditor te = (ITextEditor) openend;
				TextSelection ts = new TextSelection(offset, length);
				te.getSelectionProvider().setSelection(ts);
			}
		} catch (PartInitException e) {
			Activator.getDefault().logError("Cannot open editor on "+targetFile.getName(), e);
		}
	}

}
