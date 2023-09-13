package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Items;

public class CriterionConditionEntityEquipment {

    public static final CriterionConditionEntityEquipment ANY = new CriterionConditionEntityEquipment(CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY);
    public static final CriterionConditionEntityEquipment CAPTAIN = new CriterionConditionEntityEquipment(CriterionConditionItem.a.item().of(Items.WHITE_BANNER).hasNbt(Raid.getLeaderBannerInstance().getTag()).build(), CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY);
    private final CriterionConditionItem head;
    private final CriterionConditionItem chest;
    private final CriterionConditionItem legs;
    private final CriterionConditionItem feet;
    private final CriterionConditionItem mainhand;
    private final CriterionConditionItem offhand;

    public CriterionConditionEntityEquipment(CriterionConditionItem criterionconditionitem, CriterionConditionItem criterionconditionitem1, CriterionConditionItem criterionconditionitem2, CriterionConditionItem criterionconditionitem3, CriterionConditionItem criterionconditionitem4, CriterionConditionItem criterionconditionitem5) {
        this.head = criterionconditionitem;
        this.chest = criterionconditionitem1;
        this.legs = criterionconditionitem2;
        this.feet = criterionconditionitem3;
        this.mainhand = criterionconditionitem4;
        this.offhand = criterionconditionitem5;
    }

    public boolean matches(@Nullable Entity entity) {
        if (this == CriterionConditionEntityEquipment.ANY) {
            return true;
        } else if (!(entity instanceof EntityLiving)) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) entity;

            return !this.head.matches(entityliving.getItemBySlot(EnumItemSlot.HEAD)) ? false : (!this.chest.matches(entityliving.getItemBySlot(EnumItemSlot.CHEST)) ? false : (!this.legs.matches(entityliving.getItemBySlot(EnumItemSlot.LEGS)) ? false : (!this.feet.matches(entityliving.getItemBySlot(EnumItemSlot.FEET)) ? false : (!this.mainhand.matches(entityliving.getItemBySlot(EnumItemSlot.MAINHAND)) ? false : this.offhand.matches(entityliving.getItemBySlot(EnumItemSlot.OFFHAND))))));
        }
    }

    public static CriterionConditionEntityEquipment fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "equipment");
            CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("head"));
            CriterionConditionItem criterionconditionitem1 = CriterionConditionItem.fromJson(jsonobject.get("chest"));
            CriterionConditionItem criterionconditionitem2 = CriterionConditionItem.fromJson(jsonobject.get("legs"));
            CriterionConditionItem criterionconditionitem3 = CriterionConditionItem.fromJson(jsonobject.get("feet"));
            CriterionConditionItem criterionconditionitem4 = CriterionConditionItem.fromJson(jsonobject.get("mainhand"));
            CriterionConditionItem criterionconditionitem5 = CriterionConditionItem.fromJson(jsonobject.get("offhand"));

            return new CriterionConditionEntityEquipment(criterionconditionitem, criterionconditionitem1, criterionconditionitem2, criterionconditionitem3, criterionconditionitem4, criterionconditionitem5);
        } else {
            return CriterionConditionEntityEquipment.ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionEntityEquipment.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("head", this.head.serializeToJson());
            jsonobject.add("chest", this.chest.serializeToJson());
            jsonobject.add("legs", this.legs.serializeToJson());
            jsonobject.add("feet", this.feet.serializeToJson());
            jsonobject.add("mainhand", this.mainhand.serializeToJson());
            jsonobject.add("offhand", this.offhand.serializeToJson());
            return jsonobject;
        }
    }

    public static class a {

        private CriterionConditionItem head;
        private CriterionConditionItem chest;
        private CriterionConditionItem legs;
        private CriterionConditionItem feet;
        private CriterionConditionItem mainhand;
        private CriterionConditionItem offhand;

        public a() {
            this.head = CriterionConditionItem.ANY;
            this.chest = CriterionConditionItem.ANY;
            this.legs = CriterionConditionItem.ANY;
            this.feet = CriterionConditionItem.ANY;
            this.mainhand = CriterionConditionItem.ANY;
            this.offhand = CriterionConditionItem.ANY;
        }

        public static CriterionConditionEntityEquipment.a equipment() {
            return new CriterionConditionEntityEquipment.a();
        }

        public CriterionConditionEntityEquipment.a head(CriterionConditionItem criterionconditionitem) {
            this.head = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a chest(CriterionConditionItem criterionconditionitem) {
            this.chest = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a legs(CriterionConditionItem criterionconditionitem) {
            this.legs = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a feet(CriterionConditionItem criterionconditionitem) {
            this.feet = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a mainhand(CriterionConditionItem criterionconditionitem) {
            this.mainhand = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a offhand(CriterionConditionItem criterionconditionitem) {
            this.offhand = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment build() {
            return new CriterionConditionEntityEquipment(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
        }
    }
}
