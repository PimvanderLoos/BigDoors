package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

public abstract class DataConverterEntityNameAbstract extends DataConverterEntityName {

    public DataConverterEntityNameAbstract(String s, Schema schema, boolean flag) {
        super(s, schema, flag);
    }

    @Override
    protected Pair<String, Typed<?>> a(String s, Typed<?> typed) {
        Pair<String, Dynamic<?>> pair = this.a(s, (Dynamic) typed.getOrCreate(DSL.remainderFinder()));

        return Pair.of(pair.getFirst(), typed.set(DSL.remainderFinder(), pair.getSecond()));
    }

    protected abstract Pair<String, Dynamic<?>> a(String s, Dynamic<?> dynamic);
}
