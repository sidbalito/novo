import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;

public class Persistencia {
	private static Persistencia instance;
	RecordStore rs;
	private DataOutputStream out;
	private DataInputStream in;
	private byte[] buff;
	private ByteArrayOutputStream bytesOut;
	private ByteArrayInputStream bytesIn;
	private int recNum = 1;
	public static final int PRIMEIRO = 1;
	
	void open(String nome){
		try {
			rs = RecordStore.openRecordStore(nome, true);
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	boolean exist(String nome){
		try {
			rs = RecordStore.openRecordStore(nome, false);
		} catch (RecordStoreException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}

	private DataOutputStream out() {
		bytesOut = new ByteArrayOutputStream();
		if(out == null)	out = new DataOutputStream(bytesOut);
		return out;
	}
	
	void close(){
		if(rs != null)
			try {
				if(in != null)in.close();
				if(out != null)out.close();
				rs.closeRecordStore();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	void grava(int rec){
		try {
			out().writeInt(rec);
			grava(bytesOut.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void grava(String rec){
		try {
			out().writeUTF(rec);
			grava(bytesOut.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static Persistencia getInstance() {
		if(instance == null) instance = new Persistencia();
		return instance;
	}

	public void grava(Object rec) {
		grava(rec.toString());
	}

	public void grava(byte[] rec) {
		try {
			if(rs.getNumRecords() > 0)
				rs.setRecord(recNum, rec, 0, rec.length);
			else
				rs.addRecord(rec, 0, rec.length);
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String leString() {
		try {
			le();
			if(in() != null & buff != null){
				in.reset();
				String s = in.readUTF();
				return s;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private void le() throws RecordStoreException {
		System.out.println(rs.getNumRecords());
		if(rs.getNumRecords() >= recNum) buff = rs.getRecord(recNum);
	}

	public int leInt(){
		try {
			le();
			if(buff != null) return 0;
			in.reset();
			int i = in.readInt();
			in.close();
			return i;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private DataInputStream in() {
		if(bytesIn == null) bytesIn = new ByteArrayInputStream(buff);
		if(in == null & buff != null) in = new DataInputStream(bytesIn);
		return in;
	}
	
	public void setRecNum(int num){
		if(num > 0) recNum = num;
	}
	
	public static String[] listRecordStores(){
		return RecordStore.listRecordStores();
	}

	
}
