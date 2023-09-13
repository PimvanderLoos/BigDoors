package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class DataConverterRecipeBase extends DataFix {

    private final String name;
    private final Function<String, String> renamer;

    public DataConverterRecipeBase(Schema schema, boolean flag, String s, Function<String, String> function) {
        super(schema, flag);
        this.name = s;
        this.renamer = function;
    }

    protected TypeRewriteRule makeRule() {
        Type<Pair<String, String>> type = DSL.named(DataConverterTypes.RECIPE.typeName(), DataConverterSchemaNamed.a());

        if (!Objects.equals(type, this.getInputSchema().getType(DataConverterTypes.RECIPE))) {
            throw new IllegalStateException("Recipe type is not what was expected.");
        } else {
            return this.fixTypeEverywhere(this.name, type, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapSecond(this.renamer);
                };
            });
        }
    }
}
