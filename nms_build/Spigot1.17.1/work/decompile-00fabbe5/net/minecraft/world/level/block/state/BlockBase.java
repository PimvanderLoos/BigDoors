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
import net.minecraft.core.BaseBlockPosition;
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

    @Deprecated
    public void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {}

    @Deprecated
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
            case LAND:
                return !iblockdata.r(iblockaccess, blockposition);
            case WATER:
                return iblockaccess.getFluid(blockposition).a((Tag) TagsFluid.WATER);
            case AIR:
                return !iblockdata.r(iblockaccess, blockposition);
            default:
                return false;
        }
    }

    @Deprecated
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return iblockdata;
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
        return false;
    }

    @Deprecated
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        PacketDebug.a(world, blockposition);
    }

    @Deprecated
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {}

    @Deprecated
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.isTileEntity() && !iblockdata.a(iblockdata1.getBlock())) {
            world.removeTileEntity(blockposition);
        }

    }

    @Deprecated
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return EnumInteractionResult.PASS;
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        return false;
    }

    @Deprecated
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Deprecated
    public boolean g_(IBlockData iblockdata) {
        return false;
    }

    @Deprecated
    public boolean isPowerSource(IBlockData iblockdata) {
        return false;
    }

    @Deprecated
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return this.material.getPushReaction();
    }

    @Deprecated
    public Fluid c_(IBlockData iblockdata) {
        return FluidTypes.EMPTY.h();
    }

    @Deprecated
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return false;
    }

    public BlockBase.EnumRandomOffset S_() {
        return BlockBase.EnumRandomOffset.NONE;
    }

    public float U_() {
        return 0.25F;
    }

    public float X_() {
        return 0.2F;
    }

    @Deprecated
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata;
    }

    @Deprecated
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata;
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return this.material.isReplaceable() && (blockactioncontext.getItemStack().isEmpty() || !blockactioncontext.getItemStack().a(this.getItem()));
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, FluidType fluidtype) {
        return this.material.isReplaceable() || !this.material.isBuildable();
    }

    @Deprecated
    public List<ItemStack> a(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        MinecraftKey minecraftkey = this.r();

        if (minecraftkey == LootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootTableInfo loottableinfo = loottableinfo_builder.set(LootContextParameters.BLOCK_STATE, iblockdata).build(LootContextParameterSets.BLOCK);
            WorldServer worldserver = loottableinfo.getWorld();
            LootTable loottable = worldserver.getMinecraftServer().getLootTableRegistry().getLootTable(minecraftkey);

            return loottable.populateLoot(loottableinfo);
        }
    }

    @Deprecated
    public long a(IBlockData iblockdata, BlockPosition blockposition) {
        return MathHelper.a((BaseBlockPosition) blockposition);
    }

    @Deprecated
    public VoxelShape b_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getShape(iblockaccess, blockposition);
    }

    @Deprecated
    public VoxelShape f(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.c(iblockdata, iblockaccess, blockposition, VoxelShapeCollision.a());
    }

    @Deprecated
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    @Deprecated
    public int g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.i(iblockaccess, blockposition) ? iblockaccess.O() : (iblockdata.a(iblockaccess, blockposition) ? 0 : 1);
    }

    @Nullable
    @Deprecated
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return null;
    }

    @Deprecated
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return true;
    }

    @Deprecated
    public float b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.r(iblockaccess, blockposition) ? 0.2F : 1.0F;
    }

    @Deprecated
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return 0;
    }

    @Deprecated
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.b();
    }

    @Deprecated
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.hasCollision ? iblockdata.getShape(iblockaccess, blockposition) : VoxelShapes.a();
    }

    @Deprecated
    public boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return Block.a(iblockdata.getCollisionShape(iblockaccess, blockposition));
    }

    @Deprecated
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.c(iblockdata, iblockaccess, blockposition, voxelshapecollision);
    }

    @Deprecated
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        this.tickAlways(iblockdata, worldserver, blockposition, random);
    }

    @Deprecated
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {}

    @Deprecated
    public float getDamage(IBlockData iblockdata, EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
        float f = iblockdata.h(iblockaccess, blockposition);

        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = entityhuman.hasBlock(iblockdata) ? 30 : 100;

            return entityhuman.c(iblockdata) / f / (float) i;
        }
    }

    @Deprecated
    public void dropNaturally(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {}

    @Deprecated
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {}

    @Deprecated
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 0;
    }

    @Deprecated
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {}

    @Deprecated
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 0;
    }

    public final MinecraftKey r() {
        if (this.drops == null) {
            MinecraftKey minecraftkey = IRegistry.BLOCK.getKey(this.p());

            this.drops = new MinecraftKey(minecraftkey.getNamespace(), "blocks/" + minecraftkey.getKey());
        }

        return this.drops;
    }

    @Deprecated
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {}

    public abstract Item getItem();

    protected abstract Block p();

    public MaterialMapColor s() {
        return (MaterialMapColor) this.properties.materialColor.apply(this.p().getBlockData());
    }

    public float t() {
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
                return iblockdata.d(iblockaccess, blockposition, EnumDirection.UP) && iblockdata.f() < 14;
            };
            this.isRedstoneConductor = (iblockdata, iblockaccess, blockposition) -> {
                return iblockdata.getMaterial().f() && iblockdata.r(iblockaccess, blockposition);
            };
            this.isSuffocating = (iblockdata, iblockaccess, blockposition) -> {
                return this.material.isSolid() && iblockdata.r(iblockaccess, blockposition);
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

        public static BlockBase.Info a(Material material) {
            return a(material, material.h());
        }

        public static BlockBase.Info a(Material material, EnumColor enumcolor) {
            return a(material, enumcolor.e());
        }

        public static BlockBase.Info a(Material material, MaterialMapColor materialmapcolor) {
            return new BlockBase.Info(material, materialmapcolor);
        }

        public static BlockBase.Info a(Material material, Function<IBlockData, MaterialMapColor> function) {
            return new BlockBase.Info(material, function);
        }

        public static BlockBase.Info a(BlockBase blockbase) {
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

        public BlockBase.Info a() {
            this.hasCollision = false;
            this.canOcclude = false;
            return this;
        }

        public BlockBase.Info b() {
            this.canOcclude = false;
            return this;
        }

        public BlockBase.Info a(float f) {
            this.friction = f;
            return this;
        }

        public BlockBase.Info b(float f) {
            this.speedFactor = f;
            return this;
        }

        public BlockBase.Info c(float f) {
            this.jumpFactor = f;
            return this;
        }

        public BlockBase.Info a(SoundEffectType soundeffecttype) {
            this.soundType = soundeffecttype;
            return this;
        }

        public BlockBase.Info a(ToIntFunction<IBlockData> tointfunction) {
            this.lightEmission = tointfunction;
            return this;
        }

        public BlockBase.Info a(float f, float f1) {
            return this.e(f).f(f1);
        }

        public BlockBase.Info c() {
            return this.d(0.0F);
        }

        public BlockBase.Info d(float f) {
            this.a(f, f);
            return this;
        }

        public BlockBase.Info d() {
            this.isRandomlyTicking = true;
            return this;
        }

        public BlockBase.Info e() {
            this.dynamicShape = true;
            return this;
        }

        public BlockBase.Info f() {
            this.drops = LootTables.EMPTY;
            return this;
        }

        public BlockBase.Info a(Block block) {
            this.drops = block.r();
            return this;
        }

        public BlockBase.Info g() {
            this.isAir = true;
            return this;
        }

        public BlockBase.Info a(BlockBase.d<EntityTypes<?>> blockbase_d) {
            this.isValidSpawn = blockbase_d;
            return this;
        }

        public BlockBase.Info a(BlockBase.e blockbase_e) {
            this.isRedstoneConductor = blockbase_e;
            return this;
        }

        public BlockBase.Info b(BlockBase.e blockbase_e) {
            this.isSuffocating = blockbase_e;
            return this;
        }

        public BlockBase.Info c(BlockBase.e blockbase_e) {
            this.isViewBlocking = blockbase_e;
            return this;
        }

        public BlockBase.Info d(BlockBase.e blockbase_e) {
            this.hasPostProcess = blockbase_e;
            return this;
        }

        public BlockBase.Info e(BlockBase.e blockbase_e) {
            this.emissiveRendering = blockbase_e;
            return this;
        }

        public BlockBase.Info h() {
            this.requiresCorrectToolForDrops = true;
            return this;
        }

        public BlockBase.Info a(MaterialMapColor materialmapcolor) {
            this.materialColor = (iblockdata) -> {
                return materialmapcolor;
            };
            return this;
        }

        public BlockBase.Info e(float f) {
            this.destroyTime = f;
            return this;
        }

        public BlockBase.Info f(float f) {
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

            this.lightEmission = blockbase_info.lightEmission.applyAsInt(this.q());
            this.useShapeForLightOcclusion = block.g_(this.q());
            this.isAir = blockbase_info.isAir;
            this.material = blockbase_info.material;
            this.materialColor = (MaterialMapColor) blockbase_info.materialColor.apply(this.q());
            this.destroySpeed = blockbase_info.destroyTime;
            this.requiresCorrectToolForDrops = blockbase_info.requiresCorrectToolForDrops;
            this.canOcclude = blockbase_info.canOcclude;
            this.isRedstoneConductor = blockbase_info.isRedstoneConductor;
            this.isSuffocating = blockbase_info.isSuffocating;
            this.isViewBlocking = blockbase_info.isViewBlocking;
            this.hasPostProcess = blockbase_info.hasPostProcess;
            this.emissiveRendering = blockbase_info.emissiveRendering;
        }

        public void a() {
            if (!this.getBlock().o()) {
                this.cache = new BlockBase.BlockData.Cache(this.q());
            }

        }

        public Block getBlock() {
            return (Block) this.owner;
        }

        public Material getMaterial() {
            return this.material;
        }

        public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
            return this.getBlock().properties.isValidSpawn.test(this.q(), iblockaccess, blockposition, entitytypes);
        }

        public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().c(this.q(), iblockaccess, blockposition);
        }

        public int b(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.cache != null ? this.cache.lightBlock : this.getBlock().g(this.q(), iblockaccess, blockposition);
        }

        public VoxelShape a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.cache != null && this.cache.occlusionShapes != null ? this.cache.occlusionShapes[enumdirection.ordinal()] : VoxelShapes.a(this.c(iblockaccess, blockposition), enumdirection);
        }

        public VoxelShape c(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().b_(this.q(), iblockaccess, blockposition);
        }

        public boolean d() {
            return this.cache == null || this.cache.largeCollisionShape;
        }

        public boolean e() {
            return this.useShapeForLightOcclusion;
        }

        public int f() {
            return this.lightEmission;
        }

        public boolean isAir() {
            return this.isAir;
        }

        public MaterialMapColor d(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.materialColor;
        }

        public IBlockData a(EnumBlockRotation enumblockrotation) {
            return this.getBlock().a(this.q(), enumblockrotation);
        }

        public IBlockData a(EnumBlockMirror enumblockmirror) {
            return this.getBlock().a(this.q(), enumblockmirror);
        }

        public EnumRenderType h() {
            return this.getBlock().b_(this.q());
        }

        public boolean e(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.emissiveRendering.test(this.q(), iblockaccess, blockposition);
        }

        public float f(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().b(this.q(), iblockaccess, blockposition);
        }

        public boolean isOccluding(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.isRedstoneConductor.test(this.q(), iblockaccess, blockposition);
        }

        public boolean isPowerSource() {
            return this.getBlock().isPowerSource(this.q());
        }

        public int b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.getBlock().a(this.q(), iblockaccess, blockposition, enumdirection);
        }

        public boolean isComplexRedstone() {
            return this.getBlock().isComplexRedstone(this.q());
        }

        public int a(World world, BlockPosition blockposition) {
            return this.getBlock().a(this.q(), world, blockposition);
        }

        public float h(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.destroySpeed;
        }

        public float getDamage(EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().getDamage(this.q(), entityhuman, iblockaccess, blockposition);
        }

        public int c(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.getBlock().b(this.q(), iblockaccess, blockposition, enumdirection);
        }

        public EnumPistonReaction getPushReaction() {
            return this.getBlock().getPushReaction(this.q());
        }

        public boolean i(IBlockAccess iblockaccess, BlockPosition blockposition) {
            if (this.cache != null) {
                return this.cache.solidRender;
            } else {
                IBlockData iblockdata = this.q();

                return iblockdata.l() ? Block.a(iblockdata.c(iblockaccess, blockposition)) : false;
            }
        }

        public boolean l() {
            return this.canOcclude;
        }

        public boolean a(IBlockData iblockdata, EnumDirection enumdirection) {
            return this.getBlock().a(this.q(), iblockdata, enumdirection);
        }

        public VoxelShape getShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.a(iblockaccess, blockposition, VoxelShapeCollision.a());
        }

        public VoxelShape a(IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
            return this.getBlock().a(this.q(), iblockaccess, blockposition, voxelshapecollision);
        }

        public VoxelShape getCollisionShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.cache != null ? this.cache.collisionShape : this.b(iblockaccess, blockposition, VoxelShapeCollision.a());
        }

        public VoxelShape b(IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
            return this.getBlock().c(this.q(), iblockaccess, blockposition, voxelshapecollision);
        }

        public VoxelShape l(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().f(this.q(), iblockaccess, blockposition);
        }

        public VoxelShape c(IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
            return this.getBlock().b(this.q(), iblockaccess, blockposition, voxelshapecollision);
        }

        public VoxelShape m(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.getBlock().a(this.q(), iblockaccess, blockposition);
        }

        public final boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, Entity entity) {
            return this.a(iblockaccess, blockposition, entity, EnumDirection.UP);
        }

        public final boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, Entity entity, EnumDirection enumdirection) {
            return Block.a(this.b(iblockaccess, blockposition, VoxelShapeCollision.a(entity)), enumdirection);
        }

        public Vec3D n(IBlockAccess iblockaccess, BlockPosition blockposition) {
            Block block = this.getBlock();
            BlockBase.EnumRandomOffset blockbase_enumrandomoffset = block.S_();

            if (blockbase_enumrandomoffset == BlockBase.EnumRandomOffset.NONE) {
                return Vec3D.ZERO;
            } else {
                long i = MathHelper.c(blockposition.getX(), 0, blockposition.getZ());
                float f = block.U_();
                double d0 = MathHelper.a(((double) ((float) (i & 15L) / 15.0F) - 0.5D) * 0.5D, (double) (-f), (double) f);
                double d1 = blockbase_enumrandomoffset == BlockBase.EnumRandomOffset.XYZ ? ((double) ((float) (i >> 4 & 15L) / 15.0F) - 1.0D) * (double) block.X_() : 0.0D;
                double d2 = MathHelper.a(((double) ((float) (i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D, (double) (-f), (double) f);

                return new Vec3D(d0, d1, d2);
            }
        }

        public boolean a(World world, BlockPosition blockposition, int i, int j) {
            return this.getBlock().a(this.q(), world, blockposition, i, j);
        }

        public void doPhysics(World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
            this.getBlock().doPhysics(this.q(), world, blockposition, block, blockposition1, flag);
        }

        public final void a(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
            this.a(generatoraccess, blockposition, i, 512);
        }

        public final void a(GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {
            this.getBlock();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            EnumDirection[] aenumdirection = BlockBase.UPDATE_SHAPE_ORDER;
            int k = aenumdirection.length;

            for (int l = 0; l < k; ++l) {
                EnumDirection enumdirection = aenumdirection[l];

                blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
                IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition);
                IBlockData iblockdata1 = iblockdata.updateState(enumdirection.opposite(), this.q(), generatoraccess, blockposition_mutableblockposition, blockposition);

                Block.a(iblockdata, iblockdata1, generatoraccess, blockposition_mutableblockposition, i, j);
            }

        }

        public final void b(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
            this.b(generatoraccess, blockposition, i, 512);
        }

        public void b(GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {
            this.getBlock().a(this.q(), generatoraccess, blockposition, i, j);
        }

        public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
            this.getBlock().onPlace(this.q(), world, blockposition, iblockdata, flag);
        }

        public void remove(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
            this.getBlock().remove(this.q(), world, blockposition, iblockdata, flag);
        }

        public void a(WorldServer worldserver, BlockPosition blockposition, Random random) {
            this.getBlock().tickAlways(this.q(), worldserver, blockposition, random);
        }

        public void b(WorldServer worldserver, BlockPosition blockposition, Random random) {
            this.getBlock().tick(this.q(), worldserver, blockposition, random);
        }

        public void a(World world, BlockPosition blockposition, Entity entity) {
            this.getBlock().a(this.q(), world, blockposition, entity);
        }

        public void dropNaturally(WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
            this.getBlock().dropNaturally(this.q(), worldserver, blockposition, itemstack);
        }

        public List<ItemStack> a(LootTableInfo.Builder loottableinfo_builder) {
            return this.getBlock().a(this.q(), loottableinfo_builder);
        }

        public EnumInteractionResult interact(World world, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
            return this.getBlock().interact(this.q(), world, movingobjectpositionblock.getBlockPosition(), entityhuman, enumhand, movingobjectpositionblock);
        }

        public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
            this.getBlock().attack(this.q(), world, blockposition, entityhuman);
        }

        public boolean o(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.isSuffocating.test(this.q(), iblockaccess, blockposition);
        }

        public boolean p(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.isViewBlocking.test(this.q(), iblockaccess, blockposition);
        }

        public IBlockData updateState(EnumDirection enumdirection, IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
            return this.getBlock().updateState(this.q(), enumdirection, iblockdata, generatoraccess, blockposition, blockposition1);
        }

        public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
            return this.getBlock().a(this.q(), iblockaccess, blockposition, pathmode);
        }

        public boolean a(BlockActionContext blockactioncontext) {
            return this.getBlock().a(this.q(), blockactioncontext);
        }

        public boolean a(FluidType fluidtype) {
            return this.getBlock().a(this.q(), fluidtype);
        }

        public boolean canPlace(IWorldReader iworldreader, BlockPosition blockposition) {
            return this.getBlock().canPlace(this.q(), iworldreader, blockposition);
        }

        public boolean q(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.hasPostProcess.test(this.q(), iblockaccess, blockposition);
        }

        @Nullable
        public ITileInventory b(World world, BlockPosition blockposition) {
            return this.getBlock().getInventory(this.q(), world, blockposition);
        }

        public boolean a(Tag<Block> tag) {
            return tag.isTagged(this.getBlock());
        }

        public boolean a(Tag<Block> tag, Predicate<BlockBase.BlockData> predicate) {
            return this.a(tag) && predicate.test(this);
        }

        public boolean isTileEntity() {
            return this.getBlock() instanceof ITileEntity;
        }

        @Nullable
        public <T extends TileEntity> BlockEntityTicker<T> a(World world, TileEntityTypes<T> tileentitytypes) {
            return this.getBlock() instanceof ITileEntity ? ((ITileEntity) this.getBlock()).a(world, this.q(), tileentitytypes) : null;
        }

        public boolean a(Block block) {
            return this.getBlock() == block;
        }

        public Fluid getFluid() {
            return this.getBlock().c_(this.q());
        }

        public boolean isTicking() {
            return this.getBlock().isTicking(this.q());
        }

        public long a(BlockPosition blockposition) {
            return this.getBlock().a(this.q(), blockposition);
        }

        public SoundEffectType getStepSound() {
            return this.getBlock().getStepSound(this.q());
        }

        public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
            this.getBlock().a(world, iblockdata, movingobjectpositionblock, iprojectile);
        }

        public boolean d(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.a(iblockaccess, blockposition, enumdirection, EnumBlockSupport.FULL);
        }

        public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection, EnumBlockSupport enumblocksupport) {
            return this.cache != null ? this.cache.a(enumdirection, enumblocksupport) : enumblocksupport.a(this.q(), iblockaccess, blockposition, enumdirection);
        }

        public boolean r(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.cache != null ? this.cache.isCollisionShapeFullBlock : this.getBlock().a_(this.q(), iblockaccess, blockposition);
        }

        protected abstract IBlockData q();

        public boolean isRequiresSpecialTool() {
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

                this.solidRender = iblockdata.i(BlockAccessAir.INSTANCE, BlockPosition.ZERO);
                this.propagatesSkylightDown = block.c(iblockdata, (IBlockAccess) BlockAccessAir.INSTANCE, BlockPosition.ZERO);
                this.lightBlock = block.g(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO);
                int i;

                if (!iblockdata.l()) {
                    this.occlusionShapes = null;
                } else {
                    this.occlusionShapes = new VoxelShape[BlockBase.BlockData.Cache.DIRECTIONS.length];
                    VoxelShape voxelshape = block.b_(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO);
                    EnumDirection[] aenumdirection = BlockBase.BlockData.Cache.DIRECTIONS;

                    i = aenumdirection.length;

                    for (int j = 0; j < i; ++j) {
                        EnumDirection enumdirection = aenumdirection[j];

                        this.occlusionShapes[enumdirection.ordinal()] = VoxelShapes.a(voxelshape, enumdirection);
                    }
                }

                this.collisionShape = block.c(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO, VoxelShapeCollision.a());
                if (!this.collisionShape.isEmpty() && block.S_() != BlockBase.EnumRandomOffset.NONE) {
                    throw new IllegalStateException(String.format("%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", IRegistry.BLOCK.getKey(block)));
                } else {
                    this.largeCollisionShape = Arrays.stream(EnumDirection.EnumAxis.values()).anyMatch((enumdirection_enumaxis) -> {
                        return this.collisionShape.b(enumdirection_enumaxis) < 0.0D || this.collisionShape.c(enumdirection_enumaxis) > 1.0D;
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

                            this.faceSturdy[b(enumdirection1, enumblocksupport)] = enumblocksupport.a(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO, enumdirection1);
                        }
                    }

                    this.isCollisionShapeFullBlock = Block.a(iblockdata.getCollisionShape(BlockAccessAir.INSTANCE, BlockPosition.ZERO));
                }
            }

            public boolean a(EnumDirection enumdirection, EnumBlockSupport enumblocksupport) {
                return this.faceSturdy[b(enumdirection, enumblocksupport)];
            }

            private static int b(EnumDirection enumdirection, EnumBlockSupport enumblocksupport) {
                return enumdirection.ordinal() * BlockBase.BlockData.Cache.SUPPORT_TYPE_COUNT + enumblocksupport.ordinal();
            }
        }
    }
}
