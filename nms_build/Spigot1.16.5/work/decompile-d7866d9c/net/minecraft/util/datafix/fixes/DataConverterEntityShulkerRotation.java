package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.stream.Stream;

public class DataConverterEntityShulkerRotation extends DataConverterNamedEntity {

    public DataConverterEntityShulkerRotation(Schema schema) {
        super(schema, false, "EntityShulkerRotationFix", DataConverterTypes.ENTITY, "minecraft:shulker");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        List<Double> list = dynamic.get("Rotation").asList((dynamic1) -> {
            return dynamic1.asDouble(180.0D);
        });

        if (!list.isEmpty()) {
            list.set(0, (Double) list.get(0) - 180.0D);
            Stream stream = list.stream();

            dynamic.getClass();
            return dynamic.set("Rotation", dynamic.createList(stream.map(dynamic::createDouble)));
        } else {
            return dynamic;
        }
    }

    @Override
    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }
}
