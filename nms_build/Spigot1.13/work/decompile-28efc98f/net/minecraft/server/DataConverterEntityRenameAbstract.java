package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;

public abstract class DataConverterEntityRenameAbstract extends DataFix {

    private final String a;

    public DataConverterEntityRenameAbstract(String s, Schema schema, boolean flag) {
        super(schema, flag);
        this.a = s;
    }

    public TypeRewriteRule makeRule() {
        TaggedChoiceType taggedchoicetype = this.getInputSchema().findChoiceType(DataConverterTypes.ENTITY);
        TaggedChoiceType taggedchoicetype1 = this.getOutputSchema().findChoiceType(DataConverterTypes.ENTITY);
        Type type = DSL.named(DataConverterTypes.m.typeName(), DSL.namespacedString());

        if (!Objects.equals(this.getOutputSchema().getType(DataConverterTypes.m), type)) {
            throw new IllegalStateException("Entity name type is not what was expected.");
        } else {
            return TypeRewriteRule.seq(this.fixTypeEverywhere(this.a, taggedchoicetype, taggedchoicetype1, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapFirst((s) -> {
                        String s1 = this.a(s);
                        Type type = (Type) taggedchoicetype.types().get(s);
                        Type type1 = (Type) taggedchoicetype1.types().get(s1);

                        if (!type1.equals(type, true, true)) {
                            throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", new Object[] { type1, type}));
                        } else {
                            return s1;
                        }
                    });
                };
            }), this.fixTypeEverywhere(this.a + " for entity name", type, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapSecond(this::a);
                };
            }));
        }
    }

    protected abstract String a(String s);
}
