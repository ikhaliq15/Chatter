package net.theinterweb.Chatterbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;


public class ChatServer {

    /**
     * The port that the server listens on.
     */
    private static final int PORT = 8118;

    /**
     * The set of all names of clients in the chat room.  Maintained
     * so that we can check that new clients are not registering name
     * already in use.
     */
    private static ArrayList<String> names = new ArrayList<String>();

    /**
     * The set of all the print writers for all the clients.  This
     * set is kept so we can easily broadcast messages.
     */
    
    private static ArrayList<String> admins = new ArrayList<String>();
    
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    /**
     * The appplication main method, which just listens on a port and
     * spawns handler threads.
     */
    
    static Scanner scan = new Scanner(System.in);
    static String command;
    static ChatClient chatclient;
    static String adminname;
    static boolean enteringName = true;
    static String[] messages = new String[3];
    static List<String> listOfString = new ArrayList<String>();
    static List<String> oldListOfString = new ArrayList<String>();
    static List<String> ip = new ArrayList<String>();
    static Socket socket;
    static int nameTyped = 0;
    
    //The main function
    public static void main(String[] args) throws Exception {
    	
    	oldListOfString = listOfString;
    	
    	//Set up the server
        ServerSocket listener = new ServerSocket(PORT);
        socket = new Socket("localhost", PORT);
        chatclient = new ChatClient();
        chatclient.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatclient.frame.setLocationRelativeTo(null);
        chatclient.textField.setEditable(true);
        chatclient.frame.setVisible(true);
        chatclient.textField.setText("ENTER YOUR NAME HERE(DELETE THIS): ");
        chatclient.textField.selectAll();
        chatclient.logout.setText("Close Server");
        
        //The action listener for the box where the message is typed
        
        try{
        	
        	
        	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        	
        	chatclient.textField.addActionListener(new ActionListener(){
        		@Override
        		public void actionPerformed(ActionEvent e) {
//        			if(nameTyped == 1){
//        				adminname = names.get(0);
//        				nameTyped ++;
//        			}
        			if(names.size() > 0){
        				adminname = names.get(names.size()-1);
        			}else{
        				adminname = chatclient.textField.getText();
        			}
        			//nameTyped ++;
        			
        			if(chatclient.textField.getText().toString().toLowerCase().contains("/kick")){
        				try{
        					names.remove(chatclient.textField.getText().toString().substring(6));
        				}catch(Exception ex){
        					chatclient.messageArea.append("Use of kick: /kick [name]\n");
        				}
        			}else if(chatclient.textField.getText().toString().contains("/reset")){
        				//out.println("CLEAR");
        			}else if(chatclient.textField.getText().toString().contains("/get")){
        				try{
        					if(!chatclient.textField.getText().toString().substring(5).equals(null) || !chatclient.textField.getText().toString().substring(5).equals("")){
        						if(chatclient.textField.getText().toString().substring(5).contains("names")){
        							chatclient.messageArea.append(names.toString() + "\n");
        						}else if(chatclient.textField.getText().toString().substring(5).contains("ips")){
        							chatclient.messageArea.append(ip.toString() + "\n");
        							//System.out.println(ip);
        						}else if(chatclient.textField.getText().toString().substring(5).contains("admins")){
        							chatclient.messageArea.append(admins.toString() + "\n");
        						}
        					}
        				}catch(Exception exception1){
        					chatclient.messageArea.append("Use of get: /get [names;ips;admins]\n");
        				}
        			}else if(chatclient.textField.getText().toString().contains("/op")){
        				admins.add(chatclient.textField.getText().toString().substring(4));
        			}else{
        				out.println(chatclient.textField.getText());
        			}
        			chatclient.textField.setText("");
        		}  
        	});
        }catch(Exception ex){
        	
        }
        
        //Start Listening For Other People
        try {
        	while(true){
				new Handler(listener.accept()).start();
        	}   	
        } finally {
            listener.close();
            socket.close();
        }
    }
    
    protected static String getIps() {
    	List<String> ips = new ArrayList<String>();
    	for(int i = 0; i < ip.size()/2;i+=2){
    		ips.add(ip.get(i));
    	}
    	return ips.toString();
	}

