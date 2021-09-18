package communication;

import java.util.Scanner;

/**
 * @author Nicholas Yeo Ming Jie
 * @author Neo Zhao Wei
 * @author David Loh Shun Hao
 * @version 1.0
 * @since 2020-10-27
 */
public class Client {

    /**
     * This method is used to ensure that the simulator is able to communicate by sending
     * and receiving messages from the RPI.
     *
     * @param args
     */
    public static void main(String[] args) {

        TCPComm tcpObj = TCPComm.getInstance();
        System.out.println(tcpObj);
        tcpObj.establishConnection();
        System.out.println("Start");
        //Scanner sc = new Scanner(System.in);
        //String enteredMsg = "";
        tcpObj.sendMessage("hello!");

		/*
		 * while (!enteredMsg.equals("end")) { System.out.println("Enter message: ");
		 * enteredMsg = sc.nextLine(); tcpObj.sendMessage(enteredMsg);
		 * System.out.println("Waiting for reply.."); //tcpObj.readMessage(); }
		 */
        //tcpObj.closeConnection();
    }
}
