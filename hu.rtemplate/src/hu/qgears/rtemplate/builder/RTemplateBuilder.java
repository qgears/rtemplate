package hu.qgears.rtemplate.builder;

import hu.qgears.rtemplate.RTemplate;
import hu.qgears.rtemplate.TemplateSequences;
import hu.qgears.rtemplate.internal.Activator;
import hu.qgears.rtemplate.util.UtilFile;
import hu.qgears.rtemplate.util.UtilResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class RTemplateBuilder {

	enum ConfState {
		unknown, ok, fail
	}

	enum Type {
		template, java, none
	}

	ConfState previous = ConfState.unknown;
	final static String KEY = "hu.qgears.rtemplate";
	String confFile = "rtemplate.conf";
	boolean updateErrors=true;
	
	public RTemplateBuilder() {
	}
	public RTemplateBuilder(boolean updateErrors) {
		this.updateErrors=updateErrors;
	}

	String javaDir;
	String templateDir;

	private IProject project;
	

	public IProject getProject() {
		return project;
	}
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public void initRTConfState() throws CoreException {
		if (previous.equals(ConfState.unknown)) {
			checkRtConf();
		}
	}
		
	protected void templateToJava(IFile file, IResourceDelta delta) throws CoreException, IOException {
		IFile targetFile=getPair(file);
		if(targetFile!=null)
		{
			// Template files should not "generate" new Java files
			// Creating a new Java template class must be done on the Java side
			IPath targetPath=targetFile.getProjectRelativePath();
			boolean javaAlsoChanged=delta.findMember(targetPath)!=null;
			
			if(!(targetFile.exists()&&
					targetFile.isSynchronized(IFile.DEPTH_ONE)))
			{
				// Do not create Java file and do not overwrite file that was changed outside the editor
				return;
			}
			if(javaAlsoChanged)
			{
				// Do not overwrite Java file that was changed in the same refresh transaction!
				File fTemplate=file.getLocation().toFile();
				File fTarget=targetFile.getLocation().toFile();
				Activator.getDefault().getLog().log(new Status(IStatus.INFO, 
						Activator.PLUGIN_ID, "Template build disabled due to Java file also changed"+
						fTemplate.getAbsolutePath()+" "+fTarget.getAbsolutePath()));
				return;
			}
			File fTemplate=file.getLocation().toFile();
			File fTarget=targetFile.getLocation().toFile();
			long templateTimeStamp=fTemplate.lastModified();
			long targetTimeStamp=fTarget.lastModified();
			// Template files should not overwrite newer Java files
			// This caused problems in some cases where
			// Java files are handled by version control while
			// template files are not
			if(templateTimeStamp<=targetTimeStamp)
			{
				Activator.getDefault().getLog().log(new Status(IStatus.INFO, 
						Activator.PLUGIN_ID, "Template build disabled due to old timestamp: "+templateTimeStamp+" "+targetTimeStamp+" "+
						fTemplate.getAbsolutePath()+" "+fTemplate.exists()+" "+fTarget.getAbsolutePath()+" "+fTarget.exists()));
				return;
			}
			if(getLogEnabled())
			{
				Activator.getDefault().getLog().log(new Status(IStatus.INFO, 
					Activator.PLUGIN_ID, "template compiled to Java: "+
					fTemplate.getAbsolutePath()+" to: "+fTarget.getAbsolutePath()));
			}
			createIfNotExists((IFolder) targetFile.getParent());
			InputStream is = file.getContents();
			try {
				String temp = UtilFile.loadAsString(is, file.getCharset(true));
				temp = new RTemplate(sequences).templateToJava(temp);
				UtilResource.saveFile(targetFile, temp);
			} finally {
				is.close();
			}
		}
	}

	void createIfNotExists(IFolder folder) throws CoreException {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder) {
				createIfNotExists((IFolder) parent);
			}
			folder.create(true, true, null);
		}
	}

	protected void javaToTemplate(IFile file, IResourceDelta delta) throws CoreException, IOException {
		IFile targetFile=getPair(file);
		if(targetFile!=null)
		{
			// Template files should not "generate" new Java files
			// Creating a new Java template class must be done on the Java side
			IPath targetPath=targetFile.getProjectRelativePath();
			boolean templateAlsoChanged=delta.findMember(targetPath)!=null;
			
			if(templateAlsoChanged)
			{
				// Log the fact that the template has also bee changed since the last
				// synchronized point!
				File fTemplate=file.getLocation().toFile();
				File fTarget=targetFile.getLocation().toFile();
				Activator.getDefault().getLog().log(new Status(IStatus.INFO, 
						Activator.PLUGIN_ID, "Java and template file changed in the same transaction: "+
						fTemplate.getAbsolutePath()+" "+fTarget.getAbsolutePath()));
			}
			if(getLogEnabled())
			{
				File fTemplate=file.getLocation().toFile();
				File fTarget=targetFile.getLocation().toFile();
				Activator.getDefault().getLog().log(new Status(IStatus.INFO, 
					Activator.PLUGIN_ID, "Java compiled to template: "+
					fTemplate.getAbsolutePath()+" to: "+fTarget.getAbsolutePath()));
			}
			createIfNotExists((IFolder)targetFile.getParent());
			InputStream is = file.getContents();
			try {
				String charset=file.getCharset(true);
				String temp = UtilFile.loadAsString(is, charset);
				temp = new RTemplate(sequences).javaToTemplate(temp);
				UtilResource.saveFile(targetFile, temp);
			} finally {
				is.close();
			}
		}
	}

	private boolean getLogEnabled() {
		return false;
	}
	public IFile getPair(IFile file)
	{
		switch (getType(file)) {
		case template:
		{
			String name = file.getName();
			if (name.endsWith(".rt")) {
				name = name.substring(0, name.length() - ".rt".length());
			}else
			{
				return null;
			}
			List<String> dirs = UtilResource.getDirs(file);
			IFolder target = getProject().getFolder(javaDir);
			for (String s : dirs) {
				target = target.getFolder(s);
			}
			IFile targetFile = target.getFile(name);
			return targetFile;
		}
		case java:
		{
			String name = file.getName();
			List<String> dirs = UtilResource.getDirs(file);
			IFolder target = getProject().getFolder(templateDir);
			for (String s : dirs) {
				target = target.getFolder(s);
			}
			name = name + ".rt";
			IFile targetFile = target.getFile(name);
			return targetFile;
		}
		default:
			break;
		}
		return null;
	}
	
	protected Type getType(IFile file) {
		Type ret=getTypeRecursive(file);
		switch (ret) {
		case java:
			try
			{
				if(sequences.fileNameMatches(file.getFullPath().toFile()))
				{
					return Type.java;
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			break;
		case template:
			if(file.getName().endsWith(".rt"))
			{
				return Type.template;
			}
		default:
			break;
		}
		return Type.none;
	}
	private Type getTypeRecursive(IResource res) {
		IResource parent = res;
		while (!(parent.getParent() instanceof IProject)) {
			parent = parent.getParent();
		}
		if (parent.getName().equals(javaDir)) {
			return Type.java;
		} else if (parent.getName().equals(templateDir)) {
			return Type.template;
		} else {
			return Type.none;
		}
	}
	public ConfState loadConfiguration(IProject project) throws CoreException
	{
		IFile f = project.getFile(confFile);

		ConfState newConfState;
		if (!f.exists()) {
			newConfState = ConfState.fail;
		} else {
			newConfState = ConfState.ok;
			if(updateErrors)
			{
				deleteMarkers(f);
			}
			try {
				Properties props = new Properties();
				InputStream is = f.getContents();
				try {
					props.load(new InputStreamReader(is, f.getCharset(true)));
				} finally {
					is.close();
				}
				javaDir = props.getProperty("javaDir");
				templateDir = props.getProperty("templateDir");
				sequences=TemplateSequences.parseProperties(props);
			} catch (Exception e) {
				addError(f, "Error parsing as properties file: "
						+ e.getMessage());
			}
			if (javaDir == null || javaDir.length() == 0) {
				addError(f, "Property \"javaDir\" is mandatory!");
			}
			if (templateDir == null || templateDir.length() == 0) {
				addError(f, "Property \"templateDir\" is mandatory!");
			}
			if (javaDir != null && javaDir.indexOf('/') >= 0) {
				addError(f,
						"Property \"javaDir\" must not contain the character '/'");
			}
			if (templateDir != null && templateDir.indexOf('/') >= 0) {
				addError(f,
						"Property \"templateDir\" must not contain the character '/'");
			}
			if (templateDir != null && javaDir != null
					&& javaDir.equals(templateDir)) {
				addError(f,
						"Property \"templateDir\" and \"javaDir\" must be different!");
				javaDir = templateDir = null;
			}
		}
		return newConfState;
	}
	public void checkRtConf() throws CoreException {
		ConfState newConfState=loadConfiguration(getProject());
		changeConfState(newConfState);
	}
	public static void deleteMarkers(IResource resource) throws CoreException {
		IMarker[] markers = resource.findMarkers(IMarker.PROBLEM, true,
				IResource.DEPTH_ZERO);
		for (IMarker marker : markers) {
			if (KEY.equals(marker.getAttribute(KEY))) {
				marker.delete();
			}
		}
	}

	void changeConfState(ConfState newConfState) throws CoreException {
		if (!newConfState.equals(previous)) {
			if(updateErrors)
			{
				deleteMarkers(getProject());
			}
			if (newConfState.equals(ConfState.fail)) {
				addError(getProject(), "rtemplate configuration file: "
						+ confFile + " does not exist");
			}
		}
	}

	private void addError(IResource project, String string)
			throws CoreException {
		if(updateErrors)
		{
			IMarker marker = project.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			marker.setAttribute(IMarker.MESSAGE, string);
			marker.setAttribute(KEY, KEY);
		}
	}

	public File getJavaDir(IProject project) {
		if(javaDir==null)
		{
			return null;
		}else
		{
			return project.getFolder(javaDir).getLocation().toFile();
		}
	}
	public File getTemplateDir(IProject project) {
		if(templateDir==null)
		{
			return null;
		}else
		{
			return project.getFolder(templateDir).getLocation().toFile();
		}
	}
	TemplateSequences sequences=new TemplateSequences();

	public TemplateSequences getTemplateSequences() {
		return sequences;
	}
	
	public static RTemplateBuilder createBuilderOn(IResource file){
		RTemplateBuilder bld=new RTemplateBuilder(false);
		bld.setProject(file.getProject());
		try {
			bld.initRTConfState();
			return bld;
		} catch (CoreException e) {
			Activator.getDefault().logError("Error parsing RTemplate configuration state",e);
		}
		return null;
	}

}
