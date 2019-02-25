/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project2task5;

/**
 *
 * @author shomonamukherjee
 */
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Server {

    static int sum = 0;

    public static void main(String args[]) throws ClassNotFoundException, IOException, Exception {

       ServerSocket aSocket = null;

        try {

            //Create a TCP Socket and listen at port 6789 
            aSocket = new ServerSocket(6789);
            //Receive the client request at the socket
            Socket clientSocket = aSocket.accept();


            //Map to store key value pairs ie associate client ids with their state
            Map<String, Integer> hm = new HashMap<>();

            //Show client is active now 
            System.out.println("Server Running");

            //Open an object input stream to accept data from client
            ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
            //PrintWriter to pass string values to the client
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            //Keep listening on the socket
            while (true) {
                //Data object read from the object input stream from client
                Data data = (Data) inStream.readObject();
                //Stores the response string to be sent back to the client
                String responseString = null;

                /**
                 * If data is verified then perform the operation
                 * If key exists in the hash map then replace the state
                 * If key doesn't exist then add a key and state to the map 
                 */
                if (verify(data)) {

                    switch (data.operation) {

                        case "add":
                            if (hm.containsKey(data.id)) {
                                int newVal = hm.get(data.id) + data.value;
                                hm.put(data.id, newVal);

                            } else {
                                hm.put(data.id, data.value);
                            }
                            responseString = "OK";
                            break;
                        case "subtract":
                            if (hm.containsKey(data.id)) {
                                int newVal = hm.get(data.id) - data.value;
                                hm.put(data.id, newVal);

                            } else {
                                hm.put(data.id, 0 - data.value);
                            }
                            responseString = "OK";
                            break;
                        case "view":
                            if(hm.containsKey(data.id))
                            responseString = Integer.toString(hm.get(data.id));
                            else
                            responseString = "Nothing to view for this id yet!";

                    }
                } else {
                    responseString = "Error in request";
                }

                //Print echo string on the server
                System.out.println("Echoing: " + responseString);
                //Send the response string to the client
                out.println(responseString);
                out.flush();
            }
            //If any socket exception occurs print the message
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
            //If any I/0 Exception occurs print the message
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            //Irrespective of any exception if the socket is initialized close it 
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }
    }
/**
 * 
 * @param data
 * @return boolean value based on the truth value of the id and sign
 * @throws Exception 
 */
    public static boolean verify(Data data) throws Exception {
        
        boolean verified = false;
        
        //Compute the hashed id from the public key and check if its the same as id passed
        String id = Hash.ComputeSHA_256_as_Hex_String((data.e.add(data.n)).toString());
        id = id.substring(id.length() - 40, id.length());

        if (id.equals(data.id)) {
            //If the ids match verify the signature
            Verify verifySig = new Verify(data.e, data.n);
            
            //Call verify from Verify class to check if the signature is correct
            if (verifySig.verify(data.id + data.e.toString() + data.n.toString() + data.operation + data.value, data.sign)) {
                
                verified = true;

            }
            
            else{
                verified = false;
            }
        }
        return verified;

    }

}
