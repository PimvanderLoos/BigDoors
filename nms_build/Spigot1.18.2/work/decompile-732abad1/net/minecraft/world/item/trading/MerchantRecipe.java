package net.minecraft.world.item.trading;

import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemStack;

public class MerchantRecipe {

    public ItemStack baseCostA;
    public ItemStack costB;
    public final ItemStack result;
    public int uses;
    public int maxUses;
    public boolean rewardExp;
    private int specialPriceDiff;
    private int demand;
    public float priceMultiplier;
    public int xp;

    public MerchantRecipe(NBTTagCompound nbttagcompound) {
        this.rewardExp = true;
        this.xp = 1;
        this.baseCostA = ItemStack.of(nbttagcompound.getCompound("buy"));
        this.costB = ItemStack.of(nbttagcompound.getCompound("buyB"));
        this.result = ItemStack.of(nbttagcompound.getCompound("sell"));
        this.uses = nbttagcompound.getInt("uses");
        if (nbttagcompound.contains("maxUses", 99)) {
            this.maxUses = nbttagcompound.getInt("maxUses");
        } else {
            this.maxUses = 4;
        }

        if (nbttagcompound.contains("rewardExp", 1)) {
            this.rewardExp = nbttagcompound.getBoolean("rewardExp");
        }

        if (nbttagcompound.contains("xp", 3)) {
            this.xp = nbttagcompound.getInt("xp");
        }

        if (nbttagcompound.contains("priceMultiplier", 5)) {
            this.priceMultiplier = nbttagcompound.getFloat("priceMultiplier");
        }

        this.specialPriceDiff = nbttagcompound.getInt("specialPrice");
        this.demand = nbttagcompound.getInt("demand");
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1, int i, int j, float f) {
        this(itemstack, ItemStack.EMPTY, itemstack1, i, j, f);
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2, int i, int j, float f) {
        this(itemstack, itemstack1, itemstack2, 0, i, j, f);
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2, int i, int j, int k, float f) {
        this(itemstack, itemstack1, itemstack2, i, j, k, f, 0);
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2, int i, int j, int k, float f, int l) {
        this.rewardExp = true;
        this.xp = 1;
        this.baseCostA = itemstack;
        this.costB = itemstack1;
        this.result = itemstack2;
        this.uses = i;
        this.maxUses = j;
        this.xp = k;
        this.priceMultiplier = f;
        this.demand = l;
    }

    public ItemStack getBaseCostA() {
        return this.baseCostA;
    }

    public ItemStack getCostA() {
        int i = this.baseCostA.getCount();
        ItemStack itemstack = this.baseCostA.copy();
        int j = Math.max(0, MathHelper.floor((float) (i * this.demand) * this.priceMultiplier));

        itemstack.setCount(MathHelper.clamp(i + j + this.specialPriceDiff, (int) 1, this.baseCostA.getItem().getMaxStackSize()));
        return itemstack;
    }

    public ItemStack getCostB() {
        return this.costB;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void updateDemand() {
        this.demand = this.demand + this.uses - (this.maxUses - this.uses);
    }

    public ItemStack assemble() {
        return this.result.copy();
    }

    public int getUses() {
        return this.uses;
    }

    public void resetUses() {
        this.uses = 0;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public void increaseUses() {
        ++this.uses;
    }

    public int getDemand() {
        return this.demand;
    }

    public void addToSpecialPriceDiff(int i) {
        this.specialPriceDiff += i;
    }

    public void resetSpecialPriceDiff() {
        this.specialPriceDiff = 0;
    }

    public int getSpecialPriceDiff() {
        return this.specialPriceDiff;
    }

    public void setSpecialPriceDiff(int i) {
        this.specialPriceDiff = i;
    }

    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }

    public int getXp() {
        return this.xp;
    }

    public boolean isOutOfStock() {
        return this.uses >= this.maxUses;
    }

    public void setToOutOfStock() {
        this.uses = this.maxUses;
    }

    public boolean needsRestock() {
        return this.uses > 0;
    }

    public boolean shouldRewardExp() {
        return this.rewardExp;
    }

    public NBTTagCompound createTag() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.put("buy", this.baseCostA.save(new NBTTagCompound()));
        nbttagcompound.put("sell", this.result.save(new NBTTagCompound()));
        nbttagcompound.put("buyB", this.costB.save(new NBTTagCompound()));
        nbttagcompound.putInt("uses", this.uses);
        nbttagcompound.putInt("maxUses", this.maxUses);
        nbttagcompound.putBoolean("rewardExp", this.rewardExp);
        nbttagcompound.putInt("xp", this.xp);
        nbttagcompound.putFloat("priceMultiplier", this.priceMultiplier);
        nbttagcompound.putInt("specialPrice", this.specialPriceDiff);
        nbttagcompound.putInt("demand", this.demand);
        return nbttagcompound;
    }

    public boolean satisfiedBy(ItemStack itemstack, ItemStack itemstack1) {
        return this.isRequiredItem(itemstack, this.getCostA()) && itemstack.getCount() >= this.getCostA().getCount() && this.isRequiredItem(itemstack1, this.costB) && itemstack1.getCount() >= this.costB.getCount();
    }

    private boolean isRequiredItem(ItemStack itemstack, ItemStack itemstack1) {
        if (itemstack1.isEmpty() && itemstack.isEmpty()) {
            return true;
        } else {
            ItemStack itemstack2 = itemstack.copy();

            if (itemstack2.getItem().canBeDepleted()) {
                itemstack2.setDamageValue(itemstack2.getDamageValue());
            }

            return ItemStack.isSame(itemstack2, itemstack1) && (!itemstack1.hasTag() || itemstack2.hasTag() && GameProfileSerializer.compareNbt(itemstack1.getTag(), itemstack2.getTag(), false));
        }
    }

    public boolean take(ItemStack itemstack, ItemStack itemstack1) {
        if (!this.satisfiedBy(itemstack, itemstack1)) {
            return false;
        } else {
            itemstack.shrink(this.getCostA().getCount());
            if (!this.getCostB().isEmpty()) {
                itemstack1.shrink(this.getCostB().getCount());
            }

            return true;
        }
    }
}
