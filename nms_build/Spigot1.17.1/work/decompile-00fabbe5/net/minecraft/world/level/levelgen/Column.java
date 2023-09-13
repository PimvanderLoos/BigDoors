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

    public static Column.b a(int i, int j) {
        return new Column.b(i - 1, j + 1);
    }

    public static Column.b b(int i, int j) {
        return new Column.b(i, j);
    }

    public static Column a(int i) {
        return new Column.c(i, false);
    }

    public static Column b(int i) {
        return new Column.c(i + 1, false);
    }

    public static Column c(int i) {
        return new Column.c(i, true);
    }

    public static Column d(int i) {
        return new Column.c(i - 1, true);
    }

    public static Column a() {
        return Column.a.INSTANCE;
    }

    public static Column a(OptionalInt optionalint, OptionalInt optionalint1) {
        return (Column) (optionalint.isPresent() && optionalint1.isPresent() ? b(optionalint.getAsInt(), optionalint1.getAsInt()) : (optionalint.isPresent() ? c(optionalint.getAsInt()) : (optionalint1.isPresent() ? a(optionalint1.getAsInt()) : a())));
    }

    public abstract OptionalInt b();

    public abstract OptionalInt c();

    public abstract OptionalInt d();

    public Column a(OptionalInt optionalint) {
        return a(optionalint, this.b());
    }

    public Column b(OptionalInt optionalint) {
        return a(this.c(), optionalint);
    }

    public static Optional<Column> a(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition, int i, Predicate<IBlockData> predicate, Predicate<IBlockData> predicate1) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        if (!virtuallevelreadable.a(blockposition, predicate)) {
            return Optional.empty();
        } else {
            int j = blockposition.getY();
            OptionalInt optionalint = a(virtuallevelreadable, i, predicate, predicate1, blockposition_mutableblockposition, j, EnumDirection.UP);
            OptionalInt optionalint1 = a(virtuallevelreadable, i, predicate, predicate1, blockposition_mutableblockposition, j, EnumDirection.DOWN);

            return Optional.of(a(optionalint1, optionalint));
        }
    }

    private static OptionalInt a(VirtualLevelReadable virtuallevelreadable, int i, Predicate<IBlockData> predicate, Predicate<IBlockData> predicate1, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int j, EnumDirection enumdirection) {
        blockposition_mutableblockposition.t(j);

        for (int k = 1; k < i && virtuallevelreadable.a(blockposition_mutableblockposition, predicate); ++k) {
            blockposition_mutableblockposition.c(enumdirection);
        }

        return virtuallevelreadable.a(blockposition_mutableblockposition, predicate1) ? OptionalInt.of(blockposition_mutableblockposition.getY()) : OptionalInt.empty();
    }

    public static final class b extends Column {

        private final int floor;
        private final int ceiling;

        protected b(int i, int j) {
            this.floor = i;
            this.ceiling = j;
            if (this.g() < 0) {
                throw new IllegalArgumentException("Column of negative height: " + this);
            }
        }

        @Override
        public OptionalInt b() {
            return OptionalInt.of(this.ceiling);
        }

        @Override
        public OptionalInt c() {
            return OptionalInt.of(this.floor);
        }

        @Override
        public OptionalInt d() {
            return OptionalInt.of(this.g());
        }

        public int e() {
            return this.ceiling;
        }

        public int f() {
            return this.floor;
        }

        public int g() {
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
        public OptionalInt b() {
            return this.pointingUp ? OptionalInt.empty() : OptionalInt.of(this.edge);
        }

        @Override
        public OptionalInt c() {
            return this.pointingUp ? OptionalInt.of(this.edge) : OptionalInt.empty();
        }

        @Override
        public OptionalInt d() {
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
        public OptionalInt b() {
            return OptionalInt.empty();
        }

        @Override
        public OptionalInt c() {
            return OptionalInt.empty();
        }

        @Override
        public OptionalInt d() {
            return OptionalInt.empty();
        }

        public String toString() {
            return "C(-)";
        }
    }
}
