package EncryptionLayer;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;
import java.util.Random;

public class Encryption {

    private String sessionPassword;

    public Encryption() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        this.sessionPassword = salt.toString();
    }

    public String getSessionPassword() {
        return this.sessionPassword;
    }

    public String decrypt(String string) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, InvalidKeySpecException {
        SecretKeySpec key = createSecretKey();
        String iv = string.split(":")[0];
        String message = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(message)), "UTF-8");
    }

    public byte[] base64Decode(String property) {
        return Base64.getMimeDecoder().decode(property);
    }

    public String encrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, InvalidKeyException {
        SecretKeySpec key = createSecretKey();
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(message.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    public String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public SecretKeySpec createSecretKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        char[] password = getSessionPassword().toCharArray();
        if (password == null) {
            throw new IllegalArgumentException("Run with -Dpassword=<password>");
        }
        byte[] salt = new String("01234567").getBytes();
        int iteration = 40000;
        int keyLength = 128;
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iteration, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }
}