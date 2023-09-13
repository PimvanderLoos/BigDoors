package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionManager;

public abstract class VerticalAnchor {

    public static final Codec<VerticalAnchor> CODEC = ExtraCodecs.a(VerticalAnchor.b.CODEC, ExtraCodecs.a(VerticalAnchor.a.CODEC, VerticalAnchor.c.CODEC)).xmap(VerticalAnchor::a, VerticalAnchor::a);
    private static final VerticalAnchor BOTTOM = b(0);
    private static final VerticalAnchor TOP = c(0);
    private final int value;

    protected VerticalAnchor(int i) {
        this.value = i;
    }

    public static VerticalAnchor a(int i) {
        return new VerticalAnchor.b(i);
    }

    public static VerticalAnchor b(int i) {
        return new VerticalAnchor.a(i);
    }

    public static VerticalAnchor c(int i) {
        return new VerticalAnchor.c(i);
    }

    public static VerticalAnchor a() {
        return VerticalAnchor.BOTTOM;
    }

    public static VerticalAnchor b() {
        return VerticalAnchor.TOP;
    }

    private static VerticalAnchor a(Either<VerticalAnchor.b, Either<VerticalAnchor.a, VerticalAnchor.c>> either) {
        return (VerticalAnchor) either.map(Function.identity(), (either1) -> {
            return (VerticalAnchor) either1.map(Function.identity(), Function.identity());
        });
    }

    private static Either<VerticalAnchor.b, Either<VerticalAnchor.a, VerticalAnchor.c>> a(VerticalAnchor verticalanchor) {
        return verticalanchor instanceof VerticalAnchor.b ? Either.left((VerticalAnchor.b) verticalanchor) : Either.right(verticalanchor instanceof VerticalAnchor.a ? Either.left((VerticalAnchor.a) verticalanchor) : Either.right((VerticalAnchor.c) verticalanchor));
    }

    protected int c() {
        return this.value;
    }

    public abstract int a(WorldGenerationContext worldgenerationcontext);

    private static final class b extends VerticalAnchor {

        public static final Codec<VerticalAnchor.b> CODEC = Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("absolute").xmap(VerticalAnchor.b::new, VerticalAnchor::c).codec();

        protected b(int i) {
            super(i);
        }

        @Override
        public int a(WorldGenerationContext worldgenerationcontext) {
            return this.c();
        }

        public String toString() {
            return this.c() + " absolute";
        }
    }

    private static final class a extends VerticalAnchor {

        public static final Codec<VerticalAnchor.a> CODEC = Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("above_bottom").xmap(VerticalAnchor.a::new, VerticalAnchor::c).codec();

        protected a(int i) {
            super(i);
        }

        @Override
        public int a(WorldGenerationContext worldgenerationcontext) {
            return worldgenerationcontext.a() + this.c();
        }

        public String toString() {
            return this.c() + " above bottom";
        }
    }

    private static final class c extends VerticalAnchor {

        public static final Codec<VerticalAnchor.c> CODEC = Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("below_top").xmap(VerticalAnchor.c::new, VerticalAnchor::c).codec();

        protected c(int i) {
            super(i);
        }

        @Override
        public int a(WorldGenerationContext worldgenerationcontext) {
            return worldgenerationcontext.b() - 1 + worldgenerationcontext.a() - this.c();
        }

        public String toString() {
            return this.c() + " below top";
        }
    }
}
