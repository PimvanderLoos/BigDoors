package net.minecraft.util;

import com.google.common.primitives.Longs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.network.PacketDataSerializer;

public class MinecraftEncryption {

    private static final String SYMMETRIC_ALGORITHM = "AES";
    private static final int SYMMETRIC_BITS = 128;
    private static final String ASYMMETRIC_ALGORITHM = "RSA";
    private static final int ASYMMETRIC_BITS = 1024;
    private static final String BYTE_ENCODING = "ISO_8859_1";
    private static final String HASH_ALGORITHM = "SHA-1";
    public static final String SIGNING_ALGORITHM = "SHA256withRSA";
    public static final int SIGNATURE_BYTES = 256;
    private static final String PEM_RSA_PRIVATE_KEY_HEADER = "-----BEGIN RSA PRIVATE KEY-----";
    private static final String PEM_RSA_PRIVATE_KEY_FOOTER = "-----END RSA PRIVATE KEY-----";
    public static final String RSA_PUBLIC_KEY_HEADER = "-----BEGIN RSA PUBLIC KEY-----";
    private static final String RSA_PUBLIC_KEY_FOOTER = "-----END RSA PUBLIC KEY-----";
    public static final String MIME_LINE_SEPARATOR = "\n";
    public static final Encoder MIME_ENCODER = Base64.getMimeEncoder(76, "\n".getBytes(StandardCharsets.UTF_8));
    public static final Codec<PublicKey> PUBLIC_KEY_CODEC = Codec.STRING.comapFlatMap((s) -> {
        try {
            return DataResult.success(stringToRsaPublicKey(s));
        } catch (CryptographyException cryptographyexception) {
            Objects.requireNonNull(cryptographyexception);
            return DataResult.error(cryptographyexception::getMessage);
        }
    }, MinecraftEncryption::rsaPublicKeyToString);
    public static final Codec<PrivateKey> PRIVATE_KEY_CODEC = Codec.STRING.comapFlatMap((s) -> {
        try {
            return DataResult.success(stringToPemRsaPrivateKey(s));
        } catch (CryptographyException cryptographyexception) {
            Objects.requireNonNull(cryptographyexception);
            return DataResult.error(cryptographyexception::getMessage);
        }
    }, MinecraftEncryption::pemRsaPrivateKeyToString);

    public MinecraftEncryption() {}

