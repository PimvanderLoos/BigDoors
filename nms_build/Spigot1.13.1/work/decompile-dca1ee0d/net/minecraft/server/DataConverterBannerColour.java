package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class DataConverterBannerColour extends DataConverterNamedEntity {

    public DataConverterBannerColour(Schema schema, boolean flag) {
        super(schema, flag, "BlockEntityBannerColorFix", DataConverterTypes.j, "minecraft:banner");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        dynamic = dynamic.update("Base", (dynamic) -> {
            return dynamic.createInt(15 - dynamic.getNumberValue(Integer.valueOf(0)).intValue());
        });
        dynamic = dynamic.update("Patterns", (dynamic) -> {
            Optional optional = dynamic.getStream().map((stream) -> {
                return stream.map((dynamic) -> {
                    return dynamic.update("Color", (dynamicx) -> {
                        return dynamicx.createInt(15 - dynamicx.getNumberValue(Integer.valueOf(0)).intValue());
                    });
                });
            });

            dynamic.getClass();
            return (Dynamic) DataFixUtils.orElse(optional.map(dynamic::createList), dynamic);
        });
        return dynamic;
    }

    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }
}
