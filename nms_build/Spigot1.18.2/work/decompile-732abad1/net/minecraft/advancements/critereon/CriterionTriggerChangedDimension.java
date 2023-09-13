package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.World;

public class CriterionTriggerChangedDimension extends CriterionTriggerAbstract<CriterionTriggerChangedDimension.a> {

    static final MinecraftKey ID = new MinecraftKey("changed_dimension");

    public CriterionTriggerChangedDimension() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerChangedDimension.ID;
    }

    @Override
    public CriterionTriggerChangedDimension.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        ResourceKey<World> resourcekey = jsonobject.has("from") ? ResourceKey.create(IRegistry.DIMENSION_REGISTRY, new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "from"))) : null;
        ResourceKey<World> resourcekey1 = jsonobject.has("to") ? ResourceKey.create(IRegistry.DIMENSION_REGISTRY, new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "to"))) : null;

        return new CriterionTriggerChangedDimension.a(criterionconditionentity_b, resourcekey, resourcekey1);
    }

    public void trigger(EntityPlayer entityplayer, ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
        this.trigger(entityplayer, (criteriontriggerchangeddimension_a) -> {
            return criteriontriggerchangeddimension_a.matches(resourcekey, resourcekey1);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final ResourceKey<World> from;
        @Nullable
        private final ResourceKey<World> to;

        public a(CriterionConditionEntity.b criterionconditionentity_b, @Nullable ResourceKey<World> resourcekey, @Nullable ResourceKey<World> resourcekey1) {
            super(CriterionTriggerChangedDimension.ID, criterionconditionentity_b);
            this.from = resourcekey;
            this.to = resourcekey1;
        }

        public static CriterionTriggerChangedDimension.a changedDimension() {
            return new CriterionTriggerChangedDimension.a(CriterionConditionEntity.b.ANY, (ResourceKey) null, (ResourceKey) null);
        }

        public static CriterionTriggerChangedDimension.a changedDimension(ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
            return new CriterionTriggerChangedDimension.a(CriterionConditionEntity.b.ANY, resourcekey, resourcekey1);
        }

        public static CriterionTriggerChangedDimension.a changedDimensionTo(ResourceKey<World> resourcekey) {
            return new CriterionTriggerChangedDimension.a(CriterionConditionEntity.b.ANY, (ResourceKey) null, resourcekey);
        }

        public static CriterionTriggerChangedDimension.a changedDimensionFrom(ResourceKey<World> resourcekey) {
            return new CriterionTriggerChangedDimension.a(CriterionConditionEntity.b.ANY, resourcekey, (ResourceKey) null);
        }

        public boolean matches(ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
            return this.from != null && this.from != resourcekey ? false : this.to == null || this.to == resourcekey1;
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            if (this.from != null) {
                jsonobject.addProperty("from", this.from.location().toString());
            }

            if (this.to != null) {
                jsonobject.addProperty("to", this.to.location().toString());
            }

            return jsonobject;
        }
    }
}
