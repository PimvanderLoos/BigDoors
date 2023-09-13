package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;

public class NBTReadLimiter {

    public static final NBTReadLimiter UNLIMITED = new NBTReadLimiter(0L) {
        @Override
        public void accountBytes(long i) {}
    };
    private final long quota;
    private long usage;

    public NBTReadLimiter(long i) {
        this.quota = i;
    }

    public void accountBytes(long i) {
        this.usage += i;
        if (this.usage > this.quota) {
            throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.usage + "bytes where max allowed: " + this.quota);
        }
    }

    @VisibleForTesting
    public long getUsage() {
        return this.usage;
    }
}
