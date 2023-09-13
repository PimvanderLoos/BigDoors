package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class DataConverterBiomeBase extends DataFix {

    private final String a;
    private final Map<String, String> b;

    public DataConverterBiomeBase(Schema schema, boolean flag, String s, Map<String, String> map) {
        super(schema, flag);
        this.b = map;
        this.a = s;
    }

    protected TypeRewriteRule makeRule() {
        Type<Pair<String, String>> type = DSL.named(DataConverterTypes.BIOME.typeName(), DataConverterSchemaNamed.a());

        if (!Objects.equals(type, this.getInputSchema().getType(DataConverterTypes.BIOME))) {
            throw new IllegalStateException("Biome type is not what was expected.");
        } else {
            return this.fixTypeEverywhere(this.a, type, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapSecond((s) -> {
                        return (String) this.b.getOrDefault(s, s);
                    });
                };
            });
        }
    }
}
