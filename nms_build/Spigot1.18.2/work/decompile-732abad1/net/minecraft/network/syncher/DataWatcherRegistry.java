package net.minecraft.network.syncher;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.Vector3f;
import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.RegistryID;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class DataWatcherRegistry {

    private static final RegistryID<DataWatcherSerializer<?>> SERIALIZERS = RegistryID.create(16);
    public static final DataWatcherSerializer<Byte> BYTE = new DataWatcherSerializer<Byte>() {
        public void write(PacketDataSerializer packetdataserializer, Byte obyte) {
            packetdataserializer.writeByte(obyte);
        }

        @Override
        public Byte read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readByte();
        }

        public Byte copy(Byte obyte) {
            return obyte;
        }
    };
    public static final DataWatcherSerializer<Integer> INT = new DataWatcherSerializer<Integer>() {
        public void write(PacketDataSerializer packetdataserializer, Integer integer) {
            packetdataserializer.writeVarInt(integer);
        }

        @Override
        public Integer read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readVarInt();
        }

        public Integer copy(Integer integer) {
            return integer;
        }
    };
    public static final DataWatcherSerializer<Float> FLOAT = new DataWatcherSerializer<Float>() {
        public void write(PacketDataSerializer packetdataserializer, Float ofloat) {
            packetdataserializer.writeFloat(ofloat);
        }

        @Override
        public Float read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readFloat();
        }

        public Float copy(Float ofloat) {
            return ofloat;
        }
    };
    public static final DataWatcherSerializer<String> STRING = new DataWatcherSerializer<String>() {
        public void write(PacketDataSerializer packetdataserializer, String s) {
            packetdataserializer.writeUtf(s);
        }

        @Override
        public String read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readUtf();
        }

        public String copy(String s) {
            return s;
        }
    };
    public static final DataWatcherSerializer<IChatBaseComponent> COMPONENT = new DataWatcherSerializer<IChatBaseComponent>() {
        public void write(PacketDataSerializer packetdataserializer, IChatBaseComponent ichatbasecomponent) {
            packetdataserializer.writeComponent(ichatbasecomponent);
        }

        @Override
        public IChatBaseComponent read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readComponent();
        }

        public IChatBaseComponent copy(IChatBaseComponent ichatbasecomponent) {
            return ichatbasecomponent;
        }
    };
    public static final DataWatcherSerializer<Optional<IChatBaseComponent>> OPTIONAL_COMPONENT = new DataWatcherSerializer<Optional<IChatBaseComponent>>() {
        public void write(PacketDataSerializer packetdataserializer, Optional<IChatBaseComponent> optional) {
            if (optional.isPresent()) {
                packetdataserializer.writeBoolean(true);
                packetdataserializer.writeComponent((IChatBaseComponent) optional.get());
            } else {
                packetdataserializer.writeBoolean(false);
            }

        }

        @Override
        public Optional<IChatBaseComponent> read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readBoolean() ? Optional.of(packetdataserializer.readComponent()) : Optional.empty();
        }

        public Optional<IChatBaseComponent> copy(Optional<IChatBaseComponent> optional) {
            return optional;
        }
    };
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
    public static final DataWatcherSerializer<Optional<IBlockData>> BLOCK_STATE = new DataWatcherSerializer<Optional<IBlockData>>() {
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

        public Optional<IBlockData> copy(Optional<IBlockData> optional) {
            return optional;
        }
    };
    public static final DataWatcherSerializer<Boolean> BOOLEAN = new DataWatcherSerializer<Boolean>() {
        public void write(PacketDataSerializer packetdataserializer, Boolean obool) {
            packetdataserializer.writeBoolean(obool);
        }

        @Override
        public Boolean read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readBoolean();
        }

        public Boolean copy(Boolean obool) {
            return obool;
        }
    };
    public static final DataWatcherSerializer<ParticleParam> PARTICLE = new DataWatcherSerializer<ParticleParam>() {
        public void write(PacketDataSerializer packetdataserializer, ParticleParam particleparam) {
            packetdataserializer.writeVarInt(IRegistry.PARTICLE_TYPE.getId(particleparam.getType()));
            particleparam.writeToNetwork(packetdataserializer);
        }

        @Override
        public ParticleParam read(PacketDataSerializer packetdataserializer) {
            return this.readParticle(packetdataserializer, (Particle) IRegistry.PARTICLE_TYPE.byId(packetdataserializer.readVarInt()));
        }

        private <T extends ParticleParam> T readParticle(PacketDataSerializer packetdataserializer, Particle<T> particle) {
            return particle.getDeserializer().fromNetwork(particle, packetdataserializer);
        }

        public ParticleParam copy(ParticleParam particleparam) {
            return particleparam;
        }
    };
    public static final DataWatcherSerializer<Vector3f> ROTATIONS = new DataWatcherSerializer<Vector3f>() {
        public void write(PacketDataSerializer packetdataserializer, Vector3f vector3f) {
            packetdataserializer.writeFloat(vector3f.getX());
            packetdataserializer.writeFloat(vector3f.getY());
            packetdataserializer.writeFloat(vector3f.getZ());
        }

        @Override
        public Vector3f read(PacketDataSerializer packetdataserializer) {
            return new Vector3f(packetdataserializer.readFloat(), packetdataserializer.readFloat(), packetdataserializer.readFloat());
        }

        public Vector3f copy(Vector3f vector3f) {
            return vector3f;
        }
    };
    public static final DataWatcherSerializer<BlockPosition> BLOCK_POS = new DataWatcherSerializer<BlockPosition>() {
        public void write(PacketDataSerializer packetdataserializer, BlockPosition blockposition) {
            packetdataserializer.writeBlockPos(blockposition);
        }

        @Override
        public BlockPosition read(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readBlockPos();
        }

        public BlockPosition copy(BlockPosition blockposition) {
            return blockposition;
        }
    };
    public static final DataWatcherSerializer<Optional<BlockPosition>> OPTIONAL_BLOCK_POS = new DataWatcherSerializer<Optional<BlockPosition>>() {
        public void write(PacketDataSerializer packetdataserializer, Optional<BlockPosition> optional) {
            packetdataserializer.writeBoolean(optional.isPresent());
            if (optional.isPresent()) {
                packetdataserializer.writeBlockPos((BlockPosition) optional.get());
            }

        }

        @Override
        public Optional<BlockPosition> read(PacketDataSerializer packetdataserializer) {
            return !packetdataserializer.readBoolean() ? Optional.empty() : Optional.of(packetdataserializer.readBlockPos());
        }

        public Optional<BlockPosition> copy(Optional<BlockPosition> optional) {
            return optional;
        }
    };
    public static final DataWatcherSerializer<EnumDirection> DIRECTION = new DataWatcherSerializer<EnumDirection>() {
        public void write(PacketDataSerializer packetdataserializer, EnumDirection enumdirection) {
            packetdataserializer.writeEnum(enumdirection);
        }

        @Override
        public EnumDirection read(PacketDataSerializer packetdataserializer) {
            return (EnumDirection) packetdataserializer.readEnum(EnumDirection.class);
        }

        public EnumDirection copy(EnumDirection enumdirection) {
            return enumdirection;
        }
    };
    public static final DataWatcherSerializer<Optional<UUID>> OPTIONAL_UUID = new DataWatcherSerializer<Optional<UUID>>() {
        public void write(PacketDataSerializer packetdataserializer, Optional<UUID> optional) {
            packetdataserializer.writeBoolean(optional.isPresent());
            if (optional.isPresent()) {
                packetdataserializer.writeUUID((UUID) optional.get());
            }

        }

        @Override
        public Optional<UUID> read(PacketDataSerializer packetdataserializer) {
            return !packetdataserializer.readBoolean() ? Optional.empty() : Optional.of(packetdataserializer.readUUID());
        }

        public Optional<UUID> copy(Optional<UUID> optional) {
            return optional;
        }
    };
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
    public static final DataWatcherSerializer<VillagerData> VILLAGER_DATA = new DataWatcherSerializer<VillagerData>() {
        public void write(PacketDataSerializer packetdataserializer, VillagerData villagerdata) {
            packetdataserializer.writeVarInt(IRegistry.VILLAGER_TYPE.getId(villagerdata.getType()));
            packetdataserializer.writeVarInt(IRegistry.VILLAGER_PROFESSION.getId(villagerdata.getProfession()));
            packetdataserializer.writeVarInt(villagerdata.getLevel());
        }

        @Override
        public VillagerData read(PacketDataSerializer packetdataserializer) {
            return new VillagerData((VillagerType) IRegistry.VILLAGER_TYPE.byId(packetdataserializer.readVarInt()), (VillagerProfession) IRegistry.VILLAGER_PROFESSION.byId(packetdataserializer.readVarInt()), packetdataserializer.readVarInt());
        }

        public VillagerData copy(VillagerData villagerdata) {
            return villagerdata;
        }
    };
    public static final DataWatcherSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new DataWatcherSerializer<OptionalInt>() {
        public void write(PacketDataSerializer packetdataserializer, OptionalInt optionalint) {
            packetdataserializer.writeVarInt(optionalint.orElse(-1) + 1);
        }

        @Override
        public OptionalInt read(PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.readVarInt();

            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
        }

        public OptionalInt copy(OptionalInt optionalint) {
            return optionalint;
        }
    };
    public static final DataWatcherSerializer<EntityPose> POSE = new DataWatcherSerializer<EntityPose>() {
        public void write(PacketDataSerializer packetdataserializer, EntityPose entitypose) {
            packetdataserializer.writeEnum(entitypose);
        }

        @Override
        public EntityPose read(PacketDataSerializer packetdataserializer) {
            return (EntityPose) packetdataserializer.readEnum(EntityPose.class);
        }

        public EntityPose copy(EntityPose entitypose) {
            return entitypose;
        }
    };

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
    }
}
