package hu.qgears.rtemplate.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * The current state of code generation.
 * <p>
 * Stores:
 * <li>the template output buffer
 * <li>template tracker objects - see {@link TemplateTracker}
 * <li>the deferred parts - see {@link DeferredTemplate}
 * 
 * @author rizsi
 *
 */
public class TemplateState {

	private StringBuilder out;

	private TemplateTracker tracker;
	
	private List<DeferredTemplate> deferredParts = new ArrayList<>();

	/**
	 * Appends the given String to output, and also adds the current stacktrace
	 * to the tracker (if it is setup)
	 * 
	 * @param string
	 *            The string to append
	 * @return 
	 */
	public TemplateState append(String string) {
		out.append(string);
		checkBreakPoint(null);
		if (tracker != null) {
			tracker.track(out.length(), string);
		}
		return this;
	}

	public TemplateState(StringBuilder out, TemplateTracker tracker) {
		this.out = out;
		this.tracker = tracker;
	}

	public TemplateState(boolean track) {
		this(new StringBuilder(), new TemplateTracker());
	}

	/**
	 * Debug feature to stop Java debugger when the codegenerator writes code
	 * part specified as parameter.
	 */
	private void checkBreakPoint(String expectedOutput) {
		// TODO breakpoint at a specific state of the output
		if (expectedOutput != null && out.length() > expectedOutput.length()) {
			String b = out.substring(out.length() - expectedOutput.length());
			if (b.equals(expectedOutput)) {
				System.out.println("***********");
			}
		}
	}

	/**
	 * {@link TemplateTracker} object to annotate output pieces with code that
	 * has generated it. It is a debug only feature. See {@link TemplateTracker}
	 */
	public TemplateTracker getTracker() {
		return tracker;
	}

	/**
	 * The output of code generation.
	 */
	public StringBuilder getOut() {
		return out;
	}


	public void insert(int offset, String s, TemplateTracker instertedTT) {
		out.insert(offset, s);
		for(DeferredTemplate d:deferredParts)
		{
			d.parentInserted(offset, s.length());
		}
		if(tracker!=null)
		{
			tracker.insert(offset, s, instertedTT);
		}
	}

	public void addDeferred(DeferredTemplate dt) {
		deferredParts.add(dt);
	}
	/**
	 * {@link DeferredTemplate} parts that are executed after all other code
	 * generation was done but their output is inserted to the position where
	 * they were created.
	 */
	public List<DeferredTemplate> getDeferredParts() {
		return deferredParts;
	}
	
	public void setOut(StringBuilder out) {
		this.out = out;
	}
	
}
