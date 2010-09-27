/**
   Webget
   @author Kexin XIE
   @version 1.0 August/05
*/

import java.net.*;
import java.io.*;
import java.util.*;

public class WebGet{
    
    private static int bufferSize = 1024;
    
    public static void main(String[] args) throws IOException {
	
	//two command line arguments
	int plainProxyPort = 0; // plain proxy port
	String webURL =  null; // web URL
		
	//check for the number of message
	if (args.length != 2){
	    System.err.println("Invalid command line arguments.");
	    System.exit(-1);
	}
	
	//pass the first argument value to the plainProxyPort variable
	try {
	    plainProxyPort = Integer.parseInt(args[0]);
	}catch(Exception e){
	    System.err.println("Invalid command line arguments.");
	    System.exit(-1);
	}
	
	//pass the webURL information to the variable webURL
	webURL = args[1];
	
	PrintWriter out = null;
	InputStream in = null;
	Socket clientSocket = null;
	
	try {
            // Connect to the process listening on
            // port 34567 on this host (localhost)
            clientSocket = new Socket("localhost", plainProxyPort);
	    out = new PrintWriter(clientSocket.getOutputStream(), true);
	    in = clientSocket.getInputStream();
        } catch (Exception e) {
            System.err.println("Unable to connect to server.");
	    System.exit(-1);
        }
	
	String webServerName = findWebServerName(webURL);
	String fileName = findFileName(webURL);
	out.println(webServerName);
	out.println(fileName);
	
	//buffer of the bytes form plain proxy server
	byte [] buffer = new byte [bufferSize];
	int readStatus;
	while((readStatus = in.read(buffer, 0, bufferSize)) != -1){
	    System.out.write(buffer, 0, readStatus);
	}
	out.close();
	clientSocket.close();
    }
    
    /**
       find the web server name by given URL
       assume the url is in correct format
       @param url the string of url
       @return web server name string
    */
    private static String findWebServerName(String url){
	StringTokenizer st = new StringTokenizer(url, "/");
	st.nextToken();
	return st.nextToken();
    }
    
    /**
       find the file name by given URL
       assume the url is in correct format
       @param url the string of URL
       @return file name string in given URL
    */
    private static String findFileName(String url){
	String [] urlArray = new String [2];
	//split the url into two parts between "//"
	urlArray = url.split("//");
	//find the string after "//"
	String urlRear = urlArray[1];
	//find the index of the first exist '/' charater
	int index = urlRear.indexOf('/');
	//return the string after and include of the '/' charater
	return urlRear.substring(index);
    }
}