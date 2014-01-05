import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import tagparser.TagParser;


public class Touch extends MIDlet {

	public Touch() {
		// TODO Auto-generated constructor stub
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		Display display = Display.getDisplay(this);
		Carregando.setDisplay(display);
		Displayable displayable = new Carregando();
		display.setCurrent(displayable);
		displayable = new MultiStrings(this);
		display.setCurrent(displayable);
	}

}
