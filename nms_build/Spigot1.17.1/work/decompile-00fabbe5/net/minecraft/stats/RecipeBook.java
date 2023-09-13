package net.minecraft.stats;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.ContainerRecipeBook;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.IRecipe;

public class RecipeBook {

    public final Set<MinecraftKey> known = Sets.newHashSet();
    protected final Set<MinecraftKey> highlight = Sets.newHashSet();
    private final RecipeBookSettings bookSettings = new RecipeBookSettings();

    public RecipeBook() {}

    public void a(RecipeBook recipebook) {
        this.known.clear();
        this.highlight.clear();
        this.bookSettings.a(recipebook.bookSettings);
        this.known.addAll(recipebook.known);
        this.highlight.addAll(recipebook.highlight);
    }

    public void a(IRecipe<?> irecipe) {
        if (!irecipe.isComplex()) {
            this.a(irecipe.getKey());
        }

    }

    protected void a(MinecraftKey minecraftkey) {
        this.known.add(minecraftkey);
    }

    public boolean b(@Nullable IRecipe<?> irecipe) {
        return irecipe == null ? false : this.known.contains(irecipe.getKey());
    }

    public boolean hasDiscoveredRecipe(MinecraftKey minecraftkey) {
        return this.known.contains(minecraftkey);
    }

    public void c(IRecipe<?> irecipe) {
        this.c(irecipe.getKey());
    }

    protected void c(MinecraftKey minecraftkey) {
        this.known.remove(minecraftkey);
        this.highlight.remove(minecraftkey);
    }

    public boolean d(IRecipe<?> irecipe) {
        return this.highlight.contains(irecipe.getKey());
    }

    public void e(IRecipe<?> irecipe) {
        this.highlight.remove(irecipe.getKey());
    }

    public void f(IRecipe<?> irecipe) {
        this.d(irecipe.getKey());
    }

    protected void d(MinecraftKey minecraftkey) {
        this.highlight.add(minecraftkey);
    }

    public boolean a(RecipeBookType recipebooktype) {
        return this.bookSettings.a(recipebooktype);
    }

    public void a(RecipeBookType recipebooktype, boolean flag) {
        this.bookSettings.a(recipebooktype, flag);
    }

    public boolean a(ContainerRecipeBook<?> containerrecipebook) {
        return this.b(containerrecipebook.t());
    }

    public boolean b(RecipeBookType recipebooktype) {
        return this.bookSettings.b(recipebooktype);
    }

    public void b(RecipeBookType recipebooktype, boolean flag) {
        this.bookSettings.b(recipebooktype, flag);
    }

    public void a(RecipeBookSettings recipebooksettings) {
        this.bookSettings.a(recipebooksettings);
    }

    public RecipeBookSettings a() {
        return this.bookSettings.a();
    }

    public void a(RecipeBookType recipebooktype, boolean flag, boolean flag1) {
        this.bookSettings.a(recipebooktype, flag);
        this.bookSettings.b(recipebooktype, flag1);
    }
}
