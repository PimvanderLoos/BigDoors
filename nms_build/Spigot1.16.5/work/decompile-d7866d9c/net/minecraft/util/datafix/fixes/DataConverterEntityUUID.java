package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class DataConverterEntityUUID extends DataConverterUUIDBase {

    private static final Set<String> c = Sets.newHashSet();
    private static final Set<String> d = Sets.newHashSet();
    private static final Set<String> e = Sets.newHashSet();
    private static final Set<String> f = Sets.newHashSet();
    private static final Set<String> g = Sets.newHashSet();
    private static final Set<String> h = Sets.newHashSet();

    public DataConverterEntityUUID(Schema schema) {
        super(schema, DataConverterTypes.ENTITY);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("EntityUUIDFixes", this.getInputSchema().getType(this.b), (typed) -> {
            typed = typed.update(DSL.remainderFinder(), DataConverterEntityUUID::c);

            String s;
            Iterator iterator;

            for (iterator = DataConverterEntityUUID.c.iterator(); iterator.hasNext(); typed = this.a(typed, s, DataConverterEntityUUID::l)) {
                s = (String) iterator.next();
            }

            for (iterator = DataConverterEntityUUID.d.iterator(); iterator.hasNext(); typed = this.a(typed, s, DataConverterEntityUUID::l)) {
                s = (String) iterator.next();
            }

            for (iterator = DataConverterEntityUUID.e.iterator(); iterator.hasNext(); typed = this.a(typed, s, DataConverterEntityUUID::m)) {
                s = (String) iterator.next();
            }

            for (iterator = DataConverterEntityUUID.f.iterator(); iterator.hasNext(); typed = this.a(typed, s, DataConverterEntityUUID::n)) {
                s = (String) iterator.next();
            }

            for (iterator = DataConverterEntityUUID.g.iterator(); iterator.hasNext(); typed = this.a(typed, s, DataConverterEntityUUID::b)) {
                s = (String) iterator.next();
            }

            for (iterator = DataConverterEntityUUID.h.iterator(); iterator.hasNext(); typed = this.a(typed, s, DataConverterEntityUUID::o)) {
                s = (String) iterator.next();
            }

            typed = this.a(typed, "minecraft:bee", DataConverterEntityUUID::k);
            typed = this.a(typed, "minecraft:zombified_piglin", DataConverterEntityUUID::k);
            typed = this.a(typed, "minecraft:fox", DataConverterEntityUUID::j);
            typed = this.a(typed, "minecraft:item", DataConverterEntityUUID::i);
            typed = this.a(typed, "minecraft:shulker_bullet", DataConverterEntityUUID::h);
            typed = this.a(typed, "minecraft:area_effect_cloud", DataConverterEntityUUID::g);
            typed = this.a(typed, "minecraft:zombie_villager", DataConverterEntityUUID::f);
            typed = this.a(typed, "minecraft:evoker_fangs", DataConverterEntityUUID::e);
            typed = this.a(typed, "minecraft:piglin", DataConverterEntityUUID::d);
            return typed;
        });
    }

    private static Dynamic<?> d(Dynamic<?> dynamic) {
        return dynamic.update("Brain", (dynamic1) -> {
            return dynamic1.update("memories", (dynamic2) -> {
                return dynamic2.update("minecraft:angry_at", (dynamic3) -> {
                    return (Dynamic) a(dynamic3, "value", "value").orElseGet(() -> {
                        DataConverterEntityUUID.LOGGER.warn("angry_at has no value.");
                        return dynamic3;
                    });
                });
            });
        });
    }

    private static Dynamic<?> e(Dynamic<?> dynamic) {
        return (Dynamic) c(dynamic, "OwnerUUID", "Owner").orElse(dynamic);
    }

    private static Dynamic<?> f(Dynamic<?> dynamic) {
        return (Dynamic) c(dynamic, "ConversionPlayer", "ConversionPlayer").orElse(dynamic);
    }

    private static Dynamic<?> g(Dynamic<?> dynamic) {
        return (Dynamic) c(dynamic, "OwnerUUID", "Owner").orElse(dynamic);
    }

    private static Dynamic<?> h(Dynamic<?> dynamic) {
        dynamic = (Dynamic) b(dynamic, "Owner", "Owner").orElse(dynamic);
        return (Dynamic) b(dynamic, "Target", "Target").orElse(dynamic);
    }

    private static Dynamic<?> i(Dynamic<?> dynamic) {
        dynamic = (Dynamic) b(dynamic, "Owner", "Owner").orElse(dynamic);
        return (Dynamic) b(dynamic, "Thrower", "Thrower").orElse(dynamic);
    }

    private static Dynamic<?> j(Dynamic<?> dynamic) {
        Optional<Dynamic<?>> optional = dynamic.get("TrustedUUIDs").result().map((dynamic1) -> {
            return dynamic.createList(dynamic1.asStream().map((dynamic2) -> {
                return (Dynamic) a(dynamic2).orElseGet(() -> {
                    DataConverterEntityUUID.LOGGER.warn("Trusted contained invalid data.");
                    return dynamic2;
                });
            }));
        });

        return (Dynamic) DataFixUtils.orElse(optional.map((dynamic1) -> {
            return dynamic.remove("TrustedUUIDs").set("Trusted", dynamic1);
        }), dynamic);
    }

    private static Dynamic<?> k(Dynamic<?> dynamic) {
        return (Dynamic) a(dynamic, "HurtBy", "HurtBy").orElse(dynamic);
    }

    private static Dynamic<?> l(Dynamic<?> dynamic) {
        Dynamic<?> dynamic1 = m(dynamic);

        return (Dynamic) a(dynamic1, "OwnerUUID", "Owner").orElse(dynamic1);
    }

    private static Dynamic<?> m(Dynamic<?> dynamic) {
        Dynamic<?> dynamic1 = n(dynamic);

        return (Dynamic) c(dynamic1, "LoveCause", "LoveCause").orElse(dynamic1);
    }

    private static Dynamic<?> n(Dynamic<?> dynamic) {
        return b(dynamic).update("Leash", (dynamic1) -> {
            return (Dynamic) c(dynamic1, "UUID", "UUID").orElse(dynamic1);
        });
    }

    public static Dynamic<?> b(Dynamic<?> dynamic) {
        return dynamic.update("Attributes", (dynamic1) -> {
            return dynamic.createList(dynamic1.asStream().map((dynamic2) -> {
                return dynamic2.update("Modifiers", (dynamic3) -> {
                    return dynamic2.createList(dynamic3.asStream().map((dynamic4) -> {
                        return (Dynamic) c(dynamic4, "UUID", "UUID").orElse(dynamic4);
                    }));
                });
            }));
        });
    }

    private static Dynamic<?> o(Dynamic<?> dynamic) {
        return (Dynamic) DataFixUtils.orElse(dynamic.get("OwnerUUID").result().map((dynamic1) -> {
            return dynamic.remove("OwnerUUID").set("Owner", dynamic1);
        }), dynamic);
    }

    public static Dynamic<?> c(Dynamic<?> dynamic) {
        return (Dynamic) c(dynamic, "UUID", "UUID").orElse(dynamic);
    }

    static {
        DataConverterEntityUUID.c.add("minecraft:donkey");
        DataConverterEntityUUID.c.add("minecraft:horse");
        DataConverterEntityUUID.c.add("minecraft:llama");
        DataConverterEntityUUID.c.add("minecraft:mule");
        DataConverterEntityUUID.c.add("minecraft:skeleton_horse");
        DataConverterEntityUUID.c.add("minecraft:trader_llama");
        DataConverterEntityUUID.c.add("minecraft:zombie_horse");
        DataConverterEntityUUID.d.add("minecraft:cat");
        DataConverterEntityUUID.d.add("minecraft:parrot");
        DataConverterEntityUUID.d.add("minecraft:wolf");
        DataConverterEntityUUID.e.add("minecraft:bee");
        DataConverterEntityUUID.e.add("minecraft:chicken");
        DataConverterEntityUUID.e.add("minecraft:cow");
        DataConverterEntityUUID.e.add("minecraft:fox");
        DataConverterEntityUUID.e.add("minecraft:mooshroom");
        DataConverterEntityUUID.e.add("minecraft:ocelot");
        DataConverterEntityUUID.e.add("minecraft:panda");
        DataConverterEntityUUID.e.add("minecraft:pig");
        DataConverterEntityUUID.e.add("minecraft:polar_bear");
        DataConverterEntityUUID.e.add("minecraft:rabbit");
        DataConverterEntityUUID.e.add("minecraft:sheep");
        DataConverterEntityUUID.e.add("minecraft:turtle");
        DataConverterEntityUUID.e.add("minecraft:hoglin");
        DataConverterEntityUUID.f.add("minecraft:bat");
        DataConverterEntityUUID.f.add("minecraft:blaze");
        DataConverterEntityUUID.f.add("minecraft:cave_spider");
        DataConverterEntityUUID.f.add("minecraft:cod");
        DataConverterEntityUUID.f.add("minecraft:creeper");
        DataConverterEntityUUID.f.add("minecraft:dolphin");
        DataConverterEntityUUID.f.add("minecraft:drowned");
        DataConverterEntityUUID.f.add("minecraft:elder_guardian");
        DataConverterEntityUUID.f.add("minecraft:ender_dragon");
        DataConverterEntityUUID.f.add("minecraft:enderman");
        DataConverterEntityUUID.f.add("minecraft:endermite");
        DataConverterEntityUUID.f.add("minecraft:evoker");
        DataConverterEntityUUID.f.add("minecraft:ghast");
        DataConverterEntityUUID.f.add("minecraft:giant");
        DataConverterEntityUUID.f.add("minecraft:guardian");
        DataConverterEntityUUID.f.add("minecraft:husk");
        DataConverterEntityUUID.f.add("minecraft:illusioner");
        DataConverterEntityUUID.f.add("minecraft:magma_cube");
        DataConverterEntityUUID.f.add("minecraft:pufferfish");
        DataConverterEntityUUID.f.add("minecraft:zombified_piglin");
        DataConverterEntityUUID.f.add("minecraft:salmon");
        DataConverterEntityUUID.f.add("minecraft:shulker");
        DataConverterEntityUUID.f.add("minecraft:silverfish");
        DataConverterEntityUUID.f.add("minecraft:skeleton");
        DataConverterEntityUUID.f.add("minecraft:slime");
        DataConverterEntityUUID.f.add("minecraft:snow_golem");
        DataConverterEntityUUID.f.add("minecraft:spider");
        DataConverterEntityUUID.f.add("minecraft:squid");
        DataConverterEntityUUID.f.add("minecraft:stray");
        DataConverterEntityUUID.f.add("minecraft:tropical_fish");
        DataConverterEntityUUID.f.add("minecraft:vex");
        DataConverterEntityUUID.f.add("minecraft:villager");
        DataConverterEntityUUID.f.add("minecraft:iron_golem");
        DataConverterEntityUUID.f.add("minecraft:vindicator");
        DataConverterEntityUUID.f.add("minecraft:pillager");
        DataConverterEntityUUID.f.add("minecraft:wandering_trader");
        DataConverterEntityUUID.f.add("minecraft:witch");
        DataConverterEntityUUID.f.add("minecraft:wither");
        DataConverterEntityUUID.f.add("minecraft:wither_skeleton");
        DataConverterEntityUUID.f.add("minecraft:zombie");
        DataConverterEntityUUID.f.add("minecraft:zombie_villager");
        DataConverterEntityUUID.f.add("minecraft:phantom");
        DataConverterEntityUUID.f.add("minecraft:ravager");
        DataConverterEntityUUID.f.add("minecraft:piglin");
        DataConverterEntityUUID.g.add("minecraft:armor_stand");
        DataConverterEntityUUID.h.add("minecraft:arrow");
        DataConverterEntityUUID.h.add("minecraft:dragon_fireball");
        DataConverterEntityUUID.h.add("minecraft:firework_rocket");
        DataConverterEntityUUID.h.add("minecraft:fireball");
        DataConverterEntityUUID.h.add("minecraft:llama_spit");
        DataConverterEntityUUID.h.add("minecraft:small_fireball");
        DataConverterEntityUUID.h.add("minecraft:snowball");
        DataConverterEntityUUID.h.add("minecraft:spectral_arrow");
        DataConverterEntityUUID.h.add("minecraft:egg");
        DataConverterEntityUUID.h.add("minecraft:ender_pearl");
        DataConverterEntityUUID.h.add("minecraft:experience_bottle");
        DataConverterEntityUUID.h.add("minecraft:potion");
        DataConverterEntityUUID.h.add("minecraft:trident");
        DataConverterEntityUUID.h.add("minecraft:wither_skull");
    }
}
