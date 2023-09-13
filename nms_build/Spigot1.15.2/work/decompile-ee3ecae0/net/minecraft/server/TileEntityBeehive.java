package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class TileEntityBeehive extends TileEntity implements ITickable {

    private final List<TileEntityBeehive.HiveBee> bees = Lists.newArrayList();
    @Nullable
    public BlockPosition flowerPos = null;

    public TileEntityBeehive() {
        super(TileEntityTypes.BEEHIVE);
    }

    @Override
    public void update() {
        if (this.d()) {
            this.a((EntityHuman) null, this.world.getType(this.getPosition()), TileEntityBeehive.ReleaseStatus.EMERGENCY);
        }

        super.update();
    }

    public boolean d() {
        if (this.world == null) {
            return false;
        } else {
            Iterator iterator = BlockPosition.a(this.position.b(-1, -1, -1), this.position.b(1, 1, 1)).iterator();

            BlockPosition blockposition;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                blockposition = (BlockPosition) iterator.next();
            } while (!(this.world.getType(blockposition).getBlock() instanceof BlockFire));

            return true;
        }
    }

    public boolean isEmpty() {
        return this.bees.isEmpty();
    }

    public boolean isFull() {
        return this.bees.size() == 3;
    }

    public void a(@Nullable EntityHuman entityhuman, IBlockData iblockdata, TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus) {
        List<Entity> list = this.a(iblockdata, tileentitybeehive_releasestatus);

        if (entityhuman != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (entity instanceof EntityBee) {
                    EntityBee entitybee = (EntityBee) entity;

                    if (entityhuman.getPositionVector().distanceSquared(entity.getPositionVector()) <= 16.0D) {
                        if (!this.k()) {
                            entitybee.a((Entity) entityhuman);
                        } else {
                            entitybee.setCannotEnterHiveTicks(400);
                        }
                    }
                }
            }
        }

    }

    private List<Entity> a(IBlockData iblockdata, TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus) {
        List<Entity> list = Lists.newArrayList();

        this.bees.removeIf((tileentitybeehive_hivebee) -> {
            return this.a(iblockdata, tileentitybeehive_hivebee.entityData, list, tileentitybeehive_releasestatus);
        });
        return list;
    }

    public void a(Entity entity, boolean flag) {
        this.a(entity, flag, 0);
    }

    public int j() {
        return this.bees.size();
    }

    public static int a(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockBeehive.c);
    }

    public boolean k() {
        return BlockCampfire.b(this.world, this.getPosition(), 5);
    }

    protected void l() {
        PacketDebug.a(this);
    }

    public void a(Entity entity, boolean flag, int i) {
        if (this.bees.size() < 3) {
            entity.stopRiding();
            entity.ejectPassengers();
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entity.d(nbttagcompound);
            this.bees.add(new TileEntityBeehive.HiveBee(nbttagcompound, i, flag ? 2400 : 600));
            if (this.world != null) {
                if (entity instanceof EntityBee) {
                    EntityBee entitybee = (EntityBee) entity;

                    if (entitybee.hasFlowerPos() && (!this.x() || this.world.random.nextBoolean())) {
                        this.flowerPos = entitybee.getFlowerPos();
                    }
                }

                BlockPosition blockposition = this.getPosition();

                this.world.playSound((EntityHuman) null, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), SoundEffects.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            entity.die();
        }
    }

    private boolean a(IBlockData iblockdata, NBTTagCompound nbttagcompound, @Nullable List<Entity> list, TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus) {
        BlockPosition blockposition = this.getPosition();

        if ((this.world.isNight() || this.world.isRaining()) && tileentitybeehive_releasestatus != TileEntityBeehive.ReleaseStatus.EMERGENCY) {
            return false;
        } else {
            nbttagcompound.remove("Passengers");
            nbttagcompound.remove("Leash");
            nbttagcompound.c("UUID");
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockBeehive.b);
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            boolean flag = !this.world.getType(blockposition1).getCollisionShape(this.world, blockposition1).isEmpty();

            if (flag && tileentitybeehive_releasestatus != TileEntityBeehive.ReleaseStatus.EMERGENCY) {
                return false;
            } else {
                Entity entity = EntityTypes.a(nbttagcompound, this.world, (entity1) -> {
                    return entity1;
                });

                if (entity != null) {
                    float f = entity.getWidth();
                    double d0 = flag ? 0.0D : 0.55D + (double) (f / 2.0F);
                    double d1 = (double) blockposition.getX() + 0.5D + d0 * (double) enumdirection.getAdjacentX();
                    double d2 = (double) blockposition.getY() + 0.5D - (double) (entity.getHeight() / 2.0F);
                    double d3 = (double) blockposition.getZ() + 0.5D + d0 * (double) enumdirection.getAdjacentZ();

                    entity.setPositionRotation(d1, d2, d3, entity.yaw, entity.pitch);
                    if (!entity.getEntityType().a(TagsEntity.BEEHIVE_INHABITORS)) {
                        return false;
                    } else {
                        if (entity instanceof EntityBee) {
                            EntityBee entitybee = (EntityBee) entity;

                            if (this.x() && !entitybee.hasFlowerPos() && this.world.random.nextFloat() < 0.9F) {
                                entitybee.setFlowerPos(this.flowerPos);
                            }

                            if (tileentitybeehive_releasestatus == TileEntityBeehive.ReleaseStatus.HONEY_DELIVERED) {
                                entitybee.eG();
                                if (iblockdata.getBlock().a(TagsBlock.BEEHIVES)) {
                                    int i = a(iblockdata);

                                    if (i < 5) {
                                        int j = this.world.random.nextInt(100) == 0 ? 2 : 1;

                                        if (i + j > 5) {
                                            --j;
                                        }

                                        this.world.setTypeUpdate(this.getPosition(), (IBlockData) iblockdata.set(BlockBeehive.c, i + j));
                                    }
                                }
                            }

                            entitybee.eu();
                            if (list != null) {
                                list.add(entitybee);
                            }
                        }

                        BlockPosition blockposition2 = this.getPosition();

                        this.world.playSound((EntityHuman) null, (double) blockposition2.getX(), (double) blockposition2.getY(), (double) blockposition2.getZ(), SoundEffects.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return this.world.addEntity(entity);
                    }
                } else {
                    return false;
                }
            }
        }
    }

    private boolean x() {
        return this.flowerPos != null;
    }

    private void y() {
        Iterator<TileEntityBeehive.HiveBee> iterator = this.bees.iterator();
        IBlockData iblockdata = this.getBlock();

        while (iterator.hasNext()) {
            TileEntityBeehive.HiveBee tileentitybeehive_hivebee = (TileEntityBeehive.HiveBee) iterator.next();

            if (tileentitybeehive_hivebee.ticksInHive > tileentitybeehive_hivebee.minOccupationTicks) {
                NBTTagCompound nbttagcompound = tileentitybeehive_hivebee.entityData;
                TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus = nbttagcompound.getBoolean("HasNectar") ? TileEntityBeehive.ReleaseStatus.HONEY_DELIVERED : TileEntityBeehive.ReleaseStatus.BEE_RELEASED;

                if (this.a(iblockdata, nbttagcompound, (List) null, tileentitybeehive_releasestatus)) {
                    iterator.remove();
                }
            } else {
                tileentitybeehive_hivebee.ticksInHive++;
            }
        }

    }

    @Override
    public void tick() {
        if (!this.world.isClientSide) {
            this.y();
            BlockPosition blockposition = this.getPosition();

            if (this.bees.size() > 0 && this.world.getRandom().nextDouble() < 0.005D) {
                double d0 = (double) blockposition.getX() + 0.5D;
                double d1 = (double) blockposition.getY();
                double d2 = (double) blockposition.getZ() + 0.5D;

                this.world.playSound((EntityHuman) null, d0, d1, d2, SoundEffects.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            this.l();
        }
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.bees.clear();
        NBTTagList nbttaglist = nbttagcompound.getList("Bees", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
            TileEntityBeehive.HiveBee tileentitybeehive_hivebee = new TileEntityBeehive.HiveBee(nbttagcompound1.getCompound("EntityData"), nbttagcompound1.getInt("TicksInHive"), nbttagcompound1.getInt("MinOccupationTicks"));

            this.bees.add(tileentitybeehive_hivebee);
        }

        this.flowerPos = null;
        if (nbttagcompound.hasKey("FlowerPos")) {
            this.flowerPos = GameProfileSerializer.c(nbttagcompound.getCompound("FlowerPos"));
        }

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.set("Bees", this.m());
        if (this.x()) {
            nbttagcompound.set("FlowerPos", GameProfileSerializer.a(this.flowerPos));
        }

        return nbttagcompound;
    }

    public NBTTagList m() {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.bees.iterator();

        while (iterator.hasNext()) {
            TileEntityBeehive.HiveBee tileentitybeehive_hivebee = (TileEntityBeehive.HiveBee) iterator.next();

            tileentitybeehive_hivebee.entityData.c("UUID");
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.set("EntityData", tileentitybeehive_hivebee.entityData);
            nbttagcompound.setInt("TicksInHive", tileentitybeehive_hivebee.ticksInHive);
            nbttagcompound.setInt("MinOccupationTicks", tileentitybeehive_hivebee.minOccupationTicks);
            nbttaglist.add(nbttagcompound);
        }

        return nbttaglist;
    }

    static class HiveBee {

        private final NBTTagCompound entityData;
        private int ticksInHive;
        private final int minOccupationTicks;

        private HiveBee(NBTTagCompound nbttagcompound, int i, int j) {
            nbttagcompound.c("UUID");
            this.entityData = nbttagcompound;
            this.ticksInHive = i;
            this.minOccupationTicks = j;
        }
    }

    public static enum ReleaseStatus {

        HONEY_DELIVERED, BEE_RELEASED, EMERGENCY;

        private ReleaseStatus() {}
    }
}
