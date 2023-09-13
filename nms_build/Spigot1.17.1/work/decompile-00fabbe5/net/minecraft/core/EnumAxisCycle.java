package net.minecraft.core;

public enum EnumAxisCycle {

    NONE {
        @Override
        public int a(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.a(i, j, k);
        }

        @Override
        public double a(double d0, double d1, double d2, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.a(d0, d1, d2);
        }

        @Override
        public EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis;
        }

        @Override
        public EnumAxisCycle a() {
            return this;
        }
    },
    FORWARD {
        @Override
        public int a(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.a(k, i, j);
        }

        @Override
        public double a(double d0, double d1, double d2, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.a(d2, d0, d1);
        }

        @Override
        public EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis) {
            return null.AXIS_VALUES[Math.floorMod(enumdirection_enumaxis.ordinal() + 1, 3)];
        }

        @Override
        public EnumAxisCycle a() {
            return null.BACKWARD;
        }
    },
    BACKWARD {
        @Override
        public int a(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.a(j, k, i);
        }

        @Override
        public double a(double d0, double d1, double d2, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.a(d1, d2, d0);
        }

        @Override
        public EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis) {
            return null.AXIS_VALUES[Math.floorMod(enumdirection_enumaxis.ordinal() - 1, 3)];
        }

        @Override
        public EnumAxisCycle a() {
            return null.FORWARD;
        }
    };

    public static final EnumDirection.EnumAxis[] AXIS_VALUES = EnumDirection.EnumAxis.values();
    public static final EnumAxisCycle[] VALUES = values();

    EnumAxisCycle() {}

    public abstract int a(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis);

    public abstract double a(double d0, double d1, double d2, EnumDirection.EnumAxis enumdirection_enumaxis);

    public abstract EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis);

    public abstract EnumAxisCycle a();

    public static EnumAxisCycle a(EnumDirection.EnumAxis enumdirection_enumaxis, EnumDirection.EnumAxis enumdirection_enumaxis1) {
        return EnumAxisCycle.VALUES[Math.floorMod(enumdirection_enumaxis1.ordinal() - enumdirection_enumaxis.ordinal(), 3)];
    }
}
