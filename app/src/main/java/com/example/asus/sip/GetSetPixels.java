package com.example.asus.sip;

import android.graphics.Bitmap;
import android.graphics.Color;


//Utility for set and get a pixels value into bitmap image
public class GetSetPixels {

    //Metodo per inserire il testo
    public static void setStego(Bitmap img, String password) {

        //get image width, height and the first pixel for insert data
        int width = img.getWidth();
        int height = img.getHeight();
        byte[] passwordBytes = password.getBytes();
        String digest = SHA256.calculateHash(password);
        int somma = 0;
        for (int i : passwordBytes) {
            somma += i;
        }

        /*jump contiene il primo pixel dove inserire il primo carattere
         * i restanti salti saranno determinati dal pixel successivo
         * pointer viene utilizzato come puntatore al pixel
         */
        int pointer = 0,
        b=0;
        int offset=3;
        int jump = (int) (somma / passwordBytes.length);
        byte[] textByte = HexStringToByteArray(digest);

        //Cicli annidati per scansionare l'immagine utilizzando il pointer ed inserire il digest
            for (int y = 0; y < height && b<textByte.length; y++) {
                for (int x = 0; x < width && b<textByte.length; x++) {
                    if (pointer == jump) {
                        //Utilizzo il valore della componente blue con tecnica LSB su 2 bit
                        int blue = Color.blue(img.getPixel(x,y));
                        blue = ((blue & 252) | extractBitToString(textByte, b, offset)); //inserire il valore
                        //img.setPixel(x, y, blue);
                        if (offset<128) {
                            offset *= 4; //punto alla coppia dei due bit successivi
                        }
                        else {
                            b++;
                            offset=0;
                        }
                        //controlli per la verifica dei confini dell'immagine
                        if (x+1 < width-1) {
                            jump = jump + Color.blue(img.getPixel(x++, y));
                        }
                        else if (y+1 < height) {
                            jump = jump + Color.blue(img.getPixel(0, y++));
                        }
                    }
                pointer++;
                }
            }
    }

    /* Metodo che si occupa di estrapolare 2 bit per volta da una stringa
     * suddivisa in byte. Ritorna due bit per volta sin quando non termina la
     * stringa passata come argomento (texttoinject)
     */
    public static int extractBitToString (byte[] textByte, int b, int offset) {
        //selectbit contiene l'i-esima coppia di bit del byte della stringa
        int selectbit = (textByte[b] & (~offset)) | offset; //Con & cancello con | setto i bit
        return selectbit;
    }

    //Funzione per la conversione di una stringa Hex in un array di Byte dec
    public static byte[] HexStringToByteArray(String s) {
        byte data[] = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = ((Integer.decode("+0x" + s.charAt(i) + s.charAt(i + 1))).byteValue());
        }
        return data;
    }
}