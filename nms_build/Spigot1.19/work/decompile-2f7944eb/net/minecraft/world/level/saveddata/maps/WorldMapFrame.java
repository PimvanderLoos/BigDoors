package net.minecraft.world.level.saveddata.maps;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;

public class WorldMapFrame {

    private final BlockPosition pos;
    private final int rotation;
    private final int entityId;

    public WorldMapFrame(BlockPosition blockposition, int i, int j) {
        this.pos = blockposition;
        this.rotation = i;
        this.entityId = j;
    }

    public static WorldMapFrame load(NBTTagCompound nbttagcompound) {
        BlockPosition blockposition = GameProfileSerializer.readBlockPos(nbttagcompound.getCompound("Pos"));
        int i = nbttagcompound.getInt("Rotation");
        int j = nbttagcompound.getInt("EntityId");

        return new WorldMapFrame(blockposition, i, j);
    }

    public NBTTagCompound save() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.put("Pos", GameProfileSerializer.writeBlockPos(this.pos));
        nbttagcompound.putInt("Rotation", this.rotation);
        nbttagcompound.putInt("EntityId", this.entityId);
        return nbttagcompound;
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public String getId() {
        return frameId(this.pos);
    }

    public static String frameId(BlockPosition blockposition) {
        int i = blockposition.getX();

        return "frame-" + i + "," + blockposition.getY() + "," + blockposition.getZ();
    }
}
