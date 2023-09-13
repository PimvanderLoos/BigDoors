package net.minecraft.world.entity.decoration;

import net.minecraft.core.IRegistry;

public class Paintings {

    public static final Paintings KEBAB = a("kebab", 16, 16);
    public static final Paintings AZTEC = a("aztec", 16, 16);
    public static final Paintings ALBAN = a("alban", 16, 16);
    public static final Paintings AZTEC2 = a("aztec2", 16, 16);
    public static final Paintings BOMB = a("bomb", 16, 16);
    public static final Paintings PLANT = a("plant", 16, 16);
    public static final Paintings WASTELAND = a("wasteland", 16, 16);
    public static final Paintings POOL = a("pool", 32, 16);
    public static final Paintings COURBET = a("courbet", 32, 16);
    public static final Paintings SEA = a("sea", 32, 16);
    public static final Paintings SUNSET = a("sunset", 32, 16);
    public static final Paintings CREEBET = a("creebet", 32, 16);
    public static final Paintings WANDERER = a("wanderer", 16, 32);
    public static final Paintings GRAHAM = a("graham", 16, 32);
    public static final Paintings MATCH = a("match", 32, 32);
    public static final Paintings BUST = a("bust", 32, 32);
    public static final Paintings STAGE = a("stage", 32, 32);
    public static final Paintings VOID = a("void", 32, 32);
    public static final Paintings SKULL_AND_ROSES = a("skull_and_roses", 32, 32);
    public static final Paintings WITHER = a("wither", 32, 32);
    public static final Paintings FIGHTERS = a("fighters", 64, 32);
    public static final Paintings POINTER = a("pointer", 64, 64);
    public static final Paintings PIGSCENE = a("pigscene", 64, 64);
    public static final Paintings BURNING_SKULL = a("burning_skull", 64, 64);
    public static final Paintings SKELETON = a("skeleton", 64, 48);
    public static final Paintings DONKEY_KONG = a("donkey_kong", 64, 48);
    private final int width;
    private final int height;

    private static Paintings a(String s, int i, int j) {
        return (Paintings) IRegistry.a((IRegistry) IRegistry.MOTIVE, s, (Object) (new Paintings(i, j)));
    }

    public Paintings(int i, int j) {
        this.width = i;
        this.height = j;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
