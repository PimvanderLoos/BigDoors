package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.util.FastBufferedInputStream;

public class RegionFileCompression {

    private static final Int2ObjectMap<RegionFileCompression> VERSIONS = new Int2ObjectOpenHashMap();
    public static final RegionFileCompression VERSION_GZIP = register(new RegionFileCompression(1, (inputstream) -> {
        return new FastBufferedInputStream(new GZIPInputStream(inputstream));
    }, (outputstream) -> {
        return new BufferedOutputStream(new GZIPOutputStream(outputstream));
    }));
    public static final RegionFileCompression VERSION_DEFLATE = register(new RegionFileCompression(2, (inputstream) -> {
        return new FastBufferedInputStream(new InflaterInputStream(inputstream));
    }, (outputstream) -> {
        return new BufferedOutputStream(new DeflaterOutputStream(outputstream));
    }));
    public static final RegionFileCompression VERSION_NONE = register(new RegionFileCompression(3, (inputstream) -> {
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

    private static RegionFileCompression register(RegionFileCompression regionfilecompression) {
        RegionFileCompression.VERSIONS.put(regionfilecompression.id, regionfilecompression);
        return regionfilecompression;
    }

    @Nullable
    public static RegionFileCompression fromId(int i) {
        return (RegionFileCompression) RegionFileCompression.VERSIONS.get(i);
    }

    public static boolean isValidVersion(int i) {
        return RegionFileCompression.VERSIONS.containsKey(i);
    }

    public int getId() {
        return this.id;
    }

    public OutputStream wrap(OutputStream outputstream) throws IOException {
        return (OutputStream) this.outputWrapper.wrap(outputstream);
    }

    public InputStream wrap(InputStream inputstream) throws IOException {
        return (InputStream) this.inputWrapper.wrap(inputstream);
    }

    @FunctionalInterface
    private interface a<O> {

        O wrap(O o0) throws IOException;
    }
}
