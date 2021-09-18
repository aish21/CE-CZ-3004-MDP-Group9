package communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Nicholas Yeo Ming Jie
 * @author Neo Zhao Wei
 * @author David Loh Shun Hao
 * @version 1.0
 * @since 2020-10-27
 */
public class TCPComm {

    private static final String IPaddr = "192.168.9.9";
    private static final int portNum = 5182;
    private static TCPComm tcpObj = null;
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;

    /**
     * private default constructor
     */
    private TCPComm() {
    }

    /**
     * This method return a singleton object of this class
     *
     * @return TCPcomm object
     */
    public static TCPComm getInstance() {
        if (tcpObj == null) {
            tcpObj = new TCPComm();
        }

        return tcpObj;
    }

    /**
     * This method attempts to establish socket connect via the specified RPI address and port.
     *
     * @return Error message if the connections fails, otherwise an empty String.
     */
    public String establishConnection() {
        String msg = "";
        System.out.println("1");
        try {
        	System.out.println("2");
            clientSocket = new Socket(IPaddr, portNum);
            System.out.println("hello: " + clientSocket);
            this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
            this.inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception ex) {

            msg = "Failed to Connect RPI : " + ex.getMessage();
        }


        return msg;
    }

    /**
     * This method closes the established socket connection to RPI.
     */
    public void closeConnection() {

        if (clientSocket != null)
            System.out.println("Socket status before close: " + clientSocket.isClosed());
        else {
            System.out.println("Socket status before close: socket is null");
        }
        if (clientSocket != null) {
            try {
                clientSocket.close();
                outputStream.close();
                inputStream.close();
                clientSocket = null;
                outputStream = null;
                inputStream = null;
                System.out.println("Successfully closed connection.");

            } catch (IOException ex) {

                System.out.println("Closing connection error: " + ex.getMessage());
            }

        }

    }

    /**
     * This method write message to the RPI through the established connection via socket
     *
     * @param msg The string message to be written to RPI
     * @return The message string sent, otherwise an error message.
     */
    public String sendMessage(String msg) {
        String rmsg = "";
        try {
            this.outputStream.writeBytes(msg + "!");
            rmsg = msg;
        } catch (Exception ex) {
            System.out.println("TCPComm sendmsg() Exception: " + ex.getMessage());
            rmsg = ex.getMessage();
        }

        return rmsg;
    }

    /**
     * This method read message received from RPi through the established connection via socket.
     *
     * @return String message that was received.
     * @throws InterruptedException If the connection gets interrupted.
     */
    public String readMessage() throws InterruptedException {
        String receivedMsg = "";
        try {
            receivedMsg = this.inputStream.readLine();
        } catch (IOException ex) {

            System.out.println("TCP ReadMsg() Exception: " + receivedMsg + ":" + ex.getMessage());
            throw new InterruptedException("TCP ReadMsg() Exception");
        }
        return receivedMsg;

    }

}
