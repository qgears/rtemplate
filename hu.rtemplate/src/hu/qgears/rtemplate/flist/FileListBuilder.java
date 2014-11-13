package hu.qgears.rtemplate.flist;

import hu.qgears.rtemplate.util.UtilResource;
import hu.qgears.rtemplate.util.UtilString;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class FileListBuilder extends IncrementalProjectBuilder {
	@Override
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
			throws CoreException {
		try {
			if (kind == IncrementalProjectBuilder.FULL_BUILD) {
				fullBuild(args, monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					fullBuild(args, monitor);
				} else {
					fullBuild(args, monitor);
///					incrementalBuild(args, delta, monitor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof CoreException) {
				throw (CoreException) e;
			}
			throw new RuntimeException(e);
		}
		return null;
	}

//	private void incrementalBuild(Map<?,?> args, IResourceDelta delta,
//			IProgressMonitor monitor) throws CoreException {
//		delta.accept(new IResourceDeltaVisitor() {
//			
//			@Override
//			public boolean visit(IResourceDelta arg0) throws CoreException {
//				visitResource(arg0.getResource());
//				return true;
//			}
//		});
//		checkChange();
//	}
	private void checkChange() throws CoreException, UnsupportedEncodingException {
		if(!oldFiles.equals(files))
		{
			List<String> files=new ArrayList<String>(this.files);
			String filesStr=UtilString.concat(files, "", "\n", "\n");
			IFile out=getProject().getFile("fileList.txt");
			UtilResource.saveFile(out, filesStr);
		}
	}
	Set<String> files=new TreeSet<String>();
	Set<String> oldFiles=new TreeSet<String>();
	private void fullBuild(Map<?,?> args, IProgressMonitor monitor) throws CoreException, UnsupportedEncodingException {
		IProject project=getProject();
		oldFiles=files;
		files=new TreeSet<String>();
		files.clear();
		project.accept(new IResourceVisitor() {
			
			@Override
			public boolean visit(IResource res) throws CoreException {
				visitResource(res);
				return true;
			}
		});
		checkChange();
	}
	private void visitResource(IResource res)
	{
		if(res.exists())
		{
			String fileName=null;
			if(res instanceof IFile)
			{
				fileName=UtilString.concat(UtilResource.getPath((IFile)res), "/","/","");
			}else if (res instanceof IFolder)
			{
				fileName=UtilString.concat(UtilResource.getPath((IFolder)res), "/","/","/");
			}
			if(fileName!=null)
			{
				if(!fileName.startsWith("/."))
				{
					files.add(fileName);
				}
			}
		}
//		if(!res.exists())
//		{
//			String fileName=null;
//			if(res instanceof IFile)
//			{
//				fileName=UtilString.concat(UtilResource.getDirs((IFile)res), "/","/","");
//			}else if (res instanceof IFolder)
//			{
//				fileName=UtilString.concat(UtilResource.getDirs((IFile)res), "/","/","/");
//			}
//			if(fileName!=null)
//			{
//				files.remove(fileName);
//			}
//		}
	}

}