	public static int close(){
    		PrintWriter out = null;
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			out.println("EXIT");
			System.exit(0);
			return 0;
    }
    
    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for a dealing with a single client
     * and broadcasting its messages.
     */
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
       
        
        /**
         * Constructs a handler thread, squirreling away the socket.
         * All the interesting work is done in the run method.
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Services this thread's client by repeatedly requesting a
         * screen name until a unique one has been submitted, then
         * acknowledges the name and registers the output stream for
         * the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        
        public void run() {
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                
                chatclient.logout.addActionListener(new ActionListener(){
                	public void actionPerformed(ActionEvent e){
                		out.println("EXIT");
                	}
                });
                
                chatclient.frame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        				out.println("EXIT");
        				
                    }
                });
                
                
                chatclient.textField.addActionListener(new ActionListener(){
            		@Override
            		public void actionPerformed(ActionEvent e) {
            			if(chatclient.textField.getText().contains("/reset")){
                			out.println("CLEAR");
                			chatclient.messageArea.setText((""));
            			}
            			if(chatclient.textField.getText().equals("")){
            				
            			}
            		}	
            	});
                
                
                // Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  Note that
                // checking for the existence of a name and adding the name
                // must be done while locking the set of names.
                while (true) {
                	if(!names.isEmpty()){
                		out.println("SUBMITNAME");
                	}
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                    	if(name.toLowerCase().contains("admin") || name.contains("[") || name.contains("]")){
                    		out.println("EXIT");
                    	}
                        if (!names.contains(name)) {
                        	if(names.size() > 0){
                        		names.add(names.size()-1, name);
                        	}else{
                        		names.add(0, name);
                        	}
                            for (@SuppressWarnings("unused") PrintWriter writer : writers) {
                                //writer.println("NAMEJOINED " + name);
                                out.println("YOUJOINED " + "You joined the server.\n");
                            }
                            break;
                        }
                    }
                }
                
                // Now that a successful name has been chosen, add the
                // socket's print writer to the set of all writers so
                // this client can receive broadcast messages.
                out.println("NAMEACCEPTED");
                out.println("IPASK");
                writers.add(out);

                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    System.out.println(input);
                    for (PrintWriter writer : writers) {
                       if(input.equals("CLEAR")){
                    	   out.println("CLEAR");
                       }else if(input.startsWith("IP")){
                    	   if(!name.equals(null) && !ip.contains(name + ": " + input.substring(2))){
                    		   //System.out.println(name);
                    		   ip.add(name + ": " + input.substring(2));
                    	   }
                       }else if(name.equals(adminname)){
                           writer.println("MESSAGE " + "[" + adminname + "]"+ ": " + input);
                       }else if(admins.contains(name)){
                    	   if(input.contains("/reset")){
                    		   out.println("CLEAR");
                    	   }else if(input.contains("/op")){
                    		   admins.add(input.substring(name.length()+6));
                    		   //System.out.println(input);
                    		   //System.out.println(admins);
                    	   }else if(input.contains("/get")){
                    		   try{
               					if(!input.substring(5).equals(null) || !input.substring(5).equals("")){
               						if(input.substring(5).contains("names")){
               							out.println("GETNAMES " + names.toString());
               							input = "GOTNAMES";
               							//System.out.println("NAMES SENT!");
               						}else if(input.substring(5).contains("ips")){
               							out.println("GETIPS " + ip.toString());
               							input = "GOTNAMES";
               						}
               					}
               				}catch(Exception exception1){
               					writer.println("Use of get: /get [names;ips]\n");
               				}
                    	   }else{
                    		   writer.println("MESSAGE [" + name + "]"+ ": " + input.substring((name.length())+2, input.length()) + "\n");
                    	   }
                       }else{
                    	   if(names.contains(name)){
                    		   writer.println("MESSAGE " + input + "\n");
                    		   //System.out.println("a message");
                    	   }else{
                    		   out.println("BANNED");
                    	   }
                       }
                    }
                    if(admins.contains(name)){
            				chatclient.messageArea.append("[" + name + "]"+ ": " + input.substring((name.length())+2, input.length()) + "\n");
            		}else if(name.equals(adminname)){
                    	chatclient.messageArea.append("[" + adminname + "]"+ ": " + input + "\n");
                    }else if(input.startsWith("IP")){
                    	chatclient.messageArea.append("");
                    }else{
                    	chatclient.messageArea.append(input + "\n");
                    }
                }
                
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}