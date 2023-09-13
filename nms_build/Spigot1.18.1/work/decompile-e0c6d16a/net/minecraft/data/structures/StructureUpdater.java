package net.minecraft.data.structures;

import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureUpdater implements SnbtToNbt.a {

    private static final Logger LOGGER = LogManager.getLogger();

    public StructureUpdater() {}

    @Override
    public NBTTagCompound apply(String s, NBTTagCompound nbttagcompound) {
        return s.startsWith("data/minecraft/structures/") ? update(s, nbttagcompound) : nbttagcompound;
    }

    public static NBTTagCompound update(String s, NBTTagCompound nbttagcompound) {
        return updateStructure(s, patchVersion(nbttagcompound));
    }

    private static NBTTagCompound patchVersion(NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.contains("DataVersion", 99)) {
            nbttagcompound.putInt("DataVersion", 500);
        }

        return nbttagcompound;
    }

    private static NBTTagCompound updateStructure(String s, NBTTagCompound nbttagcompound) {
        DefinedStructure definedstructure = new DefinedStructure();
        int i = nbttagcompound.getInt("DataVersion");
        boolean flag = true;

        if (i < 2830) {
            StructureUpdater.LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", i, 2830, s);
        }

        NBTTagCompound nbttagcompound1 = GameProfileSerializer.update(DataConverterRegistry.getDataFixer(), DataFixTypes.STRUCTURE, nbttagcompound, i);

        definedstructure.load(nbttagcompound1);
        return definedstructure.save(new NBTTagCompound());
    }
}
