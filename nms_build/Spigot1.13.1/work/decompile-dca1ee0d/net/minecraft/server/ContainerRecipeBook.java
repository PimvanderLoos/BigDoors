package net.minecraft.server;

public abstract class ContainerRecipeBook extends Container {

    public ContainerRecipeBook() {}

    public abstract void a(AutoRecipeStackManager autorecipestackmanager);

    public abstract void d();

    public abstract boolean a(IRecipe irecipe);

    public abstract int e();

    public abstract int f();

    public abstract int g();
}
