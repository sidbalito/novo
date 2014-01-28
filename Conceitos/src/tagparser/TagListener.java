package tagparser;

/**
 * Interface .
 */

import java.util.*;

/**
 * Interface que responde �s ocorr�ncias de elementos
 * @author Sidnei
 *
 */
public interface TagListener
{
  /**
   * M�todo chamado na abertura de uma tag.
   *
   * @param name Nome da tag.
   * @param attributes Atributos.
   */

  public void openTag( String name, Hashtable attributes );

  /**
   * M�todo chamado para manipular texto encontrado entre a abertura e o fechamento de uma tag.
   *
   * @param text Texto encontrado entre a abertura e o fechamento da tag.
   */

  public void handleText( String text );

  /**
   * M�todo chamado no fechamento da tag.
   *
   * @param name O nome da tag.
   */

  public void closeTag( String name );
}
