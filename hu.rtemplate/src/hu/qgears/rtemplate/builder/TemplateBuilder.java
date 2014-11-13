package hu.qgears.rtemplate.builder;


import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Incremental project builder that does rtemplate build on the
 * rtemplate projects.
 * @author rizsi
 *
 */
public class TemplateBuilder extends IncrementalProjectBuilder {
	RTemplateBuilder rtb=new RTemplateBuilder();
	public TemplateBuilder() {
	}

	@Override
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
			throws CoreException {
		try {
			rtb.setProject(getProject());
			rtb.initRTConfState();
			if (kind == IncrementalProjectBuilder.FULL_BUILD) {
				fullBuild(args, monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					fullBuild(args, monitor);
				} else {
					incrementalBuild(args, delta, monitor);
				}
			}
		} catch (Exception e) {
			if (e instanceof CoreException) {
				throw (CoreException) e;
			}
			throw new RuntimeException(e);
		}
		return null;
	}
	private void fullBuild(Map<?,?> args, IProgressMonitor monitor)
	throws CoreException {
		rtb.checkRtConf();
	}
	
	private void incrementalBuild(@SuppressWarnings("rawtypes") Map args, final IResourceDelta rootDelta,
			IProgressMonitor monitor) throws Exception {
		if(ProjectLock.getInstance().isLocked(getProject()))
		{
			return;
		}
		rootDelta.accept(new IResourceDeltaVisitor() {

			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {
				IResource resource = delta.getResource();
				if (resource instanceof IProject) {
					return true;
				} else if (resource instanceof IFile
						&& resource.getParent() instanceof IProject) {
					if (resource.exists()) {
						RTemplateBuilder.deleteMarkers(resource);
					}
					if (rtb.confFile.equals(resource.getName())) {
						rtb.checkRtConf();
					}
				} else if (resource instanceof IContainer
						&& resource.getParent() instanceof IProject) {
					if (resource.getName().equals(rtb.javaDir)
							|| resource.getName().equals(rtb.templateDir)) {
						return true;
					} else {
						return false;
					}
				} else if (resource instanceof IContainer) {
					return true;
				} else if (resource instanceof IFile) {
					try {
						if (rtb.javaDir != null && rtb.templateDir != null
								&& resource.exists()) {
							switch (rtb.getType((IFile)resource)) {
							case java:
								rtb.javaToTemplate((IFile) resource, rootDelta);
								break;
							case template:
								rtb.templateToJava((IFile) resource, rootDelta);
								break;
							default:
								break;
							}
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				return false;
			}
		});
	}
	
}
