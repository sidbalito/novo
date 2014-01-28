import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


public class Conceitos extends MIDlet {

	private Definicoes definicoes;
	private String path;
	private Display display;

	public Conceitos() {

	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		salvaDefinicoes();
	}

	protected void pauseApp() {
		salvaDefinicoes();
	}

	protected void startApp() throws MIDletStateChangeException {
		if(display == null) display = Display.getDisplay(this);
		Aviso.setDisplay(display);
		Verbetes v = new Verbetes(this);
		v.exibir(null);
		if(definicoes == null) carregaDefinicoes();
		ListaConceitos lc = new ListaConceitos(this);
		lc.setDef(definicoes);
		lc.setCaminho(path);
//		display.setCurrent(lc);
//		display.setCurrent(new Alert("Arquivo", path, null, null));
	}

	private void carregaDefinicoes() {
		definicoes = new Definicoes();
		definicoes.carrega();
	}
	
	private void salvaDefinicoes(){
		definicoes.salvaPos();
	}

}
