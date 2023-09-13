package net.minecraft.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTReadLimiter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.CryptographyException;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftEncryption;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PacketDataSerializer extends ByteBuf {

    private static final int MAX_VARINT_SIZE = 5;
    private static final int MAX_VARLONG_SIZE = 10;
    public static final int DEFAULT_NBT_QUOTA = 2097152;
    private final ByteBuf source;
    public static final short MAX_STRING_LENGTH = 32767;
    public static final int MAX_COMPONENT_STRING_LENGTH = 262144;
    private static final int PUBLIC_KEY_SIZE = 256;
    private static final int MAX_PUBLIC_KEY_HEADER_SIZE = 256;
    private static final int MAX_PUBLIC_KEY_LENGTH = 512;
    private static final Gson GSON = new Gson();

    public PacketDataSerializer(ByteBuf bytebuf) {
        this.source = bytebuf;
    }

    public static int getVarIntSize(int i) {
        for (int j = 1; j < 5; ++j) {
            if ((i & -1 << j * 7) == 0) {
                return j;
            }
        }

        return 5;
    }

    public static int getVarLongSize(long i) {
        for (int j = 1; j < 10; ++j) {
            if ((i & -1L << j * 7) == 0L) {
                return j;
            }
        }

        return 10;
    }

    /** @deprecated */
    @Deprecated
    public <T> T readWithCodec(DynamicOps<NBTBase> dynamicops, Codec<T> codec) {
        NBTTagCompound nbttagcompound = this.readAnySizeNbt();

        return SystemUtils.getOrThrow(codec.parse(dynamicops, nbttagcompound), (s) -> {
            return new DecoderException("Failed to decode: " + s + " " + nbttagcompound);
        });
    }

    /** @deprecated */
    @Deprecated
    public <T> void writeWithCodec(DynamicOps<NBTBase> dynamicops, Codec<T> codec, T t0) {
        NBTBase nbtbase = (NBTBase) SystemUtils.getOrThrow(codec.encodeStart(dynamicops, t0), (s) -> {
            return new EncoderException("Failed to encode: " + s + " " + t0);
        });

        this.writeNbt((NBTTagCompound) nbtbase);
    }

    public <T> T readJsonWithCodec(Codec<T> codec) {
        JsonElement jsonelement = (JsonElement) ChatDeserializer.fromJson(PacketDataSerializer.GSON, this.readUtf(), JsonElement.class);
        DataResult<T> dataresult = codec.parse(JsonOps.INSTANCE, jsonelement);

        return SystemUtils.getOrThrow(dataresult, (s) -> {
            return new DecoderException("Failed to decode json: " + s);
        });
    }

    public <T> void writeJsonWithCodec(Codec<T> codec, T t0) {
        DataResult<JsonElement> dataresult = codec.encodeStart(JsonOps.INSTANCE, t0);

        this.writeUtf(PacketDataSerializer.GSON.toJson((JsonElement) SystemUtils.getOrThrow(dataresult, (s) -> {
            return new EncoderException("Failed to encode: " + s + " " + t0);
        })));
    }

    public <T> void writeId(Registry<T> registry, T t0) {
        int i = registry.getId(t0);

        if (i == -1) {
            throw new IllegalArgumentException("Can't find id for '" + t0 + "' in map " + registry);
        } else {
            this.writeVarInt(i);
        }
    }

    public <T> void writeId(Registry<Holder<T>> registry, Holder<T> holder, PacketDataSerializer.b<T> packetdataserializer_b) {
        switch (holder.kind()) {
            case REFERENCE:
                int i = registry.getId(holder);

                if (i == -1) {
                    Object object = holder.value();

                    throw new IllegalArgumentException("Can't find id for '" + object + "' in map " + registry);
                }

                this.writeVarInt(i + 1);
                break;
            case DIRECT:
                this.writeVarInt(0);
                packetdataserializer_b.accept(this, holder.value());
        }

    }

    @Nullable
    public <T> T readById(Registry<T> registry) {
        int i = this.readVarInt();

        return registry.byId(i);
    }

    public <T> Holder<T> readById(Registry<Holder<T>> registry, PacketDataSerializer.a<T> packetdataserializer_a) {
        int i = this.readVarInt();

        if (i == 0) {
            return Holder.direct(packetdataserializer_a.apply(this));
        } else {
            Holder<T> holder = (Holder) registry.byId(i - 1);

            if (holder == null) {
                throw new IllegalArgumentException("Can't find element with id " + i);
            } else {
                return holder;
            }
        }
    }

    public static <T> IntFunction<T> limitValue(IntFunction<T> intfunction, int i) {
        return (j) -> {
            if (j > i) {
                throw new DecoderException("Value " + j + " is larger than limit " + i);
            } else {
                return intfunction.apply(j);
            }
        };
    }

    public <T, C extends Collection<T>> C readCollection(IntFunction<C> intfunction, PacketDataSerializer.a<T> packetdataserializer_a) {
        int i = this.readVarInt();
        C c0 = (Collection) intfunction.apply(i);

        for (int j = 0; j < i; ++j) {
            c0.add(packetdataserializer_a.apply(this));
        }

        return c0;
    }

    public <T> void writeCollection(Collection<T> collection, PacketDataSerializer.b<T> packetdataserializer_b) {
        this.writeVarInt(collection.size());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            T t0 = iterator.next();

            packetdataserializer_b.accept(this, t0);
        }

    }

    public <T> List<T> readList(PacketDataSerializer.a<T> packetdataserializer_a) {
        return (List) this.readCollection(Lists::newArrayListWithCapacity, packetdataserializer_a);
    }

    public IntList readIntIdList() {
        int i = this.readVarInt();
        IntArrayList intarraylist = new IntArrayList();

        for (int j = 0; j < i; ++j) {
            intarraylist.add(this.readVarInt());
        }

        return intarraylist;
    }

    public void writeIntIdList(IntList intlist) {
        this.writeVarInt(intlist.size());
        intlist.forEach(this::writeVarInt);
    }

    public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> intfunction, PacketDataSerializer.a<K> packetdataserializer_a, PacketDataSerializer.a<V> packetdataserializer_a1) {
        int i = this.readVarInt();
        M m0 = (Map) intfunction.apply(i);

        for (int j = 0; j < i; ++j) {
            K k0 = packetdataserializer_a.apply(this);
            V v0 = packetdataserializer_a1.apply(this);

            m0.put(k0, v0);
        }

        return m0;
    }

    public <K, V> Map<K, V> readMap(PacketDataSerializer.a<K> packetdataserializer_a, PacketDataSerializer.a<V> packetdataserializer_a1) {
        return this.readMap(Maps::newHashMapWithExpectedSize, packetdataserializer_a, packetdataserializer_a1);
    }

    public <K, V> void writeMap(Map<K, V> map, PacketDataSerializer.b<K> packetdataserializer_b, PacketDataSerializer.b<V> packetdataserializer_b1) {
        this.writeVarInt(map.size());
        map.forEach((object, object1) -> {
            packetdataserializer_b.accept(this, object);
            packetdataserializer_b1.accept(this, object1);
        });
    }

    public void readWithCount(Consumer<PacketDataSerializer> consumer) {
        int i = this.readVarInt();

        for (int j = 0; j < i; ++j) {
            consumer.accept(this);
        }

    }

    public <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumset, Class<E> oclass) {
        E[] ae = (Enum[]) oclass.getEnumConstants();
        BitSet bitset = new BitSet(ae.length);

        for (int i = 0; i < ae.length; ++i) {
            bitset.set(i, enumset.contains(ae[i]));
        }

        this.writeFixedBitSet(bitset, ae.length);
    }

    public <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> oclass) {
        E[] ae = (Enum[]) oclass.getEnumConstants();
        BitSet bitset = this.readFixedBitSet(ae.length);
        EnumSet<E> enumset = EnumSet.noneOf(oclass);

        for (int i = 0; i < ae.length; ++i) {
            if (bitset.get(i)) {
                enumset.add(ae[i]);
            }
        }

        return enumset;
    }

    public <T> void writeOptional(Optional<T> optional, PacketDataSerializer.b<T> packetdataserializer_b) {
        if (optional.isPresent()) {
            this.writeBoolean(true);
            packetdataserializer_b.accept(this, optional.get());
        } else {
            this.writeBoolean(false);
        }

    }

    public <T> Optional<T> readOptional(PacketDataSerializer.a<T> packetdataserializer_a) {
        return this.readBoolean() ? Optional.of(packetdataserializer_a.apply(this)) : Optional.empty();
    }

    @Nullable
    public <T> T readNullable(PacketDataSerializer.a<T> packetdataserializer_a) {
        return this.readBoolean() ? packetdataserializer_a.apply(this) : null;
    }

    public <T> void writeNullable(@Nullable T t0, PacketDataSerializer.b<T> packetdataserializer_b) {
        if (t0 != null) {
            this.writeBoolean(true);
            packetdataserializer_b.accept(this, t0);
        } else {
            this.writeBoolean(false);
        }

    }

    public <L, R> void writeEither(Either<L, R> either, PacketDataSerializer.b<L> packetdataserializer_b, PacketDataSerializer.b<R> packetdataserializer_b1) {
        either.ifLeft((object) -> {
            this.writeBoolean(true);
            packetdataserializer_b.accept(this, object);
        }).ifRight((object) -> {
            this.writeBoolean(false);
            packetdataserializer_b1.accept(this, object);
        });
    }

    public <L, R> Either<L, R> readEither(PacketDataSerializer.a<L> packetdataserializer_a, PacketDataSerializer.a<R> packetdataserializer_a1) {
        return this.readBoolean() ? Either.left(packetdataserializer_a.apply(this)) : Either.right(packetdataserializer_a1.apply(this));
    }

    public byte[] readByteArray() {
        return this.readByteArray(this.readableBytes());
    }

    public PacketDataSerializer writeByteArray(byte[] abyte) {
        this.writeVarInt(abyte.length);
        this.writeBytes(abyte);
        return this;
    }

    public byte[] readByteArray(int i) {
        int j = this.readVarInt();

        if (j > i) {
            throw new DecoderException("ByteArray with size " + j + " is bigger than allowed " + i);
        } else {
            byte[] abyte = new byte[j];

            this.readBytes(abyte);
            return abyte;
        }
    }

    public PacketDataSerializer writeVarIntArray(int[] aint) {
        this.writeVarInt(aint.length);
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint1[j];

            this.writeVarInt(k);
        }

        return this;
    }

    public int[] readVarIntArray() {
        return this.readVarIntArray(this.readableBytes());
    }

    public int[] readVarIntArray(int i) {
        int j = this.readVarInt();

        if (j > i) {
            throw new DecoderException("VarIntArray with size " + j + " is bigger than allowed " + i);
        } else {
            int[] aint = new int[j];

            for (int k = 0; k < aint.length; ++k) {
                aint[k] = this.readVarInt();
            }

            return aint;
        }
    }

    public PacketDataSerializer writeLongArray(long[] along) {
        this.writeVarInt(along.length);
        long[] along1 = along;
        int i = along.length;

        for (int j = 0; j < i; ++j) {
            long k = along1[j];

            this.writeLong(k);
        }

        return this;
    }

    public long[] readLongArray() {
        return this.readLongArray((long[]) null);
    }

    public long[] readLongArray(@Nullable long[] along) {
        return this.readLongArray(along, this.readableBytes() / 8);
    }

    public long[] readLongArray(@Nullable long[] along, int i) {
        int j = this.readVarInt();

        if (along == null || along.length != j) {
            if (j > i) {
                throw new DecoderException("LongArray with size " + j + " is bigger than allowed " + i);
            }

            along = new long[j];
        }

        for (int k = 0; k < along.length; ++k) {
            along[k] = this.readLong();
        }

        return along;
    }

    @VisibleForTesting
    public byte[] accessByteBufWithCorrectSize() {
        int i = this.writerIndex();
        byte[] abyte = new byte[i];

        this.getBytes(0, abyte);
        return abyte;
    }

    public BlockPosition readBlockPos() {
        return BlockPosition.of(this.readLong());
    }

    public PacketDataSerializer writeBlockPos(BlockPosition blockposition) {
        this.writeLong(blockposition.asLong());
        return this;
    }

    public ChunkCoordIntPair readChunkPos() {
        return new ChunkCoordIntPair(this.readLong());
    }

    public PacketDataSerializer writeChunkPos(ChunkCoordIntPair chunkcoordintpair) {
        this.writeLong(chunkcoordintpair.toLong());
        return this;
    }

    public SectionPosition readSectionPos() {
        return SectionPosition.of(this.readLong());
    }

    public PacketDataSerializer writeSectionPos(SectionPosition sectionposition) {
        this.writeLong(sectionposition.asLong());
        return this;
    }

    public GlobalPos readGlobalPos() {
        ResourceKey<World> resourcekey = this.readResourceKey(Registries.DIMENSION);
        BlockPosition blockposition = this.readBlockPos();

        return GlobalPos.of(resourcekey, blockposition);
    }

    public void writeGlobalPos(GlobalPos globalpos) {
        this.writeResourceKey(globalpos.dimension());
        this.writeBlockPos(globalpos.pos());
    }

    public Vector3f readVector3f() {
        return new Vector3f(this.readFloat(), this.readFloat(), this.readFloat());
    }

    public void writeVector3f(Vector3f vector3f) {
        this.writeFloat(vector3f.x());
        this.writeFloat(vector3f.y());
        this.writeFloat(vector3f.z());
    }

    public Quaternionf readQuaternion() {
        return new Quaternionf(this.readFloat(), this.readFloat(), this.readFloat(), this.readFloat());
    }

    public void writeQuaternion(Quaternionf quaternionf) {
        this.writeFloat(quaternionf.x);
        this.writeFloat(quaternionf.y);
        this.writeFloat(quaternionf.z);
        this.writeFloat(quaternionf.w);
    }

    public IChatBaseComponent readComponent() {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(this.readUtf(262144));

        if (ichatmutablecomponent == null) {
            throw new DecoderException("Received unexpected null component");
        } else {
            return ichatmutablecomponent;
        }
    }

    public PacketDataSerializer writeComponent(IChatBaseComponent ichatbasecomponent) {
        return this.writeUtf(IChatBaseComponent.ChatSerializer.toJson(ichatbasecomponent), 262144);
    }

    public <T extends Enum<T>> T readEnum(Class<T> oclass) {
        return ((Enum[]) oclass.getEnumConstants())[this.readVarInt()];
    }

    public PacketDataSerializer writeEnum(Enum<?> oenum) {
        return this.writeVarInt(oenum.ordinal());
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;

        byte b0;

        do {
            b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public long readVarLong() {
        long i = 0L;
        int j = 0;

        byte b0;

        do {
            b0 = this.readByte();
            i |= (long) (b0 & 127) << j++ * 7;
            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public PacketDataSerializer writeUUID(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public UUID readUUID() {
        return new UUID(this.readLong(), this.readLong());
    }

    public PacketDataSerializer writeVarInt(int i) {
        while ((i & -128) != 0) {
            this.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        this.writeByte(i);
        return this;
    }

    public PacketDataSerializer writeVarLong(long i) {
        while ((i & -128L) != 0L) {
            this.writeByte((int) (i & 127L) | 128);
            i >>>= 7;
        }

        this.writeByte((int) i);
        return this;
    }

    public PacketDataSerializer writeNbt(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            this.writeByte(0);
        } else {
            try {
                NBTCompressedStreamTools.write(nbttagcompound, (DataOutput) (new ByteBufOutputStream(this)));
            } catch (IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }

        return this;
    }

    @Nullable
    public NBTTagCompound readNbt() {
        return this.readNbt(new NBTReadLimiter(2097152L));
    }

    @Nullable
    public NBTTagCompound readAnySizeNbt() {
        return this.readNbt(NBTReadLimiter.UNLIMITED);
    }

    @Nullable
    public NBTTagCompound readNbt(NBTReadLimiter nbtreadlimiter) {
        int i = this.readerIndex();
        byte b0 = this.readByte();

        if (b0 == 0) {
            return null;
        } else {
            this.readerIndex(i);

            try {
                return NBTCompressedStreamTools.read(new ByteBufInputStream(this), nbtreadlimiter);
            } catch (IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }
    }

    public PacketDataSerializer writeItem(ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            Item item = itemstack.getItem();

            this.writeId(BuiltInRegistries.ITEM, item);
            this.writeByte(itemstack.getCount());
            NBTTagCompound nbttagcompound = null;

            if (item.canBeDepleted() || item.shouldOverrideMultiplayerNbt()) {
                nbttagcompound = itemstack.getTag();
            }

            this.writeNbt(nbttagcompound);
        }

        return this;
    }

    public ItemStack readItem() {
        if (!this.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            Item item = (Item) this.readById(BuiltInRegistries.ITEM);
            byte b0 = this.readByte();
            ItemStack itemstack = new ItemStack(item, b0);

            itemstack.setTag(this.readNbt());
            return itemstack;
        }
    }

    public String readUtf() {
        return this.readUtf(32767);
    }

    public String readUtf(int i) {
        int j = getMaxEncodedUtfLength(i);
        int k = this.readVarInt();

        if (k > j) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + k + " > " + j + ")");
        } else if (k < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String s = this.toString(this.readerIndex(), k, StandardCharsets.UTF_8);

            this.readerIndex(this.readerIndex() + k);
            if (s.length() > i) {
                int l = s.length();

                throw new DecoderException("The received string length is longer than maximum allowed (" + l + " > " + i + ")");
            } else {
                return s;
            }
        }
    }

    public PacketDataSerializer writeUtf(String s) {
        return this.writeUtf(s, 32767);
    }

    public PacketDataSerializer writeUtf(String s, int i) {
        if (s.length() > i) {
            int j = s.length();

            throw new EncoderException("String too big (was " + j + " characters, max " + i + ")");
        } else {
            byte[] abyte = s.getBytes(StandardCharsets.UTF_8);
            int k = getMaxEncodedUtfLength(i);

            if (abyte.length > k) {
                throw new EncoderException("String too big (was " + abyte.length + " bytes encoded, max " + k + ")");
            } else {
                this.writeVarInt(abyte.length);
                this.writeBytes(abyte);
                return this;
            }
        }
    }

    private static int getMaxEncodedUtfLength(int i) {
        return i * 3;
    }

    public MinecraftKey readResourceLocation() {
        return new MinecraftKey(this.readUtf(32767));
    }

    public PacketDataSerializer writeResourceLocation(MinecraftKey minecraftkey) {
        this.writeUtf(minecraftkey.toString());
        return this;
    }

    public <T> ResourceKey<T> readResourceKey(ResourceKey<? extends IRegistry<T>> resourcekey) {
        MinecraftKey minecraftkey = this.readResourceLocation();

        return ResourceKey.create(resourcekey, minecraftkey);
    }

    public void writeResourceKey(ResourceKey<?> resourcekey) {
        this.writeResourceLocation(resourcekey.location());
    }

    public Date readDate() {
        return new Date(this.readLong());
    }

    public PacketDataSerializer writeDate(Date date) {
        this.writeLong(date.getTime());
        return this;
    }

    public Instant readInstant() {
        return Instant.ofEpochMilli(this.readLong());
    }

    public void writeInstant(Instant instant) {
        this.writeLong(instant.toEpochMilli());
    }

    public PublicKey readPublicKey() {
        try {
            return MinecraftEncryption.byteToPublicKey(this.readByteArray(512));
        } catch (CryptographyException cryptographyexception) {
            throw new DecoderException("Malformed public key bytes", cryptographyexception);
        }
    }

    public PacketDataSerializer writePublicKey(PublicKey publickey) {
        this.writeByteArray(publickey.getEncoded());
        return this;
    }

    public MovingObjectPositionBlock readBlockHitResult() {
        BlockPosition blockposition = this.readBlockPos();
        EnumDirection enumdirection = (EnumDirection) this.readEnum(EnumDirection.class);
        float f = this.readFloat();
        float f1 = this.readFloat();
        float f2 = this.readFloat();
        boolean flag = this.readBoolean();

        return new MovingObjectPositionBlock(new Vec3D((double) blockposition.getX() + (double) f, (double) blockposition.getY() + (double) f1, (double) blockposition.getZ() + (double) f2), enumdirection, blockposition, flag);
    }

    public void writeBlockHitResult(MovingObjectPositionBlock movingobjectpositionblock) {
        BlockPosition blockposition = movingobjectpositionblock.getBlockPos();

        this.writeBlockPos(blockposition);
        this.writeEnum(movingobjectpositionblock.getDirection());
        Vec3D vec3d = movingobjectpositionblock.getLocation();

        this.writeFloat((float) (vec3d.x - (double) blockposition.getX()));
        this.writeFloat((float) (vec3d.y - (double) blockposition.getY()));
        this.writeFloat((float) (vec3d.z - (double) blockposition.getZ()));
        this.writeBoolean(movingobjectpositionblock.isInside());
    }

    public BitSet readBitSet() {
        return BitSet.valueOf(this.readLongArray());
    }

    public void writeBitSet(BitSet bitset) {
        this.writeLongArray(bitset.toLongArray());
    }

    public BitSet readFixedBitSet(int i) {
        byte[] abyte = new byte[MathHelper.positiveCeilDiv(i, 8)];

        this.readBytes(abyte);
        return BitSet.valueOf(abyte);
    }

    public void writeFixedBitSet(BitSet bitset, int i) {
        if (bitset.length() > i) {
            int j = bitset.length();

            throw new EncoderException("BitSet is larger than expected size (" + j + ">" + i + ")");
        } else {
            byte[] abyte = bitset.toByteArray();

            this.writeBytes(Arrays.copyOf(abyte, MathHelper.positiveCeilDiv(i, 8)));
        }
    }

    public GameProfile readGameProfile() {
        UUID uuid = this.readUUID();
        String s = this.readUtf(16);
        GameProfile gameprofile = new GameProfile(uuid, s);

        gameprofile.getProperties().putAll(this.readGameProfileProperties());
        return gameprofile;
    }

    public void writeGameProfile(GameProfile gameprofile) {
        this.writeUUID(gameprofile.getId());
        this.writeUtf(gameprofile.getName());
        this.writeGameProfileProperties(gameprofile.getProperties());
    }

    public PropertyMap readGameProfileProperties() {
        PropertyMap propertymap = new PropertyMap();

        this.readWithCount((packetdataserializer) -> {
            Property property = this.readProperty();

            propertymap.put(property.getName(), property);
        });
        return propertymap;
    }

    public void writeGameProfileProperties(PropertyMap propertymap) {
        this.writeCollection(propertymap.values(), PacketDataSerializer::writeProperty);
    }

    public Property readProperty() {
        String s = this.readUtf();
        String s1 = this.readUtf();

        if (this.readBoolean()) {
            String s2 = this.readUtf();

            return new Property(s, s1, s2);
        } else {
            return new Property(s, s1);
        }
    }

    public void writeProperty(Property property) {
        this.writeUtf(property.getName());
        this.writeUtf(property.getValue());
        if (property.hasSignature()) {
            this.writeBoolean(true);
            this.writeUtf(property.getSignature());
        } else {
            this.writeBoolean(false);
        }

    }

    public int capacity() {
        return this.source.capacity();
    }

    public ByteBuf capacity(int i) {
        return this.source.capacity(i);
    }

    public int maxCapacity() {
        return this.source.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.source.alloc();
    }

    public ByteOrder order() {
        return this.source.order();
    }

    public ByteBuf order(ByteOrder byteorder) {
        return this.source.order(byteorder);
    }

    public ByteBuf unwrap() {
        return this.source.unwrap();
    }

    public boolean isDirect() {
        return this.source.isDirect();
    }

    public boolean isReadOnly() {
        return this.source.isReadOnly();
    }

    public ByteBuf asReadOnly() {
        return this.source.asReadOnly();
    }

    public int readerIndex() {
        return this.source.readerIndex();
    }

    public ByteBuf readerIndex(int i) {
        return this.source.readerIndex(i);
    }

    public int writerIndex() {
        return this.source.writerIndex();
    }

    public ByteBuf writerIndex(int i) {
        return this.source.writerIndex(i);
    }

    public ByteBuf setIndex(int i, int j) {
        return this.source.setIndex(i, j);
    }

    public int readableBytes() {
        return this.source.readableBytes();
    }

    public int writableBytes() {
        return this.source.writableBytes();
    }

    public int maxWritableBytes() {
        return this.source.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.source.isReadable();
    }

    public boolean isReadable(int i) {
        return this.source.isReadable(i);
    }

    public boolean isWritable() {
        return this.source.isWritable();
    }

    public boolean isWritable(int i) {
        return this.source.isWritable(i);
    }

    public ByteBuf clear() {
        return this.source.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.source.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.source.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.source.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.source.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.source.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.source.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int i) {
        return this.source.ensureWritable(i);
    }

    public int ensureWritable(int i, boolean flag) {
        return this.source.ensureWritable(i, flag);
    }

    public boolean getBoolean(int i) {
        return this.source.getBoolean(i);
    }

    public byte getByte(int i) {
        return this.source.getByte(i);
    }

    public short getUnsignedByte(int i) {
        return this.source.getUnsignedByte(i);
    }

    public short getShort(int i) {
        return this.source.getShort(i);
    }

    public short getShortLE(int i) {
        return this.source.getShortLE(i);
    }

    public int getUnsignedShort(int i) {
        return this.source.getUnsignedShort(i);
    }

    public int getUnsignedShortLE(int i) {
        return this.source.getUnsignedShortLE(i);
    }

    public int getMedium(int i) {
        return this.source.getMedium(i);
    }

    public int getMediumLE(int i) {
        return this.source.getMediumLE(i);
    }

    public int getUnsignedMedium(int i) {
        return this.source.getUnsignedMedium(i);
    }

    public int getUnsignedMediumLE(int i) {
        return this.source.getUnsignedMediumLE(i);
    }

    public int getInt(int i) {
        return this.source.getInt(i);
    }

    public int getIntLE(int i) {
        return this.source.getIntLE(i);
    }

    public long getUnsignedInt(int i) {
        return this.source.getUnsignedInt(i);
    }

    public long getUnsignedIntLE(int i) {
        return this.source.getUnsignedIntLE(i);
    }

    public long getLong(int i) {
        return this.source.getLong(i);
    }

    public long getLongLE(int i) {
        return this.source.getLongLE(i);
    }

    public char getChar(int i) {
        return this.source.getChar(i);
    }

    public float getFloat(int i) {
        return this.source.getFloat(i);
    }

    public double getDouble(int i) {
        return this.source.getDouble(i);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        return this.source.getBytes(i, bytebuf);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        return this.source.getBytes(i, bytebuf, j);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.source.getBytes(i, bytebuf, j, k);
    }

    public ByteBuf getBytes(int i, byte[] abyte) {
        return this.source.getBytes(i, abyte);
    }

    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        return this.source.getBytes(i, abyte, j, k);
    }

    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        return this.source.getBytes(i, bytebuffer);
    }

    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        return this.source.getBytes(i, outputstream, j);
    }

    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.source.getBytes(i, gatheringbytechannel, j);
    }

    public int getBytes(int i, FileChannel filechannel, long j, int k) throws IOException {
        return this.source.getBytes(i, filechannel, j, k);
    }

    public CharSequence getCharSequence(int i, int j, Charset charset) {
        return this.source.getCharSequence(i, j, charset);
    }

    public ByteBuf setBoolean(int i, boolean flag) {
        return this.source.setBoolean(i, flag);
    }

    public ByteBuf setByte(int i, int j) {
        return this.source.setByte(i, j);
    }

    public ByteBuf setShort(int i, int j) {
        return this.source.setShort(i, j);
    }

    public ByteBuf setShortLE(int i, int j) {
        return this.source.setShortLE(i, j);
    }

    public ByteBuf setMedium(int i, int j) {
        return this.source.setMedium(i, j);
    }

    public ByteBuf setMediumLE(int i, int j) {
        return this.source.setMediumLE(i, j);
    }

    public ByteBuf setInt(int i, int j) {
        return this.source.setInt(i, j);
    }

    public ByteBuf setIntLE(int i, int j) {
        return this.source.setIntLE(i, j);
    }

    public ByteBuf setLong(int i, long j) {
        return this.source.setLong(i, j);
    }

    public ByteBuf setLongLE(int i, long j) {
        return this.source.setLongLE(i, j);
    }

    public ByteBuf setChar(int i, int j) {
        return this.source.setChar(i, j);
    }

    public ByteBuf setFloat(int i, float f) {
        return this.source.setFloat(i, f);
    }

    public ByteBuf setDouble(int i, double d0) {
        return this.source.setDouble(i, d0);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        return this.source.setBytes(i, bytebuf);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        return this.source.setBytes(i, bytebuf, j);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.source.setBytes(i, bytebuf, j, k);
    }

    public ByteBuf setBytes(int i, byte[] abyte) {
        return this.source.setBytes(i, abyte);
    }

    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        return this.source.setBytes(i, abyte, j, k);
    }

    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        return this.source.setBytes(i, bytebuffer);
    }

    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        return this.source.setBytes(i, inputstream, j);
    }

    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        return this.source.setBytes(i, scatteringbytechannel, j);
    }

    public int setBytes(int i, FileChannel filechannel, long j, int k) throws IOException {
        return this.source.setBytes(i, filechannel, j, k);
    }

    public ByteBuf setZero(int i, int j) {
        return this.source.setZero(i, j);
    }

    public int setCharSequence(int i, CharSequence charsequence, Charset charset) {
        return this.source.setCharSequence(i, charsequence, charset);
    }

    public boolean readBoolean() {
        return this.source.readBoolean();
    }

    public byte readByte() {
        return this.source.readByte();
    }

    public short readUnsignedByte() {
        return this.source.readUnsignedByte();
    }

    public short readShort() {
        return this.source.readShort();
    }

    public short readShortLE() {
        return this.source.readShortLE();
    }

    public int readUnsignedShort() {
        return this.source.readUnsignedShort();
    }

    public int readUnsignedShortLE() {
        return this.source.readUnsignedShortLE();
    }

    public int readMedium() {
        return this.source.readMedium();
    }

    public int readMediumLE() {
        return this.source.readMediumLE();
    }

    public int readUnsignedMedium() {
        return this.source.readUnsignedMedium();
    }

    public int readUnsignedMediumLE() {
        return this.source.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.source.readInt();
    }

    public int readIntLE() {
        return this.source.readIntLE();
    }

    public long readUnsignedInt() {
        return this.source.readUnsignedInt();
    }

    public long readUnsignedIntLE() {
        return this.source.readUnsignedIntLE();
    }

    public long readLong() {
        return this.source.readLong();
    }

    public long readLongLE() {
        return this.source.readLongLE();
    }

    public char readChar() {
        return this.source.readChar();
    }

    public float readFloat() {
        return this.source.readFloat();
    }

    public double readDouble() {
        return this.source.readDouble();
    }

    public ByteBuf readBytes(int i) {
        return this.source.readBytes(i);
    }

    public ByteBuf readSlice(int i) {
        return this.source.readSlice(i);
    }

    public ByteBuf readRetainedSlice(int i) {
        return this.source.readRetainedSlice(i);
    }

    public ByteBuf readBytes(ByteBuf bytebuf) {
        return this.source.readBytes(bytebuf);
    }

    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        return this.source.readBytes(bytebuf, i);
    }

    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        return this.source.readBytes(bytebuf, i, j);
    }

    public ByteBuf readBytes(byte[] abyte) {
        return this.source.readBytes(abyte);
    }

    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        return this.source.readBytes(abyte, i, j);
    }

    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        return this.source.readBytes(bytebuffer);
    }

    public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
        return this.source.readBytes(outputstream, i);
    }

    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
        return this.source.readBytes(gatheringbytechannel, i);
    }

    public CharSequence readCharSequence(int i, Charset charset) {
        return this.source.readCharSequence(i, charset);
    }

    public int readBytes(FileChannel filechannel, long i, int j) throws IOException {
        return this.source.readBytes(filechannel, i, j);
    }

    public ByteBuf skipBytes(int i) {
        return this.source.skipBytes(i);
    }

    public ByteBuf writeBoolean(boolean flag) {
        return this.source.writeBoolean(flag);
    }

    public ByteBuf writeByte(int i) {
        return this.source.writeByte(i);
    }

    public ByteBuf writeShort(int i) {
        return this.source.writeShort(i);
    }

    public ByteBuf writeShortLE(int i) {
        return this.source.writeShortLE(i);
    }

    public ByteBuf writeMedium(int i) {
        return this.source.writeMedium(i);
    }

    public ByteBuf writeMediumLE(int i) {
        return this.source.writeMediumLE(i);
    }

    public ByteBuf writeInt(int i) {
        return this.source.writeInt(i);
    }

    public ByteBuf writeIntLE(int i) {
        return this.source.writeIntLE(i);
    }

    public ByteBuf writeLong(long i) {
        return this.source.writeLong(i);
    }

    public ByteBuf writeLongLE(long i) {
        return this.source.writeLongLE(i);
    }

    public ByteBuf writeChar(int i) {
        return this.source.writeChar(i);
    }

    public ByteBuf writeFloat(float f) {
        return this.source.writeFloat(f);
    }

    public ByteBuf writeDouble(double d0) {
        return this.source.writeDouble(d0);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf) {
        return this.source.writeBytes(bytebuf);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        return this.source.writeBytes(bytebuf, i);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        return this.source.writeBytes(bytebuf, i, j);
    }

    public ByteBuf writeBytes(byte[] abyte) {
        return this.source.writeBytes(abyte);
    }

    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        return this.source.writeBytes(abyte, i, j);
    }

    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        return this.source.writeBytes(bytebuffer);
    }

    public int writeBytes(InputStream inputstream, int i) throws IOException {
        return this.source.writeBytes(inputstream, i);
    }

    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException {
        return this.source.writeBytes(scatteringbytechannel, i);
    }

    public int writeBytes(FileChannel filechannel, long i, int j) throws IOException {
        return this.source.writeBytes(filechannel, i, j);
    }

    public ByteBuf writeZero(int i) {
        return this.source.writeZero(i);
    }

    public int writeCharSequence(CharSequence charsequence, Charset charset) {
        return this.source.writeCharSequence(charsequence, charset);
    }

    public int indexOf(int i, int j, byte b0) {
        return this.source.indexOf(i, j, b0);
    }

    public int bytesBefore(byte b0) {
        return this.source.bytesBefore(b0);
    }

    public int bytesBefore(int i, byte b0) {
        return this.source.bytesBefore(i, b0);
    }

    public int bytesBefore(int i, int j, byte b0) {
        return this.source.bytesBefore(i, j, b0);
    }

    public int forEachByte(ByteProcessor byteprocessor) {
        return this.source.forEachByte(byteprocessor);
    }

    public int forEachByte(int i, int j, ByteProcessor byteprocessor) {
        return this.source.forEachByte(i, j, byteprocessor);
    }

    public int forEachByteDesc(ByteProcessor byteprocessor) {
        return this.source.forEachByteDesc(byteprocessor);
    }

    public int forEachByteDesc(int i, int j, ByteProcessor byteprocessor) {
        return this.source.forEachByteDesc(i, j, byteprocessor);
    }

    public ByteBuf copy() {
        return this.source.copy();
    }

    public ByteBuf copy(int i, int j) {
        return this.source.copy(i, j);
    }

    public ByteBuf slice() {
        return this.source.slice();
    }

    public ByteBuf retainedSlice() {
        return this.source.retainedSlice();
    }

    public ByteBuf slice(int i, int j) {
        return this.source.slice(i, j);
    }

    public ByteBuf retainedSlice(int i, int j) {
        return this.source.retainedSlice(i, j);
    }

    public ByteBuf duplicate() {
        return this.source.duplicate();
    }

    public ByteBuf retainedDuplicate() {
        return this.source.retainedDuplicate();
    }

    public int nioBufferCount() {
        return this.source.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.source.nioBuffer();
    }

    public ByteBuffer nioBuffer(int i, int j) {
        return this.source.nioBuffer(i, j);
    }

    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.source.internalNioBuffer(i, j);
    }

    public ByteBuffer[] nioBuffers() {
        return this.source.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int i, int j) {
        return this.source.nioBuffers(i, j);
    }

    public boolean hasArray() {
        return this.source.hasArray();
    }

    public byte[] array() {
        return this.source.array();
    }

    public int arrayOffset() {
        return this.source.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.source.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.source.memoryAddress();
    }

    public String toString(Charset charset) {
        return this.source.toString(charset);
    }

    public String toString(int i, int j, Charset charset) {
        return this.source.toString(i, j, charset);
    }

    public int hashCode() {
        return this.source.hashCode();
    }

    public boolean equals(Object object) {
        return this.source.equals(object);
    }

    public int compareTo(ByteBuf bytebuf) {
        return this.source.compareTo(bytebuf);
    }

    public String toString() {
        return this.source.toString();
    }

    public ByteBuf retain(int i) {
        return this.source.retain(i);
    }

    public ByteBuf retain() {
        return this.source.retain();
    }

    public ByteBuf touch() {
        return this.source.touch();
    }

    public ByteBuf touch(Object object) {
        return this.source.touch(object);
    }

    public int refCnt() {
        return this.source.refCnt();
    }

    public boolean release() {
        return this.source.release();
    }

    public boolean release(int i) {
        return this.source.release(i);
    }

    @FunctionalInterface
    public interface b<T> extends BiConsumer<PacketDataSerializer, T> {

        default PacketDataSerializer.b<Optional<T>> asOptional() {
            return (packetdataserializer, optional) -> {
                packetdataserializer.writeOptional(optional, this);
            };
        }
    }

    @FunctionalInterface
    public interface a<T> extends Function<PacketDataSerializer, T> {

        default PacketDataSerializer.a<Optional<T>> asOptional() {
            return (packetdataserializer) -> {
                return packetdataserializer.readOptional(this);
            };
        }
    }
}
