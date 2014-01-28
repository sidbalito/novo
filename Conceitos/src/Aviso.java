import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;


public class Aviso {

	private static Display display;

	public static void aviso(String titulo, String texto, Display display) {
		Alert aviso = new Alert(titulo);
		aviso.setTimeout(Alert.FOREVER);
		aviso.setString(texto);
		display.setCurrent(aviso);
	}

	public static void aviso(String titulo, String texto) {
		aviso(titulo, texto, display);
	}

	public static void setDisplay(Display display) {
		Aviso.display = display;
	}

}
