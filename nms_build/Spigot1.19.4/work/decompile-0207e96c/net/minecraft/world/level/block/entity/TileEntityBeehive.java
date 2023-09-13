package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsEntity;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockBeehive;
import net.minecraft.world.level.block.BlockCampfire;
import net.minecraft.world.level.block.BlockFire;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;

public class TileEntityBeehive extends TileEntity {

    public static final String TAG_FLOWER_POS = "FlowerPos";
    public static final String MIN_OCCUPATION_TICKS = "MinOccupationTicks";
    public static final String ENTITY_DATA = "EntityData";
    public static final String TICKS_IN_HIVE = "TicksInHive";
    public static final String HAS_NECTAR = "HasNectar";
    public static final String BEES = "Bees";
    private static final List<String> IGNORED_BEE_TAGS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "HivePos", "Passengers", "Leash", "UUID");
    public static final int MAX_OCCUPANTS = 3;
    private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
    private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
    public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
    private final List<TileEntityBeehive.HiveBee> stored = Lists.newArrayList();
    @Nullable
    public BlockPosition savedFlowerPos;

    public TileEntityBeehive(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BEEHIVE, blockposition, iblockdata);
    }

    @Override
    public void setChanged() {
        if (this.isFireNearby()) {
            this.emptyAllLivingFromHive((EntityHuman) null, this.level.getBlockState(this.getBlockPos()), TileEntityBeehive.ReleaseStatus.EMERGENCY);
        }

        super.setChanged();
    }

    public boolean isFireNearby() {
        if (this.level == null) {
            return false;
        } else {
            Iterator iterator = BlockPosition.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1)).iterator();

            BlockPosition blockposition;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                blockposition = (BlockPosition) iterator.next();
            } while (!(this.level.getBlockState(blockposition).getBlock() instanceof BlockFire));

            return true;
        }
    }

    public boolean isEmpty() {
        return this.stored.isEmpty();
    }

    public boolean isFull() {
        return this.stored.size() == 3;
    }

    public void emptyAllLivingFromHive(@Nullable EntityHuman entityhuman, IBlockData iblockdata, TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus) {
        List<Entity> list = this.releaseAllOccupants(iblockdata, tileentitybeehive_releasestatus);

        if (entityhuman != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (entity instanceof EntityBee) {
                    EntityBee entitybee = (EntityBee) entity;

                    if (entityhuman.position().distanceToSqr(entity.position()) <= 16.0D) {
                        if (!this.isSedated()) {
                            entitybee.setTarget(entityhuman);
                        } else {
                            entitybee.setStayOutOfHiveCountdown(400);
                        }
                    }
                }
            }
        }

    }

    private List<Entity> releaseAllOccupants(IBlockData iblockdata, TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus) {
        List<Entity> list = Lists.newArrayList();

        this.stored.removeIf((tileentitybeehive_hivebee) -> {
            return releaseOccupant(this.level, this.worldPosition, iblockdata, tileentitybeehive_hivebee, list, tileentitybeehive_releasestatus, this.savedFlowerPos);
        });
        if (!list.isEmpty()) {
            super.setChanged();
        }

        return list;
    }

    public void addOccupant(Entity entity, boolean flag) {
        this.addOccupantWithPresetTicks(entity, flag, 0);
    }

    @VisibleForDebug
    public int getOccupantCount() {
        return this.stored.size();
    }

    public static int getHoneyLevel(IBlockData iblockdata) {
        return (Integer) iblockdata.getValue(BlockBeehive.HONEY_LEVEL);
    }

    @VisibleForDebug
    public boolean isSedated() {
        return BlockCampfire.isSmokeyPos(this.level, this.getBlockPos());
    }

    public void addOccupantWithPresetTicks(Entity entity, boolean flag, int i) {
        if (this.stored.size() < 3) {
            entity.stopRiding();
            entity.ejectPassengers();
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entity.save(nbttagcompound);
            this.storeBee(nbttagcompound, i, flag);
            if (this.level != null) {
                if (entity instanceof EntityBee) {
                    EntityBee entitybee = (EntityBee) entity;

                    if (entitybee.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                        this.savedFlowerPos = entitybee.getSavedFlowerPos();
                    }
                }

                BlockPosition blockposition = this.getBlockPos();

                this.level.playSound((EntityHuman) null, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), SoundEffects.BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entity, this.getBlockState()));
            }

            entity.discard();
            super.setChanged();
        }
    }

    public void storeBee(NBTTagCompound nbttagcompound, int i, boolean flag) {
        this.stored.add(new TileEntityBeehive.HiveBee(nbttagcompound, i, flag ? 2400 : 600));
    }

    private static boolean releaseOccupant(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBeehive.HiveBee tileentitybeehive_hivebee, @Nullable List<Entity> list, TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus, @Nullable BlockPosition blockposition1) {
        if ((world.isNight() || world.isRaining()) && tileentitybeehive_releasestatus != TileEntityBeehive.ReleaseStatus.EMERGENCY) {
            return false;
        } else {
            NBTTagCompound nbttagcompound = tileentitybeehive_hivebee.entityData.copy();

            removeIgnoredBeeTags(nbttagcompound);
            nbttagcompound.put("HivePos", GameProfileSerializer.writeBlockPos(blockposition));
            nbttagcompound.putBoolean("NoGravity", true);
            EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockBeehive.FACING);
            BlockPosition blockposition2 = blockposition.relative(enumdirection);
            boolean flag = !world.getBlockState(blockposition2).getCollisionShape(world, blockposition2).isEmpty();

            if (flag && tileentitybeehive_releasestatus != TileEntityBeehive.ReleaseStatus.EMERGENCY) {
                return false;
            } else {
                Entity entity = EntityTypes.loadEntityRecursive(nbttagcompound, world, (entity1) -> {
                    return entity1;
                });

                if (entity != null) {
                    if (!entity.getType().is(TagsEntity.BEEHIVE_INHABITORS)) {
                        return false;
                    } else {
                        if (entity instanceof EntityBee) {
                            EntityBee entitybee = (EntityBee) entity;

                            if (blockposition1 != null && !entitybee.hasSavedFlowerPos() && world.random.nextFloat() < 0.9F) {
                                entitybee.setSavedFlowerPos(blockposition1);
                            }

                            if (tileentitybeehive_releasestatus == TileEntityBeehive.ReleaseStatus.HONEY_DELIVERED) {
                                entitybee.dropOffNectar();
                                if (iblockdata.is(TagsBlock.BEEHIVES, (blockbase_blockdata) -> {
                                    return blockbase_blockdata.hasProperty(BlockBeehive.HONEY_LEVEL);
                                })) {
                                    int i = getHoneyLevel(iblockdata);

                                    if (i < 5) {
                                        int j = world.random.nextInt(100) == 0 ? 2 : 1;

                                        if (i + j > 5) {
                                            --j;
                                        }

                                        world.setBlockAndUpdate(blockposition, (IBlockData) iblockdata.setValue(BlockBeehive.HONEY_LEVEL, i + j));
                                    }
                                }
                            }

                            setBeeReleaseData(tileentitybeehive_hivebee.ticksInHive, entitybee);
                            if (list != null) {
                                list.add(entitybee);
                            }

                            float f = entity.getBbWidth();
                            double d0 = flag ? 0.0D : 0.55D + (double) (f / 2.0F);
                            double d1 = (double) blockposition.getX() + 0.5D + d0 * (double) enumdirection.getStepX();
                            double d2 = (double) blockposition.getY() + 0.5D - (double) (entity.getBbHeight() / 2.0F);
                            double d3 = (double) blockposition.getZ() + 0.5D + d0 * (double) enumdirection.getStepZ();

                            entity.moveTo(d1, d2, d3, entity.getYRot(), entity.getXRot());
                        }

                        world.playSound((EntityHuman) null, blockposition, SoundEffects.BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entity, world.getBlockState(blockposition)));
                        return world.addFreshEntity(entity);
                    }
                } else {
                    return false;
                }
            }
        }
    }

    static void removeIgnoredBeeTags(NBTTagCompound nbttagcompound) {
        Iterator iterator = TileEntityBeehive.IGNORED_BEE_TAGS.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            nbttagcompound.remove(s);
        }

    }

    private static void setBeeReleaseData(int i, EntityBee entitybee) {
        int j = entitybee.getAge();

        if (j < 0) {
            entitybee.setAge(Math.min(0, j + i));
        } else if (j > 0) {
            entitybee.setAge(Math.max(0, j - i));
        }

        entitybee.setInLoveTime(Math.max(0, entitybee.getInLoveTime() - i));
    }

    private boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    private static void tickOccupants(World world, BlockPosition blockposition, IBlockData iblockdata, List<TileEntityBeehive.HiveBee> list, @Nullable BlockPosition blockposition1) {
        boolean flag = false;

        TileEntityBeehive.HiveBee tileentitybeehive_hivebee;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); ++tileentitybeehive_hivebee.ticksInHive) {
            tileentitybeehive_hivebee = (TileEntityBeehive.HiveBee) iterator.next();
            if (tileentitybeehive_hivebee.ticksInHive > tileentitybeehive_hivebee.minOccupationTicks) {
                TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus = tileentitybeehive_hivebee.entityData.getBoolean("HasNectar") ? TileEntityBeehive.ReleaseStatus.HONEY_DELIVERED : TileEntityBeehive.ReleaseStatus.BEE_RELEASED;

                if (releaseOccupant(world, blockposition, iblockdata, tileentitybeehive_hivebee, (List) null, tileentitybeehive_releasestatus, blockposition1)) {
                    flag = true;
                    iterator.remove();
                }
            }
        }

        if (flag) {
            setChanged(world, blockposition, iblockdata);
        }

    }

    public static void serverTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBeehive tileentitybeehive) {
        tickOccupants(world, blockposition, iblockdata, tileentitybeehive.stored, tileentitybeehive.savedFlowerPos);
        if (!tileentitybeehive.stored.isEmpty() && world.getRandom().nextDouble() < 0.005D) {
            double d0 = (double) blockposition.getX() + 0.5D;
            double d1 = (double) blockposition.getY();
            double d2 = (double) blockposition.getZ() + 0.5D;

            world.playSound((EntityHuman) null, d0, d1, d2, SoundEffects.BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        PacketDebug.sendHiveInfo(world, blockposition, iblockdata, tileentitybeehive);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.stored.clear();
        NBTTagList nbttaglist = nbttagcompound.getList("Bees", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
            TileEntityBeehive.HiveBee tileentitybeehive_hivebee = new TileEntityBeehive.HiveBee(nbttagcompound1.getCompound("EntityData"), nbttagcompound1.getInt("TicksInHive"), nbttagcompound1.getInt("MinOccupationTicks"));

            this.stored.add(tileentitybeehive_hivebee);
        }

        this.savedFlowerPos = null;
        if (nbttagcompound.contains("FlowerPos")) {
            this.savedFlowerPos = GameProfileSerializer.readBlockPos(nbttagcompound.getCompound("FlowerPos"));
        }

    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.put("Bees", this.writeBees());
        if (this.hasSavedFlowerPos()) {
            nbttagcompound.put("FlowerPos", GameProfileSerializer.writeBlockPos(this.savedFlowerPos));
        }

    }

    public NBTTagList writeBees() {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.stored.iterator();

        while (iterator.hasNext()) {
            TileEntityBeehive.HiveBee tileentitybeehive_hivebee = (TileEntityBeehive.HiveBee) iterator.next();
            NBTTagCompound nbttagcompound = tileentitybeehive_hivebee.entityData.copy();

            nbttagcompound.remove("UUID");
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.put("EntityData", nbttagcompound);
            nbttagcompound1.putInt("TicksInHive", tileentitybeehive_hivebee.ticksInHive);
            nbttagcompound1.putInt("MinOccupationTicks", tileentitybeehive_hivebee.minOccupationTicks);
            nbttaglist.add(nbttagcompound1);
        }

        return nbttaglist;
    }

    public static enum ReleaseStatus {

        HONEY_DELIVERED, BEE_RELEASED, EMERGENCY;

        private ReleaseStatus() {}
    }

    private static class HiveBee {

        final NBTTagCompound entityData;
        int ticksInHive;
        final int minOccupationTicks;

        HiveBee(NBTTagCompound nbttagcompound, int i, int j) {
            TileEntityBeehive.removeIgnoredBeeTags(nbttagcompound);
            this.entityData = nbttagcompound;
            this.ticksInHive = i;
            this.minOccupationTicks = j;
        }
    }
}
