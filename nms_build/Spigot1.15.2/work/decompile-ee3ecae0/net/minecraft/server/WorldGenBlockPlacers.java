package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WorldGenBlockPlacers<P extends WorldGenBlockPlacer> {

    public static final WorldGenBlockPlacers<WorldGenBlockPlacerSimple> a = a("simple_block_placer", WorldGenBlockPlacerSimple::new);
    public static final WorldGenBlockPlacers<WorldGenBlockPlacerDoublePlant> b = a("double_plant_placer", WorldGenBlockPlacerDoublePlant::new);
    public static final WorldGenBlockPlacers<WorldGenBlockPlacerColumn> c = a("column_placer", WorldGenBlockPlacerColumn::new);
    private final Function<Dynamic<?>, P> d;

    private static <P extends WorldGenBlockPlacer> WorldGenBlockPlacers<P> a(String s, Function<Dynamic<?>, P> function) {
        return (WorldGenBlockPlacers) IRegistry.a(IRegistry.u, s, (Object) (new WorldGenBlockPlacers<>(function)));
    }

    private WorldGenBlockPlacers(Function<Dynamic<?>, P> function) {
        this.d = function;
    }

    public P a(Dynamic<?> dynamic) {
        return (WorldGenBlockPlacer) this.d.apply(dynamic);
    }
}
