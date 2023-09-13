package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class SimpleRenameFix extends DataFix {

    private final String fixerName;
    private final Map<String, String> nameMapping;
    private final TypeReference typeReference;

    public SimpleRenameFix(Schema schema, TypeReference typereference, Map<String, String> map) {
        this(schema, typereference, typereference.typeName() + "-renames at version: " + schema.getVersionKey(), map);
    }

    public SimpleRenameFix(Schema schema, TypeReference typereference, String s, Map<String, String> map) {
        super(schema, false);
        this.nameMapping = map;
        this.fixerName = s;
        this.typeReference = typereference;
    }

    protected TypeRewriteRule makeRule() {
        Type<Pair<String, String>> type = DSL.named(this.typeReference.typeName(), DataConverterSchemaNamed.namespacedString());

        if (!Objects.equals(type, this.getInputSchema().getType(this.typeReference))) {
            throw new IllegalStateException("\"" + this.typeReference.typeName() + "\" type is not what was expected.");
        } else {
            return this.fixTypeEverywhere(this.fixerName, type, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapSecond((s) -> {
                        return (String) this.nameMapping.getOrDefault(s, s);
                    });
                };
            });
        }
    }
}
