package net.minecraft.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.math.NumberUtils;

public class DataConverterWorldGenSettings extends DataFix {

    private static final Splitter a = Splitter.on(';').limit(5);
    private static final Splitter b = Splitter.on(',');
    private static final Splitter c = Splitter.on('x').limit(2);
    private static final Splitter d = Splitter.on('*').limit(2);
    private static final Splitter e = Splitter.on(':').limit(3);

    public DataConverterWorldGenSettings(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(DataConverterTypes.a), (typed) -> {
            return typed.update(DSL.remainderFinder(), this::a);
        });
    }

    private Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.getString("generatorName").equalsIgnoreCase("flat") ? dynamic.update("generatorOptions", (dynamic) -> {
            Optional optional = dynamic.getStringValue().map(this::a);

            dynamic.getClass();
            return (Dynamic) DataFixUtils.orElse(optional.map(dynamic::createString), dynamic);
        }) : dynamic;
    }

    @VisibleForTesting
    String a(String s) {
        if (s.isEmpty()) {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
        } else {
            Iterator iterator = DataConverterWorldGenSettings.a.split(s).iterator();
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
                Splitter splitter = i < 3 ? DataConverterWorldGenSettings.c : DataConverterWorldGenSettings.d;

                stringbuilder.append((String) StreamSupport.stream(DataConverterWorldGenSettings.b.split(s2).spliterator(), false).map((s) -> {
                    List list = splitter.splitToList(s);
                    int i;
                    String s1;

                    if (list.size() == 2) {
                        i = NumberUtils.toInt((String) list.get(0));
                        s1 = (String) list.get(1);
                    } else {
                        i = 1;
                        s1 = (String) list.get(0);
                    }

                    List list1 = DataConverterWorldGenSettings.e.splitToList(s1);
                    int j = ((String) list1.get(0)).equals("minecraft") ? 1 : 0;
                    String s2 = (String) list1.get(j);
                    int k = l == 3 ? DataConverterEntityBlockState.a("minecraft:" + s2) : NumberUtils.toInt(s2, 0);
                    int i1 = j + 1;
                    int j1 = list1.size() > i1 ? NumberUtils.toInt((String) list1.get(i1), 0) : 0;

                    return (i == 1 ? "" : i + "*") + DataConverterFlattenData.b(k << 4 | j1).getString("Name");
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
