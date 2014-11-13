package hu.qgears.rtemplate.editor;

import java.util.HashMap;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Manages the colors used by the editor. Disposes the allocated resources when
 * the editor is closed.
 * @author rizsi
 *
 */
public class ColorManager {
	Display display;
	HashMap<Integer, Color> colors=new HashMap<Integer, Color>();
	public void dispose() {
		for(Color c: colors.values())
		{
			c.dispose();
		}
	}

	public Color getColor(int r, int g, int b) {
		int key=(r<<16)+(g<<8)+b;
		Color ret=colors.get(key);
		if(ret==null)
		{
			ret=new Color(display, r, g, b );
			colors.put(key, ret);
		}
		return ret;
	}

	public void setDisplay(Display display) {
		this.display=display;
	}

}
