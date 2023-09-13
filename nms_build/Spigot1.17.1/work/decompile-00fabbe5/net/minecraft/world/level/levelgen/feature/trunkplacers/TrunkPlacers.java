package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class TrunkPlacers<P extends TrunkPlacer> {

    public static final TrunkPlacers<TrunkPlacerStraight> STRAIGHT_TRUNK_PLACER = a("straight_trunk_placer", TrunkPlacerStraight.CODEC);
    public static final TrunkPlacers<TrunkPlacerForking> FORKING_TRUNK_PLACER = a("forking_trunk_placer", TrunkPlacerForking.CODEC);
    public static final TrunkPlacers<TrunkPlacerGiant> GIANT_TRUNK_PLACER = a("giant_trunk_placer", TrunkPlacerGiant.CODEC);
    public static final TrunkPlacers<TrunkPlacerMegaJungle> MEGA_JUNGLE_TRUNK_PLACER = a("mega_jungle_trunk_placer", TrunkPlacerMegaJungle.CODEC);
    public static final TrunkPlacers<TrunkPlacerDarkOak> DARK_OAK_TRUNK_PLACER = a("dark_oak_trunk_placer", TrunkPlacerDarkOak.CODEC);
    public static final TrunkPlacers<TrunkPlacerFancy> FANCY_TRUNK_PLACER = a("fancy_trunk_placer", TrunkPlacerFancy.CODEC);
    public static final TrunkPlacers<BendingTrunkPlacer> BENDING_TRUNK_PLACER = a("bending_trunk_placer", BendingTrunkPlacer.CODEC);
    private final Codec<P> codec;

    private static <P extends TrunkPlacer> TrunkPlacers<P> a(String s, Codec<P> codec) {
        return (TrunkPlacers) IRegistry.a(IRegistry.TRUNK_PLACER_TYPES, s, (Object) (new TrunkPlacers<>(codec)));
    }

    private TrunkPlacers(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> a() {
        return this.codec;
    }
}
