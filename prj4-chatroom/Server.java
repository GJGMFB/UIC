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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Server extends JFrame implements ActionListener {

    // GUI items
    JButton connectBtn;
    JLabel serverAddr_label;
    JTextField serverAddr_info;
    
    JLabel serverPort_label;
    JTextField serverPort_info;
    JTextArea history;
    
    JFrame debugWindow;
    
    protected boolean isRunning;

    // Network Items
    boolean serverContinue;
    ServerSocket serverSocket;
    ArrayList<Socket> connectedSockets;
    ArrayList<String> connectedUsers;

    
    public Server() {
        super("Server");    // window title
        
        connectedSockets = new ArrayList<>();
        connectedUsers = new ArrayList<>();
        isRunning = false;
        
        // get content pane and set its layout
        Container container = getContentPane();
        container.setLayout(new GridLayout(5, 0));
        
        
        //Server address label GUI
        serverAddr_label = new JLabel("IP Address");
        container.add(serverAddr_label);
        
        // Set Server address
        String serverAddr = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            serverAddr = addr.getHostAddress();
        } 
        // If attempt to retrieve current ip fails, set to default
        catch (UnknownHostException e) {
            serverAddr = "127.0.0.1";
        }
        
        // Display Server Info
        serverAddr_info = new JTextField(serverAddr);
        serverAddr_info.setEditable(false);
        serverAddr_info.setForeground(Color.GRAY);
        serverAddr_info.setBackground(Color.WHITE);
        container.add(serverAddr_info);
        
        serverPort_label = new JLabel("Port");
        container.add(serverPort_label);
        serverPort_info = new JTextField(" Not Listening ");
        serverPort_info.setEditable(false);
        serverPort_info.setForeground(Color.GRAY);
        serverPort_info.setBackground(Color.WHITE);
        container.add(serverPort_info);

        // Server buttons
        connectBtn = new JButton("Listen");
        connectBtn.addActionListener(this);
        container.add(connectBtn);
        
        debugWindow = new JFrame("Debug Information");
        history = new JTextArea(40, 1);
        history.setEditable(false);
        debugWindow.add(new JScrollPane(history));

        debugWindow.setSize(400, 200);
		debugWindow.setLocation(250,0);
		debugWindow.setVisible(true);
        
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(250, 200);
        setVisible(true);

    } // end Server constructor

    // handle button events
    public void actionPerformed(ActionEvent event) {
        if (isRunning == false) {
            new ConnectionThread(this);
        } 
        else {
            serverContinue = false;
            isRunning = false;
            connectBtn.setText("Listen");
            serverPort_info.setText(" Disconnected ");
        }
    }
    
    public static void main(String args[]) {
        Server ServerApplication = new Server();
        ServerApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


} // end class Server

//@SuppressWarnings("ResultOfObjectAllocationIgnored")
        
class ConnectionThread extends Thread {

    Server gui;
    int listenPort;
    
    public ConnectionThread(Server s) {
        gui = s;
        start();
    }
    
    public void show_Connected(){
        int n = gui.connectedSockets.size();
        int i=0;
        String s = "";
        for ( ; i<n; i++)
            s += gui.connectedUsers.get(i)+ ": " +
                    gui.connectedSockets.get(i)+"\n";
        
        gui.history.insert("\nClients Connected:\n"+ s +"\n",0);
        /*
        gui.history.append("\nClients Connected:\n");
        for (Socket s : gui.connectedSockets)
            gui.history.insert(s + "\n", 0);
        gui.history.append("\nUsers connected: ");
        for (String s : gui.connectedUsers)
            gui.history.insert(s + "\n", 0);
        */
    }
    
    /**
     * Refreshes lists of connected sockets and connected users
     */
    public void refresh_Connected(){
        int n = gui.connectedSockets.size();
        for (int i=0; i<n; i++){
            Socket s = gui.connectedSockets.get(i);
            if (s.isClosed()){
                gui.connectedSockets.remove(i);
                gui.connectedUsers.remove(i);
            }
        }
    }
    
    public void add_CliToList(Socket cliSocket, String cliName){       
        gui.history.insert("\n"+ print_Time() +"  New Client Connected!\n",0);
        gui.connectedSockets.add(cliSocket);
        gui.connectedUsers.add(cliName);       
    }
    
    public Date print_Time(){
        return Calendar.getInstance().getTime();
    }
    
    
    @Override
    public void run() {
        gui.serverContinue = true;
        gui.isRunning = true;
        
        try {
            gui.serverSocket = new ServerSocket(0);
            listenPort = gui.serverSocket.getLocalPort();            
            gui.serverPort_info.setText("" + listenPort);
            
            gui.history.append("Connection Socket Created\n");
            try {
                while (gui.serverContinue) {
                    gui.history.append("Waiting for Connection...\n");
                    gui.connectBtn.setText("Disconnect");
                    
                    // get new client connection and add it to the list
                    Socket cliSocket = gui.serverSocket.accept();

                    BufferedReader b = new BufferedReader(
                            new InputStreamReader(cliSocket.getInputStream()));
                    
                    // corresponds to out.println(currUser) in Client program 
                    String cliName = b.readLine();
                    
                    add_CliToList(cliSocket, cliName);
                    
                    // prints list of connected clients
                    show_Connected();
                    
                    // create new client communication thread
//                    new CommunicationThread(cliSocket, gui);
                    new CommunicationThread(cliSocket, cliName, gui);
                }
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + listenPort + ".");
            System.exit(1);
        } 
        finally {
            try {
                for (Socket s : gui.connectedSockets)
                    s.close();
                gui.connectedSockets.clear();
                gui.connectedUsers.clear();               
                gui.serverSocket.close();
                
            } catch (IOException e) {
                System.err.println("Could not close port: " + listenPort + ".");
                System.exit(1);
            }
        }
    }
}

