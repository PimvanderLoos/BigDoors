package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.PersistentBase;
import org.slf4j.Logger;

public class WorldPersistentData {

    private static final Logger LOGGER = LogUtils.getLogger();
    public final Map<String, PersistentBase> cache = Maps.newHashMap();
    private final DataFixer fixerUpper;
    private final File dataFolder;

    public WorldPersistentData(File file, DataFixer datafixer) {
        this.fixerUpper = datafixer;
        this.dataFolder = file;
    }

    private File getDataFile(String s) {
        return new File(this.dataFolder, s + ".dat");
    }

    public <T extends PersistentBase> T computeIfAbsent(Function<NBTTagCompound, T> function, Supplier<T> supplier, String s) {
        T t0 = this.get(function, s);

        if (t0 != null) {
            return t0;
        } else {
            T t1 = (PersistentBase) supplier.get();

            this.set(s, t1);
            return t1;
        }
    }

    @Nullable
    public <T extends PersistentBase> T get(Function<NBTTagCompound, T> function, String s) {
        PersistentBase persistentbase = (PersistentBase) this.cache.get(s);

        if (persistentbase == null && !this.cache.containsKey(s)) {
            persistentbase = this.readSavedData(function, s);
            this.cache.put(s, persistentbase);
        }

        return persistentbase;
    }

    @Nullable
    private <T extends PersistentBase> T readSavedData(Function<NBTTagCompound, T> function, String s) {
        try {
            File file = this.getDataFile(s);

            if (file.exists()) {
                NBTTagCompound nbttagcompound = this.readTagFromDisk(s, SharedConstants.getCurrentVersion().getDataVersion().getVersion());

                return (PersistentBase) function.apply(nbttagcompound.getCompound("data"));
            }
        } catch (Exception exception) {
            WorldPersistentData.LOGGER.error("Error loading saved data: {}", s, exception);
        }

        return null;
    }

    public void set(String s, PersistentBase persistentbase) {
        this.cache.put(s, persistentbase);
    }

    public NBTTagCompound readTagFromDisk(String s, int i) throws IOException {
        File file = this.getDataFile(s);
        FileInputStream fileinputstream = new FileInputStream(file);

        NBTTagCompound nbttagcompound;

        try {
            PushbackInputStream pushbackinputstream = new PushbackInputStream(fileinputstream, 2);

            try {
                NBTTagCompound nbttagcompound1;

                if (this.isGzip(pushbackinputstream)) {
                    nbttagcompound1 = NBTCompressedStreamTools.readCompressed((InputStream) pushbackinputstream);
                } else {
                    DataInputStream datainputstream = new DataInputStream(pushbackinputstream);

                    try {
                        nbttagcompound1 = NBTCompressedStreamTools.read((DataInput) datainputstream);
                    } catch (Throwable throwable) {
                        try {
                            datainputstream.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }

                        throw throwable;
                    }

                    datainputstream.close();
                }

                int j = GameProfileSerializer.getDataVersion(nbttagcompound1, 1343);

                nbttagcompound = DataFixTypes.SAVED_DATA.update(this.fixerUpper, nbttagcompound1, j, i);
            } catch (Throwable throwable2) {
                try {
                    pushbackinputstream.close();
                } catch (Throwable throwable3) {
                    throwable2.addSuppressed(throwable3);
                }

                throw throwable2;
            }

            pushbackinputstream.close();
        } catch (Throwable throwable4) {
            try {
                fileinputstream.close();
            } catch (Throwable throwable5) {
                throwable4.addSuppressed(throwable5);
            }

            throw throwable4;
        }

        fileinputstream.close();
        return nbttagcompound;
    }

    private boolean isGzip(PushbackInputStream pushbackinputstream) throws IOException {
        byte[] abyte = new byte[2];
        boolean flag = false;
        int i = pushbackinputstream.read(abyte, 0, 2);

        if (i == 2) {
            int j = (abyte[1] & 255) << 8 | abyte[0] & 255;

            if (j == 35615) {
                flag = true;
            }
        }

        if (i != 0) {
            pushbackinputstream.unread(abyte, 0, i);
        }

        return flag;
    }

    public void save() {
        this.cache.forEach((s, persistentbase) -> {
            if (persistentbase != null) {
                persistentbase.save(this.getDataFile(s));
            }

        });
    }
}
