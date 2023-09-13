package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;

public class RegionFileCompression {

    private static final Int2ObjectMap<RegionFileCompression> d = new Int2ObjectOpenHashMap();
    public static final RegionFileCompression a = a(new RegionFileCompression(1, GZIPInputStream::new, GZIPOutputStream::new));
    public static final RegionFileCompression b = a(new RegionFileCompression(2, InflaterInputStream::new, DeflaterOutputStream::new));
    public static final RegionFileCompression c = a(new RegionFileCompression(3, (inputstream) -> {
        return inputstream;
    }, (outputstream) -> {
        return outputstream;
    }));
    private final int e;
    private final RegionFileCompression.a<InputStream> f;
    private final RegionFileCompression.a<OutputStream> g;

    private RegionFileCompression(int i, RegionFileCompression.a<InputStream> regionfilecompression_a, RegionFileCompression.a<OutputStream> regionfilecompression_a1) {
        this.e = i;
        this.f = regionfilecompression_a;
        this.g = regionfilecompression_a1;
    }

    private static RegionFileCompression a(RegionFileCompression regionfilecompression) {
        RegionFileCompression.d.put(regionfilecompression.e, regionfilecompression);
        return regionfilecompression;
    }

    @Nullable
    public static RegionFileCompression a(int i) {
        return (RegionFileCompression) RegionFileCompression.d.get(i);
    }

    public static boolean b(int i) {
        return RegionFileCompression.d.containsKey(i);
    }

    public int a() {
        return this.e;
    }

    public OutputStream a(OutputStream outputstream) throws IOException {
        return (OutputStream) this.g.wrap(outputstream);
    }

    public InputStream a(InputStream inputstream) throws IOException {
        return (InputStream) this.f.wrap(inputstream);
    }

    @FunctionalInterface
    interface a<O> {

        O wrap(O o0) throws IOException;
    }
}
