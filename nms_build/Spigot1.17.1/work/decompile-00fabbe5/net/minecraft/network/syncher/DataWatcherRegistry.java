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

    private static final RegistryID<DataWatcherSerializer<?>> SERIALIZERS = new RegistryID<>(16);
    public static final DataWatcherSerializer<Byte> BYTE = new DataWatcherSerializer<Byte>() {
        public void a(PacketDataSerializer packetdataserializer, Byte obyte) {
            packetdataserializer.writeByte(obyte);
        }

        @Override
        public Byte a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readByte();
        }

        public Byte a(Byte obyte) {
            return obyte;
        }
    };
    public static final DataWatcherSerializer<Integer> INT = new DataWatcherSerializer<Integer>() {
        public void a(PacketDataSerializer packetdataserializer, Integer integer) {
            packetdataserializer.d(integer);
        }

        @Override
        public Integer a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.j();
        }

        public Integer a(Integer integer) {
            return integer;
        }
    };
    public static final DataWatcherSerializer<Float> FLOAT = new DataWatcherSerializer<Float>() {
        public void a(PacketDataSerializer packetdataserializer, Float ofloat) {
            packetdataserializer.writeFloat(ofloat);
        }

        @Override
        public Float a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readFloat();
        }

        public Float a(Float ofloat) {
            return ofloat;
        }
    };
    public static final DataWatcherSerializer<String> STRING = new DataWatcherSerializer<String>() {
        public void a(PacketDataSerializer packetdataserializer, String s) {
            packetdataserializer.a(s);
        }

        @Override
        public String a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.p();
        }

        public String a(String s) {
            return s;
        }
    };
    public static final DataWatcherSerializer<IChatBaseComponent> COMPONENT = new DataWatcherSerializer<IChatBaseComponent>() {
        public void a(PacketDataSerializer packetdataserializer, IChatBaseComponent ichatbasecomponent) {
            packetdataserializer.a(ichatbasecomponent);
        }

        @Override
        public IChatBaseComponent a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.i();
        }

        public IChatBaseComponent a(IChatBaseComponent ichatbasecomponent) {
            return ichatbasecomponent;
        }
    };
    public static final DataWatcherSerializer<Optional<IChatBaseComponent>> OPTIONAL_COMPONENT = new DataWatcherSerializer<Optional<IChatBaseComponent>>() {
        public void a(PacketDataSerializer packetdataserializer, Optional<IChatBaseComponent> optional) {
            if (optional.isPresent()) {
                packetdataserializer.writeBoolean(true);
                packetdataserializer.a((IChatBaseComponent) optional.get());
            } else {
                packetdataserializer.writeBoolean(false);
            }

        }

        @Override
        public Optional<IChatBaseComponent> a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readBoolean() ? Optional.of(packetdataserializer.i()) : Optional.empty();
        }

        public Optional<IChatBaseComponent> a(Optional<IChatBaseComponent> optional) {
            return optional;
        }
    };
    public static final DataWatcherSerializer<ItemStack> ITEM_STACK = new DataWatcherSerializer<ItemStack>() {
        public void a(PacketDataSerializer packetdataserializer, ItemStack itemstack) {
            packetdataserializer.a(itemstack);
        }

        @Override
        public ItemStack a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.o();
        }

        public ItemStack a(ItemStack itemstack) {
            return itemstack.cloneItemStack();
        }
    };
    public static final DataWatcherSerializer<Optional<IBlockData>> BLOCK_STATE = new DataWatcherSerializer<Optional<IBlockData>>() {
        public void a(PacketDataSerializer packetdataserializer, Optional<IBlockData> optional) {
            if (optional.isPresent()) {
                packetdataserializer.d(Block.getCombinedId((IBlockData) optional.get()));
            } else {
                packetdataserializer.d(0);
            }

        }

        @Override
        public Optional<IBlockData> a(PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.j();

            return i == 0 ? Optional.empty() : Optional.of(Block.getByCombinedId(i));
        }

        public Optional<IBlockData> a(Optional<IBlockData> optional) {
            return optional;
        }
    };
    public static final DataWatcherSerializer<Boolean> BOOLEAN = new DataWatcherSerializer<Boolean>() {
        public void a(PacketDataSerializer packetdataserializer, Boolean obool) {
            packetdataserializer.writeBoolean(obool);
        }

        @Override
        public Boolean a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readBoolean();
        }

        public Boolean a(Boolean obool) {
            return obool;
        }
    };
    public static final DataWatcherSerializer<ParticleParam> PARTICLE = new DataWatcherSerializer<ParticleParam>() {
        public void a(PacketDataSerializer packetdataserializer, ParticleParam particleparam) {
            packetdataserializer.d(IRegistry.PARTICLE_TYPE.getId(particleparam.getParticle()));
            particleparam.a(packetdataserializer);
        }

        @Override
        public ParticleParam a(PacketDataSerializer packetdataserializer) {
            return this.a(packetdataserializer, (Particle) IRegistry.PARTICLE_TYPE.fromId(packetdataserializer.j()));
        }

        private <T extends ParticleParam> T a(PacketDataSerializer packetdataserializer, Particle<T> particle) {
            return particle.d().b(particle, packetdataserializer);
        }

        public ParticleParam a(ParticleParam particleparam) {
            return particleparam;
        }
    };
    public static final DataWatcherSerializer<Vector3f> ROTATIONS = new DataWatcherSerializer<Vector3f>() {
        public void a(PacketDataSerializer packetdataserializer, Vector3f vector3f) {
            packetdataserializer.writeFloat(vector3f.getX());
            packetdataserializer.writeFloat(vector3f.getY());
            packetdataserializer.writeFloat(vector3f.getZ());
        }

        @Override
        public Vector3f a(PacketDataSerializer packetdataserializer) {
            return new Vector3f(packetdataserializer.readFloat(), packetdataserializer.readFloat(), packetdataserializer.readFloat());
        }

        public Vector3f a(Vector3f vector3f) {
            return vector3f;
        }
    };
    public static final DataWatcherSerializer<BlockPosition> BLOCK_POS = new DataWatcherSerializer<BlockPosition>() {
        public void a(PacketDataSerializer packetdataserializer, BlockPosition blockposition) {
            packetdataserializer.a(blockposition);
        }

        @Override
        public BlockPosition a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.f();
        }

        public BlockPosition a(BlockPosition blockposition) {
            return blockposition;
        }
    };
    public static final DataWatcherSerializer<Optional<BlockPosition>> OPTIONAL_BLOCK_POS = new DataWatcherSerializer<Optional<BlockPosition>>() {
        public void a(PacketDataSerializer packetdataserializer, Optional<BlockPosition> optional) {
            packetdataserializer.writeBoolean(optional.isPresent());
            if (optional.isPresent()) {
                packetdataserializer.a((BlockPosition) optional.get());
            }

        }

        @Override
        public Optional<BlockPosition> a(PacketDataSerializer packetdataserializer) {
            return !packetdataserializer.readBoolean() ? Optional.empty() : Optional.of(packetdataserializer.f());
        }

        public Optional<BlockPosition> a(Optional<BlockPosition> optional) {
            return optional;
        }
    };
    public static final DataWatcherSerializer<EnumDirection> DIRECTION = new DataWatcherSerializer<EnumDirection>() {
        public void a(PacketDataSerializer packetdataserializer, EnumDirection enumdirection) {
            packetdataserializer.a((Enum) enumdirection);
        }

        @Override
        public EnumDirection a(PacketDataSerializer packetdataserializer) {
            return (EnumDirection) packetdataserializer.a(EnumDirection.class);
        }

        public EnumDirection a(EnumDirection enumdirection) {
            return enumdirection;
        }
    };
    public static final DataWatcherSerializer<Optional<UUID>> OPTIONAL_UUID = new DataWatcherSerializer<Optional<UUID>>() {
        public void a(PacketDataSerializer packetdataserializer, Optional<UUID> optional) {
            packetdataserializer.writeBoolean(optional.isPresent());
            if (optional.isPresent()) {
                packetdataserializer.a((UUID) optional.get());
            }

        }

        @Override
        public Optional<UUID> a(PacketDataSerializer packetdataserializer) {
            return !packetdataserializer.readBoolean() ? Optional.empty() : Optional.of(packetdataserializer.l());
        }

        public Optional<UUID> a(Optional<UUID> optional) {
            return optional;
        }
    };
    public static final DataWatcherSerializer<NBTTagCompound> COMPOUND_TAG = new DataWatcherSerializer<NBTTagCompound>() {
        public void a(PacketDataSerializer packetdataserializer, NBTTagCompound nbttagcompound) {
            packetdataserializer.a(nbttagcompound);
        }

        @Override
        public NBTTagCompound a(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.m();
        }

        public NBTTagCompound a(NBTTagCompound nbttagcompound) {
            return nbttagcompound.clone();
        }
    };
    public static final DataWatcherSerializer<VillagerData> VILLAGER_DATA = new DataWatcherSerializer<VillagerData>() {
        public void a(PacketDataSerializer packetdataserializer, VillagerData villagerdata) {
            packetdataserializer.d(IRegistry.VILLAGER_TYPE.getId(villagerdata.getType()));
            packetdataserializer.d(IRegistry.VILLAGER_PROFESSION.getId(villagerdata.getProfession()));
            packetdataserializer.d(villagerdata.getLevel());
        }

        @Override
        public VillagerData a(PacketDataSerializer packetdataserializer) {
            return new VillagerData((VillagerType) IRegistry.VILLAGER_TYPE.fromId(packetdataserializer.j()), (VillagerProfession) IRegistry.VILLAGER_PROFESSION.fromId(packetdataserializer.j()), packetdataserializer.j());
        }

        public VillagerData a(VillagerData villagerdata) {
            return villagerdata;
        }
    };
    public static final DataWatcherSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new DataWatcherSerializer<OptionalInt>() {
        public void a(PacketDataSerializer packetdataserializer, OptionalInt optionalint) {
            packetdataserializer.d(optionalint.orElse(-1) + 1);
        }

        @Override
        public OptionalInt a(PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.j();

            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
        }

        public OptionalInt a(OptionalInt optionalint) {
            return optionalint;
        }
    };
    public static final DataWatcherSerializer<EntityPose> POSE = new DataWatcherSerializer<EntityPose>() {
        public void a(PacketDataSerializer packetdataserializer, EntityPose entitypose) {
            packetdataserializer.a((Enum) entitypose);
        }

        @Override
        public EntityPose a(PacketDataSerializer packetdataserializer) {
            return (EntityPose) packetdataserializer.a(EntityPose.class);
        }

        public EntityPose a(EntityPose entitypose) {
            return entitypose;
        }
    };

    public static void a(DataWatcherSerializer<?> datawatcherserializer) {
        DataWatcherRegistry.SERIALIZERS.c(datawatcherserializer);
    }

    @Nullable
    public static DataWatcherSerializer<?> a(int i) {
        return (DataWatcherSerializer) DataWatcherRegistry.SERIALIZERS.fromId(i);
    }

    public static int b(DataWatcherSerializer<?> datawatcherserializer) {
        return DataWatcherRegistry.SERIALIZERS.getId(datawatcherserializer);
    }

    private DataWatcherRegistry() {}

    static {
        a(DataWatcherRegistry.BYTE);
        a(DataWatcherRegistry.INT);
        a(DataWatcherRegistry.FLOAT);
        a(DataWatcherRegistry.STRING);
        a(DataWatcherRegistry.COMPONENT);
        a(DataWatcherRegistry.OPTIONAL_COMPONENT);
        a(DataWatcherRegistry.ITEM_STACK);
        a(DataWatcherRegistry.BOOLEAN);
        a(DataWatcherRegistry.ROTATIONS);
        a(DataWatcherRegistry.BLOCK_POS);
        a(DataWatcherRegistry.OPTIONAL_BLOCK_POS);
        a(DataWatcherRegistry.DIRECTION);
        a(DataWatcherRegistry.OPTIONAL_UUID);
        a(DataWatcherRegistry.BLOCK_STATE);
        a(DataWatcherRegistry.COMPOUND_TAG);
        a(DataWatcherRegistry.PARTICLE);
        a(DataWatcherRegistry.VILLAGER_DATA);
        a(DataWatcherRegistry.OPTIONAL_UNSIGNED_INT);
        a(DataWatcherRegistry.POSE);
    }
}
