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
	private int inc;

	protected void paint(Graphics gr) {
		gr.setColor(255, 255, 255);
		width = getWidth();
		height = getHeight();
		int fontHeight = gr.getFont().getHeight();
		gr.fillRect(0, 0, width, height);
		drawCircle(gr, (height >> 1)+fontHeight);
		gr.setColor(0);
		gr.drawString("CARREGANDO...", width >> 1, (height+fontHeight) >> 1, Graphics.BASELINE|Graphics.HCENTER);
		new Thread(new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(ATRASO);
					inc += 5;
					repaint();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
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
			gr.fillArc((width >> 1)-raio, top-raio, diametro, diametro, i*30+inc, 25);
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

}
