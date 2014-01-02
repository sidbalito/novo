import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.microedition.location.AddressInfo;

import tagparser.TagListener;


public class TaggedWords implements TagListener {
	private final static String STR_TEXTO = "texto";
	private final static String STR_TITULO = "titulo";
	private static final String STR_CONCEITO = "conceito";
	private static final String STR_DEFINICAO = "definicao";
	private static final int CONCEITO = 1;
	private static final int DEFINICAO = 2;
	private static final int TEXTO = 3;
	private static final int TITULO = 4;
	private int modo;
	private Texto texto = new Texto();
	private Hashtable conceitos = MultiStrings.conceitos;
	private Object conceito;
	private Vector textos = MultiStrings.textos;
	private Stack modos = new Stack();
	private String fileName;

	public void openTag(String name, Hashtable attributes) {
		modos.push(new Integer(modo));
		if(STR_CONCEITO.equals(name)){
			modo = CONCEITO;
		} else if(STR_DEFINICAO.equals(name)){
			modo = DEFINICAO;
		} else if(STR_TEXTO.equals(name)){
			modo = TEXTO;
		} else if(STR_TITULO.equals(name)){
			modo = TITULO;
		}
	}

	public void handleText(String text) {
		if(text == null || text == "") return;
		switch (modo) {
		case CONCEITO:
			conceito = text.toLowerCase();
		break;
		case DEFINICAO:
			conceitos.put(conceito, text);
		break;
		case TEXTO:
			texto.addTexto(text);
		break;
		case TITULO:
			texto.setTitulo(text);
		break;
		}
	}

	private void addText() {
		//System.out.println("Adding: "+texto);
		//if(textos.contains(texto)) System.out.println("Igual");
		if(textos.contains(texto)) return;
		//System.out.println("Ok");
		textos.addElement(texto);
		texto = new Texto();
	}

	public void closeTag(String name) {
		Object obj = null;
		if(!modos.empty()) obj= modos.pop();
		if(obj instanceof Integer) modo = ((Integer)obj).intValue();
		if(name.equals(STR_TEXTO)){
			addText();
		}
	}

	public void setFileName(String name) {
		fileName = name;
	}

}
