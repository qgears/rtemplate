package hu.qgears.rtemplate.runtime;

import java.util.StringTokenizer;

/**
 * Generate code as template.
 * @author rizsi
 *
 */
abstract public class RAbstractTemplatePart {
	protected TemplateState templateState;
	private ICodeGeneratorContext codeGeneratorContext;
	
	/**
	 * Create a template part object that writes text into the
	 * parent template's output.
	 * @param parent
	 */
	public RAbstractTemplatePart(RAbstractTemplatePart parent) {
		super();
		this.templateState=parent.templateState;
		this.codeGeneratorContext=parent.codeGeneratorContext;
	}
	/**
	 * Create a template part object that can be used to generate code into an
	 * output buffer.
	 * @param codeGeneratorContext
	 */
	public RAbstractTemplatePart(ICodeGeneratorContext codeGeneratorContext) {
		super();
		this.templateState=new TemplateState(codeGeneratorContext.needReport());
		this.codeGeneratorContext=codeGeneratorContext;
	}
	/**
	 * Insert a string into the already generated code.
	 * All {@link TemplateTracker}s and {@link DeferredTemplate} parts offsets are updated with the insert.
	 * @param s
	 */
	final public void insert(int offset, String s, TemplateTracker instertedTT)
	{
		templateState.insert(offset, s,instertedTT);
		
	}
	/**
	 * Write a string by appending it to the output content.
	 * @param string
	 */
	protected final void write(String string) {
		templateState.append(string);
	}
	/**
	 * Write an object by appending it to the output content.
	 * Use ""+o to convert the object to string.
	 * @param o
	 */
	protected final void writeObject(Object o) {
		String string=""+o;
		templateState.append(string);
	}
	/**
	 * Get the hosting code generator context object.
	 * @return
	 */
	final public ICodeGeneratorContext getCodeGeneratorContext() {
		return codeGeneratorContext;
	}
	/**
	 * Add a method for deferred execution.
	 * The method will be called after all other code generation is done but its output will be
	 * inserted to the current offset. See {@link DeferredTemplate}.
	 * 
	 * The parameters are passed to the deferred method as an array of objects.
	 * 
	 * @param f
	 * @param param
	 */
	final protected void deferred(final Consumer<Object[]> f, final Object ... param)
	{
		DeferredTemplate dt=new DeferredTemplate(this) {
			@Override
			protected void g() {
				executeDeferredTemplate(this, f, param);
			}
		};
		setupDeferredTemplate(dt);
		templateState.addDeferred(dt);
	}
	/**
	 * Subclasses may override this method to achieve paremetrization of the deferred templates.
	 * For example add a formatter to it.
	 * @param dt
	 */
	protected void setupDeferredTemplate(DeferredTemplate dt) {
	}
	/**
	 * Execute a single deferred template.
	 * @param dt
	 * @param f
	 * @param param
	 */
	final protected void executeDeferredTemplate(DeferredTemplate dt, Consumer<Object[]> f, Object[] param) {
		f.accept(param);
	}
	/**
	 * Finish all deferred template parts by executing them and inserting their output to the
	 * output buffer.
	 */
	final protected void finishDeferredParts() {
		for(DeferredTemplate t: templateState.getDeferredParts())
		{
			t.generate();
		}
	}
	/**
	 * get the current length of the output buffer.
	 * @return
	 */
	final public int getCurrentLength() {
		return templateState.getOut().length();
	}
	/**
	 * Substitute the current template state with the given state.
	 * Used before deferred methods are executed to set up a separate template output state.
	 * @param templateState2
	 * @return the previous stored template state object
	 */
	final public TemplateState activate(TemplateState templateState2) {
		TemplateState ret=templateState;
		templateState=templateState2;
		return ret;
	}
	/**
	 * Re-set the previous template state object.
	 * Used after deferred methods are executed.
	 * @param ts
	 */
	final public void reset(TemplateState ts) {
		templateState=ts;
	}
	/**
	 * At the current state of the template finish code generation and save the output as a file:
	 *  * execute deferred template pieces
	 *  * Store the output code
	 *  * Store the output code generation report (if this feature is enabled by the codeGeneratorContext)
	 */
	final protected void finishCodeGeneration(String path) {
		finishDeferredParts();
		String o=templateState.getOut().toString();
		//logLongLines(path, o, 120);
		getCodeGeneratorContext().createFile(path, o);
		if(getCodeGeneratorContext().needReport()&&templateState.getTracker()!=null)
		{
			getCodeGeneratorContext().createReport(path, o, templateState.getTracker());
		}
	}
	
	private void logLongLines (String path, String output, int maxLength) {
		StringTokenizer st = new StringTokenizer(output, "\n");
		int lineNum = 1;
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			// Logging too long lines
			/*if (line != null && line.length() > maxLength) {
				System.out.println("Too long line, path: " + path + ", length: " + line.length() + ", lineNum: " + lineNum + ". Line:\n" + line);
			}*/
			
			// Logging lines with whitespaces at the end
			/*if (line != null && (line.endsWith(" ") || line.endsWith("\t"))) {
				System.out.println("Line ends with whitespace, path: " + path + ", lineNum: " + lineNum + ". Line:\n" + line);
			}*/
			lineNum++;
		}
	}
	
	/**
	 * Accessor to the templateState object.
	 * @return
	 */
	public TemplateState getTemplateState() {
		return templateState;
	}
}
