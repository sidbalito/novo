import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;


public class Tela extends Canvas {

	private static final int BRANCO = 0xFFFFFF;
	private static final int PRETO = 0;
	private static final int AZUL = 0xFF;
	private double graus = 30;

	protected void paint(Graphics gr) {
		circulo(gr);
		
	}

	private void circulo(Graphics gr) {
		int width = gr.getClipWidth();
		int height = gr.getClipHeight();
		int midWidth = width >> 1;
		int midHeight = height >> 1;
		int raio = 15;
		if(midHeight < midWidth)raio = midHeight;
		gr.setColor(BRANCO);
		gr.fillRect(0, 0, width, height);
		gr.setColor(AZUL);
		gr.fillArc(midWidth-raio, midHeight-raio, raio << 1, raio << 1, 0, 360);
		gr.setColor(BRANCO);

		for(int i = 0; i < 360; i+=40){
			double angulo = Math.toRadians(graus+i);
			double adjacente = Math.sin(angulo);
			double oposto = Math.cos(angulo);
			gr.drawLine(midWidth, midHeight, (int)(midWidth+raio*oposto), (int)(midHeight+raio*adjacente));
		}
		raio = raio - (raio >> 1);
		gr.fillArc(midWidth-raio, midHeight-raio, raio << 1, raio << 1, 0, 360);
		
		new Thread(new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(100);
					graus+=5;
					repaint();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}

}
