package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionInstance;
import net.minecraft.resources.MinecraftKey;

public abstract class CriterionInstanceAbstract implements CriterionInstance {

    private final MinecraftKey criterion;
    private final CriterionConditionEntity.b player;

    public CriterionInstanceAbstract(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b) {
        this.criterion = minecraftkey;
        this.player = criterionconditionentity_b;
    }

    @Override
    public MinecraftKey a() {
        return this.criterion;
    }

    protected CriterionConditionEntity.b b() {
        return this.player;
    }

    @Override
    public JsonObject a(LootSerializationContext lootserializationcontext) {
        JsonObject jsonobject = new JsonObject();

        jsonobject.add("player", this.player.a(lootserializationcontext));
        return jsonobject;
    }

    public String toString() {
        return "AbstractCriterionInstance{criterion=" + this.criterion + "}";
    }
}
