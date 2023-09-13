package net.minecraft.world.level.saveddata;

import java.io.File;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PersistentBase {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String id;
    private boolean c;

    public PersistentBase(String s) {
        this.id = s;
    }

    public abstract void a(NBTTagCompound nbttagcompound);

    public abstract NBTTagCompound b(NBTTagCompound nbttagcompound);

    public void b() {
        this.a(true);
    }

    public void a(boolean flag) {
        this.c = flag;
    }

    public boolean c() {
        return this.c;
    }

    public String getId() {
        return this.id;
    }

    public void a(File file) {
        if (this.c()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.set("data", this.b(new NBTTagCompound()));
            nbttagcompound.setInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());

            try {
                NBTCompressedStreamTools.a(nbttagcompound, file);
            } catch (IOException ioexception) {
                PersistentBase.LOGGER.error("Could not save data {}", this, ioexception);
            }

            this.a(false);
        }
    }
}
