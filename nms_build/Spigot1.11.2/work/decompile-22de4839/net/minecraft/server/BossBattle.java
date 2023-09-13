package net.minecraft.server;

import java.util.UUID;

public abstract class BossBattle {

    private final UUID h;
    public IChatBaseComponent title;
    protected float b;
    public BossBattle.BarColor color;
    public BossBattle.BarStyle style;
    protected boolean e;
    protected boolean f;
    protected boolean g;

    public BossBattle(UUID uuid, IChatBaseComponent ichatbasecomponent, BossBattle.BarColor bossbattle_barcolor, BossBattle.BarStyle bossbattle_barstyle) {
        this.h = uuid;
        this.title = ichatbasecomponent;
        this.color = bossbattle_barcolor;
        this.style = bossbattle_barstyle;
        this.b = 1.0F;
    }

    public UUID d() {
        return this.h;
    }

    public IChatBaseComponent e() {
        return this.title;
    }

    public void a(IChatBaseComponent ichatbasecomponent) {
        this.title = ichatbasecomponent;
    }

    public float getProgress() {
        return this.b;
    }

    public void a(float f) {
        this.b = f;
    }

    public BossBattle.BarColor g() {
        return this.color;
    }

    public BossBattle.BarStyle h() {
        return this.style;
    }

    public boolean i() {
        return this.e;
    }

    public BossBattle a(boolean flag) {
        this.e = flag;
        return this;
    }

    public boolean j() {
        return this.f;
    }

    public BossBattle b(boolean flag) {
        this.f = flag;
        return this;
    }

    public BossBattle c(boolean flag) {
        this.g = flag;
        return this;
    }

    public boolean k() {
        return this.g;
    }

    public static enum BarStyle {

        PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20;

        private BarStyle() {}
    }

    public static enum BarColor {

        PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE;

        private BarColor() {}
    }
}
