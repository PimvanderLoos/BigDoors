package net.minecraft.world.level.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.MathHelper;
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
    private static final Random RANDOM = new Random();
    private IChatBaseComponent name;

    public TileEntityEnchantTable(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.ENCHANTING_TABLE, blockposition, iblockdata);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(this.name));
        }

        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.name = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEnchantTable tileentityenchanttable) {
        tileentityenchanttable.oOpen = tileentityenchanttable.open;
        tileentityenchanttable.oRot = tileentityenchanttable.rot;
        EntityHuman entityhuman = world.a((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, 3.0D, false);

        if (entityhuman != null) {
            double d0 = entityhuman.locX() - ((double) blockposition.getX() + 0.5D);
            double d1 = entityhuman.locZ() - ((double) blockposition.getZ() + 0.5D);

            tileentityenchanttable.tRot = (float) MathHelper.d(d1, d0);
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
        tileentityenchanttable.open = MathHelper.a(tileentityenchanttable.open, 0.0F, 1.0F);
        ++tileentityenchanttable.time;
        tileentityenchanttable.oFlip = tileentityenchanttable.flip;
        float f2 = (tileentityenchanttable.flipT - tileentityenchanttable.flip) * 0.4F;
        float f3 = 0.2F;

        f2 = MathHelper.a(f2, -0.2F, 0.2F);
        tileentityenchanttable.flipA += (f2 - tileentityenchanttable.flipA) * 0.9F;
        tileentityenchanttable.flip += tileentityenchanttable.flipA;
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        return (IChatBaseComponent) (this.name != null ? this.name : new ChatMessage("container.enchant"));
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
