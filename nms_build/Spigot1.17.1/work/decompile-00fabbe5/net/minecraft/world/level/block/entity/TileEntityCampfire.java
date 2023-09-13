package net.minecraft.world.level.block.entity;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.InventoryUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeCampfire;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockCampfire;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityCampfire extends TileEntity implements Clearable {

    private static final int BURN_COOL_SPEED = 2;
    private static final int NUM_SLOTS = 4;
    private final NonNullList<ItemStack> items;
    public final int[] cookingProgress;
    public final int[] cookingTime;

    public TileEntityCampfire(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.CAMPFIRE, blockposition, iblockdata);
        this.items = NonNullList.a(4, ItemStack.EMPTY);
        this.cookingProgress = new int[4];
        this.cookingTime = new int[4];
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityCampfire tileentitycampfire) {
        boolean flag = false;

        for (int i = 0; i < tileentitycampfire.items.size(); ++i) {
            ItemStack itemstack = (ItemStack) tileentitycampfire.items.get(i);

            if (!itemstack.isEmpty()) {
                flag = true;
                int j = tileentitycampfire.cookingProgress[i]++;

                if (tileentitycampfire.cookingProgress[i] >= tileentitycampfire.cookingTime[i]) {
                    InventorySubcontainer inventorysubcontainer = new InventorySubcontainer(new ItemStack[]{itemstack});
                    ItemStack itemstack1 = (ItemStack) world.getCraftingManager().craft(Recipes.CAMPFIRE_COOKING, inventorysubcontainer, world).map((recipecampfire) -> {
                        return recipecampfire.a(inventorysubcontainer);
                    }).orElse(itemstack);

                    InventoryUtils.dropItem(world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), itemstack1);
                    tileentitycampfire.items.set(i, ItemStack.EMPTY);
                    world.notify(blockposition, iblockdata, iblockdata, 3);
                }
            }
        }

        if (flag) {
            a(world, blockposition, iblockdata);
        }

    }

    public static void b(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityCampfire tileentitycampfire) {
        boolean flag = false;

        for (int i = 0; i < tileentitycampfire.items.size(); ++i) {
            if (tileentitycampfire.cookingProgress[i] > 0) {
                flag = true;
                tileentitycampfire.cookingProgress[i] = MathHelper.clamp(tileentitycampfire.cookingProgress[i] - 2, 0, tileentitycampfire.cookingTime[i]);
            }
        }

        if (flag) {
            a(world, blockposition, iblockdata);
        }

    }

    public static void c(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityCampfire tileentitycampfire) {
        Random random = world.random;
        int i;

        if (random.nextFloat() < 0.11F) {
            for (i = 0; i < random.nextInt(2) + 2; ++i) {
                BlockCampfire.a(world, blockposition, (Boolean) iblockdata.get(BlockCampfire.SIGNAL_FIRE), false);
            }
        }

        i = ((EnumDirection) iblockdata.get(BlockCampfire.FACING)).get2DRotationValue();

        for (int j = 0; j < tileentitycampfire.items.size(); ++j) {
            if (!((ItemStack) tileentitycampfire.items.get(j)).isEmpty() && random.nextFloat() < 0.2F) {
                EnumDirection enumdirection = EnumDirection.fromType2(Math.floorMod(j + i, 4));
                float f = 0.3125F;
                double d0 = (double) blockposition.getX() + 0.5D - (double) ((float) enumdirection.getAdjacentX() * 0.3125F) + (double) ((float) enumdirection.g().getAdjacentX() * 0.3125F);
                double d1 = (double) blockposition.getY() + 0.5D;
                double d2 = (double) blockposition.getZ() + 0.5D - (double) ((float) enumdirection.getAdjacentZ() * 0.3125F) + (double) ((float) enumdirection.g().getAdjacentZ() * 0.3125F);

                for (int k = 0; k < 4; ++k) {
                    world.addParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 5.0E-4D, 0.0D);
                }
            }
        }

    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items.clear();
        ContainerUtil.b(nbttagcompound, this.items);
        int[] aint;

        if (nbttagcompound.hasKeyOfType("CookingTimes", 11)) {
            aint = nbttagcompound.getIntArray("CookingTimes");
            System.arraycopy(aint, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, aint.length));
        }

        if (nbttagcompound.hasKeyOfType("CookingTotalTimes", 11)) {
            aint = nbttagcompound.getIntArray("CookingTotalTimes");
            System.arraycopy(aint, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, aint.length));
        }

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        this.c(nbttagcompound);
        nbttagcompound.setIntArray("CookingTimes", this.cookingProgress);
        nbttagcompound.setIntArray("CookingTotalTimes", this.cookingTime);
        return nbttagcompound;
    }

    private NBTTagCompound c(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        ContainerUtil.a(nbttagcompound, this.items, true);
        return nbttagcompound;
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.worldPosition, 13, this.Z_());
    }

    @Override
    public NBTTagCompound Z_() {
        return this.c(new NBTTagCompound());
    }

    public Optional<RecipeCampfire> a(ItemStack itemstack) {
        return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.level.getCraftingManager().craft(Recipes.CAMPFIRE_COOKING, new InventorySubcontainer(new ItemStack[]{itemstack}), this.level);
    }

    public boolean a(ItemStack itemstack, int i) {
        for (int j = 0; j < this.items.size(); ++j) {
            ItemStack itemstack1 = (ItemStack) this.items.get(j);

            if (itemstack1.isEmpty()) {
                this.cookingTime[j] = i;
                this.cookingProgress[j] = 0;
                this.items.set(j, itemstack.cloneAndSubtract(1));
                this.g();
                return true;
            }
        }

        return false;
    }

    private void g() {
        this.update();
        this.getWorld().notify(this.getPosition(), this.getBlock(), this.getBlock(), 3);
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    public void f() {
        if (this.level != null) {
            this.g();
        }

    }
}
