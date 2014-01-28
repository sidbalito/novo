import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import tagparser.TagListener;
import tagparser.TagParser;


public class Definicoes implements TagListener{

	private static final int BLOCK_SIZE = 5;
	private static final int NUM_ALTERNATIVAS = 3;
	private static final String DEFINICOES = "definicoes";
	private static final int DOCUMENTO = 0;
	private static final String STR_CONCEITO = "conceito";
	private static final String STR_DEFINICAO = "definicao";
	private static final int DEFINICAO = 1;
	private static final int CONCEITO = 2;
	private static final String POSICAO = "posicao";
	private int modo;
	private StringableHashtable definicoes =  new StringableHashtable();
	private String conceito, enunciado, alternativaCorreta;
	private int contador = -1;
	private int item = BLOCK_SIZE;
	private String[] vConceitos = new String[BLOCK_SIZE];
	//private Vector vDefinicoes = new Vector();
	private int correto;
	private String[] strings;
	private Persistencia persistencia = Persistencia.getInstance();
	private String path;
	private boolean listado;
	private Enumeration enumeration;

	public Definicoes(StringableHashtable verbetes) {
		if(verbetes != null)definicoes = verbetes;
	}

	public Definicoes() {
		this(null);
	}

	public void openTag(String name, Hashtable attributes) {
		if(name.equals(STR_DEFINICAO)) modo = DEFINICAO;
		if(name.equals(STR_CONCEITO)) modo = CONCEITO;
	}

	public void handleText(String text) {
		listado = true;
		switch(modo){
		case CONCEITO: 
			conceito = new String(text);
			break;
		case DEFINICAO: 
			if(conceito == null) break;
			definicoes.put(conceito, text);
			//vConceitos.addElement(conceito);
			//vDefinicoes.addElement(text);
			conceito = null;
			break;
		}
		modo = DOCUMENTO;
	}

	public void closeTag(String name) {
		
	}
	
	public void sorteia(Alternativas alternativas){
		strings = new String[NUM_ALTERNATIVAS];
		//if(vConceitos.size() <= 0) strings = new String[]{STR_VAZIA,STR_VAZIA,STR_VAZIA};
		//TODO reiniciar contador
		//if(contador >= vConceitos.size()) contador = 0;
		int items = 0;
		item++;
		if(item >= vConceitos.length){
			items = lista();
			for(int i = items; i < vConceitos.length; i++) vConceitos[i] = StringableHashtable.STR_VAZIA;
		}
		
		//TODO System.out.println("item: "+item);
		//TODO redefinir sorteio
		int[] aleatorios = aleatorios(vConceitos.length);
		for(int i = 0; i < strings.length; i++) {
			strings[i] = (String) definicoes.get(vConceitos[aleatorios[i]]);
			if(strings[i] == null) strings[i] = StringableHashtable.STR_VAZIA;
		}
		//TODO redefinir enunciado
		enunciado = (String) vConceitos[aleatorios[correto]];
		alternativaCorreta = strings[correto];
		alternativas.setAlternativas(enunciado, strings);
	}
	
	private int[] aleatorios(int max) {
		Random aleatorio = new Random();
		correto = Math.abs(aleatorio.nextInt() % NUM_ALTERNATIVAS);
		int aleatorios[] = new int[NUM_ALTERNATIVAS];
		if(max < NUM_ALTERNATIVAS) return aleatorios;
		int i = 0;
		int j = -1;
		boolean altera = true;
		do{
			if(i == correto) aleatorios[i] = item;
			else {
				if(altera) {
					aleatorios[i] =  Math.abs(aleatorio.nextInt() % max);
					j = i;
				} 
				if(aleatorios[i] == item){
					altera = true;
					continue;
				}
			}
 
			j--;
			if(j < 0){
				i++;
				altera = true;
			} else altera = aleatorios[j]==aleatorios[i];
		}while(i < aleatorios.length);
		return aleatorios;
	}
	
	public boolean getCorreto(int resposta){
		return resposta == correto;
	}
	
	public int getCorreto(){
		return correto;
	}

	public String getConceito() {
		return enunciado;
	}

	public String getDefinicao() {
		return alternativaCorreta;
	}

	public void carrega() {
		persistencia.open(DEFINICOES);
		persistencia.setRecNum(Persistencia.PRIMEIRO);
		String s = persistencia.leString();
		System.out.println("Carregando: "+s);
		definicoes.fromString(s);
		persistencia.close();
		persistencia.open(POSICAO);
		contador = persistencia.leInt();
		persistencia.close();
	}

	public void salva() {
		persistencia.open(DEFINICOES);
		System.out.println("Salvando: "+definicoes);
		persistencia.grava(definicoes);
		persistencia.close();
		salvaPos();
		
	}
	
	public void importaDefinicoes() {
		TagParser tagParser = new TagParser(this);
		try {
			InputStream in = arquivo();
			if(in == null) return;
			tagParser.parse(new InputStreamReader(in, "UTF-8"));
			salva();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private InputStream arquivo() throws IOException {
		StringBuffer sb = new StringBuffer("file:///");
		Enumeration roots = FileSystemRegistry.listRoots();
		if(roots.hasMoreElements()){
			sb.append(roots.nextElement());
			sb.append("conceitos.xml");
			path = sb.toString();
			FileConnection fc = (FileConnection) Connector.open(path);
			InputStream in = null;
			if(fc.exists()){
				in  = fc.openInputStream();
			} else {
				fc.create();
			}
			fc.close();
			return in;
		}
		
		return null;
	}
	
	public int lista(){
		listado = true;
		//redefinir listagem
		int contador = 0;
		boolean pula = false;
		if(enumeration == null){
			contador = 0;
			pula = true;
			enumeration = definicoes.keys();
		} else contador  = this.contador;
		System.out.println("contador: "+this.contador);
		int i = 0;
		while(enumeration.hasMoreElements()){
			String element = (String) enumeration.nextElement();
			if(!enumeration.hasMoreElements())enumeration = definicoes.keys();
			contador++;
			if(pula &&  contador <= this.contador) continue;
			System.out.println(element);
			vConceitos[i] = element;
			i++;
			if(i >= vConceitos.length) break;
		}
		if(!enumeration.hasMoreElements()) {
			enumeration = null;
			contador = 0;
		}
		this.contador = contador;
		item = 0;
		return i;
	}

	public void salvaPos() {
		persistencia.open(POSICAO);
		persistencia.grava(contador);
		persistencia.close();
	}

	public void inicia() {
		contador = 0;
	}


}
