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

    private static final MinecraftKey a = new MinecraftKey("changed_dimension");

    public CriterionTriggerChangedDimension() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerChangedDimension.a;
    }

    @Override
    public CriterionTriggerChangedDimension.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        ResourceKey<World> resourcekey = jsonobject.has("from") ? ResourceKey.a(IRegistry.L, new MinecraftKey(ChatDeserializer.h(jsonobject, "from"))) : null;
        ResourceKey<World> resourcekey1 = jsonobject.has("to") ? ResourceKey.a(IRegistry.L, new MinecraftKey(ChatDeserializer.h(jsonobject, "to"))) : null;

        return new CriterionTriggerChangedDimension.a(criterionconditionentity_b, resourcekey, resourcekey1);
    }

    public void a(EntityPlayer entityplayer, ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
        this.a(entityplayer, (criteriontriggerchangeddimension_a) -> {
            return criteriontriggerchangeddimension_a.b(resourcekey, resourcekey1);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final ResourceKey<World> a;
        @Nullable
        private final ResourceKey<World> b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, @Nullable ResourceKey<World> resourcekey, @Nullable ResourceKey<World> resourcekey1) {
            super(CriterionTriggerChangedDimension.a, criterionconditionentity_b);
            this.a = resourcekey;
            this.b = resourcekey1;
        }

        public static CriterionTriggerChangedDimension.a a(ResourceKey<World> resourcekey) {
            return new CriterionTriggerChangedDimension.a(CriterionConditionEntity.b.a, (ResourceKey) null, resourcekey);
        }

        public boolean b(ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
            return this.a != null && this.a != resourcekey ? false : this.b == null || this.b == resourcekey1;
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            if (this.a != null) {
                jsonobject.addProperty("from", this.a.a().toString());
            }

            if (this.b != null) {
                jsonobject.addProperty("to", this.b.a().toString());
            }

            return jsonobject;
        }
    }
}
