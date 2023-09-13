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
    public MinecraftKey a() {
        return CriterionTriggerChangedDimension.ID;
    }

    @Override
    public CriterionTriggerChangedDimension.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        ResourceKey<World> resourcekey = jsonobject.has("from") ? ResourceKey.a(IRegistry.DIMENSION_REGISTRY, new MinecraftKey(ChatDeserializer.h(jsonobject, "from"))) : null;
        ResourceKey<World> resourcekey1 = jsonobject.has("to") ? ResourceKey.a(IRegistry.DIMENSION_REGISTRY, new MinecraftKey(ChatDeserializer.h(jsonobject, "to"))) : null;

        return new CriterionTriggerChangedDimension.a(criterionconditionentity_b, resourcekey, resourcekey1);
    }

    public void a(EntityPlayer entityplayer, ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
        this.a(entityplayer, (criteriontriggerchangeddimension_a) -> {
            return criteriontriggerchangeddimension_a.b(resourcekey, resourcekey1);
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

        public static CriterionTriggerChangedDimension.a c() {
            return new CriterionTriggerChangedDimension.a(CriterionConditionEntity.b.ANY, (ResourceKey) null, (ResourceKey) null);
        }

        public static CriterionTriggerChangedDimension.a a(ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
            return new CriterionTriggerChangedDimension.a(CriterionConditionEntity.b.ANY, resourcekey, resourcekey1);
        }

        public static CriterionTriggerChangedDimension.a a(ResourceKey<World> resourcekey) {
            return new CriterionTriggerChangedDimension.a(CriterionConditionEntity.b.ANY, (ResourceKey) null, resourcekey);
        }

        public static CriterionTriggerChangedDimension.a b(ResourceKey<World> resourcekey) {
            return new CriterionTriggerChangedDimension.a(CriterionConditionEntity.b.ANY, resourcekey, (ResourceKey) null);
        }

        public boolean b(ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
            return this.from != null && this.from != resourcekey ? false : this.to == null || this.to == resourcekey1;
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            if (this.from != null) {
                jsonobject.addProperty("from", this.from.a().toString());
            }

            if (this.to != null) {
                jsonobject.addProperty("to", this.to.a().toString());
            }

            return jsonobject;
        }
    }
}
