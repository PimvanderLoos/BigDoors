package net.minecraft.network.protocol.game;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;

public class PacketPlayOutTileEntityData implements Packet<PacketListenerPlayOut> {

    private final BlockPosition pos;
    private final TileEntityTypes<?> type;
    @Nullable
    private final NBTTagCompound tag;

    public static PacketPlayOutTileEntityData create(TileEntity tileentity, Function<TileEntity, NBTTagCompound> function) {
        return new PacketPlayOutTileEntityData(tileentity.getBlockPos(), tileentity.getType(), (NBTTagCompound) function.apply(tileentity));
    }

    public static PacketPlayOutTileEntityData create(TileEntity tileentity) {
        return create(tileentity, TileEntity::getUpdateTag);
    }

    private PacketPlayOutTileEntityData(BlockPosition blockposition, TileEntityTypes<?> tileentitytypes, NBTTagCompound nbttagcompound) {
        this.pos = blockposition;
        this.type = tileentitytypes;
        this.tag = nbttagcompound.isEmpty() ? null : nbttagcompound;
    }

    public PacketPlayOutTileEntityData(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.readBlockPos();
        this.type = (TileEntityTypes) packetdataserializer.readById(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        this.tag = packetdataserializer.readNbt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeId(BuiltInRegistries.BLOCK_ENTITY_TYPE, this.type);
        packetdataserializer.writeNbt(this.tag);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleBlockEntityData(this);
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public TileEntityTypes<?> getType() {
        return this.type;
    }

    @Nullable
    public NBTTagCompound getTag() {
        return this.tag;
    }
}
