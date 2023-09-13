package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class BlockRenameFixWithJigsaw extends DataConverterBlockRename {

    private final String name;

    public BlockRenameFixWithJigsaw(Schema schema, String s) {
        super(schema, s);
        this.name = s;
    }

    @Override
    public TypeRewriteRule makeRule() {
        TypeReference typereference = DataConverterTypes.BLOCK_ENTITY;
        String s = "minecraft:jigsaw";
        OpticFinder<?> opticfinder = DSL.namedChoice("minecraft:jigsaw", this.getInputSchema().getChoiceType(typereference, "minecraft:jigsaw"));
        TypeRewriteRule typerewriterule = this.fixTypeEverywhereTyped(this.name + " for jigsaw state", this.getInputSchema().getType(typereference), this.getOutputSchema().getType(typereference), (typed) -> {
            return typed.updateTyped(opticfinder, this.getOutputSchema().getChoiceType(typereference, "minecraft:jigsaw"), (typed1) -> {
                return typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    return dynamic.update("final_state", (dynamic1) -> {
                        Optional optional = dynamic1.asString().result().map((s1) -> {
                            int i = s1.indexOf(91);
                            int j = s1.indexOf(123);
                            int k = s1.length();

                            if (i > 0) {
                                k = Math.min(k, i);
                            }

                            if (j > 0) {
                                k = Math.min(k, j);
                            }

                            String s2 = s1.substring(0, k);
                            String s3 = this.fixBlock(s2);

                            return s3 + s1.substring(k);
                        });

                        Objects.requireNonNull(dynamic);
                        return (Dynamic) DataFixUtils.orElse(optional.map(dynamic::createString), dynamic1);
                    });
                });
            });
        });

        return TypeRewriteRule.seq(super.makeRule(), typerewriterule);
    }

    public static DataFix create(Schema schema, String s, final Function<String, String> function) {
        return new BlockRenameFixWithJigsaw(schema, s) {
            @Override
            protected String fixBlock(String s1) {
                return (String) function.apply(s1);
            }
        };
    }
}
