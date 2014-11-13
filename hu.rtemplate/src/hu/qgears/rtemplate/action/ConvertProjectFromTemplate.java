package hu.qgears.rtemplate.action;


import hu.qgears.rtemplate.builder.RTemplateBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class ConvertProjectFromTemplate extends AbstractConvertProject {

	@Override
	boolean toTemplate() {
		return false;
	}

	@Override
	void afterConvert() throws CoreException {
		RTemplateBuilder.deleteMarkers(selectedProject);
		IFile f=selectedProject.getFile("rtemplate.conf");
		if(f.exists())
		{
			RTemplateBuilder.deleteMarkers(f);
		}
	}

}
