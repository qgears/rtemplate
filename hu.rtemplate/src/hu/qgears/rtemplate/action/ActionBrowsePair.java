package hu.qgears.rtemplate.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import hu.qgears.rtemplate.builder.RTemplateBuilder;
import hu.qgears.rtemplate.internal.Activator;

public class ActionBrowsePair implements IViewActionDelegate, IActionDelegate {
	private Object selected;
	private IFile targetFile;
	@SuppressWarnings("unused")
	private IViewPart view;
	@Override
	public void init(IViewPart view) {
		this.view=view;
	}

	@Override
	public void run(IAction action) {
		try {
			IDE.openEditor(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), targetFile, true);
		} catch (Throwable e) {
			Activator.getDefault().logError("Error browsing to rtemplate pair",e);
		}
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		try
		{
			action.setEnabled(false);
			selected=targetFile=null;
			if(selection instanceof IStructuredSelection)
			{
				Object o = ((IStructuredSelection) selection).getFirstElement();
				selected=o;
				if(selected instanceof IAdaptable)
				{
					IAdaptable a=(IAdaptable) selected;
					IResource f=(IResource)a.getAdapter(IResource.class);
					if(f instanceof IFile)
					{
						RTemplateBuilder bld= RTemplateBuilder.createBuilderOn(f);
						targetFile=bld.getPair((IFile)f);
						if(targetFile!=null)
						{
							action.setEnabled(true);
						}
					}
				}
			}
		}catch(Throwable e)
		{
			Activator.getDefault().logError("Error browsing to rtemplate pair",e);
		}
	}
}
