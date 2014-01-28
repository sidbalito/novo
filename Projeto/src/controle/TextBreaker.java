package controle;

public class TextBreaker {
		
	private String text;
	private int pos;
	private String hint;
	private int textLen;
	private boolean breakLine;


	public TextBreaker(String text) {
		setText(text);
	}


	public void setText(String text) {
		this.text = text;
		textLen = text.length();
	}
	

	public String nextPart() {
		if(text == null) return null;
		int startPos = pos;
		boolean isWord = false;
		breakLine = false;
		while(pos < textLen){
			char ch = Character.toLowerCase(text.charAt(pos));
			if(ch == '\n') breakLine = true;
			boolean isAlpha = (ch >= 'a') && (ch <= 'z');
			if(!isWord && pos == startPos && isAlpha) isWord = true;
			if(!isWord && isAlpha) break;
			if(isWord && !isAlpha){
				hint = text.substring(startPos, pos);
				isWord = false;
			}
			pos++;
		}
		if(breakLine && pos-startPos == 1) return "";
		if(startPos == pos) return null;
		int endPos = pos;
		if(breakLine)endPos--;
		return text.substring(startPos, endPos);
	}
	
	public String nextPart(int pos) {
		this.pos = pos;
		return nextPart();
	}


	public String getHint() {
		return hint;
	}


	public int getPos() {
		return pos;
	}


	public void setPos(int pos) {
		this.pos = pos;
	}


}
