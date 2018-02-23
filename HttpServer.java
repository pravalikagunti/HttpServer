/*
 * Author:Pravalika Gunti
 * Description: program to create a multi-threaded TCP server that is able to receive an HTTP packet from a browser 
 * and implementing GET method.
 * At the end, web server will deliver a simple HTML web page with an embedded image
 * This is a server program, it can be tested by using web browser
 */

/*Importing files*/
import java.io.*;
import java.net.*;

/*Start of class*/
public class HttpServer implements Runnable{
	private Socket clientSocket;
	private String CRLF="\r\n";
	
	public HttpServer()
	{
	}
	public HttpServer(Socket s) 
	{
		this.clientSocket=s;
	}

	/*Main Method*/
	public static void main(String args[]) throws IOException
	{
        /*while compiling, this if condition is used to check argument is given or not*/	
	if(args.length==1){
			int serverPort;
			serverPort = Integer.parseInt(args[0]);
			ServerSocket  svrSocket= new ServerSocket(serverPort);
			while(true)
			{
				
				/*Accepting client request, this returns a local Socket*/
				/*to communicate with the client*/
				Socket clientSocket = svrSocket.accept();
				InetAddress clientIP = clientSocket.getInetAddress();
				System.out.println("Connected to " + clientIP.toString());
				
                                /* Constructing an object to process the HTTP request message*/
				HttpServer request_msg = new HttpServer( clientSocket );

				/*Creation of thread*/
				Thread thread = new Thread(request_msg);

				/*Thread starts*/
				thread.start();
			}
		}
		else{
			System.out.println("run server program as java filename portnumber");
		}
	}

	/*run method*/
	public void run() {
		try{

			/*Get the input streams of the socket*/
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			/*read the bytes from the input stream*/
			String str="";
			str=in.readLine();

			/*checking string length is not equal to zero*/
			if(str.length()!=0){

				/*spliting the string*/
				String[] stri=str.split(" ");

				/*first string consist of method name*/
				String cmd = stri[0];
				System.out.println("Request Type:"+cmd);

				/*second string consist of url requested for*/
				String[] fileName = stri[1].split("/");
				System.out.println("URL Requested:"+fileName[1]);
				
				processCommand(cmd, fileName[1]);

				/*third string consist of http version*/
				/*String[] strs=stri[2].split("/");
 * 				System.out.println("HTTP version: "+strs[1]);*/

				/*displaying headerlines*/
				/*String headerLine;*/
				/*int a=1;*/
				/*while((headerLine=in.readLine()).length()>0){
 * 					System.out.println("Header Line"+a+": "+headerLine);
 * 										a=a+1;
 * 														}*/
			}

			/*closing stream*/
			in.close();
			/*out.close();*/
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*method that display the file or prints not found exception in browser*/
	
	private void processCommand(String cmd, String fileName) throws IOException {
			if(cmd.compareToIgnoreCase("GET")==0) {
				boolean fileExists=true;
				
				try{

					/*pen up file, f a FileNotFound exception is thrown, then the*/
					/*file doesn't reside at the server*/
					FileInputStream fis=new FileInputStream(fileName);
					}
				
				catch(FileNotFoundException e){
					fileExists=false;
				}
				
				if (fileExists)
				{
					/*code to send the file as a stream of bytes to the client*/
					DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
					FileInputStream fis=new FileInputStream(fileName);				 
					
					/*Creating status line*/
					 String statusLine = "HTTP/1.1 200 OK" + CRLF; /* response line*/
					 
					 /* header line. Only interested in the type of object we are sending back*/
					 String contentType = "Content-type: " + contentType( fileName ) + CRLF;
					 
					 /*Entity body. HTML file with message Not Found as the text*/
					 /*String entityBody = "<HTML>" + "<HEAD><TITLE>200 OK</TITLE></HEAD>"+"</HTML>";*/
					 
					 out.writeBytes(statusLine);
					 out.writeBytes(contentType);
					 out.writeBytes(CRLF);
					 /*out.writeBytes(entityBody);*/
					 
					 byte[] b=new byte[204800000];
					 
					 for(int i=0;((i=fis.read(b))>-1);i++) 
				      {
				         out.write(b,0,i);
				      }	
					 fis.close();
				}
				else
				{
					
					/*code to send the file as a stream of bytes to the client*/
					DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
					
					/*Creating status line*/
					String statusLine = "HTTP/1.1 404 Not found" + CRLF; /* response line*/
					
					/* header line. Only interested in the type of object we are sending back*/
					String contentType = "Content-type:" + contentType( fileName ) + CRLF;
					
					/*Entity body. HTML file with message Not Found as the text*/
					String entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" +"<BODY>Not Found</BODY></HTML>";
					
					out.writeBytes(statusLine);
					out.writeBytes(contentType);
					out.writeBytes(CRLF);
					out.writeBytes(entityBody);
				
				}				
			}
		}
		
		private String contentType(String filename) {
                         if (filename.endsWith("html"))
			 {
			 return("text/html");
			 }
			 else if (filename.endsWith("gif"))
			 {
				 return("image/gif");
			 }
			 else if (filename.endsWith("jpg"))
			 {
				 return("image/jpeg");
			 }
			 else if (filename.endsWith("pdf"))
			 {
				 return("text/pdf");
			 }
			 else if (filename.endsWith("jpeg"))
			 {
				 return("image/jpg");
			 }
			 else
			 {
			 return("application/octet-stream");
			 }
		}
		
	}


