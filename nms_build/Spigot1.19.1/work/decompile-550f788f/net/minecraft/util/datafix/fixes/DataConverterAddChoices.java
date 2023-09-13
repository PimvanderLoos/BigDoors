package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Locale;

public class DataConverterAddChoices extends DataFix {

    private final String name;
    private final TypeReference type;

    public DataConverterAddChoices(Schema schema, String s, TypeReference typereference) {
        super(schema, true);
        this.name = s;
        this.type = typereference;
    }

    public TypeRewriteRule makeRule() {
        TaggedChoiceType<?> taggedchoicetype = this.getInputSchema().findChoiceType(this.type);
        TaggedChoiceType<?> taggedchoicetype1 = this.getOutputSchema().findChoiceType(this.type);

        return this.cap(this.name, taggedchoicetype, taggedchoicetype1);
    }

    protected final <K> TypeRewriteRule cap(String s, TaggedChoiceType<K> taggedchoicetype, TaggedChoiceType<?> taggedchoicetype1) {
        if (taggedchoicetype.getKeyType() != taggedchoicetype1.getKeyType()) {
            throw new IllegalStateException("Could not inject: key type is not the same");
        } else {
            return this.fixTypeEverywhere(s, taggedchoicetype, taggedchoicetype1, (dynamicops) -> {
                return (pair) -> {
                    if (!taggedchoicetype1.hasType(pair.getFirst())) {
                        throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown type %s in %s ", pair.getFirst(), this.type));
                    } else {
                        return pair;
                    }
                };
            });
        }
    }
}
