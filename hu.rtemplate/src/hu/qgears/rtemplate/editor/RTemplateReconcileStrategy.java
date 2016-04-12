package hu.qgears.rtemplate.editor;



import java.util.List;

import hu.qgears.rtemplate.LinePart;
import hu.qgears.rtemplate.LinePartCode;
import hu.qgears.rtemplate.LinePartCodeOut;
import hu.qgears.rtemplate.LinePartCustom;
import hu.qgears.rtemplate.LinePartSetTabsPrefix;
import hu.qgears.rtemplate.RTemplate;
import hu.qgears.rtemplate.TemplateSequences;
import hu.qgears.rtemplate.builder.RTemplateBuilder;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

public class RTemplateReconcileStrategy implements IReconcilingStrategy {
	IDocument doc;
	RTemplateEditor editor;
	ISourceViewer sourceViewer;
	ColorManager colorManager;
	public RTemplateReconcileStrategy(RTemplateEditor editor,
			ISourceViewer sourceViewer,
			ColorManager colorManager) {
		super();
		this.editor=editor;
		this.sourceViewer = sourceViewer;
		this.colorManager=colorManager;
	}

	@Override
	public void reconcile(IRegion arg0) {
		TemplateSequences sequences=new TemplateSequences();
		try
		{
		if(editor!=null)
		{
			IEditorInput input=editor.getEditorInput();
			if(input instanceof IFileEditorInput)
			{
				IFileEditorInput fei=(IFileEditorInput) input;
				IProject project=fei.getFile().getProject();
				RTemplateBuilder tbld=new RTemplateBuilder();
				tbld.loadConfiguration(project);
				sequences=tbld.getTemplateSequences(project);
			}
		}
		}catch(Exception e){
			e.printStackTrace();
			// TODO should we log?
		}
		final String str=doc.get();
		final List<LinePart> lps=new RTemplate(sequences).parseTemplate(str);
		sourceViewer.getTextWidget().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				setTextPresentation(lps, str);
			}

		});

	}
	private void setTextPresentation(List<LinePart> lps, String str) {
		try {
			TextPresentation tp=new TextPresentation();
			for(LinePart lp:lps)
			{
				// TODO use the java preset color scheme
				if(lp instanceof LinePartCode)
				{
					StyleRange sr=new StyleRange();
					sr.foreground=
						colorManager.getColor(0, 128, 0);
					sr.start=lp.getFrom();
					sr.length=lp.getLength();
					tp.addStyleRange(sr);
				}
				else if(lp instanceof LinePartSetTabsPrefix)
				{
					StyleRange sr=new StyleRange();
					sr.foreground=
						colorManager.getColor(255, 0, 0);
					sr.start=lp.getFrom();
					sr.length=lp.getLength();
					tp.addStyleRange(sr);
				}
				else if(lp instanceof LinePartCodeOut)
				{
					StyleRange sr=new StyleRange();
					sr.foreground=
						colorManager.getColor(255, 0, 255);
					sr.fontStyle=SWT.BOLD;
					sr.start=lp.getFrom();
					sr.length=lp.getLength();
					tp.addStyleRange(sr);
				}
				else if(lp instanceof LinePartCustom)
				{
					StyleRange sr=new StyleRange();
					sr.foreground=
						((LinePartCustom) lp).getColor(colorManager);
					sr.fontStyle=SWT.BOLD;
					sr.start=lp.getFrom();
					sr.length=lp.getLength();
					tp.addStyleRange(sr);
				}
			}
			sourceViewer.changeTextPresentation(tp, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reconcile(DirtyRegion arg0, IRegion arg1) {
		// TODO should we implement it?
		System.out.println("NOT implemented! Reconcile region2: "+arg0+" "+arg1);
	}

	@Override
	public void setDocument(IDocument doc) {
		this.doc=doc;
		reconcile(null);
	}

}
