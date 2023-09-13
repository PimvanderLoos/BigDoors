package net.minecraft.core;

public enum EnumAxisCycle {

    NONE {
        @Override
        public int cycle(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.choose(i, j, k);
        }

        @Override
        public double cycle(double d0, double d1, double d2, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.choose(d0, d1, d2);
        }

        @Override
        public EnumDirection.EnumAxis cycle(EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis;
        }

        @Override
        public EnumAxisCycle inverse() {
            return this;
        }
    },
    FORWARD {
        @Override
        public int cycle(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.choose(k, i, j);
        }

        @Override
        public double cycle(double d0, double d1, double d2, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.choose(d2, d0, d1);
        }

        @Override
        public EnumDirection.EnumAxis cycle(EnumDirection.EnumAxis enumdirection_enumaxis) {
            return null.AXIS_VALUES[Math.floorMod(enumdirection_enumaxis.ordinal() + 1, 3)];
        }

        @Override
        public EnumAxisCycle inverse() {
            return null.BACKWARD;
        }
    },
    BACKWARD {
        @Override
        public int cycle(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.choose(j, k, i);
        }

        @Override
        public double cycle(double d0, double d1, double d2, EnumDirection.EnumAxis enumdirection_enumaxis) {
            return enumdirection_enumaxis.choose(d1, d2, d0);
        }

        @Override
        public EnumDirection.EnumAxis cycle(EnumDirection.EnumAxis enumdirection_enumaxis) {
            return null.AXIS_VALUES[Math.floorMod(enumdirection_enumaxis.ordinal() - 1, 3)];
        }

        @Override
        public EnumAxisCycle inverse() {
            return null.FORWARD;
        }
    };

    public static final EnumDirection.EnumAxis[] AXIS_VALUES = EnumDirection.EnumAxis.values();
    public static final EnumAxisCycle[] VALUES = values();

    EnumAxisCycle() {}

    public abstract int cycle(int i, int j, int k, EnumDirection.EnumAxis enumdirection_enumaxis);

    public abstract double cycle(double d0, double d1, double d2, EnumDirection.EnumAxis enumdirection_enumaxis);

    public abstract EnumDirection.EnumAxis cycle(EnumDirection.EnumAxis enumdirection_enumaxis);

    public abstract EnumAxisCycle inverse();

    public static EnumAxisCycle between(EnumDirection.EnumAxis enumdirection_enumaxis, EnumDirection.EnumAxis enumdirection_enumaxis1) {
        return EnumAxisCycle.VALUES[Math.floorMod(enumdirection_enumaxis1.ordinal() - enumdirection_enumaxis.ordinal(), 3)];
    }
}
