package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;

public class LootItemFunctionCopyNBT extends LootItemFunctionConditional {

    final NbtProvider source;
    final List<LootItemFunctionCopyNBT.b> operations;

    LootItemFunctionCopyNBT(LootItemCondition[] alootitemcondition, NbtProvider nbtprovider, List<LootItemFunctionCopyNBT.b> list) {
        super(alootitemcondition);
        this.source = nbtprovider;
        this.operations = ImmutableList.copyOf(list);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_NBT;
    }

    static ArgumentNBTKey.g compileNbtPath(String s) {
        try {
            return (new ArgumentNBTKey()).parse(new StringReader(s));
        } catch (CommandSyntaxException commandsyntaxexception) {
            throw new IllegalArgumentException("Failed to parse path " + s, commandsyntaxexception);
        }
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return this.source.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        NBTBase nbtbase = this.source.get(loottableinfo);

        if (nbtbase != null) {
            this.operations.forEach((lootitemfunctioncopynbt_b) -> {
                Objects.requireNonNull(itemstack);
                lootitemfunctioncopynbt_b.apply(itemstack::getOrCreateTag, nbtbase);
            });
        }

        return itemstack;
    }

    public static LootItemFunctionCopyNBT.a copyData(NbtProvider nbtprovider) {
        return new LootItemFunctionCopyNBT.a(nbtprovider);
    }

    public static LootItemFunctionCopyNBT.a copyData(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return new LootItemFunctionCopyNBT.a(ContextNbtProvider.forContextEntity(loottableinfo_entitytarget));
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionCopyNBT.a> {

        private final NbtProvider source;
        private final List<LootItemFunctionCopyNBT.b> ops = Lists.newArrayList();

        a(NbtProvider nbtprovider) {
            this.source = nbtprovider;
        }

        public LootItemFunctionCopyNBT.a copy(String s, String s1, LootItemFunctionCopyNBT.Action lootitemfunctioncopynbt_action) {
            this.ops.add(new LootItemFunctionCopyNBT.b(s, s1, lootitemfunctioncopynbt_action));
            return this;
        }

        public LootItemFunctionCopyNBT.a copy(String s, String s1) {
            return this.copy(s, s1, LootItemFunctionCopyNBT.Action.REPLACE);
        }

        @Override
        protected LootItemFunctionCopyNBT.a getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootItemFunctionCopyNBT(this.getConditions(), this.source, this.ops);
        }
    }

    private static class b {

        private final String sourcePathText;
        private final ArgumentNBTKey.g sourcePath;
        private final String targetPathText;
        private final ArgumentNBTKey.g targetPath;
        private final LootItemFunctionCopyNBT.Action op;

        b(String s, String s1, LootItemFunctionCopyNBT.Action lootitemfunctioncopynbt_action) {
            this.sourcePathText = s;
            this.sourcePath = LootItemFunctionCopyNBT.compileNbtPath(s);
            this.targetPathText = s1;
            this.targetPath = LootItemFunctionCopyNBT.compileNbtPath(s1);
            this.op = lootitemfunctioncopynbt_action;
        }

        public void apply(Supplier<NBTBase> supplier, NBTBase nbtbase) {
            try {
                List<NBTBase> list = this.sourcePath.get(nbtbase);

                if (!list.isEmpty()) {
                    this.op.merge((NBTBase) supplier.get(), this.targetPath, list);
                }
            } catch (CommandSyntaxException commandsyntaxexception) {
                ;
            }

        }

        public JsonObject toJson() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("source", this.sourcePathText);
            jsonobject.addProperty("target", this.targetPathText);
            jsonobject.addProperty("op", this.op.name);
            return jsonobject;
        }

