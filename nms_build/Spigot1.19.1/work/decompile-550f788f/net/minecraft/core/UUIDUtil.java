package net.minecraft.core;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.SystemUtils;

public final class UUIDUtil {

    public static final Codec<UUID> CODEC = Codec.INT_STREAM.comapFlatMap((intstream) -> {
        return SystemUtils.fixedSize(intstream, 4).map(UUIDUtil::uuidFromIntArray);
    }, (uuid) -> {
        return Arrays.stream(uuidToIntArray(uuid));
    });
    public static final int UUID_BYTES = 16;
    private static final String UUID_PREFIX_OFFLINE_PLAYER = "OfflinePlayer:";

    private UUIDUtil() {}

    public static UUID uuidFromIntArray(int[] aint) {
        return new UUID((long) aint[0] << 32 | (long) aint[1] & 4294967295L, (long) aint[2] << 32 | (long) aint[3] & 4294967295L);
    }

    public static int[] uuidToIntArray(UUID uuid) {
        long i = uuid.getMostSignificantBits();
        long j = uuid.getLeastSignificantBits();

        return leastMostToIntArray(i, j);
    }

    private static int[] leastMostToIntArray(long i, long j) {
        return new int[]{(int) (i >> 32), (int) i, (int) (j >> 32), (int) j};
    }

    public static byte[] uuidToByteArray(UUID uuid) {
        byte[] abyte = new byte[16];

        ByteBuffer.wrap(abyte).order(ByteOrder.BIG_ENDIAN).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        return abyte;
    }

    public static UUID readUUID(Dynamic<?> dynamic) {
        int[] aint = dynamic.asIntStream().toArray();

        if (aint.length != 4) {
            throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + aint.length + ".");
        } else {
            return uuidFromIntArray(aint);
        }
    }

    public static UUID getOrCreatePlayerUUID(GameProfile gameprofile) {
        UUID uuid = gameprofile.getId();

        if (uuid == null) {
            uuid = createOfflinePlayerUUID(gameprofile.getName());
        }

        return uuid;
    }

    public static UUID createOfflinePlayerUUID(String s) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + s).getBytes(StandardCharsets.UTF_8));
    }
}
