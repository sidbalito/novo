import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;


public class Texto {

	private static final String STR_TEXTOS = "textos";

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((texto == null) ? 0 : texto.toString().hashCode());
		result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Texto other = (Texto) obj;
		if (hashCode() != other.hashCode())
			return false;
		return true;
	}

	private StringBuffer texto = new StringBuffer();
	private String titulo;

	public void setTitulo(String text) {
		titulo = text;
	}

	public String getTexto() {
		
		return texto.toString();
	}

	public String getTitulo() {
		return titulo;
	}
	
	public String toString() {
		return getTitulo();
	}

	public void setTexto(String text) {
		texto = new StringBuffer(text);
	}

	public void addTexto(String text) {
		texto.append(text);
	}
	
	public String toXML(){
		StringBuffer sb = new StringBuffer();
		sb.append("<texto>");
		sb.append("<titulo>").append(titulo).append("</titulo>");
		sb.append(texto).append("</texto>");
		return sb.toString();
	}
}
