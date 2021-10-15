package communication;

import java.util.Scanner;

/**
 * @author Goh Cheng Guan, Clive
 * @author Lau Zhen Jie
 * @version 1.0
 * @since 2021-9-15
 */
public class Client {

    /**
     * This method is used to ensure that the simulator is able to communicate by sending
     * and receiving messages from the RPI.
     *
     * @param args
     */
    public static void main(String[] args) {

        TCPComm2 tcpObj = TCPComm2.getInstance();
        System.out.println(tcpObj);
        tcpObj.establishConnection();
        System.out.println("Start");
        Scanner sc = new Scanner(System.in);
        String enteredMsg = "";
        tcpObj.sendMessage("hello!");

		/*
		 * while (!enteredMsg.equals("end")) { System.out.println("Enter message: ");
		 * enteredMsg = sc.nextLine(); tcpObj.sendMessage(enteredMsg);
		 * System.out.println("Waiting for reply.."); //tcpObj.readMessage(); }
		 */
        //tcpObj.closeConnection();
    }
}
