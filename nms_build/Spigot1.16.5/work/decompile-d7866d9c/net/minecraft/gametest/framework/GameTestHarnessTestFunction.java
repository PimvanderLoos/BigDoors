package net.minecraft.gametest.framework;

import java.util.function.Consumer;
import net.minecraft.world.level.block.EnumBlockRotation;

public class GameTestHarnessTestFunction {

    private final String a;
    private final String b;
    private final String c;
    private final boolean d;
    private final Consumer<GameTestHarnessHelper> e;
    private final int f;
    private final long g;
    private final EnumBlockRotation h;

    public void a(GameTestHarnessHelper gametestharnesshelper) {
        this.e.accept(gametestharnesshelper);
    }

    public String a() {
        return this.b;
    }

    public String b() {
        return this.c;
    }

    public String toString() {
        return this.b;
    }

    public int c() {
        return this.f;
    }

    public boolean d() {
        return this.d;
    }

    public String e() {
        return this.a;
    }

    public long f() {
        return this.g;
    }

    public EnumBlockRotation g() {
        return this.h;
    }
}
