package net.minecraft.server;

import com.mojang.serialization.DataResult;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemCompass extends Item implements ItemVanishable {

    private static final Logger LOGGER = LogManager.getLogger();

    public ItemCompass(Item.Info item_info) {
        super(item_info);
    }

    public static boolean d(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null && (nbttagcompound.hasKey("LodestoneDimension") || nbttagcompound.hasKey("LodestonePos"));
    }

    @Override
    public boolean e(ItemStack itemstack) {
        return d(itemstack) || super.e(itemstack);
    }

    public static Optional<ResourceKey<World>> a(NBTTagCompound nbttagcompound) {
        return World.f.parse(DynamicOpsNBT.a, nbttagcompound.get("LodestoneDimension")).result();
    }

    @Override
    public void a(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        if (!world.isClientSide) {
            if (d(itemstack)) {
                NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

                if (nbttagcompound.hasKey("LodestoneTracked") && !nbttagcompound.getBoolean("LodestoneTracked")) {
                    return;
                }

                Optional<ResourceKey<World>> optional = a(nbttagcompound);

                if (optional.isPresent() && optional.get() == world.getDimensionKey() && nbttagcompound.hasKey("LodestonePos") && !((WorldServer) world).y().a(VillagePlaceType.w, GameProfileSerializer.b(nbttagcompound.getCompound("LodestonePos")))) {
                    nbttagcompound.remove("LodestonePos");
                }
            }

        }
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        World world = itemactioncontext.getWorld();

        if (!world.getType(blockposition).a(Blocks.LODESTONE)) {
            return super.a(itemactioncontext);
        } else {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            EntityHuman entityhuman = itemactioncontext.getEntity();
            ItemStack itemstack = itemactioncontext.getItemStack();
            boolean flag = !entityhuman.abilities.canInstantlyBuild && itemstack.getCount() == 1;

            if (flag) {
                this.a(world.getDimensionKey(), blockposition, itemstack.getOrCreateTag());
            } else {
                ItemStack itemstack1 = new ItemStack(Items.COMPASS, 1);
                NBTTagCompound nbttagcompound = itemstack.hasTag() ? itemstack.getTag().clone() : new NBTTagCompound();

                itemstack1.setTag(nbttagcompound);
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemstack.subtract(1);
                }

                this.a(world.getDimensionKey(), blockposition, nbttagcompound);
                if (!entityhuman.inventory.pickup(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }
            }

            return EnumInteractionResult.a(world.isClientSide);
        }
    }

    private void a(ResourceKey<World> resourcekey, BlockPosition blockposition, NBTTagCompound nbttagcompound) {
        nbttagcompound.set("LodestonePos", GameProfileSerializer.a(blockposition));
        DataResult dataresult = World.f.encodeStart(DynamicOpsNBT.a, resourcekey);
        Logger logger = ItemCompass.LOGGER;

        logger.getClass();
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.set("LodestoneDimension", nbtbase);
        });
        nbttagcompound.setBoolean("LodestoneTracked", true);
    }

    @Override
    public String f(ItemStack itemstack) {
        return d(itemstack) ? "item.minecraft.lodestone_compass" : super.f(itemstack);
    }
}
