package ssiemens.ss16.netzwerke.basics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class TCPClient2 {
    public static final String host ="www.mmix.cs.hm.edu/mmixvd/help/mmixvd.html";
	
	public static void main(String[] args){
		TCPClient2 client = new TCPClient2();
		client.doRequest();
	}
	
	
	public void doRequest(){
		try(InputStream input = 
				new URL("http://"+host).openStream();
				BufferedReader fromServer =
						new BufferedReader(
								new InputStreamReader(
										input))){
			
			for(String line=fromServer.readLine();
					line !=null /*&& line.length()>0*/;
					line = fromServer.readLine()){
				System.out.println(line);
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	
}
