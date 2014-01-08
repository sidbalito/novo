import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import tagparser.TagParser;
import visual.TextHint;
import visual.Titulo;
import controle.Paginador;


public class MultiStrings extends Canvas implements CommandListener, FileBrowserListener{

	private static final char NEW_LINE = '\n';
	private static final String STR_TEXTOS = "textos";
	private static final String STR_CONCEITOS = "conceitos";
	private static final String STR_BOOKMARKS = "bookmarks";
	private static final long MIN_TIME = 500; 
	private int height;
	private int width;
	private int primeiraLinha, ultimaLinha;
	private int y1;
	private int y2;
	private boolean lastLine;
	private Vector strLines = new Vector();
	private int fontHeight = 1;
	private int linha;
	private int screenLine;
	private Graphics gr;
	private Font font;
	private String hint;
	TextHint currHint = new TextHint();
	Runnable hideHintRunner, showHintRunner;
	private int pos;
	private int textLen;
	private boolean released;
	private int x;
	private Command cmdImportar = new Command("Importar", Command.ITEM, 0);
	private Command cmdAbrir = new Command("Abrir", Command.ITEM, 0);
	private Command cmdSair = new Command("Sair", Command.ITEM, 0);
	private Command cmdTextos = new Command("Não Lidos", Command.ITEM, 0);
	private Command cmdLidos = new Command("Lidos", Command.ITEM, 0);
	private Command cmdBookmarks = new Command("Favoritos", Command.ITEM, 0);
	private MIDlet midlet;
	private InputStream in;
	private Display display;
	private Vector bookmarks = new Vector();
	private boolean estrutrado;
	private Command cmdPalvras = new Command("Palavras", Command.ITEM, 0);
	private boolean dragged;
	private boolean fileAdded;
	private List lista;
	static Vector textos = new Vector(), lidos = new Vector();
	public static Hashtable dicionarioGlobal = new Hashtable();
	public Texto currTexto;
	private int numLines;
	private Paginador paginador = new Paginador();
	private String text;
	private Titulo titulo;
	private boolean breakLine;
	private boolean doBreak; 

	public MultiStrings(MIDlet midlet) {
		loadConceitos();
		loadTextos();
		loadBookmarks();
		//System.out.println("Criando...");
		/*
		long inicio = System.currentTimeMillis();
		strings[0] = "Teste1 ";
		for(int i = 1; i < strings.length; i++){
			strings[i] = "teste"+(i+1)+" ";
		}
		long tempo = System.currentTimeMillis() - inicio;//*/
		//System.out.println("Tempo decorrido: "+tempo+" segundos.");
		this.midlet = midlet;
		addCommand(cmdTextos);
		setCommandListener(this);
		display = Display.getDisplay(midlet);
		criaListaTextos();
		 //System.out.println(conceitos);
	}


	private void criaListaTextos() {
		lista = new List("Textos", List.IMPLICIT);
		final Command cmdSelecionar = new Command("Selecionar", Command.ITEM, 0);
		final Command cmdExcluir = new Command("Excluir", Command.ITEM, 0);
		final Command cmdVoltar = new Command("Voltar", Command.ITEM, 0);
		final Displayable caller = this;
		
		lista.setSelectCommand(cmdSelecionar);
		lista.addCommand(cmdExcluir);
		lista.addCommand(cmdAbrir);
		lista.addCommand(cmdImportar);
		lista.addCommand(cmdBookmarks);
		lista.addCommand(cmdSair);
		lista.addCommand(cmdVoltar);
		final FileBrowserListener fbl = this;
		lista.setCommandListener(new CommandListener() {
			boolean modoExcluir; 
			public void commandAction(Command cmd, Displayable dsp) {
				if(cmd == cmdVoltar){
					display.setCurrent(caller);
				} else if(cmd == cmdSelecionar){
					if(modoExcluir){
						int selected = lista.getSelectedIndex();
						lista.delete(selected);
						textos.removeElementAt(selected);
					} else {
						showText(lista.getSelectedIndex());
						display.setCurrent(caller);
					}
				} else if(cmd == cmdExcluir){
					modoExcluir = !modoExcluir;
					if(modoExcluir) lista.setTitle("Textos - Excluir");
					else lista.setTitle("Textos");
				} else if(cmd == cmdAbrir){
					estrutrado = false;
					display.setCurrent(new FileBrowser(fbl));
				} else if(cmd == cmdBookmarks) {
					listBookmarks(lista);
				} else if (cmd == cmdImportar) {
					importa();
				} else if(cmd == cmdSair){
					sair();
				}
			}

		});
	}


