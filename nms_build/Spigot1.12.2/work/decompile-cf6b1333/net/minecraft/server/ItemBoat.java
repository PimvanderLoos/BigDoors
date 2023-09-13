package net.minecraft.server;

import java.util.List;

public class ItemBoat extends Item {

    private final EntityBoat.EnumBoatType a;

    public ItemBoat(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this.a = entityboat_enumboattype;
        this.maxStackSize = 1;
        this.b(CreativeModeTab.e);
        this.c("boat." + entityboat_enumboattype.a());
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        float f = 1.0F;
        float f1 = entityhuman.lastPitch + (entityhuman.pitch - entityhuman.lastPitch) * 1.0F;
        float f2 = entityhuman.lastYaw + (entityhuman.yaw - entityhuman.lastYaw) * 1.0F;
        double d0 = entityhuman.lastX + (entityhuman.locX - entityhuman.lastX) * 1.0D;
        double d1 = entityhuman.lastY + (entityhuman.locY - entityhuman.lastY) * 1.0D + (double) entityhuman.getHeadHeight();
        double d2 = entityhuman.lastZ + (entityhuman.locZ - entityhuman.lastZ) * 1.0D;
        Vec3D vec3d = new Vec3D(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        Vec3D vec3d1 = vec3d.add((double) f7 * 5.0D, (double) f6 * 5.0D, (double) f8 * 5.0D);
        MovingObjectPosition movingobjectposition = world.rayTrace(vec3d, vec3d1, true);

        if (movingobjectposition == null) {
            return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
        } else {
            Vec3D vec3d2 = entityhuman.e(1.0F);
            boolean flag = false;
            List list = world.getEntities(entityhuman, entityhuman.getBoundingBox().b(vec3d2.x * 5.0D, vec3d2.y * 5.0D, vec3d2.z * 5.0D).g(1.0D));

            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (entity.isInteractable()) {
                    AxisAlignedBB axisalignedbb = entity.getBoundingBox().g((double) entity.aI());

                    if (axisalignedbb.b(vec3d)) {
                        flag = true;
                    }
                }
            }

            if (flag) {
                return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
            } else if (movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
            } else {
                Block block = world.getType(movingobjectposition.a()).getBlock();
                boolean flag1 = block == Blocks.WATER || block == Blocks.FLOWING_WATER;
                EntityBoat entityboat = new EntityBoat(world, movingobjectposition.pos.x, flag1 ? movingobjectposition.pos.y - 0.12D : movingobjectposition.pos.y, movingobjectposition.pos.z);

                entityboat.setType(this.a);
                entityboat.yaw = entityhuman.yaw;
                if (!world.getCubes(entityboat, entityboat.getBoundingBox().g(-0.1D)).isEmpty()) {
                    return new InteractionResultWrapper(EnumInteractionResult.FAIL, itemstack);
                } else {
                    if (!world.isClientSide) {
                        world.addEntity(entityboat);
                    }

                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    entityhuman.b(StatisticList.b((Item) this));
                    return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
                }
            }
        }
    }
}
