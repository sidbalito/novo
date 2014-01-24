import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import proxy.ProxyClient;


public class Teste extends MIDlet {

	public Teste() {
		// TODO Auto-generated constructor stub
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		//Display.getDisplay(this).setCurrent(new Tela());
		try {
			InputStream is = ((HttpConnection)Connector.open("http://www.google.com")).openInputStream();//new ProxyClient().connect("www.google.com");
			int len = is.available();
			System.out.println("Tamanho: "+len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
