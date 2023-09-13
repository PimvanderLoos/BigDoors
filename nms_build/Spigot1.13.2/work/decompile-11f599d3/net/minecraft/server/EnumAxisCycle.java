package net.minecraft.server;

public enum EnumAxisCycle {

    NONE {
        public int a(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.a(i, j, k);
        }

        public EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis;
        }

        public EnumAxisCycle a() {
            return this;
        }
    },
    FORWARD {
        public int a(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.a(k, i, j);
        }

        public EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis) {
            return null.d[Math.floorMod(enumdirection_enumaxis.ordinal() + 1, 3)];
        }

        public EnumAxisCycle a() {
            return null.BACKWARD;
        }
    },
    BACKWARD {
        public int a(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.a(j, k, i);
        }

        public EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis) {
            return null.d[Math.floorMod(enumdirection_enumaxis.ordinal() - 1, 3)];
        }

        public EnumAxisCycle a() {
            return null.FORWARD;
        }
    };

    public static final EnumDirection.EnumAxis[] d = EnumDirection.EnumAxis.values();
    public static final EnumAxisCycle[] e = values();

    private EnumAxisCycle() {}

    public abstract int a(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis);

    public abstract EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis);

    public abstract EnumAxisCycle a();

    public static EnumAxisCycle a(EnumDirection.EnumAxis enumdirection_enumaxis, EnumDirection.EnumAxis enumdirection_enumaxis1) {
        return EnumAxisCycle.e[Math.floorMod(enumdirection_enumaxis1.ordinal() - enumdirection_enumaxis.ordinal(), 3)];
    }
}
