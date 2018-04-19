package EncryptionLayer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Base64;

public class AsymmetricEncryption {

    private Cipher cipher;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Creates a new AsymmetricEncryption. By doing this it
     * creates a new RSA cipher and generates a pair of
     * public and private key, which are used for the
     * encryption.
     * @throws NoSuchPaddingException i
     * @throws NoSuchAlgorithmException
     */
    public AsymmetricEncryption() throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.cipher = Cipher.getInstance("RSA");
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512);
        KeyPair kp = kpg.generateKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    /**
     * Returns the privateKey
     * @return the privateKey
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * Returns the publicKey
     * @return the publicKey
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Returns the bytes of the publicKey
     * @return
     */
    public byte[] getPublicKeyBytes() {
        return getPublicKey().getEncoded();
    }

    /**
     * Encrypts a message using the privateKey. It first initializes
     * the cipher and the using the cipher and base64 encoding the
     * message is encoded.
     * @param message we want to encode
     * @return the encoded message as a string
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encryptMessage(String message) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
        return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes()));
    }

    /**
     * Decrypts the messge using the publicKey. It does the same steps
     * as the encryption but it first uses the base64 decode then the
     * cipher.
     * @param message we want to decrypt
     * @param pKey the public key of the encryption
     * @return
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String decryptMessage(String message, PublicKey pKey) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.DECRYPT_MODE, pKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(message)));
    }
}