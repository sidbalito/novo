
public class PositionedString {
	
	private String string;
	private int x;
	private int width;
	
	public PositionedString(String s, int x, int width) {
		string = s;
		this.x = x;
		this.width = width;
	}
	
	public String toString() {
		return string;
	}
	
	public boolean isPosition(int x){
		//System.out.println(x + ": "+this.x+", "+(this.x+width));
		return x > this.x && x < (this.x+width);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

}
