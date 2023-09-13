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
    private Raid b;

    public BehaviorCelebrate(int i, int j) {
        super(ImmutableMap.of(), i, j);
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        BlockPosition blockposition = entityvillager.getChunkCoordinates();

        this.b = worldserver.b_(blockposition);
        return this.b != null && this.b.isVictory() && BehaviorOutside.a(worldserver, entityvillager, blockposition);
    }

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.b != null && !this.b.isStopped();
    }

    protected void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.b = null;
        entityvillager.getBehaviorController().a(worldserver.getDayTime(), worldserver.getTime());
    }

    protected void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        Random random = entityvillager.getRandom();

        if (random.nextInt(100) == 0) {
            entityvillager.eR();
        }

        if (random.nextInt(200) == 0 && BehaviorOutside.a(worldserver, entityvillager, entityvillager.getChunkCoordinates())) {
            EnumColor enumcolor = (EnumColor) SystemUtils.a((Object[]) EnumColor.values(), random);
            int j = random.nextInt(3);
            ItemStack itemstack = this.a(enumcolor, j);
            EntityFireworks entityfireworks = new EntityFireworks(entityvillager.world, entityvillager, entityvillager.locX(), entityvillager.getHeadY(), entityvillager.locZ(), itemstack);

            entityvillager.world.addEntity(entityfireworks);
        }

    }

    private ItemStack a(EnumColor enumcolor, int i) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack itemstack1 = new ItemStack(Items.FIREWORK_STAR);
        NBTTagCompound nbttagcompound = itemstack1.a("Explosion");
        List<Integer> list = Lists.newArrayList();

        list.add(enumcolor.getFireworksColor());
        nbttagcompound.b("Colors", (List) list);
        nbttagcompound.setByte("Type", (byte) ItemFireworks.EffectType.BURST.a());
        NBTTagCompound nbttagcompound1 = itemstack.a("Fireworks");
        NBTTagList nbttaglist = new NBTTagList();
        NBTTagCompound nbttagcompound2 = itemstack1.b("Explosion");

        if (nbttagcompound2 != null) {
            nbttaglist.add(nbttagcompound2);
        }

        nbttagcompound1.setByte("Flight", (byte) i);
        if (!nbttaglist.isEmpty()) {
            nbttagcompound1.set("Explosions", nbttaglist);
        }

        return itemstack;
    }
}
