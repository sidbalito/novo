import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;


public class FilePicker extends Form {
	TextField nomeArquivo = new TextField("Arquivo:", "", 255, TextField.ANY);
	Command cmdOk = new Command("Ok", Command.ITEM, 0);
	Command cmdCancelar = new Command("Cancelar", Command.ITEM, 0);
	public FilePicker() {
		super("Abrir arquivo");
		append(nomeArquivo);
		addCommand(cmdCancelar);
		addCommand(cmdOk);
	}

}
