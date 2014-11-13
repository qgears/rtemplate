package hu.qgears.rtemplate.test;

import hu.qgears.rtemplate.RTemplate;
import hu.qgears.rtemplate.TemplateSequences;
import hu.qgears.rtemplate.util.UtilFile;

import java.io.IOException;


public class TestTemplates {
	public static void main(String[] args)
	{
		try {
			new TestTemplates().run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void run() throws IOException
	{
		String temp=UtilFile.loadAsString(getClass().getResource("jtest1"), "UTF-8");
		System.out.println(new RTemplate(new TemplateSequences()).javaToTemplate(temp));
		temp=UtilFile.loadAsString(getClass().getResource("ttest1"), "UTF-8");
		System.out.println(new RTemplate(new TemplateSequences()).templateToJava(temp));
	}
}
