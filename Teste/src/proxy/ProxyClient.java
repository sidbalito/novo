package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

public class ProxyClient {
	private String host = "10.3.100.211", port ="8080";
	private SocketConnection conn;
	
	public InputStream connect(String url) throws IOException{
		conn = (SocketConnection)Connector.open("socket://" + host 
				+ ":" + port);
		OutputStream os = conn.openOutputStream();
		os.write(getRequestHeader(url).getBytes());
		InputStream is = conn.openInputStream();
		return is;
	}
	
	private String getRequestHeader(String targetURL) {
		StringBuffer buf = new StringBuffer(512);
		buf.append("GET http://" + targetURL + " HTTP/1.1\n");
		
		buf.append("User-Agent: MIDP2.0\n");
		buf.append('\n');
		
		return buf.toString();
	}
}
