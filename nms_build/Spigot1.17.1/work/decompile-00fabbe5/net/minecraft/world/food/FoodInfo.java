package net.minecraft.world.food;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.effect.MobEffect;

public class FoodInfo {

    private final int nutrition;
    private final float saturationModifier;
    private final boolean isMeat;
    private final boolean canAlwaysEat;
    private final boolean fastFood;
    private final List<Pair<MobEffect, Float>> effects;

    FoodInfo(int i, float f, boolean flag, boolean flag1, boolean flag2, List<Pair<MobEffect, Float>> list) {
        this.nutrition = i;
        this.saturationModifier = f;
        this.isMeat = flag;
        this.canAlwaysEat = flag1;
        this.fastFood = flag2;
        this.effects = list;
    }

    public int getNutrition() {
        return this.nutrition;
    }

    public float getSaturationModifier() {
        return this.saturationModifier;
    }

    public boolean c() {
        return this.isMeat;
    }

    public boolean d() {
        return this.canAlwaysEat;
    }

    public boolean e() {
        return this.fastFood;
    }

    public List<Pair<MobEffect, Float>> f() {
        return this.effects;
    }

    public static class a {

        private int nutrition;
        private float saturationModifier;
        private boolean isMeat;
        private boolean canAlwaysEat;
        private boolean fastFood;
        private final List<Pair<MobEffect, Float>> effects = Lists.newArrayList();

        public a() {}

        public FoodInfo.a a(int i) {
            this.nutrition = i;
            return this;
        }

        public FoodInfo.a a(float f) {
            this.saturationModifier = f;
            return this;
        }

        public FoodInfo.a a() {
            this.isMeat = true;
            return this;
        }

        public FoodInfo.a b() {
            this.canAlwaysEat = true;
            return this;
        }

        public FoodInfo.a c() {
            this.fastFood = true;
            return this;
        }

        public FoodInfo.a a(MobEffect mobeffect, float f) {
            this.effects.add(Pair.of(mobeffect, f));
            return this;
        }

        public FoodInfo d() {
            return new FoodInfo(this.nutrition, this.saturationModifier, this.isMeat, this.canAlwaysEat, this.fastFood, this.effects);
        }
    }
}
