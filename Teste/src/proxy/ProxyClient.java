package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

public class ProxyClient {
	private String host = "192.168.1.1", port ="80";
	private String protocol = "datagram";
	private Connection conn;
	
	public InputStream connect(String target) throws IOException{
		String url = protocol +"://"+ host 
				+ ":" + port;
		conn = (DatagramConnection)Connector.open(url, Connector.READ_WRITE);
		System.out.println(url + "\n-> " + conn + (conn instanceof Datagram));
		if(conn instanceof StreamConnection){
			StreamConnection streamConn = (StreamConnection) conn;
			OutputStream os = streamConn.openOutputStream();
			os.write(getRequestHeader(target).getBytes());
			InputStream is = streamConn.openInputStream();
			return is;
		} else if(conn instanceof DatagramConnection){
			DatagramConnection datagramConn = (DatagramConnection) conn;
			Datagram datagram = datagramConn.newDatagram(512);
			datagram.write(getRequestHeader(target).getBytes());
			return (InputStream) datagram;
		}
		return null;
	}
	
	private String getRequestHeader(String targetURL) {
		StringBuffer buf = new StringBuffer(512);
		buf.append("GET http://" + targetURL + " HTTP/1.1\n");
		
		buf.append("User-Agent: MIDP2.0\n");
		buf.append('\n');
		
		return buf.toString();
	}

	public void config(String host, String port, String protocolo) {
		this.host = host;
		this.port = port;
		this.protocol = protocolo;
	}
}
