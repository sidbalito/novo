import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.midlet.MIDlet;


public class ListaConceitos extends Form implements CommandListener, Alternativas {

	private static final String CONCEITOS = "Conceitos";
	private String conceito = "<conceito>";
	private String caminho = "";
	private ChoiceGroup definicao = new ChoiceGroup("", ChoiceGroup.EXCLUSIVE);
	private Command cmdSair = new Command("Sair", Command.EXIT, 0);
	private Command cmdConfirma = new Command("Confirma", Command.ITEM, 0);
	private Command cmdArquivo = new Command("Arquivo", Command.ITEM, 1);
	private Command cmdImporta = new Command("Importa", Command.ITEM, 1);
	private Command cmdReiniciar = new Command("Reiniciar", Command.ITEM, 1);
	private Command cmdFinalizar = new Command("Finalizar", Command.ITEM, 1);
	private Command cmdVerbetes = new Command("Verbetes", Command.ITEM, 1);
	private MIDlet midlet;
	private Display display;
	private Definicoes def;
	private int erros;
	private int verbetes;
	private long tempo, intervalo;
	private static String[] definicoes;
	private static boolean sorteia = true;
	public ListaConceitos(MIDlet midlet) {
		super(CONCEITOS);
		this.midlet = midlet;
		display = Display.getDisplay(midlet);
		setItemStateListener(new ItemStateListener() {
			
			public void itemStateChanged(Item item) {
				verifica();
			}
		});
		carregaDefinicao();
		setCommandListener(this);
		addCommand(cmdSair);
		addCommand(cmdConfirma);
		addCommand(cmdImporta);
		addCommand(cmdReiniciar);
		addCommand(cmdFinalizar);
		addCommand(cmdVerbetes);
	}

	public void carregaDefinicao() {
		if(def == null) return;
		if(sorteia) {
			def.sorteia(this);
			sorteia = false;
		}
	}

	public void setAlternativas(String enunciado, String[] alternativas) {
		//TODO
		//TODO if(alternativas != null) for(int i = 0; i < alternativas.length; i++)System.out.println(alternativas[i]);
		//TODO else System.out.println(alternativas);
		deleteAll();
		definicoes = alternativas;
		definicao = new ChoiceGroup("Significa: ", ChoiceGroup.MULTIPLE, alternativas, null);
		conceito = enunciado;
		append(conceito);
		append(definicao);
		intervalo = System.currentTimeMillis();
	}

	public void commandAction(Command cmd, Displayable dsp) {
		if(cmd == cmdSair) {
			def.salvaPos();
			midlet.notifyDestroyed();
		}
		if(cmd == cmdConfirma) verifica();
		if(cmd == cmdArquivo) caminho();
		if(cmd == cmdReiniciar) {
			def.inicia();
			inicia();
		}
		if(cmd == cmdFinalizar) finaliza();
		if(cmd == cmdVerbetes) new Verbetes(midlet).exibir(this);
		if(cmd == cmdImporta) {
			def.importaDefinicoes();
			carregaDefinicao();
		}
	}

	private void finaliza() {
		if(verbetes == 0) return;
		//TODO System.out.println(tempo);
		long media = ((int)tempo/1000)/verbetes;
		String s = verbetes+" verbetes\n"+(100*erros/verbetes)+"% de erros\n"+"Tempo médio: "+media+"s";
		Aviso.aviso("Contagem", s);
		inicia();
	}

	private void inicia() {
		verbetes = 0;
		erros = 0;
		tempo = 0;
	}

	private void caminho() {
		display.setCurrent(new Alert("Arquivo", caminho, null, null));
	}

	private void verifica() {
		intervalo =System.currentTimeMillis()-intervalo;
		tempo += intervalo;
		//TODO System.out.println("Tempo: "+intervalo/1000);
		verbetes++;
		if(!definicao.isSelected(def.getCorreto())){
			erros++;
			Aviso.aviso("Resposta errada", "A traducao de "+def.getConceito()+" é "+def.getDefinicao());
		}
		sorteia = true;
		carregaDefinicao();
	}

	public void setDef(Definicoes def) {
		this.def = def;
		carregaDefinicao();
	}

	public String getCaminho() {
		return caminho;
	}

	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}

}
