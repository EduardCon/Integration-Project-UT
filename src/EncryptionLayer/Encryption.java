package EncryptionLayer;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class Encryption {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int RANDOM_BYTES_LENGTH = 16;
    private static final String MAC_ALGORITHM = "HMACSHA256";
    private static final String STRING_ENCODING = "ISO_8859_1";
    private static final String HEX_AES_KEY =
            "B22E2B9A77C6DE2B9A779E7B2C6DA76E51C829E725EC8478A76E51C825EC8478";
    private static final String HEX_MAC_KEY = "AEB908AA1CEDFFDEA1F255640A05EEF6";

    public Encryption() {}

    public String encrypt(String plainText, String hexAesKey, String hexMacKey, String macAlgorithm)
            throws Exception {
        // compute the cipher
        byte[] decodedHexAesKey = DatatypeConverter.parseHexBinary(hexAesKey);
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodedHexAesKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] paddingBytes = new byte[RANDOM_BYTES_LENGTH];
        secureRandom.nextBytes(paddingBytes);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(paddingBytes));
        byte[] encryptedText = cipher.doFinal(plainText.getBytes(STRING_ENCODING));

        // Prepend random to the encryptedText
        byte[] paddedCipher = concatByteArrays(paddingBytes, encryptedText);

        // Append message digest
        byte[] digest = computeDigest(paddedCipher, hexMacKey, macAlgorithm);
        byte[] completeText = concatByteArrays(paddedCipher, digest);

        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(completeText);
    }

    public String decrypt(String encryptedText, String hexAesKey, String hexMacKey,
                          String macAlgorithm) throws Exception {
        BASE64Decoder base64decoder = new BASE64Decoder();
        int macLength = Mac.getInstance(macAlgorithm).getMacLength();
        byte[] completeBytes = base64decoder.decodeBuffer(encryptedText);
        int macStartIndex = completeBytes.length - macLength;
        byte[] padding = Arrays.copyOfRange(completeBytes, 0, RANDOM_BYTES_LENGTH);
        byte[] paddedCipher = Arrays.copyOfRange(completeBytes, 0, macStartIndex);
        byte[] encryptedBytes = Arrays.copyOfRange(completeBytes, RANDOM_BYTES_LENGTH, macStartIndex);
        byte[] digestBytes = Arrays.copyOfRange(completeBytes, macStartIndex, completeBytes.length);


        byte[] computedDigest = computeDigest(paddedCipher, hexMacKey, macAlgorithm);
//        if (!MessageDigest.isEqual(digestBytes, computedDigest)) {
        //          throw new RuntimeException("Message corrupted");
        //    }

        byte[] decodedHexAesKey = DatatypeConverter.parseHexBinary(hexAesKey);
        SecretKeySpec keySpec = new SecretKeySpec(decodedHexAesKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(padding));

        String plaintext = new String(cipher.doFinal(encryptedBytes), STRING_ENCODING);
        return plaintext;
    }

    private byte[] concatByteArrays(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    private byte[] computeDigest(byte[] message, String hexMacKey, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] decodedHexMacKey = DatatypeConverter.parseHexBinary(hexMacKey);
        SecretKeySpec secretKeySpc = new SecretKeySpec(decodedHexMacKey, algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpc);
        byte[] digest = mac.doFinal(message);
        return digest;
    }
}