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
    public LootItemFunctionType a() {
        return LootItemFunctions.COPY_NBT;
    }

    static ArgumentNBTKey.g a(String s) {
        try {
            return (new ArgumentNBTKey()).parse(new StringReader(s));
        } catch (CommandSyntaxException commandsyntaxexception) {
            throw new IllegalArgumentException("Failed to parse path " + s, commandsyntaxexception);
        }
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return this.source.b();
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        NBTBase nbtbase = this.source.a(loottableinfo);

        if (nbtbase != null) {
            this.operations.forEach((lootitemfunctioncopynbt_b) -> {
                Objects.requireNonNull(itemstack);
                lootitemfunctioncopynbt_b.a(itemstack::getOrCreateTag, nbtbase);
            });
        }

        return itemstack;
    }

    public static LootItemFunctionCopyNBT.a a(NbtProvider nbtprovider) {
        return new LootItemFunctionCopyNBT.a(nbtprovider);
    }

    public static LootItemFunctionCopyNBT.a a(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return new LootItemFunctionCopyNBT.a(ContextNbtProvider.a(loottableinfo_entitytarget));
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionCopyNBT.a> {

        private final NbtProvider source;
        private final List<LootItemFunctionCopyNBT.b> ops = Lists.newArrayList();

        a(NbtProvider nbtprovider) {
            this.source = nbtprovider;
        }

        public LootItemFunctionCopyNBT.a a(String s, String s1, LootItemFunctionCopyNBT.Action lootitemfunctioncopynbt_action) {
            this.ops.add(new LootItemFunctionCopyNBT.b(s, s1, lootitemfunctioncopynbt_action));
            return this;
        }

        public LootItemFunctionCopyNBT.a a(String s, String s1) {
            return this.a(s, s1, LootItemFunctionCopyNBT.Action.REPLACE);
        }

        @Override
        protected LootItemFunctionCopyNBT.a d() {
            return this;
        }

        @Override
        public LootItemFunction b() {
            return new LootItemFunctionCopyNBT(this.g(), this.source, this.ops);
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
            this.sourcePath = LootItemFunctionCopyNBT.a(s);
            this.targetPathText = s1;
            this.targetPath = LootItemFunctionCopyNBT.a(s1);
            this.op = lootitemfunctioncopynbt_action;
        }

        public void a(Supplier<NBTBase> supplier, NBTBase nbtbase) {
            try {
                List<NBTBase> list = this.sourcePath.a(nbtbase);

                if (!list.isEmpty()) {
                    this.op.a((NBTBase) supplier.get(), this.targetPath, list);
                }
            } catch (CommandSyntaxException commandsyntaxexception) {
                ;
            }

        }

        public JsonObject a() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("source", this.sourcePathText);
            jsonobject.addProperty("target", this.targetPathText);
            jsonobject.addProperty("op", this.op.name);
            return jsonobject;
        }

        public static LootItemFunctionCopyNBT.b a(JsonObject jsonobject) {
            String s = ChatDeserializer.h(jsonobject, "source");
            String s1 = ChatDeserializer.h(jsonobject, "target");
            LootItemFunctionCopyNBT.Action lootitemfunctioncopynbt_action = LootItemFunctionCopyNBT.Action.a(ChatDeserializer.h(jsonobject, "op"));

            return new LootItemFunctionCopyNBT.b(s, s1, lootitemfunctioncopynbt_action);
        }
    }

    public static class d extends LootItemFunctionConditional.c<LootItemFunctionCopyNBT> {

        public d() {}

        public void a(JsonObject jsonobject, LootItemFunctionCopyNBT lootitemfunctioncopynbt, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctioncopynbt, jsonserializationcontext);
            jsonobject.add("source", jsonserializationcontext.serialize(lootitemfunctioncopynbt.source));
            JsonArray jsonarray = new JsonArray();
            Stream stream = lootitemfunctioncopynbt.operations.stream().map(LootItemFunctionCopyNBT.b::a);

            Objects.requireNonNull(jsonarray);
            stream.forEach(jsonarray::add);
            jsonobject.add("ops", jsonarray);
        }

        @Override
        public LootItemFunctionCopyNBT b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            NbtProvider nbtprovider = (NbtProvider) ChatDeserializer.a(jsonobject, "source", jsondeserializationcontext, NbtProvider.class);
            List<LootItemFunctionCopyNBT.b> list = Lists.newArrayList();
            JsonArray jsonarray = ChatDeserializer.u(jsonobject, "ops");
            Iterator iterator = jsonarray.iterator();

            while (iterator.hasNext()) {
                JsonElement jsonelement = (JsonElement) iterator.next();
                JsonObject jsonobject1 = ChatDeserializer.m(jsonelement, "op");

                list.add(LootItemFunctionCopyNBT.b.a(jsonobject1));
            }

            return new LootItemFunctionCopyNBT(alootitemcondition, nbtprovider, list);
        }
    }

    public static enum Action {

        REPLACE("replace") {
            @Override
            public void a(NBTBase nbtbase, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException {
                NBTBase nbtbase1 = (NBTBase) Iterables.getLast(list);

                Objects.requireNonNull(nbtbase1);
                argumentnbtkey_g.b(nbtbase, nbtbase1::clone);
            }
        },
        APPEND("append") {
            @Override
            public void a(NBTBase nbtbase, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException {
                List<NBTBase> list1 = argumentnbtkey_g.a(nbtbase, NBTTagList::new);

                list1.forEach((nbtbase1) -> {
                    if (nbtbase1 instanceof NBTTagList) {
                        list.forEach((nbtbase2) -> {
                            ((NBTTagList) nbtbase1).add(nbtbase2.clone());
                        });
                    }

                });
            }
        },
        MERGE("merge") {
            @Override
            public void a(NBTBase nbtbase, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException {
                List<NBTBase> list1 = argumentnbtkey_g.a(nbtbase, NBTTagCompound::new);

                list1.forEach((nbtbase1) -> {
                    if (nbtbase1 instanceof NBTTagCompound) {
                        list.forEach((nbtbase2) -> {
                            if (nbtbase2 instanceof NBTTagCompound) {
                                ((NBTTagCompound) nbtbase1).a((NBTTagCompound) nbtbase2);
                            }

                        });
                    }

                });
            }
        };

        final String name;

        public abstract void a(NBTBase nbtbase, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException;

        Action(String s) {
            this.name = s;
        }

        public static LootItemFunctionCopyNBT.Action a(String s) {
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
