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

    public void a(Item item, ItemStack itemstack) {
        if (item.isFood()) {
            FoodInfo foodinfo = item.getFoodInfo();

            this.eat(foodinfo.getNutrition(), foodinfo.getSaturationModifier());
        }

    }

    public void a(EntityHuman entityhuman) {
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

        if (flag && this.saturationLevel > 0.0F && entityhuman.fu() && this.foodLevel >= 20) {
            ++this.tickTimer;
            if (this.tickTimer >= 10) {
                float f = Math.min(this.saturationLevel, 6.0F);

                entityhuman.heal(f / 6.0F);
                this.a(f);
                this.tickTimer = 0;
            }
        } else if (flag && this.foodLevel >= 18 && entityhuman.fu()) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                entityhuman.heal(1.0F);
                this.a(6.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (entityhuman.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || entityhuman.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL) {
                    entityhuman.damageEntity(DamageSource.STARVE, 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("foodLevel", 99)) {
            this.foodLevel = nbttagcompound.getInt("foodLevel");
            this.tickTimer = nbttagcompound.getInt("foodTickTimer");
            this.saturationLevel = nbttagcompound.getFloat("foodSaturationLevel");
            this.exhaustionLevel = nbttagcompound.getFloat("foodExhaustionLevel");
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("foodLevel", this.foodLevel);
        nbttagcompound.setInt("foodTickTimer", this.tickTimer);
        nbttagcompound.setFloat("foodSaturationLevel", this.saturationLevel);
        nbttagcompound.setFloat("foodExhaustionLevel", this.exhaustionLevel);
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public int b() {
        return this.lastFoodLevel;
    }

    public boolean c() {
        return this.foodLevel < 20;
    }

    public void a(float f) {
        this.exhaustionLevel = Math.min(this.exhaustionLevel + f, 40.0F);
    }

    public float d() {
        return this.exhaustionLevel;
    }

    public float getSaturationLevel() {
        return this.saturationLevel;
    }

    public void a(int i) {
        this.foodLevel = i;
    }

    public void b(float f) {
        this.saturationLevel = f;
    }

    public void c(float f) {
        this.exhaustionLevel = f;
    }
}
