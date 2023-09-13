package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.BlockAccessAir;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.EnumBlockSupport;
import net.minecraft.world.level.block.EnumRenderType;
import net.minecraft.world.level.block.ITileEntity;
import net.minecraft.world.level.block.SoundEffectType;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialMapColor;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public abstract class BlockBase {

    protected static final EnumDirection[] UPDATE_SHAPE_ORDER = new EnumDirection[]{EnumDirection.WEST, EnumDirection.EAST, EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.DOWN, EnumDirection.UP};
    protected final Material material;
    protected final boolean hasCollision;
    protected final float explosionResistance;
    protected final boolean isRandomlyTicking;
    protected final SoundEffectType soundType;
    protected final float friction;
    protected final float speedFactor;
    protected final float jumpFactor;
    protected final boolean dynamicShape;
    protected final BlockBase.Info properties;
    @Nullable
    protected MinecraftKey drops;

    public BlockBase(BlockBase.Info blockbase_info) {
        this.material = blockbase_info.material;
        this.hasCollision = blockbase_info.hasCollision;
        this.drops = blockbase_info.drops;
        this.explosionResistance = blockbase_info.explosionResistance;
        this.isRandomlyTicking = blockbase_info.isRandomlyTicking;
        this.soundType = blockbase_info.soundType;
        this.friction = blockbase_info.friction;
        this.speedFactor = blockbase_info.speedFactor;
        this.jumpFactor = blockbase_info.jumpFactor;
        this.dynamicShape = blockbase_info.dynamicShape;
        this.properties = blockbase_info;
    }

    /** @deprecated */
    @Deprecated
    public void updateIndirectNeighbourShapes(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {}

    /** @deprecated */
    @Deprecated
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
            case LAND:
                return !iblockdata.isCollisionShapeFullBlock(iblockaccess, blockposition);
            case WATER:
                return iblockaccess.getFluidState(blockposition).is((Tag) TagsFluid.WATER);
            case AIR:
                return !iblockdata.isCollisionShapeFullBlock(iblockaccess, blockposition);
            default:
                return false;
        }
    }

    /** @deprecated */
    @Deprecated
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return iblockdata;
    }

    /** @deprecated */
    @Deprecated
    public boolean skipRendering(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
        return false;
    }

    /** @deprecated */
    @Deprecated
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        PacketDebug.sendNeighborsUpdatePacket(world, blockposition);
    }

    /** @deprecated */
    @Deprecated
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {}

    /** @deprecated */
    @Deprecated
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.hasBlockEntity() && !iblockdata.is(iblockdata1.getBlock())) {
            world.removeBlockEntity(blockposition);
        }

    }

    /** @deprecated */
    @Deprecated
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return EnumInteractionResult.PASS;
    }

    /** @deprecated */
    @Deprecated
    public boolean triggerEvent(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        return false;
    }

    /** @deprecated */
    @Deprecated
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    /** @deprecated */
    @Deprecated
    public boolean useShapeForLightOcclusion(IBlockData iblockdata) {
        return false;
    }

    /** @deprecated */
    @Deprecated
    public boolean isSignalSource(IBlockData iblockdata) {
        return false;
    }

    /** @deprecated */
    @Deprecated
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return this.material.getPushReaction();
    }

    /** @deprecated */
    @Deprecated
    public Fluid getFluidState(IBlockData iblockdata) {
        return FluidTypes.EMPTY.defaultFluidState();
    }

    /** @deprecated */
    @Deprecated
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return false;
    }

    public BlockBase.EnumRandomOffset getOffsetType() {
        return BlockBase.EnumRandomOffset.NONE;
    }

    public float getMaxHorizontalOffset() {
        return 0.25F;
    }

    public float getMaxVerticalOffset() {
        return 0.2F;
    }

    /** @deprecated */
    @Deprecated
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata;
    }

    /** @deprecated */
    @Deprecated
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata;
    }

    /** @deprecated */
    @Deprecated
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return this.material.isReplaceable() && (blockactioncontext.getItemInHand().isEmpty() || !blockactioncontext.getItemInHand().is(this.asItem()));
    }

    /** @deprecated */
    @Deprecated
    public boolean canBeReplaced(IBlockData iblockdata, FluidType fluidtype) {
        return this.material.isReplaceable() || !this.material.isSolid();
    }

    /** @deprecated */
    @Deprecated
    public List<ItemStack> getDrops(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        MinecraftKey minecraftkey = this.getLootTable();

        if (minecraftkey == LootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootTableInfo loottableinfo = loottableinfo_builder.withParameter(LootContextParameters.BLOCK_STATE, iblockdata).create(LootContextParameterSets.BLOCK);
            WorldServer worldserver = loottableinfo.getLevel();
            LootTable loottable = worldserver.getServer().getLootTables().get(minecraftkey);

            return loottable.getRandomItems(loottableinfo);
        }
    }

    /** @deprecated */
    @Deprecated
    public long getSeed(IBlockData iblockdata, BlockPosition blockposition) {
        return MathHelper.getSeed(blockposition);
    }

    /** @deprecated */
    @Deprecated
    public VoxelShape getOcclusionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getShape(iblockaccess, blockposition);
    }

    /** @deprecated */
    @Deprecated
    public VoxelShape getBlockSupportShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getCollisionShape(iblockdata, iblockaccess, blockposition, VoxelShapeCollision.empty());
    }

    /** @deprecated */
    @Deprecated
    public VoxelShape getInteractionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.empty();
    }

    /** @deprecated */
    @Deprecated
    public int getLightBlock(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.isSolidRender(iblockaccess, blockposition) ? iblockaccess.getMaxLightLevel() : (iblockdata.propagatesSkylightDown(iblockaccess, blockposition) ? 0 : 1);
    }

    /** @deprecated */
    @Nullable
    @Deprecated
    public ITileInventory getMenuProvider(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return null;
    }

    /** @deprecated */
    @Deprecated
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return true;
    }

    /** @deprecated */
    @Deprecated
    public float getShadeBrightness(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.isCollisionShapeFullBlock(iblockaccess, blockposition) ? 0.2F : 1.0F;
    }

    /** @deprecated */
    @Deprecated
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return 0;
    }

    /** @deprecated */
    @Deprecated
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.block();
    }

    /** @deprecated */
    @Deprecated
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.hasCollision ? iblockdata.getShape(iblockaccess, blockposition) : VoxelShapes.empty();
    }

    /** @deprecated */
    @Deprecated
    public boolean isCollisionShapeFullBlock(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return Block.isShapeFullBlock(iblockdata.getCollisionShape(iblockaccess, blockposition));
    }

    /** @deprecated */
    @Deprecated
    public VoxelShape getVisualShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.getCollisionShape(iblockdata, iblockaccess, blockposition, voxelshapecollision);
    }

    /** @deprecated */
    @Deprecated
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        this.tick(iblockdata, worldserver, blockposition, random);
    }

    /** @deprecated */
    @Deprecated
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {}

    /** @deprecated */
    @Deprecated
    public float getDestroyProgress(IBlockData iblockdata, EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
        float f = iblockdata.getDestroySpeed(iblockaccess, blockposition);

        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = entityhuman.hasCorrectToolForDrops(iblockdata) ? 30 : 100;

            return entityhuman.getDestroySpeed(iblockdata) / f / (float) i;
        }
    }

    /** @deprecated */
    @Deprecated
    public void spawnAfterBreak(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {}

    /** @deprecated */
    @Deprecated
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {}

    /** @deprecated */
    @Deprecated
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 0;
    }

    /** @deprecated */
    @Deprecated
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {}

    /** @deprecated */
    @Deprecated
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 0;
    }

    public final MinecraftKey getLootTable() {
        if (this.drops == null) {
            MinecraftKey minecraftkey = IRegistry.BLOCK.getKey(this.asBlock());

            this.drops = new MinecraftKey(minecraftkey.getNamespace(), "blocks/" + minecraftkey.getPath());
        }

        return this.drops;
    }

    /** @deprecated */
    @Deprecated
    public void onProjectileHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {}

    public abstract Item asItem();

    protected abstract Block asBlock();

    public MaterialMapColor defaultMaterialColor() {
        return (MaterialMapColor) this.properties.materialColor.apply(this.asBlock().defaultBlockState());
    }

    public float defaultDestroyTime() {
        return this.properties.destroyTime;
    }

    public static class Info {

        Material material;
        Function<IBlockData, MaterialMapColor> materialColor;
        boolean hasCollision;
        SoundEffectType soundType;
        ToIntFunction<IBlockData> lightEmission;
        float explosionResistance;
        float destroyTime;
        boolean requiresCorrectToolForDrops;
        boolean isRandomlyTicking;
        float friction;
        float speedFactor;
        float jumpFactor;
        MinecraftKey drops;
        boolean canOcclude;
        boolean isAir;
        BlockBase.d<EntityTypes<?>> isValidSpawn;
        BlockBase.e isRedstoneConductor;
        BlockBase.e isSuffocating;
        BlockBase.e isViewBlocking;
        BlockBase.e hasPostProcess;
        BlockBase.e emissiveRendering;
        boolean dynamicShape;

        private Info(Material material, MaterialMapColor materialmapcolor) {
            this(material, (iblockdata) -> {
                return materialmapcolor;
            });
        }

        private Info(Material material, Function<IBlockData, MaterialMapColor> function) {
            this.hasCollision = true;
            this.soundType = SoundEffectType.STONE;
            this.lightEmission = (iblockdata) -> {
                return 0;
            };
            this.friction = 0.6F;
            this.speedFactor = 1.0F;
            this.jumpFactor = 1.0F;
            this.canOcclude = true;
            this.isValidSpawn = (iblockdata, iblockaccess, blockposition, entitytypes) -> {
                return iblockdata.isFaceSturdy(iblockaccess, blockposition, EnumDirection.UP) && iblockdata.getLightEmission() < 14;
            };
            this.isRedstoneConductor = (iblockdata, iblockaccess, blockposition) -> {
                return iblockdata.getMaterial().isSolidBlocking() && iblockdata.isCollisionShapeFullBlock(iblockaccess, blockposition);
            };
            this.isSuffocating = (iblockdata, iblockaccess, blockposition) -> {
                return this.material.blocksMotion() && iblockdata.isCollisionShapeFullBlock(iblockaccess, blockposition);
            };
            this.isViewBlocking = this.isSuffocating;
            this.hasPostProcess = (iblockdata, iblockaccess, blockposition) -> {
                return false;
            };
            this.emissiveRendering = (iblockdata, iblockaccess, blockposition) -> {
                return false;
            };
            this.material = material;
            this.materialColor = function;
        }

        public static BlockBase.Info of(Material material) {
            return of(material, material.getColor());
        }

        public static BlockBase.Info of(Material material, EnumColor enumcolor) {
            return of(material, enumcolor.getMaterialColor());
        }

        public static BlockBase.Info of(Material material, MaterialMapColor materialmapcolor) {
            return new BlockBase.Info(material, materialmapcolor);
        }

        public static BlockBase.Info of(Material material, Function<IBlockData, MaterialMapColor> function) {
            return new BlockBase.Info(material, function);
        }

        public static BlockBase.Info copy(BlockBase blockbase) {
            BlockBase.Info blockbase_info = new BlockBase.Info(blockbase.material, blockbase.properties.materialColor);

            blockbase_info.material = blockbase.properties.material;
            blockbase_info.destroyTime = blockbase.properties.destroyTime;
            blockbase_info.explosionResistance = blockbase.properties.explosionResistance;
            blockbase_info.hasCollision = blockbase.properties.hasCollision;
            blockbase_info.isRandomlyTicking = blockbase.properties.isRandomlyTicking;
            blockbase_info.lightEmission = blockbase.properties.lightEmission;
            blockbase_info.materialColor = blockbase.properties.materialColor;
            blockbase_info.soundType = blockbase.properties.soundType;
            blockbase_info.friction = blockbase.properties.friction;
            blockbase_info.speedFactor = blockbase.properties.speedFactor;
            blockbase_info.dynamicShape = blockbase.properties.dynamicShape;
            blockbase_info.canOcclude = blockbase.properties.canOcclude;
            blockbase_info.isAir = blockbase.properties.isAir;
            blockbase_info.requiresCorrectToolForDrops = blockbase.properties.requiresCorrectToolForDrops;
            return blockbase_info;
        }

        public BlockBase.Info noCollission() {
            this.hasCollision = false;
            this.canOcclude = false;
            return this;
        }

        public BlockBase.Info noOcclusion() {
            this.canOcclude = false;
            return this;
        }

        public BlockBase.Info friction(float f) {
            this.friction = f;
            return this;
        }

        public BlockBase.Info speedFactor(float f) {
            this.speedFactor = f;
            return this;
        }

        public BlockBase.Info jumpFactor(float f) {
            this.jumpFactor = f;
            return this;
        }

        public BlockBase.Info sound(SoundEffectType soundeffecttype) {
            this.soundType = soundeffecttype;
            return this;
        }

        public BlockBase.Info lightLevel(ToIntFunction<IBlockData> tointfunction) {
            this.lightEmission = tointfunction;
            return this;
        }

        public BlockBase.Info strength(float f, float f1) {
            return this.destroyTime(f).explosionResistance(f1);
        }

        public BlockBase.Info instabreak() {
            return this.strength(0.0F);
        }

        public BlockBase.Info strength(float f) {
            this.strength(f, f);
            return this;
        }

        public BlockBase.Info randomTicks() {
            this.isRandomlyTicking = true;
            return this;
        }

        public BlockBase.Info dynamicShape() {
            this.dynamicShape = true;
            return this;
        }

        public BlockBase.Info noDrops() {
            this.drops = LootTables.EMPTY;
            return this;
        }

        public BlockBase.Info dropsLike(Block block) {
            this.drops = block.getLootTable();
            return this;
        }

        public BlockBase.Info air() {
            this.isAir = true;
            return this;
        }

        public BlockBase.Info isValidSpawn(BlockBase.d<EntityTypes<?>> blockbase_d) {
            this.isValidSpawn = blockbase_d;
            return this;
        }

        public BlockBase.Info isRedstoneConductor(BlockBase.e blockbase_e) {
            this.isRedstoneConductor = blockbase_e;
            return this;
        }

        public BlockBase.Info isSuffocating(BlockBase.e blockbase_e) {
            this.isSuffocating = blockbase_e;
            return this;
        }

        public BlockBase.Info isViewBlocking(BlockBase.e blockbase_e) {
            this.isViewBlocking = blockbase_e;
            return this;
        }

        public BlockBase.Info hasPostProcess(BlockBase.e blockbase_e) {
            this.hasPostProcess = blockbase_e;
            return this;
        }

        public BlockBase.Info emissiveRendering(BlockBase.e blockbase_e) {
            this.emissiveRendering = blockbase_e;
            return this;
        }

        public BlockBase.Info requiresCorrectToolForDrops() {
            this.requiresCorrectToolForDrops = true;
            return this;
        }

        public BlockBase.Info color(MaterialMapColor materialmapcolor) {
            this.materialColor = (iblockdata) -> {
                return materialmapcolor;
            };
            return this;
        }

        public BlockBase.Info destroyTime(float f) {
            this.destroyTime = f;
            return this;
        }

        public BlockBase.Info explosionResistance(float f) {
            this.explosionResistance = Math.max(0.0F, f);
            return this;
        }
    }

    public static enum EnumRandomOffset {

        NONE, XZ, XYZ;

        private EnumRandomOffset() {}
    }

    public interface d<A> {

        boolean test(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, A a0);
    }

    public interface e {

        boolean test(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition);
    }

    public abstract static class BlockData extends IBlockDataHolder<Block, IBlockData> {

        private final int lightEmission;
        private final boolean useShapeForLightOcclusion;
        private final boolean isAir;
        private final Material material;
        private final MaterialMapColor materialColor;
        public final float destroySpeed;
        private final boolean requiresCorrectToolForDrops;
        private final boolean canOcclude;
        private final BlockBase.e isRedstoneConductor;
        private final BlockBase.e isSuffocating;
        private final BlockBase.e isViewBlocking;
        private final BlockBase.e hasPostProcess;
        private final BlockBase.e emissiveRendering;
        @Nullable
        protected BlockBase.BlockData.Cache cache;

        protected BlockData(Block block, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap, MapCodec<IBlockData> mapcodec) {
            super(block, immutablemap, mapcodec);
            BlockBase.Info blockbase_info = block.properties;

            this.lightEmission = blockbase_info.lightEmission.applyAsInt(this.asState());
            this.useShapeForLightOcclusion = block.useShapeForLightOcclusion(this.asState());
            this.isAir = blockbase_info.isAir;
            this.material = blockbase_info.material;
            this.materialColor = (MaterialMapColor) blockbase_info.materialColor.apply(this.asState());
            this.destroySpeed = blockbase_info.destroyTime;
            this.requiresCorrectToolForDrops = blockbase_info.requiresCorrectToolForDrops;
            this.canOcclude = blockbase_info.canOcclude;
            this.isRedstoneConductor = blockbase_info.isRedstoneConductor;
            this.isSuffocating = blockbase_info.isSuffocating;
            this.isViewBlocking = blockbase_info.isViewBlocking;
            this.hasPostProcess = blockbase_info.hasPostProcess;
            this.emissiveRendering = blockbase_info.emissiveRendering;
        }

        public void initCache() {
            if (!this.getBlock().hasDynamicShape()) {
                this.cache = new BlockBase.BlockData.Cache(this.asState());
            }

        }

        public Block getBlock() {
            return (Block) this.owner;
        }

        public Material getMaterial() {
            return this.material;
        }

        public boolean isValidSpawn(IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
            return this.getBlock().properties.isValidSpawn.test(this.asState(), iblockaccess, blockposition, entitytypes);
        }

        public boolean propagatesSkylightDown(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this.asState(), iblockaccess, blockposition);
        }

        public int getLightBlock(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.cache != null ? this.cache.lightBlock : this.getBlock().getLightBlock(this.asState(), iblockaccess, blockposition);
        }

        public VoxelShape getFaceOcclusionShape(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.cache != null && this.cache.occlusionShapes != null ? this.cache.occlusionShapes[enumdirection.ordinal()] : VoxelShapes.getFaceShape(this.getOcclusionShape(iblockaccess, blockposition), enumdirection);
        }

        public VoxelShape getOcclusionShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().getOcclusionShape(this.asState(), iblockaccess, blockposition);
        }

        public boolean hasLargeCollisionShape() {
            return this.cache == null || this.cache.largeCollisionShape;
        }

        public boolean useShapeForLightOcclusion() {
            return this.useShapeForLightOcclusion;
        }

        public int getLightEmission() {
            return this.lightEmission;
        }

        public boolean isAir() {
            return this.isAir;
        }

        public MaterialMapColor getMapColor(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.materialColor;
        }

        public IBlockData rotate(EnumBlockRotation enumblockrotation) {
            return this.getBlock().rotate(this.asState(), enumblockrotation);
        }

        public IBlockData mirror(EnumBlockMirror enumblockmirror) {
            return this.getBlock().mirror(this.asState(), enumblockmirror);
        }

        public EnumRenderType getRenderShape() {
            return this.getBlock().getRenderShape(this.asState());
        }

        public boolean emissiveRendering(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.emissiveRendering.test(this.asState(), iblockaccess, blockposition);
        }

        public float getShadeBrightness(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().getShadeBrightness(this.asState(), iblockaccess, blockposition);
        }

        public boolean isRedstoneConductor(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.isRedstoneConductor.test(this.asState(), iblockaccess, blockposition);
        }

        public boolean isSignalSource() {
            return this.getBlock().isSignalSource(this.asState());
        }

        public int getSignal(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.getBlock().getSignal(this.asState(), iblockaccess, blockposition, enumdirection);
        }

        public boolean hasAnalogOutputSignal() {
            return this.getBlock().hasAnalogOutputSignal(this.asState());
        }

        public int getAnalogOutputSignal(World world, BlockPosition blockposition) {
            return this.getBlock().getAnalogOutputSignal(this.asState(), world, blockposition);
        }

        public float getDestroySpeed(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.destroySpeed;
        }

        public float getDestroyProgress(EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().getDestroyProgress(this.asState(), entityhuman, iblockaccess, blockposition);
        }

        public int getDirectSignal(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.getBlock().getDirectSignal(this.asState(), iblockaccess, blockposition, enumdirection);
        }

        public EnumPistonReaction getPistonPushReaction() {
            return this.getBlock().getPistonPushReaction(this.asState());
        }

        public boolean isSolidRender(IBlockAccess iblockaccess, BlockPosition blockposition) {
            if (this.cache != null) {
                return this.cache.solidRender;
            } else {
                IBlockData iblockdata = this.asState();

                return iblockdata.canOcclude() ? Block.isShapeFullBlock(iblockdata.getOcclusionShape(iblockaccess, blockposition)) : false;
            }
        }

        public boolean canOcclude() {
            return this.canOcclude;
        }

        public boolean skipRendering(IBlockData iblockdata, EnumDirection enumdirection) {
            return this.getBlock().skipRendering(this.asState(), iblockdata, enumdirection);
        }

        public VoxelShape getShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getShape(iblockaccess, blockposition, VoxelShapeCollision.empty());
        }

        public VoxelShape getShape(IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
            return this.getBlock().getShape(this.asState(), iblockaccess, blockposition, voxelshapecollision);
        }

        public VoxelShape getCollisionShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.cache != null ? this.cache.collisionShape : this.getCollisionShape(iblockaccess, blockposition, VoxelShapeCollision.empty());
        }

        public VoxelShape getCollisionShape(IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
            return this.getBlock().getCollisionShape(this.asState(), iblockaccess, blockposition, voxelshapecollision);
        }

        public VoxelShape getBlockSupportShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().getBlockSupportShape(this.asState(), iblockaccess, blockposition);
        }

        public VoxelShape getVisualShape(IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
            return this.getBlock().getVisualShape(this.asState(), iblockaccess, blockposition, voxelshapecollision);
        }

        public VoxelShape getInteractionShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().getInteractionShape(this.asState(), iblockaccess, blockposition);
        }

        public final boolean entityCanStandOn(IBlockAccess iblockaccess, BlockPosition blockposition, Entity entity) {
            return this.entityCanStandOnFace(iblockaccess, blockposition, entity, EnumDirection.UP);
        }

        public final boolean entityCanStandOnFace(IBlockAccess iblockaccess, BlockPosition blockposition, Entity entity, EnumDirection enumdirection) {
            return Block.isFaceFull(this.getCollisionShape(iblockaccess, blockposition, VoxelShapeCollision.of(entity)), enumdirection);
        }

        public Vec3D getOffset(IBlockAccess iblockaccess, BlockPosition blockposition) {
            Block block = this.getBlock();
            BlockBase.EnumRandomOffset blockbase_enumrandomoffset = block.getOffsetType();

            if (blockbase_enumrandomoffset == BlockBase.EnumRandomOffset.NONE) {
                return Vec3D.ZERO;
            } else {
                long i = MathHelper.getSeed(blockposition.getX(), 0, blockposition.getZ());
                float f = block.getMaxHorizontalOffset();
                double d0 = MathHelper.clamp(((double) ((float) (i & 15L) / 15.0F) - 0.5D) * 0.5D, (double) (-f), (double) f);
                double d1 = blockbase_enumrandomoffset == BlockBase.EnumRandomOffset.XYZ ? ((double) ((float) (i >> 4 & 15L) / 15.0F) - 1.0D) * (double) block.getMaxVerticalOffset() : 0.0D;
                double d2 = MathHelper.clamp(((double) ((float) (i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D, (double) (-f), (double) f);

                return new Vec3D(d0, d1, d2);
            }
        }

        public boolean triggerEvent(World world, BlockPosition blockposition, int i, int j) {
            return this.getBlock().triggerEvent(this.asState(), world, blockposition, i, j);
        }

        public void neighborChanged(World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
            this.getBlock().neighborChanged(this.asState(), world, blockposition, block, blockposition1, flag);
        }

        public final void updateNeighbourShapes(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
            this.updateNeighbourShapes(generatoraccess, blockposition, i, 512);
        }

        public final void updateNeighbourShapes(GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {
            this.getBlock();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            EnumDirection[] aenumdirection = BlockBase.UPDATE_SHAPE_ORDER;
            int k = aenumdirection.length;

            for (int l = 0; l < k; ++l) {
                EnumDirection enumdirection = aenumdirection[l];

                blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
                IBlockData iblockdata = generatoraccess.getBlockState(blockposition_mutableblockposition);
                IBlockData iblockdata1 = iblockdata.updateShape(enumdirection.getOpposite(), this.asState(), generatoraccess, blockposition_mutableblockposition, blockposition);

                Block.updateOrDestroy(iblockdata, iblockdata1, generatoraccess, blockposition_mutableblockposition, i, j);
            }

        }

        public final void updateIndirectNeighbourShapes(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
            this.updateIndirectNeighbourShapes(generatoraccess, blockposition, i, 512);
        }

        public void updateIndirectNeighbourShapes(GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {
            this.getBlock().updateIndirectNeighbourShapes(this.asState(), generatoraccess, blockposition, i, j);
        }

        public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
            this.getBlock().onPlace(this.asState(), world, blockposition, iblockdata, flag);
        }

        public void onRemove(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
            this.getBlock().onRemove(this.asState(), world, blockposition, iblockdata, flag);
        }

        public void tick(WorldServer worldserver, BlockPosition blockposition, Random random) {
            this.getBlock().tick(this.asState(), worldserver, blockposition, random);
        }

        public void randomTick(WorldServer worldserver, BlockPosition blockposition, Random random) {
            this.getBlock().randomTick(this.asState(), worldserver, blockposition, random);
        }

        public void entityInside(World world, BlockPosition blockposition, Entity entity) {
            this.getBlock().entityInside(this.asState(), world, blockposition, entity);
        }

        public void spawnAfterBreak(WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
            this.getBlock().spawnAfterBreak(this.asState(), worldserver, blockposition, itemstack);
        }

        public List<ItemStack> getDrops(LootTableInfo.Builder loottableinfo_builder) {
            return this.getBlock().getDrops(this.asState(), loottableinfo_builder);
        }

        public EnumInteractionResult use(World world, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
            return this.getBlock().use(this.asState(), world, movingobjectpositionblock.getBlockPos(), entityhuman, enumhand, movingobjectpositionblock);
        }

        public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
            this.getBlock().attack(this.asState(), world, blockposition, entityhuman);
        }

        public boolean isSuffocating(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.isSuffocating.test(this.asState(), iblockaccess, blockposition);
        }

        public boolean isViewBlocking(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.isViewBlocking.test(this.asState(), iblockaccess, blockposition);
        }

        public IBlockData updateShape(EnumDirection enumdirection, IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
            return this.getBlock().updateShape(this.asState(), enumdirection, iblockdata, generatoraccess, blockposition, blockposition1);
        }

        public boolean isPathfindable(IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
            return this.getBlock().isPathfindable(this.asState(), iblockaccess, blockposition, pathmode);
        }

        public boolean canBeReplaced(BlockActionContext blockactioncontext) {
            return this.getBlock().canBeReplaced(this.asState(), blockactioncontext);
        }

        public boolean canBeReplaced(FluidType fluidtype) {
            return this.getBlock().canBeReplaced(this.asState(), fluidtype);
        }

        public boolean canSurvive(IWorldReader iworldreader, BlockPosition blockposition) {
            return this.getBlock().canSurvive(this.asState(), iworldreader, blockposition);
        }

        public boolean hasPostProcess(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.hasPostProcess.test(this.asState(), iblockaccess, blockposition);
        }

        @Nullable
        public ITileInventory getMenuProvider(World world, BlockPosition blockposition) {
            return this.getBlock().getMenuProvider(this.asState(), world, blockposition);
        }

        public boolean is(Tag<Block> tag) {
            return tag.contains(this.getBlock());
        }

        public boolean is(Tag<Block> tag, Predicate<BlockBase.BlockData> predicate) {
            return this.is(tag) && predicate.test(this);
        }

        public boolean hasBlockEntity() {
            return this.getBlock() instanceof ITileEntity;
        }

        @Nullable
        public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, TileEntityTypes<T> tileentitytypes) {
            return this.getBlock() instanceof ITileEntity ? ((ITileEntity) this.getBlock()).getTicker(world, this.asState(), tileentitytypes) : null;
        }

        public boolean is(Block block) {
            return this.getBlock() == block;
        }

        public Fluid getFluidState() {
            return this.getBlock().getFluidState(this.asState());
        }

        public boolean isRandomlyTicking() {
            return this.getBlock().isRandomlyTicking(this.asState());
        }

        public long getSeed(BlockPosition blockposition) {
            return this.getBlock().getSeed(this.asState(), blockposition);
        }

        public SoundEffectType getSoundType() {
            return this.getBlock().getSoundType(this.asState());
        }

        public void onProjectileHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
            this.getBlock().onProjectileHit(world, iblockdata, movingobjectpositionblock, iprojectile);
        }

        public boolean isFaceSturdy(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.isFaceSturdy(iblockaccess, blockposition, enumdirection, EnumBlockSupport.FULL);
        }

        public boolean isFaceSturdy(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection, EnumBlockSupport enumblocksupport) {
            return this.cache != null ? this.cache.isFaceSturdy(enumdirection, enumblocksupport) : enumblocksupport.isSupporting(this.asState(), iblockaccess, blockposition, enumdirection);
        }

        public boolean isCollisionShapeFullBlock(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.cache != null ? this.cache.isCollisionShapeFullBlock : this.getBlock().isCollisionShapeFullBlock(this.asState(), iblockaccess, blockposition);
        }

        protected abstract IBlockData asState();

        public boolean requiresCorrectToolForDrops() {
            return this.requiresCorrectToolForDrops;
        }

        private static final class Cache {

            private static final EnumDirection[] DIRECTIONS = EnumDirection.values();
            private static final int SUPPORT_TYPE_COUNT = EnumBlockSupport.values().length;
            protected final boolean solidRender;
            final boolean propagatesSkylightDown;
            final int lightBlock;
            @Nullable
            final VoxelShape[] occlusionShapes;
            protected final VoxelShape collisionShape;
            protected final boolean largeCollisionShape;
            private final boolean[] faceSturdy;
            protected final boolean isCollisionShapeFullBlock;

            Cache(IBlockData iblockdata) {
                Block block = iblockdata.getBlock();

                this.solidRender = iblockdata.isSolidRender(BlockAccessAir.INSTANCE, BlockPosition.ZERO);
                this.propagatesSkylightDown = block.propagatesSkylightDown(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO);
                this.lightBlock = block.getLightBlock(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO);
                int i;

                if (!iblockdata.canOcclude()) {
                    this.occlusionShapes = null;
                } else {
                    this.occlusionShapes = new VoxelShape[BlockBase.BlockData.Cache.DIRECTIONS.length];
                    VoxelShape voxelshape = block.getOcclusionShape(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO);
                    EnumDirection[] aenumdirection = BlockBase.BlockData.Cache.DIRECTIONS;

                    i = aenumdirection.length;

                    for (int j = 0; j < i; ++j) {
                        EnumDirection enumdirection = aenumdirection[j];

                        this.occlusionShapes[enumdirection.ordinal()] = VoxelShapes.getFaceShape(voxelshape, enumdirection);
                    }
                }

                this.collisionShape = block.getCollisionShape(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO, VoxelShapeCollision.empty());
                if (!this.collisionShape.isEmpty() && block.getOffsetType() != BlockBase.EnumRandomOffset.NONE) {
                    throw new IllegalStateException(String.format("%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", IRegistry.BLOCK.getKey(block)));
                } else {
                    this.largeCollisionShape = Arrays.stream(EnumDirection.EnumAxis.values()).anyMatch((enumdirection_enumaxis) -> {
                        return this.collisionShape.min(enumdirection_enumaxis) < 0.0D || this.collisionShape.max(enumdirection_enumaxis) > 1.0D;
                    });
                    this.faceSturdy = new boolean[BlockBase.BlockData.Cache.DIRECTIONS.length * BlockBase.BlockData.Cache.SUPPORT_TYPE_COUNT];
                    EnumDirection[] aenumdirection1 = BlockBase.BlockData.Cache.DIRECTIONS;
                    int k = aenumdirection1.length;

                    for (i = 0; i < k; ++i) {
                        EnumDirection enumdirection1 = aenumdirection1[i];
                        EnumBlockSupport[] aenumblocksupport = EnumBlockSupport.values();
                        int l = aenumblocksupport.length;

                        for (int i1 = 0; i1 < l; ++i1) {
                            EnumBlockSupport enumblocksupport = aenumblocksupport[i1];

                            this.faceSturdy[getFaceSupportIndex(enumdirection1, enumblocksupport)] = enumblocksupport.isSupporting(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO, enumdirection1);
                        }
                    }

                    this.isCollisionShapeFullBlock = Block.isShapeFullBlock(iblockdata.getCollisionShape(BlockAccessAir.INSTANCE, BlockPosition.ZERO));
                }
            }

            public boolean isFaceSturdy(EnumDirection enumdirection, EnumBlockSupport enumblocksupport) {
                return this.faceSturdy[getFaceSupportIndex(enumdirection, enumblocksupport)];
            }

            private static int getFaceSupportIndex(EnumDirection enumdirection, EnumBlockSupport enumblocksupport) {
                return enumdirection.ordinal() * BlockBase.BlockData.Cache.SUPPORT_TYPE_COUNT + enumblocksupport.ordinal();
            }
        }
    }
}
