package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldLoaderServer extends WorldLoader {

    private static final Logger e = LogManager.getLogger();

    public WorldLoaderServer(java.nio.file.Path java_nio_file_path, java.nio.file.Path java_nio_file_path1, DataFixer datafixer) {
        super(java_nio_file_path, java_nio_file_path1, datafixer);
    }

    protected int c() {
        return 19133;
    }

    public IDataManager a(String s, @Nullable MinecraftServer minecraftserver) {
        return new ServerNBTManager(this.a.toFile(), s, minecraftserver, this.c);
    }

    public boolean isConvertable(String s) {
        WorldData worlddata = this.c(s);

        return worlddata != null && worlddata.k() != this.c();
    }

    public boolean convert(String s, IProgressUpdate iprogressupdate) {
        iprogressupdate.a(0);
        List<File> list = Lists.newArrayList();
        List<File> list1 = Lists.newArrayList();
        List<File> list2 = Lists.newArrayList();
        File file = new File(this.a.toFile(), s);
        File file1 = DimensionManager.NETHER.a(file);
        File file2 = DimensionManager.THE_END.a(file);

        WorldLoaderServer.e.info("Scanning folders...");
        this.a(file, (Collection) list);
        if (file1.exists()) {
            this.a(file1, (Collection) list1);
        }

        if (file2.exists()) {
            this.a(file2, (Collection) list2);
        }

        int i = list.size() + list1.size() + list2.size();

        WorldLoaderServer.e.info("Total conversion count is {}", i);
        WorldData worlddata = this.c(s);
        BiomeLayout<BiomeLayoutFixedConfiguration, WorldChunkManagerHell> biomelayout = BiomeLayout.b;
        BiomeLayout<BiomeLayoutOverworldConfiguration, WorldChunkManagerOverworld> biomelayout1 = BiomeLayout.c;
        WorldChunkManager worldchunkmanager;

        if (worlddata != null && worlddata.getType() == WorldType.FLAT) {
            worldchunkmanager = biomelayout.a(((BiomeLayoutFixedConfiguration) biomelayout.b()).a(Biomes.PLAINS));
        } else {
            worldchunkmanager = biomelayout1.a(((BiomeLayoutOverworldConfiguration) biomelayout1.b()).a(worlddata).a((GeneratorSettingsOverworld) ChunkGeneratorType.a.b()));
        }

        this.a(new File(file, "region"), (Iterable) list, worldchunkmanager, 0, i, iprogressupdate);
        this.a(new File(file1, "region"), (Iterable) list1, biomelayout.a(((BiomeLayoutFixedConfiguration) biomelayout.b()).a(Biomes.NETHER)), list.size(), i, iprogressupdate);
        this.a(new File(file2, "region"), (Iterable) list2, biomelayout.a(((BiomeLayoutFixedConfiguration) biomelayout.b()).a(Biomes.THE_END)), list.size() + list1.size(), i, iprogressupdate);
        worlddata.d(19133);
        if (worlddata.getType() == WorldType.NORMAL_1_1) {
            worlddata.a(WorldType.NORMAL);
        }

        this.i(s);
        IDataManager idatamanager = this.a(s, (MinecraftServer) null);

        idatamanager.saveWorldData(worlddata);
        return true;
    }

    private void i(String s) {
        File file = new File(this.a.toFile(), s);

        if (!file.exists()) {
            WorldLoaderServer.e.warn("Unable to create level.dat_mcr backup");
        } else {
            File file1 = new File(file, "level.dat");

            if (!file1.exists()) {
                WorldLoaderServer.e.warn("Unable to create level.dat_mcr backup");
            } else {
                File file2 = new File(file, "level.dat_mcr");

                if (!file1.renameTo(file2)) {
                    WorldLoaderServer.e.warn("Unable to create level.dat_mcr backup");
                }

            }
        }
    }

    private void a(File file, Iterable<File> iterable, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            File file1 = (File) iterator.next();

            this.a(file, file1, worldchunkmanager, i, j, iprogressupdate);
            ++i;
            int k = (int) Math.round(100.0D * (double) i / (double) j);

            iprogressupdate.a(k);
        }

    }

    private void a(File file, File file1, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
        try {
            String s = file1.getName();
            RegionFile regionfile = new RegionFile(file1);
            RegionFile regionfile1 = new RegionFile(new File(file, s.substring(0, s.length() - ".mcr".length()) + ".mca"));

            for (int k = 0; k < 32; ++k) {
                int l;

                for (l = 0; l < 32; ++l) {
                    if (regionfile.d(k, l) && !regionfile1.d(k, l)) {
                        DataInputStream datainputstream = regionfile.a(k, l);

                        if (datainputstream == null) {
                            WorldLoaderServer.e.warn("Failed to fetch input stream");
                        } else {
                            NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(datainputstream);

                            datainputstream.close();
                            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
                            OldChunkLoader.OldChunk oldchunkloader_oldchunk = OldChunkLoader.a(nbttagcompound1);
                            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                            NBTTagCompound nbttagcompound3 = new NBTTagCompound();

                            nbttagcompound2.set("Level", nbttagcompound3);
                            OldChunkLoader.a(oldchunkloader_oldchunk, nbttagcompound3, worldchunkmanager);
                            DataOutputStream dataoutputstream = regionfile1.c(k, l);

                            NBTCompressedStreamTools.a(nbttagcompound2, (DataOutput) dataoutputstream);
                            dataoutputstream.close();
                        }
                    }
                }

                l = (int) Math.round(100.0D * (double) (i * 1024) / (double) (j * 1024));
                int i1 = (int) Math.round(100.0D * (double) ((k + 1) * 32 + i * 1024) / (double) (j * 1024));

                if (i1 > l) {
                    iprogressupdate.a(i1);
                }
            }

            regionfile.close();
            regionfile1.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    private void a(File file, Collection<File> collection) {
        File file1 = new File(file, "region");
        File[] afile = file1.listFiles((file2, s) -> {
            return s.endsWith(".mcr");
        });

        if (afile != null) {
            Collections.addAll(collection, afile);
        }

    }
}
