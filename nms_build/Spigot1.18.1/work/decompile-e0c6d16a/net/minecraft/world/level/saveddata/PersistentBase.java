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
    private boolean dirty;

    public PersistentBase() {}

    public abstract NBTTagCompound save(NBTTagCompound nbttagcompound);

    public void setDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean flag) {
        this.dirty = flag;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void save(File file) {
        if (this.isDirty()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.put("data", this.save(new NBTTagCompound()));
            nbttagcompound.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

            try {
                NBTCompressedStreamTools.writeCompressed(nbttagcompound, file);
            } catch (IOException ioexception) {
                PersistentBase.LOGGER.error("Could not save data {}", this, ioexception);
            }

            this.setDirty(false);
        }
    }
}
