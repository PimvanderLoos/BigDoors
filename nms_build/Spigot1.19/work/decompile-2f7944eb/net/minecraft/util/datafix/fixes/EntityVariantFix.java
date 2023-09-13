package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;
import java.util.function.IntFunction;

public class EntityVariantFix extends DataConverterNamedEntity {

    private final String fieldName;
    private final IntFunction<String> idConversions;

    public EntityVariantFix(Schema schema, String s, TypeReference typereference, String s1, String s2, IntFunction<String> intfunction) {
        super(schema, false, s, typereference, s1);
        this.fieldName = s2;
        this.idConversions = intfunction;
    }

    private static <T> Dynamic<T> updateAndRename(Dynamic<T> dynamic, String s, String s1, Function<Dynamic<T>, Dynamic<T>> function) {
        return dynamic.map((object) -> {
            DynamicOps<T> dynamicops = dynamic.getOps();
            Function<T, T> function1 = (object1) -> {
                return ((Dynamic) function.apply(new Dynamic(dynamicops, object1))).getValue();
            };

            return dynamicops.get(object, s).map((object1) -> {
                return dynamicops.set(object, s1, function1.apply(object1));
            }).result().orElse(object);
        });
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return updateAndRename(dynamic, this.fieldName, "variant", (dynamic1) -> {
                return (Dynamic) DataFixUtils.orElse(dynamic1.asNumber().map((number) -> {
                    return dynamic1.createString((String) this.idConversions.apply(number.intValue()));
                }).result(), dynamic1);
            });
        });
    }
}
