package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootItemFunctionSetAttribute extends LootItemFunctionConditional {

    final List<LootItemFunctionSetAttribute.b> modifiers;

    LootItemFunctionSetAttribute(LootItemCondition[] alootitemcondition, List<LootItemFunctionSetAttribute.b> list) {
        super(alootitemcondition);
        this.modifiers = ImmutableList.copyOf(list);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_ATTRIBUTES;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return (Set) this.modifiers.stream().flatMap((lootitemfunctionsetattribute_b) -> {
            return lootitemfunctionsetattribute_b.amount.getReferencedContextParams().stream();
        }).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        RandomSource randomsource = loottableinfo.getRandom();
        Iterator iterator = this.modifiers.iterator();

        while (iterator.hasNext()) {
            LootItemFunctionSetAttribute.b lootitemfunctionsetattribute_b = (LootItemFunctionSetAttribute.b) iterator.next();
            UUID uuid = lootitemfunctionsetattribute_b.id;

            if (uuid == null) {
                uuid = UUID.randomUUID();
            }

            EnumItemSlot enumitemslot = (EnumItemSlot) SystemUtils.getRandom((Object[]) lootitemfunctionsetattribute_b.slots, randomsource);

            itemstack.addAttributeModifier(lootitemfunctionsetattribute_b.attribute, new AttributeModifier(uuid, lootitemfunctionsetattribute_b.name, (double) lootitemfunctionsetattribute_b.amount.getFloat(loottableinfo), lootitemfunctionsetattribute_b.operation), enumitemslot);
        }

        return itemstack;
    }

    public static LootItemFunctionSetAttribute.c modifier(String s, AttributeBase attributebase, AttributeModifier.Operation attributemodifier_operation, NumberProvider numberprovider) {
        return new LootItemFunctionSetAttribute.c(s, attributebase, attributemodifier_operation, numberprovider);
    }

    public static LootItemFunctionSetAttribute.a setAttributes() {
        return new LootItemFunctionSetAttribute.a();
    }

    private static class b {

        final String name;
        final AttributeBase attribute;
        final AttributeModifier.Operation operation;
        final NumberProvider amount;
        @Nullable
        final UUID id;
        final EnumItemSlot[] slots;

        b(String s, AttributeBase attributebase, AttributeModifier.Operation attributemodifier_operation, NumberProvider numberprovider, EnumItemSlot[] aenumitemslot, @Nullable UUID uuid) {
            this.name = s;
            this.attribute = attributebase;
            this.operation = attributemodifier_operation;
            this.amount = numberprovider;
            this.id = uuid;
            this.slots = aenumitemslot;
        }

        public JsonObject serialize(JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("name", this.name);
            jsonobject.addProperty("attribute", BuiltInRegistries.ATTRIBUTE.getKey(this.attribute).toString());
            jsonobject.addProperty("operation", operationToString(this.operation));
            jsonobject.add("amount", jsonserializationcontext.serialize(this.amount));
            if (this.id != null) {
                jsonobject.addProperty("id", this.id.toString());
            }

            if (this.slots.length == 1) {
                jsonobject.addProperty("slot", this.slots[0].getName());
            } else {
                JsonArray jsonarray = new JsonArray();
                EnumItemSlot[] aenumitemslot = this.slots;
                int i = aenumitemslot.length;

                for (int j = 0; j < i; ++j) {
                    EnumItemSlot enumitemslot = aenumitemslot[j];

                    jsonarray.add(new JsonPrimitive(enumitemslot.getName()));
                }

                jsonobject.add("slot", jsonarray);
            }

            return jsonobject;
        }

        public static LootItemFunctionSetAttribute.b deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            String s = ChatDeserializer.getAsString(jsonobject, "name");
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "attribute"));
            AttributeBase attributebase = (AttributeBase) BuiltInRegistries.ATTRIBUTE.get(minecraftkey);

            if (attributebase == null) {
                throw new JsonSyntaxException("Unknown attribute: " + minecraftkey);
            } else {
                AttributeModifier.Operation attributemodifier_operation = operationFromString(ChatDeserializer.getAsString(jsonobject, "operation"));
                NumberProvider numberprovider = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "amount", jsondeserializationcontext, NumberProvider.class);
                UUID uuid = null;
                EnumItemSlot[] aenumitemslot;

                if (ChatDeserializer.isStringValue(jsonobject, "slot")) {
                    aenumitemslot = new EnumItemSlot[]{EnumItemSlot.byName(ChatDeserializer.getAsString(jsonobject, "slot"))};
                } else {
                    if (!ChatDeserializer.isArrayNode(jsonobject, "slot")) {
                        throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
                    }

                    JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "slot");

                    aenumitemslot = new EnumItemSlot[jsonarray.size()];
                    int i = 0;

                    JsonElement jsonelement;

                    for (Iterator iterator = jsonarray.iterator(); iterator.hasNext(); aenumitemslot[i++] = EnumItemSlot.byName(ChatDeserializer.convertToString(jsonelement, "slot"))) {
                        jsonelement = (JsonElement) iterator.next();
                    }

                    if (aenumitemslot.length == 0) {
                        throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                    }
                }

                if (jsonobject.has("id")) {
                    String s1 = ChatDeserializer.getAsString(jsonobject, "id");

                    try {
                        uuid = UUID.fromString(s1);
                    } catch (IllegalArgumentException illegalargumentexception) {
                        throw new JsonSyntaxException("Invalid attribute modifier id '" + s1 + "' (must be UUID format, with dashes)");
                    }
                }

                return new LootItemFunctionSetAttribute.b(s, attributebase, attributemodifier_operation, numberprovider, aenumitemslot, uuid);
            }
        }

        private static String operationToString(AttributeModifier.Operation attributemodifier_operation) {
            switch (attributemodifier_operation) {
                case ADDITION:
                    return "addition";
                case MULTIPLY_BASE:
                    return "multiply_base";
                case MULTIPLY_TOTAL:
                    return "multiply_total";
                default:
                    throw new IllegalArgumentException("Unknown operation " + attributemodifier_operation);
            }
        }

        private static AttributeModifier.Operation operationFromString(String s) {
            byte b0 = -1;

            switch (s.hashCode()) {
                case -1226589444:
                    if (s.equals("addition")) {
                        b0 = 0;
                    }
                    break;
                case -78229492:
                    if (s.equals("multiply_base")) {
                        b0 = 1;
                    }
                    break;
                case 1886894441:
                    if (s.equals("multiply_total")) {
                        b0 = 2;
                    }
            }

            switch (b0) {
                case 0:
                    return AttributeModifier.Operation.ADDITION;
                case 1:
                    return AttributeModifier.Operation.MULTIPLY_BASE;
                case 2:
                    return AttributeModifier.Operation.MULTIPLY_TOTAL;
                default:
                    throw new JsonSyntaxException("Unknown attribute modifier operation " + s);
            }
        }
    }

    public static class c {

        private final String name;
        private final AttributeBase attribute;
        private final AttributeModifier.Operation operation;
        private final NumberProvider amount;
        @Nullable
        private UUID id;
        private final Set<EnumItemSlot> slots = EnumSet.noneOf(EnumItemSlot.class);

        public c(String s, AttributeBase attributebase, AttributeModifier.Operation attributemodifier_operation, NumberProvider numberprovider) {
            this.name = s;
            this.attribute = attributebase;
            this.operation = attributemodifier_operation;
            this.amount = numberprovider;
        }

        public LootItemFunctionSetAttribute.c forSlot(EnumItemSlot enumitemslot) {
            this.slots.add(enumitemslot);
            return this;
        }

        public LootItemFunctionSetAttribute.c withUuid(UUID uuid) {
            this.id = uuid;
            return this;
        }

        public LootItemFunctionSetAttribute.b build() {
            return new LootItemFunctionSetAttribute.b(this.name, this.attribute, this.operation, this.amount, (EnumItemSlot[]) this.slots.toArray(new EnumItemSlot[0]), this.id);
        }
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionSetAttribute.a> {

        private final List<LootItemFunctionSetAttribute.b> modifiers = Lists.newArrayList();

        public a() {}

        @Override
        protected LootItemFunctionSetAttribute.a getThis() {
            return this;
        }

        public LootItemFunctionSetAttribute.a withModifier(LootItemFunctionSetAttribute.c lootitemfunctionsetattribute_c) {
            this.modifiers.add(lootitemfunctionsetattribute_c.build());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootItemFunctionSetAttribute(this.getConditions(), this.modifiers);
        }
    }

    public static class d extends LootItemFunctionConditional.c<LootItemFunctionSetAttribute> {

        public d() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionSetAttribute lootitemfunctionsetattribute, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetattribute, jsonserializationcontext);
            JsonArray jsonarray = new JsonArray();
            Iterator iterator = lootitemfunctionsetattribute.modifiers.iterator();

            while (iterator.hasNext()) {
                LootItemFunctionSetAttribute.b lootitemfunctionsetattribute_b = (LootItemFunctionSetAttribute.b) iterator.next();

                jsonarray.add(lootitemfunctionsetattribute_b.serialize(jsonserializationcontext));
            }

            jsonobject.add("modifiers", jsonarray);
        }

        @Override
        public LootItemFunctionSetAttribute deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "modifiers");
            List<LootItemFunctionSetAttribute.b> list = Lists.newArrayListWithExpectedSize(jsonarray.size());
            Iterator iterator = jsonarray.iterator();

            while (iterator.hasNext()) {
                JsonElement jsonelement = (JsonElement) iterator.next();

                list.add(LootItemFunctionSetAttribute.b.deserialize(ChatDeserializer.convertToJsonObject(jsonelement, "modifier"), jsondeserializationcontext));
            }

            if (list.isEmpty()) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            } else {
                return new LootItemFunctionSetAttribute(alootitemcondition, list);
            }
        }
    }
}