    public static SecretKey generateSecretKey() throws CryptographyException {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");

            keygenerator.init(128);
            return keygenerator.generateKey();
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    public static KeyPair generateKeyPair() throws CryptographyException {
        try {
            KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");

            keypairgenerator.initialize(1024);
            return keypairgenerator.generateKeyPair();
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    public static byte[] digestData(String s, PublicKey publickey, SecretKey secretkey) throws CryptographyException {
        try {
            return digestData(s.getBytes("ISO_8859_1"), secretkey.getEncoded(), publickey.getEncoded());
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    private static byte[] digestData(byte[]... abyte) throws Exception {
        MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");
        byte[][] abyte1 = abyte;
        int i = abyte.length;

        for (int j = 0; j < i; ++j) {
            byte[] abyte2 = abyte1[j];

            messagedigest.update(abyte2);
        }

        return messagedigest.digest();
    }

    private static <T extends Key> T rsaStringToKey(String s, String s1, String s2, MinecraftEncryption.a<T> minecraftencryption_a) throws CryptographyException {
        int i = s.indexOf(s1);

        if (i != -1) {
            i += s1.length();
            int j = s.indexOf(s2, i);

            s = s.substring(i, j + 1);
        }

        try {
            return minecraftencryption_a.apply(Base64.getMimeDecoder().decode(s));
        } catch (IllegalArgumentException illegalargumentexception) {
            throw new CryptographyException(illegalargumentexception);
        }
    }

    public static PrivateKey stringToPemRsaPrivateKey(String s) throws CryptographyException {
        return (PrivateKey) rsaStringToKey(s, "-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----", MinecraftEncryption::byteToPrivateKey);
    }

    public static PublicKey stringToRsaPublicKey(String s) throws CryptographyException {
        return (PublicKey) rsaStringToKey(s, "-----BEGIN RSA PUBLIC KEY-----", "-----END RSA PUBLIC KEY-----", MinecraftEncryption::byteToPublicKey);
    }

    public static String rsaPublicKeyToString(PublicKey publickey) {
        if (!"RSA".equals(publickey.getAlgorithm())) {
            throw new IllegalArgumentException("Public key must be RSA");
        } else {
            Encoder encoder = MinecraftEncryption.MIME_ENCODER;

            return "-----BEGIN RSA PUBLIC KEY-----\n" + encoder.encodeToString(publickey.getEncoded()) + "\n-----END RSA PUBLIC KEY-----\n";
        }
    }

    public static String pemRsaPrivateKeyToString(PrivateKey privatekey) {
        if (!"RSA".equals(privatekey.getAlgorithm())) {
            throw new IllegalArgumentException("Private key must be RSA");
        } else {
            Encoder encoder = MinecraftEncryption.MIME_ENCODER;

            return "-----BEGIN RSA PRIVATE KEY-----\n" + encoder.encodeToString(privatekey.getEncoded()) + "\n-----END RSA PRIVATE KEY-----\n";
        }
    }

    private static PrivateKey byteToPrivateKey(byte[] abyte) throws CryptographyException {
        try {
            PKCS8EncodedKeySpec pkcs8encodedkeyspec = new PKCS8EncodedKeySpec(abyte);
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");

            return keyfactory.generatePrivate(pkcs8encodedkeyspec);
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    public static PublicKey byteToPublicKey(byte[] abyte) throws CryptographyException {
        try {
            X509EncodedKeySpec x509encodedkeyspec = new X509EncodedKeySpec(abyte);
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");

            return keyfactory.generatePublic(x509encodedkeyspec);
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    public static SecretKey decryptByteToSecretKey(PrivateKey privatekey, byte[] abyte) throws CryptographyException {
        byte[] abyte1 = decryptUsingKey(privatekey, abyte);

        try {
            return new SecretKeySpec(abyte1, "AES");
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    public static byte[] encryptUsingKey(Key key, byte[] abyte) throws CryptographyException {
        return cipherData(1, key, abyte);
    }

    public static byte[] decryptUsingKey(Key key, byte[] abyte) throws CryptographyException {
        return cipherData(2, key, abyte);
    }

    private static byte[] cipherData(int i, Key key, byte[] abyte) throws CryptographyException {
        try {
            return setupCipher(i, key.getAlgorithm(), key).doFinal(abyte);
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    private static Cipher setupCipher(int i, String s, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(s);

        cipher.init(i, key);
        return cipher;
    }

    public static Cipher getCipher(int i, Key key) throws CryptographyException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");

            cipher.init(i, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (Exception exception) {
            throw new CryptographyException(exception);
        }
    }

    private interface a<T extends Key> {

        T apply(byte[] abyte) throws CryptographyException;
    }

    public static record b(long salt, byte[] signature) {

        public static final MinecraftEncryption.b EMPTY = new MinecraftEncryption.b(0L, ByteArrays.EMPTY_ARRAY);

        public b(PacketDataSerializer packetdataserializer) {
            this(packetdataserializer.readLong(), packetdataserializer.readByteArray());
        }

        public boolean isValid() {
            return this.signature.length > 0;
        }

        public static void write(PacketDataSerializer packetdataserializer, MinecraftEncryption.b minecraftencryption_b) {
            packetdataserializer.writeLong(minecraftencryption_b.salt);
            packetdataserializer.writeByteArray(minecraftencryption_b.signature);
        }

        public byte[] saltAsBytes() {
            return Longs.toByteArray(this.salt);
        }
    }

    public static class c {

        private static final SecureRandom secureRandom = new SecureRandom();

        public c() {}

        public static long getLong() {
            return MinecraftEncryption.c.secureRandom.nextLong();
        }
    }
}
