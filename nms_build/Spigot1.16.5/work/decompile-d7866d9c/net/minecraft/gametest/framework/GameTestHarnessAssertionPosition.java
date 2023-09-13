package net.minecraft.gametest.framework;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;

public class GameTestHarnessAssertionPosition extends GameTestHarnessAssertion {

    private final BlockPosition a;
    private final BlockPosition b;
    private final long c;

    public String getMessage() {
        String s = "" + this.a.getX() + "," + this.a.getY() + "," + this.a.getZ() + " (relative: " + this.b.getX() + "," + this.b.getY() + "," + this.b.getZ() + ")";

        return super.getMessage() + " at " + s + " (t=" + this.c + ")";
    }

    @Nullable
    public String a() {
        return super.getMessage() + " here";
    }

    @Nullable
    public BlockPosition c() {
        return this.a;
    }
}