	protected void paint(Graphics graphics) {
		if(currTexto == null && textos.size() > 0 && display != null) listaTextos(MultiStrings.textos);
		gr = graphics;
		width = gr.getClipWidth();
		height = gr.getClipHeight();
		numLines = height / fontHeight;
		paginador.setLinhasPorPagina(numLines);
		font = gr.getFont();
		fontHeight = font.getHeight();
		strLines = new Vector();
		strLines.addElement(new Vector());
		if(dragged){
			int deltaY = y2 - y1;
			int linhas = deltaY/fontHeight;
			if(-deltaY > fontHeight && !lastLine){
				primeiraLinha -= linhas;
			} else if(deltaY > fontHeight){
				primeiraLinha -= linhas;
			}
			deltaY = 0;
			y1 = y2;
		}
		//TODO Primeira linha
		if(primeiraLinha < 0) primeiraLinha = 0;
		else if(primeiraLinha >=  paginador.getUltimaLinha()) primeiraLinha = paginador.getUltimaLinha();
//		else if(primeiraLinha >=  lines.size()) primeiraLinha = lines.size()-1;
		//System.out.println("Primeira: "+primeiraLinha);
		//
		pos = paginador.getPosicao(primeiraLinha);//getLine(primeiraLinha);
		x = 0;
		int strWidth = 0;
		lastLine = false;
		screenLine = 0;
		linha = primeiraLinha;
		String s = null;
		int y = 0;
		gr.setColor(255, 255, 255);
		gr.fillRect(0, 0, width, height);
		gr.setColor(0);
		if(currTexto != null) text = currTexto.getTexto();
		y = Titulo.HANDLE_HEIGHT;
		while((y+fontHeight) < height){
			int currPos = pos;
			s = nextPart();
			if(s == null) {
				lastLine = true;
				break;
			}
			strWidth = font.stringWidth(s);
			if(doBreak||(x+strWidth) > width){
				//System.out.println(linha);
				y+=fontHeight;
				linha++;
				screenLine++; 
				if(linha > ultimaLinha) ultimaLinha = linha;
				x = 0;
				strLines.addElement(new Vector());
				//TODO AddLine
				//addLine(currPos);
				paginador.storePosition(linha, currPos);
			}
			doBreak = breakLine; 
			addHint();
			if((y+fontHeight) > height) break; 
			gr.drawString(s, x, y, 0);
			x+=strWidth;
		}
		//System.out.println("Ultima linha: "+pos +" de " + textLen);
		//if(currHint.hasHint()) drawHint();
		if(titulo != null){
			y = titulo.drawTitle(gr, font);
		}
		if(currHint.hasHint()) currHint.drawHint(graphics, font);
		//System.out.println(lines);
	}


	private PositionedString getPositionedString(int x, int y){
		
		int yPos = y;// + titulo.HANDLE_HEIGHT;
		int linha = (yPos / fontHeight);
		System.out.println("Linha: "+linha);
		if(linha >= strLines.size()) return null;
		Vector pStrings = (Vector) strLines.elementAt(linha);
		//System.out.println("Linha: "+pStrings +" - " + linha + " de "+strLines.size());
		for(int i = 0; i < pStrings.size(); i++){
			PositionedString ps = ((PositionedString) pStrings.elementAt(i));
			if (ps.isPosition(x)) return ps;
		}
		return null;
	}


