package EncryptionLayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class GenerateKeys {
    
    private KeyPairGenerator keyGenerator;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    public GenerateKeys(int keyLength) throws NoSuchAlgorithmException {
        this.keyGenerator = KeyPairGenerator.getInstance("RSA");
        this.keyGenerator.initialize(keyLength);
    }
    
    public void createKeys() {
        this.pair = this.keyGenerator.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }
    
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
    
    public static void main(String[] args){
        GenerateKeys generate;
        try {
            generate = new GenerateKeys(2048);
            generate.createKeys();
            generate.writeToFile("KeyPair/publicKey", generate.getPublicKey().getEncoded());
            generate.writeToFile("KeyPair/privateKey", generate.getPrivateKey().getEncoded());
        }catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }
}
