package hu.qgears.rtemplate.runtime;


/**
 * Deferred template part.
 * 
 * Deferred template parts are method calls in the code generator that generate code to the 
 * point from where they are called but they are not processed until all other code generation is done.
 * 
 * This mechanism can be used to implement:
 *  * imports/includes depending on what is required
 *  * initialization code depending on what objects will be used later, etc.
 * 
 * Implementation:
 *  * The implementation creates a {@link DeferredTemplate} object that is stored with the template state until
 *    all other processing is done. When the template is finished then the {@link DeferredTemplate} instances are processed
 *    one bu one. They access the host object state when processed so they can be used to print code for objects collected
 *    throughout code generation.
 *  * The code generated by the {@link DeferredTemplate} object is inserted into the output at the location where the
 *    deferred template object was created. (Eg. The imports are inserted into the beginning of the file).
 * 
 * @author rizsi
 *
 */
abstract public class DeferredTemplate extends RAbstractTemplatePart
{
	/**
	 * Parent of this deferred template part.
	 */
	private RAbstractTemplatePart parent;
	/**
	 * This deferred template must be inserted at this offset into the parent's output.
	 */
	private int offset;
	/**
	 * Create a deferred template at the current state of the parent template.
	 * The current offset of the parent is stored and the output will be inserted here.
	 * 
	 * In case a {@link TemplateTracker} is active it also stores the current stack trace so that the
	 * tracker output track where the deferred template object was created.
	 * @param parent
	 */
	public DeferredTemplate(RAbstractTemplatePart parent) {
		super(parent.getCodeGeneratorContext());
		this.parent=parent;
		offset=parent.getCurrentLength();
		if(templateState.tt!=null)
		{
			templateState.tt.markFirstCall();
		}
	}
	/**
	 * Generate the content of this deferred template and insert it into the parent template's output.
	 * 
	 * * Sets up the template state of the parent template to generate into this deferred template part.
	 * * Calls the deferred method.
	 * * Reset the state of the parent template
	 * * Insert the generated code into the parent template's output at the required offset.
	 */
	final public void generate() {
		TemplateState ts=parent.activate(templateState);
		try
		{
			g();
		}finally
		{
			parent.reset(ts);
		}
		finishDeferredParts();
		parent.insert(offset, templateState.out.toString(), templateState.tt);
	}

	/**
	 * This is the deferred method that will be executed when all other execution of code generation was done.
	 */
	protected abstract void g();

	/**
	 * There was a piece of code inserted into the parent template (possibly by the processing of this
	 * or another deferred template).
	 * 
	 * The insertion offset of this deferred template must be translated in case the offset of the inserted piece 
	 * is less then this object's offset.
	 * @param offset2 offset of the inserted code
	 * @param length length of the inserted code.
	 */
	public void parentInserted(int offset2, int length) {
		if(offset2<offset)
		{
			offset+=length;
		}
	}

}
