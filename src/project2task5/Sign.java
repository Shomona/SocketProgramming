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
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Referenced from BabySign.java provided in class
 */
public class Sign {

    private BigInteger e, d, n;

    public Sign(BigInteger e, BigInteger d, BigInteger n) {
        this.e = e;
        this.d = d;
        this.n = n;
    }

    /**
     * Signing proceeds as follows: 1) Get the bytes from the string to be
     * signed. 2) Compute a SHA-1 digest of these bytes. 3) Copy these bytes
     * into a byte array that is one byte longer than needed. The resulting byte
     * array has its extra byte set to zero. This is because RSA works only on
     * positive numbers. The most significant byte (in the new byte array) is
     * the 0'th byte. It must be set to zero. 4) Create a BigInteger from the
     * byte array. 5) Encrypt the BigInteger with RSA d and n. 6) Return to the
     * caller a String representation of this BigInteger.
     *
     * @param message a sting to be signed
     * @return a string representing a big integer - the encrypted hash.
     * @throws Exception
     */
    public String sign(String message) throws Exception {

        // compute the digest with SHA-256
        byte[] bytesOfMessage = message.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);

        byte[] messageDigest = new byte[bigDigest.length + 1];

        // the value to be signed non-negative.
        // we add a 0 byte as the most significant byte to keep
        messageDigest[0] = 0;

        for (int i = 0; i < bigDigest.length; i++) {

            messageDigest[i + 1] = bigDigest[i];

        }

        // From the digest, create a BigInteger
        BigInteger m = new BigInteger(messageDigest);

        // encrypt the digest with the private key
        BigInteger c = m.modPow(d, n);

        // return this as a big integer string
        return c.toString();
    }

    // From Stack overflow
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
