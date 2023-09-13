package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutAttachEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateAttributes;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import net.minecraft.world.phys.Vec3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int TOLERANCE_LEVEL_ROTATION = 1;
    private final WorldServer level;
    private final Entity entity;
    private final int updateInterval;
    private final boolean trackDelta;
    private final Consumer<Packet<?>> broadcast;
    private long xp;
    private long yp;
    private long zp;
    private int yRotp;
    private int xRotp;
    private int yHeadRotp;
    private Vec3D ap;
    private int tickCount;
    private int teleportDelay;
    private List<Entity> lastPassengers;
    private boolean wasRiding;
    private boolean wasOnGround;

    public EntityTrackerEntry(WorldServer worldserver, Entity entity, int i, boolean flag, Consumer<Packet<?>> consumer) {
        this.ap = Vec3D.ZERO;
        this.lastPassengers = Collections.emptyList();
        this.level = worldserver;
        this.broadcast = consumer;
        this.entity = entity;
        this.updateInterval = i;
        this.trackDelta = flag;
        this.updateSentPos();
        this.yRotp = MathHelper.floor(entity.getYRot() * 256.0F / 360.0F);
        this.xRotp = MathHelper.floor(entity.getXRot() * 256.0F / 360.0F);
        this.yHeadRotp = MathHelper.floor(entity.getYHeadRot() * 256.0F / 360.0F);
        this.wasOnGround = entity.isOnGround();
    }

    public void sendChanges() {
        List<Entity> list = this.entity.getPassengers();

        if (!list.equals(this.lastPassengers)) {
            this.lastPassengers = list;
            this.broadcast.accept(new PacketPlayOutMount(this.entity));
        }

        if (this.entity instanceof EntityItemFrame && this.tickCount % 10 == 0) {
            EntityItemFrame entityitemframe = (EntityItemFrame) this.entity;
            ItemStack itemstack = entityitemframe.getItem();

            if (itemstack.getItem() instanceof ItemWorldMap) {
                Integer integer = ItemWorldMap.getMapId(itemstack);
                WorldMap worldmap = ItemWorldMap.getSavedData(integer, this.level);

                if (worldmap != null) {
                    Iterator iterator = this.level.players().iterator();

                    while (iterator.hasNext()) {
                        EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                        worldmap.tickCarriedBy(entityplayer, itemstack);
                        Packet<?> packet = worldmap.getUpdatePacket(integer, entityplayer);

                        if (packet != null) {
                            entityplayer.connection.send(packet);
                        }
                    }
                }
            }

            this.sendDirtyEntityData();
        }

        if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
            int i;
            int j;

            if (this.entity.isPassenger()) {
                i = MathHelper.floor(this.entity.getYRot() * 256.0F / 360.0F);
                j = MathHelper.floor(this.entity.getXRot() * 256.0F / 360.0F);
                boolean flag = Math.abs(i - this.yRotp) >= 1 || Math.abs(j - this.xRotp) >= 1;

                if (flag) {
                    this.broadcast.accept(new PacketPlayOutEntity.PacketPlayOutEntityLook(this.entity.getId(), (byte) i, (byte) j, this.entity.isOnGround()));
                    this.yRotp = i;
                    this.xRotp = j;
                }

                this.updateSentPos();
                this.sendDirtyEntityData();
                this.wasRiding = true;
            } else {
                ++this.teleportDelay;
                i = MathHelper.floor(this.entity.getYRot() * 256.0F / 360.0F);
                j = MathHelper.floor(this.entity.getXRot() * 256.0F / 360.0F);
                Vec3D vec3d = this.entity.position().subtract(PacketPlayOutEntity.packetToEntity(this.xp, this.yp, this.zp));
                boolean flag1 = vec3d.lengthSqr() >= 7.62939453125E-6D;
                Packet<?> packet1 = null;
                boolean flag2 = flag1 || this.tickCount % 60 == 0;
                boolean flag3 = Math.abs(i - this.yRotp) >= 1 || Math.abs(j - this.xRotp) >= 1;

                if (this.tickCount > 0 || this.entity instanceof EntityArrow) {
                    long k = PacketPlayOutEntity.entityToPacket(vec3d.x);
                    long l = PacketPlayOutEntity.entityToPacket(vec3d.y);
                    long i1 = PacketPlayOutEntity.entityToPacket(vec3d.z);
                    boolean flag4 = k < -32768L || k > 32767L || l < -32768L || l > 32767L || i1 < -32768L || i1 > 32767L;

                    if (!flag4 && this.teleportDelay <= 400 && !this.wasRiding && this.wasOnGround == this.entity.isOnGround()) {
                        if ((!flag2 || !flag3) && !(this.entity instanceof EntityArrow)) {
                            if (flag2) {
                                packet1 = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(this.entity.getId(), (short) ((int) k), (short) ((int) l), (short) ((int) i1), this.entity.isOnGround());
                            } else if (flag3) {
                                packet1 = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.entity.getId(), (byte) i, (byte) j, this.entity.isOnGround());
                            }
                        } else {
                            packet1 = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(this.entity.getId(), (short) ((int) k), (short) ((int) l), (short) ((int) i1), (byte) i, (byte) j, this.entity.isOnGround());
                        }
                    } else {
                        this.wasOnGround = this.entity.isOnGround();
                        this.teleportDelay = 0;
                        packet1 = new PacketPlayOutEntityTeleport(this.entity);
                    }
                }

                if ((this.trackDelta || this.entity.hasImpulse || this.entity instanceof EntityLiving && ((EntityLiving) this.entity).isFallFlying()) && this.tickCount > 0) {
                    Vec3D vec3d1 = this.entity.getDeltaMovement();
                    double d0 = vec3d1.distanceToSqr(this.ap);

                    if (d0 > 1.0E-7D || d0 > 0.0D && vec3d1.lengthSqr() == 0.0D) {
                        this.ap = vec3d1;
                        this.broadcast.accept(new PacketPlayOutEntityVelocity(this.entity.getId(), this.ap));
                    }
                }

                if (packet1 != null) {
                    this.broadcast.accept(packet1);
                }

                this.sendDirtyEntityData();
                if (flag2) {
                    this.updateSentPos();
                }

                if (flag3) {
                    this.yRotp = i;
                    this.xRotp = j;
                }

                this.wasRiding = false;
            }

            i = MathHelper.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
            if (Math.abs(i - this.yHeadRotp) >= 1) {
                this.broadcast.accept(new PacketPlayOutEntityHeadRotation(this.entity, (byte) i));
                this.yHeadRotp = i;
            }

            this.entity.hasImpulse = false;
        }

        ++this.tickCount;
        if (this.entity.hurtMarked) {
            this.broadcastAndSend(new PacketPlayOutEntityVelocity(this.entity));
            this.entity.hurtMarked = false;
        }

    }

    public void removePairing(EntityPlayer entityplayer) {
        this.entity.stopSeenByPlayer(entityplayer);
        entityplayer.connection.send(new PacketPlayOutEntityDestroy(new int[]{this.entity.getId()}));
    }

    public void addPairing(EntityPlayer entityplayer) {
        PlayerConnection playerconnection = entityplayer.connection;

        Objects.requireNonNull(entityplayer.connection);
        this.sendPairingData(playerconnection::send);
        this.entity.startSeenByPlayer(entityplayer);
    }

    public void sendPairingData(Consumer<Packet<?>> consumer) {
        if (this.entity.isRemoved()) {
            EntityTrackerEntry.LOGGER.warn("Fetching packet for removed entity {}", this.entity);
        }

        Packet<?> packet = this.entity.getAddEntityPacket();

        this.yHeadRotp = MathHelper.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
        consumer.accept(packet);
        if (!this.entity.getEntityData().isEmpty()) {
            consumer.accept(new PacketPlayOutEntityMetadata(this.entity.getId(), this.entity.getEntityData(), true));
        }

        boolean flag = this.trackDelta;

        if (this.entity instanceof EntityLiving) {
            Collection<AttributeModifiable> collection = ((EntityLiving) this.entity).getAttributes().getSyncableAttributes();

            if (!collection.isEmpty()) {
                consumer.accept(new PacketPlayOutUpdateAttributes(this.entity.getId(), collection));
            }

            if (((EntityLiving) this.entity).isFallFlying()) {
                flag = true;
            }
        }

        this.ap = this.entity.getDeltaMovement();
        if (flag && !(packet instanceof PacketPlayOutSpawnEntityLiving)) {
            consumer.accept(new PacketPlayOutEntityVelocity(this.entity.getId(), this.ap));
        }

        if (this.entity instanceof EntityLiving) {
            List<Pair<EnumItemSlot, ItemStack>> list = Lists.newArrayList();
            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
            int i = aenumitemslot.length;

            for (int j = 0; j < i; ++j) {
                EnumItemSlot enumitemslot = aenumitemslot[j];
                ItemStack itemstack = ((EntityLiving) this.entity).getItemBySlot(enumitemslot);

                if (!itemstack.isEmpty()) {
                    list.add(Pair.of(enumitemslot, itemstack.copy()));
                }
            }

            if (!list.isEmpty()) {
                consumer.accept(new PacketPlayOutEntityEquipment(this.entity.getId(), list));
            }
        }

        if (this.entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) this.entity;
            Iterator iterator = entityliving.getActiveEffects().iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                consumer.accept(new PacketPlayOutEntityEffect(this.entity.getId(), mobeffect));
            }
        }

        if (!this.entity.getPassengers().isEmpty()) {
            consumer.accept(new PacketPlayOutMount(this.entity));
        }

        if (this.entity.isPassenger()) {
            consumer.accept(new PacketPlayOutMount(this.entity.getVehicle()));
        }

        if (this.entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) this.entity;

            if (entityinsentient.isLeashed()) {
                consumer.accept(new PacketPlayOutAttachEntity(entityinsentient, entityinsentient.getLeashHolder()));
            }
        }

    }

    private void sendDirtyEntityData() {
        DataWatcher datawatcher = this.entity.getEntityData();

        if (datawatcher.isDirty()) {
            this.broadcastAndSend(new PacketPlayOutEntityMetadata(this.entity.getId(), datawatcher, false));
        }

        if (this.entity instanceof EntityLiving) {
            Set<AttributeModifiable> set = ((EntityLiving) this.entity).getAttributes().getDirtyAttributes();

            if (!set.isEmpty()) {
                this.broadcastAndSend(new PacketPlayOutUpdateAttributes(this.entity.getId(), set));
            }

            set.clear();
        }

    }

    private void updateSentPos() {
        this.xp = PacketPlayOutEntity.entityToPacket(this.entity.getX());
        this.yp = PacketPlayOutEntity.entityToPacket(this.entity.getY());
        this.zp = PacketPlayOutEntity.entityToPacket(this.entity.getZ());
    }

    public Vec3D sentPos() {
        return PacketPlayOutEntity.packetToEntity(this.xp, this.yp, this.zp);
    }

    private void broadcastAndSend(Packet<?> packet) {
        this.broadcast.accept(packet);
        if (this.entity instanceof EntityPlayer) {
            ((EntityPlayer) this.entity).connection.send(packet);
        }

    }
}
