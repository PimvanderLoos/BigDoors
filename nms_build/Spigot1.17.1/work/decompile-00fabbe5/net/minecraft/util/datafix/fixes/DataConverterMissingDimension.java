package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FieldFinder;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList.CompoundListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class DataConverterMissingDimension extends DataFix {

    public DataConverterMissingDimension(Schema schema, boolean flag) {
        super(schema, flag);
    }

    private static <A> Type<Pair<A, Dynamic<?>>> a(String s, Type<A> type) {
        return DSL.and(DSL.field(s, type), DSL.remainderType());
    }

    private static <A> Type<Pair<Either<A, Unit>, Dynamic<?>>> b(String s, Type<A> type) {
        return DSL.and(DSL.optional(DSL.field(s, type)), DSL.remainderType());
    }

    private static <A1, A2> Type<Pair<Either<A1, Unit>, Pair<Either<A2, Unit>, Dynamic<?>>>> a(String s, Type<A1> type, String s1, Type<A2> type1) {
        return DSL.and(DSL.optional(DSL.field(s, type)), DSL.optional(DSL.field(s1, type1)), DSL.remainderType());
    }

    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        TaggedChoiceType<String> taggedchoicetype = new TaggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL.remainderType(), "minecraft:flat", b("settings", a("biome", schema.getType(DataConverterTypes.BIOME), "layers", (Type) DSL.list(b("block", schema.getType(DataConverterTypes.BLOCK_NAME))))), "minecraft:noise", a("biome_source", DSL.taggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:fixed", a("biome", schema.getType(DataConverterTypes.BIOME)), "minecraft:multi_noise", DSL.list(a("biome", schema.getType(DataConverterTypes.BIOME))), "minecraft:checkerboard", a("biomes", DSL.list(schema.getType(DataConverterTypes.BIOME))), "minecraft:vanilla_layered", DSL.remainderType(), "minecraft:the_end", DSL.remainderType())), "settings", DSL.or(DSL.string(), a("default_block", schema.getType(DataConverterTypes.BLOCK_NAME), "default_fluid", schema.getType(DataConverterTypes.BLOCK_NAME))))));
        CompoundListType<String, ?> compoundlisttype = DSL.compoundList(DataConverterSchemaNamed.a(), a("generator", taggedchoicetype));
        Type<?> type = DSL.and(compoundlisttype, DSL.remainderType());
        Type<?> type1 = schema.getType(DataConverterTypes.WORLD_GEN_SETTINGS);
        FieldFinder<?> fieldfinder = new FieldFinder("dimensions", type);

        if (!type1.findFieldType("dimensions").equals(type)) {
            throw new IllegalStateException();
        } else {
            OpticFinder<? extends List<? extends Pair<String, ?>>> opticfinder = compoundlisttype.finder();

            return this.fixTypeEverywhereTyped("MissingDimensionFix", type1, (typed) -> {
                return typed.updateTyped(fieldfinder, (typed1) -> {
                    return typed1.updateTyped(opticfinder, (typed2) -> {
                        if (!(typed2.getValue() instanceof List)) {
                            throw new IllegalStateException("List exptected");
                        } else if (((List) typed2.getValue()).isEmpty()) {
                            Dynamic<?> dynamic = (Dynamic) typed.get(DSL.remainderFinder());
                            Dynamic<?> dynamic1 = this.a(dynamic);

                            return (Typed) DataFixUtils.orElse(compoundlisttype.readTyped(dynamic1).result().map(Pair::getFirst), typed2);
                        } else {
                            return typed2;
                        }
                    });
                });
            });
        }
    }

    private <T> Dynamic<T> a(Dynamic<T> dynamic) {
        long i = dynamic.get("seed").asLong(0L);

        return new Dynamic(dynamic.getOps(), DataConverterWorldGenSettingsBuilding.a(dynamic, i, DataConverterWorldGenSettingsBuilding.a(dynamic, i), false));
    }
}
