package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.projectile.EntityFireworks;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemFireworks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BehaviorCelebrate extends Behavior<EntityVillager> {

    @Nullable
    private Raid currentRaid;

    public BehaviorCelebrate(int i, int j) {
        super(ImmutableMap.of(), i, j);
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        BlockPosition blockposition = entityvillager.blockPosition();

        this.currentRaid = worldserver.getRaidAt(blockposition);
        return this.currentRaid != null && this.currentRaid.isVictory() && BehaviorOutside.hasNoBlocksAbove(worldserver, entityvillager, blockposition);
    }

    protected boolean canStillUse(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.currentRaid != null && !this.currentRaid.isStopped();
    }

    protected void stop(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.currentRaid = null;
        entityvillager.getBrain().updateActivityFromSchedule(worldserver.getDayTime(), worldserver.getGameTime());
    }

    protected void tick(WorldServer worldserver, EntityVillager entityvillager, long i) {
        Random random = entityvillager.getRandom();

        if (random.nextInt(100) == 0) {
            entityvillager.playCelebrateSound();
        }

        if (random.nextInt(200) == 0 && BehaviorOutside.hasNoBlocksAbove(worldserver, entityvillager, entityvillager.blockPosition())) {
            EnumColor enumcolor = (EnumColor) SystemUtils.getRandom((Object[]) EnumColor.values(), random);
            int j = random.nextInt(3);
            ItemStack itemstack = this.getFirework(enumcolor, j);
            EntityFireworks entityfireworks = new EntityFireworks(entityvillager.level, entityvillager, entityvillager.getX(), entityvillager.getEyeY(), entityvillager.getZ(), itemstack);

            entityvillager.level.addFreshEntity(entityfireworks);
        }

    }

    private ItemStack getFirework(EnumColor enumcolor, int i) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack itemstack1 = new ItemStack(Items.FIREWORK_STAR);
        NBTTagCompound nbttagcompound = itemstack1.getOrCreateTagElement("Explosion");
        List<Integer> list = Lists.newArrayList();

        list.add(enumcolor.getFireworkColor());
        nbttagcompound.putIntArray("Colors", (List) list);
        nbttagcompound.putByte("Type", (byte) ItemFireworks.EffectType.BURST.getId());
        NBTTagCompound nbttagcompound1 = itemstack.getOrCreateTagElement("Fireworks");
        NBTTagList nbttaglist = new NBTTagList();
        NBTTagCompound nbttagcompound2 = itemstack1.getTagElement("Explosion");

        if (nbttagcompound2 != null) {
            nbttaglist.add(nbttagcompound2);
        }

        nbttagcompound1.putByte("Flight", (byte) i);
        if (!nbttaglist.isEmpty()) {
            nbttagcompound1.put("Explosions", nbttaglist);
        }

        return itemstack;
    }
}
