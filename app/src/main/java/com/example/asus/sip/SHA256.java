package com.example.asus.sip;

import java.security.*;

public class SHA256 {
    public static String calculateHash(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();

        } catch (Exception e) {
            return null;
        }
    }
}
