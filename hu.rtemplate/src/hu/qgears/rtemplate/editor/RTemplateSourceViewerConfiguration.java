package hu.qgears.rtemplate.editor;

import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class RTemplateSourceViewerConfiguration extends
		SourceViewerConfiguration {
	RTemplateEditor editor;
	ColorManager colorManager;
	public RTemplateSourceViewerConfiguration(RTemplateEditor editor, ColorManager colorManager) {
		super();
		this.editor=editor;
		this.colorManager = colorManager;
	}
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		colorManager.setDisplay(sourceViewer.getTextWidget().getDisplay());
		IReconciler rec=super.getReconciler(sourceViewer);
		RTemplateReconcileStrategy coloring=new RTemplateReconcileStrategy(editor, sourceViewer, colorManager);
		rec=new MonoReconciler(coloring, false);
		return rec;
	}
}
