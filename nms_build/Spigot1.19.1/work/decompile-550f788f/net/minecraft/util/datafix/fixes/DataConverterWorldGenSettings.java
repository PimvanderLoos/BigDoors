package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.math.NumberUtils;

public class DataConverterWorldGenSettings extends DataFix {

    private static final String GENERATOR_OPTIONS = "generatorOptions";
    @VisibleForTesting
    static final String DEFAULT = "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
    private static final Splitter SPLITTER = Splitter.on(';').limit(5);
    private static final Splitter LAYER_SPLITTER = Splitter.on(',');
    private static final Splitter OLD_AMOUNT_SPLITTER = Splitter.on('x').limit(2);
    private static final Splitter AMOUNT_SPLITTER = Splitter.on('*').limit(2);
    private static final Splitter BLOCK_SPLITTER = Splitter.on(':').limit(3);

    public DataConverterWorldGenSettings(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(DataConverterTypes.LEVEL), (typed) -> {
            return typed.update(DSL.remainderFinder(), this::fix);
        });
    }

    private Dynamic<?> fix(Dynamic<?> dynamic) {
        return dynamic.get("generatorName").asString("").equalsIgnoreCase("flat") ? dynamic.update("generatorOptions", (dynamic1) -> {
            DataResult dataresult = dynamic1.asString().map(this::fixString);

            Objects.requireNonNull(dynamic1);
            return (Dynamic) DataFixUtils.orElse(dataresult.map(dynamic1::createString).result(), dynamic1);
        }) : dynamic;
    }

    @VisibleForTesting
    String fixString(String s) {
        if (s.isEmpty()) {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
        } else {
            Iterator<String> iterator = DataConverterWorldGenSettings.SPLITTER.split(s).iterator();
            String s1 = (String) iterator.next();
            int i;
            String s2;

            if (iterator.hasNext()) {
                i = NumberUtils.toInt(s1, 0);
                s2 = (String) iterator.next();
            } else {
                i = 0;
                s2 = s1;
            }

            if (i >= 0 && i <= 3) {
                StringBuilder stringbuilder = new StringBuilder();
                Splitter splitter = i < 3 ? DataConverterWorldGenSettings.OLD_AMOUNT_SPLITTER : DataConverterWorldGenSettings.AMOUNT_SPLITTER;

                stringbuilder.append((String) StreamSupport.stream(DataConverterWorldGenSettings.LAYER_SPLITTER.split(s2).spliterator(), false).map((s3) -> {
                    List<String> list = splitter.splitToList(s3);
                    int j;
                    String s4;

                    if (list.size() == 2) {
                        j = NumberUtils.toInt((String) list.get(0));
                        s4 = (String) list.get(1);
                    } else {
                        j = 1;
                        s4 = (String) list.get(0);
                    }

                    List<String> list1 = DataConverterWorldGenSettings.BLOCK_SPLITTER.splitToList(s4);
                    int k = ((String) list1.get(0)).equals("minecraft") ? 1 : 0;
                    String s5 = (String) list1.get(k);
                    int l = i == 3 ? DataConverterEntityBlockState.getBlockId("minecraft:" + s5) : NumberUtils.toInt(s5, 0);
                    int i1 = k + 1;
                    int j1 = list1.size() > i1 ? NumberUtils.toInt((String) list1.get(i1), 0) : 0;
                    String s6 = j == 1 ? "" : j + "*";

                    return s6 + DataConverterFlattenData.getTag(l << 4 | j1).get("Name").asString("");
                }).collect(Collectors.joining(",")));

                while (iterator.hasNext()) {
                    stringbuilder.append(';').append((String) iterator.next());
                }

                return stringbuilder.toString();
            } else {
                return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
            }
        }
    }
}
