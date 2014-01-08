package controle;
import java.util.Vector;


public class Paginador {

	private int linhasPorPagina = 1;
	private Vector paginas = new Vector();
	private int ultimaLinha;

	public void storePosition(int linha, int posicao){
		//System.out.println("Linha: "+linha+" posição: "+posicao);
		int numPagina = linha / linhasPorPagina;
		int indice = linha % linhasPorPagina;
		if(linha > ultimaLinha)ultimaLinha = linha;
		int[] linhas = getLinhas(numPagina);
		linhas[indice] = posicao;
		
	}

	private int[] getLinhas(int numPagina) {
		int[] pagina = null;
		if(numPagina >= paginas.size()){
			paginas.addElement(new int[linhasPorPagina]);
		}
		pagina = (int[]) paginas.elementAt(numPagina);
		return pagina;
	}
	
	public int getPosicao(int linha){
		int numPagina = linha / linhasPorPagina;
		int indice = linha % linhasPorPagina;
		int[] linhas = getLinhas(numPagina);
		return linhas[indice];
	}

	public int getUltimaLinha() {
		return ultimaLinha;
	}

	public void setLinhasPorPagina(int numLines) {
		linhasPorPagina = numLines;
	}
}
