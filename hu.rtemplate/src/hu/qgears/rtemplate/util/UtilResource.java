package hu.qgears.rtemplate.util;


import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class UtilResource {
	public static List<String> getDirs(IResource file) {
		IResource parent = file.getParent();
		List<String> ret = new ArrayList<String>();
		while (parent!=null&& !(parent.getParent() instanceof IProject)) {
			ret.add(parent.getName());
			parent = parent.getParent();
		}
		Collections.reverse(ret);
		return ret;
	}
	public static List<String> getPath(IResource file) {
		IResource parent = file;
		List<String> ret = new ArrayList<String>();
		while (parent!=null&& 
				(parent.getParent() instanceof IFile ||
						parent.getParent() instanceof IFolder)) {
			ret.add(parent.getName());
			parent = parent.getParent();
		}
		ret.add(parent.getName());
		Collections.reverse(ret);
		return ret;
	}
	public static void saveFile(IFile targetFile, String temp) throws CoreException,
		UnsupportedEncodingException {
		String charset=targetFile.getCharset(true);
		if (targetFile.exists()) {
			targetFile.setContents(new ByteArrayInputStream(temp.getBytes(charset)),
					true, false, null);
		} else {
			targetFile.create(new ByteArrayInputStream(temp.getBytes(charset)), true,
					null);
		}
	}

}
