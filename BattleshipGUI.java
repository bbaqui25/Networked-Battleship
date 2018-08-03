
//import oracle.jvm.hotspot.jfr.JFR;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.text.DefaultCaret;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.event.*;
import java.lang.*;
import java.lang.Object;
import java.io.*;
import javax.imageio.ImageIO;
import java.net.*;
import java.util.Random;
import java.lang.Thread;


//
// Left board is ours, Right board is opponent's
//

public class BattleshipGUI extends Thread{
    private JTextField textField;
    private JTextArea textArea;
    private JScrollPane scroll;
    private DefaultCaret caret;
    public GridButton primary_grid[][];
    public GridButton tracking_grid[][];

    public int currentShipIndex = -1;
    public int spacesLeft = 17;
    public boolean setToHorizontal = true;
    public boolean canPlace = false;
    private boolean shipCheck = false;
    
    private boolean is_Server;
    private boolean is_Client;
    public boolean myTurn;
    public boolean allMyShipsSunk;
    
    public int hitCount;	//Counts number of hits
    public int missCount;	//counts number of misses

    public boolean getCanPlace() {
        return canPlace;
    }

    public boolean getShipCheck(){
        return shipCheck;
    }

    public void setShipCheck(boolean newVal) {
        shipCheck = newVal;
    }

    public void disableButtons(int x, int y,int size) {
        primary_grid[x][y].setEnabled(false);
    }
    public boolean checkEachShip(ShipIndices[] shpind) {
    	for(int w=0;w<shpind.length;w++) {
    		int rw=shpind[w].xVal;
    		int cl=shpind[w].yVal;
    		if(primary_grid[rw][cl].getHit()==false) {
    			return false;
    		}
    	}
    	return true;
    }
    public boolean[] checkShipArray(Ship[] ships) {
    	boolean[] sunkShips=new boolean[ships.length];
    	for(int w=0;w<ships.length;w++) {
    			if(checkEachShip(ships[w].shipindices)==true) {
    				ships[w].setToHasSunk();
    				sunkShips[w]=true;
    			}
    			else {
    				sunkShips[w]=false;
    			}
    	}
    	return sunkShips;
    }

    static int threadCount;	//a counter representing the number of players/BattleshipGUI created

    private static final int gridWidth = 900;
    private static final int gridHeight = 700;


    public static void main(String[] args) {
        new BattleshipGUI();
    }

