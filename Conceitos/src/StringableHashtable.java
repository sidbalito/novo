import java.util.Hashtable;


public class StringableHashtable extends Hashtable {

	static final String STR_VAZIA = "";

	public void fromString(String origem){
		boolean open = false;
		clear();
		StringBuffer sb = null;
		String chave = StringableHashtable.STR_VAZIA;
		for(int i = 0; i < origem.length(); i++){
			char ch = origem.charAt(i);
			if(ch == '{'){
				if(open) break;
				else open = true;
				continue;
			}
			if(ch == ','){
				i++;
				put(chave, sb.toString());
				sb = null;
				continue;
			}
			if(ch == '}'){
				put(chave, sb.toString());
				open = false;
				break;
			}
			if(ch == '='){
				chave = sb.toString();
				sb = null;
				continue;
			}
			if(sb == null)sb = new StringBuffer();
			sb.append(ch);
		}
	}


}
