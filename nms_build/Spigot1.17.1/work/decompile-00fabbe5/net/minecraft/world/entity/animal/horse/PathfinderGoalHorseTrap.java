package net.minecraft.world.entity.animal.horse;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;

public class PathfinderGoalHorseTrap extends PathfinderGoal {

    private final EntityHorseSkeleton horse;

    public PathfinderGoalHorseTrap(EntityHorseSkeleton entityhorseskeleton) {
        this.horse = entityhorseskeleton;
    }

    @Override
    public boolean a() {
        return this.horse.level.isPlayerNearby(this.horse.locX(), this.horse.locY(), this.horse.locZ(), 10.0D);
    }

    @Override
    public void e() {
        WorldServer worldserver = (WorldServer) this.horse.level;
        DifficultyDamageScaler difficultydamagescaler = worldserver.getDamageScaler(this.horse.getChunkCoordinates());

        this.horse.v(false);
        this.horse.setTamed(true);
        this.horse.setAgeRaw(0);
        EntityLightning entitylightning = (EntityLightning) EntityTypes.LIGHTNING_BOLT.a((World) worldserver);

        entitylightning.teleportAndSync(this.horse.locX(), this.horse.locY(), this.horse.locZ());
        entitylightning.setEffect(true);
        worldserver.addEntity(entitylightning);
        EntitySkeleton entityskeleton = this.a(difficultydamagescaler, this.horse);

        entityskeleton.startRiding(this.horse);
        worldserver.addAllEntities(entityskeleton);

        for (int i = 0; i < 3; ++i) {
            EntityHorseAbstract entityhorseabstract = this.a(difficultydamagescaler);
            EntitySkeleton entityskeleton1 = this.a(difficultydamagescaler, entityhorseabstract);

            entityskeleton1.startRiding(entityhorseabstract);
            entityhorseabstract.i(this.horse.getRandom().nextGaussian() * 0.5D, 0.0D, this.horse.getRandom().nextGaussian() * 0.5D);
            worldserver.addAllEntities(entityhorseabstract);
        }

    }

    private EntityHorseAbstract a(DifficultyDamageScaler difficultydamagescaler) {
        EntityHorseSkeleton entityhorseskeleton = (EntityHorseSkeleton) EntityTypes.SKELETON_HORSE.a(this.horse.level);

        entityhorseskeleton.prepare((WorldServer) this.horse.level, difficultydamagescaler, EnumMobSpawn.TRIGGERED, (GroupDataEntity) null, (NBTTagCompound) null);
        entityhorseskeleton.setPosition(this.horse.locX(), this.horse.locY(), this.horse.locZ());
        entityhorseskeleton.invulnerableTime = 60;
        entityhorseskeleton.setPersistent();
        entityhorseskeleton.setTamed(true);
        entityhorseskeleton.setAgeRaw(0);
        return entityhorseskeleton;
    }

    private EntitySkeleton a(DifficultyDamageScaler difficultydamagescaler, EntityHorseAbstract entityhorseabstract) {
        EntitySkeleton entityskeleton = (EntitySkeleton) EntityTypes.SKELETON.a(entityhorseabstract.level);

        entityskeleton.prepare((WorldServer) entityhorseabstract.level, difficultydamagescaler, EnumMobSpawn.TRIGGERED, (GroupDataEntity) null, (NBTTagCompound) null);
        entityskeleton.setPosition(entityhorseabstract.locX(), entityhorseabstract.locY(), entityhorseabstract.locZ());
        entityskeleton.invulnerableTime = 60;
        entityskeleton.setPersistent();
        if (entityskeleton.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
            entityskeleton.setSlot(EnumItemSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        }

        entityskeleton.setSlot(EnumItemSlot.MAINHAND, EnchantmentManager.a(entityskeleton.getRandom(), this.a(entityskeleton.getItemInMainHand()), (int) (5.0F + difficultydamagescaler.d() * (float) entityskeleton.getRandom().nextInt(18)), false));
        entityskeleton.setSlot(EnumItemSlot.HEAD, EnchantmentManager.a(entityskeleton.getRandom(), this.a(entityskeleton.getEquipment(EnumItemSlot.HEAD)), (int) (5.0F + difficultydamagescaler.d() * (float) entityskeleton.getRandom().nextInt(18)), false));
        return entityskeleton;
    }

    private ItemStack a(ItemStack itemstack) {
        itemstack.removeTag("Enchantments");
        return itemstack;
    }
}
