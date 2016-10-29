package ssiemens.ss16.netzwerke;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient1 {
    public static final String host ="www.heise.org";
	
	public static void main(String[] args){
		TCPClient1 client = new TCPClient1();
		client.doRequest();
	}
	
	
	public void doRequest(){
		try(Socket s=new Socket(host, 80);
				BufferedWriter toServer = 
						new BufferedWriter(
								new OutputStreamWriter(
										s.getOutputStream()));
				BufferedReader fromServer =
						new BufferedReader(
								new InputStreamReader(
										s.getInputStream()))){
			toServer.write("GET / HTTP/1.1\r\n");
			toServer.write("Host: "+host+"\r\n");
			toServer.write("\r\n");
			toServer.flush();


			for(String line=fromServer.readLine();
					line !=null /*&& line.length()>0*/;
					line = fromServer.readLine()){
				System.out.println(line);
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
