import java.io.DataInputStream;


public interface FileBrowserListener {

	void file(String path);
	
	void cancel();

}
