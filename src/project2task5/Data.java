/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project2task5;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * @author shomonamukherjee
 */
//Class to pass data between sockets in object format
public class Data implements Serializable {
    
    String id; // id of the client
    BigInteger e; // public key component
    BigInteger n; // public key component
    String operation; // operation to be performed by server
    int value; // value passed to the server
    String sign; // Sign of the client
    
    //Constructor to initialize
    public Data(String id, BigInteger e, BigInteger n, String operation, int value, String sign){
        
        this.id = id;
        this.e = e;
        this.n = n;
        this.operation = operation;
        this.value = value;
        this.sign = sign;
        
    }
    
}