    public BattleshipGUI() {



        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("Network Battlefield");

                //
                // Panel used to add ships
                //

                JPanel addShipsPanel = new JPanel();

                //
                // Create array of ships to be added to panel
                //
                String[] AircraftfilesH={"batt1.gif","batt2.gif","batt3.gif","batt4.gif","batt5.gif"};	//Horizontal layout
                String[] BattleshipfilesH={"batt1.gif","batt2.gif","batt4.gif","batt5.gif"};
                String[] DestroyerfilesH= {"batt1.gif","batt3.gif","batt5.gif"};
                String[] SubmarinefilesH= {"batt1.gif","batt3.gif","batt5.gif"};             
                String[] PatrolBoatfilesH= {"batt1.gif","batt5.gif"};
                
                String[] AircraftfilesV={"batt6.gif","batt7.gif","batt8.gif","batt9.gif","batt10.gif"};	//Horizontal layout
                String[] BattleshipfilesV={"batt6.gif","batt7.gif","batt9.gif","batt10.gif"};
                String[] DestroyerfilesV= {"batt6.gif","batt8.gif","batt10.gif"};
                String[] SubmarinefilesV= {"batt6.gif","batt8.gif","batt10.gif"};             
                String[] PatrolBoatfilesV= {"batt6.gif","batt10.gif"};
                
                Ship shipArray[] = new Ship[5];
                Ship ac = new Ship("Aircraft Carrier",5);
                Ship bs = new Ship("Battleship",4);
                Ship d = new Ship("Destroyer",3);
                Ship s = new Ship("Submarine",3);
                Ship pb = new Ship("Patrol Boat",2);

                shipArray[0] = ac;
                shipArray[1] = bs;
                shipArray[2] = d;
                shipArray[3] = s;
                shipArray[4] = pb;

                //
                // Label used to tell user if they are placing ships
                // horizontally or vertically
                //

                addShipsPanel.setLayout(new GridLayout(7,3));
                JLabel currentWayLabel = new JLabel();


                for (int i = 0 ; i < 7 ; ++i) {
                    for (int j = 0 ; j < 3 ; ++j) {

                        //
                        // if i == 0 set up change vert/hor
                        // if i == 1 set up headers
                        // else set up ship info
                        //

                        if (i == 0) {
                            if (j == 0) {
                                JLabel header = new JLabel("Current:");
                                addShipsPanel.add(header);
                            } else if (j == 1) {
                                String currentWay = "";
                                if (setToHorizontal) {
                                    currentWay = "Horizontal";
                                } else {
                                    currentWay = "Vertical";
                                }
                                currentWayLabel.setText(currentWay);
                                addShipsPanel.add(currentWayLabel);
                            } else {
                                JButton changeHorVert = new JButton("Change");
                                addShipsPanel.add(changeHorVert);

                                changeHorVert.addActionListener(e -> {
                                    setToHorizontal = !setToHorizontal;

                                    String currentWay;
                                    if (setToHorizontal) {
                                        currentWay = "Horizontal";
                                    } else {
                                        currentWay = "Vertical";
                                    }

                                    currentWayLabel.setText(currentWay);


                                });
                            }
                        } else if (i == 1) {
                            if (j == 0) {
                                JLabel header = new JLabel("Ship Name");
                                addShipsPanel.add(header);
                            } else if (j == 1) {
                                JLabel header = new JLabel("Ship Size");
                                addShipsPanel.add(header);
                            } else {
                                JLabel header = new JLabel("Ship Added");
                                addShipsPanel.add(header);
                            }

                        } else {
                            if (j == 0) {
                                JLabel shipName = new JLabel(shipArray[i-2].getName());
                                addShipsPanel.add(shipName);
                            } else if (j == 1) {
                                JLabel shipSize = new JLabel(String.valueOf(shipArray[i-2].getSize()));
                                addShipsPanel.add(shipSize);
                            } else {
                                JButton shipAdded = new JButton(String.valueOf(shipArray[i-2].isPlace()));
                                addShipsPanel.add(shipAdded);
                                shipAdded.setPreferredSize(new Dimension(40,40));

                                int index = i-2;


                                shipAdded.addActionListener(e -> {

                                    if (!canPlace) {
                                        canPlace = true;
                                        currentShipIndex = index;
                                        shipAdded.setText("True");
                                        shipAdded.setEnabled(false);

                                    }


                                });
                            }

                        }
                    }
                }
                String pegs[]= {"batt102.gif","batt103.gif"};//miss,hit pegs
                JPanel g=new JPanel(new GridLayout(1,1));
                JButton tryAttack=new JButton();
                tryAttack.setText("tryAttack");
                tryAttack.addActionListener(new ActionListener(){
                	public void actionPerformed(ActionEvent ae) {
                		if(is_Server==true) {
                			try {
                                Attack h=new Attack(primary_grid,"I am waiting for an Attack");
                                if(h.madehit==true) {            
                                	hitCount++;
                                	File picFile=new File("./battleship/"+pegs[1]);
                                	try{
                            			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                            			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                            			ImageIcon section=new ImageIcon(buffresized);
                            			
                            			primary_grid[h.xcoord][h.ycoord].setIcon(section);
                            			}catch(IOException ie){}
                                }
                                else {
                                	missCount++;
                                	File picFile=new File("./battleship/"+pegs[0]);
                                	try{
                            			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                            			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                            			ImageIcon section=new ImageIcon(buffresized);
                            			
                            			primary_grid[h.xcoord][h.ycoord].setIcon(section);
                            			}catch(IOException ie){}
                                }
                                }catch(IOException iact) {}
                			//Check for any ship has been sunk
                			//for(int w=0;w<4;w++) {
                				//for(int z=0;z<shipsArray[w].size;z++)
                					
                			//}
                			//Code to check for the number of ships that have been sunk and if all ships of this player have been sunk
                			boolean[] mysunkships=checkShipArray(shipArray);
                			String sunkMessage="The ship(s) which have sunk on your side: "+"\n";
                			int sunkCount=0;
                			for(int n=0;n<mysunkships.length;n++) {
                				if(mysunkships[n]==true) {
                					sunkCount++;
                					sunkMessage+=shipArray[n].getName()+"\n";
                				}
                			}
                			if(sunkCount==5) {
                				allMyShipsSunk=true;
                				sunkMessage+="\n"+"All of your ships have sunk!"+"\n"+"You lose!";
                			}
                			JOptionPane.showMessageDialog(null,sunkMessage);
                			
                			//tryAttack.setEnabled(false);
                			textArea.setText("Other player's turn to press tryAttack button. You will be the client. When you press the tryAttack button, you will be able to hit some "
                					+ "\n"+"coordinates on the other player's grid");
                			is_Server=false;
                			is_Client=true;
                		}
                		else {
                			 try {
                				 String pt1=JOptionPane.showInputDialog(null,"Enter the coordinates to hit in the format letter first then number(i.e. A3)");
                                 HitAttempt a=new HitAttempt(primary_grid,pt1);
                                 if(a.madehit==true) {            
                                	 hitCount++;
                                 	File picFile=new File("./battleship/"+pegs[1]);
                                 	try{
                             			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                             			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                             			ImageIcon section=new ImageIcon(buffresized);
                             			
                             			tracking_grid[a.xcoord][a.ycoord].setIcon(section);
                             			}catch(IOException ie){}
                                 }
                                 else {
                                	 missCount++;
                                 	File picFile=new File("./battleship/"+pegs[0]);
                                 	try{
                             			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                             			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                             			ImageIcon section=new ImageIcon(buffresized);
                             			
                             			tracking_grid[a.xcoord][a.ycoord].setIcon(section);
                             			}catch(IOException ie){}
                                 }
                                 
                             }catch(IOException iact) {}
                			 //tryAttack.setEnabled(true);

                 			textArea.setText("Your turn to press tryAttack button. You will be the server."+
                 			"\n"+"When you press the tryAttack button, you will send a signal to the other player to try and attack you.");
                			 is_Server=true;
                			 is_Client=false;
                		}
                		//statistics string
                		String stats="Number of hits: "+ hitCount+"\n"+"Number of misses: "+missCount+"\n"+"Hits to misses ratio: "+ hitCount+"/"+missCount+"\n";
                		//switch sides
                		JOptionPane.showMessageDialog(null, stats+"Now the turns will be switched");
                		
                	}
                });
                g.add(tryAttack);
                
                
                frame.add(g,BorderLayout.SOUTH);
                
                frame.add(addShipsPanel,BorderLayout.CENTER);


                is_Server=false;
                is_Client=false;

                hitCount=0;
                missCount=0;
                
                allMyShipsSunk=false;
                // adding a menu...
                JMenuBar menuBar = new JMenuBar();
                this.setJMenuBar(menuBar);

                // adds the file menu and its corresponding items...
                JMenu file = new JMenu("File");
                // set up Exit menu item
                JMenuItem exit = new JMenuItem("Exit");
                file.add(exit);
                exit.addActionListener(
                        new ActionListener() {
                            // terminate application when user clicks exitItem
                            public void actionPerformed( ActionEvent event )
                            {
                                System.exit( 0 );
                            }
                        }
                ); // end call to addActionListener
                menuBar.add( file);

                JMenu stat = new JMenu("Stats");
                // set up Exit menu item
                JMenuItem statsMenu = new JMenuItem("Display my Stats");
                stat.add(statsMenu);
                statsMenu.addActionListener(
                        new ActionListener() {
                            // terminate application when user clicks exitItem
                            public void actionPerformed( ActionEvent event )
                            {
                            	String stats="Number of hits: "+ hitCount+"\n"+"Number of misses: "+missCount+"\n"+"Hits to misses ratio: "+ hitCount+"/"+missCount+"\n";
                                JOptionPane.showMessageDialog(null,stats);     
                            }
                        }
                ); // end call to addActionListener
                menuBar.add(stat);
                // create connection menu
                JMenu connection = new JMenu( "Connection" );
                // set up server menu item
                JMenuItem server = new JMenuItem( "Server" );
                connection.add(server);
                server.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent ae) {

                                                 ServerSocket serverSocket = null;
                                                 try {
                                                     serverSocket = new ServerSocket(4444);
                                                 }
                                                 catch (IOException e) {
                                                     textArea.setText("Could not listen.");
                                                 }

                                                 Socket clientSocket = null;

                                                 try {
                                                     textArea.setText("Waiting for client.");

                                                     clientSocket = serverSocket.accept();
                                                     textArea.setText("Client has connected.");
                                                     JOptionPane.showMessageDialog(frame, "Place ships");
                                                     is_Server=true;
                                                     clientSocket.close();
                                                     serverSocket.close();
                                                 }
                                                 catch (IOException e)
                                                 {
                                                     textArea.setText("Did not accept.");
                                                     System.exit(1);

                                                 }
                                                 
                                             }
                                         }
                );
                // set up client menu item
                JMenuItem client = new JMenuItem( "Client" );
                client.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        try {
                            Socket socket = new Socket("127.0.0.1", 4444);
                            textArea.setText("Connected.");
                            JOptionPane.showMessageDialog(frame, "Place ships");
                            is_Client=true;
                            socket.close();

                        } catch (UnknownHostException e) {
                            textArea.setText("Could not connect.");
                            System.exit(1);
                        } catch (IOException e) {
                            textArea.setText("Could not connect.");
                            System.exit(1);
                        }
                       
                    }

                });
                connection.add(client);
                JMenuItem howto_connect = new JMenuItem("How to connect...");
                connection.add(howto_connect);
                // implements the action listener for instructions on how to play battleship...
                class howto_connect implements ActionListener {
                    public void actionPerformed (ActionEvent e) {
                        JFrame inst_frame = new JFrame("How to Connect");
                        inst_frame.setVisible(true);
                        inst_frame.setSize(800, 400);
                        JLabel label = new JLabel("<html> First run the program and under connection click Server.<br>"  	
                        				                                                         + "Open Another window and under connection click Client. <br>" 			                                                                          
                        					                                                 + "You will see a message under the status bar that they are connected.<br>" 		                                                                           
                        					                                                             + "After all the ships are placed, click the tryAttack button on the Player1:Server side.<br>"  			
                        					                                                         + "Then go to the Player2:Client side and click the tryAttack button<br>"			
                        					                                                             + "It will prompt you to add coordinates. And then switch turns and repeat the process.</html>");
                        JPanel panel = new JPanel();
                        inst_frame.add(panel);
                        panel.add(label);
                    }
                }
                howto_connect.addActionListener(new howto_connect() );
                // set up disconnects menu item
                JMenuItem disconnect = new JMenuItem("Disconnect");
                connection.add(disconnect);
                // implements the action listener for instructions on how to play battleship...
                class disconnect implements ActionListener {
                    public void actionPerformed (ActionEvent e) {
                    		System.exit( 0 );
                    }
                }
                disconnect.addActionListener(new disconnect() );
                
                menuBar.add(connection);

                // create help menu
                JMenu help = new JMenu( "Help" );
                // set up About... menu item
                JMenuItem about = new JMenuItem( "About" );
                help.add( about );
                // implements the action listener for "about" and lists the authors...
                class about implements ActionListener {
                    public void actionPerformed (ActionEvent e) {
                        JFrame about_frame = new JFrame("About");
                        about_frame.setVisible(true);
                        about_frame.setSize(300, 300);
                        JLabel label = new JLabel("<html>Network Battleship - made by:<br>"
                                + "Chris Janowski - cjanow3<br>"
                                + "Bushra Baqui - bbaqui2<br>"
                                + "Nabeelah Khan - nkhan44<br>"
                                + "CS 342 - Programming Assignment 4<br>"
                                + "Date: 16th November 2017</html>");
                        JPanel panel = new JPanel();
                        about_frame.add(panel);
                        panel.add(label);
                    }
                }
                about.addActionListener(new about());
                // set up how to play menu item
                JMenuItem howto_play = new JMenuItem("How to play...");
                help.add(howto_play);
                // implements the action listener for instructions on how to play battleship...
                class howto_play implements ActionListener {
                    public void actionPerformed (ActionEvent e) {
                        JFrame inst_frame = new JFrame("How to: Play Battleship");
                        inst_frame.setVisible(true);
                        inst_frame.setSize(800, 400);
                        JLabel label = new JLabel("<html>The game is played on four square grids, two for each player.<br>"
                                + "On one grid the player arranges ships and records the shots by the opponent. <br>"
                                + "On the other grid the player records their own shots.<br>"
                                + "Before play begins, each player arranges a number of ships secretly on the grid for that player.<br>"
                                + "Each ship occupies a number of consecutive squares on the grid, arranged either horizontally or vertically. <br>"
                                + "The number of squares for each ship is determined by the type of the ship. <br>"
                                + "The ships cannot overlap nor can a ship go outside of the grid.<br>"
                                + "The types and numbers of ships allowed are the same for each player.<br>"
                                + "After the ships have been positioned, the game proceeds in a series of rounds. In each round, each player has a turn.<br>"
                                + "During a turn, the player selects a target square in the opponents' grid which is to be shot at. <br>"
                                + "If a ship occupies the squares, then it takes a hit.<br>"
                                + "When all of the squares of a ship have been hit, the ship is sunk.<br>"
                                + "If at the end of a round all of one player's ships have been sunk, the game ends and the other player wins.<br>"
                                + "There is a connection between client and server. <br>"
                                + "Once a connection is established, the players are allowed to place their ships on a grid. <br>"
                                + "Once the ships have been placed, the guessing of locations begins.<br>"
                                + "When a game is over, it asks the players if they want to play another game. <br>"
                                + "If they do, it allows the users to place their ships on the grid. Otherwise, disconnects. <br></html>");
                        JPanel panel = new JPanel();
                        inst_frame.add(panel);
                        panel.add(label);
                    }
                }
                howto_play.addActionListener(new howto_play() );
                menuBar.add(help);
                
                JMenu turn=new JMenu("Turn");
                JMenuItem endTurn = new JMenuItem( "End Turn" );
                turn.add( endTurn );
                // implements the action listener for endTurn 
                class endTurn implements ActionListener {
                    public void actionPerformed (ActionEvent e) {
                        JFrame endTurn_frame = new JFrame("End Turn");
                        endTurn_frame.setVisible(true);
                        endTurn_frame.setSize(300, 300);
                        JLabel label = new JLabel("<html> Ready </html>");
                        JPanel panel = new JPanel();
                        endTurn_frame.add(panel);
                        panel.add(label);
                    }
                }
                endTurn.addActionListener(new endTurn());
                menuBar.add(turn);

                frame.add(BorderLayout.NORTH, menuBar);


                JPanel east = new JPanel(new GridLayout(11,11));//TestPane();
                JPanel west = new JPanel(new GridLayout(11,11));//TestPane();

                primary_grid=new GridButton[11][11];
                tracking_grid=new GridButton[11][11];



                String[] letters= {"A","B","C","D","E","F","G","H","I","J"};

                for(int i=0;i<11;i++) {
                    for(int j=0;j<11;j++) {
                        primary_grid[i][j] = new GridButton(i,j);
                        tracking_grid[i][j] = new GridButton(i,j);

                        if(i == 0 && j != 0){
                            GridButton labelButtonPrimary = new GridButton(i,j);
                            GridButton labelButtonTracking = new GridButton(i,j);

                            labelButtonPrimary.setText("" + j);
                            labelButtonTracking.setText("" + j);
                            primary_grid[i][j]=labelButtonPrimary;
                            tracking_grid[i][j]=labelButtonTracking;

                            east.add(labelButtonTracking);
                            west.add(labelButtonPrimary);
                        }
                        else if(j==0 && i!=0) {
                            GridButton labelButtonPrimary=new GridButton(i,j);
                            GridButton labelButtonTracking=new GridButton(i,j);

                            labelButtonPrimary.setText(letters[i-1]);
                            labelButtonTracking.setText(letters[i-1]);

                            primary_grid[i][j]=labelButtonPrimary;
                            tracking_grid[i][j]=labelButtonTracking;

                            east.add(labelButtonTracking);
                            west.add(labelButtonPrimary);
                        }

                        else {

                            int indexRow = i;
                            int indexCol = j;

                            GridButton validButtonPrimary = new GridButton(i,j);
                            GridButton validButtonTracking = new GridButton(i,j);

                            primary_grid[i][j]=validButtonPrimary;
                            tracking_grid[i][j]=validButtonTracking;

                            //
                            // Action listener determines whether to place the ship
                            // horizontally or vertically, then does so if a ship
                            // has been chosen from the center panel
                            //

                            validButtonPrimary.addActionListener(e -> {

                                //
                                // Determine whether to place ship vertically
                                // or horizontally, then proceed to place ship
                                //

                                //
                                // First check to see if there is another ship
                                // in the path
                                //

                                if (setToHorizontal) {
                                    int startShipIndex = indexCol - shipArray[currentShipIndex].getSize() + 1;
                                    if (startShipIndex > 0 && !shipArray[currentShipIndex].isPlace()) {
                                        int tempSize = shipArray[currentShipIndex].getSize();

                                        //
                                        // Check to see if current space has ship in it
                                        //
                                        boolean tempHasShip = false;
                                        for (int k = 1; k <= tempSize; ++k) {
                                            int tempCol = indexCol - tempSize + k;

                                            tempHasShip = primary_grid[indexRow][tempCol].getShip();

                                            if (tempHasShip) {
                                                setShipCheck(true);
                                            }

                                        }
                                        if (tempHasShip) {
                                            
                                            JOptionPane.showMessageDialog(frame,"Ship overlapping");
                                            tempHasShip = false;

                                       }
                                    } else if (startShipIndex == 0) {
                                   	 JOptionPane.showMessageDialog(frame,"Cell out of bounds!");
                                   }
                                    
                                } else {
                                    int startShipIndex = indexRow - shipArray[currentShipIndex].getSize() + 1;
                                    if (startShipIndex > 0 && !shipArray[currentShipIndex].isPlace()) {
                                        int tempSize = shipArray[currentShipIndex].getSize();

                                        //
                                        // Check is current space has a ship in it
                                        //
                                        boolean tempHasShip = false;
                                        for (int k = 1; k <= tempSize; ++k) {
                                            int tempRow = indexRow - tempSize + k;

                                             tempHasShip = primary_grid[tempRow][indexCol].getShip();

                                            if (tempHasShip) {
                                                setShipCheck(true);
                                            }

                                        }
                                        if (tempHasShip) {
                                            JOptionPane.showMessageDialog(frame,"Ship overlapping");
                                            tempHasShip = false;

                                       }
                                        
                                        
                                   
                                    }
                                    else if (startShipIndex == 0) {
                                   	 JOptionPane.showMessageDialog(frame,"Cell out of bounds!");
                                   }
                                }


                                if (setToHorizontal && canPlace && !getShipCheck()) {

                                    //
                                    // Horizontal Option
                                    //

                                    int startShipIndex = indexCol - shipArray[currentShipIndex].getSize() + 1;
                                    if (startShipIndex > 0 && !shipArray[currentShipIndex].isPlace()) {
                                        int tempSize = shipArray[currentShipIndex].getSize();

                                        //
                                        // Place the ship relative to spaces left in ship
                                        // Set that GridButton() to have a ship
                                        //
                                        
                                        for (int k = 1 ; k <= tempSize ; ++k) {
                                            int tempCol = indexCol - tempSize + k;

                                            if(shipArray[currentShipIndex].getName()=="Aircraft Carrier") {
                                            	File picFile=new File("./battleship/"+AircraftfilesH[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);

                                    			primary_grid[indexRow][tempCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            	
                                            }
                                            else if(shipArray[currentShipIndex].getName()=="Battleship"){
                                            	File picFile=new File("./battleship/"+BattleshipfilesH[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);

                                    			primary_grid[indexRow][tempCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            }
                                            else if(shipArray[currentShipIndex].getName()=="Destroyer"){
                                            	File picFile=new File("./battleship/"+DestroyerfilesH[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);
                                    			
                                    			primary_grid[indexRow][tempCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            }
                                            else if(shipArray[currentShipIndex].getName()=="Submarine"){
                                            	File picFile=new File("./battleship/"+SubmarinefilesH[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);

                                    			primary_grid[indexRow][tempCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            }
                                            else if(shipArray[currentShipIndex].getName()=="Patrol Boat"){
                                            	File picFile=new File("./battleship/"+PatrolBoatfilesH[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);

                                    			primary_grid[indexRow][tempCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            }
                                            ShipIndices si=new ShipIndices(indexRow,tempCol);
                                            shipArray[currentShipIndex].shipindices[k-1]=si;
                                            //primary_grid[indexRow][tempCol].setButtonText();
                                            primary_grid[indexRow][tempCol].setToHasShip();

                                        }

                                        //
                                        // Set ship that was just placed to
                                        // have been placed
                                        //

                                        shipArray[currentShipIndex].setToBeenPlaced();

                                        //
                                        // Determine if all ships have been placed so we
                                        // can hide the adding ships panel
                                        //

                                        boolean allShipsHaveBeenPlaced = true;

                                        for (int in = 0 ; in < shipArray.length ; ++in) {
                                            if (!shipArray[in].isPlace()) {
                                                if (in != 0) {
                                                    allShipsHaveBeenPlaced = false;
                                                }

                                            }
                                        }

                                        if (allShipsHaveBeenPlaced) {
                                            addShipsPanel.setVisible(false);
                                            JOptionPane.showMessageDialog(frame,"All ships have been placed!");
                                            if(is_Server==true) {
                                            	    Random r=new Random();
                                            	    int side=r.nextInt()%2;
                                            	    if(side==0) {	//Server goes first
                                            	    	myTurn=true;
                                            	    	 ServerSocket serverSocket = null;
                                            	         try {
                                            	             serverSocket = new ServerSocket(4444);
                                            	         }
                                            	         catch (IOException ioen) {
                                            	             textArea.setText("Could not listen.");
                                            	         }

                                            	         Socket clientSocket = null;
                                            	         Socket communicationSocket=null;
                                            	         try {
                                            	             textArea.setText("Waiting for client.");

                                            	             clientSocket = serverSocket.accept();
                                            	             textArea.setText("My turn now");
                                            	        
                                            	             //clientSocket.close();
                                            	             //serverSocket.close();
                                            	             PrintWriter out = new PrintWriter(communicationSocket.getOutputStream(), 
                                            	                     true); 
                                            	             BufferedReader in = new BufferedReader( 
                                            	             		new InputStreamReader( communicationSocket.getInputStream())); 

                                            	             String inputLine="";; 

                                            	             while ((inputLine = in.readLine()) != null) 
                                            	             { 
                                            	             	System.out.println ("Server: " + inputLine); 
                                            	             	out.println(inputLine); 

                                            	             	if (inputLine.equals("Bye.")) 
                                            	             		break; 
                                            	             } 
                                            	             clientSocket.close();
                                            	             serverSocket.close();
                                            	         }
                                            	         catch (IOException ioe)
                                            	         {
                                            	             textArea.setText("Did not accept.");
                                            	             System.exit(1);

                                            	         }
                                            	     	

                                            	    }
                                            	    else if(is_Client==true){	//client goes first
                                            	    	try {
                                            	             Socket socket = new Socket("127.0.0.1", 4444);
                                            	             textArea.setText("Connected.");
                                            	             is_Client=true;
                                            	             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                            	             BufferedReader in = new BufferedReader(new InputStreamReader(
                                            	                                         socket.getInputStream()));
                                            	             BufferedReader stdIn = new BufferedReader(
                                            	                     new StringReader(JOptionPane.showInputDialog(null,"Enter the word 'Ready' if and when you are finished placing your ships.")));
                                            	             String userInput="";
                                            	        	 while ((userInput = stdIn.readLine()) != null) {
                                            		    	     out.println(userInput);
                                            		    	     System.out.println("echo: " + in.readLine());
                                            		    	     
                                            		    	     if (userInput.equals("Ready")) 
                                            		    	             break;
                                            		    	 }
                                            	        	   out.close();
                                            	        	   in.close();
                                            	              socket.close();

                                            		    
                                            	         } catch (UnknownHostException uee) {
                                            	             textArea.setText("Could not connect.");
                                            	             System.exit(1);
                                            	         } catch (IOException ioe) {
                                            	             textArea.setText("Could not connect.");
                                            	             System.exit(1);
                                            	         }
                                            	    }	    
                                            }
                                        }


                                        canPlace = false;
                                    }  else if (startShipIndex <= 0) {	
                                    		        JOptionPane.showMessageDialog(frame,"Cell out of bounds!");			
                              }


                                } else if (!setToHorizontal && canPlace && !getShipCheck()) {

                                    //
                                    // Vertical
                                    //

                                    int startShipIndex = indexRow - shipArray[currentShipIndex].getSize() + 1;
                                    if (startShipIndex > 0 && !shipArray[currentShipIndex].isPlace()) {
                                        int tempSize = shipArray[currentShipIndex].getSize();

                                        //
                                        // Place the ship relative to spaces left in ship
                                        // Set that GridButton() to have a ship
                                        //

                                        for (int k = 1; k <= tempSize; ++k) {
                                            int tempRow = indexRow - tempSize + k;
                                            
                                            if(shipArray[currentShipIndex].getName()=="Aircraft Carrier") {
                                            	
                                            	File picFile=new File("./battleship/"+AircraftfilesV[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);

                                    			primary_grid[tempRow][indexCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            }
                                            else if(shipArray[currentShipIndex].getName()=="Battleship"){
                                            	File picFile=new File("./battleship/"+BattleshipfilesV[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);

                                    			primary_grid[tempRow][indexCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            }
                                            else if(shipArray[currentShipIndex].getName()=="Destroyer"){
                                            	File picFile=new File("./battleship/"+DestroyerfilesV[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);

                                    			primary_grid[tempRow][indexCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            }
                                            else if(shipArray[currentShipIndex].getName()=="Submarine"){
                                            	File picFile=new File("./battleship/"+SubmarinefilesV[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);

                                    			primary_grid[tempRow][indexCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            }
                                            else if(shipArray[currentShipIndex].getName()=="Patrol Boat"){
                                            	File picFile=new File("./battleship/"+PatrolBoatfilesV[k-1]);
                                    			
                                    			try{
                                    			BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                    			Image buffresized=buffImage.getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
                                    			ImageIcon section=new ImageIcon(buffresized);

                                    			primary_grid[tempRow][indexCol].setIcon(section);
                                    			}catch(IOException ie){}
                                            }
                                            ShipIndices si=new ShipIndices(tempRow,indexCol);
                                            shipArray[currentShipIndex].shipindices[k-1]=si;
                                           // primary_grid[tempRow][indexCol].setButtonText();
                                            primary_grid[tempRow][indexCol].setToHasShip();

                                        }

                                        //
                                        // Set ship that was just placed to
                                        // have been placed
                                        //

                                        shipArray[currentShipIndex].setToBeenPlaced();

                                        //
                                        // Determine if all ships have been placed so we
                                        // can hide the adding ships panel
                                        //

                                        boolean allShipsHaveBeenPlaced = true;

                                        for (int in = 0; in < shipArray.length; ++in) {
                                            if (!shipArray[in].isPlace()) {
                                                if (in != 0) {
                                                    allShipsHaveBeenPlaced = false;
                                                }

                                            }
                                        }

                                        if (allShipsHaveBeenPlaced) {
                                            addShipsPanel.setVisible(false);
                                            JOptionPane.showMessageDialog(frame,"All ships have been placed!");
                                        }


                                        canPlace = false;


                                    }

                                    else if (startShipIndex <= 0) {	
                                    		                                        JOptionPane.showMessageDialog(frame,"Cell out of bounds!");			
                                    		                                   }
                                }

                                if (getShipCheck()) {
                                    setShipCheck(false);
                                }
                            });


                            validButtonTracking.addActionListener(ev -> {

                                /*if (currentShipIndex != -1) {

                                    int startShipIndex = indexCol - shipArray[currentShipIndex].getSize() + 1;
                                    System.out.println("index col = : " + indexCol + " and start col index is = " +  startShipIndex + "\n" );
                                    if (startShipIndex > 0) {
                                        int tempSize = shipArray[currentShipIndex].getSize();

                                        for (int k = 1 ; k <= tempSize ; ++k) {
                                            int tempCol = indexCol - tempSize + k;
                                            System.out.println("current col = " + tempCol);

                                            if (primary_grid[indexRow][tempCol] != null) {
                                                System.out.println("not null");

                                                //primary_grid[indexRow][tempCol].printRowCol();

                                                //primary_grid[indexRow][tempCol].setButtonText();

                                                validButtonTracking.setButtonText();
                                            } else {
                                                System.out.println("null");
                                            }

                                        }
                                    }
                                }*/

                                System.out.println("clicked tracking button");

                            });




                           // for(int i=1;i<11;i++) {
                            //for(int j=1;j<11;j++) {
                            	
                            File picFile=new File("./battleship/batt100.gif");

                            try{
                                BufferedImage buffImage = ImageIO.read(picFile);	//read the image
                                validButtonTracking.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
                                validButtonPrimary.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));

                                Image buffresized=buffImage.getScaledInstance(100,100,java.awt.Image.SCALE_SMOOTH);
                                ImageIcon section=new ImageIcon(buffresized);

                                tracking_grid[i][j]=validButtonTracking;
                                primary_grid[i][j]=validButtonPrimary;
                                validButtonTracking.setIcon(section);
                                validButtonPrimary.setIcon(section);
                            }

                            catch(IOException ie){}
                           // }
                            //}


                            east.add(validButtonTracking);
                            west.add(validButtonPrimary);
                        }
                    }

                }


                east.setPreferredSize(new Dimension(700,700));
                east.setBackground(new Color(239,232,206));
                west.setPreferredSize(new Dimension(700,700));
                west.setBackground(new Color(239,232,206));
                frame.setPreferredSize(new Dimension(1575,800));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(BorderLayout.EAST,east);
                frame.add(BorderLayout.WEST,west);

                frame.setResizable(true);
                frame.setVisible(true);
                frame.pack();

                // create text components
                textField = new JTextField();
                textArea = new JTextArea();
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);

                // automatically scroll down
                if(textArea.getCaret() instanceof DefaultCaret){
                    caret = (DefaultCaret) textArea.getCaret();
                    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
                    textArea.setCaret(caret);
                }

                scroll = new JScrollPane(textArea);
                scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scroll.setAutoscrolls(true);
                scroll.setPreferredSize(new Dimension(gridWidth - 50, 80));


                JPanel text = new JPanel();
                text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
                text.add(scroll);
                text.add(textField);
                textField.setBackground(new Color(239,232,206));
                text.setPreferredSize(new Dimension(gridHeight - 50, 80));

                frame.add(BorderLayout.SOUTH, text);

                threadCount++;
            }

            private void setJMenuBar(JMenuBar menuBar) {
                // TODO Auto-generated method stub
            }
        });








    }


    public class TestPane extends JPanel {

        private static final long serialVersionUID = 1L;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            // draw grid
            for (int i = 0; i < 10; i++) {
                g2.setColor(new Color(102,0,51));
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString(new Character((char) (i + 65)).toString(), i * 60 + 90, 40);	// letters A...J
                g2.drawString(new Integer(i + 1).toString(), 9, i * 60 + 90);					// numbers 1...10
                g2.setColor( new Color(102,0,51));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(60, i * 60 + 60, gridWidth - 40, i * 60 + 60);					// draw lines
                g2.drawLine(i * 60 + 60, 60, i * 60 + 60, gridHeight - 40);
            }
            g2.drawLine(60, 660, gridWidth - 40, 660);
            g2.drawLine(660, 60, 660, gridHeight - 40);
        }
    }



}
