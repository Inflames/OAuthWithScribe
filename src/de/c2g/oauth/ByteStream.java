package de.c2g.oauth;

/** (c) by Adjutant of Tino */

import java.io.*;

import org.scribe.model.Token;

import android.content.Context;
import android.util.Log;

public class ByteStream  {
	
	
	public static Object read () {
		File fi;
		
		Object token = null;
		FileInputStream fis;
		fi = new File("/sdcard/data/ByteStream1.txt");
		
		byte[] b = new byte[(int)fi.length()];
		//ByteArrayInputStream bis = new ByteArrayInputStream(b);
		
		try {
			fis = new FileInputStream(fi);
			ObjectInputStream in = new ObjectInputStream(fis);
			token = in.readObject();
			fis.close();
			in.close();
			
			
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return token;
		
				
		}
	
	
	public static void write(byte[] cs) {
		

		
		File fo;
		FileOutputStream fos;		
		fo = new File("/sdcard/data/ByteStream1.txt");
		try {
					
			fos = new FileOutputStream(fo);
			fos.write(cs);
			fos.close();
		}
		catch(FileNotFoundException e) {}
		catch(IOException e) {}	
	}
	

}
