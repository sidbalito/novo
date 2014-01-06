import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;


public class Carregando extends Canvas {

	private static final int AZUL = 0xFF;
	private static final int BRANCO = 0xFFFFFF;
	protected static final long ATRASO = 100;
	private static Display display;
	private int width;
	private int height;
	private int graus;

	protected void paint(Graphics gr) {
		gr.setColor(255, 255, 255);
		width = getWidth();
		height = getHeight();
		int fontHeight = gr.getFont().getHeight();
		gr.fillRect(0, 0, width, height);
		circulo(gr, (height >> 1)+fontHeight);
		gr.setColor(0);
		gr.drawString("CARREGANDO...", width >> 1, (height+fontHeight) >> 1, Graphics.BASELINE|Graphics.HCENTER);
		new Thread(new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(ATRASO);
					graus += 5;
					repaint();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void drawCircle(Graphics gr, int top) {
		gr.setColor(AZUL);
		int raio = 30;
		top += raio;
		int diametro = raio << 1;
		for(int i = 0; i < 12; i++){
			gr.fillArc((width >> 1)-raio, top-raio, diametro, diametro, i*30+graus, 20);
		}
		raio = 15;
		diametro = raio << 1;
		gr.setColor(BRANCO);
		gr.fillArc((width >> 1)-raio, top-raio, diametro, diametro, 0, 360);
		
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
	private void circulo(Graphics gr, int top) {
		int width = gr.getClipWidth();
		int height = gr.getClipHeight();
		int midWidth = width >> 1;
		int midHeight = height >> 1;
		int raio = 15;
		if(midHeight < midWidth)raio = midHeight;
		gr.setColor(BRANCO);
		gr.fillRect(0, 0, width, height);
		gr.setColor(AZUL);
		int centroY = top+raio;
		gr.fillArc(midWidth-raio, centroY-raio, raio << 1, raio << 1, 0, 360);
		gr.setColor(BRANCO);

		for(int i = 0; i < 360; i+=40){
			double angulo = Math.toRadians(graus+i);
			double adjacente = Math.sin(angulo);
			double oposto = Math.cos(angulo);
			gr.drawLine(midWidth, top+raio, (int)(midWidth+raio*oposto), (int)(top+raio+raio*adjacente));
		}
		raio = raio - (raio >> 1);
		gr.fillArc(midWidth-raio, centroY-raio, raio << 1, raio << 1, 0, 360);
		
		new Thread(new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(100);
					graus+=5;
					repaint();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
	}

}
