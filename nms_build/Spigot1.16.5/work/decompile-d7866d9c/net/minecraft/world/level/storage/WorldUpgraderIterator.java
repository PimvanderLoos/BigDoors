package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.serialization.DynamicOps;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.DataPackConfiguration;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.biome.WorldChunkManagerHell;
import net.minecraft.world.level.biome.WorldChunkManagerOverworld;
import net.minecraft.world.level.chunk.storage.OldChunkLoader;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpgraderIterator {

    private static final Logger LOGGER = LogManager.getLogger();

    static boolean a(Convertable.ConversionSession convertable_conversionsession, IProgressUpdate iprogressupdate) {
        iprogressupdate.a(0);
        List<File> list = Lists.newArrayList();
        List<File> list1 = Lists.newArrayList();
        List<File> list2 = Lists.newArrayList();
        File file = convertable_conversionsession.a(World.OVERWORLD);
        File file1 = convertable_conversionsession.a(World.THE_NETHER);
        File file2 = convertable_conversionsession.a(World.THE_END);

        WorldUpgraderIterator.LOGGER.info("Scanning folders...");
        a(file, (Collection) list);
        if (file1.exists()) {
            a(file1, (Collection) list1);
        }

        if (file2.exists()) {
            a(file2, (Collection) list2);
        }

        int i = list.size() + list1.size() + list2.size();

        WorldUpgraderIterator.LOGGER.info("Total conversion count is {}", i);
        IRegistryCustom.Dimension iregistrycustom_dimension = IRegistryCustom.b();
        RegistryReadOps<NBTBase> registryreadops = RegistryReadOps.a((DynamicOps) DynamicOpsNBT.a, (IResourceManager) IResourceManager.Empty.INSTANCE, iregistrycustom_dimension);
        SaveData savedata = convertable_conversionsession.a((DynamicOps) registryreadops, DataPackConfiguration.a);
        long j = savedata != null ? savedata.getGeneratorSettings().getSeed() : 0L;
        IRegistry<BiomeBase> iregistry = iregistrycustom_dimension.b(IRegistry.ay);
        Object object;

        if (savedata != null && savedata.getGeneratorSettings().isFlatWorld()) {
            object = new WorldChunkManagerHell((BiomeBase) iregistry.d(Biomes.PLAINS));
        } else {
            object = new WorldChunkManagerOverworld(j, false, false, iregistry);
        }

        a(iregistrycustom_dimension, new File(file, "region"), (Iterable) list, (WorldChunkManager) object, 0, i, iprogressupdate);
        a(iregistrycustom_dimension, new File(file1, "region"), (Iterable) list1, new WorldChunkManagerHell((BiomeBase) iregistry.d(Biomes.NETHER_WASTES)), list.size(), i, iprogressupdate);
        a(iregistrycustom_dimension, new File(file2, "region"), (Iterable) list2, new WorldChunkManagerHell((BiomeBase) iregistry.d(Biomes.THE_END)), list.size() + list1.size(), i, iprogressupdate);
        a(convertable_conversionsession);
        convertable_conversionsession.a((IRegistryCustom) iregistrycustom_dimension, savedata);
        return true;
    }

    private static void a(Convertable.ConversionSession convertable_conversionsession) {
        File file = convertable_conversionsession.getWorldFolder(SavedFile.LEVEL_DAT).toFile();

        if (!file.exists()) {
            WorldUpgraderIterator.LOGGER.warn("Unable to create level.dat_mcr backup");
        } else {
            File file1 = new File(file.getParent(), "level.dat_mcr");

            if (!file.renameTo(file1)) {
                WorldUpgraderIterator.LOGGER.warn("Unable to create level.dat_mcr backup");
            }

        }
    }

    private static void a(IRegistryCustom.Dimension iregistrycustom_dimension, File file, Iterable<File> iterable, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            File file1 = (File) iterator.next();

            a(iregistrycustom_dimension, file, file1, worldchunkmanager, i, j, iprogressupdate);
            ++i;
            int k = (int) Math.round(100.0D * (double) i / (double) j);

            iprogressupdate.a(k);
        }

    }

    private static void a(IRegistryCustom.Dimension iregistrycustom_dimension, File file, File file1, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
        String s = file1.getName();

        try {
            RegionFile regionfile = new RegionFile(file1, file, true);
            Throwable throwable = null;

            try {
                RegionFile regionfile1 = new RegionFile(new File(file, s.substring(0, s.length() - ".mcr".length()) + ".mca"), file, true);
                Throwable throwable1 = null;

                try {
                    for (int k = 0; k < 32; ++k) {
                        int l;

                        for (l = 0; l < 32; ++l) {
                            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(k, l);

                            if (regionfile.chunkExists(chunkcoordintpair) && !regionfile1.chunkExists(chunkcoordintpair)) {
                                NBTTagCompound nbttagcompound;

                                try {
                                    DataInputStream datainputstream = regionfile.a(chunkcoordintpair);
                                    Throwable throwable2 = null;

                                    try {
                                        if (datainputstream == null) {
                                            WorldUpgraderIterator.LOGGER.warn("Failed to fetch input stream for chunk {}", chunkcoordintpair);
                                            continue;
                                        }

                                        nbttagcompound = NBTCompressedStreamTools.a((DataInput) datainputstream);
                                    } catch (Throwable throwable3) {
                                        throwable2 = throwable3;
                                        throw throwable3;
                                    } finally {
                                        if (datainputstream != null) {
                                            if (throwable2 != null) {
                                                try {
                                                    datainputstream.close();
                                                } catch (Throwable throwable4) {
                                                    throwable2.addSuppressed(throwable4);
                                                }
                                            } else {
                                                datainputstream.close();
                                            }
                                        }

                                    }
                                } catch (IOException ioexception) {
                                    WorldUpgraderIterator.LOGGER.warn("Failed to read data for chunk {}", chunkcoordintpair, ioexception);
                                    continue;
                                }

                                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
                                OldChunkLoader.OldChunk oldchunkloader_oldchunk = OldChunkLoader.a(nbttagcompound1);
                                NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                                NBTTagCompound nbttagcompound3 = new NBTTagCompound();

                                nbttagcompound2.set("Level", nbttagcompound3);
                                OldChunkLoader.a(iregistrycustom_dimension, oldchunkloader_oldchunk, nbttagcompound3, worldchunkmanager);
                                DataOutputStream dataoutputstream = regionfile1.c(chunkcoordintpair);
                                Throwable throwable5 = null;

                                try {
                                    NBTCompressedStreamTools.a(nbttagcompound2, (DataOutput) dataoutputstream);
                                } catch (Throwable throwable6) {
                                    throwable5 = throwable6;
                                    throw throwable6;
                                } finally {
                                    if (dataoutputstream != null) {
                                        if (throwable5 != null) {
                                            try {
                                                dataoutputstream.close();
                                            } catch (Throwable throwable7) {
                                                throwable5.addSuppressed(throwable7);
                                            }
                                        } else {
                                            dataoutputstream.close();
                                        }
                                    }

                                }
                            }
                        }

                        l = (int) Math.round(100.0D * (double) (i * 1024) / (double) (j * 1024));
                        int i1 = (int) Math.round(100.0D * (double) ((k + 1) * 32 + i * 1024) / (double) (j * 1024));

                        if (i1 > l) {
                            iprogressupdate.a(i1);
                        }
                    }
                } catch (Throwable throwable8) {
                    throwable1 = throwable8;
                    throw throwable8;
                } finally {
                    if (regionfile1 != null) {
                        if (throwable1 != null) {
                            try {
                                regionfile1.close();
                            } catch (Throwable throwable9) {
                                throwable1.addSuppressed(throwable9);
                            }
                        } else {
                            regionfile1.close();
                        }
                    }

                }
            } catch (Throwable throwable10) {
                throwable = throwable10;
                throw throwable10;
            } finally {
                if (regionfile != null) {
                    if (throwable != null) {
                        try {
                            regionfile.close();
                        } catch (Throwable throwable11) {
                            throwable.addSuppressed(throwable11);
                        }
                    } else {
                        regionfile.close();
                    }
                }

            }
        } catch (IOException ioexception1) {
            WorldUpgraderIterator.LOGGER.error("Failed to upgrade region file {}", file1, ioexception1);
        }

    }

    private static void a(File file, Collection<File> collection) {
        File file1 = new File(file, "region");
        File[] afile = file1.listFiles((file2, s) -> {
            return s.endsWith(".mcr");
        });

        if (afile != null) {
            Collections.addAll(collection, afile);
        }

    }
}
