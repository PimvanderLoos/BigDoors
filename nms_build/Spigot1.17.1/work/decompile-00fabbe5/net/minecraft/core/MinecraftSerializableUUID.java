package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.SystemUtils;

public final class MinecraftSerializableUUID {

    public static final Codec<UUID> CODEC = Codec.INT_STREAM.comapFlatMap((intstream) -> {
        return SystemUtils.a(intstream, 4).map(MinecraftSerializableUUID::a);
    }, (uuid) -> {
        return Arrays.stream(a(uuid));
    });

    private MinecraftSerializableUUID() {}

    public static UUID a(int[] aint) {
        return new UUID((long) aint[0] << 32 | (long) aint[1] & 4294967295L, (long) aint[2] << 32 | (long) aint[3] & 4294967295L);
    }

    public static int[] a(UUID uuid) {
        long i = uuid.getMostSignificantBits();
        long j = uuid.getLeastSignificantBits();

        return a(i, j);
    }

    private static int[] a(long i, long j) {
        return new int[]{(int) (i >> 32), (int) i, (int) (j >> 32), (int) j};
    }

    public static UUID a(Dynamic<?> dynamic) {
        int[] aint = dynamic.asIntStream().toArray();

        if (aint.length != 4) {
            throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + aint.length + ".");
        } else {
            return a(aint);
        }
    }
}
