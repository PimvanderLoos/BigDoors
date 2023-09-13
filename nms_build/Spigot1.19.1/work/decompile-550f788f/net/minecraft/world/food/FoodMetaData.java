package net.minecraft.world.food;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

public class FoodMetaData {

    public int foodLevel = 20;
    public float saturationLevel = 5.0F;
    public float exhaustionLevel;
    private int tickTimer;
    private int lastFoodLevel = 20;

    public FoodMetaData() {}

    public void eat(int i, float f) {
        this.foodLevel = Math.min(i + this.foodLevel, 20);
        this.saturationLevel = Math.min(this.saturationLevel + (float) i * f * 2.0F, (float) this.foodLevel);
    }

    public void eat(Item item, ItemStack itemstack) {
        if (item.isEdible()) {
            FoodInfo foodinfo = item.getFoodProperties();

            this.eat(foodinfo.getNutrition(), foodinfo.getSaturationModifier());
        }

    }

    public void tick(EntityHuman entityhuman) {
        EnumDifficulty enumdifficulty = entityhuman.level.getDifficulty();

        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (enumdifficulty != EnumDifficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean flag = entityhuman.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);

        if (flag && this.saturationLevel > 0.0F && entityhuman.isHurt() && this.foodLevel >= 20) {
            ++this.tickTimer;
            if (this.tickTimer >= 10) {
                float f = Math.min(this.saturationLevel, 6.0F);

                entityhuman.heal(f / 6.0F);
                this.addExhaustion(f);
                this.tickTimer = 0;
            }
        } else if (flag && this.foodLevel >= 18 && entityhuman.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                entityhuman.heal(1.0F);
                this.addExhaustion(6.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (entityhuman.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || entityhuman.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL) {
                    entityhuman.hurt(DamageSource.STARVE, 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }

    }

    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.contains("foodLevel", 99)) {
            this.foodLevel = nbttagcompound.getInt("foodLevel");
            this.tickTimer = nbttagcompound.getInt("foodTickTimer");
            this.saturationLevel = nbttagcompound.getFloat("foodSaturationLevel");
            this.exhaustionLevel = nbttagcompound.getFloat("foodExhaustionLevel");
        }

    }

    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.putInt("foodLevel", this.foodLevel);
        nbttagcompound.putInt("foodTickTimer", this.tickTimer);
        nbttagcompound.putFloat("foodSaturationLevel", this.saturationLevel);
        nbttagcompound.putFloat("foodExhaustionLevel", this.exhaustionLevel);
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public int getLastFoodLevel() {
        return this.lastFoodLevel;
    }

    public boolean needsFood() {
        return this.foodLevel < 20;
    }

    public void addExhaustion(float f) {
        this.exhaustionLevel = Math.min(this.exhaustionLevel + f, 40.0F);
    }

    public float getExhaustionLevel() {
        return this.exhaustionLevel;
    }

    public float getSaturationLevel() {
        return this.saturationLevel;
    }

    public void setFoodLevel(int i) {
        this.foodLevel = i;
    }

    public void setSaturation(float f) {
        this.saturationLevel = f;
    }

    public void setExhaustion(float f) {
        this.exhaustionLevel = f;
    }
}
