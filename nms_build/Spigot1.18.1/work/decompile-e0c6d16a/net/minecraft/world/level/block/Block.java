package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.server.level.WorldServer;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.piglin.PiglinAI;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block extends BlockBase implements IMaterial {

    protected static final Logger LOGGER = LogManager.getLogger();
    public static final RegistryBlockID<IBlockData> BLOCK_STATE_REGISTRY = new RegistryBlockID<>();
    private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>() {
        public Boolean load(VoxelShape voxelshape) {
            return !VoxelShapes.joinIsNotEmpty(VoxelShapes.block(), voxelshape, OperatorBoolean.NOT_SAME);
        }
    });
    public static final int UPDATE_NEIGHBORS = 1;
    public static final int UPDATE_CLIENTS = 2;
    public static final int UPDATE_INVISIBLE = 4;
    public static final int UPDATE_IMMEDIATE = 8;
    public static final int UPDATE_KNOWN_SHAPE = 16;
    public static final int UPDATE_SUPPRESS_DROPS = 32;
    public static final int UPDATE_MOVE_BY_PISTON = 64;
    public static final int UPDATE_SUPPRESS_LIGHT = 128;
    public static final int UPDATE_NONE = 4;
    public static final int UPDATE_ALL = 3;
    public static final int UPDATE_ALL_IMMEDIATE = 11;
    public static final float INDESTRUCTIBLE = -1.0F;
    public static final float INSTANT = 0.0F;
    public static final int UPDATE_LIMIT = 512;
    protected final BlockStateList<Block, IBlockData> stateDefinition;
    private IBlockData defaultBlockState;
    @Nullable
    private String descriptionId;
    @Nullable
    private Item item;
    private static final int CACHE_SIZE = 2048;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.a>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.a> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.a>(2048, 0.25F) {
            protected void rehash(int i) {}
        };

        object2bytelinkedopenhashmap.defaultReturnValue((byte) 127);
        return object2bytelinkedopenhashmap;
    });

    public static int getId(@Nullable IBlockData iblockdata) {
        if (iblockdata == null) {
            return 0;
        } else {
            int i = Block.BLOCK_STATE_REGISTRY.getId(iblockdata);

            return i == -1 ? 0 : i;
        }
    }

    public static IBlockData stateById(int i) {
        IBlockData iblockdata = (IBlockData) Block.BLOCK_STATE_REGISTRY.byId(i);

        return iblockdata == null ? Blocks.AIR.defaultBlockState() : iblockdata;
    }

    public static Block byItem(@Nullable Item item) {
        return item instanceof ItemBlock ? ((ItemBlock) item).getBlock() : Blocks.AIR;
    }

    public static IBlockData pushEntitiesUp(IBlockData iblockdata, IBlockData iblockdata1, World world, BlockPosition blockposition) {
        VoxelShape voxelshape = VoxelShapes.joinUnoptimized(iblockdata.getCollisionShape(world, blockposition), iblockdata1.getCollisionShape(world, blockposition), OperatorBoolean.ONLY_SECOND).move((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());

        if (voxelshape.isEmpty()) {
            return iblockdata1;
        } else {
            List<Entity> list = world.getEntities((Entity) null, voxelshape.bounds());
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();
                double d0 = VoxelShapes.collide(EnumDirection.EnumAxis.Y, entity.getBoundingBox().move(0.0D, 1.0D, 0.0D), List.of(voxelshape), -1.0D);

                entity.teleportTo(entity.getX(), entity.getY() + 1.0D + d0, entity.getZ());
            }

            return iblockdata1;
        }
    }

    public static VoxelShape box(double d0, double d1, double d2, double d3, double d4, double d5) {
        return VoxelShapes.box(d0 / 16.0D, d1 / 16.0D, d2 / 16.0D, d3 / 16.0D, d4 / 16.0D, d5 / 16.0D);
    }

    public static IBlockData updateFromNeighbourShapes(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata1 = iblockdata;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection[] aenumdirection = Block.UPDATE_SHAPE_ORDER;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
            iblockdata1 = iblockdata1.updateShape(enumdirection, generatoraccess.getBlockState(blockposition_mutableblockposition), generatoraccess, blockposition, blockposition_mutableblockposition);
        }

        return iblockdata1;
    }

    public static void updateOrDestroy(IBlockData iblockdata, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        updateOrDestroy(iblockdata, iblockdata1, generatoraccess, blockposition, i, 512);
    }

    public static void updateOrDestroy(IBlockData iblockdata, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {
        if (iblockdata1 != iblockdata) {
            if (iblockdata1.isAir()) {
                if (!generatoraccess.isClientSide()) {
                    generatoraccess.destroyBlock(blockposition, (i & 32) == 0, (Entity) null, j);
                }
            } else {
                generatoraccess.setBlock(blockposition, iblockdata1, i & -33, j);
            }
        }

    }

    public Block(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        BlockStateList.a<Block, IBlockData> blockstatelist_a = new BlockStateList.a<>(this);

        this.createBlockStateDefinition(blockstatelist_a);
        this.stateDefinition = blockstatelist_a.create(Block::defaultBlockState, IBlockData::new);
        this.registerDefaultState((IBlockData) this.stateDefinition.any());
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            String s = this.getClass().getSimpleName();

            if (!s.endsWith("Block")) {
                Block.LOGGER.error("Block classes should end with Block and {} doesn't.", s);
            }
        }

    }

    public static boolean isExceptionForConnection(IBlockData iblockdata) {
        return iblockdata.getBlock() instanceof BlockLeaves || iblockdata.is(Blocks.BARRIER) || iblockdata.is(Blocks.CARVED_PUMPKIN) || iblockdata.is(Blocks.JACK_O_LANTERN) || iblockdata.is(Blocks.MELON) || iblockdata.is(Blocks.PUMPKIN) || iblockdata.is((Tag) TagsBlock.SHULKER_BOXES);
    }

    public boolean isRandomlyTicking(IBlockData iblockdata) {
        return this.isRandomlyTicking;
    }

    public static boolean shouldRenderFace(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection, BlockPosition blockposition1) {
        IBlockData iblockdata1 = iblockaccess.getBlockState(blockposition1);

        if (iblockdata.skipRendering(iblockdata1, enumdirection)) {
            return false;
        } else if (iblockdata1.canOcclude()) {
            Block.a block_a = new Block.a(iblockdata, iblockdata1, enumdirection);
            Object2ByteLinkedOpenHashMap<Block.a> object2bytelinkedopenhashmap = (Object2ByteLinkedOpenHashMap) Block.OCCLUSION_CACHE.get();
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block_a);

            if (b0 != 127) {
                return b0 != 0;
            } else {
                VoxelShape voxelshape = iblockdata.getFaceOcclusionShape(iblockaccess, blockposition, enumdirection);

                if (voxelshape.isEmpty()) {
                    return true;
                } else {
                    VoxelShape voxelshape1 = iblockdata1.getFaceOcclusionShape(iblockaccess, blockposition1, enumdirection.getOpposite());
                    boolean flag = VoxelShapes.joinIsNotEmpty(voxelshape, voxelshape1, OperatorBoolean.ONLY_FIRST);

                    if (object2bytelinkedopenhashmap.size() == 2048) {
                        object2bytelinkedopenhashmap.removeLastByte();
                    }

                    object2bytelinkedopenhashmap.putAndMoveToFirst(block_a, (byte) (flag ? 1 : 0));
                    return flag;
                }
            }
        } else {
            return true;
        }
    }

    public static boolean canSupportRigidBlock(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getBlockState(blockposition).isFaceSturdy(iblockaccess, blockposition, EnumDirection.UP, EnumBlockSupport.RIGID);
    }

    public static boolean canSupportCenter(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iworldreader.getBlockState(blockposition);

        return enumdirection == EnumDirection.DOWN && iblockdata.is((Tag) TagsBlock.UNSTABLE_BOTTOM_CENTER) ? false : iblockdata.isFaceSturdy(iworldreader, blockposition, enumdirection, EnumBlockSupport.CENTER);
    }

    public static boolean isFaceFull(VoxelShape voxelshape, EnumDirection enumdirection) {
        VoxelShape voxelshape1 = voxelshape.getFaceShape(enumdirection);

        return isShapeFullBlock(voxelshape1);
    }

    public static boolean isShapeFullBlock(VoxelShape voxelshape) {
        return (Boolean) Block.SHAPE_FULL_BLOCK_CACHE.getUnchecked(voxelshape);
    }

    public boolean propagatesSkylightDown(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !isShapeFullBlock(iblockdata.getShape(iblockaccess, blockposition)) && iblockdata.getFluidState().isEmpty();
    }

    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {}

    public void destroy(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {}

    public static List<ItemStack> getDrops(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, @Nullable TileEntity tileentity) {
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder(worldserver)).withRandom(worldserver.random).withParameter(LootContextParameters.ORIGIN, Vec3D.atCenterOf(blockposition)).withParameter(LootContextParameters.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParameters.BLOCK_ENTITY, tileentity);

        return iblockdata.getDrops(loottableinfo_builder);
    }

    public static List<ItemStack> getDrops(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, @Nullable TileEntity tileentity, @Nullable Entity entity, ItemStack itemstack) {
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder(worldserver)).withRandom(worldserver.random).withParameter(LootContextParameters.ORIGIN, Vec3D.atCenterOf(blockposition)).withParameter(LootContextParameters.TOOL, itemstack).withOptionalParameter(LootContextParameters.THIS_ENTITY, entity).withOptionalParameter(LootContextParameters.BLOCK_ENTITY, tileentity);

        return iblockdata.getDrops(loottableinfo_builder);
    }

    public static void dropResources(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        WorldServer worldserver = loottableinfo_builder.getLevel();
        BlockPosition blockposition = new BlockPosition((Vec3D) loottableinfo_builder.getParameter(LootContextParameters.ORIGIN));

        iblockdata.getDrops(loottableinfo_builder).forEach((itemstack) -> {
            popResource(worldserver, blockposition, itemstack);
        });
        iblockdata.spawnAfterBreak(worldserver, blockposition, ItemStack.EMPTY);
    }

    public static void dropResources(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (world instanceof WorldServer) {
            getDrops(iblockdata, (WorldServer) world, blockposition, (TileEntity) null).forEach((itemstack) -> {
                popResource(world, blockposition, itemstack);
            });
            iblockdata.spawnAfterBreak((WorldServer) world, blockposition, ItemStack.EMPTY);
        }

    }

    public static void dropResources(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, @Nullable TileEntity tileentity) {
        if (generatoraccess instanceof WorldServer) {
            getDrops(iblockdata, (WorldServer) generatoraccess, blockposition, tileentity).forEach((itemstack) -> {
                popResource((WorldServer) generatoraccess, blockposition, itemstack);
            });
            iblockdata.spawnAfterBreak((WorldServer) generatoraccess, blockposition, ItemStack.EMPTY);
        }

    }

    public static void dropResources(IBlockData iblockdata, World world, BlockPosition blockposition, @Nullable TileEntity tileentity, Entity entity, ItemStack itemstack) {
        if (world instanceof WorldServer) {
            getDrops(iblockdata, (WorldServer) world, blockposition, tileentity, entity, itemstack).forEach((itemstack1) -> {
                popResource(world, blockposition, itemstack1);
            });
            iblockdata.spawnAfterBreak((WorldServer) world, blockposition, itemstack);
        }

    }

    public static void popResource(World world, BlockPosition blockposition, ItemStack itemstack) {
        float f = EntityTypes.ITEM.getHeight() / 2.0F;
        double d0 = (double) ((float) blockposition.getX() + 0.5F) + MathHelper.nextDouble(world.random, -0.25D, 0.25D);
        double d1 = (double) ((float) blockposition.getY() + 0.5F) + MathHelper.nextDouble(world.random, -0.25D, 0.25D) - (double) f;
        double d2 = (double) ((float) blockposition.getZ() + 0.5F) + MathHelper.nextDouble(world.random, -0.25D, 0.25D);

        popResource(world, () -> {
            return new EntityItem(world, d0, d1, d2, itemstack);
        }, itemstack);
    }

    public static void popResourceFromFace(World world, BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack) {
        int i = enumdirection.getStepX();
        int j = enumdirection.getStepY();
        int k = enumdirection.getStepZ();
        float f = EntityTypes.ITEM.getWidth() / 2.0F;
        float f1 = EntityTypes.ITEM.getHeight() / 2.0F;
        double d0 = (double) ((float) blockposition.getX() + 0.5F) + (i == 0 ? MathHelper.nextDouble(world.random, -0.25D, 0.25D) : (double) ((float) i * (0.5F + f)));
        double d1 = (double) ((float) blockposition.getY() + 0.5F) + (j == 0 ? MathHelper.nextDouble(world.random, -0.25D, 0.25D) : (double) ((float) j * (0.5F + f1))) - (double) f1;
        double d2 = (double) ((float) blockposition.getZ() + 0.5F) + (k == 0 ? MathHelper.nextDouble(world.random, -0.25D, 0.25D) : (double) ((float) k * (0.5F + f)));
        double d3 = i == 0 ? MathHelper.nextDouble(world.random, -0.1D, 0.1D) : (double) i * 0.1D;
        double d4 = j == 0 ? MathHelper.nextDouble(world.random, 0.0D, 0.1D) : (double) j * 0.1D + 0.1D;
        double d5 = k == 0 ? MathHelper.nextDouble(world.random, -0.1D, 0.1D) : (double) k * 0.1D;

        popResource(world, () -> {
            return new EntityItem(world, d0, d1, d2, itemstack, d3, d4, d5);
        }, itemstack);
    }

    private static void popResource(World world, Supplier<EntityItem> supplier, ItemStack itemstack) {
        if (!world.isClientSide && !itemstack.isEmpty() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            EntityItem entityitem = (EntityItem) supplier.get();

            entityitem.setDefaultPickUpDelay();
            world.addFreshEntity(entityitem);
        }
    }

    public void popExperience(WorldServer worldserver, BlockPosition blockposition, int i) {
        if (worldserver.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            EntityExperienceOrb.award(worldserver, Vec3D.atCenterOf(blockposition), i);
        }

    }

    public float getExplosionResistance() {
        return this.explosionResistance;
    }

    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {}

    public void stepOn(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {}

    @Nullable
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return this.defaultBlockState();
    }

    public void playerDestroy(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        entityhuman.awardStat(StatisticList.BLOCK_MINED.get(this));
        entityhuman.causeFoodExhaustion(0.005F);
        dropResources(iblockdata, world, blockposition, tileentity, entityhuman, itemstack);
    }

    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {}

    public boolean isPossibleToRespawnInThis() {
        return !this.material.isSolid() && !this.material.isLiquid();
    }

    public IChatMutableComponent getName() {
        return new ChatMessage(this.getDescriptionId());
    }

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = SystemUtils.makeDescriptionId("block", IRegistry.BLOCK.getKey(this));
        }

        return this.descriptionId;
    }

    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        entity.causeFallDamage(f, 1.0F, DamageSource.FALL);
    }

    public void updateEntityAfterFallOn(IBlockAccess iblockaccess, Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
    }

    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this);
    }

    public void fillItemCategory(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        nonnulllist.add(new ItemStack(this));
    }

    public float getFriction() {
        return this.friction;
    }

    public float getSpeedFactor() {
        return this.speedFactor;
    }

    public float getJumpFactor() {
        return this.jumpFactor;
    }

    protected void spawnDestroyParticles(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata) {
        world.levelEvent(entityhuman, 2001, blockposition, getId(iblockdata));
    }

    public void playerWillDestroy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        this.spawnDestroyParticles(world, entityhuman, blockposition, iblockdata);
        if (iblockdata.is((Tag) TagsBlock.GUARDED_BY_PIGLINS)) {
            PiglinAI.angerNearbyPiglins(entityhuman, false);
        }

        world.gameEvent(entityhuman, GameEvent.BLOCK_DESTROY, blockposition);
    }

    public void handlePrecipitation(IBlockData iblockdata, World world, BlockPosition blockposition, BiomeBase.Precipitation biomebase_precipitation) {}

    public boolean dropFromExplosion(Explosion explosion) {
        return true;
    }

    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {}

    public BlockStateList<Block, IBlockData> getStateDefinition() {
        return this.stateDefinition;
    }

    protected final void registerDefaultState(IBlockData iblockdata) {
        this.defaultBlockState = iblockdata;
    }

    public final IBlockData defaultBlockState() {
        return this.defaultBlockState;
    }

    public final IBlockData withPropertiesOf(IBlockData iblockdata) {
        IBlockData iblockdata1 = this.defaultBlockState();
        Iterator iterator = iblockdata.getBlock().getStateDefinition().getProperties().iterator();

        while (iterator.hasNext()) {
            IBlockState<?> iblockstate = (IBlockState) iterator.next();

            if (iblockdata1.hasProperty(iblockstate)) {
                iblockdata1 = copyProperty(iblockdata, iblockdata1, iblockstate);
            }
        }

        return iblockdata1;
    }

    private static <T extends Comparable<T>> IBlockData copyProperty(IBlockData iblockdata, IBlockData iblockdata1, IBlockState<T> iblockstate) {
        return (IBlockData) iblockdata1.setValue(iblockstate, iblockdata.getValue(iblockstate));
    }

    public SoundEffectType getSoundType(IBlockData iblockdata) {
        return this.soundType;
    }

    @Override
    public Item asItem() {
        if (this.item == null) {
            this.item = Item.byBlock(this);
        }

        return this.item;
    }

    public boolean hasDynamicShape() {
        return this.dynamicShape;
    }

    public String toString() {
        return "Block{" + IRegistry.BLOCK.getKey(this) + "}";
    }

    public void appendHoverText(ItemStack itemstack, @Nullable IBlockAccess iblockaccess, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {}

    @Override
    protected Block asBlock() {
        return this;
    }

    protected ImmutableMap<IBlockData, VoxelShape> getShapeForEachState(Function<IBlockData, VoxelShape> function) {
        return (ImmutableMap) this.stateDefinition.getPossibleStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), function));
    }

    public static final class a {

        private final IBlockData first;
        private final IBlockData second;
        private final EnumDirection direction;

        public a(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
            this.first = iblockdata;
            this.second = iblockdata1;
            this.direction = enumdirection;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof Block.a)) {
                return false;
            } else {
                Block.a block_a = (Block.a) object;

                return this.first == block_a.first && this.second == block_a.second && this.direction == block_a.direction;
            }
        }

        public int hashCode() {
            int i = this.first.hashCode();

            i = 31 * i + this.second.hashCode();
            i = 31 * i + this.direction.hashCode();
            return i;
        }
    }
}
