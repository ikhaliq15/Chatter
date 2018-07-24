package net.theinterweb.Chatterbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledEditorKit;

import net.theinterweb.Chatterbox.Utils.CEditorPane;


public class ChatClient {

    BufferedReader in ;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(40);
//    JTextArea messageArea = new JTextArea(8, 40);
    CEditorPane messageArea = new CEditorPane();
    JButton clear = new JButton("Clear");
    JPanel pnl_Bottom = new JPanel();
    JButton logout = new JButton("Logout");
    JButton upload = new JButton("Upload");
    static String name;
    boolean admin = false;

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Return in the
     * listener sends the textfield contents to the server.  Note
     * however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED
     * message from the server.
     */
    public ChatClient() {
        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        messageArea.setEditorKit(new StyledEditorKit());
        messageArea.initListener();
        //messageArea.setContentType("text/html");
        //messageArea.setText("<html><b>Hey Guys</b> i love you</html>");
        messageArea.setPreferredSize((new JTextArea(8, 40).getPreferredSize()));
        frame.pack();
        upload.setEnabled(false);
        DefaultCaret caret = (DefaultCaret)messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.getContentPane().add(clear, "East");
        frame.getContentPane().add(logout, "South");
        frame.getContentPane().add(upload, "West");
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            /**
             * Responds to pressing the enter key in the textfield by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    out.println(name + ": " + textField.getText());
                } catch (Exception a) {
                    System.out.print("");
                }
                textField.setText("");
            }
        });
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messageArea.setText("");
            }
        });
        
        upload.addActionListener(new ActionListener () {
        	public void actionPerformed(ActionEvent e){
        		JFileChooser fileChooser = new JFileChooser();
        		int returnValue = fileChooser.showOpenDialog(frame);
        		if (returnValue == JFileChooser.APPROVE_OPTION){
        			File selectedFile = fileChooser.getSelectedFile();
        			try {
        				out.println(name + ": " + selectedFile.getPath());
        			} catch (Exception a) {
        				
        			}
        		}
        	}
        });
    }

    /**
     * Prompt for and return the address of the server.
     */
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Prompt for and return the desired screen name.
     */
    private String getName() {
        name = JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
        //System.out.println(name);
        return name;
    }
    
    private String getPassword() {
        String pass = JOptionPane.showInputDialog(
            frame,
            "Enter password: ",
            "Server Requires A Password",
            JOptionPane.PLAIN_MESSAGE);
        //System.out.println(name);
        return pass;
    }

    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = getServerAddress();@
        SuppressWarnings("resource")
        Socket socket = new Socket(serverAddress, 8118); in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        URL connection = new URL("http://checkip.amazonaws.com/");
        URLConnection con = connection.openConnection();
        String str = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        str = reader.readLine();


        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            //Log.log(line, Log.NORMAL);
            if (line == null) {

            } else if (line.startsWith("SUBMITNAME")) {
                //System.out.println();
                out.println(getName());
            } else if (line.startsWith("REQUESTPASS")) {
            	out.println(getPassword());
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
                upload.setEnabled(true);
            } else if (line.equals("IPASK")) {
                out.println("IP " + str);
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            } else if (line.startsWith("YOUJOINED")) {
                //messageArea.append(line.substring(10) + "\n");
            } else if (line.equals("EXIT")) {
                System.exit(0);
            } else if (line.equals("CLEAR")) {
                messageArea.setText("You're screen has been cleared by the server.\n");
            } else if (line.equals("QUIT")) {
                System.exit(0);
            } else if (line.equals("BANNED")) {
                messageArea.append("You have been kicked off the server!\n");
                int seconds = 3;
                while (seconds > -1) {
                    messageArea.append("You have " + seconds + " seconds left!\n");
                    seconds--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            } else if (line.startsWith("GETNAMES ")) {
                messageArea.append(line.substring(9, line.length()) + "\n");
                //line = "";
            } else if (line.startsWith("GETIPS ")) {
                messageArea.append(line.substring(7, line.length()) + "\n");
            } else if (line.startsWith("SILENCE")) {
            	messageArea.append("some one got banned! XD");
            } else if (line.startsWith("TYPING ")) {
            	
            }
        }
    }

    /**
     * Runs the client as an application with a closeable frame.
     */
    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setLocationRelativeTo(null);
        client.frame.setVisible(true);
        client.run();
    }
}