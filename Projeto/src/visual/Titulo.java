package visual;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class Titulo {
	private static final int MARGIN = 1;
	public static final int HANDLE_HEIGHT = 10;
	private static final int TRIANGLE_SIDE = 11;
	private boolean show;
	private Vector titulo;

	public int drawTitle(Graphics gr, Font font){
		if(titulo == null) return 0;
		int width = gr.getClipWidth();
		int linhas = titulo.size();
		int y = 0;
		int height = linhas * font.getHeight() + (MARGIN << 1);
		gr.setColor(128, 128, 128);
		if(show){
			gr.fillRect(0, 0, width, height);
			gr.setColor(255, 255, 255);
			for(int i = 0; i < titulo.size(); i++)
				gr.drawString(titulo.elementAt(i).toString(), width >> 1, y, 
					Graphics.TOP|Graphics.HCENTER);
		} else {
			gr.fillRect(0, 0, width, HANDLE_HEIGHT);
			gr.setColor(255, 255, 255);
			int midWidth = width >> 1;
			gr.fillTriangle(midWidth, HANDLE_HEIGHT, midWidth-TRIANGLE_SIDE, 
					0, midWidth+TRIANGLE_SIDE, 0);
		}
		return HANDLE_HEIGHT;
		
	}

	public Titulo(Vector titulo) {
		this.titulo = titulo;
	}
}
