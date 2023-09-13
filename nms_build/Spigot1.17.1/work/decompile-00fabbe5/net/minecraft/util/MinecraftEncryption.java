package net.minecraft.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MinecraftEncryption {

    private static final String SYMMETRIC_ALGORITHM = "AES";
    private static final int SYMMETRIC_BITS = 128;
    private static final String ASYMMETRIC_ALGORITHM = "RSA";
    private static final int ASYMMETRIC_BITS = 1024;
    private static final String BYTE_ENCODING = "ISO_8859_1";
    private static final String HASH_ALGORITHM = "SHA-1";

    public MinecraftEncryption() {}

    public static SecretKey a() throws CryptographyException {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");

            keygenerator.init(128);
            return keygenerator.generateKey();
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

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

    public static PublicKey a(byte[] abyte) throws CryptographyException {
        try {
            X509EncodedKeySpec x509encodedkeyspec = new X509EncodedKeySpec(abyte);
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");

            return keyfactory.generatePublic(x509encodedkeyspec);
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    public static SecretKey a(PrivateKey privatekey, byte[] abyte) throws CryptographyException {
        byte[] abyte1 = b(privatekey, abyte);

        try {
            return new SecretKeySpec(abyte1, "AES");
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    public static byte[] a(Key key, byte[] abyte) throws CryptographyException {
        return a(1, key, abyte);
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
