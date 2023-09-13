package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.stream.Stream;

public class DataConverterBannerColour extends DataConverterNamedEntity {

    public DataConverterBannerColour(Schema schema, boolean flag) {
        super(schema, flag, "BlockEntityBannerColorFix", DataConverterTypes.j, "minecraft:banner");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        dynamic = dynamic.update("Base", (dynamic1) -> {
            return dynamic1.createInt(15 - dynamic1.getNumberValue(0).intValue());
        });
        dynamic = dynamic.update("Patterns", (dynamic1) -> {
            Optional optional = dynamic1.getStream().map((stream) -> {
                return stream.map((dynamic2) -> {
                    return dynamic2.update("Color", (dynamic3) -> {
                        return dynamic3.createInt(15 - dynamic3.getNumberValue(0).intValue());
                    });
                });
            });

            dynamic1.getClass();
            return (Dynamic) DataFixUtils.orElse(optional.map(dynamic1::createList), dynamic1);
        });
        return dynamic;
    }

    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }
}
