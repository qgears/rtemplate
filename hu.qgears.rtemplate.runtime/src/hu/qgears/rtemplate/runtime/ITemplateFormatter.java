package hu.qgears.rtemplate.runtime;

/**
 * On the fly template formatter callback that can be attached to a template state object.
 * 
 * The template formatter should track the state of the template output and format the string to be output
 * before it is appended to the output buffer.
 * 
 * @author rizsi
 *
 */
public interface ITemplateFormatter {
	/**
	 * This method is called before each template append method is executed.
	 * @param string the content to be formatted before appended to the output buffer
	 * @return the formatted string that should be appended to the template output buffer
	 */
	CharSequence format(CharSequence string);
	/**
	 * This method is called after each template append was executed and the template is being closed
	 * or before adding deferred template parts.
	 * This method call flushes the current state of the template formatter in case there are some characters stored in it.
	 * @return characters stored in the formatter. null is allowed.
	 */
	CharSequence flush();
}
