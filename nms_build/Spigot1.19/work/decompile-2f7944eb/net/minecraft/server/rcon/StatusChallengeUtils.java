package net.minecraft.server.rcon;

import java.nio.charset.StandardCharsets;

public class StatusChallengeUtils {

    public static final int MAX_PACKET_SIZE = 1460;
    public static final char[] HEX_CHAR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public StatusChallengeUtils() {}

    public static String stringFromByteArray(byte[] abyte, int i, int j) {
        int k = j - 1;

        int l;

        for (l = i > k ? k : i; 0 != abyte[l] && l < k; ++l) {
            ;
        }

        return new String(abyte, i, l - i, StandardCharsets.UTF_8);
    }

    public static int intFromByteArray(byte[] abyte, int i) {
        return intFromByteArray(abyte, i, abyte.length);
    }

    public static int intFromByteArray(byte[] abyte, int i, int j) {
        return 0 > j - i - 4 ? 0 : abyte[i + 3] << 24 | (abyte[i + 2] & 255) << 16 | (abyte[i + 1] & 255) << 8 | abyte[i] & 255;
    }

    public static int intFromNetworkByteArray(byte[] abyte, int i, int j) {
        return 0 > j - i - 4 ? 0 : abyte[i] << 24 | (abyte[i + 1] & 255) << 16 | (abyte[i + 2] & 255) << 8 | abyte[i + 3] & 255;
    }

    public static String toHexString(byte b0) {
        char c0 = StatusChallengeUtils.HEX_CHAR[(b0 & 240) >>> 4];

        return c0 + StatusChallengeUtils.HEX_CHAR[b0 & 15];
    }
}
