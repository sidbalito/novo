import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;


public class Tela extends Canvas {
	
	private int x1, y1;
	private int x2, y2;
	private boolean pressed;
	protected void paint(Graphics gr) {
		int width = gr.getClipWidth();
		int height = gr.getClipHeight();
		gr.setColor(255, 255, 255);
		gr.fillRect(0, 0, width, height);
		if(pressed){
			gr.setColor(0);
			gr.drawLine(x1, y1, x2, y2);
		}
	}
	
	protected void pointerPressed(int x, int y) {
		pressed = true;
		this.x1 = x;
		this.y1 = y;
	}

	protected void pointerReleased(int x, int y) {
		pressed = false;
	}
	
	 protected void pointerDragged(int x, int y) {
		 x2 = x;
		 y2 = y;
		 repaint();
	 }

}
