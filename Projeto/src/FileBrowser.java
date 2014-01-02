import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;


public class FileBrowser extends List implements CommandListener {

	private static final String UP_LEVEL = "..";
	private static final String PREFIX = "file:///";
	private String currDir;
	private Command cmdSelect = new Command("Select", Command.ITEM, 0);
	private boolean loading;/*
	private Display display;
	private Displayable displayable;//*/
	private InputStream in;
	private FileBrowserListener fbl;
	private Command cmdVoltar = new Command("Voltar", Command.ITEM, 0);

	public FileBrowser(FileBrowserListener fbl) {
		super("Arquivos", IMPLICIT);
		currDir = PREFIX;
		setCommandListener(this);
		setSelectCommand(cmdSelect);
		addCommand(cmdVoltar);
		listRoots();/*
		this.display = display;
		this.displayable = displayable;//*/
		this.fbl = fbl;
	}
	
	private void listRoots(){
		loading = true;
		deleteAll();
		currDir = PREFIX;
		Enumeration roots = FileSystemRegistry.listRoots();
		while(roots.hasMoreElements()){
			Object root = roots.nextElement();
			append(root.toString(), null);
		}
		loading = false;
	}
	
	private void open(String file) throws IOException{
		String path = currDir+file;
		if(UP_LEVEL.equals(file)){
			int len = currDir.length();
			int end = currDir.lastIndexOf('/', len-2)+1;
			if(end > 0) path = currDir.substring(0, end);
			if(PREFIX.equals(path)){
				listRoots();
				return;
			}
		}
		openPath(path);
	}
	private void openPath(String path) throws IOException{
		loading = true;
		Carregando.mostra();
		FileConnection con = (FileConnection) Connector.open(path);
		if(con.isDirectory()){
			currDir = path;
			Enumeration en = con.list();
			deleteAll();
			append(UP_LEVEL, null);
			while (en.hasMoreElements()) {
				append((String) en.nextElement(), null);				
			}
		} else if(con.exists()){
			fbl.file(path);
		}
		Carregando.mostra(this);
		loading = false;
	}

	public void commandAction(Command cmd, Displayable dsp) {
		if(cmd == cmdSelect){
			try {
				if(!loading) open(getString(getSelectedIndex()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(cmd == cmdVoltar){
			fbl.cancel();
		}
	}

	public void setPath(String string) {
		currDir = string;
		try {
			openPath(currDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
