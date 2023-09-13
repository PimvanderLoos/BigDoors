package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionManager;

public interface VerticalAnchor {

    Codec<VerticalAnchor> CODEC = ExtraCodecs.xor(VerticalAnchor.b.CODEC, ExtraCodecs.xor(VerticalAnchor.a.CODEC, VerticalAnchor.c.CODEC)).xmap(VerticalAnchor::merge, VerticalAnchor::split);
    VerticalAnchor BOTTOM = aboveBottom(0);
    VerticalAnchor TOP = belowTop(0);

    static VerticalAnchor absolute(int i) {
        return new VerticalAnchor.b(i);
    }

    static VerticalAnchor aboveBottom(int i) {
        return new VerticalAnchor.a(i);
    }

    static VerticalAnchor belowTop(int i) {
        return new VerticalAnchor.c(i);
    }

    static VerticalAnchor bottom() {
        return VerticalAnchor.BOTTOM;
    }

    static VerticalAnchor top() {
        return VerticalAnchor.TOP;
    }

    private static VerticalAnchor merge(Either<VerticalAnchor.b, Either<VerticalAnchor.a, VerticalAnchor.c>> either) {
        return (VerticalAnchor) either.map(Function.identity(), (either1) -> {
            return (Record) either1.map(Function.identity(), Function.identity());
        });
    }

    private static Either<VerticalAnchor.b, Either<VerticalAnchor.a, VerticalAnchor.c>> split(VerticalAnchor verticalanchor) {
        return verticalanchor instanceof VerticalAnchor.b ? Either.left((VerticalAnchor.b) verticalanchor) : Either.right(verticalanchor instanceof VerticalAnchor.a ? Either.left((VerticalAnchor.a) verticalanchor) : Either.right((VerticalAnchor.c) verticalanchor));
    }

    int resolveY(WorldGenerationContext worldgenerationcontext);

    public static record b(int y) implements VerticalAnchor {

        public static final Codec<VerticalAnchor.b> CODEC = Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("absolute").xmap(VerticalAnchor.b::new, VerticalAnchor.b::y).codec();

        @Override
        public int resolveY(WorldGenerationContext worldgenerationcontext) {
            return this.y;
        }

        public String toString() {
            return this.y + " absolute";
        }
    }

    public static record a(int offset) implements VerticalAnchor {

        public static final Codec<VerticalAnchor.a> CODEC = Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("above_bottom").xmap(VerticalAnchor.a::new, VerticalAnchor.a::offset).codec();

        @Override
        public int resolveY(WorldGenerationContext worldgenerationcontext) {
            return worldgenerationcontext.getMinGenY() + this.offset;
        }

        public String toString() {
            return this.offset + " above bottom";
        }
    }

    public static record c(int offset) implements VerticalAnchor {

        public static final Codec<VerticalAnchor.c> CODEC = Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("below_top").xmap(VerticalAnchor.c::new, VerticalAnchor.c::offset).codec();

        @Override
        public int resolveY(WorldGenerationContext worldgenerationcontext) {
            return worldgenerationcontext.getGenDepth() - 1 + worldgenerationcontext.getMinGenY() - this.offset;
        }

        public String toString() {
            return this.offset + " below top";
        }
    }
}
