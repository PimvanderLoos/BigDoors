package net.minecraft.server;

public class Particle<T extends ParticleParam> {

    private final MinecraftKey a;
    private final boolean b;
    private final ParticleParam.a<T> c;

    protected Particle(MinecraftKey minecraftkey, boolean flag, ParticleParam.a<T> particleparam_a) {
        this.a = minecraftkey;
        this.b = flag;
        this.c = particleparam_a;
    }

    public static void c() {
        a("ambient_entity_effect", false);
        a("angry_villager", false);
        a("barrier", false);
        a("block", false, ParticleParamBlock.a);
        a("bubble", false);
        a("cloud", false);
        a("crit", false);
        a("damage_indicator", true);
        a("dragon_breath", false);
        a("dripping_lava", false);
        a("dripping_water", false);
        a("dust", false, ParticleParamRedstone.b);
        a("effect", false);
        a("elder_guardian", true);
        a("enchanted_hit", false);
        a("enchant", false);
        a("end_rod", false);
        a("entity_effect", false);
        a("explosion_emitter", true);
        a("explosion", true);
        a("falling_dust", false, ParticleParamBlock.a);
        a("firework", false);
        a("fishing", false);
        a("flame", false);
        a("happy_villager", false);
        a("heart", false);
        a("instant_effect", false);
        a("item", false, ParticleParamItem.a);
        a("item_slime", false);
        a("item_snowball", false);
        a("large_smoke", false);
        a("lava", false);
        a("mycelium", false);
        a("note", false);
        a("poof", true);
        a("portal", false);
        a("rain", false);
        a("smoke", false);
        a("spit", true);
        a("squid_ink", true);
        a("sweep_attack", true);
        a("totem_of_undying", false);
        a("underwater", false);
        a("splash", false);
        a("witch", false);
        a("bubble_pop", false);
        a("current_down", false);
        a("bubble_column_up", false);
        a("nautilus", false);
        a("dolphin", false);
    }

    public MinecraftKey d() {
        return this.a;
    }

    public boolean e() {
        return this.b;
    }

    public ParticleParam.a<T> f() {
        return this.c;
    }

    private static void a(String s, boolean flag) {
        IRegistry.PARTICLE_TYPE.a(new MinecraftKey(s), (Object) (new ParticleType(new MinecraftKey(s), flag)));
    }

    private static <T extends ParticleParam> void a(String s, boolean flag, ParticleParam.a<T> particleparam_a) {
        IRegistry.PARTICLE_TYPE.a(new MinecraftKey(s), (Object) (new Particle<>(new MinecraftKey(s), flag, particleparam_a)));
    }
}
