package ro.any.c12153.shared;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author C12153
 */
public final class Crypto {
    
    private static final String ENV_VAR_NAME = "APPS_KEY";
    private static SecretKeySpec key;

    static {
        try {
            byte[] envVar = System.getenv(ENV_VAR_NAME).getBytes(StandardCharsets.UTF_8);
            byte[] hash = Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(envVar), 16);
            key = new SecretKeySpec(hash, "AES");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String encrypt(String stringToEncrypt) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8)));
    }
    
    public static String decrypt(String stringToDecrypt) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
    }
}
