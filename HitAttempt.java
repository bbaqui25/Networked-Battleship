import java.io.*;

import java.net.*;


public class HitAttempt{

	public String endresult;
	public int xcoord;
	public int ycoord;
	public boolean madehit;
	public HitAttempt(GridButton gr[][],String msg) throws IOException{
	 Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
	String message="Hello";

        try {
            socket = new Socket("127.0.0.1", 10007);
            
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection");
            System.exit(1);
        }

	String m1 = msg;//"Hi";
        String m2 = null;

	System.out.println("I am the client");
	
        System.out.println ("Sending point: " + m1 + " to Server");
	out.writeObject(m1);
        out.flush();
        System.out.println ("Send point, waiting for return value");

        try {
             m2 = (String) in.readObject();
            }
        catch (Exception ex)
            {
             System.out.println (ex.getMessage());
            }

	System.out.println("Got point: " + m2 + " from Server");

	if(m2.equals("made a miss")==true){
		 String[] letters= {"A","B","C","D","E","F","G","H","I","J"};
	 String numbers[]={"1","2","3","4","5","6","7","8","9","10"};
	int m;
	for(m=0;m<numbers.length;m++){
		if(msg.substring(1).equals(numbers[m])==true){
			break;
		}
	}
	ycoord=m+1;
	int k;
	String x=Character.toString(msg.charAt(0));
	for( k=0;k<letters.length;k++){
		if(letters[k].equals(x)==true){
			break;	
		}
		
	}
	
	xcoord=k+1;

	madehit=false;
	
	}
	else{
			 String[] letters= {"A","B","C","D","E","F","G","H","I","J"};
	 String numbers[]={"1","2","3","4","5","6","7","8","9","10"};
		
	int m;
	for(m=0;m<numbers.length;m++){
		if(msg.substring(1).equals(numbers[m])==true){
			break;
		}
	}
	ycoord=m+1;
	int k;
	String x=Character.toString(msg.charAt(0));
	for( k=0;k<letters.length;k++){
		if(letters[k].equals(x)==true){
			break;	
		}
		
	}
	
	xcoord=k+1;

	madehit=false;
	String Message="";
	for(int i=1;i<11;i++){
		for(int j=1;j<11;j++){
			if(gr[i][j].row==(xcoord)&&gr[i][j].col==ycoord){
				gr[i][j].setToBeenHit();
				madehit=true;
			}
		}

	}
	


	}

	out.close();
	in.close();
	socket.close();


	}
    public static void main(String args[]) throws IOException{/*HitAttempt(String message) throws IOException {*/
       
	}
}