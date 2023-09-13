package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;

public class DataConverterGossip extends DataConverterNamedEntity {

    public DataConverterGossip(Schema schema, String s) {
        super(schema, false, "Gossip for for " + s, DataConverterTypes.ENTITY, s);
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.update("Gossips", (dynamic1) -> {
                Optional optional = dynamic1.asStreamOpt().result().map((stream) -> {
                    return stream.map((dynamic2) -> {
                        return (Dynamic) DataConverterUUIDBase.replaceUUIDLeastMost(dynamic2, "Target", "Target").orElse(dynamic2);
                    });
                });

                Objects.requireNonNull(dynamic1);
                return (Dynamic) DataFixUtils.orElse(optional.map(dynamic1::createList), dynamic1);
            });
        });
    }
}
