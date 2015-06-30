package hu.qgears.rtemplate.task;

import hu.qgears.rtemplate.TemplateSequences;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


public class TemplateTask extends Task {
	/**
	 * Templates folder
	 */
	private File template;
	/**
	 * Java folder
	 */
	private File java;
	/**
	 * Direction of compilation.
	 * Must be "toJava" or "toTemplate" 
	 */
	private String direction;
	/**
	 * The "rtemplate.conf" configuration file.
	 */
	private File configuration;
	
	@Override
	public void execute() throws BuildException {
		try {
			Properties props = new Properties();
			InputStream is = new FileInputStream(configuration);
			try {
				props.load(new InputStreamReader(is, "UTF-8"));
			} finally {
				is.close();
			}
			TemplateSequences sequences=TemplateSequences.parseProperties(props);
			new TransformDirectory(java, template, direction, sequences)
				.execute();
		} catch (Exception e1) {
			throw new BuildException(e1.getMessage(), e1);
		}
	}

	public File getTemplate() {
		return template;
	}

	public void setTemplate(File template) {
		this.template = template;
	}

	public File getJava() {
		return java;
	}

	public void setJava(File java) {
		this.java = java;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	public File getConfiguration() {
		return configuration;
	}

	public void setConfiguration(File configuration) {
		this.configuration = configuration;
	}
}
