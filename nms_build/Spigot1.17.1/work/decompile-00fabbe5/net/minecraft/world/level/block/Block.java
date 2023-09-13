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
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
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
            return !VoxelShapes.c(VoxelShapes.b(), voxelshape, OperatorBoolean.NOT_SAME);
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

    public static int getCombinedId(@Nullable IBlockData iblockdata) {
        if (iblockdata == null) {
            return 0;
        } else {
            int i = Block.BLOCK_STATE_REGISTRY.getId(iblockdata);

            return i == -1 ? 0 : i;
        }
    }

    public static IBlockData getByCombinedId(int i) {
        IBlockData iblockdata = (IBlockData) Block.BLOCK_STATE_REGISTRY.fromId(i);

        return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
    }

    public static Block asBlock(@Nullable Item item) {
        return item instanceof ItemBlock ? ((ItemBlock) item).getBlock() : Blocks.AIR;
    }

    public static IBlockData a(IBlockData iblockdata, IBlockData iblockdata1, World world, BlockPosition blockposition) {
        VoxelShape voxelshape = VoxelShapes.b(iblockdata.getCollisionShape(world, blockposition), iblockdata1.getCollisionShape(world, blockposition), OperatorBoolean.ONLY_SECOND).a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());

        if (voxelshape.isEmpty()) {
            return iblockdata1;
        } else {
            List<Entity> list = world.getEntities((Entity) null, voxelshape.getBoundingBox());
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();
                double d0 = VoxelShapes.a(EnumDirection.EnumAxis.Y, entity.getBoundingBox().d(0.0D, 1.0D, 0.0D), Stream.of(voxelshape), -1.0D);

                entity.enderTeleportTo(entity.locX(), entity.locY() + 1.0D + d0, entity.locZ());
            }

            return iblockdata1;
        }
    }

    public static VoxelShape a(double d0, double d1, double d2, double d3, double d4, double d5) {
        return VoxelShapes.create(d0 / 16.0D, d1 / 16.0D, d2 / 16.0D, d3 / 16.0D, d4 / 16.0D, d5 / 16.0D);
    }

    public static IBlockData b(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata1 = iblockdata;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection[] aenumdirection = Block.UPDATE_SHAPE_ORDER;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
            iblockdata1 = iblockdata1.updateState(enumdirection, generatoraccess.getType(blockposition_mutableblockposition), generatoraccess, blockposition, blockposition_mutableblockposition);
        }

        return iblockdata1;
    }

    public static void a(IBlockData iblockdata, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        a(iblockdata, iblockdata1, generatoraccess, blockposition, i, 512);
    }

    public static void a(IBlockData iblockdata, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {
        if (iblockdata1 != iblockdata) {
            if (iblockdata1.isAir()) {
                if (!generatoraccess.isClientSide()) {
                    generatoraccess.a(blockposition, (i & 32) == 0, (Entity) null, j);
                }
            } else {
                generatoraccess.a(blockposition, iblockdata1, i & -33, j);
            }
        }

    }

    public Block(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        BlockStateList.a<Block, IBlockData> blockstatelist_a = new BlockStateList.a<>(this);

        this.a(blockstatelist_a);
        this.stateDefinition = blockstatelist_a.a(Block::getBlockData, IBlockData::new);
        this.k((IBlockData) this.stateDefinition.getBlockData());
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            String s = this.getClass().getSimpleName();

            if (!s.endsWith("Block")) {
                Block.LOGGER.error("Block classes should end with Block and {} doesn't.", s);
            }
        }

    }

    public static boolean j(IBlockData iblockdata) {
        return iblockdata.getBlock() instanceof BlockLeaves || iblockdata.a(Blocks.BARRIER) || iblockdata.a(Blocks.CARVED_PUMPKIN) || iblockdata.a(Blocks.JACK_O_LANTERN) || iblockdata.a(Blocks.MELON) || iblockdata.a(Blocks.PUMPKIN) || iblockdata.a((Tag) TagsBlock.SHULKER_BOXES);
    }

    public boolean isTicking(IBlockData iblockdata) {
        return this.isRandomlyTicking;
    }

    public static boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection, BlockPosition blockposition1) {
        IBlockData iblockdata1 = iblockaccess.getType(blockposition1);

        if (iblockdata.a(iblockdata1, enumdirection)) {
            return false;
        } else if (iblockdata1.l()) {
            Block.a block_a = new Block.a(iblockdata, iblockdata1, enumdirection);
            Object2ByteLinkedOpenHashMap<Block.a> object2bytelinkedopenhashmap = (Object2ByteLinkedOpenHashMap) Block.OCCLUSION_CACHE.get();
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block_a);

            if (b0 != 127) {
                return b0 != 0;
            } else {
                VoxelShape voxelshape = iblockdata.a(iblockaccess, blockposition, enumdirection);

                if (voxelshape.isEmpty()) {
                    return true;
                } else {
                    VoxelShape voxelshape1 = iblockdata1.a(iblockaccess, blockposition1, enumdirection.opposite());
                    boolean flag = VoxelShapes.c(voxelshape, voxelshape1, OperatorBoolean.ONLY_FIRST);

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

    public static boolean c(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getType(blockposition).a(iblockaccess, blockposition, EnumDirection.UP, EnumBlockSupport.RIGID);
    }

    public static boolean a(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iworldreader.getType(blockposition);

        return enumdirection == EnumDirection.DOWN && iblockdata.a((Tag) TagsBlock.UNSTABLE_BOTTOM_CENTER) ? false : iblockdata.a((IBlockAccess) iworldreader, blockposition, enumdirection, EnumBlockSupport.CENTER);
    }

    public static boolean a(VoxelShape voxelshape, EnumDirection enumdirection) {
        VoxelShape voxelshape1 = voxelshape.a(enumdirection);

        return a(voxelshape1);
    }

    public static boolean a(VoxelShape voxelshape) {
        return (Boolean) Block.SHAPE_FULL_BLOCK_CACHE.getUnchecked(voxelshape);
    }

    public boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !a(iblockdata.getShape(iblockaccess, blockposition)) && iblockdata.getFluid().isEmpty();
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {}

    public void postBreak(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {}

    public static List<ItemStack> a(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, @Nullable TileEntity tileentity) {
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder(worldserver)).a(worldserver.random).set(LootContextParameters.ORIGIN, Vec3D.a((BaseBlockPosition) blockposition)).set(LootContextParameters.TOOL, ItemStack.EMPTY).setOptional(LootContextParameters.BLOCK_ENTITY, tileentity);

        return iblockdata.a(loottableinfo_builder);
    }

    public static List<ItemStack> getDrops(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, @Nullable TileEntity tileentity, @Nullable Entity entity, ItemStack itemstack) {
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder(worldserver)).a(worldserver.random).set(LootContextParameters.ORIGIN, Vec3D.a((BaseBlockPosition) blockposition)).set(LootContextParameters.TOOL, itemstack).setOptional(LootContextParameters.THIS_ENTITY, entity).setOptional(LootContextParameters.BLOCK_ENTITY, tileentity);

        return iblockdata.a(loottableinfo_builder);
    }

    public static void b(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        WorldServer worldserver = loottableinfo_builder.a();
        BlockPosition blockposition = new BlockPosition((Vec3D) loottableinfo_builder.a(LootContextParameters.ORIGIN));

        iblockdata.a(loottableinfo_builder).forEach((itemstack) -> {
            a((World) worldserver, blockposition, itemstack);
        });
        iblockdata.dropNaturally(worldserver, blockposition, ItemStack.EMPTY);
    }

    public static void c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (world instanceof WorldServer) {
            a(iblockdata, (WorldServer) world, blockposition, (TileEntity) null).forEach((itemstack) -> {
                a(world, blockposition, itemstack);
            });
            iblockdata.dropNaturally((WorldServer) world, blockposition, ItemStack.EMPTY);
        }

    }

    public static void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, @Nullable TileEntity tileentity) {
        if (generatoraccess instanceof WorldServer) {
            a(iblockdata, (WorldServer) generatoraccess, blockposition, tileentity).forEach((itemstack) -> {
                a((World) ((WorldServer) generatoraccess), blockposition, itemstack);
            });
            iblockdata.dropNaturally((WorldServer) generatoraccess, blockposition, ItemStack.EMPTY);
        }

    }

    public static void dropItems(IBlockData iblockdata, World world, BlockPosition blockposition, @Nullable TileEntity tileentity, Entity entity, ItemStack itemstack) {
        if (world instanceof WorldServer) {
            getDrops(iblockdata, (WorldServer) world, blockposition, tileentity, entity, itemstack).forEach((itemstack1) -> {
                a(world, blockposition, itemstack1);
            });
            iblockdata.dropNaturally((WorldServer) world, blockposition, itemstack);
        }

    }

    public static void a(World world, BlockPosition blockposition, ItemStack itemstack) {
        float f = EntityTypes.ITEM.l() / 2.0F;
        double d0 = (double) ((float) blockposition.getX() + 0.5F) + MathHelper.a(world.random, -0.25D, 0.25D);
        double d1 = (double) ((float) blockposition.getY() + 0.5F) + MathHelper.a(world.random, -0.25D, 0.25D) - (double) f;
        double d2 = (double) ((float) blockposition.getZ() + 0.5F) + MathHelper.a(world.random, -0.25D, 0.25D);

        a(world, () -> {
            return new EntityItem(world, d0, d1, d2, itemstack);
        }, itemstack);
    }

    public static void a(World world, BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack) {
        int i = enumdirection.getAdjacentX();
        int j = enumdirection.getAdjacentY();
        int k = enumdirection.getAdjacentZ();
        float f = EntityTypes.ITEM.k() / 2.0F;
        float f1 = EntityTypes.ITEM.l() / 2.0F;
        double d0 = (double) ((float) blockposition.getX() + 0.5F) + (i == 0 ? MathHelper.a(world.random, -0.25D, 0.25D) : (double) ((float) i * (0.5F + f)));
        double d1 = (double) ((float) blockposition.getY() + 0.5F) + (j == 0 ? MathHelper.a(world.random, -0.25D, 0.25D) : (double) ((float) j * (0.5F + f1))) - (double) f1;
        double d2 = (double) ((float) blockposition.getZ() + 0.5F) + (k == 0 ? MathHelper.a(world.random, -0.25D, 0.25D) : (double) ((float) k * (0.5F + f)));
        double d3 = i == 0 ? MathHelper.a(world.random, -0.1D, 0.1D) : (double) i * 0.1D;
        double d4 = j == 0 ? MathHelper.a(world.random, 0.0D, 0.1D) : (double) j * 0.1D + 0.1D;
        double d5 = k == 0 ? MathHelper.a(world.random, -0.1D, 0.1D) : (double) k * 0.1D;

        a(world, () -> {
            return new EntityItem(world, d0, d1, d2, itemstack, d3, d4, d5);
        }, itemstack);
    }

    private static void a(World world, Supplier<EntityItem> supplier, ItemStack itemstack) {
        if (!world.isClientSide && !itemstack.isEmpty() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            EntityItem entityitem = (EntityItem) supplier.get();

            entityitem.defaultPickupDelay();
            world.addEntity(entityitem);
        }
    }

    public void dropExperience(WorldServer worldserver, BlockPosition blockposition, int i) {
        if (worldserver.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            EntityExperienceOrb.a(worldserver, Vec3D.a((BaseBlockPosition) blockposition), i);
        }

    }

    public float getDurability() {
        return this.explosionResistance;
    }

    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {}

    public void stepOn(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {}

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return this.getBlockData();
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        entityhuman.b(StatisticList.BLOCK_MINED.b(this));
        entityhuman.applyExhaustion(0.005F);
        dropItems(iblockdata, world, blockposition, tileentity, entityhuman, itemstack);
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {}

    public boolean W_() {
        return !this.material.isBuildable() && !this.material.isLiquid();
    }

    public IChatMutableComponent g() {
        return new ChatMessage(this.h());
    }

    public String h() {
        if (this.descriptionId == null) {
            this.descriptionId = SystemUtils.a("block", IRegistry.BLOCK.getKey(this));
        }

        return this.descriptionId;
    }

    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        entity.a(f, 1.0F, DamageSource.FALL);
    }

    public void a(IBlockAccess iblockaccess, Entity entity) {
        entity.setMot(entity.getMot().d(1.0D, 0.0D, 1.0D));
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this);
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        nonnulllist.add(new ItemStack(this));
    }

    public float getFrictionFactor() {
        return this.friction;
    }

    public float getSpeedFactor() {
        return this.speedFactor;
    }

    public float getJumpFactor() {
        return this.jumpFactor;
    }

    protected void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata) {
        world.a(entityhuman, 2001, blockposition, getCombinedId(iblockdata));
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        this.a(world, entityhuman, blockposition, iblockdata);
        if (iblockdata.a((Tag) TagsBlock.GUARDED_BY_PIGLINS)) {
            PiglinAI.a(entityhuman, false);
        }

        world.a((Entity) entityhuman, GameEvent.BLOCK_DESTROY, blockposition);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, BiomeBase.Precipitation biomebase_precipitation) {}

    public boolean a(Explosion explosion) {
        return true;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {}

    public BlockStateList<Block, IBlockData> getStates() {
        return this.stateDefinition;
    }

    protected final void k(IBlockData iblockdata) {
        this.defaultBlockState = iblockdata;
    }

    public final IBlockData getBlockData() {
        return this.defaultBlockState;
    }

    public final IBlockData l(IBlockData iblockdata) {
        IBlockData iblockdata1 = this.getBlockData();
        Iterator iterator = iblockdata.getBlock().getStates().d().iterator();

        while (iterator.hasNext()) {
            IBlockState<?> iblockstate = (IBlockState) iterator.next();

            if (iblockdata1.b(iblockstate)) {
                iblockdata1 = a(iblockdata, iblockdata1, iblockstate);
            }
        }

        return iblockdata1;
    }

    private static <T extends Comparable<T>> IBlockData a(IBlockData iblockdata, IBlockData iblockdata1, IBlockState<T> iblockstate) {
        return (IBlockData) iblockdata1.set(iblockstate, iblockdata.get(iblockstate));
    }

    public SoundEffectType getStepSound(IBlockData iblockdata) {
        return this.soundType;
    }

    @Override
    public Item getItem() {
        if (this.item == null) {
            this.item = Item.getItemOf(this);
        }

        return this.item;
    }

    public boolean o() {
        return this.dynamicShape;
    }

    public String toString() {
        return "Block{" + IRegistry.BLOCK.getKey(this) + "}";
    }

    public void a(ItemStack itemstack, @Nullable IBlockAccess iblockaccess, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {}

    @Override
    protected Block p() {
        return this;
    }

    protected ImmutableMap<IBlockData, VoxelShape> a(Function<IBlockData, VoxelShape> function) {
        return (ImmutableMap) this.stateDefinition.a().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), function));
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
