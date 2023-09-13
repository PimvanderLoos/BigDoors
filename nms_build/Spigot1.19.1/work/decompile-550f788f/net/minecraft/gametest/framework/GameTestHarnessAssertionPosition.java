package net.minecraft.gametest.framework;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;

public class GameTestHarnessAssertionPosition extends GameTestHarnessAssertion {

    private final BlockPosition absolutePos;
    private final BlockPosition relativePos;
    private final long tick;

    public GameTestHarnessAssertionPosition(String s, BlockPosition blockposition, BlockPosition blockposition1, long i) {
        super(s);
        this.absolutePos = blockposition;
        this.relativePos = blockposition1;
        this.tick = i;
    }

    public String getMessage() {
        int i = this.absolutePos.getX();
        String s = i + "," + this.absolutePos.getY() + "," + this.absolutePos.getZ() + " (relative: " + this.relativePos.getX() + "," + this.relativePos.getY() + "," + this.relativePos.getZ() + ")";

        return super.getMessage() + " at " + s + " (t=" + this.tick + ")";
    }

    @Nullable
    public String getMessageToShowAtBlock() {
        return super.getMessage();
    }

    @Nullable
    public BlockPosition getRelativePos() {
        return this.relativePos;
    }

    @Nullable
    public BlockPosition getAbsolutePos() {
        return this.absolutePos;
    }
}
