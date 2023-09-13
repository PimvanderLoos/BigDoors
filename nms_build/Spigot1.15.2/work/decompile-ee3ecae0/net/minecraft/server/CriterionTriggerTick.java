package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

public class CriterionTriggerTick extends CriterionTriggerAbstract<CriterionTriggerTick.a> {

    public static final MinecraftKey a = new MinecraftKey("tick");

    public CriterionTriggerTick() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerTick.a;
    }

    @Override
    public CriterionTriggerTick.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return new CriterionTriggerTick.a();
    }

    public void a(EntityPlayer entityplayer) {
        this.b(entityplayer.getAdvancementData());
    }

    public static class a extends CriterionInstanceAbstract {

        public a() {
            super(CriterionTriggerTick.a);
        }
    }
}
