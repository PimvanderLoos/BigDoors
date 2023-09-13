package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ExceptionSuppressor;
import net.minecraft.world.level.ChunkCoordIntPair;

public final class RegionFileCache implements AutoCloseable {

    public static final String ANVIL_EXTENSION = ".mca";
    private static final int MAX_CACHE_SIZE = 256;
    public final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap();
    private final File folder;
    private final boolean sync;

    RegionFileCache(File file, boolean flag) {
        this.folder = file;
        this.sync = flag;
    }

    private RegionFile getFile(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        long i = ChunkCoordIntPair.pair(chunkcoordintpair.getRegionX(), chunkcoordintpair.getRegionZ());
        RegionFile regionfile = (RegionFile) this.regionCache.getAndMoveToFirst(i);

        if (regionfile != null) {
            return regionfile;
        } else {
            if (this.regionCache.size() >= 256) {
                ((RegionFile) this.regionCache.removeLast()).close();
            }

            if (!this.folder.exists()) {
                this.folder.mkdirs();
            }

            File file = this.folder;
            int j = chunkcoordintpair.getRegionX();
            File file1 = new File(file, "r." + j + "." + chunkcoordintpair.getRegionZ() + ".mca");
            RegionFile regionfile1 = new RegionFile(file1, this.folder, this.sync);

            this.regionCache.putAndMoveToFirst(i, regionfile1);
            return regionfile1;
        }
    }

    @Nullable
    public NBTTagCompound read(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        RegionFile regionfile = this.getFile(chunkcoordintpair);
        DataInputStream datainputstream = regionfile.a(chunkcoordintpair);

        NBTTagCompound nbttagcompound;
        label43:
        {
            try {
                if (datainputstream != null) {
                    nbttagcompound = NBTCompressedStreamTools.a((DataInput) datainputstream);
                    break label43;
                }

                nbttagcompound = null;
            } catch (Throwable throwable) {
                if (datainputstream != null) {
                    try {
                        datainputstream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (datainputstream != null) {
                datainputstream.close();
            }

            return nbttagcompound;
        }

        if (datainputstream != null) {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    protected void write(ChunkCoordIntPair chunkcoordintpair, @Nullable NBTTagCompound nbttagcompound) throws IOException {
        RegionFile regionfile = this.getFile(chunkcoordintpair);

        if (nbttagcompound == null) {
            regionfile.d(chunkcoordintpair);
        } else {
            DataOutputStream dataoutputstream = regionfile.c(chunkcoordintpair);

            try {
                NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) dataoutputstream);
            } catch (Throwable throwable) {
                if (dataoutputstream != null) {
                    try {
                        dataoutputstream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (dataoutputstream != null) {
                dataoutputstream.close();
            }
        }

    }

    public void close() throws IOException {
        ExceptionSuppressor<IOException> exceptionsuppressor = new ExceptionSuppressor<>();
        ObjectIterator objectiterator = this.regionCache.values().iterator();

        while (objectiterator.hasNext()) {
            RegionFile regionfile = (RegionFile) objectiterator.next();

            try {
                regionfile.close();
            } catch (IOException ioexception) {
                exceptionsuppressor.a(ioexception);
            }
        }

        exceptionsuppressor.a();
    }

    public void a() throws IOException {
        ObjectIterator objectiterator = this.regionCache.values().iterator();

        while (objectiterator.hasNext()) {
            RegionFile regionfile = (RegionFile) objectiterator.next();

            regionfile.a();
        }

    }
}
