package hu.qgears.rtemplate.runtime;

import hu.qgears.rtemplate.runtime.html.DecorationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Track the Java stack trace of each template write call.
 * These stack traces are weaved with the output code into a single HTML file that can be used
 * to track the parts of the generated code to the method calls of the code generator.
 * @author rizsi
 *
 */
public class TemplateTracker {
	public List<DecorationData> decorations=new ArrayList<DecorationData>();
	private String postfix="";
	/**
	 * Stack traces are stored in already HTML link formatted string.
	 * @param ttLog
	 * @param skipN
	 */
	private void appendStackTrace(StringBuilder ttLog, int skipN)
	{
		StackTraceElement[] sts=Thread.currentThread().getStackTrace();
		for(int i=skipN; i<sts.length;++i)
		{
			StackTraceElement ste=sts[i];
			ttLog.append(ste.getClassName());
			ttLog.append("(<a href=\"");
			ttLog.append("eclipse://java/"+ste.getClassName()+"/"+ste.getLineNumber());
			ttLog.append("\">");
			ttLog.append(ste.getFileName());
			ttLog.append(": ");
			ttLog.append(""+ste.getLineNumber());
			ttLog.append("</a>)<br/>\n");
		}
	}
	/**
	 * A string was appended to the output. It must be tracked.
	 *  * Stores the current stack trace. It will be annotated to the piece of code generated by this append call.
	 * @param currentLengthAfterAppend the current length of the template buffer after the append is done
	 * @param string the appended string
	 */
	public void track(int currentLengthAfterAppend, String string) {
		int l=string.length();
		int from=currentLengthAfterAppend-l;
		StringBuilder ttLog=new StringBuilder();
		appendStackTrace(ttLog, 4);
		ttLog.append(postfix);
		decorations.add(new DecorationData(from, l, "none", ttLog.toString()));
	}
	/**
	 * Insert code into the host template (possibly by a deferred template part):
	 *  * All decoration after the referenced offset must be shifted.
	 *  * Entries of the {@link TemplateTracker} of the inserted code are copied into this {@link TemplateTracker} object.
	 *    Their offsets are set to the correct value: insertedDecoration.offset+=offset
	 *  
	 * @param offset insert code to this offset
	 * @param s the code to be inserted at the given offset.
	 * @param instertedTT {@link TemplateTracker} of the inserted code.
	 */
	public void insert(int offset, String s, TemplateTracker instertedTT) {
		int insertedLength=s.length();
		for(DecorationData dd: decorations)
		{
			if(dd.offset>=offset)
			{
				dd.offset+=insertedLength;
			}
		}
		if(instertedTT!=null)
		{
			for(DecorationData dd: instertedTT.decorations)
			{
				dd.offset+=offset;
				decorations.add(dd);
			}
		}
		
	}
	/**
	 * In case this tracker is part of a deferred template
	 * then we store the current stacktrace.
	 * This method is called by the {@link DeferredTemplate} when it is created.
	 */
	public void markFirstCall() {
		StringBuilder bld=new StringBuilder();
		bld.append("<hr/>");
		appendStackTrace(bld, 6);
		postfix=bld.toString();
	}
}
