import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import proxy.ProxyClient;


public class Connections extends MIDlet {

	private TextBox tela;
	private Command cmdHttp = new Command("HTTP", Command.ITEM, 0);
	private Command cmdProxy = new Command("Proxy", Command.ITEM, 0);
	private Command cmdProtocolos = new Command("Protocolos", Command.ITEM, 0);
	private Command cmdSair = new Command("Sair", Command.ITEM, 0);
	private Command cmdOk = new Command("Ok", Command.ITEM, 0);
	private Command cmdConfig = new Command("Configurar", Command.ITEM, 0);
	private Display display;
	private ProxyClient proxy = new ProxyClient();
	private String address = "www.google.com";

	public Connections() {
		tela = new TextBox("Remoto", "", 1024, TextField.ANY);
		tela.addCommand(cmdHttp);
		tela.addCommand(cmdProxy);
		tela.addCommand(cmdProtocolos);
		tela.addCommand(cmdConfig);
		tela.addCommand(cmdSair);
		tela.setCommandListener(new CommandListener() {
			

			public void commandAction(Command cmd, Displayable arg1) {
				if(cmd == cmdHttp){
					connect(address, false);
				} else if(cmd == cmdProxy){
					connect(address, true);					
				} else if(cmd == cmdSair){
					notifyDestroyed();
				} else if (cmd == cmdConfig){
					config();
				} else if (cmd == cmdProtocolos){
					protocolos();
				} 
				
			}
		});
		display = Display.getDisplay(this);
		display.setCurrent(tela);
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		//Display.getDisplay(this).setCurrent(new Tela());
	}

	private void connect(String address, boolean useProxy) {
		try {
			InputStream is = null;
			if(useProxy){
				is = proxy.connect(address);
			} else {
				is = ((HttpConnection)Connector.open("http://"+address)).openInputStream();
			}
			int len = is.available();
			byte[] bytes = new byte[len];
			is.read(bytes);
			String s = new String(bytes);
			System.out.println(s);
			tela.setString(s);
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer(e.getClass().getName());
			sb.append(":\n");
			sb.append(e.getMessage());
			tela.setString(sb.toString());
		}
	}
	
	private void protocolos(){
		final Hashtable tabela = new Hashtable();
		tabela.put("Comm", "comm:BT16");
		tabela.put("Datagram", "datagram://localhost:80");
		tabela.put("Http", "http://www.google.com");
		tabela.put("Https", "https://www.google.com");
		tabela.put("Socket", "socket://www.google.com:80");
		final List lista = new List("Protocolos", List.IMPLICIT);
		lista.setSelectCommand(cmdOk);
		lista.addCommand(cmdSair);
		lista.setCommandListener(new CommandListener() {
			
			public void commandAction(Command arg0, Displayable arg1) {
				if(arg0 == cmdOk){
					String url = (String) tabela.get(lista.getString(lista.getSelectedIndex()));
					try {
						Connector.open(url);
					} catch (Exception e) {
						StringBuffer sb = new StringBuffer(url).append("\n").append(e.getClass().getName());
						sb.append(":\n");
						sb.append(e.getMessage());
						display.setCurrent(alerta(sb.toString()));
					}
					display.setCurrent(alerta("Sucesso."));
				} else if(arg0 == cmdSair){
					notifyDestroyed();
				}
			}

			private Alert alerta(String string) {
				Alert alerta = new Alert("", string, null, null);
				alerta.setTimeout(Alert.FOREVER);
				return alerta;
			}
		});
		for(Enumeration keys = tabela.keys(); keys.hasMoreElements(); ){
			lista.append((String) keys.nextElement(), null);
		}
		
		display.setCurrent(lista);
	}
	
	private void config(){
		final TextField host = new TextField("Host", "webfilter.trtsp.jus.br", 1024, TextField.ANY);
		final TextField port = new TextField("Porta", "3128", 5, TextField.NUMERIC);
		final TextField target = new TextField("Alvo", "www.google.com", 1024, TextField.ANY);
		final TextField protocolo = new TextField("Protocolo", "http", 1024, TextField.ANY);
		Form form = new Form("Configurações");
		form.addCommand(cmdOk);
		form.append(target);
		form.append(host);
		form.append(port);
		form.append(protocolo);
		form.setCommandListener(new CommandListener() {
			
			public void commandAction(Command cmd, Displayable arg1) {
				if(cmd == cmdOk){
					proxy.config(host.getString(), port.getString(), protocolo.getString());
					address = target.getString();
					display.setCurrent(tela);
				}
			}
		});
		display.setCurrent(form);
	}

}
