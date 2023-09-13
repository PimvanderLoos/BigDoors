package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionInstance;
import net.minecraft.resources.MinecraftKey;

public abstract class CriterionInstanceAbstract implements CriterionInstance {

    private final MinecraftKey a;
    private final CriterionConditionEntity.b b;

    public CriterionInstanceAbstract(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b) {
        this.a = minecraftkey;
        this.b = criterionconditionentity_b;
    }

    @Override
    public MinecraftKey a() {
        return this.a;
    }

    protected CriterionConditionEntity.b b() {
        return this.b;
    }

    @Override
    public JsonObject a(LootSerializationContext lootserializationcontext) {
        JsonObject jsonobject = new JsonObject();

        jsonobject.add("player", this.b.a(lootserializationcontext));
        return jsonobject;
    }

    public String toString() {
        return "AbstractCriterionInstance{criterion=" + this.a + '}';
    }
}
