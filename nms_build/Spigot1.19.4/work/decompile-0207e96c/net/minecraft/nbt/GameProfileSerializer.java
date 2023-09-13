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
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.UtilColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.IBlockDataHolder;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.Fluid;
import org.slf4j.Logger;

public final class GameProfileSerializer {

    private static final Comparator<NBTTagList> YXZ_LISTTAG_INT_COMPARATOR = Comparator.comparingInt((nbttaglist) -> {
        return nbttaglist.getInt(1);
    }).thenComparingInt((nbttaglist) -> {
        return nbttaglist.getInt(0);
    }).thenComparingInt((nbttaglist) -> {
        return nbttaglist.getInt(2);
    });
    private static final Comparator<NBTTagList> YXZ_LISTTAG_DOUBLE_COMPARATOR = Comparator.comparingDouble((nbttaglist) -> {
        return nbttaglist.getDouble(1);
    }).thenComparingDouble((nbttaglist) -> {
        return nbttaglist.getDouble(0);
    }).thenComparingDouble((nbttaglist) -> {
        return nbttaglist.getDouble(2);
    });
    public static final String SNBT_DATA_TAG = "data";
    private static final char PROPERTIES_START = '{';
    private static final char PROPERTIES_END = '}';
    private static final String ELEMENT_SEPARATOR = ",";
    private static final char KEY_VALUE_SEPARATOR = ':';
    private static final Splitter COMMA_SPLITTER = Splitter.on(",");
    private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int INDENT = 2;
    private static final int NOT_FOUND = -1;

    private GameProfileSerializer() {}

    @Nullable
    public static GameProfile readGameProfile(NBTTagCompound nbttagcompound) {
        String s = null;
        UUID uuid = null;

        if (nbttagcompound.contains("Name", 8)) {
            s = nbttagcompound.getString("Name");
        }

        if (nbttagcompound.hasUUID("Id")) {
            uuid = nbttagcompound.getUUID("Id");
        }

        try {
            GameProfile gameprofile = new GameProfile(uuid, s);

            if (nbttagcompound.contains("Properties", 10)) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Properties");
                Iterator iterator = nbttagcompound1.getAllKeys().iterator();

                while (iterator.hasNext()) {
                    String s1 = (String) iterator.next();
                    NBTTagList nbttaglist = nbttagcompound1.getList(s1, 10);

                    for (int i = 0; i < nbttaglist.size(); ++i) {
                        NBTTagCompound nbttagcompound2 = nbttaglist.getCompound(i);
                        String s2 = nbttagcompound2.getString("Value");

                        if (nbttagcompound2.contains("Signature", 8)) {
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

    public static NBTTagCompound writeGameProfile(NBTTagCompound nbttagcompound, GameProfile gameprofile) {
        if (!UtilColor.isNullOrEmpty(gameprofile.getName())) {
            nbttagcompound.putString("Name", gameprofile.getName());
        }

        if (gameprofile.getId() != null) {
            nbttagcompound.putUUID("Id", gameprofile.getId());
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
                    nbttagcompound2.putString("Value", property.getValue());
                    if (property.hasSignature()) {
                        nbttagcompound2.putString("Signature", property.getSignature());
                    }
                }

                nbttagcompound1.put(s, nbttaglist);
            }

            nbttagcompound.put("Properties", nbttagcompound1);
        }

        return nbttagcompound;
    }

    @VisibleForTesting
    public static boolean compareNbt(@Nullable NBTBase nbtbase, @Nullable NBTBase nbtbase1, boolean flag) {
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
            Iterator iterator = nbttagcompound.getAllKeys().iterator();

            String s;
            NBTBase nbtbase2;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                s = (String) iterator.next();
                nbtbase2 = nbttagcompound.get(s);
            } while (compareNbt(nbtbase2, nbttagcompound1.get(s), flag));

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
                            if (!compareNbt(nbtbase3, nbttaglist1.get(j), flag)) {
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

    public static NBTTagIntArray createUUID(UUID uuid) {
        return new NBTTagIntArray(UUIDUtil.uuidToIntArray(uuid));
    }

    public static UUID loadUUID(NBTBase nbtbase) {
        if (nbtbase.getType() != NBTTagIntArray.TYPE) {
            String s = NBTTagIntArray.TYPE.getName();

            throw new IllegalArgumentException("Expected UUID-Tag to be of type " + s + ", but found " + nbtbase.getType().getName() + ".");
        } else {
            int[] aint = ((NBTTagIntArray) nbtbase).getAsIntArray();

            if (aint.length != 4) {
                throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + aint.length + ".");
            } else {
                return UUIDUtil.uuidFromIntArray(aint);
            }
        }
    }

    public static BlockPosition readBlockPos(NBTTagCompound nbttagcompound) {
        return new BlockPosition(nbttagcompound.getInt("X"), nbttagcompound.getInt("Y"), nbttagcompound.getInt("Z"));
    }

    public static NBTTagCompound writeBlockPos(BlockPosition blockposition) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putInt("X", blockposition.getX());
        nbttagcompound.putInt("Y", blockposition.getY());
        nbttagcompound.putInt("Z", blockposition.getZ());
        return nbttagcompound;
    }

    public static IBlockData readBlockState(HolderGetter<Block> holdergetter, NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.contains("Name", 8)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            MinecraftKey minecraftkey = new MinecraftKey(nbttagcompound.getString("Name"));
            Optional<? extends Holder<Block>> optional = holdergetter.get(ResourceKey.create(Registries.BLOCK, minecraftkey));

            if (optional.isEmpty()) {
                return Blocks.AIR.defaultBlockState();
            } else {
                Block block = (Block) ((Holder) optional.get()).value();
                IBlockData iblockdata = block.defaultBlockState();

                if (nbttagcompound.contains("Properties", 10)) {
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Properties");
                    BlockStateList<Block, IBlockData> blockstatelist = block.getStateDefinition();
                    Iterator iterator = nbttagcompound1.getAllKeys().iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();
                        IBlockState<?> iblockstate = blockstatelist.getProperty(s);

                        if (iblockstate != null) {
                            iblockdata = (IBlockData) setValueHelper(iblockdata, iblockstate, s, nbttagcompound1, nbttagcompound);
                        }
                    }
                }

                return iblockdata;
            }
        }
    }

