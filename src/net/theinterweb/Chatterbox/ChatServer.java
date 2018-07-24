package net.theinterweb.Chatterbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.text.DefaultCaret;

import net.theinterweb.Chatterbox.Utils.CompareByLength;
import net.theinterweb.Chatterbox.Utils.Log;


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
    private static ArrayList < String > names = new ArrayList < String > ();

    /**
     * The set of all the print writers for all the clients.  This
     * set is kept so we can easily broadcast messages.
     */

    private static ArrayList < String > admins = new ArrayList < String > ();

    private static HashSet < PrintWriter > writers = new HashSet < PrintWriter > ();

    /**
     * The appplication main method, which just listens on a port and
     * spawns handler threads.
     */
    
    private static CompareByLength cbl = new CompareByLength("");

    static Scanner scan = new Scanner(System.in);
    static String command;
    static ChatClient chatclient;
    static String adminname;
    static boolean enteringName = true;
    static String[] messages = new String[3];
    static List < String > listOfString = new ArrayList < String > ();
    static List < String > oldListOfString = new ArrayList < String > ();
    static List < String > ip = new ArrayList < String > ();
    static Socket socket;
    static int nameTyped = 0;
    
    private static String password = "";
    private static boolean hasPassword = false;
    
    static ArrayList<String> blacklisted_phrases = new ArrayList<String>();
    static ArrayList<String> blacklisted_ips = new ArrayList<String>();
    
    //The main function
    public static void main(String[] args) throws Exception {

        oldListOfString = listOfString;

        //Set up the server
        ServerSocket listener = null;
        try {
        	listener = new ServerSocket(PORT);
        }catch (Exception e){
        	Log.log("server already exists on port.", Log.FATAL);
        }
        socket = new Socket("localhost", PORT);
        chatclient = new ChatClient();
        chatclient.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatclient.frame.setLocationRelativeTo(null);
        chatclient.textField.setEditable(true);
        chatclient.frame.setVisible(true);
        chatclient.upload.setEnabled(true);
        chatclient.textField.setText("ENTER YOUR NAME HERE(DELETE THIS): ");
        chatclient.textField.selectAll();
        chatclient.logout.setText("Close Server");
        DefaultCaret caret = (DefaultCaret)chatclient.messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
                
        Log.log("starting server.", Log.NORMAL);

        //The action listener for the box where the message is typed
        try {


            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            chatclient.upload.removeActionListener(chatclient.upload.getActionListeners()[0]);
            chatclient.upload.addActionListener(new ActionListener() {
            	public void actionPerformed (ActionEvent e){
            		JFileChooser fileChooser = new JFileChooser();
            		int returnValue = fileChooser.showOpenDialog(chatclient.frame);
            		if (returnValue == JFileChooser.APPROVE_OPTION){
            			File selectedFile = fileChooser.getSelectedFile();
            			try {
            				out.println(selectedFile.getPath());
            			} catch (Exception a) {
            				
            			}
            		}
            	}
            });

            chatclient.textField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //        			if(nameTyped == 1){
                    //        				adminname = names.get(0);
                    //        				nameTyped ++;
                    //        			}
                    if (names.size() > 0) {
                        adminname = names.get(names.size() - 1);
                    } else {
                        adminname = chatclient.textField.getText();
                    }
                    //nameTyped ++;
                    
                    String com = chatclient.textField.getText().toString();
                    
                    if (com.toLowerCase().contains("/kick")) {
//                        try {
//                            names.remove(com.substring(6));
//                        } catch (Exception ex) {
//                            chatclient.messageArea.append("Use of kick: /kick [name]\n");
//                        }
                    } else if (com.startsWith("/ban ")) {
                    	com = com.substring("/ban ".length());
                    	if (com.startsWith("phrase ")) {
                    		blacklisted_phrases.add(com.substring("phrase ".length()));
                    		//blacklisted_phrases.so
                    		Collections.sort(blacklisted_phrases, cbl);
                    	} else if (com.startsWith("ip ")) {
                    		blacklisted_ips.add(com.substring("ip ".length()));
                    	}
                    } else if (com.startsWith("/allow ")) {
                    	com = com.substring("/allow ".length());
                    	if (com.startsWith("phrase ")){
                    		blacklisted_phrases.remove(com.substring("phrase ".length()));
                    	} else if (com.startsWith("ip ")) {
                    		blacklisted_ips.remove(com.substring("ip ".length()));
                    	}
                    } else if (com.contains("/reset")) {
                        //out.println("CLEAR");
                    } else if (com.contains("/get")) {
                        try {
                            if (!com.substring(5).equals(null) || !com.toString().substring(5).equals("")) {
                                if (com.substring(5).contains("names")) {
                                    chatclient.messageArea.append(names.toString() + "\n");
                                } else if (com.substring(5).startsWith("ips")) {
                                    chatclient.messageArea.append(ip.toString() + "\n");
                                    //System.out.println(ip);
                                } else if (com.substring(5).contains("admins")) {
                                    chatclient.messageArea.append(admins.toString() + "\n");
                                } else if (com.substring(5).contains("blwords")) {
                                	chatclient.messageArea.append(blacklisted_phrases.toString() + "\n");
                                } else if (com.substring(5).startsWith("blips")) {
                                	chatclient.messageArea.append(blacklisted_ips.toString() + "\n");
                                }
                            }
                        } catch (Exception exception1) {
                            chatclient.messageArea.append("Use of get: /get [names;ips;admins;blwords;blips]\n");
                        }
                    } else if (com.contains("/op")) {
                        admins.add(com.substring(4));
                    } else if (com.startsWith("/setpassword")){
                    	hasPassword = true;
                    	password = com.substring("/setpassword ".length());
                    	chatclient.messageArea.append("Password set to " + password + ". Use /turnoffpass to turn off the password.\n");
                    } else if (com.startsWith("/turnoffpass")){
                    	hasPassword = false;
                    } else {
                        out.println(com);
                    }
                    chatclient.textField.setText("");
                }
            });
        } catch (Exception ex) {

        }

        //Start Listening For Other People
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
            socket.close();
        }
    }

    protected static String getIps() {
        List < String > ips = new ArrayList < String > ();
        for (int i = 0; i < ip.size() / 2; i += 2) {
            ips.add(ip.get(i));
        }
        return ips.toString();
    }

    public static int close() {
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
        private BufferedReader in ;
        private PrintWriter out;
        
        MessageSender ms = new MessageSender(out, name, "t");

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
            	boolean enteredPassword = false;
                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);


                chatclient.logout.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        out.println("QUIT");
                    }
                });

                chatclient.frame.addWindowListener(new java.awt.event.WindowAdapter() {@
                    Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        out.println("EXIT");
                    }
                });


                chatclient.textField.addActionListener(new ActionListener() {@
                    Override
                    public void actionPerformed(ActionEvent e) {
                		String com = chatclient.textField.getText();
                		if (com.contains("/reset")) {
                            out.println("CLEAR");
                            chatclient.messageArea.setText((""));
                        } else if (com.startsWith("/kick ")) {
                        	try {
                        		names.remove(com.substring(6));
                        	} catch (Exception ex) {
                        		chatclient.messageArea.append("Use of kick: /kick [name]\n");
                        	}
                        } else if (com.equals("")) {

                        }
                    }
                });
                
                // Check for password and ask user for it.
                while (!enteredPassword) {
                	if (!hasPassword){
                		enteredPassword = true;
                	}else{
                		out.println("REQUESTPASS");
                		String inputed_pass = in.readLine();
                    	if (inputed_pass.equals(password)){
                    		enteredPassword = true;
                    	}
                	}
                }


                // Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  Note that
                // checking for the existence of a name and adding the name
                // must be done while locking the set of names.
                while (true) {
                    if (!names.isEmpty()) {
                        out.println("SUBMITNAME");
                    }
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized(names) {
                        if (name.toLowerCase().contains("admin") || name.contains("[") || name.contains("]")) {
                            out.println("EXIT");
                        }
                        if (!names.contains(name)) {
                            if (names.size() > 0) {
                                names.add(names.size() - 1, name);
                            } else {
                                names.add(0, name);
                            }
                            for (@SuppressWarnings("unused") PrintWriter writer: writers) {
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
                
                ms.setProperties(name, out);
                ms.start();
                
                
                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                

                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    Log.log(input, Log.NORMAL);
                    for (PrintWriter writer: writers) {
                        if (input.equals("CLEAR")) {
                            out.println("CLEAR");
                        } else if (input.startsWith("IP")) {
                            if (!name.equals(null) && !ip.contains(name + ": " + input.substring(2))) {
                                //System.out.println(name);
                            	if (blacklisted_ips.contains(input.substring(3))) {
                            		names.remove(name);
                            	}
                                ip.add(name + ": " + input.substring(3));
                            }
                        } else if (name.equals(adminname)) {
                            writer.println("MESSAGE " + "[" + adminname + "]" + ": " + input);
                        } else if (admins.contains(name)) {
                            if (input.contains("/reset")) {
                                writer.println("CLEAR");
                            } else if (input.contains("/op")) {
                                admins.add(input.substring(name.length() + 6));
                                //System.out.println(input);
                                //System.out.println(admins);
                            } else if (input.contains("/get")) {
                                try {
                                    if (!input.substring(5).equals(null) || !input.substring(5).equals("")) {
                                        if (input.substring(5).contains("names")) {
                                            out.println("GETNAMES " + names.toString());
                                            input = "GOTNAMES";
                                            //System.out.println("NAMES SENT!");
                                        } else if (input.substring(5).contains("ips")) {
                                            out.println("GETIPS " + ip.toString());
                                            input = "GOTNAMES";
                                        }
                                    }
                                } catch (Exception exception1) {
                                    writer.println("Use of get: /get [names;ips]\n");
                                }
                            } else {
                            	for (int i = 0; i < blacklisted_phrases.size(); i++){
                            		if (input.toLowerCase().contains(blacklisted_phrases.get(i).toLowerCase())){
	                            		String replacement = "";
	                            		for (int j = 0; j < blacklisted_phrases.get(i).length(); j++){
	                            			replacement += "*";
	                            		}
	                            	 	input = input.replaceAll(blacklisted_phrases.get(i), replacement);
                            		}
                            	}
                                writer.println("MESSAGE [" + name + "]" + ": " + input.substring((name.length()) + 2, input.length()) + "\n");
                            }
                        } else {
                            if (names.contains(name)) {
                            	for (int i = 0; i < blacklisted_phrases.size(); i++){
                            		if (input.toLowerCase().contains(blacklisted_phrases.get(i).toLowerCase())){
	                            		String replacement = "";
	                            		for (int j = 0; j < blacklisted_phrases.get(i).length(); j++){
	                            			replacement += "*";
	                            		}
	                            	 	input = input.replaceAll(blacklisted_phrases.get(i), replacement);
                            		}
                            	}
                                writer.println("MESSAGE " + input + "\n");
                            } else {
                                //out.println("BANNED");
                            }
                        }
                    }
                    if (admins.contains(name)) {
                        chatclient.messageArea.append("[" + name + "]" + ": " + input.substring((name.length()) + 2, input.length()) + "\n");
                    } else if (name.equals(adminname)) {
                        chatclient.messageArea.append("[" + adminname + "]" + ": " + input + "\n");
                    } else if (input.startsWith("IP")) {
                        chatclient.messageArea.append("");
                    } else {
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
                } catch (IOException e) {}
            }
        }
    }
    
    static class MessageSender extends Thread {
    	private String name;
    	private PrintWriter out;
    	public MessageSender(PrintWriter out, String name, String tname){
    		this.name = name;
    		this.out = out;
    	}
    	public void setProperties(String n, PrintWriter o){
    		this.name = n;
    		this.out = o;
    	}
		public void run() {
			while (true) {
				//Log.log("in the message sender of " + this.name, Log.WARNING);
				if (!names.contains(name)){
					out.println("BANNED");
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }
}