class CommunicationThread extends Thread {

    private Socket clientSocket;
    private Server gui;
    String clientName;
    
    public CommunicationThread(Socket clientSoc, Server s) {
        clientSocket = clientSoc;
        gui = s;
        clientName = "";
        start();
    }
    
    public CommunicationThread(Socket clientSoc, String user, Server s){
        clientSocket = clientSoc;
        gui = s;
        clientName = user.trim();
        try {
            // notifies all logged-in users of the new user login name
            push_MessageToAll("#"+clientName+"#");
            
            // sends all logged-in user names to the new user
            push_MessageToNewUser(clientName);
            
        } catch (IOException ex) {
            gui.history.append("Unable to notify online users of new connection!\n");
        }
        start();
    }

    
    @Override
    public void run() {
        System.out.println(
                "New Communication Thread Started with Client Connection");

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
           
//            ArrayList<String> recps = new ArrayList<>();
//            String[] recipients = in.readLine().split(",");
//            recps.addAll(Arrays.asList(recipients));
//            for (String s : recps){
//                s = s.trim();
//                System.out.println("*~* "+s);
//            }
            ArrayList<String> recps = new ArrayList<>(); 
            String input;
            while ((input = in.readLine()) != null) {
                System.out.println("Server: " + input);
                
                // this is kind of hack-ish and can easily be send exploit code,
                // but I'm tired and it's late :)
                if (input.startsWith("[") && input.endsWith("]")){
                    // clear out any old recipients in the list
                    recps.clear();
                    String[] rec = input.substring(1, input.length()-1).split(",");
                    for (String s : rec) {
                        s = s.trim();
                        // debugging
                        System.out.println("*~* "+s);
                    }
                    recps.addAll(Arrays.asList(rec));
                    
                    for (String s : recps){
                        s = s.trim();
                        // debugging
                        System.out.println("~~~ "+s);
                    }
                }
                
                else if (input.contains("*~PM~*"))
                {
                	input = input.substring(6);
                	push_Message(clientName +": "+ input, recps); 
                }
                
                else {
                    gui.history.insert(input + "\n", 0);                
                    // push message to users in recipient list                
                    
                    push_MessageToAll(clientName +": "+ input); 
                }

//                out.println(input);
                if (input.equals("Bye.")) {
                    break;
                }
                if (input.equals("End Server.")) {
                    gui.serverContinue = false;
                }
            }
            out.close();
            in.close();
            clientSocket.close();
            
        } catch (IOException e) {
            System.err.println("Problem with Communication Thread");
        }
    }
    
    
    public void push_MessageToNewUser(String newUser) throws IOException {
        int i = gui.connectedUsers.indexOf(newUser);
        Socket s = gui.connectedSockets.get(i);
        PrintWriter outIndiv = new PrintWriter(s.getOutputStream(),true);
        
        for (String str : gui.connectedUsers) {
            if (!str.equals(newUser))
                outIndiv.println("#"+str+"#");
        }
    }
    
    /**
     * Sends a message to all connected users
     * @param msg
     * @throws IOException 
     */
    public void push_MessageToAll(String msg) throws IOException {
        int i=0;
        int n = gui.connectedSockets.size();
        for ( ; i<n; i++){
            Socket s = gui.connectedSockets.get(i);
            String sName = gui.connectedUsers.get(i);
            System.out.println("debugging: "+sName +" "+s);
            PrintWriter outIndiv = new PrintWriter(s.getOutputStream(),true);
            outIndiv.println(msg);
        }                                                 
    }
    
    /**
     * Sends a message to users in the given String[] 
     * @param msg
     * @param recps
     * @throws IOException 
     */
//    public void push_Message(String msg, ArrayList<String> recps) throws IOException {
//        int n = recps.size();
//        int i;
//        for (String str : recps){
//            if (gui.connectedUsers.contains(str)) {
//                i = gui.connectedUsers.indexOf(str);
//                Socket s = gui.connectedSockets.get(i);
//                PrintWriter outIndiv = new PrintWriter(s.getOutputStream(),true);
//                outIndiv.println(msg);
//            }
//        } 
//    }
    
    public void push_Message(String msg, ArrayList<String> recps) throws IOException {
        int n = recps.size();
        for (int i = 0; i < n; i++){
            if (gui.connectedUsers.contains(recps.get(i))) {
                int index = gui.connectedUsers.indexOf(recps.get(i));
                Socket s = gui.connectedSockets.get(index);
                PrintWriter outIndiv = new PrintWriter(s.getOutputStream(),true);
                outIndiv.println(msg);
            }
        } 
    }
}
