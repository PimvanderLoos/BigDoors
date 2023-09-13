package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.function.LongPredicate;
import net.minecraft.util.MathHelper;

public abstract class LightEngineGraph {

    private static final int NO_COMPUTED_LEVEL = 255;
    private final int levelCount;
    private final LongLinkedOpenHashSet[] queues;
    private final Long2ByteMap computedLevels;
    private int firstQueuedLevel;
    private volatile boolean hasWork;

    protected LightEngineGraph(int i, final int j, final int k) {
        if (i >= 254) {
            throw new IllegalArgumentException("Level count must be < 254.");
        } else {
            this.levelCount = i;
            this.queues = new LongLinkedOpenHashSet[i];

            for (int l = 0; l < i; ++l) {
                this.queues[l] = new LongLinkedOpenHashSet(j, 0.5F) {
                    protected void rehash(int i1) {
                        if (i1 > j) {
                            super.rehash(i1);
                        }

                    }
                };
            }

            this.computedLevels = new Long2ByteOpenHashMap(k, 0.5F) {
                protected void rehash(int i1) {
                    if (i1 > k) {
                        super.rehash(i1);
                    }

                }
            };
            this.computedLevels.defaultReturnValue((byte) -1);
            this.firstQueuedLevel = i;
        }
    }

    private int a(int i, int j) {
        int k = i;

        if (i > j) {
            k = j;
        }

        if (k > this.levelCount - 1) {
            k = this.levelCount - 1;
        }

        return k;
    }

    private void a(int i) {
        int j = this.firstQueuedLevel;

        this.firstQueuedLevel = i;

        for (int k = j + 1; k < i; ++k) {
            if (!this.queues[k].isEmpty()) {
                this.firstQueuedLevel = k;
                break;
            }
        }

    }

    protected void e(long i) {
        int j = this.computedLevels.get(i) & 255;

        if (j != 255) {
            int k = this.c(i);
            int l = this.a(k, j);

            this.a(i, l, this.levelCount, true);
            this.hasWork = this.firstQueuedLevel < this.levelCount;
        }
    }

    public void a(LongPredicate longpredicate) {
        LongArrayList longarraylist = new LongArrayList();

        this.computedLevels.keySet().forEach((i) -> {
            if (longpredicate.test(i)) {
                longarraylist.add(i);
            }

        });
        longarraylist.forEach(this::e);
    }

    private void a(long i, int j, int k, boolean flag) {
        if (flag) {
            this.computedLevels.remove(i);
        }

        this.queues[j].remove(i);
        if (this.queues[j].isEmpty() && this.firstQueuedLevel == j) {
            this.a(k);
        }

    }

    private void a(long i, int j, int k) {
        this.computedLevels.put(i, (byte) j);
        this.queues[k].add(i);
        if (this.firstQueuedLevel > k) {
            this.firstQueuedLevel = k;
        }

    }

    protected void f(long i) {
        this.a(i, i, this.levelCount - 1, false);
    }

    protected void a(long i, long j, int k, boolean flag) {
        this.a(i, j, k, this.c(j), this.computedLevels.get(j) & 255, flag);
        this.hasWork = this.firstQueuedLevel < this.levelCount;
    }

    private void a(long i, long j, int k, int l, int i1, boolean flag) {
        if (!this.a(j)) {
            k = MathHelper.clamp(k, 0, this.levelCount - 1);
            l = MathHelper.clamp(l, 0, this.levelCount - 1);
            boolean flag1;

            if (i1 == 255) {
                flag1 = true;
                i1 = l;
            } else {
                flag1 = false;
            }

            int j1;

            if (flag) {
                j1 = Math.min(i1, k);
            } else {
                j1 = MathHelper.clamp(this.a(j, i, k), 0, this.levelCount - 1);
            }

            int k1 = this.a(l, i1);

            if (l != j1) {
                int l1 = this.a(l, j1);

                if (k1 != l1 && !flag1) {
                    this.a(j, k1, l1, false);
                }

                this.a(j, j1, l1);
            } else if (!flag1) {
                this.a(j, k1, this.levelCount, true);
            }

        }
    }

    protected final void b(long i, long j, int k, boolean flag) {
        int l = this.computedLevels.get(j) & 255;
        int i1 = MathHelper.clamp(this.b(i, j, k), 0, this.levelCount - 1);

        if (flag) {
            this.a(i, j, i1, this.c(j), l, true);
        } else {
            boolean flag1;
            int j1;

            if (l == 255) {
                flag1 = true;
                j1 = MathHelper.clamp(this.c(j), 0, this.levelCount - 1);
            } else {
                j1 = l;
                flag1 = false;
            }

            if (i1 == j1) {
                this.a(i, j, this.levelCount - 1, flag1 ? j1 : this.c(j), l, false);
            }
        }

    }

    protected final boolean b() {
        return this.hasWork;
    }

    protected final int b(int i) {
        if (this.firstQueuedLevel >= this.levelCount) {
            return i;
        } else {
            while (this.firstQueuedLevel < this.levelCount && i > 0) {
                --i;
                LongLinkedOpenHashSet longlinkedopenhashset = this.queues[this.firstQueuedLevel];
                long j = longlinkedopenhashset.removeFirstLong();
                int k = MathHelper.clamp(this.c(j), 0, this.levelCount - 1);

                if (longlinkedopenhashset.isEmpty()) {
                    this.a(this.levelCount);
                }

                int l = this.computedLevels.remove(j) & 255;

                if (l < k) {
                    this.a(j, l);
                    this.a(j, l, true);
                } else if (l > k) {
                    this.a(j, l, this.a(this.levelCount - 1, l));
                    this.a(j, this.levelCount - 1);
                    this.a(j, k, false);
                }
            }

            this.hasWork = this.firstQueuedLevel < this.levelCount;
            return i;
        }
    }

    public int c() {
        return this.computedLevels.size();
    }

    protected abstract boolean a(long i);

    protected abstract int a(long i, long j, int k);

    protected abstract void a(long i, int j, boolean flag);

    protected abstract int c(long i);

    protected abstract void a(long i, int j);

    protected abstract int b(long i, long j, int k);
}
