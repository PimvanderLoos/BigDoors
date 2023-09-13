package net.minecraft.gametest.framework;

import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockButtonAbstract;
import net.minecraft.world.level.block.BlockLever;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityContainer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class GameTestHarnessHelper {

    private final GameTestHarnessInfo testInfo;
    private boolean finalCheckAdded;

    public GameTestHarnessHelper(GameTestHarnessInfo gametestharnessinfo) {
        this.testInfo = gametestharnessinfo;
    }

    public WorldServer a() {
        return this.testInfo.g();
    }

    public IBlockData a(BlockPosition blockposition) {
        return this.a().getType(this.i(blockposition));
    }

    @Nullable
    public TileEntity b(BlockPosition blockposition) {
        return this.a().getTileEntity(this.i(blockposition));
    }

    public void b() {
        AxisAlignedBB axisalignedbb = this.i();
        List<Entity> list = this.a().a(Entity.class, axisalignedbb.g(1.0D), (entity) -> {
            return !(entity instanceof EntityHuman);
        });

        list.forEach(Entity::killEntity);
    }

    public EntityItem a(Item item, float f, float f1, float f2) {
        WorldServer worldserver = this.a();
        Vec3D vec3d = this.a(new Vec3D((double) f, (double) f1, (double) f2));
        EntityItem entityitem = new EntityItem(worldserver, vec3d.x, vec3d.y, vec3d.z, new ItemStack(item, 1));

        entityitem.setMot(0.0D, 0.0D, 0.0D);
        worldserver.addEntity(entityitem);
        return entityitem;
    }

    public <E extends Entity> E a(EntityTypes<E> entitytypes, BlockPosition blockposition) {
        return this.a(entitytypes, Vec3D.c((BaseBlockPosition) blockposition));
    }

    public <E extends Entity> E a(EntityTypes<E> entitytypes, Vec3D vec3d) {
        WorldServer worldserver = this.a();
        E e0 = entitytypes.a((World) worldserver);

        if (e0 instanceof EntityInsentient) {
            ((EntityInsentient) e0).setPersistent();
        }

        Vec3D vec3d1 = this.a(vec3d);

        e0.setPositionRotation(vec3d1.x, vec3d1.y, vec3d1.z, e0.getYRot(), e0.getXRot());
        worldserver.addEntity(e0);
        return e0;
    }

    public <E extends Entity> E a(EntityTypes<E> entitytypes, int i, int j, int k) {
        return this.a(entitytypes, new BlockPosition(i, j, k));
    }

    public <E extends Entity> E a(EntityTypes<E> entitytypes, float f, float f1, float f2) {
        return this.a(entitytypes, new Vec3D((double) f, (double) f1, (double) f2));
    }

    public <E extends EntityInsentient> E b(EntityTypes<E> entitytypes, BlockPosition blockposition) {
        E e0 = (EntityInsentient) this.a(entitytypes, blockposition);

        e0.ft();
        return e0;
    }

    public <E extends EntityInsentient> E b(EntityTypes<E> entitytypes, int i, int j, int k) {
        return this.b(entitytypes, new BlockPosition(i, j, k));
    }

    public <E extends EntityInsentient> E b(EntityTypes<E> entitytypes, Vec3D vec3d) {
        E e0 = (EntityInsentient) this.a(entitytypes, vec3d);

        e0.ft();
        return e0;
    }

    public <E extends EntityInsentient> E b(EntityTypes<E> entitytypes, float f, float f1, float f2) {
        return this.b(entitytypes, new Vec3D((double) f, (double) f1, (double) f2));
    }

    public GameTestHarnessSequence a(EntityInsentient entityinsentient, BlockPosition blockposition, float f) {
        return this.f().a(2, () -> {
            PathEntity pathentity = entityinsentient.getNavigation().a(this.i(blockposition), 0);

            entityinsentient.getNavigation().a(pathentity, (double) f);
        });
    }

    public void a(int i, int j, int k) {
        this.c(new BlockPosition(i, j, k));
    }

    public void c(BlockPosition blockposition) {
        this.b(blockposition, (iblockdata) -> {
            return iblockdata.a((Tag) TagsBlock.BUTTONS);
        }, () -> {
            return "Expected button";
        });
        BlockPosition blockposition1 = this.i(blockposition);
        IBlockData iblockdata = this.a().getType(blockposition1);
        BlockButtonAbstract blockbuttonabstract = (BlockButtonAbstract) iblockdata.getBlock();

        blockbuttonabstract.d(iblockdata, this.a(), blockposition1);
    }

    public void d(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.i(blockposition);
        IBlockData iblockdata = this.a().getType(blockposition1);

        iblockdata.interact(this.a(), this.c(), EnumHand.MAIN_HAND, new MovingObjectPositionBlock(Vec3D.a((BaseBlockPosition) blockposition1), EnumDirection.NORTH, blockposition1, true));
    }

    public EntityLiving a(EntityLiving entityliving) {
        entityliving.setAirTicks(0);
        entityliving.setHealth(0.25F);
        return entityliving;
    }

    public EntityHuman c() {
        return new EntityHuman(this.a(), BlockPosition.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }
        };
    }

    public void b(int i, int j, int k) {
        this.e(new BlockPosition(i, j, k));
    }

    public void e(BlockPosition blockposition) {
        this.a(Blocks.LEVER, blockposition);
        BlockPosition blockposition1 = this.i(blockposition);
        IBlockData iblockdata = this.a().getType(blockposition1);
        BlockLever blocklever = (BlockLever) iblockdata.getBlock();

        blocklever.d(iblockdata, this.a(), blockposition1);
    }

    public void a(BlockPosition blockposition, long i) {
        this.a(blockposition, Blocks.REDSTONE_BLOCK);
        this.b(i, () -> {
            this.a(blockposition, Blocks.AIR);
        });
    }

    public void f(BlockPosition blockposition) {
        this.a().a(this.i(blockposition), false, (Entity) null);
    }

    public void a(int i, int j, int k, Block block) {
        this.a(new BlockPosition(i, j, k), block);
    }

    public void a(int i, int j, int k, IBlockData iblockdata) {
        this.a(new BlockPosition(i, j, k), iblockdata);
    }

    public void a(BlockPosition blockposition, Block block) {
        this.a(blockposition, block.getBlockData());
    }

    public void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a().setTypeAndData(this.i(blockposition), iblockdata, 3);
    }

    public void d() {
        this.a(13000);
    }

    public void a(int i) {
        this.a().setDayTime((long) i);
    }

    public void a(Block block, int i, int j, int k) {
        this.a(block, new BlockPosition(i, j, k));
    }

    public void a(Block block, BlockPosition blockposition) {
        IBlockData iblockdata = this.a(blockposition);
        Predicate predicate = (block1) -> {
            return iblockdata.a(block);
        };
        String s = block.g().getString();

        this.a(blockposition, predicate, "Expected " + s + ", got " + iblockdata.getBlock().g().getString());
    }

    public void b(Block block, int i, int j, int k) {
        this.b(block, new BlockPosition(i, j, k));
    }

    public void b(Block block, BlockPosition blockposition) {
        this.a(blockposition, (block1) -> {
            return !this.a(blockposition).a(block);
        }, "Did not expect " + block.g().getString());
    }

    public void c(Block block, int i, int j, int k) {
        this.c(block, new BlockPosition(i, j, k));
    }

    public void c(Block block, BlockPosition blockposition) {
        this.b(() -> {
            this.a(block, blockposition);
        });
    }

    public void a(BlockPosition blockposition, Predicate<Block> predicate, String s) {
        this.a(blockposition, predicate, () -> {
            return s;
        });
    }

    public void a(BlockPosition blockposition, Predicate<Block> predicate, Supplier<String> supplier) {
        this.b(blockposition, (iblockdata) -> {
            return predicate.test(iblockdata.getBlock());
        }, supplier);
    }

    public <T extends Comparable<T>> void a(BlockPosition blockposition, IBlockState<T> iblockstate, T t0) {
        this.b(blockposition, (iblockdata) -> {
            return iblockdata.b(iblockstate) && iblockdata.get(iblockstate).equals(t0);
        }, () -> {
            String s = iblockstate.getName();

            return "Expected property " + s + " to be " + t0;
        });
    }

    public <T extends Comparable<T>> void a(BlockPosition blockposition, IBlockState<T> iblockstate, Predicate<T> predicate, String s) {
        this.b(blockposition, (iblockdata) -> {
            return predicate.test(iblockdata.get(iblockstate));
        }, () -> {
            return s;
        });
    }

    public void b(BlockPosition blockposition, Predicate<IBlockData> predicate, Supplier<String> supplier) {
        IBlockData iblockdata = this.a(blockposition);

        if (!predicate.test(iblockdata)) {
            throw new GameTestHarnessAssertionPosition((String) supplier.get(), this.i(blockposition), blockposition, this.testInfo.p());
        }
    }

    public void a(EntityTypes<?> entitytypes) {
        List<? extends Entity> list = this.a().a((EntityTypeTest) entitytypes, this.i(), Entity::isAlive);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertion("Expected " + entitytypes.i() + " to exist");
        }
    }

    public void c(EntityTypes<?> entitytypes, int i, int j, int k) {
        this.c(entitytypes, new BlockPosition(i, j, k));
    }

    public void c(EntityTypes<?> entitytypes, BlockPosition blockposition) {
        BlockPosition blockposition1 = this.i(blockposition);
        List<? extends Entity> list = this.a().a((EntityTypeTest) entitytypes, new AxisAlignedBB(blockposition1), Entity::isAlive);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertionPosition("Expected " + entitytypes.i(), blockposition1, blockposition, this.testInfo.p());
        }
    }

    public void a(EntityTypes<?> entitytypes, BlockPosition blockposition, double d0) {
        BlockPosition blockposition1 = this.i(blockposition);
        List<? extends Entity> list = this.a().a((EntityTypeTest) entitytypes, (new AxisAlignedBB(blockposition1)).g(d0), Entity::isAlive);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertionPosition("Expected " + entitytypes.i(), blockposition1, blockposition, this.testInfo.p());
        }
    }

    public void a(Entity entity, int i, int j, int k) {
        this.a(entity, new BlockPosition(i, j, k));
    }

    public void a(Entity entity, BlockPosition blockposition) {
        BlockPosition blockposition1 = this.i(blockposition);
        List<? extends Entity> list = this.a().a((EntityTypeTest) entity.getEntityType(), new AxisAlignedBB(blockposition1), Entity::isAlive);

        list.stream().filter((entity1) -> {
            return entity1 == entity;
        }).findFirst().orElseThrow(() -> {
            return new GameTestHarnessAssertionPosition("Expected " + entity.getEntityType().i(), blockposition1, blockposition, this.testInfo.p());
        });
    }

    public void a(Item item, BlockPosition blockposition, double d0, int i) {
        BlockPosition blockposition1 = this.i(blockposition);
        List<EntityItem> list = this.a().a((EntityTypeTest) EntityTypes.ITEM, (new AxisAlignedBB(blockposition1)).g(d0), Entity::isAlive);
        int j = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            EntityItem entityitem = (EntityItem) entity;

            if (entityitem.getItemStack().getItem().equals(item)) {
                j += entityitem.getItemStack().getCount();
            }
        }

        if (j != i) {
            throw new GameTestHarnessAssertionPosition("Expected " + i + " " + item.o().getString() + " items to exist (found " + j + ")", blockposition1, blockposition, this.testInfo.p());
        }
    }

    public void a(Item item, BlockPosition blockposition, double d0) {
        BlockPosition blockposition1 = this.i(blockposition);
        List<? extends Entity> list = this.a().a((EntityTypeTest) EntityTypes.ITEM, (new AxisAlignedBB(blockposition1)).g(d0), Entity::isAlive);
        Iterator iterator = list.iterator();

        EntityItem entityitem;

        do {
            if (!iterator.hasNext()) {
                throw new GameTestHarnessAssertionPosition("Expected " + item.o().getString() + " item", blockposition1, blockposition, this.testInfo.p());
            }

            Entity entity = (Entity) iterator.next();

            entityitem = (EntityItem) entity;
        } while (!entityitem.getItemStack().getItem().equals(item));

    }

    public void b(EntityTypes<?> entitytypes) {
        List<? extends Entity> list = this.a().a((EntityTypeTest) entitytypes, this.i(), Entity::isAlive);

        if (!list.isEmpty()) {
            throw new GameTestHarnessAssertion("Did not expect " + entitytypes.i() + " to exist");
        }
    }

    public void d(EntityTypes<?> entitytypes, int i, int j, int k) {
        this.d(entitytypes, new BlockPosition(i, j, k));
    }

    public void d(EntityTypes<?> entitytypes, BlockPosition blockposition) {
        BlockPosition blockposition1 = this.i(blockposition);
        List<? extends Entity> list = this.a().a((EntityTypeTest) entitytypes, new AxisAlignedBB(blockposition1), Entity::isAlive);

        if (!list.isEmpty()) {
            throw new GameTestHarnessAssertionPosition("Did not expect " + entitytypes.i(), blockposition1, blockposition, this.testInfo.p());
        }
    }

    public void a(EntityTypes<?> entitytypes, double d0, double d1, double d2) {
        Vec3D vec3d = new Vec3D(d0, d1, d2);
        Vec3D vec3d1 = this.a(vec3d);
        Predicate<? super Entity> predicate = (entity) -> {
            return entity.getBoundingBox().a(vec3d1, vec3d1);
        };
        List<? extends Entity> list = this.a().a((EntityTypeTest) entitytypes, this.i(), predicate);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertion("Expected " + entitytypes.i() + " to touch " + vec3d1 + " (relative " + vec3d + ")");
        }
    }

    public void b(EntityTypes<?> entitytypes, double d0, double d1, double d2) {
        Vec3D vec3d = new Vec3D(d0, d1, d2);
        Vec3D vec3d1 = this.a(vec3d);
        Predicate<? super Entity> predicate = (entity) -> {
            return !entity.getBoundingBox().a(vec3d1, vec3d1);
        };
        List<? extends Entity> list = this.a().a((EntityTypeTest) entitytypes, this.i(), predicate);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertion("Did not expect " + entitytypes.i() + " to touch " + vec3d1 + " (relative " + vec3d + ")");
        }
    }

    public <E extends Entity, T> void a(BlockPosition blockposition, EntityTypes<E> entitytypes, Function<? super E, T> function, @Nullable T t0) {
        BlockPosition blockposition1 = this.i(blockposition);
        List<E> list = this.a().a((EntityTypeTest) entitytypes, new AxisAlignedBB(blockposition1), Entity::isAlive);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertionPosition("Expected " + entitytypes.i(), blockposition1, blockposition, this.testInfo.p());
        } else {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                E e0 = (Entity) iterator.next();
                T t1 = function.apply(e0);

                if (t1 == null) {
                    if (t0 != null) {
                        throw new GameTestHarnessAssertion("Expected entity data to be: " + t0 + ", but was: " + t1);
                    }
                } else if (!t1.equals(t0)) {
                    throw new GameTestHarnessAssertion("Expected entity data to be: " + t0 + ", but was: " + t1);
                }
            }

        }
    }

    public void g(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.i(blockposition);
        TileEntity tileentity = this.a().getTileEntity(blockposition1);

        if (tileentity instanceof TileEntityContainer && !((TileEntityContainer) tileentity).isEmpty()) {
            throw new GameTestHarnessAssertion("Container should be empty");
        }
    }

    public void a(BlockPosition blockposition, Item item) {
        BlockPosition blockposition1 = this.i(blockposition);
        TileEntity tileentity = this.a().getTileEntity(blockposition1);

        if (tileentity instanceof TileEntityContainer && ((TileEntityContainer) tileentity).a(item) != 1) {
            throw new GameTestHarnessAssertion("Container should contain: " + item);
        }
    }

    public void a(StructureBoundingBox structureboundingbox, BlockPosition blockposition) {
        BlockPosition.a(structureboundingbox).forEach((blockposition1) -> {
            BlockPosition blockposition2 = blockposition.c(blockposition1.getX() - structureboundingbox.g(), blockposition1.getY() - structureboundingbox.h(), blockposition1.getZ() - structureboundingbox.i());

            this.a(blockposition1, blockposition2);
        });
    }

    public void a(BlockPosition blockposition, BlockPosition blockposition1) {
        IBlockData iblockdata = this.a(blockposition);
        IBlockData iblockdata1 = this.a(blockposition1);

        if (iblockdata != iblockdata1) {
            this.a("Incorrect state. Expected " + iblockdata1 + ", got " + iblockdata, blockposition);
        }

    }

    public void a(long i, BlockPosition blockposition, Item item) {
        this.a(i, () -> {
            this.a(blockposition, item);
        });
    }

    public void a(long i, BlockPosition blockposition) {
        this.a(i, () -> {
            this.g(blockposition);
        });
    }

    public <E extends Entity, T> void b(BlockPosition blockposition, EntityTypes<E> entitytypes, Function<E, T> function, T t0) {
        this.b(() -> {
            this.a(blockposition, entitytypes, function, t0);
        });
    }

    public <E extends Entity> void a(E e0, Predicate<E> predicate, String s) {
        if (!predicate.test(e0)) {
            throw new GameTestHarnessAssertion("Entity " + e0 + " failed " + s + " test");
        }
    }

    public <E extends Entity, T> void a(E e0, Function<E, T> function, String s, T t0) {
        T t1 = function.apply(e0);

        if (!t1.equals(t0)) {
            throw new GameTestHarnessAssertion("Entity " + e0 + " value " + s + "=" + t1 + " is not equal to expected " + t0);
        }
    }

    public void e(EntityTypes<?> entitytypes, int i, int j, int k) {
        this.e(entitytypes, new BlockPosition(i, j, k));
    }

    public void e(EntityTypes<?> entitytypes, BlockPosition blockposition) {
        this.b(() -> {
            this.c(entitytypes, blockposition);
        });
    }

    public void f(EntityTypes<?> entitytypes, int i, int j, int k) {
        this.f(entitytypes, new BlockPosition(i, j, k));
    }

    public void f(EntityTypes<?> entitytypes, BlockPosition blockposition) {
        this.b(() -> {
            this.d(entitytypes, blockposition);
        });
    }

    public void e() {
        this.testInfo.m();
    }

    private void h() {
        if (this.finalCheckAdded) {
            throw new IllegalStateException("This test already has final clause");
        } else {
            this.finalCheckAdded = true;
        }
    }

    public void a(Runnable runnable) {
        this.h();
        this.testInfo.q().a(0L, runnable).a();
    }

    public void b(Runnable runnable) {
        this.h();
        this.testInfo.q().a(runnable).a();
    }

    public void a(int i, Runnable runnable) {
        this.h();
        this.testInfo.q().a((long) i, runnable).a();
    }

    public void a(long i, Runnable runnable) {
        this.testInfo.a(i, runnable);
    }

    public void b(long i, Runnable runnable) {
        this.a(this.testInfo.p() + i, runnable);
    }

    public void h(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.i(blockposition);
        WorldServer worldserver = this.a();

        worldserver.getType(blockposition1).b(worldserver, blockposition1, worldserver.random);
    }

    public void a(String s, BlockPosition blockposition) {
        throw new GameTestHarnessAssertionPosition(s, this.i(blockposition), blockposition, this.g());
    }

    public void a(String s, Entity entity) {
        throw new GameTestHarnessAssertionPosition(s, entity.getChunkCoordinates(), this.j(entity.getChunkCoordinates()), this.g());
    }

    public void a(String s) {
        throw new GameTestHarnessAssertion(s);
    }

    public void c(Runnable runnable) {
        this.testInfo.q().a(runnable).a(() -> {
            return new GameTestHarnessAssertion("Fail conditions met");
        });
    }

    public void d(Runnable runnable) {
        LongStream.range(this.testInfo.p(), (long) this.testInfo.w()).forEach((i) -> {
            GameTestHarnessInfo gametestharnessinfo = this.testInfo;

            Objects.requireNonNull(runnable);
            gametestharnessinfo.a(i, runnable::run);
        });
    }

    public GameTestHarnessSequence f() {
        return this.testInfo.q();
    }

    public BlockPosition i(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.testInfo.d();
        BlockPosition blockposition2 = blockposition1.f(blockposition);

        return DefinedStructure.a(blockposition2, EnumBlockMirror.NONE, this.testInfo.u(), blockposition1);
    }

    public BlockPosition j(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.testInfo.d();
        EnumBlockRotation enumblockrotation = this.testInfo.u().a(EnumBlockRotation.CLOCKWISE_180);
        BlockPosition blockposition2 = DefinedStructure.a(blockposition, EnumBlockMirror.NONE, enumblockrotation, blockposition1);

        return blockposition2.e(blockposition1);
    }

    public Vec3D a(Vec3D vec3d) {
        Vec3D vec3d1 = Vec3D.b((BaseBlockPosition) this.testInfo.d());

        return DefinedStructure.a(vec3d1.e(vec3d), EnumBlockMirror.NONE, this.testInfo.u(), this.testInfo.d());
    }

    public long g() {
        return this.testInfo.p();
    }

    private AxisAlignedBB i() {
        return this.testInfo.f();
    }

    private AxisAlignedBB j() {
        AxisAlignedBB axisalignedbb = this.testInfo.f();

        return axisalignedbb.a(BlockPosition.ZERO.e(this.i(BlockPosition.ZERO)));
    }

    public void a(Consumer<BlockPosition> consumer) {
        AxisAlignedBB axisalignedbb = this.j();

        BlockPosition.MutableBlockPosition.a(axisalignedbb.d(0.0D, 1.0D, 0.0D)).forEach(consumer);
    }

    public void e(Runnable runnable) {
        LongStream.range(this.testInfo.p(), (long) this.testInfo.w()).forEach((i) -> {
            GameTestHarnessInfo gametestharnessinfo = this.testInfo;

            Objects.requireNonNull(runnable);
            gametestharnessinfo.a(i, runnable::run);
        });
    }
}
