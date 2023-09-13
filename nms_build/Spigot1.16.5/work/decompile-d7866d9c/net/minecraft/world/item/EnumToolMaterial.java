package net.minecraft.world.item;

import java.util.function.Supplier;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.LazyInitVar;
import net.minecraft.world.item.crafting.RecipeItemStack;

public enum EnumToolMaterial implements ToolMaterial {

    WOOD(0, 59, 2.0F, 0.0F, 15, () -> {
        return RecipeItemStack.a((Tag) TagsItem.PLANKS);
    }), STONE(1, 131, 4.0F, 1.0F, 5, () -> {
        return RecipeItemStack.a((Tag) TagsItem.STONE_TOOL_MATERIALS);
    }), IRON(2, 250, 6.0F, 2.0F, 14, () -> {
        return RecipeItemStack.a(Items.IRON_INGOT);
    }), DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> {
        return RecipeItemStack.a(Items.DIAMOND);
    }), GOLD(0, 32, 12.0F, 0.0F, 22, () -> {
        return RecipeItemStack.a(Items.GOLD_INGOT);
    }), NETHERITE(4, 2031, 9.0F, 4.0F, 15, () -> {
        return RecipeItemStack.a(Items.NETHERITE_INGOT);
    });

    private final int g;
    private final int h;
    private final float i;
    private final float j;
    private final int k;
    private final LazyInitVar<RecipeItemStack> l;

    private EnumToolMaterial(int i, int j, float f, float f1, int k, Supplier supplier) {
        this.g = i;
        this.h = j;
        this.i = f;
        this.j = f1;
        this.k = k;
        this.l = new LazyInitVar<>(supplier);
    }

    @Override
    public int a() {
        return this.h;
    }

    @Override
    public float b() {
        return this.i;
    }

    @Override
    public float c() {
        return this.j;
    }

    @Override
    public int d() {
        return this.g;
    }

    @Override
    public int e() {
        return this.k;
    }

    @Override
    public RecipeItemStack f() {
        return (RecipeItemStack) this.l.a();
    }
}
