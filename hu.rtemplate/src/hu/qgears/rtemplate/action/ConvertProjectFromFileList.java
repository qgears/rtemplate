package hu.qgears.rtemplate.action;

import org.eclipse.core.runtime.CoreException;

public class ConvertProjectFromFileList extends AbstractFileListConvertProject {

	@Override
	boolean toTemplate() {
		return false;
	}

	@Override
	void afterConvert() throws CoreException {
	}

}
