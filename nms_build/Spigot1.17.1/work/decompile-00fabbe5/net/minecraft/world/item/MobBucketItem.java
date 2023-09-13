package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.EntityTropicalFish;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidType;

public class MobBucketItem extends ItemBucket {

    private final EntityTypes<?> type;
    private final SoundEffect emptySound;

    public MobBucketItem(EntityTypes<?> entitytypes, FluidType fluidtype, SoundEffect soundeffect, Item.Info item_info) {
        super(fluidtype, item_info);
        this.type = entitytypes;
        this.emptySound = soundeffect;
    }

    @Override
    public void a(@Nullable EntityHuman entityhuman, World world, ItemStack itemstack, BlockPosition blockposition) {
        if (world instanceof WorldServer) {
            this.a((WorldServer) world, itemstack, blockposition);
            world.a((Entity) entityhuman, GameEvent.ENTITY_PLACE, blockposition);
        }

    }

    @Override
    protected void a(@Nullable EntityHuman entityhuman, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.playSound(entityhuman, blockposition, this.emptySound, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    private void a(WorldServer worldserver, ItemStack itemstack, BlockPosition blockposition) {
        Entity entity = this.type.spawnCreature(worldserver, itemstack, (EntityHuman) null, blockposition, EnumMobSpawn.BUCKET, true, false);

        if (entity instanceof Bucketable) {
            Bucketable bucketable = (Bucketable) entity;

            bucketable.c(itemstack.getOrCreateTag());
            bucketable.setFromBucket(true);
        }

    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        if (this.type == EntityTypes.TROPICAL_FISH) {
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound != null && nbttagcompound.hasKeyOfType("BucketVariantTag", 3)) {
                int i = nbttagcompound.getInt("BucketVariantTag");
                EnumChatFormat[] aenumchatformat = new EnumChatFormat[]{EnumChatFormat.ITALIC, EnumChatFormat.GRAY};
                String s = "color.minecraft." + EntityTropicalFish.t(i);
                String s1 = "color.minecraft." + EntityTropicalFish.u(i);

                for (int j = 0; j < EntityTropicalFish.COMMON_VARIANTS.length; ++j) {
                    if (i == EntityTropicalFish.COMMON_VARIANTS[j]) {
                        list.add((new ChatMessage(EntityTropicalFish.b(j))).a(aenumchatformat));
                        return;
                    }
                }

                list.add((new ChatMessage(EntityTropicalFish.v(i))).a(aenumchatformat));
                ChatMessage chatmessage = new ChatMessage(s);

                if (!s.equals(s1)) {
                    chatmessage.c(", ").addSibling(new ChatMessage(s1));
                }

                chatmessage.a(aenumchatformat);
                list.add(chatmessage);
            }
        }

    }
}
