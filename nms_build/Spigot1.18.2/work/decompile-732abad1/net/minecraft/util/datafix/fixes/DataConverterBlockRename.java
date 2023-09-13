package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public abstract class DataConverterBlockRename extends DataFix {

    private final String name;

    public DataConverterBlockRename(Schema schema, String s) {
        super(schema, false);
        this.name = s;
    }

    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.BLOCK_NAME);
        Type<Pair<String, String>> type1 = DSL.named(DataConverterTypes.BLOCK_NAME.typeName(), DataConverterSchemaNamed.namespacedString());

        if (!Objects.equals(type, type1)) {
            throw new IllegalStateException("block type is not what was expected.");
        } else {
            TypeRewriteRule typerewriterule = this.fixTypeEverywhere(this.name + " for block", type1, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapSecond(this::fixBlock);
                };
            });
            TypeRewriteRule typerewriterule1 = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(DataConverterTypes.BLOCK_STATE), (typed) -> {
                return typed.update(DSL.remainderFinder(), (dynamic) -> {
                    Optional<String> optional = dynamic.get("Name").asString().result();

                    return optional.isPresent() ? dynamic.set("Name", dynamic.createString(this.fixBlock((String) optional.get()))) : dynamic;
                });
            });

            return TypeRewriteRule.seq(typerewriterule, typerewriterule1);
        }
    }

    protected abstract String fixBlock(String s);

    public static DataFix create(Schema schema, String s, final Function<String, String> function) {
        return new DataConverterBlockRename(schema, s) {
            @Override
            protected String fixBlock(String s1) {
                return (String) function.apply(s1);
            }
        };
    }
}
