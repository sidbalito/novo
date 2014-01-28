import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import tagparser.TagListener;


public class Verbete implements TagListener {
	private static final String TAG_END_OPEN = "</";
	private static final String TAG_CLOSE = ">";
	private static final String TAG_OPEN = "<";
	private static Verbete verbete;
	private static Vector verbetes;
	private String enunciado;
	private String definicao;
	private int caixa;
	private long data;
	int modo;
	static final int CONCEITO = 2;
	static final int DEFINICAO = 1;
	private static final int CAIXA = 3;
	private static final int DATA = 4;
	private static final int DOCUMENTO = 0;
	
	public Verbete() {
		verbete = this;
	}
	public String getEnunciado() {
		return enunciado;
	}
	public void setEnunciado(String enunciado) {
		this.enunciado = enunciado;
	}
	public String getDefinicao() {
		return definicao;
	}
	public void setDefinicao(String definicao) {
		this.definicao = definicao;
	}
	public int getCaixa() {
		return caixa;
	}
	public void setCaixa(int caixa) {
		this.caixa = caixa;
	}
	public long getData() {
		return data;
	}
	public void setData(long data) {
		this.data = data;
	}
	
	public String toString(){
		tagged(Verbetes.STR_CONCEITO, enunciado);
		tagged(Verbetes.STR_DEFINICAO, definicao);
		tagged(Verbetes.STR_CAIXA, ""+caixa);
		tagged(Verbetes.STR_DATA, ""+data);
		return "";
	}
	
	private void tagged(String tag, String conteudo) {
		StringBuffer sb = new StringBuffer(TAG_OPEN);
		sb.append(tag).append(TAG_CLOSE);
		sb.append(conteudo).append(TAG_END_OPEN).append(TAG_CLOSE);
	}

	public void handleText(String text) {
		//if(verbete == null)verbete = new Verbete();
		int modo = 0;
		switch(modo){
		case CONCEITO:
			if(verbetes == null)verbetes = new Vector();
			verbete = new Verbete();
			verbetes.addElement(verbete);
			verbete.enunciado = text;
			break;
		case DEFINICAO: 
			verbete.definicao = text;
			break;
		case CAIXA:
			verbete.caixa = Integer.parseInt(text);
			break;
		case DATA:
			verbete.data = Long.parseLong(text);
			break;
		}
		modo = DOCUMENTO;
	}
	public void closeTag(String name) {
		
	}
	
	public void openTag(String name, Hashtable attributes) {
		if(name.equals(Verbetes.STR_DEFINICAO)) modo = Verbete.DEFINICAO;
		if(name.equals(Verbetes.STR_CONCEITO)) modo = Verbete.CONCEITO;
	}
	public static TagListener tagParser() {
		return new Verbete();
	}
	public static Vector getVerbetes() {
		if(verbetes == null)verbetes = new Vector();
		return verbetes;
	}
}
