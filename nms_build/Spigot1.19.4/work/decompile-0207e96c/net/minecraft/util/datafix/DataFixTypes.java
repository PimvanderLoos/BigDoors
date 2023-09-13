package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public enum DataFixTypes {

    LEVEL(DataConverterTypes.LEVEL), PLAYER(DataConverterTypes.PLAYER), CHUNK(DataConverterTypes.CHUNK), HOTBAR(DataConverterTypes.HOTBAR), OPTIONS(DataConverterTypes.OPTIONS), STRUCTURE(DataConverterTypes.STRUCTURE), STATS(DataConverterTypes.STATS), SAVED_DATA(DataConverterTypes.SAVED_DATA), ADVANCEMENTS(DataConverterTypes.ADVANCEMENTS), POI_CHUNK(DataConverterTypes.POI_CHUNK), WORLD_GEN_SETTINGS(DataConverterTypes.WORLD_GEN_SETTINGS), ENTITY_CHUNK(DataConverterTypes.ENTITY_CHUNK);

    public static final Set<TypeReference> TYPES_FOR_LEVEL_LIST = Set.of(DataFixTypes.LEVEL.type);
    private final TypeReference type;

    private DataFixTypes(TypeReference typereference) {
        this.type = typereference;
    }

    private static int currentVersion() {
        return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
    }

    public <T> Dynamic<T> update(DataFixer datafixer, Dynamic<T> dynamic, int i, int j) {
        return datafixer.update(this.type, dynamic, i, j);
    }

    public <T> Dynamic<T> updateToCurrentVersion(DataFixer datafixer, Dynamic<T> dynamic, int i) {
        return this.update(datafixer, dynamic, i, currentVersion());
    }

    public NBTTagCompound update(DataFixer datafixer, NBTTagCompound nbttagcompound, int i, int j) {
        return (NBTTagCompound) this.update(datafixer, new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound), i, j).getValue();
    }

    public NBTTagCompound updateToCurrentVersion(DataFixer datafixer, NBTTagCompound nbttagcompound, int i) {
        return this.update(datafixer, nbttagcompound, i, currentVersion());
    }
}
