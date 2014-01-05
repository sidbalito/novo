
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;


public class Carregando extends Canvas {

	private static final String CARREGANDO = "CARREGANDO...";
	private static final int CINZA = 0x808080;
	private static final int MARGIN = 20;
	private static final int AZUL = 0xFF;
	private static final int PRETO = 0;
	private static Display display;

	protected void paint(Graphics gr) {
		Font font = gr.getFont();
		int screenWidth = gr.getClipWidth();
		int screeHeight = gr.getClipHeight();
		int strWidth = font.stringWidth(CARREGANDO);
		int winWidth = strWidth + (MARGIN << 1); 
		int fontHeight = font.getHeight();
		int winHeight = fontHeight << 2;
		if(winHeight > screeHeight) winHeight = screeHeight;
		if(winWidth > screenWidth) winWidth = screenWidth;
		int top = ((screeHeight-winHeight)>>1)-fontHeight;
		int left = (screenWidth-winWidth)>>1;
		//gr.clipRect(left, top, winWidth, winHeight);
		gr.setColor(CINZA);
		gr.fillRect(left, top, winWidth, winHeight);
		gr.setColor(AZUL);
		gr.fillRect(left, top, winWidth, fontHeight);
		gr.drawRect(left, top, winWidth, winHeight);
		gr.setColor(PRETO);
		gr.drawString(CARREGANDO, screenWidth >> 1, (screeHeight >> 1), Graphics.BASELINE|Graphics.HCENTER);
	}

	public static void setDisplay(Display display) {
		Carregando.display = display;
	}

	public static void mostra() {
		mostra(new Carregando());
	}

	public static void mostra(Displayable displayable) {
		if(display == null || displayable ==null) return;
		display.setCurrent(displayable);
		
	}

}
