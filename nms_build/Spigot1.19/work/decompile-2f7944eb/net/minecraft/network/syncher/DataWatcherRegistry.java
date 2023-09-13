package net.minecraft.network.syncher;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.Vector3f;
import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.RegistryID;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class DataWatcherRegistry {

    private static final RegistryID<DataWatcherSerializer<?>> SERIALIZERS = RegistryID.create(16);
    public static final DataWatcherSerializer<Byte> BYTE = DataWatcherSerializer.simple((packetdataserializer, obyte) -> {
        packetdataserializer.writeByte(obyte);
    }, PacketDataSerializer::readByte);
    public static final DataWatcherSerializer<Integer> INT = DataWatcherSerializer.simple(PacketDataSerializer::writeVarInt, PacketDataSerializer::readVarInt);
    public static final DataWatcherSerializer<Float> FLOAT = DataWatcherSerializer.simple(PacketDataSerializer::writeFloat, PacketDataSerializer::readFloat);
    public static final DataWatcherSerializer<String> STRING = DataWatcherSerializer.simple(PacketDataSerializer::writeUtf, PacketDataSerializer::readUtf);
    public static final DataWatcherSerializer<IChatBaseComponent> COMPONENT = DataWatcherSerializer.simple(PacketDataSerializer::writeComponent, PacketDataSerializer::readComponent);
    public static final DataWatcherSerializer<Optional<IChatBaseComponent>> OPTIONAL_COMPONENT = DataWatcherSerializer.optional(PacketDataSerializer::writeComponent, PacketDataSerializer::readComponent);
    public static final DataWatcherSerializer<ItemStack> ITEM_STACK = new DataWatcherSerializer<ItemStack>() {
        public void write(PacketDataSerializer packetdataserializer, ItemStack itemstack) {
            packetdataserializer.writeItem(itemstack);
        }

        @Override
        public ItemStack read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readItem();
        }

        public ItemStack copy(ItemStack itemstack) {
            return itemstack.copy();
        }
    };
    public static final DataWatcherSerializer<Optional<IBlockData>> BLOCK_STATE = new DataWatcherSerializer.a<Optional<IBlockData>>() {
        public void write(PacketDataSerializer packetdataserializer, Optional<IBlockData> optional) {
            if (optional.isPresent()) {
                packetdataserializer.writeVarInt(Block.getId((IBlockData) optional.get()));
            } else {
                packetdataserializer.writeVarInt(0);
            }

        }

        @Override
        public Optional<IBlockData> read(PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.readVarInt();

            return i == 0 ? Optional.empty() : Optional.of(Block.stateById(i));
        }
    };
    public static final DataWatcherSerializer<Boolean> BOOLEAN = DataWatcherSerializer.simple(PacketDataSerializer::writeBoolean, PacketDataSerializer::readBoolean);
    public static final DataWatcherSerializer<ParticleParam> PARTICLE = new DataWatcherSerializer.a<ParticleParam>() {
        public void write(PacketDataSerializer packetdataserializer, ParticleParam particleparam) {
            packetdataserializer.writeId(IRegistry.PARTICLE_TYPE, particleparam.getType());
            particleparam.writeToNetwork(packetdataserializer);
        }

        @Override
        public ParticleParam read(PacketDataSerializer packetdataserializer) {
            return this.readParticle(packetdataserializer, (Particle) packetdataserializer.readById(IRegistry.PARTICLE_TYPE));
        }

        private <T extends ParticleParam> T readParticle(PacketDataSerializer packetdataserializer, Particle<T> particle) {
            return particle.getDeserializer().fromNetwork(particle, packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<Vector3f> ROTATIONS = new DataWatcherSerializer.a<Vector3f>() {
        public void write(PacketDataSerializer packetdataserializer, Vector3f vector3f) {
            packetdataserializer.writeFloat(vector3f.getX());
            packetdataserializer.writeFloat(vector3f.getY());
            packetdataserializer.writeFloat(vector3f.getZ());
        }

        @Override
        public Vector3f read(PacketDataSerializer packetdataserializer) {
            return new Vector3f(packetdataserializer.readFloat(), packetdataserializer.readFloat(), packetdataserializer.readFloat());
        }
    };
    public static final DataWatcherSerializer<BlockPosition> BLOCK_POS = DataWatcherSerializer.simple(PacketDataSerializer::writeBlockPos, PacketDataSerializer::readBlockPos);
    public static final DataWatcherSerializer<Optional<BlockPosition>> OPTIONAL_BLOCK_POS = DataWatcherSerializer.optional(PacketDataSerializer::writeBlockPos, PacketDataSerializer::readBlockPos);
    public static final DataWatcherSerializer<EnumDirection> DIRECTION = DataWatcherSerializer.simpleEnum(EnumDirection.class);
    public static final DataWatcherSerializer<Optional<UUID>> OPTIONAL_UUID = DataWatcherSerializer.optional(PacketDataSerializer::writeUUID, PacketDataSerializer::readUUID);
    public static final DataWatcherSerializer<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = DataWatcherSerializer.optional(PacketDataSerializer::writeGlobalPos, PacketDataSerializer::readGlobalPos);
    public static final DataWatcherSerializer<NBTTagCompound> COMPOUND_TAG = new DataWatcherSerializer<NBTTagCompound>() {
        public void write(PacketDataSerializer packetdataserializer, NBTTagCompound nbttagcompound) {
            packetdataserializer.writeNbt(nbttagcompound);
        }

        @Override
        public NBTTagCompound read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readNbt();
        }

        public NBTTagCompound copy(NBTTagCompound nbttagcompound) {
            return nbttagcompound.copy();
        }
    };
    public static final DataWatcherSerializer<VillagerData> VILLAGER_DATA = new DataWatcherSerializer.a<VillagerData>() {
        public void write(PacketDataSerializer packetdataserializer, VillagerData villagerdata) {
            packetdataserializer.writeId(IRegistry.VILLAGER_TYPE, villagerdata.getType());
            packetdataserializer.writeId(IRegistry.VILLAGER_PROFESSION, villagerdata.getProfession());
            packetdataserializer.writeVarInt(villagerdata.getLevel());
        }

        @Override
        public VillagerData read(PacketDataSerializer packetdataserializer) {
            return new VillagerData((VillagerType) packetdataserializer.readById(IRegistry.VILLAGER_TYPE), (VillagerProfession) packetdataserializer.readById(IRegistry.VILLAGER_PROFESSION), packetdataserializer.readVarInt());
        }
    };
    public static final DataWatcherSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new DataWatcherSerializer.a<OptionalInt>() {
        public void write(PacketDataSerializer packetdataserializer, OptionalInt optionalint) {
            packetdataserializer.writeVarInt(optionalint.orElse(-1) + 1);
        }

        @Override
        public OptionalInt read(PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.readVarInt();

            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
        }
    };
    public static final DataWatcherSerializer<EntityPose> POSE = DataWatcherSerializer.simpleEnum(EntityPose.class);
    public static final DataWatcherSerializer<CatVariant> CAT_VARIANT = DataWatcherSerializer.simpleId(IRegistry.CAT_VARIANT);
    public static final DataWatcherSerializer<FrogVariant> FROG_VARIANT = DataWatcherSerializer.simpleId(IRegistry.FROG_VARIANT);
    public static final DataWatcherSerializer<Holder<PaintingVariant>> PAINTING_VARIANT = DataWatcherSerializer.simpleId(IRegistry.PAINTING_VARIANT.asHolderIdMap());

    public static void registerSerializer(DataWatcherSerializer<?> datawatcherserializer) {
        DataWatcherRegistry.SERIALIZERS.add(datawatcherserializer);
    }

    @Nullable
    public static DataWatcherSerializer<?> getSerializer(int i) {
        return (DataWatcherSerializer) DataWatcherRegistry.SERIALIZERS.byId(i);
    }

    public static int getSerializedId(DataWatcherSerializer<?> datawatcherserializer) {
        return DataWatcherRegistry.SERIALIZERS.getId(datawatcherserializer);
    }

    private DataWatcherRegistry() {}

    static {
        registerSerializer(DataWatcherRegistry.BYTE);
        registerSerializer(DataWatcherRegistry.INT);
        registerSerializer(DataWatcherRegistry.FLOAT);
        registerSerializer(DataWatcherRegistry.STRING);
        registerSerializer(DataWatcherRegistry.COMPONENT);
        registerSerializer(DataWatcherRegistry.OPTIONAL_COMPONENT);
        registerSerializer(DataWatcherRegistry.ITEM_STACK);
        registerSerializer(DataWatcherRegistry.BOOLEAN);
        registerSerializer(DataWatcherRegistry.ROTATIONS);
        registerSerializer(DataWatcherRegistry.BLOCK_POS);
        registerSerializer(DataWatcherRegistry.OPTIONAL_BLOCK_POS);
        registerSerializer(DataWatcherRegistry.DIRECTION);
        registerSerializer(DataWatcherRegistry.OPTIONAL_UUID);
        registerSerializer(DataWatcherRegistry.BLOCK_STATE);
        registerSerializer(DataWatcherRegistry.COMPOUND_TAG);
        registerSerializer(DataWatcherRegistry.PARTICLE);
        registerSerializer(DataWatcherRegistry.VILLAGER_DATA);
        registerSerializer(DataWatcherRegistry.OPTIONAL_UNSIGNED_INT);
        registerSerializer(DataWatcherRegistry.POSE);
        registerSerializer(DataWatcherRegistry.CAT_VARIANT);
        registerSerializer(DataWatcherRegistry.FROG_VARIANT);
        registerSerializer(DataWatcherRegistry.OPTIONAL_GLOBAL_POS);
        registerSerializer(DataWatcherRegistry.PAINTING_VARIANT);
    }
}
