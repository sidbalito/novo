package visual;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class Titulo {
	private static final int MARGIN = 1;
	public static final int HANDLE_HEIGHT = 10;
	private static final int TRIANGLE_SIDE = (int) ((HANDLE_HEIGHT-2)*.866);
	protected static final long ATRASO = 100;
	private boolean show;
	private Vector titulo;
	Runnable runnable;
	Canvas canvas;
	private int titleBottom;

	public int drawTitle(Graphics gr, Font font){
		if(titulo == null) return 0;
		int width = gr.getClipWidth();
		int linhas = titulo.size();
		int y = 0;
		int height = HANDLE_HEIGHT; 
		if(show)	height += linhas * font.getHeight() + (MARGIN << 1);
		gr.setColor(128, 128, 128);
		gr.fillRect(0, 0, width, height);
		if(show){
			gr.setColor(255, 255, 255);
			int fontHeight = font.getHeight();
			for(int i = 0; i < titulo.size(); i++){
				gr.drawString(titulo.elementAt(i).toString(), width >> 1, y, 
					Graphics.TOP|Graphics.HCENTER);
				y+= fontHeight;
			}
		}
		titleBottom = y;
		gr.setColor(255, 255, 255);
		int midWidth = width >> 1;
		if(show) gr.fillTriangle(midWidth, titleBottom + 1, midWidth-TRIANGLE_SIDE, 
				titleBottom + HANDLE_HEIGHT-2, midWidth+TRIANGLE_SIDE, titleBottom + HANDLE_HEIGHT-2);
		else gr.fillTriangle(midWidth, titleBottom + HANDLE_HEIGHT-2, midWidth-TRIANGLE_SIDE, 
				titleBottom + 1, midWidth+TRIANGLE_SIDE, titleBottom + 1);
		return HANDLE_HEIGHT;
		
	}

	public Titulo(Vector titulo, Canvas canvas) {
		this.titulo = titulo;
		this.canvas = canvas;
	}
	
	public void solicitaExibir(){
		new Thread(new Runnable() {
			
			public void run() {
				try {
					runnable = this;
					Thread.sleep(ATRASO);
					if(runnable != this) return;
					show = !show;
					canvas.repaint();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void cancelaExibir(){
		if(!show)runnable = null;
	}

	public void atPosition(int x, int y) {
		if(y > titleBottom && y < titleBottom+HANDLE_HEIGHT){
			solicitaExibir();
		}
	}
}
