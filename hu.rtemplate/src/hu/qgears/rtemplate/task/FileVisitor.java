package hu.qgears.rtemplate.task;

import java.io.File;

abstract public class FileVisitor {
	public void visitDir(File dir, String prefix)
	{
		File[] fs=dir.listFiles();
		if(fs!=null)
		{
			for(File f:fs)
			{
				if(f.isDirectory()&&!f.getName().startsWith("."))
				{
					visitDir(f, prefix+f.getName()+"/");
				}
				else if(f.isFile())
				{
					visit(f, prefix);
				}
			}
		}
	}
	abstract public void visit(File f, String prefix);
}
