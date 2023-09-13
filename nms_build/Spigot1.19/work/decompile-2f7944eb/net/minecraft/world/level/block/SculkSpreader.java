package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;
import org.slf4j.Logger;

public class SculkSpreader {

    public static final int MAX_GROWTH_RATE_RADIUS = 24;
    public static final int MAX_CHARGE = 1000;
    public static final float MAX_DECAY_FACTOR = 0.5F;
    private static final int MAX_CURSORS = 32;
    public static final int SHRIEKER_PLACEMENT_RATE = 11;
    final boolean isWorldGeneration;
    private final TagKey<Block> replaceableBlocks;
    private final int growthSpawnCost;
    private final int noGrowthRadius;
    private final int chargeDecayRate;
    private final int additionalDecayRate;
    private List<SculkSpreader.a> cursors = new ArrayList();
    private static final Logger LOGGER = LogUtils.getLogger();

    public SculkSpreader(boolean flag, TagKey<Block> tagkey, int i, int j, int k, int l) {
        this.isWorldGeneration = flag;
        this.replaceableBlocks = tagkey;
        this.growthSpawnCost = i;
        this.noGrowthRadius = j;
        this.chargeDecayRate = k;
        this.additionalDecayRate = l;
    }

    public static SculkSpreader createLevelSpreader() {
        return new SculkSpreader(false, TagsBlock.SCULK_REPLACEABLE, 10, 4, 10, 5);
    }

    public static SculkSpreader createWorldGenSpreader() {
        return new SculkSpreader(true, TagsBlock.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
    }

    public TagKey<Block> replaceableBlocks() {
        return this.replaceableBlocks;
    }

    public int growthSpawnCost() {
        return this.growthSpawnCost;
    }

    public int noGrowthRadius() {
        return this.noGrowthRadius;
    }

    public int chargeDecayRate() {
        return this.chargeDecayRate;
    }

    public int additionalDecayRate() {
        return this.additionalDecayRate;
    }

    public boolean isWorldGeneration() {
        return this.isWorldGeneration;
    }

    @VisibleForTesting
    public List<SculkSpreader.a> getCursors() {
        return this.cursors;
    }

    public void clear() {
        this.cursors.clear();
    }

    public void load(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.contains("cursors", 9)) {
            this.cursors.clear();
            DataResult dataresult = SculkSpreader.a.CODEC.listOf().parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.getList("cursors", 10)));
            Logger logger = SculkSpreader.LOGGER;

            Objects.requireNonNull(logger);
            List<SculkSpreader.a> list = (List) dataresult.resultOrPartial(logger::error).orElseGet(ArrayList::new);
            int i = Math.min(list.size(), 32);

