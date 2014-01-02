import java.util.Vector;


public class Paginador {

	private int numPagina;
	private int linhasPorPagina;
	private Vector paginas;

	public void storePosition(int linha, int posicao){
		numPagina = linha / linhasPorPagina;
		int indice = linha % linhasPorPagina;
		int[] linhas = getLinhas(numPagina);
		linhas[indice] = posicao;
		
	}

	private int[] getLinhas(int numPagina) {
		int[] pagina = null;
		if(numPagina < paginas.size()){
			pagina = (int[]) paginas.elementAt(numPagina);
		} else {
			pagina = new int[linhasPorPagina];
			paginas.addElement(pagina);
		}
		return pagina;
	}
}
