package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class TrunkPlacers<P extends TrunkPlacer> {

    public static final TrunkPlacers<TrunkPlacerStraight> a = a("straight_trunk_placer", TrunkPlacerStraight.a);
    public static final TrunkPlacers<TrunkPlacerForking> b = a("forking_trunk_placer", TrunkPlacerForking.a);
    public static final TrunkPlacers<TrunkPlacerGiant> c = a("giant_trunk_placer", TrunkPlacerGiant.a);
    public static final TrunkPlacers<TrunkPlacerMegaJungle> d = a("mega_jungle_trunk_placer", TrunkPlacerMegaJungle.b);
    public static final TrunkPlacers<TrunkPlacerDarkOak> e = a("dark_oak_trunk_placer", TrunkPlacerDarkOak.a);
    public static final TrunkPlacers<TrunkPlacerFancy> f = a("fancy_trunk_placer", TrunkPlacerFancy.a);
    private final Codec<P> g;

    private static <P extends TrunkPlacer> TrunkPlacers<P> a(String s, Codec<P> codec) {
        return (TrunkPlacers) IRegistry.a(IRegistry.TRUNK_PLACER_TYPE, s, (Object) (new TrunkPlacers<>(codec)));
    }

    private TrunkPlacers(Codec<P> codec) {
        this.g = codec;
    }

    public Codec<P> a() {
        return this.g;
    }
}
