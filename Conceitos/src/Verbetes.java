import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import tagparser.TagListener;
import tagparser.TagParser;


public class Verbetes extends List implements CommandListener {

	private static final int DOCUMENTO = 0;
	static final String STR_CONCEITO = "conceito"; 
	static final String STR_DEFINICAO = "definicao"; //definição
	private static final int ZERO = 0;
	private static final int NUM_CAIXAS = 5;
	private static final int MS_DIA = 86400000;
	private static final String VERBETES = "verbetes";
	private static final String TITULO = "Verbetes";
	private static final long[] DESLOCAMENTO = {0, 1, 3, 5, 7};
	private Command cmdVoltar = new Command("Voltar", Command.BACK, 0);
	private Command cmdGuardar = new Command("Guardar", Command.OK, 0);
	private Command cmdVer = new Command("Ver", Command.ITEM, 0);
	private MIDlet midlet;
	private Display display;
	private Displayable anterior;
	private Persistencia persistencia = Persistencia.getInstance();
	private Hashtable verbetes = new Hashtable();
//TODO apagar	private StringableHashtable verbetes =  new StringableHashtable();
	private String path;
	private Hashtable caixas = new Hashtable();
	private Hashtable datas = new Hashtable();
	private Long hoje;
	private Long[] deslocados = new Long[NUM_CAIXAS];
	private boolean listado;
	private Verbete verbete;
	private String conceito;
	private Vector palavras = new Vector();
	private Vector termos;
	static final String STR_DATA = "data";
	static final String STR_CAIXA = "caixa";

	public Verbetes(MIDlet midlet) {
		super(TITULO, EXCLUSIVE);
		hoje = new Long(zeroHoras(System.currentTimeMillis()));
		mostraData(hoje.longValue());
		setCommandListener(this);
		this.midlet = midlet;
		display = Display.getDisplay(midlet);
		addCommand(cmdGuardar);
		addCommand(cmdVer);
		addCommand(cmdVoltar);
	}
	
	public void exibir(Displayable dsp){
		anterior = dsp;
		preenche();
		display.setCurrent(this);
	}

	private void preenche() {
		carrega(VERBETES);
		carrega(hoje());
		//TODO Vetor de verbetes
		if(verbetes.size() == 0) importa();
	}
	
	private void importa() {
			//TODO Vetor de verbetes
			TagParser tagParser = new TagParser(Verbete.tagParser());
			termos = Verbete.getVerbetes();
			verbetes = new Hashtable();
			for(int i = 0; i < termos.size(); i++){
				Verbete verbete = (Verbete) termos.elementAt(i);
				verbetes.put(verbete.getEnunciado(), verbete.getDefinicao());
			}
			try {
				InputStream in = arquivo();
				if(in == null) return;
				tagParser.parse(new InputStreamReader(in, "UTF-8"));
				//TODO Vetor de verbetes
				if(verbetes.size() > 0){
					salva();
					preenche();
				}
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
	

	private void carrega(String name) {
//TODO Adaptar
		int caixa = 0;
		persistencia.open(name);
		persistencia.setRecNum(Persistencia.PRIMEIRO);
		String s = persistencia.leString();
		System.out.println("Carregando: "+s);
		char ch = (char) -1;
		if(s.length() > 0){
			ch = s.charAt(0);
			if(ch != '<'){
				caixa = ch-'0';
				s = s.substring(1);
			}
			long data = System.currentTimeMillis();
			if(caixa > 0){
				System.out.println(name);
				data = Long.parseLong(name);
			}
			StringableHashtable verbetes = new StringableHashtable();
			verbetes.fromString(s);
			adiciona(verbetes, caixa, zeroHoras(data));
		}
	}

	private void adiciona(StringableHashtable verbetes, int numCaixa, long data) {
		Long longData = new Long(data);
		Integer caixa = new Integer(numCaixa);
		Enumeration keys = verbetes.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object elemenet = verbetes.get(key);
			//TODO Vetor de verbetes
			this.verbetes.put(key, elemenet);
			append((String) key, null);
			caixas.put(key, caixa);
			datas.put(key, longData);
		}
	}

	public void commandAction(Command cmd, Displayable dsp) {
		System.out.println(getSelectedIndex());
		if(cmd == cmdVoltar) display.setCurrent(anterior);
		if(cmd == cmdVer) mostra();
		if(cmd == cmdGuardar) guardar();
	}

	private void guardar() {
		String verbete = verbete();
		delete(getSelectedIndex());
		int caixa = ((Integer) this.caixas.get(verbete)).intValue();
		Long longData = desloca(hoje, caixa); 
		datas.put(verbete, longData);
	}

	private void salva() {
		StringableHashtable[] caixas = new StringableHashtable[NUM_CAIXAS];
		int numCaixa = 0;
		Integer caixa = null;
		//TODO Vetor de verbetes
		Enumeration keys = verbetes.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object elemenet = verbetes.get(key);
			caixa = ((Integer) this.caixas.get(key));
			if(caixa != null) numCaixa = caixa.intValue();
			else numCaixa = 0;
			if(caixas[numCaixa] == null) caixas[numCaixa] = new StringableHashtable();
			caixas[numCaixa].put(key, elemenet);
			verbetes.remove(key);
		}
		System.out.println("Itens restantes: "+verbetes);
		for(int i = 0; i < caixas.length; i++)
			if(caixas[i] != null)armazena(caixas[i], i);
	}

	private void armazena(StringableHashtable caixa, int numCaixa) {
		String nome = VERBETES;
		String s = caixa.toString();
		//System.out.println("Arazenando: "+s);
		if(numCaixa > 0) {
			s = numCaixa+s;
			nome = data(desloca(zeroHoras(System.currentTimeMillis()), DESLOCAMENTO[numCaixa]));
		}
		persistencia.open(nome);
		System.out.println("armazenando: "+s);
		persistencia.grava(s);
		persistencia.close();
		
	}

	private void mostra() {
		//TODO Vetor de verbetes
		String verbete = verbete();
		Aviso.aviso("tadução", "A tradução de "+verbete+" é "+verbetes.get(verbete).toString());
		delete(getSelectedIndex());
		preenche();
	}

	private String verbete() {
		return getString(getSelectedIndex());
	}

	private String hoje() {
		return data(System.currentTimeMillis()); 
	}
	
	private String data(long data){
		return new StringBuffer().append(data).toString();
	}
	
	private Long desloca(Long data, int numCaixa){
		if(deslocados[numCaixa] == null){
			long numDias = DESLOCAMENTO[numCaixa];
			deslocados[numCaixa] = new Long(desloca(data.longValue(), numDias));
		}
		//TODO
		mostraData(deslocados[numCaixa].longValue());
		return deslocados[numCaixa];
	}
	private long desloca(long data, long numDias){
		return data + MS_DIA * numDias;
	}
	
	private long zeroHoras(long data){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(data));
		cal.set(Calendar.MILLISECOND, ZERO);
		cal.set(Calendar.SECOND, ZERO);
		cal.set(Calendar.MINUTE, ZERO);
		cal.set(Calendar.HOUR, ZERO);
		return cal.getTime().getTime();
	}
	
	private void mostraData(long data){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(data));
		int dia = cal.get(Calendar.DAY_OF_MONTH);
		int mes = cal.get(Calendar.MONTH)+1;
		int ano = cal.get(Calendar.YEAR);
		System.out.println(dia+"/"+mes+"/"+ano);
	}

}
