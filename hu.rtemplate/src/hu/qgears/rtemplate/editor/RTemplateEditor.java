package hu.qgears.rtemplate.editor;



import org.eclipse.ui.editors.text.TextEditor;

/**
 * RTemplate coloring editor.
 */
public class RTemplateEditor extends TextEditor{
	ColorManager colorManager;
	/**
	 * Creates an RTemplate editor.
	 */
	public RTemplateEditor() {
		super();
		colorManager=new ColorManager();
		RTemplateSourceViewerConfiguration conf=new RTemplateSourceViewerConfiguration(this, colorManager);
		setSourceViewerConfiguration(conf);
	}
	@Override
	public void dispose() {
		super.dispose();
		colorManager.dispose();
	}
}
