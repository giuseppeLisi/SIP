package com.example.asus.sip;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class BlowfishChiper {

    public static String encryption(String plainText, String key){
        try {
            SecretKeySpec KS = new SecretKeySpec(key.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, KS);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.encodeToString(encrypted, Base64.NO_PADDING);
        } catch (Exception e) {
            return "ERROR:"+e.getMessage();
        }
    }

    public static String decryption(String chiperText, String key){
        try {

            SecretKeySpec KS = new SecretKeySpec(key.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, KS);
            byte[] decrypted = cipher.doFinal(Base64.decode(chiperText, Base64.NO_PADDING));
            return new String(decrypted);
        } catch (Exception e) {
            return "ERROR";
        }
    }
}