package net.minecraft.world.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

public class ItemCompass extends Item implements ItemVanishable {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String TAG_LODESTONE_POS = "LodestonePos";
    public static final String TAG_LODESTONE_DIMENSION = "LodestoneDimension";
    public static final String TAG_LODESTONE_TRACKED = "LodestoneTracked";

    public ItemCompass(Item.Info item_info) {
        super(item_info);
    }

    public static boolean isLodestoneCompass(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null && (nbttagcompound.contains("LodestoneDimension") || nbttagcompound.contains("LodestonePos"));
    }

    private static Optional<ResourceKey<World>> getLodestoneDimension(NBTTagCompound nbttagcompound) {
        return World.RESOURCE_KEY_CODEC.parse(DynamicOpsNBT.INSTANCE, nbttagcompound.get("LodestoneDimension")).result();
    }

    @Nullable
    public static GlobalPos getLodestonePosition(NBTTagCompound nbttagcompound) {
        boolean flag = nbttagcompound.contains("LodestonePos");
        boolean flag1 = nbttagcompound.contains("LodestoneDimension");

        if (flag && flag1) {
            Optional<ResourceKey<World>> optional = getLodestoneDimension(nbttagcompound);

            if (optional.isPresent()) {
                BlockPosition blockposition = GameProfileSerializer.readBlockPos(nbttagcompound.getCompound("LodestonePos"));

                return GlobalPos.of((ResourceKey) optional.get(), blockposition);
            }
        }

        return null;
    }

    @Nullable
    public static GlobalPos getSpawnPosition(World world) {
        return world.dimensionType().natural() ? GlobalPos.of(world.dimension(), world.getSharedSpawnPos()) : null;
    }

    @Override
    public boolean isFoil(ItemStack itemstack) {
        return isLodestoneCompass(itemstack) || super.isFoil(itemstack);
    }

    @Override
    public void inventoryTick(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        if (!world.isClientSide) {
            if (isLodestoneCompass(itemstack)) {
                NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

                if (nbttagcompound.contains("LodestoneTracked") && !nbttagcompound.getBoolean("LodestoneTracked")) {
                    return;
                }

                Optional<ResourceKey<World>> optional = getLodestoneDimension(nbttagcompound);

                if (optional.isPresent() && optional.get() == world.dimension() && nbttagcompound.contains("LodestonePos")) {
                    BlockPosition blockposition = GameProfileSerializer.readBlockPos(nbttagcompound.getCompound("LodestonePos"));

                    if (!world.isInWorldBounds(blockposition) || !((WorldServer) world).getPoiManager().existsAtPosition(PoiTypes.LODESTONE, blockposition)) {
                        nbttagcompound.remove("LodestonePos");
                    }
                }
            }

        }
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        World world = itemactioncontext.getLevel();

        if (!world.getBlockState(blockposition).is(Blocks.LODESTONE)) {
            return super.useOn(itemactioncontext);
        } else {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            EntityHuman entityhuman = itemactioncontext.getPlayer();
            ItemStack itemstack = itemactioncontext.getItemInHand();
            boolean flag = !entityhuman.getAbilities().instabuild && itemstack.getCount() == 1;

            if (flag) {
                this.addLodestoneTags(world.dimension(), blockposition, itemstack.getOrCreateTag());
            } else {
                ItemStack itemstack1 = new ItemStack(Items.COMPASS, 1);
                NBTTagCompound nbttagcompound = itemstack.hasTag() ? itemstack.getTag().copy() : new NBTTagCompound();

                itemstack1.setTag(nbttagcompound);
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.addLodestoneTags(world.dimension(), blockposition, nbttagcompound);
                if (!entityhuman.getInventory().add(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }
            }

            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        }
    }

    private void addLodestoneTags(ResourceKey<World> resourcekey, BlockPosition blockposition, NBTTagCompound nbttagcompound) {
        nbttagcompound.put("LodestonePos", GameProfileSerializer.writeBlockPos(blockposition));
        DataResult dataresult = World.RESOURCE_KEY_CODEC.encodeStart(DynamicOpsNBT.INSTANCE, resourcekey);
        Logger logger = ItemCompass.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("LodestoneDimension", nbtbase);
        });
        nbttagcompound.putBoolean("LodestoneTracked", true);
    }

    @Override
    public String getDescriptionId(ItemStack itemstack) {
        return isLodestoneCompass(itemstack) ? "item.minecraft.lodestone_compass" : super.getDescriptionId(itemstack);
    }
}
