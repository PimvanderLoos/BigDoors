package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3D;

public class CriterionConditionDamageSource {

    public static final CriterionConditionDamageSource ANY = CriterionConditionDamageSource.a.damageType().build();
    private final List<TagPredicate<DamageType>> tags;
    private final CriterionConditionEntity directEntity;
    private final CriterionConditionEntity sourceEntity;

    public CriterionConditionDamageSource(List<TagPredicate<DamageType>> list, CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1) {
        this.tags = list;
        this.directEntity = criterionconditionentity;
        this.sourceEntity = criterionconditionentity1;
    }

    public boolean matches(EntityPlayer entityplayer, DamageSource damagesource) {
        return this.matches(entityplayer.getLevel(), entityplayer.position(), damagesource);
    }

    public boolean matches(WorldServer worldserver, Vec3D vec3d, DamageSource damagesource) {
        if (this == CriterionConditionDamageSource.ANY) {
            return true;
        } else {
            Iterator iterator = this.tags.iterator();

            TagPredicate tagpredicate;

            do {
                if (!iterator.hasNext()) {
                    if (!this.directEntity.matches(worldserver, vec3d, damagesource.getDirectEntity())) {
                        return false;
                    }

                    if (!this.sourceEntity.matches(worldserver, vec3d, damagesource.getEntity())) {
                        return false;
                    }

                    return true;
                }

                tagpredicate = (TagPredicate) iterator.next();
            } while (tagpredicate.matches(damagesource.typeHolder()));

            return false;
        }
    }

    public static CriterionConditionDamageSource fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "damage type");
            JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "tags", (JsonArray) null);
            Object object;

            if (jsonarray != null) {
                object = new ArrayList(jsonarray.size());
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement1 = (JsonElement) iterator.next();

                    ((List) object).add(TagPredicate.fromJson(jsonelement1, Registries.DAMAGE_TYPE));
                }
            } else {
                object = List.of();
            }

            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.fromJson(jsonobject.get("direct_entity"));
            CriterionConditionEntity criterionconditionentity1 = CriterionConditionEntity.fromJson(jsonobject.get("source_entity"));

            return new CriterionConditionDamageSource((List) object, criterionconditionentity, criterionconditionentity1);
        } else {
            return CriterionConditionDamageSource.ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionDamageSource.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (!this.tags.isEmpty()) {
                JsonArray jsonarray = new JsonArray(this.tags.size());

                for (int i = 0; i < this.tags.size(); ++i) {
                    jsonarray.add(((TagPredicate) this.tags.get(i)).serializeToJson());
                }

                jsonobject.add("tags", jsonarray);
            }

            jsonobject.add("direct_entity", this.directEntity.serializeToJson());
            jsonobject.add("source_entity", this.sourceEntity.serializeToJson());
            return jsonobject;
        }
    }

    public static class a {

        private final Builder<TagPredicate<DamageType>> tags = ImmutableList.builder();
        private CriterionConditionEntity directEntity;
        private CriterionConditionEntity sourceEntity;

        public a() {
            this.directEntity = CriterionConditionEntity.ANY;
            this.sourceEntity = CriterionConditionEntity.ANY;
        }

        public static CriterionConditionDamageSource.a damageType() {
            return new CriterionConditionDamageSource.a();
        }

        public CriterionConditionDamageSource.a tag(TagPredicate<DamageType> tagpredicate) {
            this.tags.add(tagpredicate);
            return this;
        }

        public CriterionConditionDamageSource.a direct(CriterionConditionEntity criterionconditionentity) {
            this.directEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionDamageSource.a direct(CriterionConditionEntity.a criterionconditionentity_a) {
            this.directEntity = criterionconditionentity_a.build();
            return this;
        }

        public CriterionConditionDamageSource.a source(CriterionConditionEntity criterionconditionentity) {
            this.sourceEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionDamageSource.a source(CriterionConditionEntity.a criterionconditionentity_a) {
            this.sourceEntity = criterionconditionentity_a.build();
            return this;
        }

        public CriterionConditionDamageSource build() {
            return new CriterionConditionDamageSource(this.tags.build(), this.directEntity, this.sourceEntity);
        }
    }
}
