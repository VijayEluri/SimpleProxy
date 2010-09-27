/**
   plain proxy server
   @author Kexin XIE
   @version 1.0 August/05
*/

import java.net.*;
import java.io.*;
import java.lang.*;

public class PlainProxy{
    
    //the size of data buffer read from HTTP proxy server
    private static int bufferSize = 1024;
    
    public static void main (String[] args) throws IOException{
	
	// three command line arguments
	int listenPort = 0; //the port the proxy server is listen on
	String proxyServerHostName = null;//the host name of the HTTP proxy 
	//server to which the request will be passed
	int proxyServerPort = 0;//the port number of the HTTP proxy 
	//server on above host e.g. 80
	
	//check for correct number of arguments in command line
	if(args.length != 3){
	    System.err.println("Invalid command line arguments.");
	    System.exit(-1);
	}
	
	//store the command line arguments into corresponding variables
	//test for numeric types
	try{
	    listenPort = Integer.parseInt(args[0]);
	}catch(Exception e){
	    System.err.println("Invalid command line arguments.");
	    System.exit(-1);
	}
	//store the server host name arguments in the variable
	proxyServerHostName = args[1];
	//test for numeric types
	try{
	    proxyServerPort = Integer.parseInt(args[2]);
	}catch(Exception e){
	    System.err.println("Invalid command line arguments.");
	    System.exit(-1);
	}
	
	ServerSocket serverSocket = null;
	try {
            // listen on port listenPort
            serverSocket = new ServerSocket(listenPort);
	    System.err.println("Waiting for incoming connections.");
        } catch (Exception e) {
	    //print out the error message
	    System.err.println("Unable to listen on give port.");
	    System.exit(-1);
        }
	
	while(true){
	    
	    Socket connSocket = null;
	    
	    //code copied from lecture
	    try {
		// block, waiting for a conn. request
		connSocket = serverSocket.accept();
		// At this point, we have a connection
		System.out.println("Connection accepted from " +
				   connSocket.getInetAddress().getHostName() +
				   " port " + connSocket.getPort());
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    
	    OutputStream outDown = connSocket.getOutputStream();
	    // Create a PrintWriter and BufferedReader 
	    // for interaction with our stream
	    BufferedReader inDown = new BufferedReader(
	            new InputStreamReader(connSocket.getInputStream()));
	    
	    //read the two lines from client
	    String requestLine1 = inDown.readLine();
	    String requestLine2 = inDown.readLine();
	    
	    Socket clientSocket = null;
	    
	    try {
		clientSocket = new Socket(proxyServerHostName,
					  proxyServerPort);
	    } catch (Exception e) {
		System.err.println("Unable to connect to proxy server");
		System.exit(-1);
	    }
	    
	    PrintWriter outUp = new PrintWriter(
	            clientSocket.getOutputStream(), true);
	    InputStream inUp = clientSocket.getInputStream();
	    //send the request
	    outUp.println("GET http://" + requestLine1
			  + requestLine2 + " HTTP/1.0\r\n");
	    
	    byte [] buffer = new byte [bufferSize];
	    //read the 15 characters
	    inUp.read(buffer, 0, 15);
	    String responseLine = new String (buffer, 0, 15);
	    
	    System.out.println(responseLine);
	    if(responseLine.equals("HTTP/1.0 200 OK")){
		//System.out.println(line);
		System.out.println("sending data to clients...");
		
		//skip after reach the blank line
		boolean findBlankLine = false;
		while(!findBlankLine){
		    if(inUp.read() == '\r' && 
		       inUp.read() == '\n' && 
		       inUp.read() == '\r' && 
		       inUp.read() == '\n'){
			findBlankLine = true;
		    }
		}
		
		//print the lines to client
		int readStatus;
		while((readStatus = inUp.read(buffer, 0, bufferSize)) != -1){
		    outDown.write(buffer, 0, readStatus);
		}
		
		System.out.println("all data have been sent");
		
		//close connection with the upper server
		outUp.println("\r\n");
		clientSocket.close();
	    }else{
		System.out.println("no data have been sent out");
	    }
	    
	    System.out.println("closing connection with client " + 
			       connSocket.getInetAddress().getHostName()
			       + " port " + connSocket.getPort());
	    
	    //close connection with client
	    connSocket.close();
	    inDown.close();
	    outUp.close();
	}
    }
}