            for (int j = 0; j < i; ++j) {
                this.addCursor((SculkSpreader.a) list.get(j));
            }
        }

    }

    public void save(NBTTagCompound nbttagcompound) {
        DataResult dataresult = SculkSpreader.a.CODEC.listOf().encodeStart(DynamicOpsNBT.INSTANCE, this.cursors);
        Logger logger = SculkSpreader.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("cursors", nbtbase);
        });
    }

    public void addCursors(BlockPosition blockposition, int i) {
        while (i > 0) {
            int j = Math.min(i, 1000);

            this.addCursor(new SculkSpreader.a(blockposition, j));
            i -= j;
        }

    }

    private void addCursor(SculkSpreader.a sculkspreader_a) {
        if (this.cursors.size() < 32) {
            this.cursors.add(sculkspreader_a);
        }
    }

    public void updateCursors(GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource, boolean flag) {
        if (!this.cursors.isEmpty()) {
            List<SculkSpreader.a> list = new ArrayList();
            Map<BlockPosition, SculkSpreader.a> map = new HashMap();
            Object2IntMap<BlockPosition> object2intmap = new Object2IntOpenHashMap();
            Iterator iterator = this.cursors.iterator();

            BlockPosition blockposition1;

            while (iterator.hasNext()) {
                SculkSpreader.a sculkspreader_a = (SculkSpreader.a) iterator.next();

                sculkspreader_a.update(generatoraccess, blockposition, randomsource, this, flag);
                if (sculkspreader_a.charge <= 0) {
                    generatoraccess.levelEvent(3006, sculkspreader_a.getPos(), 0);
                } else {
                    blockposition1 = sculkspreader_a.getPos();
                    object2intmap.computeInt(blockposition1, (blockposition2, integer) -> {
                        return (integer == null ? 0 : integer) + sculkspreader_a.charge;
                    });
                    SculkSpreader.a sculkspreader_a1 = (SculkSpreader.a) map.get(blockposition1);

                    if (sculkspreader_a1 == null) {
                        map.put(blockposition1, sculkspreader_a);
                        list.add(sculkspreader_a);
                    } else if (!this.isWorldGeneration() && sculkspreader_a.charge + sculkspreader_a1.charge <= 1000) {
                        sculkspreader_a1.mergeWith(sculkspreader_a);
                    } else {
                        list.add(sculkspreader_a);
                        if (sculkspreader_a.charge < sculkspreader_a1.charge) {
                            map.put(blockposition1, sculkspreader_a);
                        }
                    }
                }
            }

            ObjectIterator objectiterator = object2intmap.object2IntEntrySet().iterator();

            while (objectiterator.hasNext()) {
                Entry<BlockPosition> entry = (Entry) objectiterator.next();

                blockposition1 = (BlockPosition) entry.getKey();
                int i = entry.getIntValue();
                SculkSpreader.a sculkspreader_a2 = (SculkSpreader.a) map.get(blockposition1);
                Collection<EnumDirection> collection = sculkspreader_a2 == null ? null : sculkspreader_a2.getFacingData();

                if (i > 0 && collection != null) {
                    int j = (int) (Math.log1p((double) i) / 2.299999952316284D) + 1;
                    int k = (j << 6) + MultifaceBlock.pack(collection);

                    generatoraccess.levelEvent(3006, blockposition1, k);
                }
            }

            this.cursors = list;
        }
    }

    public static class a {

        private static final ObjectArrayList<BaseBlockPosition> NON_CORNER_NEIGHBOURS = (ObjectArrayList) SystemUtils.make(new ObjectArrayList(18), (objectarraylist) -> {
            Stream stream = BlockPosition.betweenClosedStream(new BlockPosition(-1, -1, -1), new BlockPosition(1, 1, 1)).filter((blockposition) -> {
                return (blockposition.getX() == 0 || blockposition.getY() == 0 || blockposition.getZ() == 0) && !blockposition.equals(BlockPosition.ZERO);
            }).map(BlockPosition::immutable);

            Objects.requireNonNull(objectarraylist);
            stream.forEach(objectarraylist::add);
        });
        public static final int MAX_CURSOR_DECAY_DELAY = 1;
        private BlockPosition pos;
        int charge;
        private int updateDelay;
        private int decayDelay;
        @Nullable
        private Set<EnumDirection> facings;
        private static final Codec<Set<EnumDirection>> DIRECTION_SET = EnumDirection.CODEC.listOf().xmap((list) -> {
            return Sets.newEnumSet(list, EnumDirection.class);
        }, Lists::newArrayList);
        public static final Codec<SculkSpreader.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(BlockPosition.CODEC.fieldOf("pos").forGetter(SculkSpreader.a::getPos), Codec.intRange(0, 1000).fieldOf("charge").orElse(0).forGetter(SculkSpreader.a::getCharge), Codec.intRange(0, 1).fieldOf("decay_delay").orElse(1).forGetter(SculkSpreader.a::getDecayDelay), Codec.intRange(0, Integer.MAX_VALUE).fieldOf("update_delay").orElse(0).forGetter((sculkspreader_a) -> {
                return sculkspreader_a.updateDelay;
            }), SculkSpreader.a.DIRECTION_SET.optionalFieldOf("facings").forGetter((sculkspreader_a) -> {
                return Optional.ofNullable(sculkspreader_a.getFacingData());
            })).apply(instance, SculkSpreader.a::new);
        });

        private a(BlockPosition blockposition, int i, int j, int k, Optional<Set<EnumDirection>> optional) {
            this.pos = blockposition;
            this.charge = i;
            this.decayDelay = j;
            this.updateDelay = k;
            this.facings = (Set) optional.orElse((Object) null);
        }

        public a(BlockPosition blockposition, int i) {
            this(blockposition, i, 1, 0, Optional.empty());
        }

        public BlockPosition getPos() {
            return this.pos;
        }

        public int getCharge() {
            return this.charge;
        }

        public int getDecayDelay() {
            return this.decayDelay;
        }

        @Nullable
        public Set<EnumDirection> getFacingData() {
            return this.facings;
        }

        private boolean shouldUpdate(GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag) {
            if (this.charge <= 0) {
                return false;
            } else if (flag) {
                return true;
            } else if (generatoraccess instanceof WorldServer) {
                WorldServer worldserver = (WorldServer) generatoraccess;

                return worldserver.shouldTickBlocksAt(blockposition);
            } else {
                return false;
            }
        }

        public void update(GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource, SculkSpreader sculkspreader, boolean flag) {
            if (this.shouldUpdate(generatoraccess, blockposition, sculkspreader.isWorldGeneration)) {
                if (this.updateDelay > 0) {
                    --this.updateDelay;
                } else {
                    IBlockData iblockdata = generatoraccess.getBlockState(this.pos);
                    SculkBehaviour sculkbehaviour = getBlockBehaviour(iblockdata);

                    if (flag && sculkbehaviour.attemptSpreadVein(generatoraccess, this.pos, iblockdata, this.facings, sculkspreader.isWorldGeneration())) {
                        if (sculkbehaviour.canChangeBlockStateOnSpread()) {
                            iblockdata = generatoraccess.getBlockState(this.pos);
                            sculkbehaviour = getBlockBehaviour(iblockdata);
                        }

                        generatoraccess.playSound((EntityHuman) null, this.pos, SoundEffects.SCULK_BLOCK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }

                    this.charge = sculkbehaviour.attemptUseCharge(this, generatoraccess, blockposition, randomsource, sculkspreader, flag);
                    if (this.charge <= 0) {
                        sculkbehaviour.onDischarged(generatoraccess, iblockdata, this.pos, randomsource);
                    } else {
                        BlockPosition blockposition1 = getValidMovementPos(generatoraccess, this.pos, randomsource);

                        if (blockposition1 != null) {
                            sculkbehaviour.onDischarged(generatoraccess, iblockdata, this.pos, randomsource);
                            this.pos = blockposition1.immutable();
                            if (sculkspreader.isWorldGeneration() && !this.pos.closerThan(new BaseBlockPosition(blockposition.getX(), this.pos.getY(), blockposition.getZ()), 15.0D)) {
                                this.charge = 0;
                                return;
                            }

                            iblockdata = generatoraccess.getBlockState(blockposition1);
                        }

                        if (iblockdata.getBlock() instanceof SculkBehaviour) {
                            this.facings = MultifaceBlock.availableFaces(iblockdata);
                        }

                        this.decayDelay = sculkbehaviour.updateDecayDelay(this.decayDelay);
                        this.updateDelay = sculkbehaviour.getSculkSpreadDelay();
                    }
                }
            }
        }

        void mergeWith(SculkSpreader.a sculkspreader_a) {
            this.charge += sculkspreader_a.charge;
            sculkspreader_a.charge = 0;
            this.updateDelay = Math.min(this.updateDelay, sculkspreader_a.updateDelay);
        }

        private static SculkBehaviour getBlockBehaviour(IBlockData iblockdata) {
            Block block = iblockdata.getBlock();
            SculkBehaviour sculkbehaviour;

            if (block instanceof SculkBehaviour) {
                SculkBehaviour sculkbehaviour1 = (SculkBehaviour) block;

                sculkbehaviour = sculkbehaviour1;
            } else {
                sculkbehaviour = SculkBehaviour.DEFAULT;
            }

            return sculkbehaviour;
        }

        private static List<BaseBlockPosition> getRandomizedNonCornerNeighbourOffsets(RandomSource randomsource) {
            return SystemUtils.shuffledCopy(SculkSpreader.a.NON_CORNER_NEIGHBOURS, randomsource);
        }

        @Nullable
        private static BlockPosition getValidMovementPos(GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition.mutable();
            Iterator iterator = getRandomizedNonCornerNeighbourOffsets(randomsource).iterator();

            while (iterator.hasNext()) {
                BaseBlockPosition baseblockposition = (BaseBlockPosition) iterator.next();

                blockposition_mutableblockposition1.setWithOffset(blockposition, baseblockposition);
                IBlockData iblockdata = generatoraccess.getBlockState(blockposition_mutableblockposition1);

                if (iblockdata.getBlock() instanceof SculkBehaviour && isMovementUnobstructed(generatoraccess, blockposition, blockposition_mutableblockposition1)) {
                    blockposition_mutableblockposition.set(blockposition_mutableblockposition1);
                    if (SculkVeinBlock.hasSubstrateAccess(generatoraccess, iblockdata, blockposition_mutableblockposition1)) {
                        break;
                    }
                }
            }

            return blockposition_mutableblockposition.equals(blockposition) ? null : blockposition_mutableblockposition;
        }

        private static boolean isMovementUnobstructed(GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
            if (blockposition.distManhattan(blockposition1) == 1) {
                return true;
            } else {
                BlockPosition blockposition2 = blockposition1.subtract(blockposition);
                EnumDirection enumdirection = EnumDirection.fromAxisAndDirection(EnumDirection.EnumAxis.X, blockposition2.getX() < 0 ? EnumDirection.EnumAxisDirection.NEGATIVE : EnumDirection.EnumAxisDirection.POSITIVE);
                EnumDirection enumdirection1 = EnumDirection.fromAxisAndDirection(EnumDirection.EnumAxis.Y, blockposition2.getY() < 0 ? EnumDirection.EnumAxisDirection.NEGATIVE : EnumDirection.EnumAxisDirection.POSITIVE);
                EnumDirection enumdirection2 = EnumDirection.fromAxisAndDirection(EnumDirection.EnumAxis.Z, blockposition2.getZ() < 0 ? EnumDirection.EnumAxisDirection.NEGATIVE : EnumDirection.EnumAxisDirection.POSITIVE);

                return blockposition2.getX() == 0 ? isUnobstructed(generatoraccess, blockposition, enumdirection1) || isUnobstructed(generatoraccess, blockposition, enumdirection2) : (blockposition2.getY() == 0 ? isUnobstructed(generatoraccess, blockposition, enumdirection) || isUnobstructed(generatoraccess, blockposition, enumdirection2) : isUnobstructed(generatoraccess, blockposition, enumdirection) || isUnobstructed(generatoraccess, blockposition, enumdirection1));
            }
        }

        private static boolean isUnobstructed(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection) {
            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            return !generatoraccess.getBlockState(blockposition1).isFaceSturdy(generatoraccess, blockposition1, enumdirection.getOpposite());
        }
    }
}
