import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;


public class ShowStrings extends Canvas {
	
	String text = "The class String includes methods for examining individual characters of " +
			"the sequence, for comparing strings, for searching strings, for extracting " +
			"substrings, and for creating a copy of a string with all characters translated to " +
			"uppercase or to lowercase." + "The class String includes methods for examining individual characters of " +
			"the sequence, for comparing strings, for searching strings, for extracting " +
			"substrings, and for creating a copy of a string with all characters translated to " +
			"uppercase or to lowercase." + "The class String includes methods for examining individual characters of " +
			"the sequence, for comparing strings, for searching strings, for extracting " +
			"substrings, and for creating a copy of a string with all characters translated to " +
			"uppercase or to lowercase." ;
	private int pos;
	private int textLen;
	private Graphics gr;
	private boolean draged;
	private int y2;
	private int y1;
	private boolean lastLine;
	private int primeiraLinha;
	private Vector lines= new Vector();
	private int width;
	private int height;
	private Font font;
	private int fontHeight;
	private int linha;
	
	
	protected void paint(Graphics graphics) {
		gr = graphics;
		if(draged){
			int deltaY = y2 - y1;
			if(deltaY < 10 && !lastLine){
				primeiraLinha++;
			} else if(deltaY > 10){
				primeiraLinha--;
			}
			//System.out.println("Drag: "+line);
		}
		if(primeiraLinha < 0) primeiraLinha = 0;
		else if(primeiraLinha >=  lines.size()) primeiraLinha = lines.size()-1;
		//System.out.println("Adjust: "+line);
		int i = getLine(primeiraLinha), x = 0, y = 0;
		width = gr.getClipWidth();
		height = gr.getClipHeight();
		gr.setColor(255, 255, 255);
		gr.fillRect(0, 0, width, height);
		gr.setColor(0);
		font = gr.getFont();
		fontHeight = font.getHeight();
		lastLine = false;
		linha = primeiraLinha;
		textLen = text.length();
		while(pos < textLen){
			String part = nextPart();
			int strWidth = font.stringWidth(part);
			if((x+strWidth) > width){
				y+=fontHeight;
				x = 0;
			}
			gr.drawString(part, x, y, 0);
			x += strWidth;
		}
	}
	
	private String nextPart() {
		int starPos = pos, wordEnd = 0;
		boolean isWord = false;
		while(pos < textLen){
			char ch = Character.toLowerCase(text.charAt(pos));
			boolean isAlpha = (ch >= 'a') && (ch <= 'z');
			if(!isWord && pos == starPos && isAlpha) isWord = true;
			if(!isWord && isAlpha) break;
			if(isWord && !isAlpha){
				addHint(text.substring(starPos, pos));
				isWord = false;
			}
			pos++;
		}
		return text.substring(starPos, pos);
	}

	private void addHint(String hint) {
		System.out.println("Hint: '"+hint+"'");
	}

	protected void pointerPressed(int x, int y) {
		this.y1 = y;
	}

	protected void pointerReleased(int x, int y) {
		if(!draged){
			;
		}
		draged = false;
	}
	
	 protected void pointerDragged(int x, int y) {
		 draged = true;
		 y2 = y;
		 repaint();
	 }
		private void addLine(int i) {
			int size = lines.size();
			if(size > 0){
				if(getLine(size)>i) {
					//System.out.println("Cancela adição de linha.");
					return;
				}
			}
			lines.addElement(new Integer(i));
		}
		
		private int getLine(int i){
			if(i >= lines.size())return 0;
			Object obj = lines.elementAt(i);
			if(obj == null) return 0;
			int valor = ((Integer)obj).intValue();
			//System.out.println("getLine: "+valor);
			return valor;
		}
}
