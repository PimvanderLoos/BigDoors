package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

public class DataConverterMemoryExpiry extends DataConverterNamedEntity {

    public DataConverterMemoryExpiry(Schema schema, String s) {
        super(schema, false, "Memory expiry data fix (" + s + ")", DataConverterTypes.ENTITY, s);
    }

    @Override
    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.update("Brain", this::b);
    }

    private Dynamic<?> b(Dynamic<?> dynamic) {
        return dynamic.update("memories", this::c);
    }

    private Dynamic<?> c(Dynamic<?> dynamic) {
        return dynamic.updateMapValues(this::a);
    }

    private Pair<Dynamic<?>, Dynamic<?>> a(Pair<Dynamic<?>, Dynamic<?>> pair) {
        return pair.mapSecond(this::d);
    }

    private Dynamic<?> d(Dynamic<?> dynamic) {
        return dynamic.createMap(ImmutableMap.of(dynamic.createString("value"), dynamic));
    }
}