    private static <S extends IBlockDataHolder<?, S>, T extends Comparable<T>> S setValueHelper(S s0, IBlockState<T> iblockstate, String s, NBTTagCompound nbttagcompound, NBTTagCompound nbttagcompound1) {
        Optional<T> optional = iblockstate.getValue(nbttagcompound.getString(s));

        if (optional.isPresent()) {
            return (IBlockDataHolder) s0.setValue(iblockstate, (Comparable) optional.get());
        } else {
            GameProfileSerializer.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{s, nbttagcompound.getString(s), nbttagcompound1.toString()});
            return s0;
        }
    }

    public static NBTTagCompound writeBlockState(IBlockData iblockdata) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putString("Name", BuiltInRegistries.BLOCK.getKey(iblockdata.getBlock()).toString());
        ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap = iblockdata.getValues();

        if (!immutablemap.isEmpty()) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            UnmodifiableIterator unmodifiableiterator = immutablemap.entrySet().iterator();

            while (unmodifiableiterator.hasNext()) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();

                nbttagcompound1.putString(iblockstate.getName(), getName(iblockstate, (Comparable) entry.getValue()));
            }

            nbttagcompound.put("Properties", nbttagcompound1);
        }

        return nbttagcompound;
    }

    public static NBTTagCompound writeFluidState(Fluid fluid) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putString("Name", BuiltInRegistries.FLUID.getKey(fluid.getType()).toString());
        ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap = fluid.getValues();

        if (!immutablemap.isEmpty()) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            UnmodifiableIterator unmodifiableiterator = immutablemap.entrySet().iterator();

            while (unmodifiableiterator.hasNext()) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();

                nbttagcompound1.putString(iblockstate.getName(), getName(iblockstate, (Comparable) entry.getValue()));
            }

            nbttagcompound.put("Properties", nbttagcompound1);
        }

        return nbttagcompound;
    }

    private static <T extends Comparable<T>> String getName(IBlockState<T> iblockstate, Comparable<?> comparable) {
        return iblockstate.getName(comparable);
    }

    public static String prettyPrint(NBTBase nbtbase) {
        return prettyPrint(nbtbase, false);
    }

    public static String prettyPrint(NBTBase nbtbase, boolean flag) {
        return prettyPrint(new StringBuilder(), nbtbase, 0, flag).toString();
    }

    public static StringBuilder prettyPrint(StringBuilder stringbuilder, NBTBase nbtbase, int i, boolean flag) {
        int j;
        int k;
        String s;
        int l;
        int i1;
        int j1;

        switch (nbtbase.getId()) {
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
                byte[] abyte = nbttagbytearray.getAsByteArray();

                j = abyte.length;
                indent(i, stringbuilder).append("byte[").append(j).append("] {\n");
                if (flag) {
                    indent(i + 1, stringbuilder);

                    for (k = 0; k < abyte.length; ++k) {
                        if (k != 0) {
                            stringbuilder.append(',');
                        }

                        if (k % 16 == 0 && k / 16 > 0) {
                            stringbuilder.append('\n');
                            if (k < abyte.length) {
                                indent(i + 1, stringbuilder);
                            }
                        } else if (k != 0) {
                            stringbuilder.append(' ');
                        }

                        stringbuilder.append(String.format(Locale.ROOT, "0x%02X", abyte[k] & 255));
                    }
                } else {
                    indent(i + 1, stringbuilder).append(" // Skipped, supply withBinaryBlobs true");
                }

                stringbuilder.append('\n');
                indent(i, stringbuilder).append('}');
                break;
            case 9:
                NBTTagList nbttaglist = (NBTTagList) nbtbase;
                int k1 = nbttaglist.size();
                byte b0 = nbttaglist.getElementType();

                s = b0 == 0 ? "undefined" : NBTTagTypes.getType(b0).getPrettyName();
                indent(i, stringbuilder).append("list<").append(s).append(">[").append(k1).append("] [");
                if (k1 != 0) {
                    stringbuilder.append('\n');
                }

                for (l = 0; l < k1; ++l) {
                    if (l != 0) {
                        stringbuilder.append(",\n");
                    }

                    indent(i + 1, stringbuilder);
                    prettyPrint(stringbuilder, nbttaglist.get(l), i + 1, flag);
                }

                if (k1 != 0) {
                    stringbuilder.append('\n');
                }

                indent(i, stringbuilder).append(']');
                break;
            case 10:
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                List<String> list = Lists.newArrayList(nbttagcompound.getAllKeys());

                Collections.sort(list);
                indent(i, stringbuilder).append('{');
                if (stringbuilder.length() - stringbuilder.lastIndexOf("\n") > 2 * (i + 1)) {
                    stringbuilder.append('\n');
                    indent(i + 1, stringbuilder);
                }

                j = list.stream().mapToInt(String::length).max().orElse(0);
                s = Strings.repeat(" ", j);

                for (l = 0; l < list.size(); ++l) {
                    if (l != 0) {
                        stringbuilder.append(",\n");
                    }

                    String s1 = (String) list.get(l);

                    indent(i + 1, stringbuilder).append('"').append(s1).append('"').append(s, 0, s.length() - s1.length()).append(": ");
                    prettyPrint(stringbuilder, nbttagcompound.get(s1), i + 1, flag);
                }

                if (!list.isEmpty()) {
                    stringbuilder.append('\n');
                }

                indent(i, stringbuilder).append('}');
                break;
            case 11:
                NBTTagIntArray nbttagintarray = (NBTTagIntArray) nbtbase;
                int[] aint = nbttagintarray.getAsIntArray();

                j = 0;
                int[] aint1 = aint;

                l = aint.length;

                for (j1 = 0; j1 < l; ++j1) {
                    i1 = aint1[j1];
                    j = Math.max(j, String.format(Locale.ROOT, "%X", i1).length());
                }

                k = aint.length;
                indent(i, stringbuilder).append("int[").append(k).append("] {\n");
                if (flag) {
                    indent(i + 1, stringbuilder);

                    for (l = 0; l < aint.length; ++l) {
                        if (l != 0) {
                            stringbuilder.append(',');
                        }

                        if (l % 16 == 0 && l / 16 > 0) {
                            stringbuilder.append('\n');
                            if (l < aint.length) {
                                indent(i + 1, stringbuilder);
                            }
                        } else if (l != 0) {
                            stringbuilder.append(' ');
                        }

                        stringbuilder.append(String.format(Locale.ROOT, "0x%0" + j + "X", aint[l]));
                    }
                } else {
                    indent(i + 1, stringbuilder).append(" // Skipped, supply withBinaryBlobs true");
                }

                stringbuilder.append('\n');
                indent(i, stringbuilder).append('}');
                break;
            case 12:
                NBTTagLongArray nbttaglongarray = (NBTTagLongArray) nbtbase;
                long[] along = nbttaglongarray.getAsLongArray();
                long l1 = 0L;
                long[] along1 = along;

                j1 = along.length;

                for (i1 = 0; i1 < j1; ++i1) {
                    long i2 = along1[i1];

                    l1 = Math.max(l1, (long) String.format(Locale.ROOT, "%X", i2).length());
                }

                long j2 = (long) along.length;

                indent(i, stringbuilder).append("long[").append(j2).append("] {\n");
                if (flag) {
                    indent(i + 1, stringbuilder);

                    for (i1 = 0; i1 < along.length; ++i1) {
                        if (i1 != 0) {
                            stringbuilder.append(',');
                        }

                        if (i1 % 16 == 0 && i1 / 16 > 0) {
                            stringbuilder.append('\n');
                            if (i1 < along.length) {
                                indent(i + 1, stringbuilder);
                            }
                        } else if (i1 != 0) {
                            stringbuilder.append(' ');
                        }

                        stringbuilder.append(String.format(Locale.ROOT, "0x%0" + l1 + "X", along[i1]));
                    }
                } else {
                    indent(i + 1, stringbuilder).append(" // Skipped, supply withBinaryBlobs true");
                }

                stringbuilder.append('\n');
                indent(i, stringbuilder).append('}');
                break;
            default:
                stringbuilder.append("<UNKNOWN :(>");
        }

        return stringbuilder;
    }

    private static StringBuilder indent(int i, StringBuilder stringbuilder) {
        int j = stringbuilder.lastIndexOf("\n") + 1;
        int k = stringbuilder.length() - j;

        for (int l = 0; l < 2 * i - k; ++l) {
            stringbuilder.append(' ');
        }

        return stringbuilder;
    }

    public static IChatBaseComponent toPrettyComponent(NBTBase nbtbase) {
        return (new TextComponentTagVisitor("", 0)).visit(nbtbase);
    }

    public static String structureToSnbt(NBTTagCompound nbttagcompound) {
        return (new SnbtPrinterTagVisitor()).visit(packStructureTemplate(nbttagcompound));
    }

    public static NBTTagCompound snbtToStructure(String s) throws CommandSyntaxException {
        return unpackStructureTemplate(MojangsonParser.parseTag(s));
    }

    @VisibleForTesting
    static NBTTagCompound packStructureTemplate(NBTTagCompound nbttagcompound) {
        boolean flag = nbttagcompound.contains("palettes", 9);
        NBTTagList nbttaglist;

        if (flag) {
            nbttaglist = nbttagcompound.getList("palettes", 9).getList(0);
        } else {
            nbttaglist = nbttagcompound.getList("palette", 10);
        }

        Stream stream = nbttaglist.stream();

        Objects.requireNonNull(NBTTagCompound.class);
        NBTTagList nbttaglist1 = (NBTTagList) stream.map(NBTTagCompound.class::cast).map(GameProfileSerializer::packBlockState).map(NBTTagString::valueOf).collect(Collectors.toCollection(NBTTagList::new));

        nbttagcompound.put("palette", nbttaglist1);
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
                    nbttagcompound1.putString(nbttaglist1.getString(i), packBlockState(nbttaglist4.getCompound(i)));
                }

                nbttaglist2.add(nbttagcompound1);
            });
            nbttagcompound.put("palettes", nbttaglist2);
        }

        if (nbttagcompound.contains("entities", 9)) {
            nbttaglist2 = nbttagcompound.getList("entities", 10);
            stream = nbttaglist2.stream();
            Objects.requireNonNull(NBTTagCompound.class);
            nbttaglist3 = (NBTTagList) stream.map(NBTTagCompound.class::cast).sorted(Comparator.comparing((nbttagcompound1) -> {
                return nbttagcompound1.getList("pos", 6);
            }, GameProfileSerializer.YXZ_LISTTAG_DOUBLE_COMPARATOR)).collect(Collectors.toCollection(NBTTagList::new));
            nbttagcompound.put("entities", nbttaglist3);
        }

        stream = nbttagcompound.getList("blocks", 10).stream();
        Objects.requireNonNull(NBTTagCompound.class);
        nbttaglist2 = (NBTTagList) stream.map(NBTTagCompound.class::cast).sorted(Comparator.comparing((nbttagcompound1) -> {
            return nbttagcompound1.getList("pos", 3);
        }, GameProfileSerializer.YXZ_LISTTAG_INT_COMPARATOR)).peek((nbttagcompound1) -> {
            nbttagcompound1.putString("state", nbttaglist1.getString(nbttagcompound1.getInt("state")));
        }).collect(Collectors.toCollection(NBTTagList::new));
        nbttagcompound.put("data", nbttaglist2);
        nbttagcompound.remove("blocks");
        return nbttagcompound;
    }

    @VisibleForTesting
    static NBTTagCompound unpackStructureTemplate(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = nbttagcompound.getList("palette", 8);
        Stream stream = nbttaglist.stream();

        Objects.requireNonNull(NBTTagString.class);
        Map<String, NBTBase> map = (Map) stream.map(NBTTagString.class::cast).map(NBTTagString::getAsString).collect(ImmutableMap.toImmutableMap(Function.identity(), GameProfileSerializer::unpackBlockState));

        if (nbttagcompound.contains("palettes", 9)) {
            Stream stream1 = nbttagcompound.getList("palettes", 10).stream();

            Objects.requireNonNull(NBTTagCompound.class);
            nbttagcompound.put("palettes", (NBTBase) stream1.map(NBTTagCompound.class::cast).map((nbttagcompound1) -> {
                Stream stream2 = map.keySet().stream();

                Objects.requireNonNull(nbttagcompound1);
                return (NBTTagList) stream2.map(nbttagcompound1::getString).map(GameProfileSerializer::unpackBlockState).collect(Collectors.toCollection(NBTTagList::new));
            }).collect(Collectors.toCollection(NBTTagList::new)));
            nbttagcompound.remove("palette");
        } else {
            nbttagcompound.put("palette", (NBTBase) map.values().stream().collect(Collectors.toCollection(NBTTagList::new)));
        }

        if (nbttagcompound.contains("data", 9)) {
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

                nbttagcompound1.putInt("state", k);
            }

            nbttagcompound.put("blocks", nbttaglist1);
            nbttagcompound.remove("data");
        }

        return nbttagcompound;
    }

    @VisibleForTesting
    static String packBlockState(NBTTagCompound nbttagcompound) {
        StringBuilder stringbuilder = new StringBuilder(nbttagcompound.getString("Name"));

        if (nbttagcompound.contains("Properties", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Properties");
            String s = (String) nbttagcompound1.getAllKeys().stream().sorted().map((s1) -> {
                return s1 + ":" + nbttagcompound1.get(s1).getAsString();
            }).collect(Collectors.joining(","));

            stringbuilder.append('{').append(s).append('}');
        }

        return stringbuilder.toString();
    }

    @VisibleForTesting
    static NBTTagCompound unpackBlockState(String s) {
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
                        nbttagcompound1.putString((String) list.get(0), (String) list.get(1));
                    } else {
                        GameProfileSerializer.LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", s);
                    }

                });
                nbttagcompound.put("Properties", nbttagcompound1);
            }
        } else {
            s1 = s;
        }

        nbttagcompound.putString("Name", s1);
        return nbttagcompound;
    }

    public static NBTTagCompound addCurrentDataVersion(NBTTagCompound nbttagcompound) {
        int i = SharedConstants.getCurrentVersion().getDataVersion().getVersion();

        return addDataVersion(nbttagcompound, i);
    }

    public static NBTTagCompound addDataVersion(NBTTagCompound nbttagcompound, int i) {
        nbttagcompound.putInt("DataVersion", i);
        return nbttagcompound;
    }

    public static int getDataVersion(NBTTagCompound nbttagcompound, int i) {
        return nbttagcompound.contains("DataVersion", 99) ? nbttagcompound.getInt("DataVersion") : i;
    }
}
