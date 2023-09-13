package net.minecraft.server;

import java.util.Arrays;

public enum PointGroupS {

    P123(0, 1, 2), P213(1, 0, 2), P132(0, 2, 1), P231(1, 2, 0), P312(2, 0, 1), P321(2, 1, 0);

    private final int[] g;
    private final Matrix3f h;
    private static final PointGroupS[][] i = (PointGroupS[][]) SystemUtils.a((Object) (new PointGroupS[values().length][values().length]), (apointgroups) -> {
        PointGroupS[] apointgroups1 = values();
        int i = apointgroups1.length;

        for (int j = 0; j < i; ++j) {
            PointGroupS pointgroups = apointgroups1[j];
            PointGroupS[] apointgroups2 = values();
            int k = apointgroups2.length;

            for (int l = 0; l < k; ++l) {
                PointGroupS pointgroups1 = apointgroups2[l];
                int[] aint = new int[3];

                for (int i1 = 0; i1 < 3; ++i1) {
                    aint[i1] = pointgroups.g[pointgroups1.g[i1]];
                }

                PointGroupS pointgroups2 = (PointGroupS) Arrays.stream(values()).filter((pointgroups3) -> {
                    return Arrays.equals(pointgroups3.g, aint);
                }).findFirst().get();

                apointgroups[pointgroups.ordinal()][pointgroups1.ordinal()] = pointgroups2;
            }
        }

    });

    private PointGroupS(int i, int j, int k) {
        this.g = new int[]{i, j, k};
        this.h = new Matrix3f();
        this.h.a(0, this.a(0), 1.0F);
        this.h.a(1, this.a(1), 1.0F);
        this.h.a(2, this.a(2), 1.0F);
    }

    public PointGroupS a(PointGroupS pointgroups) {
        return PointGroupS.i[this.ordinal()][pointgroups.ordinal()];
    }

    public int a(int i) {
        return this.g[i];
    }

    public Matrix3f a() {
        return this.h;
    }
}
