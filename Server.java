import java.net.*; // As we are using the concepts of networking

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*; // For BufferredReader

class Server extends JFrame {

    ServerSocket server; // variable for creating serversocket i.e; server via Serversocket class inside
                         // net class.

    Socket socket; // variable for creating socket that is client via Socket class present inside
                   // net class

    // Two input and output streams.
    BufferedReader br; // Read the data
    PrintWriter out; // Write the data

    // Now, the client object which is being accepted by the server after the
    // connection is established is read by br and after reading the message is sent
    // by out by the serversocket.


    //Declare Components
    private JLabel heading=new JLabel("Server Area");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font=new Font("Roboto",Font.PLAIN,20);



    // Creating the constructor of Server
    public Server() {

        // Initialization of objects

        try {

            server = new ServerSocket(7778); // defining port is important so that the client can understand to whom we
                                             // are sending the request.

            System.out.println("Server is ready to accept the connection");
            System.out.println("Waiting....");

            socket = server.accept(); // here, server is accepting the request of client(Socket) and returning the
                                      // object of socket. i.e.; socket= //With the help of this socket(client) , that
                                      // is after getting the socket object as well as connection we can create the
                                      // input string and output string for reading and sending the data.

            br = new BufferedReader(new InputStreamReader(socket.getInputStream())); // We are generating an input
                                                                                     // stream from the socket which is
                                                                                     // then read here in the server.
                                                                                     // The data coming from the
                                                                                     // client(getInputStream) is in the
                                                                                     // byte format and then it is
                                                                                     // passed to the InputStreamReader
                                                                                     // which converts the byte into
                                                                                     // character and then the character
                                                                                     // is read by the br.

            out = new PrintWriter(socket.getOutputStream());


            createGUI();
            handleEvents();
            startReading();
            // startWriting();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    private void handleEvents() {

        messageInput.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
            // System.out.println("Key released "+e.getKeyCode());
                
            if(e.getKeyCode()==10){
                    //System.out.println("You have pressed enter button");
                    String contentToSend=messageInput.getText();
                    messageArea.append("Me :"+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus(); 
                }
            }
            
        });

    }


    private void createGUI(){
        //gui code
        this.setTitle("Server messenger");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("icons8-chat-room-30.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        //Layout of the frame of the window
        this.setLayout(new BorderLayout());

        //adding the components to the frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);


    }





    // We have to read and write at the same time that is run both the tasks
    // simultaneously , for which we need multithreading.
    public void startReading() { // Reading the data from the br

        // thread will keep on giving the data by reading

        Runnable r1 = () -> { // thread

            System.out.println("Reader started...");

            try {
                while (true) { // Infinite loop which keeps on reading

                    String msg = br.readLine();

                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this,"Client terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }

                    //System.out.println("Client :" + msg);
                    messageArea.append("Client :"+ msg+"\n");

                }

            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("Connection closed");
            }
        };

        new Thread(r1).start(); // starting the thread

    }

    public void startWriting() { // Writing the data from the out

        // here, thread will keep on accepting the data from the user and send it it to
        // the client.

        Runnable r2 = () -> { // thread

            System.out.println("Writer Started....");

            try {
                while (true && !socket.isClosed()) {

                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in)); // Accepting the message
                                                                                               // from the user.

                    String content = br1.readLine();

                    out.println(content); // Sending the message/content
                    out.flush(); // forcefully sending the data

                    if (content.equals("exit")) {

                        socket.close();
                        break;
                    } // After sending the message to the client if the message is exit the socket
                      // will be closed

                }
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("Connection closed");
            }

           // System.out.println("Connection closed");

        };

        new Thread(r2).start();// Starting the thread

    }

    public static void main(String[] args) { // On calling the main function the message will print and the constructor
                                             // is called, so basically the idea is to perform all operations inside the
                                             // constructor only. This is the purpose of creating the constructor, i.e;
                                             // in order to execute all the work while calling the main function due to
                                             // which the constructor is also called.

        System.out.println("this is server..going to start server");

        // Calling the constructor of the server
        new Server();

    }
}