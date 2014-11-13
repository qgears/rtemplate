package hu.qgears.rtemplate.builder;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;

public class ProjectLock {
	private static ProjectLock instance=new ProjectLock();
	public static ProjectLock getInstance() {
		return instance;
	}

	private ProjectLock() {
	}
	private Set<IProject> lockedProjects=new HashSet<IProject>();
	
	synchronized public void lockProject(IProject p)
	{
		lockedProjects.add(p);
	}
	
	synchronized public void unlockProject(IProject p)
	{
		lockedProjects.remove(p);
	}

	synchronized public boolean isLocked(IProject project) {
		return lockedProjects.contains(project);
	}

}
