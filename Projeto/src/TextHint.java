import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;


public class TextHint {
	private static final int MARGIN = 2;
	private String hint;
	private int textX;
	private int textY;

	public void clearHint(){
		hint= null;
	}
	
	public boolean hasHint(){
		return hint != null;
	}
	
	public void setHint(String hint, int x, int y){
		this.hint = hint;
		this.textX = x;
		this.textY = y;
	}
	
	public void drawHint(Graphics gr, Font font){
		if(hint == null) return;
		int screenWidth = gr.getClipWidth();
		int screenHeight = gr.getClipHeight();
		int width = font.stringWidth(hint) + (MARGIN << 1);
		int fontHeight = font.getHeight();
		int height = fontHeight + MARGIN;
		int x = textX;
		int y = textY-height;
		if((textX + width) > screenWidth) x = screenWidth - width;
		if(y < 0) y = textY + fontHeight;
		gr.setColor(255, 255, 0);
		gr.fillRect(x, y, width, height);
		gr.setColor(0);
		gr.drawRect(x, y, width, height);
		gr.drawString(hint, x+MARGIN, y+MARGIN, 0);
	}
}
