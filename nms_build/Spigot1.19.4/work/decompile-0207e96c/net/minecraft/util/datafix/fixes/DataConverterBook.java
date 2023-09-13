package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.ChatDeserializer;
import org.apache.commons.lang3.StringUtils;

public class DataConverterBook extends DataFix {

    public DataConverterBook(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public Dynamic<?> fixTag(Dynamic<?> dynamic) {
        return dynamic.update("pages", (dynamic1) -> {
            DataResult dataresult = dynamic1.asStreamOpt().map((stream) -> {
                return stream.map((dynamic2) -> {
                    if (!dynamic2.asString().result().isPresent()) {
                        return dynamic2;
                    } else {
                        String s = dynamic2.asString("");
                        Object object = null;

                        if (!"null".equals(s) && !StringUtils.isEmpty(s)) {
                            if ((s.charAt(0) != '"' || s.charAt(s.length() - 1) != '"') && (s.charAt(0) != '{' || s.charAt(s.length() - 1) != '}')) {
                                object = IChatBaseComponent.literal(s);
                            } else {
                                try {
                                    object = (IChatBaseComponent) ChatDeserializer.fromNullableJson(DataConverterSignText.GSON, s, IChatBaseComponent.class, true);
                                    if (object == null) {
                                        object = CommonComponents.EMPTY;
                                    }
                                } catch (Exception exception) {
                                    ;
                                }

                                if (object == null) {
                                    try {
                                        object = IChatBaseComponent.ChatSerializer.fromJson(s);
                                    } catch (Exception exception1) {
                                        ;
                                    }
                                }

                                if (object == null) {
                                    try {
                                        object = IChatBaseComponent.ChatSerializer.fromJsonLenient(s);
                                    } catch (Exception exception2) {
                                        ;
                                    }
                                }

                                if (object == null) {
                                    object = IChatBaseComponent.literal(s);
                                }
                            }
                        } else {
                            object = CommonComponents.EMPTY;
                        }

                        return dynamic2.createString(IChatBaseComponent.ChatSerializer.toJson((IChatBaseComponent) object));
                    }
                });
            });

            Objects.requireNonNull(dynamic);
            return (Dynamic) DataFixUtils.orElse(dataresult.map(dynamic::createList).result(), dynamic.emptyList());
        });
    }

    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.ITEM_STACK);
        OpticFinder<?> opticfinder = type.findField("tag");

        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", type, (typed) -> {
            return typed.updateTyped(opticfinder, (typed1) -> {
                return typed1.update(DSL.remainderFinder(), this::fixTag);
            });
        });
    }
}
