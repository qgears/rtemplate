package hu.qgears.rtemplate.flist;

import hu.qgears.rtemplate.util.UtilFile;
import hu.qgears.rtemplate.util.UtilResource;
import hu.qgears.rtemplate.util.UtilString;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FileListBuilder extends IncrementalProjectBuilder {
	private String error=null;
	private Set<String> files = new TreeSet<String>();
	private String oldContent = "";
	@Override
	protected IProject[] build(int kind,
			@SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
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
					// / incrementalBuild(args, delta, monitor);
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
	private void checkChange() throws CoreException,
			UnsupportedEncodingException {
		List<String> files = new ArrayList<String>(this.files.size()+4);
		if(error!=null)
		{
			files.add("# Error: "+error);
		}
		files.add(UtilString.concat(new ArrayList<String>(srcs), "# Source folders: ", ", ", ""));
		files.add(UtilString.concat(new ArrayList<String>(includes), "# Includes: '", "', '", "'"));
		files.add("# Resources in binary:");
		files.addAll(this.files);
		String filesStr = UtilString.concat(files, "", "\n", "\n");
		String outputFilePath="fileList.txt"; // The default output file path when not specified otherwise
		try
		{
			IFile fileListConf=getProject().getFile("fileList.conf");
			try(InputStream is=fileListConf.getContents(true))
			{
				Properties props = new Properties();
				props.load(new InputStreamReader(is, fileListConf.getCharset(true)));
				outputFilePath=props.getProperty("outputFile");
			}
		}catch(Exception e)
		{
			// Throw on in some possible form: Eclipse will log the problem
			//if (e instanceof CoreException) {
			//	throw (CoreException) e;
			//}
			//throw new RuntimeException(e);
		}
		if(!filesStr.equals(oldContent))
		{
			IFile out = getProject().getFile(outputFilePath);
			UtilResource.saveFile(out, filesStr);
		}
		oldContent=filesStr;
	}


	private void fullBuild(Map<?, ?> args, IProgressMonitor monitor)
			throws CoreException, UnsupportedEncodingException {
		IProject project = getProject();
		files = new TreeSet<String>();
		parseProjectSetup(project);
		project.accept(new IResourceVisitor() {
			@Override
			public boolean visit(IResource res) throws CoreException {
				visitResource(res);
				return true;
			}
		});
		checkChange();
	}

	private void parseProjectSetup(IProject project) {
		IFile f = project.getFile(".classpath");
		IFile g = project.getFile("build.properties");
		try {
			String classpath = "";
			String buildprops = "";
			if (f.exists()) {
				classpath = UtilFile.loadAsString(f.getContents(),
						f.getCharset());
			}
			if (g.exists()) {
				buildprops = UtilFile.loadAsString(g.getContents(),
						g.getCharset());
			}
			parseSetup(classpath, buildprops);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	class ClassPathHandler extends DefaultHandler
	{
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			String src=attributes.getValue("kind");
			if("src".equals(src) && "classpathentry".equals(qName))
			{
				// Classpath is unused for now
				// System.out.println("Src path: "+attributes.getValue("path"));
			}
		}
	}
	private Set<String> includes=new TreeSet<String>();
	private Set<String> srcs=new TreeSet<String>();
	public void parseSetup(String classpath, String buildprops) {
		error=null;
		includes.clear();
		srcs.clear();
		if (classpath!=null&&classpath.length() > 0) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setValidating(false);
				factory.setXIncludeAware(false);
				SAXParser saxParser = factory.newSAXParser();
				ClassPathHandler handler = new ClassPathHandler();
				saxParser.parse(new InputSource(new StringReader(classpath)), handler);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(buildprops!=null&&buildprops.length()>0)
		{
			try {
				Properties prop=new Properties();
				prop.load(new StringReader(buildprops));
				String binin=(String)prop.get("bin.includes");
				if(binin!=null)
				{
					List<String>pieces=UtilString.split(binin, ",");
					includes.addAll(pieces);
				}
				String src=(String)prop.get("source..");
				if(src!=null)
				{
					srcs.addAll(UtilString.split(src, ","));
				}
				includes.remove(".");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void visitResource(IResource res) {
		if (res.exists()) {
			String fileName = null;
			if (res instanceof IFile) {
				fileName = UtilString.concat(UtilResource.getPath((IFile) res),
						"", "/", "");
			} else if (res instanceof IFolder) {
				fileName = UtilString.concat(
						UtilResource.getPath((IFolder) res), "", "/", "/");
			}
			if (fileName != null) {
				for(String pre: includes)
				{
					if(fileName.startsWith(pre))
					{
						files.add(fileName);
					}
				}for(String src: srcs)
				{
					if(fileName.startsWith(src))
					{
						String s=fileName.substring(src.length());
						if(s.length()>0)
						{
							files.add(s);
						}
					}
				}
			}
		}
	}
}
