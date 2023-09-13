package net.minecraft.world.entity;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public enum RelativeMovement {

    X(0), Y(1), Z(2), Y_ROT(3), X_ROT(4);

    public static final Set<RelativeMovement> ALL = Set.of(values());
    public static final Set<RelativeMovement> ROTATION = Set.of(RelativeMovement.X_ROT, RelativeMovement.Y_ROT);
    private final int bit;

    private RelativeMovement(int i) {
        this.bit = i;
    }

    private int getMask() {
        return 1 << this.bit;
    }

    private boolean isSet(int i) {
        return (i & this.getMask()) == this.getMask();
    }

    public static Set<RelativeMovement> unpack(int i) {
        Set<RelativeMovement> set = EnumSet.noneOf(RelativeMovement.class);
        RelativeMovement[] arelativemovement = values();
        int j = arelativemovement.length;

        for (int k = 0; k < j; ++k) {
            RelativeMovement relativemovement = arelativemovement[k];

            if (relativemovement.isSet(i)) {
                set.add(relativemovement);
            }
        }

        return set;
    }

    public static int pack(Set<RelativeMovement> set) {
        int i = 0;

        RelativeMovement relativemovement;

        for (Iterator iterator = set.iterator(); iterator.hasNext(); i |= relativemovement.getMask()) {
            relativemovement = (RelativeMovement) iterator.next();
        }

        return i;
    }
}
