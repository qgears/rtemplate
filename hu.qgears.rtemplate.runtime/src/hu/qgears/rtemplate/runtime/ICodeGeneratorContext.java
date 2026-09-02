package hu.qgears.rtemplate.runtime;

/**
 * Code generator's connection to the outer worlds.
 */
public interface ICodeGeneratorContext {

	/**
	 * Do we generate code generation report HTML files that allow tracking the generated code to the
	 * Java program line that has printed that line.
	 * @return
	 */
	boolean needReport();

	/**
	 * Do we generate code generation report HTML files that allow tracking the generated code to the
	 * Java program line that has printed that line.
	 * @param path The path that we ask whether we need debug report for.
	 * @return
	 */
	default boolean needReport(String path)
	{
		return needReport();
	}
	
	/**
	 * Create code generation output file.
	 * @param path path of output
	 * @param o content of the generated file.
	 */
	void createFile(String path, String o);

	/**
	 * Create a code generation report HTML file that tracks the generated code to the Java code that has generated it.
	 * @param path
	 * @param o Content of the output that is annotated with the Java stack traces stored in the TemplateTracker object
	 * @param tt stores the Java stack trace annotations that are connected to the output code by weaving them into a single HTML file.
	 */
	void createReport(String path, String o, TemplateTracker tt);

}
