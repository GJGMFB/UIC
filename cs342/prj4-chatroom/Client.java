/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SarahHayden
 * @author Dennis Aurelian Leancu
 * @author Paul Nguyen
 */

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.lang.*;
import java.util.*;
import java.util.List;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Client extends JFrame implements ActionListener {
    
    // GUI items
    JButton sendBtn;
    JButton connectBtn;
    JTextField serverAddr_txt;
    JTextField serverPort_txt;
    JTextField currUser_txt;    
    JTextField message_txt;
    JTextArea history;
    JList usersJList; 
    DefaultListModel usersListModel;
    
    // holds chat stuff and usersPanel - instantiated 
    // once client clicks "Connect" button
    JPanel chatPanel = new JPanel(new BorderLayout(20,0));    
    Container container;
    
    // Network Items
    boolean isConnected;
    Socket hostSocket;
    String currUser;
    PrintWriter out;
    BufferedReader in;
    
    //Private Message Component
    JButton sendPM;
    JFrame privateMsg;
    JTextField msg;

    /**
     * Constructor - sets up Client GUI
     */
    public Client() {
        super("Client Chat");
        isConnected = false;
        
        JLabel serverAddr_label = new JLabel("Server Address:", JLabel.RIGHT);
        JLabel serverPort_label = new JLabel("Server Port:", JLabel.RIGHT);
        JLabel userName_label = new JLabel("User Name:", JLabel.RIGHT);
    
        serverAddr_txt = new JTextField(10);
        serverPort_txt = new JTextField(5);
        currUser_txt = new JTextField(10);
        serverAddr_label.setLabelFor(serverAddr_txt);
        serverPort_label.setLabelFor(serverPort_txt);
        userName_label.setLabelFor(currUser_txt);
                
        JPanel serverPanel = new JPanel(new FlowLayout());
        JPanel btnPanel = new JPanel(new FlowLayout());
        
        serverPanel.add(serverAddr_label);
        serverPanel.add(serverAddr_txt);
        serverPanel.add(serverPort_label);
        serverPanel.add(serverPort_txt);
        
        connectBtn = new JButton("Connect");
        connectBtn.addActionListener(this);
                
        serverPanel.add(userName_label);
        serverPanel.add(currUser_txt);
        btnPanel.add(connectBtn);
        
        privateMsg = new JFrame("Private Message");
		privateMsg.setLayout(new FlowLayout());
        sendPM = new JButton("Send");
    	sendPM.addActionListener(this);
    	msg = new JTextField(20);
		privateMsg.add(msg);
		privateMsg.add(sendPM);
		privateMsg.setSize(400, 100);
		
                
        // creates chat window but does not add it to the frame
        create_ChatWindow();
        
        container = getContentPane();
        container.setLayout(new BorderLayout(20,0));        
        container.add(serverPanel, BorderLayout.NORTH);
        container.add(btnPanel, BorderLayout.CENTER);
                
//        setSize(500, 250);
        pack();
        setVisible(true);

    } // end Client constructor

    
    /**
     * Creates chat portion of the UI
     */
    public void create_ChatWindow() {        
        // holds message_txt and sendBtn
        JPanel msgPanel = new JPanel(new FlowLayout());
        
        // history = chat history
        history = new JTextArea(10, 40);
        history.setEditable(false);
        
        message_txt = new JTextField(20);
        message_txt.addActionListener(this);
        sendBtn = new JButton("Send");
        sendBtn.addActionListener(this);        
        msgPanel.add(message_txt);
        msgPanel.add(sendBtn);
        
        chatPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        chatPanel.add(new JScrollPane(history), BorderLayout.WEST);
        chatPanel.add(msgPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates logged in users list in Client chat window
     */
    public void create_UsersWindow() {
        // set up Users List Model
        usersListModel = new DefaultListModel();
        usersListModel.addListDataListener(new ListDataHandler());
        
        // set up Users JList based on usersListModel
        usersJList = new JList(usersListModel);
        usersJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        usersJList.setSelectedIndex(0);
        usersJList.addListSelectionListener(new ListSelectionHandler());
        usersJList.addMouseListener(new MouseAdapter() {
        	
        	public void mouseClicked(MouseEvent event){
        		if (SwingUtilities.isRightMouseButton(event))
        		{
        			
        			
//        			int length = msgGroup.size();
//        			for(int i = 0; i < length; i++)
//        			{
//        				
//        			}
//        			
        			
        			privateMsg.setVisible(true);
        			
        		}
        	}
        });
        usersJList.setVisible(true);
        
        // create JScrollPane of online users and add to frame
        JScrollPane jPane = new JScrollPane(usersJList);
        chatPanel.add(jPane,BorderLayout.CENTER);
    }
    
    
    
    /**
     * Once user clicks "Connect" the chat interface opens
     */
    public void open_ChatWindow() {
        // clear any text in the chat history and message box
        message_txt.setText(null);
        history.setText("");
        create_UsersWindow();   // create panel of logged in users
        container.add(chatPanel, BorderLayout.PAGE_END);
        sendBtn.setEnabled(true);
        message_txt.setEditable(true);
        pack();        
    }
    
    /**
     * Once user clicks "Disconnect" the chat interface is uneditable
     */
    public void close_ChatWindow() {
        sendBtn.setEnabled(false);
        message_txt.setEditable(false);
    }
    
    /**
     * Connects or disconnects Client to Server and input and output streams
     */
    public void doManageConnection() {
        if (isConnected == false) {
            String serverAddr = null;            
            int serverPort = -1;
            try {
                currUser = currUser_txt.getText();
                serverAddr = serverAddr_txt.getText();
                serverPort = Integer.parseInt(serverPort_txt.getText());
                hostSocket = new Socket(serverAddr, serverPort);
                
                out = new PrintWriter(hostSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                        hostSocket.getInputStream()));
                
                // this is how the Server gets the logged on user's name 
                out.println(currUser);  
                
                open_ChatWindow();  // open the chat interface
                isConnected = true;
                connectBtn.setText("Disconnect");
                
                // debugging info
                history.append(hostSocket.toString()+"\n");
                
            } catch (NumberFormatException e) {
                history.insert("Server Port must be an integer\n", 0);
            } catch (UnknownHostException e) {
                history.insert("Don't know about host: " + serverAddr, 0);
            } catch (IOException e) {
                history.insert("Couldn't get I/O for "
                        + "the connection to: " + serverAddr, 0);
            }
        }         
        else {  // if isConnected == true
            try {
                out.close();
                in.close();
                hostSocket.close();
                close_ChatWindow(); // close the chat interface
                isConnected = false;
                connectBtn.setText("Connect");
            } catch (IOException e) {
                history.insert("Error in closing down Socket ", 0);
            }
        }
    }
    
    /**
     * Gets list of users that will receive the message. 
     * Return value passed to doSendMessage(...).
     */
    public ArrayList<String> doGetRecipients() {
        return null;    //placeholder
    }
    
    /**
     * Need to add a parameter here for the list of who to send msg to
     */
    public void doSendMessage() {
            
        ArrayList<String> sendTo = new ArrayList<>();
        
        for (int i=0; i<usersListModel.size(); i++){
            sendTo.add(usersListModel.get(i).toString());
        }
        
        

        // for each selected user in the Online Users List, 
        // either: 
        //      1. add them to "sendTo"     or
        //      2. add them to a new panel (split pane?) and from there,
        //          add all of the users in that panel to recipient list
        
        
        System.out.println(sendTo); // debugging here
        
        // send to Server which users to send message to
        out.println(sendTo);

        // prints Message to Server
        out.println(message_txt.getText());

        // then clear the Message field
        message_txt.setText(null);

    }
    
    public void doPrivateMessage() {
        
        ArrayList<String> sendTo = new ArrayList<>();
        List<String> msgGroup = usersJList.getSelectedValuesList();
        
        for (int i=0; i<msgGroup.size(); i++){
            sendTo.add(msgGroup.get(i).toString());
        }
        
        

        // for each selected user in the Online Users List, 
        // either: 
        //      1. add them to "sendTo"     or
        //      2. add them to a new panel (split pane?) and from there,
        //          add all of the users in that panel to recipient list
        
        
        System.out.println(sendTo); // debugging here
        
        // send to Server which users to send message to
        out.println(sendTo);

        // prints Message to Server
        out.println("*~PM~*" + msg.getText());

        // then clear the Message field
        msg.setText(null);

    } 
    
    /**
     * Handle button events
     * @param event 
     */
    public void actionPerformed(ActionEvent event) {
        if (isConnected && (event.getSource() == sendBtn
                            || event.getSource() == message_txt)) {           
            doSendMessage();    // send message to user(s)            
        } 
        else if (event.getSource() == connectBtn) {
//            doManageConnection();
            new ClientThread(this);
        }
        else if (event.getSource() == sendPM) {
        	doPrivateMessage();
        	
        }
        
    }


    
    public static void main(String args[]) {
        Client application = new Client();
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }   

    
    
    class ListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
        } 
    }
    
    class ListDataHandler implements ListDataListener {

        @Override
        public void intervalAdded(ListDataEvent e) {
            
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
             
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            
        }
    }

        
} // end class Client


class ClientThread extends Thread {
    Client ClientGUI;
    String msgIn;
    
    public ClientThread(Client gui) {
        ClientGUI = gui; 
        msgIn = "";
        ClientGUI.doManageConnection();
        start();
    }

    @Override
    public void run() {      
        try {
            while ((msgIn = ClientGUI.in.readLine()) != null) {
                // new user has logged on
                if (msgIn.startsWith("#") && msgIn.endsWith("#")){
                    String newUser = msgIn.split("#")[1];   
                    // debugging to make sure #'s are trimmed off
                    System.out.println("newUser: "+newUser);
                    ClientGUI.usersListModel.addElement(newUser);
                    
                }
                else 
                    ClientGUI.history.append(msgIn+"\n");
            }
        } catch (IOException e){
            ClientGUI.history.append(
                    "Error in processing message from input stream\n");
        }

    }
}
