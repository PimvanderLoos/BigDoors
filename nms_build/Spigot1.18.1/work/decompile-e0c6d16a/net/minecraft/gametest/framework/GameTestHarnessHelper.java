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

    public WorldServer getLevel() {
        return this.testInfo.getLevel();
    }

    public IBlockData getBlockState(BlockPosition blockposition) {
        return this.getLevel().getBlockState(this.absolutePos(blockposition));
    }

    @Nullable
    public TileEntity getBlockEntity(BlockPosition blockposition) {
        return this.getLevel().getBlockEntity(this.absolutePos(blockposition));
    }

    public void killAllEntities() {
        AxisAlignedBB axisalignedbb = this.getBounds();
        List<Entity> list = this.getLevel().getEntitiesOfClass(Entity.class, axisalignedbb.inflate(1.0D), (entity) -> {
            return !(entity instanceof EntityHuman);
        });

        list.forEach(Entity::kill);
    }

    public EntityItem spawnItem(Item item, float f, float f1, float f2) {
        WorldServer worldserver = this.getLevel();
        Vec3D vec3d = this.absoluteVec(new Vec3D((double) f, (double) f1, (double) f2));
        EntityItem entityitem = new EntityItem(worldserver, vec3d.x, vec3d.y, vec3d.z, new ItemStack(item, 1));

        entityitem.setDeltaMovement(0.0D, 0.0D, 0.0D);
        worldserver.addFreshEntity(entityitem);
        return entityitem;
    }

    public <E extends Entity> E spawn(EntityTypes<E> entitytypes, BlockPosition blockposition) {
        return this.spawn(entitytypes, Vec3D.atBottomCenterOf(blockposition));
    }

    public <E extends Entity> E spawn(EntityTypes<E> entitytypes, Vec3D vec3d) {
        WorldServer worldserver = this.getLevel();
        E e0 = entitytypes.create(worldserver);

        if (e0 instanceof EntityInsentient) {
            ((EntityInsentient) e0).setPersistenceRequired();
        }

        Vec3D vec3d1 = this.absoluteVec(vec3d);

        e0.moveTo(vec3d1.x, vec3d1.y, vec3d1.z, e0.getYRot(), e0.getXRot());
        worldserver.addFreshEntity(e0);
        return e0;
    }

    public <E extends Entity> E spawn(EntityTypes<E> entitytypes, int i, int j, int k) {
        return this.spawn(entitytypes, new BlockPosition(i, j, k));
    }

    public <E extends Entity> E spawn(EntityTypes<E> entitytypes, float f, float f1, float f2) {
        return this.spawn(entitytypes, new Vec3D((double) f, (double) f1, (double) f2));
    }

    public <E extends EntityInsentient> E spawnWithNoFreeWill(EntityTypes<E> entitytypes, BlockPosition blockposition) {
        E e0 = (EntityInsentient) this.spawn(entitytypes, blockposition);

        e0.removeFreeWill();
        return e0;
    }

    public <E extends EntityInsentient> E spawnWithNoFreeWill(EntityTypes<E> entitytypes, int i, int j, int k) {
        return this.spawnWithNoFreeWill(entitytypes, new BlockPosition(i, j, k));
    }

    public <E extends EntityInsentient> E spawnWithNoFreeWill(EntityTypes<E> entitytypes, Vec3D vec3d) {
        E e0 = (EntityInsentient) this.spawn(entitytypes, vec3d);

        e0.removeFreeWill();
        return e0;
    }

    public <E extends EntityInsentient> E spawnWithNoFreeWill(EntityTypes<E> entitytypes, float f, float f1, float f2) {
        return this.spawnWithNoFreeWill(entitytypes, new Vec3D((double) f, (double) f1, (double) f2));
    }

    public GameTestHarnessSequence walkTo(EntityInsentient entityinsentient, BlockPosition blockposition, float f) {
        return this.startSequence().thenExecuteAfter(2, () -> {
            PathEntity pathentity = entityinsentient.getNavigation().createPath(this.absolutePos(blockposition), 0);

            entityinsentient.getNavigation().moveTo(pathentity, (double) f);
        });
    }

    public void pressButton(int i, int j, int k) {
        this.pressButton(new BlockPosition(i, j, k));
    }

    public void pressButton(BlockPosition blockposition) {
        this.assertBlockState(blockposition, (iblockdata) -> {
            return iblockdata.is((Tag) TagsBlock.BUTTONS);
        }, () -> {
            return "Expected button";
        });
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        IBlockData iblockdata = this.getLevel().getBlockState(blockposition1);
        BlockButtonAbstract blockbuttonabstract = (BlockButtonAbstract) iblockdata.getBlock();

        blockbuttonabstract.press(iblockdata, this.getLevel(), blockposition1);
    }

    public void useBlock(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        IBlockData iblockdata = this.getLevel().getBlockState(blockposition1);

        iblockdata.use(this.getLevel(), this.makeMockPlayer(), EnumHand.MAIN_HAND, new MovingObjectPositionBlock(Vec3D.atCenterOf(blockposition1), EnumDirection.NORTH, blockposition1, true));
    }

    public EntityLiving makeAboutToDrown(EntityLiving entityliving) {
        entityliving.setAirSupply(0);
        entityliving.setHealth(0.25F);
        return entityliving;
    }

    public EntityHuman makeMockPlayer() {
        return new EntityHuman(this.getLevel(), BlockPosition.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
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

    public void pullLever(int i, int j, int k) {
        this.pullLever(new BlockPosition(i, j, k));
    }

    public void pullLever(BlockPosition blockposition) {
        this.assertBlockPresent(Blocks.LEVER, blockposition);
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        IBlockData iblockdata = this.getLevel().getBlockState(blockposition1);
        BlockLever blocklever = (BlockLever) iblockdata.getBlock();

        blocklever.pull(iblockdata, this.getLevel(), blockposition1);
    }

    public void pulseRedstone(BlockPosition blockposition, long i) {
        this.setBlock(blockposition, Blocks.REDSTONE_BLOCK);
        this.runAfterDelay(i, () -> {
            this.setBlock(blockposition, Blocks.AIR);
        });
    }

    public void destroyBlock(BlockPosition blockposition) {
        this.getLevel().destroyBlock(this.absolutePos(blockposition), false, (Entity) null);
    }

    public void setBlock(int i, int j, int k, Block block) {
        this.setBlock(new BlockPosition(i, j, k), block);
    }

    public void setBlock(int i, int j, int k, IBlockData iblockdata) {
        this.setBlock(new BlockPosition(i, j, k), iblockdata);
    }

    public void setBlock(BlockPosition blockposition, Block block) {
        this.setBlock(blockposition, block.defaultBlockState());
    }

    public void setBlock(BlockPosition blockposition, IBlockData iblockdata) {
        this.getLevel().setBlock(this.absolutePos(blockposition), iblockdata, 3);
    }

    public void setNight() {
        this.setDayTime(13000);
    }

    public void setDayTime(int i) {
        this.getLevel().setDayTime((long) i);
    }

    public void assertBlockPresent(Block block, int i, int j, int k) {
        this.assertBlockPresent(block, new BlockPosition(i, j, k));
    }

    public void assertBlockPresent(Block block, BlockPosition blockposition) {
        IBlockData iblockdata = this.getBlockState(blockposition);
        Predicate predicate = (block1) -> {
            return iblockdata.is(block);
        };
        String s = block.getName().getString();

        this.assertBlock(blockposition, predicate, "Expected " + s + ", got " + iblockdata.getBlock().getName().getString());
    }

    public void assertBlockNotPresent(Block block, int i, int j, int k) {
        this.assertBlockNotPresent(block, new BlockPosition(i, j, k));
    }

    public void assertBlockNotPresent(Block block, BlockPosition blockposition) {
        this.assertBlock(blockposition, (block1) -> {
            return !this.getBlockState(blockposition).is(block);
        }, "Did not expect " + block.getName().getString());
    }

    public void succeedWhenBlockPresent(Block block, int i, int j, int k) {
        this.succeedWhenBlockPresent(block, new BlockPosition(i, j, k));
    }

    public void succeedWhenBlockPresent(Block block, BlockPosition blockposition) {
        this.succeedWhen(() -> {
            this.assertBlockPresent(block, blockposition);
        });
    }

    public void assertBlock(BlockPosition blockposition, Predicate<Block> predicate, String s) {
        this.assertBlock(blockposition, predicate, () -> {
            return s;
        });
    }

    public void assertBlock(BlockPosition blockposition, Predicate<Block> predicate, Supplier<String> supplier) {
        this.assertBlockState(blockposition, (iblockdata) -> {
            return predicate.test(iblockdata.getBlock());
        }, supplier);
    }

    public <T extends Comparable<T>> void assertBlockProperty(BlockPosition blockposition, IBlockState<T> iblockstate, T t0) {
        this.assertBlockState(blockposition, (iblockdata) -> {
            return iblockdata.hasProperty(iblockstate) && iblockdata.getValue(iblockstate).equals(t0);
        }, () -> {
            String s = iblockstate.getName();

            return "Expected property " + s + " to be " + t0;
        });
    }

    public <T extends Comparable<T>> void assertBlockProperty(BlockPosition blockposition, IBlockState<T> iblockstate, Predicate<T> predicate, String s) {
        this.assertBlockState(blockposition, (iblockdata) -> {
            return predicate.test(iblockdata.getValue(iblockstate));
        }, () -> {
            return s;
        });
    }

    public void assertBlockState(BlockPosition blockposition, Predicate<IBlockData> predicate, Supplier<String> supplier) {
        IBlockData iblockdata = this.getBlockState(blockposition);

        if (!predicate.test(iblockdata)) {
            throw new GameTestHarnessAssertionPosition((String) supplier.get(), this.absolutePos(blockposition), blockposition, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityTypes<?> entitytypes) {
        List<? extends Entity> list = this.getLevel().getEntities((EntityTypeTest) entitytypes, this.getBounds(), Entity::isAlive);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertion("Expected " + entitytypes.toShortString() + " to exist");
        }
    }

    public void assertEntityPresent(EntityTypes<?> entitytypes, int i, int j, int k) {
        this.assertEntityPresent(entitytypes, new BlockPosition(i, j, k));
    }

    public void assertEntityPresent(EntityTypes<?> entitytypes, BlockPosition blockposition) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        List<? extends Entity> list = this.getLevel().getEntities((EntityTypeTest) entitytypes, new AxisAlignedBB(blockposition1), Entity::isAlive);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertionPosition("Expected " + entitytypes.toShortString(), blockposition1, blockposition, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityTypes<?> entitytypes, BlockPosition blockposition, double d0) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        List<? extends Entity> list = this.getLevel().getEntities((EntityTypeTest) entitytypes, (new AxisAlignedBB(blockposition1)).inflate(d0), Entity::isAlive);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertionPosition("Expected " + entitytypes.toShortString(), blockposition1, blockposition, this.testInfo.getTick());
        }
    }

    public void assertEntityInstancePresent(Entity entity, int i, int j, int k) {
        this.assertEntityInstancePresent(entity, new BlockPosition(i, j, k));
    }

    public void assertEntityInstancePresent(Entity entity, BlockPosition blockposition) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        List<? extends Entity> list = this.getLevel().getEntities((EntityTypeTest) entity.getType(), new AxisAlignedBB(blockposition1), Entity::isAlive);

        list.stream().filter((entity1) -> {
            return entity1 == entity;
        }).findFirst().orElseThrow(() -> {
            return new GameTestHarnessAssertionPosition("Expected " + entity.getType().toShortString(), blockposition1, blockposition, this.testInfo.getTick());
        });
    }

    public void assertItemEntityCountIs(Item item, BlockPosition blockposition, double d0, int i) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        List<EntityItem> list = this.getLevel().getEntities((EntityTypeTest) EntityTypes.ITEM, (new AxisAlignedBB(blockposition1)).inflate(d0), Entity::isAlive);
        int j = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            EntityItem entityitem = (EntityItem) entity;

            if (entityitem.getItem().getItem().equals(item)) {
                j += entityitem.getItem().getCount();
            }
        }

        if (j != i) {
            throw new GameTestHarnessAssertionPosition("Expected " + i + " " + item.getDescription().getString() + " items to exist (found " + j + ")", blockposition1, blockposition, this.testInfo.getTick());
        }
    }

    public void assertItemEntityPresent(Item item, BlockPosition blockposition, double d0) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        List<? extends Entity> list = this.getLevel().getEntities((EntityTypeTest) EntityTypes.ITEM, (new AxisAlignedBB(blockposition1)).inflate(d0), Entity::isAlive);
        Iterator iterator = list.iterator();

        EntityItem entityitem;

        do {
            if (!iterator.hasNext()) {
                throw new GameTestHarnessAssertionPosition("Expected " + item.getDescription().getString() + " item", blockposition1, blockposition, this.testInfo.getTick());
            }

            Entity entity = (Entity) iterator.next();

            entityitem = (EntityItem) entity;
        } while (!entityitem.getItem().getItem().equals(item));

    }

    public void assertEntityNotPresent(EntityTypes<?> entitytypes) {
        List<? extends Entity> list = this.getLevel().getEntities((EntityTypeTest) entitytypes, this.getBounds(), Entity::isAlive);

        if (!list.isEmpty()) {
            throw new GameTestHarnessAssertion("Did not expect " + entitytypes.toShortString() + " to exist");
        }
    }

    public void assertEntityNotPresent(EntityTypes<?> entitytypes, int i, int j, int k) {
        this.assertEntityNotPresent(entitytypes, new BlockPosition(i, j, k));
    }

    public void assertEntityNotPresent(EntityTypes<?> entitytypes, BlockPosition blockposition) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        List<? extends Entity> list = this.getLevel().getEntities((EntityTypeTest) entitytypes, new AxisAlignedBB(blockposition1), Entity::isAlive);

        if (!list.isEmpty()) {
            throw new GameTestHarnessAssertionPosition("Did not expect " + entitytypes.toShortString(), blockposition1, blockposition, this.testInfo.getTick());
        }
    }

    public void assertEntityTouching(EntityTypes<?> entitytypes, double d0, double d1, double d2) {
        Vec3D vec3d = new Vec3D(d0, d1, d2);
        Vec3D vec3d1 = this.absoluteVec(vec3d);
        Predicate<? super Entity> predicate = (entity) -> {
            return entity.getBoundingBox().intersects(vec3d1, vec3d1);
        };
        List<? extends Entity> list = this.getLevel().getEntities((EntityTypeTest) entitytypes, this.getBounds(), predicate);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertion("Expected " + entitytypes.toShortString() + " to touch " + vec3d1 + " (relative " + vec3d + ")");
        }
    }

    public void assertEntityNotTouching(EntityTypes<?> entitytypes, double d0, double d1, double d2) {
        Vec3D vec3d = new Vec3D(d0, d1, d2);
        Vec3D vec3d1 = this.absoluteVec(vec3d);
        Predicate<? super Entity> predicate = (entity) -> {
            return !entity.getBoundingBox().intersects(vec3d1, vec3d1);
        };
        List<? extends Entity> list = this.getLevel().getEntities((EntityTypeTest) entitytypes, this.getBounds(), predicate);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertion("Did not expect " + entitytypes.toShortString() + " to touch " + vec3d1 + " (relative " + vec3d + ")");
        }
    }

    public <E extends Entity, T> void assertEntityData(BlockPosition blockposition, EntityTypes<E> entitytypes, Function<? super E, T> function, @Nullable T t0) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        List<E> list = this.getLevel().getEntities((EntityTypeTest) entitytypes, new AxisAlignedBB(blockposition1), Entity::isAlive);

        if (list.isEmpty()) {
            throw new GameTestHarnessAssertionPosition("Expected " + entitytypes.toShortString(), blockposition1, blockposition, this.testInfo.getTick());
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

    public void assertContainerEmpty(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        TileEntity tileentity = this.getLevel().getBlockEntity(blockposition1);

        if (tileentity instanceof TileEntityContainer && !((TileEntityContainer) tileentity).isEmpty()) {
            throw new GameTestHarnessAssertion("Container should be empty");
        }
    }

    public void assertContainerContains(BlockPosition blockposition, Item item) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        TileEntity tileentity = this.getLevel().getBlockEntity(blockposition1);

        if (tileentity instanceof TileEntityContainer && ((TileEntityContainer) tileentity).countItem(item) != 1) {
            throw new GameTestHarnessAssertion("Container should contain: " + item);
        }
    }

    public void assertSameBlockStates(StructureBoundingBox structureboundingbox, BlockPosition blockposition) {
        BlockPosition.betweenClosedStream(structureboundingbox).forEach((blockposition1) -> {
            BlockPosition blockposition2 = blockposition.offset(blockposition1.getX() - structureboundingbox.minX(), blockposition1.getY() - structureboundingbox.minY(), blockposition1.getZ() - structureboundingbox.minZ());

            this.assertSameBlockState(blockposition1, blockposition2);
        });
    }

    public void assertSameBlockState(BlockPosition blockposition, BlockPosition blockposition1) {
        IBlockData iblockdata = this.getBlockState(blockposition);
        IBlockData iblockdata1 = this.getBlockState(blockposition1);

        if (iblockdata != iblockdata1) {
            this.fail("Incorrect state. Expected " + iblockdata1 + ", got " + iblockdata, blockposition);
        }

    }

    public void assertAtTickTimeContainerContains(long i, BlockPosition blockposition, Item item) {
        this.runAtTickTime(i, () -> {
            this.assertContainerContains(blockposition, item);
        });
    }

    public void assertAtTickTimeContainerEmpty(long i, BlockPosition blockposition) {
        this.runAtTickTime(i, () -> {
            this.assertContainerEmpty(blockposition);
        });
    }

    public <E extends Entity, T> void succeedWhenEntityData(BlockPosition blockposition, EntityTypes<E> entitytypes, Function<E, T> function, T t0) {
        this.succeedWhen(() -> {
            this.assertEntityData(blockposition, entitytypes, function, t0);
        });
    }

    public <E extends Entity> void assertEntityProperty(E e0, Predicate<E> predicate, String s) {
        if (!predicate.test(e0)) {
            throw new GameTestHarnessAssertion("Entity " + e0 + " failed " + s + " test");
        }
    }

    public <E extends Entity, T> void assertEntityProperty(E e0, Function<E, T> function, String s, T t0) {
        T t1 = function.apply(e0);

        if (!t1.equals(t0)) {
            throw new GameTestHarnessAssertion("Entity " + e0 + " value " + s + "=" + t1 + " is not equal to expected " + t0);
        }
    }

    public void succeedWhenEntityPresent(EntityTypes<?> entitytypes, int i, int j, int k) {
        this.succeedWhenEntityPresent(entitytypes, new BlockPosition(i, j, k));
    }

    public void succeedWhenEntityPresent(EntityTypes<?> entitytypes, BlockPosition blockposition) {
        this.succeedWhen(() -> {
            this.assertEntityPresent(entitytypes, blockposition);
        });
    }

    public void succeedWhenEntityNotPresent(EntityTypes<?> entitytypes, int i, int j, int k) {
        this.succeedWhenEntityNotPresent(entitytypes, new BlockPosition(i, j, k));
    }

    public void succeedWhenEntityNotPresent(EntityTypes<?> entitytypes, BlockPosition blockposition) {
        this.succeedWhen(() -> {
            this.assertEntityNotPresent(entitytypes, blockposition);
        });
    }

    public void succeed() {
        this.testInfo.succeed();
    }

    private void ensureSingleFinalCheck() {
        if (this.finalCheckAdded) {
            throw new IllegalStateException("This test already has final clause");
        } else {
            this.finalCheckAdded = true;
        }
    }

    public void succeedIf(Runnable runnable) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil(0L, runnable).thenSucceed();
    }

    public void succeedWhen(Runnable runnable) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil(runnable).thenSucceed();
    }

    public void succeedOnTickWhen(int i, Runnable runnable) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil((long) i, runnable).thenSucceed();
    }

    public void runAtTickTime(long i, Runnable runnable) {
        this.testInfo.setRunAtTickTime(i, runnable);
    }

    public void runAfterDelay(long i, Runnable runnable) {
        this.runAtTickTime(this.testInfo.getTick() + i, runnable);
    }

    public void randomTick(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.absolutePos(blockposition);
        WorldServer worldserver = this.getLevel();

        worldserver.getBlockState(blockposition1).randomTick(worldserver, blockposition1, worldserver.random);
    }

    public void fail(String s, BlockPosition blockposition) {
        throw new GameTestHarnessAssertionPosition(s, this.absolutePos(blockposition), blockposition, this.getTick());
    }

    public void fail(String s, Entity entity) {
        throw new GameTestHarnessAssertionPosition(s, entity.blockPosition(), this.relativePos(entity.blockPosition()), this.getTick());
    }

    public void fail(String s) {
        throw new GameTestHarnessAssertion(s);
    }

    public void failIf(Runnable runnable) {
        this.testInfo.createSequence().thenWaitUntil(runnable).thenFail(() -> {
            return new GameTestHarnessAssertion("Fail conditions met");
        });
    }

    public void failIfEver(Runnable runnable) {
        LongStream.range(this.testInfo.getTick(), (long) this.testInfo.getTimeoutTicks()).forEach((i) -> {
            GameTestHarnessInfo gametestharnessinfo = this.testInfo;

            Objects.requireNonNull(runnable);
            gametestharnessinfo.setRunAtTickTime(i, runnable::run);
        });
    }

    public GameTestHarnessSequence startSequence() {
        return this.testInfo.createSequence();
    }

    public BlockPosition absolutePos(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.testInfo.getStructureBlockPos();
        BlockPosition blockposition2 = blockposition1.offset(blockposition);

        return DefinedStructure.transform(blockposition2, EnumBlockMirror.NONE, this.testInfo.getRotation(), blockposition1);
    }

    public BlockPosition relativePos(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.testInfo.getStructureBlockPos();
        EnumBlockRotation enumblockrotation = this.testInfo.getRotation().getRotated(EnumBlockRotation.CLOCKWISE_180);
        BlockPosition blockposition2 = DefinedStructure.transform(blockposition, EnumBlockMirror.NONE, enumblockrotation, blockposition1);

        return blockposition2.subtract(blockposition1);
    }

    public Vec3D absoluteVec(Vec3D vec3d) {
        Vec3D vec3d1 = Vec3D.atLowerCornerOf(this.testInfo.getStructureBlockPos());

        return DefinedStructure.transform(vec3d1.add(vec3d), EnumBlockMirror.NONE, this.testInfo.getRotation(), this.testInfo.getStructureBlockPos());
    }

    public long getTick() {
        return this.testInfo.getTick();
    }

    private AxisAlignedBB getBounds() {
        return this.testInfo.getStructureBounds();
    }

    private AxisAlignedBB getRelativeBounds() {
        AxisAlignedBB axisalignedbb = this.testInfo.getStructureBounds();

        return axisalignedbb.move(BlockPosition.ZERO.subtract(this.absolutePos(BlockPosition.ZERO)));
    }

    public void forEveryBlockInStructure(Consumer<BlockPosition> consumer) {
        AxisAlignedBB axisalignedbb = this.getRelativeBounds();

        BlockPosition.MutableBlockPosition.betweenClosedStream(axisalignedbb.move(0.0D, 1.0D, 0.0D)).forEach(consumer);
    }

    public void onEachTick(Runnable runnable) {
        LongStream.range(this.testInfo.getTick(), (long) this.testInfo.getTimeoutTicks()).forEach((i) -> {
            GameTestHarnessInfo gametestharnessinfo = this.testInfo;

            Objects.requireNonNull(runnable);
            gametestharnessinfo.setRunAtTickTime(i, runnable::run);
        });
    }
}
