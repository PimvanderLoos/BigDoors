package net.minecraft.util;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MinecraftEncryption {

    public static KeyPair b() throws CryptographyException {
        try {
            KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");

            keypairgenerator.initialize(1024);
            return keypairgenerator.generateKeyPair();
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    public static byte[] a(String s, PublicKey publickey, SecretKey secretkey) throws CryptographyException {
        try {
            return a(s.getBytes("ISO_8859_1"), secretkey.getEncoded(), publickey.getEncoded());
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    private static byte[] a(byte[]... abyte) throws Exception {
        MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");
        byte[][] abyte1 = abyte;
        int i = abyte.length;

        for (int j = 0; j < i; ++j) {
            byte[] abyte2 = abyte1[j];

            messagedigest.update(abyte2);
        }

        return messagedigest.digest();
    }

    public static SecretKey a(PrivateKey privatekey, byte[] abyte) throws CryptographyException {
        byte[] abyte1 = b(privatekey, abyte);

        try {
            return new SecretKeySpec(abyte1, "AES");
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    public static byte[] b(Key key, byte[] abyte) throws CryptographyException {
        return a(2, key, abyte);
    }

    private static byte[] a(int i, Key key, byte[] abyte) throws CryptographyException {
        try {
            return a(i, key.getAlgorithm(), key).doFinal(abyte);
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    private static Cipher a(int i, String s, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(s);

        cipher.init(i, key);
        return cipher;
    }

    public static Cipher a(int i, Key key) throws CryptographyException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");

            cipher.init(i, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }
}
