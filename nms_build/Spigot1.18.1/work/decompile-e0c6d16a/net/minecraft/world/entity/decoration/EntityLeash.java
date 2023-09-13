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
        this.setPos((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setPosRaw((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.375D, (double) this.pos.getZ() + 0.5D);
        double d0 = (double) this.getType().getWidth() / 2.0D;
        double d1 = (double) this.getType().getHeight();

        this.setBoundingBox(new AxisAlignedBB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0, this.getY() + d1, this.getZ() + d0));
    }

    @Override
    public void setDirection(EnumDirection enumdirection) {}

    @Override
    public int getWidth() {
        return 9;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    protected float getEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.0625F;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d0) {
        return d0 < 1024.0D;
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        this.playSound(SoundEffects.LEASH_KNOT_BREAK, 1.0F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {}

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {}

    @Override
    public EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        if (this.level.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            boolean flag = false;
            double d0 = 7.0D;
            List<EntityInsentient> list = this.level.getEntitiesOfClass(EntityInsentient.class, new AxisAlignedBB(this.getX() - 7.0D, this.getY() - 7.0D, this.getZ() - 7.0D, this.getX() + 7.0D, this.getY() + 7.0D, this.getZ() + 7.0D));
            Iterator iterator = list.iterator();

            EntityInsentient entityinsentient;

            while (iterator.hasNext()) {
                entityinsentient = (EntityInsentient) iterator.next();
                if (entityinsentient.getLeashHolder() == entityhuman) {
                    entityinsentient.setLeashedTo(this, true);
                    flag = true;
                }
            }

            if (!flag) {
                this.discard();
                if (entityhuman.getAbilities().instabuild) {
                    iterator = list.iterator();

                    while (iterator.hasNext()) {
                        entityinsentient = (EntityInsentient) iterator.next();
                        if (entityinsentient.isLeashed() && entityinsentient.getLeashHolder() == this) {
                            entityinsentient.dropLeash(true, false);
                        }
                    }
                }
            }

            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public boolean survives() {
        return this.level.getBlockState(this.pos).is((Tag) TagsBlock.FENCES);
    }

    public static EntityLeash getOrCreateKnot(World world, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        List<EntityLeash> list = world.getEntitiesOfClass(EntityLeash.class, new AxisAlignedBB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D));
        Iterator iterator = list.iterator();

        EntityLeash entityleash;

        do {
            if (!iterator.hasNext()) {
                EntityLeash entityleash1 = new EntityLeash(world, blockposition);

                world.addFreshEntity(entityleash1);
                return entityleash1;
            }

            entityleash = (EntityLeash) iterator.next();
        } while (!entityleash.getPos().equals(blockposition));

        return entityleash;
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEffects.LEASH_KNOT_PLACE, 1.0F, 1.0F);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new PacketPlayOutSpawnEntity(this, this.getType(), 0, this.getPos());
    }

    @Override
    public Vec3D getRopeHoldPosition(float f) {
        return this.getPosition(f).add(0.0D, 0.2D, 0.0D);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.LEAD);
    }
}
