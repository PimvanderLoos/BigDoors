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

    public static WorldMapFrame a(NBTTagCompound nbttagcompound) {
        BlockPosition blockposition = GameProfileSerializer.b(nbttagcompound.getCompound("Pos"));
        int i = nbttagcompound.getInt("Rotation");
        int j = nbttagcompound.getInt("EntityId");

        return new WorldMapFrame(blockposition, i, j);
    }

    public NBTTagCompound a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.set("Pos", GameProfileSerializer.a(this.pos));
        nbttagcompound.setInt("Rotation", this.rotation);
        nbttagcompound.setInt("EntityId", this.entityId);
        return nbttagcompound;
    }

    public BlockPosition b() {
        return this.pos;
    }

    public int c() {
        return this.rotation;
    }

    public int d() {
        return this.entityId;
    }

    public String e() {
        return a(this.pos);
    }

    public static String a(BlockPosition blockposition) {
        int i = blockposition.getX();

        return "frame-" + i + "," + blockposition.getY() + "," + blockposition.getZ();
    }
}
