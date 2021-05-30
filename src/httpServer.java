
import java.io.IOException;
import java.util.regex.Pattern;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class httpServer {
		 static ServerSocket serverSocket;
		 public static void main(String[] args)
		 {
			try 
			{
				serverSocket=new ServerSocket(80);//server listens to port 80
				System.out.println("Server is booted up");
				
				while(true)
				{
					Socket clientSocket = serverSocket.accept();//connection starts
					System.out.println("A new client["+clientSocket+"] is connected to the server");
					Thread client = new ClientConnection(clientSocket);//call threads clientConnection function
					client.start();
				}
			}
			catch(Exception e)
			{
				System.out.println("Problem with client socket");
			}
		 }
		 static class ClientConnection extends Thread
		 {
			 	final private Socket clientSocket;
			 	public ClientConnection(Socket clientSocket)
			 	{
			 		this.clientSocket = clientSocket;
			 	}
			 	public void run()
			 	{
			 	try
			 	{
			 		DataInputStream input=new DataInputStream(clientSocket.getInputStream());
			 		DataOutputStream output=new DataOutputStream(clientSocket.getOutputStream());
			 		Scanner scanner=new Scanner(System.in);
			 		ArrayList<addUser>Users=new ArrayList();//array of objects each object contains username and password
			 		addUser obj1=new addUser();
			 		addUser obj2=new addUser();
			 		obj1.username="youssef";
			 		obj1.password="bassem";
			 		Users.add(obj1);//object=account
			 		obj2.username="mohamed";
			 		obj2.password="mohey";
			 		Users.add(obj2);
			 		boolean flag=false;
			
			 		output.writeUTF("Connected");
			
					output.writeUTF("Do you have an account?");
					String answer=input.readUTF();
					String clientUsername = null,clientPassword=null;
					if(answer.equalsIgnoreCase("yes"))
					{
						while(flag==false)
						{
							output.writeUTF("Enter your username");
							clientUsername=input.readUTF();
							
							output.writeUTF("Enter your password");
							clientPassword=input.readUTF();
												
							for(int i=0;i<Users.size();i++)
							{
								if(clientUsername.equals(Users.get(i).username)&&clientPassword.equals(Users.get(i).password))
								{
									output.writeUTF("continue");
									flag=true;
									break;
								}
							}
							if(flag==false) 
							{
								output.writeUTF("Wrong username or password");
									continue;
							}
						}
					}
					else if(answer.equalsIgnoreCase("no"))
					{
						OUTER:
						while(flag==false)
						{
							addUser u=new addUser();
							output.writeUTF("Enter username");
							clientUsername=input.readUTF();
							
							output.writeUTF("Enter password");
							clientPassword=input.readUTF();
						
							for(int i=0;i<Users.size();i++)
							{
								if(clientUsername.equals(Users.get(i).username))
								{
									output.writeUTF("your username is already taken");
									continue OUTER;//return to outer(before while (flag==false)) to repeat this iteration correctly
								}
							}
							if(flag==false)
							{
								u.username=clientUsername;
								u.password=clientPassword;
								flag=true;
								Users.add(u);
								output.writeUTF("Your user is added successfully");
							}
						}
					}
					String httpRequest = input.readUTF();//read http request from client
					String arr[]=httpRequest.split(" ",3);// split http request into 3 strings 
					String requestHeader=arr[0];//request header could be get or post
					String path=arr[1];// path of requested file
					String pathArr[]=path.split(Pattern.quote("\\"));//pattern quote to split path when it finds '\' 
					String fileName=pathArr[pathArr.length-1];//initializing file name to last index in path array as file name is always last index
					File tmpDir = new File(fileName); //PUT our filename in temp//
					String Code; //200 OK  or 400 Error//
					boolean exists = tmpDir.exists(); //Checks weather the file exists or not//
					if (exists == true) 
					{
						Code = "200 OK";
					}
					else 
					{
						Code = "400 error";
					}
					String httpVer=arr[2];
					System.out.println(requestHeader+" "+path);
					String fileContent=null;
					//If file exists we check what it's Type//
					//Checktxt() function checks the type of file//
					if (exists == true) {
						if("html".equalsIgnoreCase(Checktxt(fileName)))
						{
							fileContent=readfile(path);
						}
						if("pic".equalsIgnoreCase(Checktxt(fileName))) 
						{
							//readImage(fileName);
						}
						else 
						{
							fileContent=readfile(path);
						}
					}
					output.writeUTF(httpVer+" "); //Return the HTTP version//
					output.writeUTF(Code); //Return the status//
					String Date=getTimeStamp(); //GetTime//
					output.writeUTF(Date); //Return Date//
					//Checks if it is text - HTML to return the file content //
					if (exists == true) 
					{
						if("txt".equalsIgnoreCase(Checktxt(fileName)))
						{
							output.writeUTF(fileContent);
						}
						
					}
					//If we are using GET, we will return the Username & Password//
					if (requestHeader.equalsIgnoreCase("GET")) 
					{
						output.writeUTF(clientUsername);
						output.writeUTF(clientPassword);
					}
						System.out.println(path);
						output.writeUTF(path);//sends path to client to open on browser
						
						scanner.close();
						input.close();
						output.close();
						
					}
					catch (IOException e)
					{
						System.out.println("Connection with the client is terminated");
					}
			}
		}

//TimeStamp
private static String getTimeStamp() {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		    Date date = new Date();
		    return formatter.format(date);
		}

//ReadFiles
private static String readfile(String filename) throws IOException {
	String content = "";
    try
    {
        content = new String ( Files.readAllBytes( Paths.get(filename) ) );
    } 
    catch (IOException e) 
    {
        e.printStackTrace();
    }

    return content;
}
//Check if it is HTML or Picture or Other//
public static String Checktxt(String fileName) {
	
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

//READ IMAGE//
public static void readImage(String image)
{
	JFrame frame = new JFrame();//creates new frame
	ImageIcon icon = new ImageIcon(image);//initialize image icon
	JLabel label = new JLabel(icon);
	frame.add(label);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.pack();
	frame.setVisible(true);
}
}