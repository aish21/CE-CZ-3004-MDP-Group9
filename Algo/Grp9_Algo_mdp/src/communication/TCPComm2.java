package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Goh Cheng Guan, Clive
 * @author Lau Zhen Jie
 * @version 1.0
 * @since 2021-9-15
 */
public class TCPComm2 {

    // initialize socket and input output streams
    private Socket socket = null;
    private InputStream din = null;
    private PrintStream dout = null;
    private static TCPComm2 cs = null;

    private static final String IPaddr = "192.168.9.9";
    private static final int portNum = 5182;

    /**
     * private default constructor
     */
    private TCPComm2() {
    }

    /**
     * This method return a singleton object of this class
     *
     * @return TCPcomm2 object
     */
    public static TCPComm2 getInstance() {
        if (cs == null) {
            cs = new TCPComm2();
        }
        return cs;
    }

    /**
     * This method attempts to establish socket connect via the specified RPI address and port.
     *
     * @return Error message if the connections fails, otherwise an empty String.
     */
    public String establishConnection() {
        String msg = "";
        if (socket == null) {
            try {
                socket = new Socket(IPaddr, portNum);
                System.out.println("Connected to " + IPaddr + ":" + Integer.toString(portNum));
                din = socket.getInputStream();
                dout = new PrintStream(socket.getOutputStream());


            } catch (UnknownHostException ex) {
                System.out.println("UnknownHostException in ConnectionSocket connectToRPI Function");
                msg = "Failed to Connect RPI : " + ex.getMessage();
            } catch (IOException ex) {
                System.out.println("IOException in ConnectionSocket connectToRPI Function");

                msg = "Failed to Connect RPI : " + ex.getMessage();
            }
        }
        return msg;
    }

    /**
     * This method write message to the RPI through the established connection via socket
     *
     * @param message The string message to be written to RPI
     */
    public void sendMessage(String message) {
        try {

            dout.write(message.getBytes());
            dout.flush();

        } catch (IOException IOEx) {
            System.out.println("IOException in ConnectionSocket sendMessage Function");
        }
    }

    /**
     * This method read message received from RPi through the established connection via socket.
     *
     * @return String message that was received.
     * @throws InterruptedException If the connection gets interrupted.
     */
    public String readMessage() throws InterruptedException {

        byte[] byteData = new byte[512];
        try {
            int size = 0;

            din.read(byteData);

            // This is to get rid of junk bytes
            while (size < 512) {
                if (byteData[size] == 0) {
                    break;
                }
                size++;
            }
            String message = new String(byteData, 0, size, "UTF-8");

            return message;
        } catch (IOException IOEx) {
            System.out.println("IOException in ConnectionSocket receiveMessage Function");
            throw new InterruptedException("TCP ReadMsg() Exception");
        }


    }

    /**
     * This method closes the established socket connection to RPI.
     */
    public void closeConnection() {
        if (socket != null) {
            try {
                socket.close();
                din.close();
                dout.close();
                dout = null;
                socket = null;
                din = null;

                System.out.println("Successfully closed the ConnectionSocket.");
            } catch (IOException IOEx) {
                System.out.println("IOException in ConnectionSocket closeConnection Function");
            }
        }
    }

}
