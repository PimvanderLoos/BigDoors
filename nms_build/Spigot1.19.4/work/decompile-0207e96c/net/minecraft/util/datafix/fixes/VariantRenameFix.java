package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;

public class VariantRenameFix extends DataConverterNamedEntity {

    private final Map<String, String> renames;

    public VariantRenameFix(Schema schema, String s, TypeReference typereference, String s1, Map<String, String> map) {
        super(schema, false, s, typereference, s1);
        this.renames = map;
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.update("variant", (dynamic1) -> {
                return (Dynamic) DataFixUtils.orElse(dynamic1.asString().map((s) -> {
                    return dynamic1.createString((String) this.renames.getOrDefault(s, s));
                }).result(), dynamic1);
            });
        });
    }
}