	protected void pointerPressed(final int x, final int y) {
		final int posy = y - Titulo.HANDLE_HEIGHT;
		
		if(!titulo.atPosition(x, y) && !currHint.hasHint())new Thread(new Runnable() {
			
			public void run() {
				try {
					released = false;
					showHintRunner = this;
					//System.out.println("Show solictado: "+this);
					Thread.sleep(MIN_TIME);
					//System.out.println("Show timeout: "+this);
					//System.out.println("ShowHintRunner: "+showHintRunner);
					if(showHintRunner != this) return;
					if(!released){
						PositionedString ps = getPositionedString(x, posy);
						if(ps != null){	
							int linha = posy/fontHeight;
							System.out.println();
							int top = linha * fontHeight + Titulo.HANDLE_HEIGHT;
							int left = ps.getX();
							currHint.setHint(ps.toString(), left, top);
							repaint();
							 new Thread(new Runnable() {		

									public void run() {
										try {
											hideHintRunner = this;
											Thread.sleep(5000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										} finally{
											if(hideHintRunner != this) return;
											currHint.clearHint();
											repaint();
										}
									}
								}).start();
							 }
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		else {
			currHint.clearHint();
			repaint();
		}
		System.currentTimeMillis();
		this.y1 = posy;
	}

	protected void pointerReleased(int x, int y) {
		dragged = false;
		released = true;
		showHintRunner = null;
		titulo.cancelaExibir();
	}
	
	 protected void pointerDragged(int x, int y) {
		currHint.clearHint();
		 dragged = true;
		 y2 = y;
		 repaint();
	 }
	 
	 
 
	 
	 private String nextPart() {
			if(currTexto == null) return null;
			int startPos = pos;
			boolean isWord = false;
			textLen = text.length();
			breakLine = false;
			while(pos < textLen){
				char ch = Character.toLowerCase(text.charAt(pos));
				if(ch == '\n') breakLine = true;
				boolean isAlpha = (ch >= 'a') && (ch <= 'z');
				if(!isWord && pos == startPos && isAlpha) isWord = true;
				if(!isWord && isAlpha) break;
				if(isWord && !isAlpha){
					hint = text.substring(startPos, pos);
					isWord = false;
				}
				pos++;
			}
			if(breakLine && pos-startPos == 1) return "";
			if(startPos == pos) return null;
			int endPos = pos;
			if(breakLine)endPos--;
			return text.substring(startPos, endPos);
		}


		private void addHint() {
			Vector ps = (Vector) strLines.elementAt(screenLine);
			//System.out.println(conceitos);
			String s = null;
			if(hint != null) s = (String) dicionarioGlobal.get(hint.toLowerCase());
			if(s == null) return;
			PositionedString curHint = new PositionedString(s, x, font.stringWidth(hint));
			ps.addElement(curHint);
		}


		public void commandAction(Command cmd, Displayable dsp) {
			if(display == null) display = Display.getDisplay(midlet);
			if(cmd == cmdSair){
				sair();
			} else if(cmd == cmdPalvras){
			} else if(cmd == cmdAbrir){
				estrutrado = false;
				display.setCurrent(new FileBrowser(this));
			} else if(cmd == cmdBookmarks) {
				listBookmarks(this);
			} else if (cmd == cmdImportar) {
				importa();
			} else if(cmd == cmdLidos){
				listaTextos(lidos);
			} else if (cmd == cmdTextos){
				listaTextos(MultiStrings.textos);
			}
		}


		private void importa() {
			estrutrado = true;
			display.setCurrent(new FileBrowser(this));
		}


		private void listBookmarks(final Displayable caller) {
			final List lstBookmarks = new List("Atalhos", List.IMPLICIT);
			final Command cmdSelecionar = new Command("Selecionar", Command.ITEM, 0);
			final Command cmdExcluir = new Command("Excluir", Command.ITEM, 0);
			final Command cmdVoltar = new Command("Voltar", Command.ITEM, 0);
			//System.out.println("Atalhos: "+bookmarks);
			for(int i = 0; i < bookmarks.size(); i++){
				Object element = bookmarks.elementAt(i);
				String s = element.toString();
				if(element != null)lstBookmarks.append(s, null);
				
			}
			lstBookmarks.setSelectCommand(cmdSelecionar);
			lstBookmarks.addCommand(cmdExcluir);
			lstBookmarks.addCommand(cmdVoltar);
			final FileBrowserListener fbl = this;
			//final FileBrowser fb = new FileBrowser(this);
			lstBookmarks.setCommandListener(new CommandListener() {
				boolean modoExcluir; 
				public void commandAction(Command cmd, Displayable dsp) {
					if(cmd == cmdVoltar){
						display.setCurrent(caller);
					} else if(cmd == cmdSelecionar){
						if(modoExcluir){
							int selected = lstBookmarks.getSelectedIndex();
							lstBookmarks.delete(selected);
							bookmarks.removeElementAt(selected);
						} else {
							FileBrowser fb = new FileBrowser(fbl);
							estrutrado = false;
							fb.setPath(lstBookmarks.getString(lstBookmarks.getSelectedIndex()));
							display.setCurrent(fb);
						}
					} else if(cmd == cmdExcluir){
						modoExcluir = !modoExcluir;
						if(modoExcluir) lstBookmarks.setTitle("Atalhos - Excluir");
						else lstBookmarks.setTitle("Atalhos");
					}
				}

			});
			display.setCurrent(lstBookmarks);
		}


		private void sair() {
			saveConceitos();
			saveTextos();
			saveBookmarks();
			midlet.notifyDestroyed();
		}


		private void listaTextos(Vector vTextos) {
			Vector textos = vTextos;
			lista.deleteAll();
			for(int i = 0; i < textos.size(); i++){
				Object element = textos.elementAt(i);
				if(element instanceof Texto){
					String s = ((Texto) element).getTitulo();
					if(s != null)lista.append(s, null);
				}
				
			}
			if(currTexto != null) {
				int currIndex = textos.indexOf(currTexto);
				if(currIndex > 0 & currIndex < textos.size()) lista.setSelectedIndex(currIndex, true);
			}
			//System.out.println(display);
			display.setCurrent(lista);
		}

		private void saveTextos() {
			try {
				RecordStore rs = RecordStore.openRecordStore(STR_TEXTOS, true);
				StringBuffer xmlTextos = new StringBuffer();
				for(int i = 0; i < textos.size(); i++){
					Object txt = textos.elementAt(i);
					if(txt instanceof Texto){
						xmlTextos.append(((Texto)txt).toXML());
					}
				}
				byte[] bytes = xmlTextos.toString().getBytes();
				if(rs.getNumRecords() <= 0)rs.addRecord(bytes, 0, bytes.length);
				else rs.setRecord(1, bytes, 0, bytes.length);
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}


		private void loadTextos(){
			if(textos == null || textos.size() > 0) return;
			try {
				RecordStore rs = RecordStore.openRecordStore(STR_TEXTOS, true);
				byte[] xmlTextos = null;
				
				if(rs.getNumRecords() > 0){
					xmlTextos = rs.getRecord(1);
					//System.out.println(new String(xmlTextos));
					if(xmlTextos != null) new TagParser(new TaggedWords()).parse(xmlTextos);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		private void loadBookmarks(){
			try {
				RecordStore rs = RecordStore.openRecordStore(STR_BOOKMARKS, true);
				int numRecs = rs.getNumRecords();
				if(numRecs > 0){
					for(int i = 1; i <= numRecs; i++){
						bookmarks.addElement(new String(rs.getRecord(i)));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		public void addBookmark(String path){
			if(bookmarks.contains(path)) return;
			bookmarks.addElement(path);
		}

		private void loadConceitos(){
			try {
				RecordStore rs = RecordStore.openRecordStore(STR_CONCEITOS, true);
				byte[] xmlConceitos = null;
				
				if(rs.getNumRecords() > 0){
					xmlConceitos = rs.getRecord(1);
					if(xmlConceitos != null)new TagParser(new TaggedWords()).parse(xmlConceitos);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		private void saveConceitos() {
			try {
				RecordStore rs = RecordStore.openRecordStore(STR_CONCEITOS, true);
				String xmlConceitos = xml(dicionarioGlobal);
				if(rs.getNumRecords() <= 0)rs.addRecord(xmlConceitos.getBytes(), 0, xmlConceitos.length());
				else rs.setRecord(1, xmlConceitos.getBytes(), 0, xmlConceitos.length());
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}
		private void saveBookmarks() {
			try {
				RecordStore rs = RecordStore.openRecordStore(STR_BOOKMARKS, true);
				int numRecs = rs.getNumRecords();
				for(int i = 0; i < bookmarks.size(); i++){
					byte[] bytes = bookmarks.elementAt(i).toString().getBytes();
					if(numRecs > i) rs.setRecord(i+1, bytes, 0, bytes.length);
					else rs.addRecord(bytes, 0, bytes.length);
				}
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}


		private String xml(Hashtable table) {
			StringBuffer sb = new StringBuffer();
			Enumeration keys = table.keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = table.get(key);
				sb.append("<conceito>").append(key).append("</conceito>");
				sb.append("<definicao>").append(value).append("</definicao>");
			}
			return sb.toString();
		}


		private void showText(int selectedIndex) {
			if(selectedIndex >= textos.size()) return;
			currTexto = (Texto)textos.elementAt(selectedIndex);
			if(currTexto == null || currTexto.getTexto() == null) return;
			setTitle(currTexto.getTitulo());
			textLen = currTexto.getTexto().length();
			System.out.println("Tamanho: "+textLen);
			new Vector();
			pos = 0;
			primeiraLinha = 0;
			//TODO AddLine
			//addLine(0);
			paginador.storePosition(0, 0);
		}

		public void setTitle(String titulo) {
			text = titulo;
			Vector strings = new Vector();
			String s = null;
			StringBuffer sb = new StringBuffer();
			int strWidth = 0;
			pos = 0;
			do{
				s = nextPart();
				if(s == null) {
					lastLine = true;
					break;
				}
				strWidth = font.stringWidth(s);
				int y = 0;
				if((x+strWidth) > width){
					x = 0;
					strings.addElement(sb.toString());
					sb.setLength(0);
				}
				//addHint();
				if((y+fontHeight) > height) break; 
				sb.append(s);
				x+=strWidth;
			}while(s != null);
			strings.addElement(sb.toString());
			this.titulo = new Titulo(strings, this);
		}


		public void file(String path) {
			display.setCurrent(new Carregando());
			TaggedWords tw = new TaggedWords();
			TagParser tp = new TagParser(tw);
			try {
				FileConnection con = (FileConnection) Connector.open(path);
				in = con.openDataInputStream();
				int barra = path.lastIndexOf('/')+1;
				int ponto = path.lastIndexOf('.')+1;
				tw.setFileName(path.substring(barra));
				addBookmark(path.substring(0, barra));
				String ext = path.substring(ponto).toLowerCase();
				if("xml".equals(ext)) estrutrado = true;
				if(estrutrado)tp.parse(in);
				else naoEstruturado(in);
				fileAdded  = true;
		} catch (IOException e) {
				e.printStackTrace();
			}
			//System.out.println(conceitos);
		
		}


		private void naoEstruturado(InputStream in) {
			DataInputStream dis = new DataInputStream(in);
			try {
				Texto texto = new Texto();
				int len = dis.available();
				byte[] bytes = new byte[len];
				dis.read(bytes);
				String s = new String(bytes);
				int inicio = 0, fim = 0;
				while(inicio < len){
					fim = s.indexOf(NEW_LINE, inicio);
					//System.out.println("tamano do titulo: "+(fim-inicio));
					if(fim-inicio > 1){
						texto.setTitulo(s.substring(inicio, fim));
						break;
					}
					inicio++;
				}
				texto.setTexto(s.substring(fim));
				//System.out.println("Adicionando: "+texto);
				textos.addElement(texto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}


		public void cancel() {
			if(!fileAdded){
				display.setCurrent(lista);
				return;
			}
			fileAdded = false;
			listaTextos(MultiStrings.textos);
		}

}