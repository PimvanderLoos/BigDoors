package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class CriterionTriggerChangedDimension extends CriterionTriggerAbstract<CriterionTriggerChangedDimension.a> {

    private static final MinecraftKey a = new MinecraftKey("changed_dimension");

    public CriterionTriggerChangedDimension() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerChangedDimension.a;
    }

    @Override
    public CriterionTriggerChangedDimension.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        DimensionManager dimensionmanager = jsonobject.has("from") ? DimensionManager.a(new MinecraftKey(ChatDeserializer.h(jsonobject, "from"))) : null;
        DimensionManager dimensionmanager1 = jsonobject.has("to") ? DimensionManager.a(new MinecraftKey(ChatDeserializer.h(jsonobject, "to"))) : null;

        return new CriterionTriggerChangedDimension.a(dimensionmanager, dimensionmanager1);
    }

    public void a(EntityPlayer entityplayer, DimensionManager dimensionmanager, DimensionManager dimensionmanager1) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerchangeddimension_a) -> {
            return criteriontriggerchangeddimension_a.b(dimensionmanager, dimensionmanager1);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final DimensionManager a;
        @Nullable
        private final DimensionManager b;

        public a(@Nullable DimensionManager dimensionmanager, @Nullable DimensionManager dimensionmanager1) {
            super(CriterionTriggerChangedDimension.a);
            this.a = dimensionmanager;
            this.b = dimensionmanager1;
        }

        public static CriterionTriggerChangedDimension.a a(DimensionManager dimensionmanager) {
            return new CriterionTriggerChangedDimension.a((DimensionManager) null, dimensionmanager);
        }

        public boolean b(DimensionManager dimensionmanager, DimensionManager dimensionmanager1) {
            return this.a != null && this.a != dimensionmanager ? false : this.b == null || this.b == dimensionmanager1;
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            if (this.a != null) {
                jsonobject.addProperty("from", DimensionManager.a(this.a).toString());
            }

            if (this.b != null) {
                jsonobject.addProperty("to", DimensionManager.a(this.b).toString());
            }

            return jsonobject;
        }
    }
}
