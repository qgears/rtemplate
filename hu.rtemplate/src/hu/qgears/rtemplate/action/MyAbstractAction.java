package hu.qgears.rtemplate.action;

import java.io.File;


import hu.qgears.rtemplate.TemplateSequences;
import hu.qgears.rtemplate.builder.ProjectLock;
import hu.qgears.rtemplate.builder.RTemplateBuilder;
import hu.qgears.rtemplate.task.TransformDirectory;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;

/**
 * Do compilation of all files at once on a project in a single direction.
 * @author rizsi
 *
 */
public abstract class MyAbstractAction implements IActionDelegate2 {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IAction action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}
	@Override
	final public void run(IAction action) {
		try {
			ProjectLock.getInstance().lockProject(selectedProject);
			try
			{
				RTemplateBuilder tbld=new RTemplateBuilder();
				tbld.loadConfiguration(selectedProject);
				File javaDir=tbld.getJavaDir(selectedProject);
				File templateDir=tbld.getTemplateDir(selectedProject);
				TemplateSequences sequences=tbld.getTemplateSequences(selectedProject);
				new TransformDirectory(javaDir, templateDir, getDirection(), sequences).execute();
				selectedProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			}finally
			{
				ProjectLock.getInstance().unlockProject(selectedProject);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	abstract String getDirection();


	IProject selectedProject;
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		Object o = ((StructuredSelection) selection).getFirstElement();
		boolean enabled=false;
		if (o instanceof PlatformObject) {
			PlatformObject po = (PlatformObject) o;
			IProject project = (IProject) po.getAdapter(IProject.class);
			if (project != null) {
				this.selectedProject=project;
				try
				{
				for(ICommand c:selectedProject.getDescription().getBuildSpec())
				{
					if(c.getBuilderName().equals(AbstractConvertProject.builderName))
					{
						enabled=true;
					}
				}
				}catch(Exception e){}
			}
		}
		action.setEnabled(enabled);
	}

}
