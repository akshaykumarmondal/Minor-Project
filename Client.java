import java.net.*;

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
import java.io.*;

public class Client extends JFrame {    //inherting properties from JFrame

    Socket socket; // coz, client

    // Two input and output streams.
    BufferedReader br; // Read the data
    PrintWriter out; // Write the data


    //Declaring Components        (GUI Part)
    private JLabel heading=new JLabel("Client Area");//Label
    private JTextArea messageArea=new JTextArea();// Message Area
    private JTextField messageInput=new JTextField();//Writing field
    private Font font=new Font("Roboto",Font.PLAIN, 20);




    //Constructor
    public Client() {

         try {

            System.out.println("Sending a request to server");

            socket = new Socket("127.0.0.1", 7778); // need ip address of computer(server) and port number should be
                                                    // same.

            System.out.println("Connection done");

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


            createGUI();    //GUI Method called later
            handleEvents(); //While writing message your message should display on the message area.

            startReading();
            //startWriting();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void handleEvents(){    //click the bulb
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
                //System.out.println("Key released "+e.getKeyCode());
                if(e.getKeyCode()==10){
                    //System.out.println("You have pressed enter button");

                    //sending the message while pressing enter

                    String contentToSend=messageInput.getText();
                    messageArea.append("Me: "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
                
            }
            
        });



    }




    private void createGUI(){       //Calling GUI function

        //GUI code.. creation
        this.setTitle("Client Messenger[END]");// Setting title
        this.setSize(500,500);// size of window
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Exists on clicking the cross button

        //coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("icons8-chat-room-30.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false); //Cannot edit the message

        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        
        
        //Setting layout of the windowframe
        this.setLayout(new BorderLayout());


        //adding the component to frames
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER); //provides scrollbar
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
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server terminated the chat");

                        //Disabling the input after server is closed
                        messageInput.setEnabled(false);
                        socket.close(); // close the reader

                        break;
                    }

                    //System.out.println("Server :" + msg);

                    //If the server does not terminates
                    messageArea.append("Server : " +msg+"\n");

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
                    } // If the client tries to write something , it will see that the message is exit
                      // and it will stop writing or the socket is closed.

                }

                System.out.println("Connection  closed");   //Only if reader is closed

            } catch (Exception e) {
                e.printStackTrace();
            }

        };

        new Thread(r2).start();// Starting the thread

    }

    public static void main(String[] args) {

        System.out.println("This is client....");
        new Client();

    }
}

// We are using loop inside try catch exception to stop the multithread that is
// to stop the writer and reader permanently while writing exit.