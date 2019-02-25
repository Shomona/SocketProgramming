/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project2task5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author shomonamukherjee
 */
public class Client {

    BigInteger n; // n is the modulus for both the private and public keys
    BigInteger e; // e is the exponent of the public key
    BigInteger d; // d is the exponent of the private key

    //Initializes the socket and output stream
    public Client() throws UnknownHostException, IOException {
        //Get ip address of the localhost ie the system the client is running on
        InetAddress aHost = InetAddress.getByName("localhost");

        //Assign server Port
        int serverPort = 6789;

        //Created a TCP Socket object
        try {
            clientSocket = new Socket(aHost, serverPort);
        }//To handle absence of server listening at the port
        catch (Exception e) {
            System.out.println("Server not listening yet! Please start the server");
            System.exit(0);
        }

        //Create an object output stream to communicate with the server in terms of objects
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    Socket clientSocket = null;
    ObjectOutputStream outputStream = null;

    public static void main(String args[]) throws IOException, NoSuchAlgorithmException, Exception {

        Scanner in = new Scanner(System.in);
        Client c = new Client();

        try {

            //Show client is active now 
            System.out.println("Client Running");

            //Create RSA keys
            c.createKeys();

            //Create id for this client
            String id = Hash.ComputeSHA_256_as_Hex_String((c.e.add(c.n)).toString());
            //Extract the least significant 20 bytes
            id = id.substring(id.length() - 40, id.length());

            //Keep sending data to server
            while (true) {

                //Menu
                //Enter the operation
                System.out.println("Enter your operation: add, subtract, view or exit");
                String operation = in.nextLine();

                //Error handling for operation
                if (!(operation.equalsIgnoreCase("add") || operation.equalsIgnoreCase("subtract") || operation.equalsIgnoreCase("view") || operation.equalsIgnoreCase("exit"))) {
                    System.out.println("Did not enter a valid operation. System Quit. Please restart!");
                    System.exit(0);
                }

                //Exit the system if the operation is exit
                if (operation.equalsIgnoreCase("exit")) {
                    System.exit(0);
                }

                //Initialize value
                int value = 0;
                //Enter the value if the operation is not view
                if (!operation.equalsIgnoreCase("view")) {
                    System.out.println("Enter your value");

                    try {
                        value = Integer.parseInt(in.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Did not enter a numerical id. System Quit. Please restart!");
                        System.exit(0);
                    }
                }

                //Create sign
                Sign s = new Sign(c.e, c.d, c.n);
                String sign = s.sign(id + c.e.toString() + c.n.toString() + operation + value);

                //Create the data to be sent
                Data data = new Data(id, c.e, c.n, operation, value, sign);

                System.out.println("Client sending data ");
                //Sending data to the client
                String response = c.sendData(data);

                //Print the reply to to the output console
                System.out.println("Reply: " + response);
            }
            //If any socket exception occurs print the message
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
            //If any I/0 Exception occurs print the message
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            //Irrespective of any exception if the socket is initialized close it 
        } finally {
            if (c.clientSocket != null) {
                c.clientSocket.close();
            }
        }
    }

    public String sendData(Data data) throws SocketException, IOException {

        //Write the data to the output stream
        outputStream.writeObject(data);

        //Open an input stream from the server
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //Response from the server
        String response = (in.readLine()).replaceAll("\0", "");
        return response;
    }

    /**
     * Create RSA keys. Referenced from RSA.java provided in class
     */
    public void createKeys() {

        Random rnd = new Random();

        // Step 1: Generate two large random primes.
        // We use 400 bits here, but best practice for security is 2048 bits.
        // Change 400 to 2048, recompile, and run the program again and you will
        // notice it takes much longer to do the math with that many bits.
        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);

        // Step 2: Compute n by the equation n = p * q.
        n = p.multiply(q);

        // Step 3: Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Step 4: Select a small odd integer e that is relatively prime to phi(n).
        // By convention the prime 65537 is used as the public exponent.
        e = new BigInteger("65537");

        // Step 5: Compute d as the multiplicative inverse of e modulo phi(n).
        d = e.modInverse(phi);
    }

}
