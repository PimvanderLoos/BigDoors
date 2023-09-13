package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.MinecraftSerializableUUID;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.UtilColor;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.IBlockDataHolder;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class GameProfileSerializer {

    private static final Comparator<NBTTagList> YXZ_LISTTAG_INT_COMPARATOR = Comparator.comparingInt((nbttaglist) -> {
        return nbttaglist.e(1);
    }).thenComparingInt((nbttaglist) -> {
        return nbttaglist.e(0);
    }).thenComparingInt((nbttaglist) -> {
        return nbttaglist.e(2);
    });
    private static final Comparator<NBTTagList> YXZ_LISTTAG_DOUBLE_COMPARATOR = Comparator.comparingDouble((nbttaglist) -> {
        return nbttaglist.h(1);
    }).thenComparingDouble((nbttaglist) -> {
        return nbttaglist.h(0);
    }).thenComparingDouble((nbttaglist) -> {
        return nbttaglist.h(2);
    });
    public static final String SNBT_DATA_TAG = "data";
    private static final char PROPERTIES_START = '{';
    private static final char PROPERTIES_END = '}';
    private static final String ELEMENT_SEPARATOR = ",";
    private static final char KEY_VALUE_SEPARATOR = ':';
    private static final Splitter COMMA_SPLITTER = Splitter.on(",");
    private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int INDENT = 2;
    private static final int NOT_FOUND = -1;

    private GameProfileSerializer() {}

    @Nullable
    public static GameProfile deserialize(NBTTagCompound nbttagcompound) {
        String s = null;
        UUID uuid = null;

        if (nbttagcompound.hasKeyOfType("Name", 8)) {
            s = nbttagcompound.getString("Name");
        }

        if (nbttagcompound.b("Id")) {
            uuid = nbttagcompound.a("Id");
        }

        try {
            GameProfile gameprofile = new GameProfile(uuid, s);

            if (nbttagcompound.hasKeyOfType("Properties", 10)) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Properties");
                Iterator iterator = nbttagcompound1.getKeys().iterator();

                while (iterator.hasNext()) {
                    String s1 = (String) iterator.next();
                    NBTTagList nbttaglist = nbttagcompound1.getList(s1, 10);

                    for (int i = 0; i < nbttaglist.size(); ++i) {
                        NBTTagCompound nbttagcompound2 = nbttaglist.getCompound(i);
                        String s2 = nbttagcompound2.getString("Value");

                        if (nbttagcompound2.hasKeyOfType("Signature", 8)) {
                            gameprofile.getProperties().put(s1, new Property(s1, s2, nbttagcompound2.getString("Signature")));
                        } else {
                            gameprofile.getProperties().put(s1, new Property(s1, s2));
                        }
                    }
                }
            }

            return gameprofile;
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static NBTTagCompound serialize(NBTTagCompound nbttagcompound, GameProfile gameprofile) {
        if (!UtilColor.b(gameprofile.getName())) {
            nbttagcompound.setString("Name", gameprofile.getName());
        }

        if (gameprofile.getId() != null) {
            nbttagcompound.a("Id", gameprofile.getId());
        }

        if (!gameprofile.getProperties().isEmpty()) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            Iterator iterator = gameprofile.getProperties().keySet().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                NBTTagList nbttaglist = new NBTTagList();

                NBTTagCompound nbttagcompound2;

                for (Iterator iterator1 = gameprofile.getProperties().get(s).iterator(); iterator1.hasNext(); nbttaglist.add(nbttagcompound2)) {
                    Property property = (Property) iterator1.next();

                    nbttagcompound2 = new NBTTagCompound();
                    nbttagcompound2.setString("Value", property.getValue());
                    if (property.hasSignature()) {
                        nbttagcompound2.setString("Signature", property.getSignature());
                    }
                }

                nbttagcompound1.set(s, nbttaglist);
            }

            nbttagcompound.set("Properties", nbttagcompound1);
        }

        return nbttagcompound;
    }

    @VisibleForTesting
    public static boolean a(@Nullable NBTBase nbtbase, @Nullable NBTBase nbtbase1, boolean flag) {
        if (nbtbase == nbtbase1) {
            return true;
        } else if (nbtbase == null) {
            return true;
        } else if (nbtbase1 == null) {
            return false;
        } else if (!nbtbase.getClass().equals(nbtbase1.getClass())) {
            return false;
        } else if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase1;
            Iterator iterator = nbttagcompound.getKeys().iterator();

            String s;
            NBTBase nbtbase2;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                s = (String) iterator.next();
                nbtbase2 = nbttagcompound.get(s);
            } while (a(nbtbase2, nbttagcompound1.get(s), flag));

            return false;
        } else if (nbtbase instanceof NBTTagList && flag) {
            NBTTagList nbttaglist = (NBTTagList) nbtbase;
            NBTTagList nbttaglist1 = (NBTTagList) nbtbase1;

            if (nbttaglist.isEmpty()) {
                return nbttaglist1.isEmpty();
            } else {
                int i = 0;

                while (i < nbttaglist.size()) {
                    NBTBase nbtbase3 = nbttaglist.get(i);
                    boolean flag1 = false;
                    int j = 0;

                    while (true) {
                        if (j < nbttaglist1.size()) {
                            if (!a(nbtbase3, nbttaglist1.get(j), flag)) {
                                ++j;
                                continue;
                            }

                            flag1 = true;
                        }

                        if (!flag1) {
                            return false;
                        }

                        ++i;
                        break;
                    }
                }

                return true;
            }
        } else {
            return nbtbase.equals(nbtbase1);
        }
    }

    public static NBTTagIntArray a(UUID uuid) {
        return new NBTTagIntArray(MinecraftSerializableUUID.a(uuid));
    }

    public static UUID a(NBTBase nbtbase) {
        if (nbtbase.b() != NBTTagIntArray.TYPE) {
            String s = NBTTagIntArray.TYPE.a();

            throw new IllegalArgumentException("Expected UUID-Tag to be of type " + s + ", but found " + nbtbase.b().a() + ".");
        } else {
            int[] aint = ((NBTTagIntArray) nbtbase).getInts();

            if (aint.length != 4) {
                throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + aint.length + ".");
            } else {
                return MinecraftSerializableUUID.a(aint);
            }
        }
    }

    public static BlockPosition b(NBTTagCompound nbttagcompound) {
        return new BlockPosition(nbttagcompound.getInt("X"), nbttagcompound.getInt("Y"), nbttagcompound.getInt("Z"));
    }

    public static NBTTagCompound a(BlockPosition blockposition) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setInt("X", blockposition.getX());
        nbttagcompound.setInt("Y", blockposition.getY());
        nbttagcompound.setInt("Z", blockposition.getZ());
        return nbttagcompound;
    }

    public static IBlockData c(NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.hasKeyOfType("Name", 8)) {
            return Blocks.AIR.getBlockData();
        } else {
            Block block = (Block) IRegistry.BLOCK.get(new MinecraftKey(nbttagcompound.getString("Name")));
            IBlockData iblockdata = block.getBlockData();

            if (nbttagcompound.hasKeyOfType("Properties", 10)) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Properties");
                BlockStateList<Block, IBlockData> blockstatelist = block.getStates();
                Iterator iterator = nbttagcompound1.getKeys().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    IBlockState<?> iblockstate = blockstatelist.a(s);

                    if (iblockstate != null) {
                        iblockdata = (IBlockData) a(iblockdata, iblockstate, s, nbttagcompound1, nbttagcompound);
                    }
                }
            }

            return iblockdata;
        }
    }

    private static <S extends IBlockDataHolder<?, S>, T extends Comparable<T>> S a(S s0, IBlockState<T> iblockstate, String s, NBTTagCompound nbttagcompound, NBTTagCompound nbttagcompound1) {
        Optional<T> optional = iblockstate.b(nbttagcompound.getString(s));

        if (optional.isPresent()) {
            return (IBlockDataHolder) s0.set(iblockstate, (Comparable) optional.get());
        } else {
            GameProfileSerializer.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", s, nbttagcompound.getString(s), nbttagcompound1.toString());
            return s0;
        }
    }

    public static NBTTagCompound a(IBlockData iblockdata) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", IRegistry.BLOCK.getKey(iblockdata.getBlock()).toString());
        ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap = iblockdata.getStateMap();

        if (!immutablemap.isEmpty()) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            UnmodifiableIterator unmodifiableiterator = immutablemap.entrySet().iterator();

            while (unmodifiableiterator.hasNext()) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();

                nbttagcompound1.setString(iblockstate.getName(), a(iblockstate, (Comparable) entry.getValue()));
            }

            nbttagcompound.set("Properties", nbttagcompound1);
        }

        return nbttagcompound;
    }

    public static NBTTagCompound a(Fluid fluid) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", IRegistry.FLUID.getKey(fluid.getType()).toString());
        ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap = fluid.getStateMap();

        if (!immutablemap.isEmpty()) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            UnmodifiableIterator unmodifiableiterator = immutablemap.entrySet().iterator();

            while (unmodifiableiterator.hasNext()) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();

                nbttagcompound1.setString(iblockstate.getName(), a(iblockstate, (Comparable) entry.getValue()));
            }

            nbttagcompound.set("Properties", nbttagcompound1);
        }

        return nbttagcompound;
    }

    private static <T extends Comparable<T>> String a(IBlockState<T> iblockstate, Comparable<?> comparable) {
        return iblockstate.a(comparable);
    }

    public static String b(NBTBase nbtbase) {
        return a(nbtbase, false);
    }

    public static String a(NBTBase nbtbase, boolean flag) {
        return a(new StringBuilder(), nbtbase, 0, flag).toString();
    }

    public static StringBuilder a(StringBuilder stringbuilder, NBTBase nbtbase, int i, boolean flag) {
        int j;
        int k;
        String s;
        int l;
        int i1;
        int j1;

        switch (nbtbase.getTypeId()) {
            case 0:
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
                stringbuilder.append(nbtbase);
                break;
            case 7:
                NBTTagByteArray nbttagbytearray = (NBTTagByteArray) nbtbase;
                byte[] abyte = nbttagbytearray.getBytes();

                j = abyte.length;
                a(i, stringbuilder).append("byte[").append(j).append("] {\n");
                if (flag) {
                    a(i + 1, stringbuilder);

                    for (k = 0; k < abyte.length; ++k) {
                        if (k != 0) {
                            stringbuilder.append(',');
                        }

                        if (k % 16 == 0 && k / 16 > 0) {
                            stringbuilder.append('\n');
                            if (k < abyte.length) {
                                a(i + 1, stringbuilder);
                            }
                        } else if (k != 0) {
                            stringbuilder.append(' ');
                        }

                        stringbuilder.append(String.format("0x%02X", abyte[k] & 255));
                    }
                } else {
                    a(i + 1, stringbuilder).append(" // Skipped, supply withBinaryBlobs true");
                }

                stringbuilder.append('\n');
                a(i, stringbuilder).append('}');
                break;
            case 9:
                NBTTagList nbttaglist = (NBTTagList) nbtbase;
                int k1 = nbttaglist.size();
                byte b0 = nbttaglist.e();

                s = b0 == 0 ? "undefined" : NBTTagTypes.a(b0).b();
                a(i, stringbuilder).append("list<").append(s).append(">[").append(k1).append("] [");
                if (k1 != 0) {
                    stringbuilder.append('\n');
                }

                for (l = 0; l < k1; ++l) {
                    if (l != 0) {
                        stringbuilder.append(",\n");
                    }

                    a(i + 1, stringbuilder);
                    a(stringbuilder, nbttaglist.get(l), i + 1, flag);
                }

                if (k1 != 0) {
                    stringbuilder.append('\n');
                }

                a(i, stringbuilder).append(']');
                break;
            case 10:
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                List<String> list = Lists.newArrayList(nbttagcompound.getKeys());

                Collections.sort(list);
                a(i, stringbuilder).append('{');
                if (stringbuilder.length() - stringbuilder.lastIndexOf("\n") > 2 * (i + 1)) {
                    stringbuilder.append('\n');
                    a(i + 1, stringbuilder);
                }

                j = list.stream().mapToInt(String::length).max().orElse(0);
                s = Strings.repeat(" ", j);

                for (l = 0; l < list.size(); ++l) {
                    if (l != 0) {
                        stringbuilder.append(",\n");
                    }

                    String s1 = (String) list.get(l);

                    a(i + 1, stringbuilder).append('"').append(s1).append('"').append(s, 0, s.length() - s1.length()).append(": ");
                    a(stringbuilder, nbttagcompound.get(s1), i + 1, flag);
                }

                if (!list.isEmpty()) {
                    stringbuilder.append('\n');
                }

                a(i, stringbuilder).append('}');
                break;
            case 11:
                NBTTagIntArray nbttagintarray = (NBTTagIntArray) nbtbase;
                int[] aint = nbttagintarray.getInts();

                j = 0;
                int[] aint1 = aint;

                l = aint.length;

                for (j1 = 0; j1 < l; ++j1) {
                    i1 = aint1[j1];
                    j = Math.max(j, String.format("%X", i1).length());
                }

                k = aint.length;
                a(i, stringbuilder).append("int[").append(k).append("] {\n");
                if (flag) {
                    a(i + 1, stringbuilder);

                    for (l = 0; l < aint.length; ++l) {
                        if (l != 0) {
                            stringbuilder.append(',');
                        }

                        if (l % 16 == 0 && l / 16 > 0) {
                            stringbuilder.append('\n');
                            if (l < aint.length) {
                                a(i + 1, stringbuilder);
                            }
                        } else if (l != 0) {
                            stringbuilder.append(' ');
                        }

                        stringbuilder.append(String.format("0x%0" + j + "X", aint[l]));
                    }
                } else {
                    a(i + 1, stringbuilder).append(" // Skipped, supply withBinaryBlobs true");
                }

                stringbuilder.append('\n');
                a(i, stringbuilder).append('}');
                break;
            case 12:
                NBTTagLongArray nbttaglongarray = (NBTTagLongArray) nbtbase;
                long[] along = nbttaglongarray.getLongs();
                long l1 = 0L;
                long[] along1 = along;

                j1 = along.length;

                for (i1 = 0; i1 < j1; ++i1) {
                    long i2 = along1[i1];

                    l1 = Math.max(l1, (long) String.format("%X", i2).length());
                }

                long j2 = (long) along.length;

                a(i, stringbuilder).append("long[").append(j2).append("] {\n");
                if (flag) {
                    a(i + 1, stringbuilder);

                    for (i1 = 0; i1 < along.length; ++i1) {
                        if (i1 != 0) {
                            stringbuilder.append(',');
                        }

                        if (i1 % 16 == 0 && i1 / 16 > 0) {
                            stringbuilder.append('\n');
                            if (i1 < along.length) {
                                a(i + 1, stringbuilder);
                            }
                        } else if (i1 != 0) {
                            stringbuilder.append(' ');
                        }

                        stringbuilder.append(String.format("0x%0" + l1 + "X", along[i1]));
                    }
                } else {
                    a(i + 1, stringbuilder).append(" // Skipped, supply withBinaryBlobs true");
                }

                stringbuilder.append('\n');
                a(i, stringbuilder).append('}');
                break;
            default:
                stringbuilder.append("<UNKNOWN :(>");
        }

        return stringbuilder;
    }

    private static StringBuilder a(int i, StringBuilder stringbuilder) {
        int j = stringbuilder.lastIndexOf("\n") + 1;
        int k = stringbuilder.length() - j;

        for (int l = 0; l < 2 * i - k; ++l) {
            stringbuilder.append(' ');
        }

        return stringbuilder;
    }

    public static NBTTagCompound a(DataFixer datafixer, DataFixTypes datafixtypes, NBTTagCompound nbttagcompound, int i) {
        return a(datafixer, datafixtypes, nbttagcompound, i, SharedConstants.getGameVersion().getWorldVersion());
    }

    public static NBTTagCompound a(DataFixer datafixer, DataFixTypes datafixtypes, NBTTagCompound nbttagcompound, int i, int j) {
        return (NBTTagCompound) datafixer.update(datafixtypes.a(), new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound), i, j).getValue();
    }

    public static IChatBaseComponent c(NBTBase nbtbase) {
        return (new TextComponentTagVisitor("", 0)).a(nbtbase);
    }

    public static String d(NBTTagCompound nbttagcompound) {
        return (new SnbtPrinterTagVisitor()).a((NBTBase) e(nbttagcompound));
    }

    public static NBTTagCompound a(String s) throws CommandSyntaxException {
        return f(MojangsonParser.parse(s));
    }

    @VisibleForTesting
    static NBTTagCompound e(NBTTagCompound nbttagcompound) {
        boolean flag = nbttagcompound.hasKeyOfType("palettes", 9);
        NBTTagList nbttaglist;

        if (flag) {
            nbttaglist = nbttagcompound.getList("palettes", 9).b(0);
        } else {
            nbttaglist = nbttagcompound.getList("palette", 10);
        }

        Stream stream = nbttaglist.stream();

        Objects.requireNonNull(NBTTagCompound.class);
        NBTTagList nbttaglist1 = (NBTTagList) stream.map(NBTTagCompound.class::cast).map(GameProfileSerializer::g).map(NBTTagString::a).collect(Collectors.toCollection(NBTTagList::new));

        nbttagcompound.set("palette", nbttaglist1);
        NBTTagList nbttaglist2;
        NBTTagList nbttaglist3;

        if (flag) {
            nbttaglist2 = new NBTTagList();
            nbttaglist3 = nbttagcompound.getList("palettes", 9);
            stream = nbttaglist3.stream();
            Objects.requireNonNull(NBTTagList.class);
            stream.map(NBTTagList.class::cast).forEach((nbttaglist4) -> {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                for (int i = 0; i < nbttaglist4.size(); ++i) {
                    nbttagcompound1.setString(nbttaglist1.getString(i), g(nbttaglist4.getCompound(i)));
                }

                nbttaglist2.add(nbttagcompound1);
            });
            nbttagcompound.set("palettes", nbttaglist2);
        }

        if (nbttagcompound.hasKeyOfType("entities", 10)) {
            nbttaglist2 = nbttagcompound.getList("entities", 10);
            stream = nbttaglist2.stream();
            Objects.requireNonNull(NBTTagCompound.class);
            nbttaglist3 = (NBTTagList) stream.map(NBTTagCompound.class::cast).sorted(Comparator.comparing((nbttagcompound1) -> {
                return nbttagcompound1.getList("pos", 6);
            }, GameProfileSerializer.YXZ_LISTTAG_DOUBLE_COMPARATOR)).collect(Collectors.toCollection(NBTTagList::new));
            nbttagcompound.set("entities", nbttaglist3);
        }

        stream = nbttagcompound.getList("blocks", 10).stream();
        Objects.requireNonNull(NBTTagCompound.class);
        nbttaglist2 = (NBTTagList) stream.map(NBTTagCompound.class::cast).sorted(Comparator.comparing((nbttagcompound1) -> {
            return nbttagcompound1.getList("pos", 3);
        }, GameProfileSerializer.YXZ_LISTTAG_INT_COMPARATOR)).peek((nbttagcompound1) -> {
            nbttagcompound1.setString("state", nbttaglist1.getString(nbttagcompound1.getInt("state")));
        }).collect(Collectors.toCollection(NBTTagList::new));
        nbttagcompound.set("data", nbttaglist2);
        nbttagcompound.remove("blocks");
        return nbttagcompound;
    }

    @VisibleForTesting
    static NBTTagCompound f(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = nbttagcompound.getList("palette", 8);
        Stream stream = nbttaglist.stream();

        Objects.requireNonNull(NBTTagString.class);
        Map<String, NBTBase> map = (Map) stream.map(NBTTagString.class::cast).map(NBTTagString::asString).collect(ImmutableMap.toImmutableMap(Function.identity(), GameProfileSerializer::b));

        if (nbttagcompound.hasKeyOfType("palettes", 9)) {
            Stream stream1 = nbttagcompound.getList("palettes", 10).stream();

            Objects.requireNonNull(NBTTagCompound.class);
            nbttagcompound.set("palettes", (NBTBase) stream1.map(NBTTagCompound.class::cast).map((nbttagcompound1) -> {
                Stream stream2 = map.keySet().stream();

                Objects.requireNonNull(nbttagcompound1);
                return (NBTTagList) stream2.map(nbttagcompound1::getString).map(GameProfileSerializer::b).collect(Collectors.toCollection(NBTTagList::new));
            }).collect(Collectors.toCollection(NBTTagList::new)));
            nbttagcompound.remove("palette");
        } else {
            nbttagcompound.set("palette", (NBTBase) map.values().stream().collect(Collectors.toCollection(NBTTagList::new)));
        }

        if (nbttagcompound.hasKeyOfType("data", 9)) {
            Object2IntMap<String> object2intmap = new Object2IntOpenHashMap();

            object2intmap.defaultReturnValue(-1);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                object2intmap.put(nbttaglist.getString(i), i);
            }

            NBTTagList nbttaglist1 = nbttagcompound.getList("data", 10);

            for (int j = 0; j < nbttaglist1.size(); ++j) {
                NBTTagCompound nbttagcompound1 = nbttaglist1.getCompound(j);
                String s = nbttagcompound1.getString("state");
                int k = object2intmap.getInt(s);

                if (k == -1) {
                    throw new IllegalStateException("Entry " + s + " missing from palette");
                }

                nbttagcompound1.setInt("state", k);
            }

            nbttagcompound.set("blocks", nbttaglist1);
            nbttagcompound.remove("data");
        }

        return nbttagcompound;
    }

    @VisibleForTesting
    static String g(NBTTagCompound nbttagcompound) {
        StringBuilder stringbuilder = new StringBuilder(nbttagcompound.getString("Name"));

        if (nbttagcompound.hasKeyOfType("Properties", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Properties");
            String s = (String) nbttagcompound1.getKeys().stream().sorted().map((s1) -> {
                return s1 + ":" + nbttagcompound1.get(s1).asString();
            }).collect(Collectors.joining(","));

            stringbuilder.append('{').append(s).append('}');
        }

        return stringbuilder.toString();
    }

    @VisibleForTesting
    static NBTTagCompound b(String s) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        int i = s.indexOf(123);
        String s1;

        if (i >= 0) {
            s1 = s.substring(0, i);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            if (i + 2 <= s.length()) {
                String s2 = s.substring(i + 1, s.indexOf(125, i));

                GameProfileSerializer.COMMA_SPLITTER.split(s2).forEach((s3) -> {
                    List<String> list = GameProfileSerializer.COLON_SPLITTER.splitToList(s3);

                    if (list.size() == 2) {
                        nbttagcompound1.setString((String) list.get(0), (String) list.get(1));
                    } else {
                        GameProfileSerializer.LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", s);
                    }

                });
                nbttagcompound.set("Properties", nbttagcompound1);
            }
        } else {
            s1 = s;
        }

        nbttagcompound.setString("Name", s1);
        return nbttagcompound;
    }
}
