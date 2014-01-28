package tagparser;

/**
 * A classe principal da análise sintática (parser).
 */

import java.io.*;

import java.util.*;

public class TagParser
{
  private static final String UTF8_INVALIDO = "Formato UTF-8 inválido";

/**
   * Leitor de dados.
   */

  private Reader reader;

  /**
   * Objeto que responde aos eventos da entrada.
   */

  private TagListener listener;

  /**
   * A tag raiz.
   */

  private String rootTag = null;

  /**
   * Indica se a origem está no formato UTF8.
   */

  private boolean isUTF8;

  /**
   * Origem dos dados.
   */

  private InputStream in;

private boolean EOF;

  /**
   * Constutor padrão.
   *
   * @param listener Objeto que responderá aos eventos da entrada.
   */

  public TagParser( TagListener listener )
  {
    this.listener = listener;
    new StringBuffer();
  }

  
  /**
   * Altera o valor do campo isUTF.
   *
   * @param flag Verdadeiro se a origem está no formato UTF8.
   */

  public void setInputUTF8Encoded( boolean flag )
  {
    isUTF8 = flag;
  }

  /**
   * Método que lê o próximo caracter.
   */

  public int nextChar()
    throws IOException
  {
    int actualValue = -1;

    int inputValue = reader.read();
    if( inputValue == -1 )
      return -1;

    // Caracter simples
    if( isUTF8 == false )
    {
      actualValue = inputValue;
    }
    else
    {
      inputValue &= 0xff;
      if      ( (inputValue & 0x80) == 0 )
      {
        actualValue = inputValue;
      }
      else if ( (inputValue & 0xF8) == 0xF0 )
      {
        actualValue = (inputValue & 0x1f)<<6;

        int nextByte = reader.read() & 0xff;
        if( (nextByte & 0xC0) != 0x80 )
          throw new IOException( UTF8_INVALIDO );
        actualValue += (nextByte & 0x3F )<<6;

        nextByte = reader.read() & 0xff;
        if( (nextByte & 0xC0) != 0x80 )
          throw new IOException( UTF8_INVALIDO );
        actualValue += (nextByte & 0x3F )<<6;

        nextByte = reader.read() & 0xff;
        if( (nextByte & 0xC0) != 0x80 )
          throw new IOException( UTF8_INVALIDO );
        actualValue += (nextByte & 0x3F );
      }
      else if ( (inputValue & 0xF0) == 0xE0 )
      {
        actualValue = (inputValue & 0x1f)<<6;

        int nextByte = reader.read() & 0xff;
        if( (nextByte & 0xC0) != 0x80 )
          throw new IOException( UTF8_INVALIDO );
        actualValue += (nextByte & 0x3F )<<6;

        nextByte = reader.read() & 0xff;
        if( (nextByte & 0xC0) != 0x80 )
          throw new IOException( UTF8_INVALIDO );
        actualValue += (nextByte & 0x3F );
      }
      else if ( (inputValue & 0xE0) == 0xC0 )
      {
        actualValue = (inputValue & 0x1f)<<6;

        int nextByte = reader.read() & 0xff;
        if( (nextByte & 0xC0) != 0x80 )
          throw new IOException( UTF8_INVALIDO );
        actualValue += (nextByte & 0x3F );
      }
    }

    return actualValue;
  }

  /**
   * Método que lê até encontrar um caracter específico.
   *
   * @param endChar O caracter que marca o final
   * @return O texto lido antes do caracter que marca o final.
   */

  private String readUntil( char endChar )
    throws IOException
  {
    StringBuffer sb = new StringBuffer();

    int ch = nextChar();
    if( ch == -1 ) {
    	EOF = true;
    	return sb.toString();
    }
    while( ch != -1)
    {
    	if(ch == endChar) break;
    	sb.append( (char) ch );
    	ch = nextChar();
    }
    if( ch != '<' && ch != '>')
      sb.append( (char) ch );

    return sb.toString();
    
  }

