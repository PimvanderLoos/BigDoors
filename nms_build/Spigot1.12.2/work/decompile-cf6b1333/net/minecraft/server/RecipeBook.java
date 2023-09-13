package net.minecraft.server;

import java.util.BitSet;
import javax.annotation.Nullable;

public class RecipeBook {

    protected final BitSet a = new BitSet();
    protected final BitSet b = new BitSet();
    protected boolean c;
    protected boolean d;

    public RecipeBook() {}

    public void a(RecipeBook recipebook) {
        this.a.clear();
        this.b.clear();
        this.a.or(recipebook.a);
        this.b.or(recipebook.b);
    }

    public void a(IRecipe irecipe) {
        if (!irecipe.c()) {
            this.a.set(d(irecipe));
        }

    }

    public boolean b(@Nullable IRecipe irecipe) {
        return this.a.get(d(irecipe));
    }

    public void c(IRecipe irecipe) {
        int i = d(irecipe);

        this.a.clear(i);
        this.b.clear(i);
    }

    protected static int d(@Nullable IRecipe irecipe) {
        return CraftingManager.recipes.a((Object) irecipe);
    }

    public void f(IRecipe irecipe) {
        this.b.clear(d(irecipe));
    }

    public void g(IRecipe irecipe) {
        this.b.set(d(irecipe));
    }

    public void a(boolean flag) {
        this.c = flag;
    }

    public void b(boolean flag) {
        this.d = flag;
    }
}
