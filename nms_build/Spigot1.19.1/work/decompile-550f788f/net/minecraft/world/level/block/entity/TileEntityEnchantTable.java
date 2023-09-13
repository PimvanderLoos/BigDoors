package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.INamableTileEntity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityEnchantTable extends TileEntity implements INamableTileEntity {

    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    public float rot;
    public float oRot;
    public float tRot;
    private static final RandomSource RANDOM = RandomSource.create();
    private IChatBaseComponent name;

    public TileEntityEnchantTable(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.ENCHANTING_TABLE, blockposition, iblockdata);
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (this.hasCustomName()) {
            nbttagcompound.putString("CustomName", IChatBaseComponent.ChatSerializer.toJson(this.name));
        }

    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.contains("CustomName", 8)) {
            this.name = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("CustomName"));
        }

    }

    public static void bookAnimationTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEnchantTable tileentityenchanttable) {
        tileentityenchanttable.oOpen = tileentityenchanttable.open;
        tileentityenchanttable.oRot = tileentityenchanttable.rot;
        EntityHuman entityhuman = world.getNearestPlayer((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, 3.0D, false);

        if (entityhuman != null) {
            double d0 = entityhuman.getX() - ((double) blockposition.getX() + 0.5D);
            double d1 = entityhuman.getZ() - ((double) blockposition.getZ() + 0.5D);

            tileentityenchanttable.tRot = (float) MathHelper.atan2(d1, d0);
            tileentityenchanttable.open += 0.1F;
            if (tileentityenchanttable.open < 0.5F || TileEntityEnchantTable.RANDOM.nextInt(40) == 0) {
                float f = tileentityenchanttable.flipT;

                do {
                    tileentityenchanttable.flipT += (float) (TileEntityEnchantTable.RANDOM.nextInt(4) - TileEntityEnchantTable.RANDOM.nextInt(4));
                } while (f == tileentityenchanttable.flipT);
            }
        } else {
            tileentityenchanttable.tRot += 0.02F;
            tileentityenchanttable.open -= 0.1F;
        }

        while (tileentityenchanttable.rot >= 3.1415927F) {
            tileentityenchanttable.rot -= 6.2831855F;
        }

        while (tileentityenchanttable.rot < -3.1415927F) {
            tileentityenchanttable.rot += 6.2831855F;
        }

        while (tileentityenchanttable.tRot >= 3.1415927F) {
            tileentityenchanttable.tRot -= 6.2831855F;
        }

        while (tileentityenchanttable.tRot < -3.1415927F) {
            tileentityenchanttable.tRot += 6.2831855F;
        }

        float f1;

        for (f1 = tileentityenchanttable.tRot - tileentityenchanttable.rot; f1 >= 3.1415927F; f1 -= 6.2831855F) {
            ;
        }

        while (f1 < -3.1415927F) {
            f1 += 6.2831855F;
        }

        tileentityenchanttable.rot += f1 * 0.4F;
        tileentityenchanttable.open = MathHelper.clamp(tileentityenchanttable.open, 0.0F, 1.0F);
        ++tileentityenchanttable.time;
        tileentityenchanttable.oFlip = tileentityenchanttable.flip;
        float f2 = (tileentityenchanttable.flipT - tileentityenchanttable.flip) * 0.4F;
        float f3 = 0.2F;

        f2 = MathHelper.clamp(f2, -0.2F, 0.2F);
        tileentityenchanttable.flipA += (f2 - tileentityenchanttable.flipA) * 0.9F;
        tileentityenchanttable.flip += tileentityenchanttable.flipA;
    }

    @Override
    public IChatBaseComponent getName() {
        return (IChatBaseComponent) (this.name != null ? this.name : IChatBaseComponent.translatable("container.enchant"));
    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.name = ichatbasecomponent;
    }

    @Nullable
    @Override
    public IChatBaseComponent getCustomName() {
        return this.name;
    }
}
