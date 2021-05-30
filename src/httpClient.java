
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;


public class httpClient {

		public static void main(String[] args) 
		{
			try
			{
				InetAddress ip=InetAddress.getByName("localhost");
				Socket clientSocket=new Socket(ip,80);
				System.out.println("Connecting to the server......");
				DataInputStream input=new DataInputStream(clientSocket.getInputStream());
				DataOutputStream output=new DataOutputStream(clientSocket.getOutputStream());
				
				Scanner scanner=new Scanner(System.in);
				String connectionConfirm=input.readUTF();
				System.out.println("Server: "+ connectionConfirm);//server sends to client that the connection is initialized
				
				String askForAccount=input.readUTF();
				System.out.println("Server: "+askForAccount);//server asks client if he has an account
				
				String answer=scanner.nextLine();
				output.writeUTF(answer);//client sends answer to server
					while(true)
					{	
						String askUsername=input.readUTF();
						System.out.println("Server: "+ askUsername);
						String username=scanner.nextLine();
						output.writeUTF(username);
						
						String askPassword=input.readUTF();
						System.out.println("Server: "+ askPassword);
						String password=scanner.nextLine();
						output.writeUTF(password);
						
						String accountResponse=input.readUTF();
						System.out.println("Server: "+accountResponse);
						if(accountResponse.equals("continue")||(accountResponse.equals("Your user is added successfully")))
							break;
						else
							continue;
					}

				boolean flag=true;
				
				DataOutputStream request=new DataOutputStream(clientSocket.getOutputStream());
				
				String requestHeader=null;
				String fileName=null;
				String host="gaia.cs.umass.edu";
				while(flag==true)
				{
					System.out.println("Enter your request");
					String clientRequest=scanner.nextLine();         //example:GET text.txt http/1.1\r\n
					
					//The Request can include \r\n because we gonna delete it in next line//
					clientRequest=clientRequest.substring(0, clientRequest.length() - 4);
					String arr[]=clientRequest.split(" ");//split request into 3 strings....... 1-request header(get or post) 2-fileName 3-http version
					requestHeader=arr[0];
					fileName=arr[1];
					String httpVer=arr[2];
					File myFile = new File(fileName);
					String path=myFile.getAbsolutePath();//to get path of requested file
					if(requestHeader.equalsIgnoreCase("GET")&&(httpVer.equalsIgnoreCase("http/1.1")||(httpVer.equalsIgnoreCase("http/1.0"))))
					{
						request.writeUTF(requestHeader+" "+path+" "+httpVer);
						request.writeUTF("Host: " + host+"\\r\\n");
						request.flush();
						System.out.println("======================================");
						System.out.println("Request Sent!");
						System.out.println("======================================");
						flag=false;
							break;
					}
					else if(requestHeader.equalsIgnoreCase("POST")&&(httpVer.equalsIgnoreCase("http/1.1")||(httpVer.equalsIgnoreCase("http/1.0"))))
					{
						String fileType=Checktxt(fileName);
						if(fileType.equals("txt"))
						{
							System.out.println("Add content to your file");
							String content=scanner.nextLine();
							writeToFile(fileName,content);
						}
						request.writeUTF(requestHeader+" "+path+" "+httpVer);
						request.writeUTF("Host: " + host);
						request.flush();
						System.out.println("======================================");
						System.out.println("Request Sent!");
						System.out.println("======================================");
						flag=false;
							break;
					}
					else
					{
						System.out.println("invalid request header or http version");
							continue;
					}
				}
				
				String Version=input.readUTF();
				String Code=input.readUTF(); 
				String Date=input.readUTF();
				if (Code.equals("200 OK"))
				{					
					String fileType =Checktxt(fileName);
					if(fileType.equalsIgnoreCase("txt"))
					{	
						String fileContent=input.readUTF();
						System.out.println(fileContent);
					}
				}
				if(requestHeader.equalsIgnoreCase("get"))
				{
					System.out.print(Version);
					System.out.println(Code + "\\r\\n");
					System.out.println("Date:"+Date+"\\r\\n");
					String clientUsername=input.readUTF();
					String clientPassword=input.readUTF();
					System.out.println("Username : " + clientUsername);
					System.out.println("Password : " + clientPassword);
				}
				else if(requestHeader.equalsIgnoreCase("POST"))
				{
					System.out.println(Version);
					System.out.println("Date:"+Date);
				} 
				
				System.out.println("======================================");
				System.out.println("Response Recieved!!");
				System.out.println("======================================");
				
				String path=input.readUTF();
				printOnBrowser(path);
				
				scanner.close();
				input.close();
				output.close();
				request.close();
				clientSocket.close();
			}
			catch(IOException e)
			{
				System.out.println("Problem with server socket");
			}
		}
public static void writeToFile(String fileName,String content) throws IOException 
{ 
	try
   	{    
		BufferedWriter writer=new BufferedWriter(new FileWriter(fileName,true));//used to write to file and true to append    
	    writer.newLine();
	    writer.write(content);
	    writer.close();    
   	}
	catch(Exception e)
	{
		System.out.println(e);
	}
}
public static String Checktxt(String fileName) 
{			
	String fileNameH =fileName.substring(fileName.length()-4, fileName.length());//initializing fileNameH to last 4 characters in file name
	String fileNameJ =fileName.substring(fileName.length()-3, fileName.length());//initializing fileNameJ to last 3 characters in file name
	if(fileNameH.equalsIgnoreCase("HTML"))//if last 4 characters are html it returns HTML as type of file
	{
		return "HTML";
	}
	if(fileNameJ.equalsIgnoreCase("PNG")||fileNameJ.equalsIgnoreCase("JPG")) //if last 3 characters are PNG or JPG it returns pic as type of file
	{
		return "Pic";
	}
	else //else file type is text
	{
		return "txt";	
	}
}
public static void printOnBrowser(String URL) 
{
	try 
	{
		Runtime rTime=Runtime.getRuntime();
		String browser="C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe ";
		Process pc=rTime.exec(browser+URL);// executes two links(browser and path of file)
		pc.waitFor();
	}
	catch(IOException | InterruptedException e)
	{
		System.out.println(e);
	}
}
}