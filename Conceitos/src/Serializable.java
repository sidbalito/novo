import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public interface Serializable {
	public void writeObject(OutputStream out)    throws IOException;
	public void readObject(InputStream in) 	     throws IOException, ClassNotFoundException;
	public void readObjectNoData();
}
