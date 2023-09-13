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

    private static final Int2ObjectMap<RegionFileCompression> VERSIONS = new Int2ObjectOpenHashMap();
    public static final RegionFileCompression VERSION_GZIP = a(new RegionFileCompression(1, GZIPInputStream::new, GZIPOutputStream::new));
    public static final RegionFileCompression VERSION_DEFLATE = a(new RegionFileCompression(2, InflaterInputStream::new, DeflaterOutputStream::new));
    public static final RegionFileCompression VERSION_NONE = a(new RegionFileCompression(3, (inputstream) -> {
        return inputstream;
    }, (outputstream) -> {
        return outputstream;
    }));
    private final int id;
    private final RegionFileCompression.a<InputStream> inputWrapper;
    private final RegionFileCompression.a<OutputStream> outputWrapper;

    private RegionFileCompression(int i, RegionFileCompression.a<InputStream> regionfilecompression_a, RegionFileCompression.a<OutputStream> regionfilecompression_a1) {
        this.id = i;
        this.inputWrapper = regionfilecompression_a;
        this.outputWrapper = regionfilecompression_a1;
    }

    private static RegionFileCompression a(RegionFileCompression regionfilecompression) {
        RegionFileCompression.VERSIONS.put(regionfilecompression.id, regionfilecompression);
        return regionfilecompression;
    }

    @Nullable
    public static RegionFileCompression a(int i) {
        return (RegionFileCompression) RegionFileCompression.VERSIONS.get(i);
    }

    public static boolean b(int i) {
        return RegionFileCompression.VERSIONS.containsKey(i);
    }

    public int a() {
        return this.id;
    }

    public OutputStream a(OutputStream outputstream) throws IOException {
        return (OutputStream) this.outputWrapper.wrap(outputstream);
    }

    public InputStream a(InputStream inputstream) throws IOException {
        return (InputStream) this.inputWrapper.wrap(inputstream);
    }

    @FunctionalInterface
    private interface a<O> {

        O wrap(O o0) throws IOException;
    }
}
