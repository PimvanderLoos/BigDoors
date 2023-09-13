package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class LootItemFunctionSetName extends LootItemFunctionConditional {

    private static final Logger LOGGER = LogUtils.getLogger();
    final IChatBaseComponent name;
    @Nullable
    final LootTableInfo.EntityTarget resolutionContext;

    LootItemFunctionSetName(LootItemCondition[] alootitemcondition, @Nullable IChatBaseComponent ichatbasecomponent, @Nullable LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        super(alootitemcondition);
        this.name = ichatbasecomponent;
        this.resolutionContext = loottableinfo_entitytarget;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_NAME;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
    }

    public static UnaryOperator<IChatBaseComponent> createResolver(LootTableInfo loottableinfo, @Nullable LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        if (loottableinfo_entitytarget != null) {
            Entity entity = (Entity) loottableinfo.getParamOrNull(loottableinfo_entitytarget.getParam());

            if (entity != null) {
                CommandListenerWrapper commandlistenerwrapper = entity.createCommandSourceStack().withPermission(2);

                return (ichatbasecomponent) -> {
                    try {
                        return ChatComponentUtils.updateForEntity(commandlistenerwrapper, ichatbasecomponent, entity, 0);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        LootItemFunctionSetName.LOGGER.warn("Failed to resolve text component", commandsyntaxexception);
                        return ichatbasecomponent;
                    }
                };
            }
        }

        return (ichatbasecomponent) -> {
            return ichatbasecomponent;
        };
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (this.name != null) {
            itemstack.setHoverName((IChatBaseComponent) createResolver(loottableinfo, this.resolutionContext).apply(this.name));
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> setName(IChatBaseComponent ichatbasecomponent) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionSetName(alootitemcondition, ichatbasecomponent, (LootTableInfo.EntityTarget) null);
        });
    }

    public static LootItemFunctionConditional.a<?> setName(IChatBaseComponent ichatbasecomponent, LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionSetName(alootitemcondition, ichatbasecomponent, loottableinfo_entitytarget);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetName> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionSetName lootitemfunctionsetname, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetname, jsonserializationcontext);
            if (lootitemfunctionsetname.name != null) {
                jsonobject.add("name", IChatBaseComponent.ChatSerializer.toJsonTree(lootitemfunctionsetname.name));
            }

            if (lootitemfunctionsetname.resolutionContext != null) {
                jsonobject.add("entity", jsonserializationcontext.serialize(lootitemfunctionsetname.resolutionContext));
            }

        }

        @Override
        public LootItemFunctionSetName deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(jsonobject.get("name"));
            LootTableInfo.EntityTarget loottableinfo_entitytarget = (LootTableInfo.EntityTarget) ChatDeserializer.getAsObject(jsonobject, "entity", (Object) null, jsondeserializationcontext, LootTableInfo.EntityTarget.class);

            return new LootItemFunctionSetName(alootitemcondition, ichatmutablecomponent, loottableinfo_entitytarget);
        }
    }
}
