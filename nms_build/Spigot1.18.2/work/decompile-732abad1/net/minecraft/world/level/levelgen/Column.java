package net.minecraft.world.level.levelgen;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class Column {

    public Column() {}

    public static Column.b around(int i, int j) {
        return new Column.b(i - 1, j + 1);
    }

    public static Column.b inside(int i, int j) {
        return new Column.b(i, j);
    }

    public static Column below(int i) {
        return new Column.c(i, false);
    }

    public static Column fromHighest(int i) {
        return new Column.c(i + 1, false);
    }

    public static Column above(int i) {
        return new Column.c(i, true);
    }

    public static Column fromLowest(int i) {
        return new Column.c(i - 1, true);
    }

    public static Column line() {
        return Column.a.INSTANCE;
    }

    public static Column create(OptionalInt optionalint, OptionalInt optionalint1) {
        return (Column) (optionalint.isPresent() && optionalint1.isPresent() ? inside(optionalint.getAsInt(), optionalint1.getAsInt()) : (optionalint.isPresent() ? above(optionalint.getAsInt()) : (optionalint1.isPresent() ? below(optionalint1.getAsInt()) : line())));
    }

    public abstract OptionalInt getCeiling();

    public abstract OptionalInt getFloor();

    public abstract OptionalInt getHeight();

    public Column withFloor(OptionalInt optionalint) {
        return create(optionalint, this.getCeiling());
    }

    public Column withCeiling(OptionalInt optionalint) {
        return create(this.getFloor(), optionalint);
    }

    public static Optional<Column> scan(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition, int i, Predicate<IBlockData> predicate, Predicate<IBlockData> predicate1) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        if (!virtuallevelreadable.isStateAtPosition(blockposition, predicate)) {
            return Optional.empty();
        } else {
            int j = blockposition.getY();
            OptionalInt optionalint = scanDirection(virtuallevelreadable, i, predicate, predicate1, blockposition_mutableblockposition, j, EnumDirection.UP);
            OptionalInt optionalint1 = scanDirection(virtuallevelreadable, i, predicate, predicate1, blockposition_mutableblockposition, j, EnumDirection.DOWN);

            return Optional.of(create(optionalint1, optionalint));
        }
    }

    private static OptionalInt scanDirection(VirtualLevelReadable virtuallevelreadable, int i, Predicate<IBlockData> predicate, Predicate<IBlockData> predicate1, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int j, EnumDirection enumdirection) {
        blockposition_mutableblockposition.setY(j);

        for (int k = 1; k < i && virtuallevelreadable.isStateAtPosition(blockposition_mutableblockposition, predicate); ++k) {
            blockposition_mutableblockposition.move(enumdirection);
        }

        return virtuallevelreadable.isStateAtPosition(blockposition_mutableblockposition, predicate1) ? OptionalInt.of(blockposition_mutableblockposition.getY()) : OptionalInt.empty();
    }

    public static final class b extends Column {

        private final int floor;
        private final int ceiling;

        protected b(int i, int j) {
            this.floor = i;
            this.ceiling = j;
            if (this.height() < 0) {
                throw new IllegalArgumentException("Column of negative height: " + this);
            }
        }

        @Override
        public OptionalInt getCeiling() {
            return OptionalInt.of(this.ceiling);
        }

        @Override
        public OptionalInt getFloor() {
            return OptionalInt.of(this.floor);
        }

        @Override
        public OptionalInt getHeight() {
            return OptionalInt.of(this.height());
        }

        public int ceiling() {
            return this.ceiling;
        }

        public int floor() {
            return this.floor;
        }

        public int height() {
            return this.ceiling - this.floor - 1;
        }

        public String toString() {
            return "C(" + this.ceiling + "-" + this.floor + ")";
        }
    }

    public static final class c extends Column {

        private final int edge;
        private final boolean pointingUp;

        public c(int i, boolean flag) {
            this.edge = i;
            this.pointingUp = flag;
        }

        @Override
        public OptionalInt getCeiling() {
            return this.pointingUp ? OptionalInt.empty() : OptionalInt.of(this.edge);
        }

        @Override
        public OptionalInt getFloor() {
            return this.pointingUp ? OptionalInt.of(this.edge) : OptionalInt.empty();
        }

        @Override
        public OptionalInt getHeight() {
            return OptionalInt.empty();
        }

        public String toString() {
            return this.pointingUp ? "C(" + this.edge + "-)" : "C(-" + this.edge + ")";
        }
    }

    public static final class a extends Column {

        static final Column.a INSTANCE = new Column.a();

        private a() {}

        @Override
        public OptionalInt getCeiling() {
            return OptionalInt.empty();
        }

        @Override
        public OptionalInt getFloor() {
            return OptionalInt.empty();
        }

        @Override
        public OptionalInt getHeight() {
            return OptionalInt.empty();
        }

        public String toString() {
            return "C(-)";
        }
    }
}
