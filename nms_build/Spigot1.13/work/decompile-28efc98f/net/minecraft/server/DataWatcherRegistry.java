package net.minecraft.server;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

public class DataWatcherRegistry {

    private static final RegistryID<DataWatcherSerializer<?>> q = new RegistryID(16);
    public static final DataWatcherSerializer<Byte> a = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, Byte obyte) {
            packetdataserializer.writeByte(obyte.byteValue());
        }

        public Byte b(PacketDataSerializer packetdataserializer) {
            return Byte.valueOf(packetdataserializer.readByte());
        }

        public DataWatcherObject<Byte> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public Byte a(Byte obyte) {
            return obyte;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<Integer> b = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, Integer integer) {
            packetdataserializer.d(integer.intValue());
        }

        public Integer b(PacketDataSerializer packetdataserializer) {
            return Integer.valueOf(packetdataserializer.g());
        }

        public DataWatcherObject<Integer> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public Integer a(Integer integer) {
            return integer;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<Float> c = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, Float ofloat) {
            packetdataserializer.writeFloat(ofloat.floatValue());
        }

        public Float b(PacketDataSerializer packetdataserializer) {
            return Float.valueOf(packetdataserializer.readFloat());
        }

        public DataWatcherObject<Float> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public Float a(Float ofloat) {
            return ofloat;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<String> d = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, String s) {
            packetdataserializer.a(s);
        }

        public String b(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.e(32767);
        }

        public DataWatcherObject<String> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public String a(String s) {
            return s;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<IChatBaseComponent> e = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, IChatBaseComponent ichatbasecomponent) {
            packetdataserializer.a(ichatbasecomponent);
        }

        public IChatBaseComponent b(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.f();
        }

        public DataWatcherObject<IChatBaseComponent> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public IChatBaseComponent a(IChatBaseComponent ichatbasecomponent) {
            return ichatbasecomponent.e();
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<Optional<IChatBaseComponent>> f = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, Optional<IChatBaseComponent> optional) {
            if (optional.isPresent()) {
                packetdataserializer.writeBoolean(true);
                packetdataserializer.a((IChatBaseComponent) optional.get());
            } else {
                packetdataserializer.writeBoolean(false);
            }

        }

        public Optional<IChatBaseComponent> b(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.readBoolean() ? Optional.of(packetdataserializer.f()) : Optional.empty();
        }

        public DataWatcherObject<Optional<IChatBaseComponent>> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public Optional<IChatBaseComponent> a(Optional<IChatBaseComponent> optional) {
            return optional.isPresent() ? Optional.of(((IChatBaseComponent) optional.get()).e()) : Optional.empty();
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<ItemStack> g = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, ItemStack itemstack) {
            packetdataserializer.a(itemstack);
        }

        public ItemStack b(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.k();
        }

        public DataWatcherObject<ItemStack> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public ItemStack a(ItemStack itemstack) {
            return itemstack.cloneItemStack();
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<Optional<IBlockData>> h = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, Optional<IBlockData> optional) {
            if (optional.isPresent()) {
                packetdataserializer.d(Block.getCombinedId((IBlockData) optional.get()));
            } else {
                packetdataserializer.d(0);
            }

        }

        public Optional<IBlockData> b(PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.g();

            return i == 0 ? Optional.empty() : Optional.of(Block.getByCombinedId(i));
        }

        public DataWatcherObject<Optional<IBlockData>> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public Optional<IBlockData> a(Optional<IBlockData> optional) {
            return optional;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<Boolean> i = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, Boolean obool) {
            packetdataserializer.writeBoolean(obool.booleanValue());
        }

        public Boolean b(PacketDataSerializer packetdataserializer) {
            return Boolean.valueOf(packetdataserializer.readBoolean());
        }

        public DataWatcherObject<Boolean> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public Boolean a(Boolean obool) {
            return obool;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<ParticleParam> j = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, ParticleParam particleparam) {
            packetdataserializer.d(Particle.REGISTRY.a((Object) particleparam.b()));
            particleparam.a(packetdataserializer);
        }

        public ParticleParam b(PacketDataSerializer packetdataserializer) {
            return this.a(packetdataserializer, (Particle) Particle.REGISTRY.getId(packetdataserializer.g()));
        }

        private <T extends ParticleParam> T a(PacketDataSerializer packetdataserializer, Particle<T> particle) {
            return particle.f().b(particle, packetdataserializer);
        }

        public DataWatcherObject<ParticleParam> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public ParticleParam a(ParticleParam particleparam) {
            return particleparam;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<Vector3f> k = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, Vector3f vector3f) {
            packetdataserializer.writeFloat(vector3f.getX());
            packetdataserializer.writeFloat(vector3f.getY());
            packetdataserializer.writeFloat(vector3f.getZ());
        }

        public Vector3f b(PacketDataSerializer packetdataserializer) {
            return new Vector3f(packetdataserializer.readFloat(), packetdataserializer.readFloat(), packetdataserializer.readFloat());
        }

        public DataWatcherObject<Vector3f> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public Vector3f a(Vector3f vector3f) {
            return vector3f;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<BlockPosition> l = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, BlockPosition blockposition) {
            packetdataserializer.a(blockposition);
        }

        public BlockPosition b(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.e();
        }

        public DataWatcherObject<BlockPosition> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public BlockPosition a(BlockPosition blockposition) {
            return blockposition;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<Optional<BlockPosition>> m = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, Optional<BlockPosition> optional) {
            packetdataserializer.writeBoolean(optional.isPresent());
            if (optional.isPresent()) {
                packetdataserializer.a((BlockPosition) optional.get());
            }

        }

        public Optional<BlockPosition> b(PacketDataSerializer packetdataserializer) {
            return !packetdataserializer.readBoolean() ? Optional.empty() : Optional.of(packetdataserializer.e());
        }

        public DataWatcherObject<Optional<BlockPosition>> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public Optional<BlockPosition> a(Optional<BlockPosition> optional) {
            return optional;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<EnumDirection> n = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, EnumDirection enumdirection) {
            packetdataserializer.a((Enum) enumdirection);
        }

        public EnumDirection b(PacketDataSerializer packetdataserializer) {
            return (EnumDirection) packetdataserializer.a(EnumDirection.class);
        }

        public DataWatcherObject<EnumDirection> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public EnumDirection a(EnumDirection enumdirection) {
            return enumdirection;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<Optional<UUID>> o = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, Optional<UUID> optional) {
            packetdataserializer.writeBoolean(optional.isPresent());
            if (optional.isPresent()) {
                packetdataserializer.a((UUID) optional.get());
            }

        }

        public Optional<UUID> b(PacketDataSerializer packetdataserializer) {
            return !packetdataserializer.readBoolean() ? Optional.empty() : Optional.of(packetdataserializer.i());
        }

        public DataWatcherObject<Optional<UUID>> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public Optional<UUID> a(Optional<UUID> optional) {
            return optional;
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };
    public static final DataWatcherSerializer<NBTTagCompound> p = new DataWatcherSerializer() {
        public void a(PacketDataSerializer packetdataserializer, NBTTagCompound nbttagcompound) {
            packetdataserializer.a(nbttagcompound);
        }

        public NBTTagCompound b(PacketDataSerializer packetdataserializer) {
            return packetdataserializer.j();
        }

        public DataWatcherObject<NBTTagCompound> a(int i) {
            return new DataWatcherObject(i, this);
        }

        public NBTTagCompound a(NBTTagCompound nbttagcompound) {
            return nbttagcompound.clone();
        }

        public Object a(PacketDataSerializer packetdataserializer) {
            return this.b(packetdataserializer);
        }
    };

    public static void a(DataWatcherSerializer<?> datawatcherserializer) {
        DataWatcherRegistry.q.c(datawatcherserializer);
    }

    @Nullable
    public static DataWatcherSerializer<?> a(int i) {
        return (DataWatcherSerializer) DataWatcherRegistry.q.fromId(i);
    }

    public static int b(DataWatcherSerializer<?> datawatcherserializer) {
        return DataWatcherRegistry.q.getId(datawatcherserializer);
    }

    static {
        a(DataWatcherRegistry.a);
        a(DataWatcherRegistry.b);
        a(DataWatcherRegistry.c);
        a(DataWatcherRegistry.d);
        a(DataWatcherRegistry.e);
        a(DataWatcherRegistry.f);
        a(DataWatcherRegistry.g);
        a(DataWatcherRegistry.i);
        a(DataWatcherRegistry.k);
        a(DataWatcherRegistry.l);
        a(DataWatcherRegistry.m);
        a(DataWatcherRegistry.n);
        a(DataWatcherRegistry.o);
        a(DataWatcherRegistry.h);
        a(DataWatcherRegistry.p);
        a(DataWatcherRegistry.j);
    }
}