        public static LootItemFunctionCopyNBT.b fromJson(JsonObject jsonobject) {
            String s = ChatDeserializer.getAsString(jsonobject, "source");
            String s1 = ChatDeserializer.getAsString(jsonobject, "target");
            LootItemFunctionCopyNBT.Action lootitemfunctioncopynbt_action = LootItemFunctionCopyNBT.Action.getByName(ChatDeserializer.getAsString(jsonobject, "op"));

            return new LootItemFunctionCopyNBT.b(s, s1, lootitemfunctioncopynbt_action);
        }
    }

    public static class d extends LootItemFunctionConditional.c<LootItemFunctionCopyNBT> {

        public d() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionCopyNBT lootitemfunctioncopynbt, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctioncopynbt, jsonserializationcontext);
            jsonobject.add("source", jsonserializationcontext.serialize(lootitemfunctioncopynbt.source));
            JsonArray jsonarray = new JsonArray();
            Stream stream = lootitemfunctioncopynbt.operations.stream().map(LootItemFunctionCopyNBT.b::toJson);

            Objects.requireNonNull(jsonarray);
            stream.forEach(jsonarray::add);
            jsonobject.add("ops", jsonarray);
        }

        @Override
        public LootItemFunctionCopyNBT deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            NbtProvider nbtprovider = (NbtProvider) ChatDeserializer.getAsObject(jsonobject, "source", jsondeserializationcontext, NbtProvider.class);
            List<LootItemFunctionCopyNBT.b> list = Lists.newArrayList();
            JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "ops");
            Iterator iterator = jsonarray.iterator();

            while (iterator.hasNext()) {
                JsonElement jsonelement = (JsonElement) iterator.next();
                JsonObject jsonobject1 = ChatDeserializer.convertToJsonObject(jsonelement, "op");

                list.add(LootItemFunctionCopyNBT.b.fromJson(jsonobject1));
            }

            return new LootItemFunctionCopyNBT(alootitemcondition, nbtprovider, list);
        }
    }

    public static enum Action {

        REPLACE("replace") {
            @Override
            public void merge(NBTBase nbtbase, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException {
                argumentnbtkey_g.set(nbtbase, (NBTBase) Iterables.getLast(list));
            }
        },
        APPEND("append") {
            @Override
            public void merge(NBTBase nbtbase, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException {
                List<NBTBase> list1 = argumentnbtkey_g.getOrCreate(nbtbase, NBTTagList::new);

                list1.forEach((nbtbase1) -> {
                    if (nbtbase1 instanceof NBTTagList) {
                        list.forEach((nbtbase2) -> {
                            ((NBTTagList) nbtbase1).add(nbtbase2.copy());
                        });
                    }

                });
            }
        },
        MERGE("merge") {
            @Override
            public void merge(NBTBase nbtbase, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException {
                List<NBTBase> list1 = argumentnbtkey_g.getOrCreate(nbtbase, NBTTagCompound::new);

                list1.forEach((nbtbase1) -> {
                    if (nbtbase1 instanceof NBTTagCompound) {
                        list.forEach((nbtbase2) -> {
                            if (nbtbase2 instanceof NBTTagCompound) {
                                ((NBTTagCompound) nbtbase1).merge((NBTTagCompound) nbtbase2);
                            }

                        });
                    }

                });
            }
        };

        final String name;

        public abstract void merge(NBTBase nbtbase, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException;

        Action(String s) {
            this.name = s;
        }

        public static LootItemFunctionCopyNBT.Action getByName(String s) {
            LootItemFunctionCopyNBT.Action[] alootitemfunctioncopynbt_action = values();
            int i = alootitemfunctioncopynbt_action.length;

            for (int j = 0; j < i; ++j) {
                LootItemFunctionCopyNBT.Action lootitemfunctioncopynbt_action = alootitemfunctioncopynbt_action[j];

                if (lootitemfunctioncopynbt_action.name.equals(s)) {
                    return lootitemfunctioncopynbt_action;
                }
            }

            throw new IllegalArgumentException("Invalid merge strategy" + s);
        }
    }
}
