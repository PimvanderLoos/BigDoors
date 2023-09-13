package net.minecraft.world.item.trading;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.item.ItemStack;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {

    public MerchantRecipeList() {}

    private MerchantRecipeList(int i) {
        super(i);
    }

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
        packetdataserializer.writeCollection(this, (packetdataserializer1, merchantrecipe) -> {
            packetdataserializer1.writeItem(merchantrecipe.getBaseCostA());
            packetdataserializer1.writeItem(merchantrecipe.getResult());
            packetdataserializer1.writeItem(merchantrecipe.getCostB());
            packetdataserializer1.writeBoolean(merchantrecipe.isOutOfStock());
            packetdataserializer1.writeInt(merchantrecipe.getUses());
            packetdataserializer1.writeInt(merchantrecipe.getMaxUses());
            packetdataserializer1.writeInt(merchantrecipe.getXp());
            packetdataserializer1.writeInt(merchantrecipe.getSpecialPriceDiff());
            packetdataserializer1.writeFloat(merchantrecipe.getPriceMultiplier());
            packetdataserializer1.writeInt(merchantrecipe.getDemand());
        });
    }

    public static MerchantRecipeList createFromStream(PacketDataSerializer packetdataserializer) {
        return (MerchantRecipeList) packetdataserializer.readCollection(MerchantRecipeList::new, (packetdataserializer1) -> {
            ItemStack itemstack = packetdataserializer1.readItem();
            ItemStack itemstack1 = packetdataserializer1.readItem();
            ItemStack itemstack2 = packetdataserializer1.readItem();
            boolean flag = packetdataserializer1.readBoolean();
            int i = packetdataserializer1.readInt();
            int j = packetdataserializer1.readInt();
            int k = packetdataserializer1.readInt();
            int l = packetdataserializer1.readInt();
            float f = packetdataserializer1.readFloat();
            int i1 = packetdataserializer1.readInt();
            MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1, i, j, k, f, i1);

            if (flag) {
                merchantrecipe.setToOutOfStock();
            }

            merchantrecipe.setSpecialPriceDiff(l);
            return merchantrecipe;
        });
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
