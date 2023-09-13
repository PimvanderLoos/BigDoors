package net.minecraft.world.item.trading;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.item.ItemStack;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {

    public MerchantRecipeList() {}

    public MerchantRecipeList(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = nbttagcompound.getList("Recipes", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            this.add(new MerchantRecipe(nbttaglist.getCompound(i)));
        }

    }

    @Nullable
    public MerchantRecipe getRecipeFor(ItemStack itemstack, ItemStack itemstack1, int i) {
        if (i > 0 && i < this.size()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) this.get(i);

            return merchantrecipe.satisfiedBy(itemstack, itemstack1) ? merchantrecipe : null;
        } else {
            for (int j = 0; j < this.size(); ++j) {
                MerchantRecipe merchantrecipe1 = (MerchantRecipe) this.get(j);

                if (merchantrecipe1.satisfiedBy(itemstack, itemstack1)) {
                    return merchantrecipe1;
                }
            }

            return null;
        }
    }

    public void writeToStream(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte((byte) (this.size() & 255));

        for (int i = 0; i < this.size(); ++i) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) this.get(i);

            packetdataserializer.writeItem(merchantrecipe.getBaseCostA());
            packetdataserializer.writeItem(merchantrecipe.getResult());
            ItemStack itemstack = merchantrecipe.getCostB();

            packetdataserializer.writeBoolean(!itemstack.isEmpty());
            if (!itemstack.isEmpty()) {
                packetdataserializer.writeItem(itemstack);
            }

            packetdataserializer.writeBoolean(merchantrecipe.isOutOfStock());
            packetdataserializer.writeInt(merchantrecipe.getUses());
            packetdataserializer.writeInt(merchantrecipe.getMaxUses());
            packetdataserializer.writeInt(merchantrecipe.getXp());
            packetdataserializer.writeInt(merchantrecipe.getSpecialPriceDiff());
            packetdataserializer.writeFloat(merchantrecipe.getPriceMultiplier());
            packetdataserializer.writeInt(merchantrecipe.getDemand());
        }

    }

    public static MerchantRecipeList createFromStream(PacketDataSerializer packetdataserializer) {
        MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
        int i = packetdataserializer.readByte() & 255;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = packetdataserializer.readItem();
            ItemStack itemstack1 = packetdataserializer.readItem();
            ItemStack itemstack2 = ItemStack.EMPTY;

            if (packetdataserializer.readBoolean()) {
                itemstack2 = packetdataserializer.readItem();
            }

            boolean flag = packetdataserializer.readBoolean();
            int k = packetdataserializer.readInt();
            int l = packetdataserializer.readInt();
            int i1 = packetdataserializer.readInt();
            int j1 = packetdataserializer.readInt();
            float f = packetdataserializer.readFloat();
            int k1 = packetdataserializer.readInt();
            MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1, k, l, i1, f, k1);

            if (flag) {
                merchantrecipe.setToOutOfStock();
            }

            merchantrecipe.setSpecialPriceDiff(j1);
            merchantrecipelist.add(merchantrecipe);
        }

        return merchantrecipelist;
    }

    public NBTTagCompound createTag() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.size(); ++i) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) this.get(i);

            nbttaglist.add(merchantrecipe.createTag());
        }

        nbttagcompound.put("Recipes", nbttaglist);
        return nbttagcompound;
    }
}
