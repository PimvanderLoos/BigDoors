package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class DataConverterBannerColour extends DataConverterNamedEntity {

    public DataConverterBannerColour(Schema schema, boolean flag) {
        super(schema, flag, "BlockEntityBannerColorFix", DataConverterTypes.BLOCK_ENTITY, "minecraft:banner");
    }

    public Dynamic<?> fixTag(Dynamic<?> dynamic) {
        dynamic = dynamic.update("Base", (dynamic1) -> {
            return dynamic1.createInt(15 - dynamic1.asInt(0));
        });
        dynamic = dynamic.update("Patterns", (dynamic1) -> {
            DataResult dataresult = dynamic1.asStreamOpt().map((stream) -> {
                return stream.map((dynamic2) -> {
                    return dynamic2.update("Color", (dynamic3) -> {
                        return dynamic3.createInt(15 - dynamic3.asInt(0));
                    });
                });
            });

            Objects.requireNonNull(dynamic1);
            return (Dynamic) DataFixUtils.orElse(dataresult.map(dynamic1::createList).result(), dynamic1);
        });
        return dynamic;
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixTag);
    }
}
