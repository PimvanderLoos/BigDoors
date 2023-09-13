package net.minecraft.world.entity.decoration;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityLeash extends EntityHanging {

    public static final double OFFSET_Y = 0.375D;

    public EntityLeash(EntityTypes<? extends EntityLeash> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityLeash(World world, BlockPosition blockposition) {
        super(EntityTypes.LEASH_KNOT, world, blockposition);
        this.setPosition((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
    }

    @Override
    protected void updateBoundingBox() {
        this.setPositionRaw((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.375D, (double) this.pos.getZ() + 0.5D);
        double d0 = (double) this.getEntityType().k() / 2.0D;
        double d1 = (double) this.getEntityType().l();

        this.a(new AxisAlignedBB(this.locX() - d0, this.locY(), this.locZ() - d0, this.locX() + d0, this.locY() + d1, this.locZ() + d0));
    }

    @Override
    public void setDirection(EnumDirection enumdirection) {}

    @Override
    public int getHangingWidth() {
        return 9;
    }

    @Override
    public int getHangingHeight() {
        return 9;
    }

    @Override
    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.0625F;
    }

    @Override
    public boolean a(double d0) {
        return d0 < 1024.0D;
    }

    @Override
    public void a(@Nullable Entity entity) {
        this.playSound(SoundEffects.LEASH_KNOT_BREAK, 1.0F, 1.0F);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {}

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {}

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        if (this.level.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            boolean flag = false;
            double d0 = 7.0D;
            List<EntityInsentient> list = this.level.a(EntityInsentient.class, new AxisAlignedBB(this.locX() - 7.0D, this.locY() - 7.0D, this.locZ() - 7.0D, this.locX() + 7.0D, this.locY() + 7.0D, this.locZ() + 7.0D));
            Iterator iterator = list.iterator();

            EntityInsentient entityinsentient;

            while (iterator.hasNext()) {
                entityinsentient = (EntityInsentient) iterator.next();
                if (entityinsentient.getLeashHolder() == entityhuman) {
                    entityinsentient.setLeashHolder(this, true);
                    flag = true;
                }
            }

            if (!flag) {
                this.die();
                if (entityhuman.getAbilities().instabuild) {
                    iterator = list.iterator();

                    while (iterator.hasNext()) {
                        entityinsentient = (EntityInsentient) iterator.next();
                        if (entityinsentient.isLeashed() && entityinsentient.getLeashHolder() == this) {
                            entityinsentient.unleash(true, false);
                        }
                    }
                }
            }

            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public boolean survives() {
        return this.level.getType(this.pos).a((Tag) TagsBlock.FENCES);
    }

    public static EntityLeash b(World world, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        List<EntityLeash> list = world.a(EntityLeash.class, new AxisAlignedBB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D));
        Iterator iterator = list.iterator();

        EntityLeash entityleash;

        do {
            if (!iterator.hasNext()) {
                EntityLeash entityleash1 = new EntityLeash(world, blockposition);

                world.addEntity(entityleash1);
                return entityleash1;
            }

            entityleash = (EntityLeash) iterator.next();
        } while (!entityleash.getBlockPosition().equals(blockposition));

        return entityleash;
    }

    @Override
    public void playPlaceSound() {
        this.playSound(SoundEffects.LEASH_KNOT_PLACE, 1.0F, 1.0F);
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntity(this, this.getEntityType(), 0, this.getBlockPosition());
    }

    @Override
    public Vec3D n(float f) {
        return this.k(f).add(0.0D, 0.2D, 0.0D);
    }

    @Override
    public ItemStack df() {
        return new ItemStack(Items.LEAD);
    }
}
