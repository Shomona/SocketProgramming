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
 * Referenced from BabyVerify.java provided in class
 */
public class Verify {

    private BigInteger e, n;

    /**
     * For verifying, a SignOrVerify object may be constructed with a RSA's e
     * and n. Only e and n are used for signature verification.
     */
    public Verify(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }

    /**
     * Verifying proceeds as follows: 1) Decrypt the encryptedHash to compute a
     * decryptedHash 2) Hash the messageToCheck using SHA-256 (be sure to handle
     * the extra byte as described in the signing method.) 3) If this new hash
     * is equal to the decryptedHash, return true else false.
     *
     * @param messageToCheck a normal string (4 hex digits) that needs to be
     * verified.
     * @param encryptedHashStr integer string - possible evidence attesting to
     * its origin.
     * @return true or false depending on whether the verification was a success
     * @throws Exception
     */
    public boolean verify(String messageToCheck, String encryptedHashStr) throws Exception {

        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(encryptedHashStr);
        // Decrypt it
        BigInteger decryptedHash = encryptedHash.modPow(e, n);

        // Get the bytes from messageToCheck
        byte[] bytesOfMessageToCheck = messageToCheck.getBytes("UTF-8");

        // compute the digest of the message with SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // messageToCheckDigest is a full SHA-256 digest
        byte[] messageToCheckDigest = md.digest(bytesOfMessageToCheck);

        byte[] extraByte = new byte[messageToCheckDigest.length + 1];
        extraByte[0] = 0;

        for (int i = 0; i < messageToCheckDigest.length; i++) {

            extraByte[i + 1] = messageToCheckDigest[i];

        }

        // Make it a big int
        BigInteger bigIntegerToCheck = new BigInteger(extraByte);

        // inform the client on how the two compare
        if (bigIntegerToCheck.compareTo(decryptedHash) == 0) {

            return true;
        } else {
            return false;
        }
    }

    // from Stack overflow
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
