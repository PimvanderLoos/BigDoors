package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Locale;

public abstract class DataConverterEntityName extends DataFix {

    protected final String name;

    public DataConverterEntityName(String s, Schema schema, boolean flag) {
        super(schema, flag);
        this.name = s;
    }

    public TypeRewriteRule makeRule() {
        TaggedChoiceType<String> taggedchoicetype = this.getInputSchema().findChoiceType(DataConverterTypes.ENTITY);
        TaggedChoiceType<String> taggedchoicetype1 = this.getOutputSchema().findChoiceType(DataConverterTypes.ENTITY);

        return this.fixTypeEverywhere(this.name, taggedchoicetype, taggedchoicetype1, (dynamicops) -> {
            return (pair) -> {
                String s = (String) pair.getFirst();
                Type<?> type = (Type) taggedchoicetype.types().get(s);
                Pair<String, Typed<?>> pair1 = this.fix(s, this.getEntity(pair.getSecond(), dynamicops, type));
                Type<?> type1 = (Type) taggedchoicetype1.types().get(pair1.getFirst());

                if (!type1.equals(((Typed) pair1.getSecond()).getType(), true, true)) {
                    throw new IllegalStateException(String.format(Locale.ROOT, "Dynamic type check failed: %s not equal to %s", type1, ((Typed) pair1.getSecond()).getType()));
                } else {
                    return Pair.of((String) pair1.getFirst(), ((Typed) pair1.getSecond()).getValue());
                }
            };
        });
    }

    private <A> Typed<A> getEntity(Object object, DynamicOps<?> dynamicops, Type<A> type) {
        return new Typed(type, dynamicops, object);
    }

    protected abstract Pair<String, Typed<?>> fix(String s, Typed<?> typed);
}
