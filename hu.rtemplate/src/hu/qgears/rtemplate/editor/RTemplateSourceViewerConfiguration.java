package hu.qgears.rtemplate.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class RTemplateSourceViewerConfiguration extends
		TextSourceViewerConfiguration {
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
	

	@Override
	protected Map<String,IAdaptable> getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map<String, IAdaptable> tt = new HashMap<String, IAdaptable>();
		tt.put("hu.qgears.rtemplateEditor",editor);
		return tt;
	}
	
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		//load hl detectors from extension points
		return getRegisteredHyperlinkDetectors(sourceViewer);
	}
	
}
