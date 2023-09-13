package net.minecraft.core;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.SystemUtils;

public final class MinecraftSerializableUUID {

    public static final Codec<UUID> a = Codec.INT_STREAM.comapFlatMap((intstream) -> {
        return SystemUtils.a(intstream, 4).map(MinecraftSerializableUUID::a);
    }, (uuid) -> {
        return Arrays.stream(a(uuid));
    });

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
}
