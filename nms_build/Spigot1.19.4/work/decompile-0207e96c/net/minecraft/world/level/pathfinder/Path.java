package net.minecraft.world.level.pathfinder;

public class Path {

    private PathPoint[] heap = new PathPoint[128];
    private int size;

    public Path() {}

    public PathPoint insert(PathPoint pathpoint) {
        if (pathpoint.heapIdx >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        } else {
            if (this.size == this.heap.length) {
                PathPoint[] apathpoint = new PathPoint[this.size << 1];

                System.arraycopy(this.heap, 0, apathpoint, 0, this.size);
                this.heap = apathpoint;
            }

            this.heap[this.size] = pathpoint;
            pathpoint.heapIdx = this.size;
            this.upHeap(this.size++);
            return pathpoint;
        }
    }

    public void clear() {
        this.size = 0;
    }

    public PathPoint peek() {
        return this.heap[0];
    }

    public PathPoint pop() {
        PathPoint pathpoint = this.heap[0];

        this.heap[0] = this.heap[--this.size];
        this.heap[this.size] = null;
        if (this.size > 0) {
            this.downHeap(0);
        }

        pathpoint.heapIdx = -1;
        return pathpoint;
    }

    public void remove(PathPoint pathpoint) {
        this.heap[pathpoint.heapIdx] = this.heap[--this.size];
        this.heap[this.size] = null;
        if (this.size > pathpoint.heapIdx) {
            if (this.heap[pathpoint.heapIdx].f < pathpoint.f) {
                this.upHeap(pathpoint.heapIdx);
            } else {
                this.downHeap(pathpoint.heapIdx);
            }
        }

        pathpoint.heapIdx = -1;
    }

    public void changeCost(PathPoint pathpoint, float f) {
        float f1 = pathpoint.f;

        pathpoint.f = f;
        if (f < f1) {
            this.upHeap(pathpoint.heapIdx);
        } else {
            this.downHeap(pathpoint.heapIdx);
        }

    }

    public int size() {
        return this.size;
    }

    private void upHeap(int i) {
        PathPoint pathpoint = this.heap[i];

        int j;

        for (float f = pathpoint.f; i > 0; i = j) {
            j = i - 1 >> 1;
            PathPoint pathpoint1 = this.heap[j];

            if (f >= pathpoint1.f) {
                break;
            }

            this.heap[i] = pathpoint1;
            pathpoint1.heapIdx = i;
        }

        this.heap[i] = pathpoint;
        pathpoint.heapIdx = i;
    }

    private void downHeap(int i) {
        PathPoint pathpoint = this.heap[i];
        float f = pathpoint.f;

        while (true) {
            int j = 1 + (i << 1);
            int k = j + 1;

            if (j >= this.size) {
                break;
            }

            PathPoint pathpoint1 = this.heap[j];
            float f1 = pathpoint1.f;
            PathPoint pathpoint2;
            float f2;

            if (k >= this.size) {
                pathpoint2 = null;
                f2 = Float.POSITIVE_INFINITY;
            } else {
                pathpoint2 = this.heap[k];
                f2 = pathpoint2.f;
            }

            if (f1 < f2) {
                if (f1 >= f) {
                    break;
                }

                this.heap[i] = pathpoint1;
                pathpoint1.heapIdx = i;
                i = j;
            } else {
                if (f2 >= f) {
                    break;
                }

                this.heap[i] = pathpoint2;
                pathpoint2.heapIdx = i;
                i = k;
            }
        }

        this.heap[i] = pathpoint;
        pathpoint.heapIdx = i;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public PathPoint[] getHeap() {
        PathPoint[] apathpoint = new PathPoint[this.size()];

        System.arraycopy(this.heap, 0, apathpoint, 0, this.size());
        return apathpoint;
    }
}
