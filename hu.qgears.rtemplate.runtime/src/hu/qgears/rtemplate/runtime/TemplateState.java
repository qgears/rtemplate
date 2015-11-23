package hu.qgears.rtemplate.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * The current state of code generation.
 * Stores:
 *  * the template output buffer
 *  * template tracker objects - see {@link TemplateTracker}
 *  * the deferred parts - see {@link DeferredTemplate}
 *  *  
 * @author rizsi
 *
 */
public class TemplateState {
	/**
	 * The output of code generation.
	 */
	public StringBuilder out;
	/**
	 * {@link TemplateTracker} object to annotate output pieces with code that has generated it.
	 * It is a debug only feature.
	 * See {@link TemplateTracker}
	 */
	public TemplateTracker tt=new TemplateTracker();
	/**
	 * {@link DeferredTemplate} parts that are executed after all other code generation was done
	 * but their output is inserted to the position where they were created.
	 */
	public List<DeferredTemplate> deferredParts=new ArrayList<>();
}
