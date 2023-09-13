package net.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldLoader implements Convertable {

    private static final Logger c = LogManager.getLogger();
    protected final File a;
    protected final DataConverterManager b;

    public WorldLoader(File file, DataConverterManager dataconvertermanager) {
        this.b = dataconvertermanager;
        if (!file.exists()) {
            file.mkdirs();
        }

        this.a = file;
    }

    @Nullable
    public WorldData c(String s) {
        File file = new File(this.a, s);

        if (!file.exists()) {
            return null;
        } else {
            File file1 = new File(file, "level.dat");

            if (file1.exists()) {
                WorldData worlddata = a(file1, this.b);

                if (worlddata != null) {
                    return worlddata;
                }
            }

            file1 = new File(file, "level.dat_old");
            return file1.exists() ? a(file1, this.b) : null;
        }
    }

    @Nullable
    public static WorldData a(File file, DataConverterManager dataconvertermanager) {
        try {
            NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file)));
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");

            return new WorldData(dataconvertermanager.a((DataConverterType) DataConverterTypes.LEVEL, nbttagcompound1));
        } catch (Exception exception) {
            WorldLoader.c.error("Exception reading {}", file, exception);
            return null;
        }
    }

    public IDataManager a(String s, boolean flag) {
        return new WorldNBTStorage(this.a, s, flag, this.b);
    }

    public boolean isConvertable(String s) {
        return false;
    }

    public boolean convert(String s, IProgressUpdate iprogressupdate) {
        return false;
    }

    public File b(String s, String s1) {
        return new File(new File(this.a, s), s1);
    }
}
