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
    public static final CriterionConditionEntityEquipment CAPTAIN = new CriterionConditionEntityEquipment(CriterionConditionItem.a.a().a(Items.WHITE_BANNER).a(Raid.s().getTag()).b(), CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY, CriterionConditionItem.ANY);
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

    public boolean a(@Nullable Entity entity) {
        if (this == CriterionConditionEntityEquipment.ANY) {
            return true;
        } else if (!(entity instanceof EntityLiving)) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) entity;

            return !this.head.a(entityliving.getEquipment(EnumItemSlot.HEAD)) ? false : (!this.chest.a(entityliving.getEquipment(EnumItemSlot.CHEST)) ? false : (!this.legs.a(entityliving.getEquipment(EnumItemSlot.LEGS)) ? false : (!this.feet.a(entityliving.getEquipment(EnumItemSlot.FEET)) ? false : (!this.mainhand.a(entityliving.getEquipment(EnumItemSlot.MAINHAND)) ? false : this.offhand.a(entityliving.getEquipment(EnumItemSlot.OFFHAND))))));
        }
    }

    public static CriterionConditionEntityEquipment a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "equipment");
            CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("head"));
            CriterionConditionItem criterionconditionitem1 = CriterionConditionItem.a(jsonobject.get("chest"));
            CriterionConditionItem criterionconditionitem2 = CriterionConditionItem.a(jsonobject.get("legs"));
            CriterionConditionItem criterionconditionitem3 = CriterionConditionItem.a(jsonobject.get("feet"));
            CriterionConditionItem criterionconditionitem4 = CriterionConditionItem.a(jsonobject.get("mainhand"));
            CriterionConditionItem criterionconditionitem5 = CriterionConditionItem.a(jsonobject.get("offhand"));

            return new CriterionConditionEntityEquipment(criterionconditionitem, criterionconditionitem1, criterionconditionitem2, criterionconditionitem3, criterionconditionitem4, criterionconditionitem5);
        } else {
            return CriterionConditionEntityEquipment.ANY;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionEntityEquipment.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("head", this.head.a());
            jsonobject.add("chest", this.chest.a());
            jsonobject.add("legs", this.legs.a());
            jsonobject.add("feet", this.feet.a());
            jsonobject.add("mainhand", this.mainhand.a());
            jsonobject.add("offhand", this.offhand.a());
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

        public static CriterionConditionEntityEquipment.a a() {
            return new CriterionConditionEntityEquipment.a();
        }

        public CriterionConditionEntityEquipment.a a(CriterionConditionItem criterionconditionitem) {
            this.head = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a b(CriterionConditionItem criterionconditionitem) {
            this.chest = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a c(CriterionConditionItem criterionconditionitem) {
            this.legs = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a d(CriterionConditionItem criterionconditionitem) {
            this.feet = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a e(CriterionConditionItem criterionconditionitem) {
            this.mainhand = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment.a f(CriterionConditionItem criterionconditionitem) {
            this.offhand = criterionconditionitem;
            return this;
        }

        public CriterionConditionEntityEquipment b() {
            return new CriterionConditionEntityEquipment(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
        }
    }
}
