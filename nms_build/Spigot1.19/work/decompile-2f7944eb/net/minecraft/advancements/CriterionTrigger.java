package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.LootDeserializationContext;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.AdvancementDataPlayer;

public interface CriterionTrigger<T extends CriterionInstance> {

    MinecraftKey getId();

    void addPlayerListener(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<T> criteriontrigger_a);

    void removePlayerListener(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<T> criteriontrigger_a);

    void removePlayerListeners(AdvancementDataPlayer advancementdataplayer);

    T createInstance(JsonObject jsonobject, LootDeserializationContext lootdeserializationcontext);

    public static class a<T extends CriterionInstance> {

        private final T trigger;
        private final Advancement advancement;
        private final String criterion;

        public a(T t0, Advancement advancement, String s) {
            this.trigger = t0;
            this.advancement = advancement;
            this.criterion = s;
        }

        public T getTriggerInstance() {
            return this.trigger;
        }

        public void run(AdvancementDataPlayer advancementdataplayer) {
            advancementdataplayer.award(this.advancement, this.criterion);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                CriterionTrigger.a<?> criteriontrigger_a = (CriterionTrigger.a) object;

                return !this.trigger.equals(criteriontrigger_a.trigger) ? false : (!this.advancement.equals(criteriontrigger_a.advancement) ? false : this.criterion.equals(criteriontrigger_a.criterion));
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = this.trigger.hashCode();

            i = 31 * i + this.advancement.hashCode();
            i = 31 * i + this.criterion.hashCode();
            return i;
        }
    }
}
