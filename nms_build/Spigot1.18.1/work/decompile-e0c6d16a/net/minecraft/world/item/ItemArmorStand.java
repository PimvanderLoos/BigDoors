package net.minecraft.world.item;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Vector3f;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class ItemArmorStand extends Item {

    public ItemArmorStand(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        EnumDirection enumdirection = itemactioncontext.getClickedFace();

        if (enumdirection == EnumDirection.DOWN) {
            return EnumInteractionResult.FAIL;
        } else {
            World world = itemactioncontext.getLevel();
            BlockActionContext blockactioncontext = new BlockActionContext(itemactioncontext);
            BlockPosition blockposition = blockactioncontext.getClickedPos();
            ItemStack itemstack = itemactioncontext.getItemInHand();
            Vec3D vec3d = Vec3D.atBottomCenterOf(blockposition);
            AxisAlignedBB axisalignedbb = EntityTypes.ARMOR_STAND.getDimensions().makeBoundingBox(vec3d.x(), vec3d.y(), vec3d.z());

            if (world.noCollision((Entity) null, axisalignedbb) && world.getEntities((Entity) null, axisalignedbb).isEmpty()) {
                if (world instanceof WorldServer) {
                    WorldServer worldserver = (WorldServer) world;
                    EntityArmorStand entityarmorstand = (EntityArmorStand) EntityTypes.ARMOR_STAND.create(worldserver, itemstack.getTag(), (IChatBaseComponent) null, itemactioncontext.getPlayer(), blockposition, EnumMobSpawn.SPAWN_EGG, true, true);

                    if (entityarmorstand == null) {
                        return EnumInteractionResult.FAIL;
                    }

                    float f = (float) MathHelper.floor((MathHelper.wrapDegrees(itemactioncontext.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;

                    entityarmorstand.moveTo(entityarmorstand.getX(), entityarmorstand.getY(), entityarmorstand.getZ(), f, 0.0F);
                    this.randomizePose(entityarmorstand, world.random);
                    worldserver.addFreshEntityWithPassengers(entityarmorstand);
                    world.playSound((EntityHuman) null, entityarmorstand.getX(), entityarmorstand.getY(), entityarmorstand.getZ(), SoundEffects.ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
                    world.gameEvent(itemactioncontext.getPlayer(), GameEvent.ENTITY_PLACE, (Entity) entityarmorstand);
                }

                itemstack.shrink(1);
                return EnumInteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return EnumInteractionResult.FAIL;
            }
        }
    }

    private void randomizePose(EntityArmorStand entityarmorstand, Random random) {
        Vector3f vector3f = entityarmorstand.getHeadPose();
        float f = random.nextFloat() * 5.0F;
        float f1 = random.nextFloat() * 20.0F - 10.0F;
        Vector3f vector3f1 = new Vector3f(vector3f.getX() + f, vector3f.getY() + f1, vector3f.getZ());

        entityarmorstand.setHeadPose(vector3f1);
        vector3f = entityarmorstand.getBodyPose();
        f = random.nextFloat() * 10.0F - 5.0F;
        vector3f1 = new Vector3f(vector3f.getX(), vector3f.getY() + f, vector3f.getZ());
        entityarmorstand.setBodyPose(vector3f1);
    }
}
