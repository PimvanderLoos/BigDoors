package net.minecraft.server;

public class CriterionInstanceAbstract implements CriterionInstance {

    private final MinecraftKey a;

    public CriterionInstanceAbstract(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    public MinecraftKey a() {
        return this.a;
    }

    public String toString() {
        return "AbstractCriterionInstance{criterion=" + this.a + '}';
    }
}
