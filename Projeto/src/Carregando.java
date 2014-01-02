import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;


public class Carregando extends Canvas {

	private static Display display;

	protected void paint(Graphics gr) {
		gr.setColor(255, 255, 255);
		gr.fillRect(0, 0, getWidth(), getHeight());
		gr.setColor(0);
		gr.drawString("CARREGANDO", 0, 0, 0);
	}

	public static void setDisplay(Display display) {
		Carregando.display = display;
	}

	public static void mostra() {
		mostra(new Carregando());
	}

	public static void mostra(Displayable displayable) {
		if(display == null || displayable ==null) return;
		display.setCurrent(displayable);
		
	}

}
