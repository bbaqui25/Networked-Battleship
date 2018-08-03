import java.io.*;

import java.net.*;


import java.lang.*;


public class Attack{

	public String endresult;
	public int xcoord;
	public int ycoord;
	public boolean madehit;
	public Attack(GridButton gr[][],String message) throws IOException{
		
   
    ServerSocket serverSocket = null; 

    try { 
         serverSocket = new ServerSocket(10007); 
        } 
    catch (IOException e) 
        { 
         System.err.println("Could not listen on port: 10007."); 
         System.exit(1); 
        } 

    Socket clientSocket = null; 

    try { 
         System.out.println ("Waiting for Client");
         clientSocket = serverSocket.accept(); 
        } 
    catch (IOException e) 
        { 
         System.err.println("Accept failed."); 
         System.exit(1); 
        } 

    ObjectOutputStream out = new ObjectOutputStream(
                                     clientSocket.getOutputStream()); 
    ObjectInputStream in = new ObjectInputStream( 
                                     clientSocket.getInputStream()); 

    String m3 = null;
    String m4 = null;

    try {
         m3 = (String) in.readObject();
        }
    catch (Exception ex)
        {
         System.out.println (ex.getMessage());
        }


    System.out.println ("Server recieved point: " + m3 + " from Client");
	 String[] letters= {"A","B","C","D","E","F","G","H","I","J"};
	 String numbers[]={"1","2","3","4","5","6","7","8","9","10"};
	String x=Character.toString(m3.charAt(0));
	System.out.println(x);
	String y="";
	if(m3.length()>2){
		 y=m3.substring(1);
	}
	else{
	 y=Character.toString(m3.charAt(1));
	}
	//int x1=letters.indexOf(x);
	//int y1=numbers.indexOf(y);
	int xcount=0;
	int ycount=0;
	
	/*for(String lt:letters){
		if(lt.equals(x)==true){
			break;	
		}
		xcount++;
	}*/
	int k;
	for(k=0;k<letters.length;k++){
		if(letters[k].equals(x)==true){
			break;	
		}
		//xcount++;
	}
	ycount=Integer.parseInt(y);
	System.out.println("ycount "+ycount);
	
	xcoord=k+1;//xcount+1;	//Integer.toString(xcount);
	ycoord=ycount;	//Integer.toString(ycount);

	System.out.println(ycount+"");
	System.out.println(xcoord+" xcoord"+" "+xcount+" xcount");
	String Message="";
	for(int i=1;i<11;i++){
		for(int j=1;j<11;j++){
			if(gr[i][j].row==(xcoord)&&gr[i][j].col==(ycoord)){
				System.out.println(""+xcoord+" "+ycoord);
				if(gr[i][j].getShip()==true){
					Message="made a hit";
					gr[i][j].setToBeenHit();
					System.out.println(Message);
					madehit=true;
	
				}
				else{
					Message="made a miss";
					System.out.println(Message);
					
					madehit=false;
				}
			}
		}

	}

    m4 = Message;//"Hi";
    System.out.println ("Server sending point: " + m4 + " to Client");
    out.writeObject(m4); 
    out.flush();


    out.close(); 
    in.close(); 
    clientSocket.close(); 
    serverSocket.close(); 

	}
    public static void main(String args[]) throws IOException{/*Attack(String message) throws IOException {*/

   
    ServerSocket serverSocket = null; 

    try { 
         serverSocket = new ServerSocket(10007); 
        } 
    catch (IOException e) 
        { 
         System.err.println("Could not listen on port: 10007."); 
         System.exit(1); 
        } 

    Socket clientSocket = null; 

    try { 
         System.out.println ("Waiting for Client");
         clientSocket = serverSocket.accept(); 
        } 
    catch (IOException e) 
        { 
         System.err.println("Accept failed."); 
         System.exit(1); 
        } 

    ObjectOutputStream out = new ObjectOutputStream(
                                     clientSocket.getOutputStream()); 
    ObjectInputStream in = new ObjectInputStream( 
                                     clientSocket.getInputStream()); 

    String m3 = null;
    String m4 = null;

    try {
         m3 = (String) in.readObject();
        }
    catch (Exception ex)
        {
         System.out.println (ex.getMessage());
        }

	System.out.println("I am the server");
    System.out.println ("Server recieved point: " + m3 + " from Client");

    m4 = "Hi";
    System.out.println ("Server sending point: " + m4 + " to Client");
    out.writeObject(m4); 
    out.flush();


    out.close(); 
    in.close(); 
    clientSocket.close(); 
    serverSocket.close(); 
   } 
}