  /**
   * Métdo que considera caracteres especiais como caracter de espaço.
   *
   * @param c O caracter a verificar.
   * @return verdadeiro se for um caracter de espaço.
   */

  private boolean isWhitespace( char c )
  {
    if( c == ' '
    ||  c == '\t'
    ||  c == '\r'
    ||  c == '\n' )
      return true;

    return false;
  }

  /**
   * Método que extrai os atributos de uma tag
   *
   * @param data string de onde os atributos serão extraidos
   */

  private Hashtable getAttributes( String data )
  {
    Hashtable attributes = new Hashtable();

    int length = data.length();
    int i = 0;
    while( i < length )
    {
      StringBuffer attrName = new StringBuffer();

      char ch = data.charAt(i);
      while( isWhitespace( ch ) && i < length )
      {
        i++;
        if( i == length )
          break;
        ch = data.charAt(i);
      }
      if( ch == '>' || i == length )
        break;

      while( ch != '=' )
      {
        attrName.append(ch);

        i++;
        if( i == length )
          break;

        ch = data.charAt(i);
      }

      if( i == length )
        break;

      String name = attrName.toString();

      // Busca o primeiro caracter não considerado como espaço
      i++;
      ch = data.charAt(i);
      while( isWhitespace( ch ) && i < length)
      {
        i++;
        if( i == length )
          break;
        ch = data.charAt(i);
      }

      int breakOn = 0;
      if( ch == '\"' )
      {
        breakOn = 1;
      }
      else if (ch =='\'' )
      {
        breakOn = 2;
      }

      // Cria um StringBuffer vazio
      StringBuffer attrValue = new StringBuffer();
      if( breakOn == 0 )
      {
        attrValue.append( ch );
      }

      i++;
      while( i < length )
      {
        ch = data.charAt(i);
        i++;
        if      ( breakOn == 0 && isWhitespace( ch ) )
        {
          break;
        }
        else if ( breakOn == 1 && ch == '\"' )
        {
          break;
        }
        else if ( breakOn == 2 && ch == '\'' )
        {
          break;
        }
        attrValue.append( ch );
      }
      String value = attrValue.toString();
      attributes.put( name, value );
    }

    return attributes;
  }

  /**
   * Metodo que lê nome e atributos da tag.
   */

  private void getTag()
    throws IOException
  {
    boolean startTag = true,
            emptyTag = false;
    String tagName = null;
    Hashtable attributes = null;

    String data = readUntil ( '>' );

    int substringStart = 0,
        substringEnd = data.length();

    if( data.startsWith( "/" )  )
    {
      startTag = false;
      substringStart++;
    }

    if( data.endsWith( "/" ) )
    {
      emptyTag = true;
      substringEnd--;
    }

    data = data.substring( substringStart, substringEnd );
    int spaceIdx = 0;
    while( spaceIdx < data.length()
    &&     isWhitespace( data.charAt(spaceIdx) ) == false )
      spaceIdx++;

    tagName = data.substring(0,spaceIdx);

    if( spaceIdx != data.length() )
    {
      data = data.substring( spaceIdx+1 );
      attributes = getAttributes( data );
    }
    
    if( startTag ) listener.openTag( tagName, attributes);

    if( emptyTag || !startTag ) listener.closeTag( tagName );
  }

  /**
   * Method to handle the reading in and dispatching of events for plain text.
   */

  private void getText()
    throws IOException
  {
    String data = readUntil ( '<' );
    listener.handleText( data );
  }

  /**
   * Parse wrapper for InputStreams
   *
   * @param _inputReader The reader for the stream.
   */

  public void  parse ( InputStream _is )
    throws IOException
  {
    in = _is;
    InputStreamReader isr = new InputStreamReader( in );
    parse( isr );
 }

  /**
   * The main parsing loop.
   *
   * @param in The reader for the stream.
   */

  public void  parse ( Reader in )
    throws IOException
  {
    reader = in;
      while( !EOF )
      {
        getText();
        getTag();
      }
  }
}
