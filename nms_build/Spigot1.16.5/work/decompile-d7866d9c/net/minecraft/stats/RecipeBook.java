package net.minecraft.stats;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.IRecipe;

public class RecipeBook {

    public final Set<MinecraftKey> recipes = Sets.newHashSet();
    protected final Set<MinecraftKey> toBeDisplayed = Sets.newHashSet();
    private final RecipeBookSettings c = new RecipeBookSettings();

    public RecipeBook() {}

    public void a(RecipeBook recipebook) {
        this.recipes.clear();
        this.toBeDisplayed.clear();
        this.c.a(recipebook.c);
        this.recipes.addAll(recipebook.recipes);
        this.toBeDisplayed.addAll(recipebook.toBeDisplayed);
    }

    public void a(IRecipe<?> irecipe) {
        if (!irecipe.isComplex()) {
            this.a(irecipe.getKey());
        }

    }

    protected void a(MinecraftKey minecraftkey) {
        this.recipes.add(minecraftkey);
    }

    public boolean b(@Nullable IRecipe<?> irecipe) {
        return irecipe == null ? false : this.recipes.contains(irecipe.getKey());
    }

    public boolean hasDiscoveredRecipe(MinecraftKey minecraftkey) {
        return this.recipes.contains(minecraftkey);
    }

    protected void c(MinecraftKey minecraftkey) {
        this.recipes.remove(minecraftkey);
        this.toBeDisplayed.remove(minecraftkey);
    }

    public void e(IRecipe<?> irecipe) {
        this.toBeDisplayed.remove(irecipe.getKey());
    }

    public void f(IRecipe<?> irecipe) {
        this.d(irecipe.getKey());
    }

    protected void d(MinecraftKey minecraftkey) {
        this.toBeDisplayed.add(minecraftkey);
    }

    public void a(RecipeBookSettings recipebooksettings) {
        this.c.a(recipebooksettings);
    }

    public RecipeBookSettings a() {
        return this.c.a();
    }

    public void a(RecipeBookType recipebooktype, boolean flag, boolean flag1) {
        this.c.a(recipebooktype, flag);
        this.c.b(recipebooktype, flag1);
    }
}
