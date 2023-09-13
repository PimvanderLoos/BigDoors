package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTypes {

    public static final MinecraftKey a = new MinecraftKey("lightning_bolt");
    private static final MinecraftKey e = new MinecraftKey("player");
    private static final Logger f = LogManager.getLogger();
    public static final RegistryMaterials<MinecraftKey, Class<? extends Entity>> b = new RegistryMaterials();
    public static final Map<MinecraftKey, EntityTypes.MonsterEggInfo> eggInfo = Maps.newLinkedHashMap();
    public static final Set<MinecraftKey> d = Sets.newHashSet();
    private static final List<String> g = Lists.newArrayList();

    @Nullable
    public static MinecraftKey a(Entity entity) {
        return getName(entity.getClass());
    }

    @Nullable
    public static MinecraftKey getName(Class<? extends Entity> oclass) {
        return (MinecraftKey) EntityTypes.b.b(oclass);
    }

    @Nullable
    public static String b(Entity entity) {
        int i = EntityTypes.b.a((Object) entity.getClass());

        return i == -1 ? null : (String) EntityTypes.g.get(i);
    }

    @Nullable
    public static String a(@Nullable MinecraftKey minecraftkey) {
        int i = EntityTypes.b.a(EntityTypes.b.get(minecraftkey));

        return i == -1 ? null : (String) EntityTypes.g.get(i);
    }

    @Nullable
    public static Entity a(@Nullable Class<? extends Entity> oclass, World world) {
        if (oclass == null) {
            return null;
        } else {
            try {
                return (Entity) oclass.getConstructor(new Class[] { World.class}).newInstance(new Object[] { world});
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    @Nullable
    public static Entity a(MinecraftKey minecraftkey, World world) {
        return a((Class) EntityTypes.b.get(minecraftkey), world);
    }

    @Nullable
    public static Entity a(NBTTagCompound nbttagcompound, World world) {
        MinecraftKey minecraftkey = new MinecraftKey(nbttagcompound.getString("id"));
        Entity entity = a(minecraftkey, world);

        if (entity == null) {
            EntityTypes.f.warn("Skipping Entity with id {}", minecraftkey);
        } else {
            entity.f(nbttagcompound);
        }

        return entity;
    }

    public static Set<MinecraftKey> a() {
        return EntityTypes.d;
    }

    public static boolean a(Entity entity, MinecraftKey minecraftkey) {
        MinecraftKey minecraftkey1 = getName(entity.getClass());

        return minecraftkey1 != null ? minecraftkey1.equals(minecraftkey) : (entity instanceof EntityHuman ? EntityTypes.e.equals(minecraftkey) : (entity instanceof EntityLightning ? EntityTypes.a.equals(minecraftkey) : false));
    }

    public static boolean b(MinecraftKey minecraftkey) {
        return EntityTypes.e.equals(minecraftkey) || a().contains(minecraftkey);
    }

    public static String b() {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = a().iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();

            stringbuilder.append(minecraftkey).append(", ");
        }

        stringbuilder.append(EntityTypes.e);
        return stringbuilder.toString();
    }

    public static void c() {
        a(1, "item", EntityItem.class, "Item");
        a(2, "xp_orb", EntityExperienceOrb.class, "XPOrb");
        a(3, "area_effect_cloud", EntityAreaEffectCloud.class, "AreaEffectCloud");
        a(4, "elder_guardian", EntityGuardianElder.class, "ElderGuardian");
        a(5, "wither_skeleton", EntitySkeletonWither.class, "WitherSkeleton");
        a(6, "stray", EntitySkeletonStray.class, "Stray");
        a(7, "egg", EntityEgg.class, "ThrownEgg");
        a(8, "leash_knot", EntityLeash.class, "LeashKnot");
        a(9, "painting", EntityPainting.class, "Painting");
        a(10, "arrow", EntityTippedArrow.class, "Arrow");
        a(11, "snowball", EntitySnowball.class, "Snowball");
        a(12, "fireball", EntityLargeFireball.class, "Fireball");
        a(13, "small_fireball", EntitySmallFireball.class, "SmallFireball");
        a(14, "ender_pearl", EntityEnderPearl.class, "ThrownEnderpearl");
        a(15, "eye_of_ender_signal", EntityEnderSignal.class, "EyeOfEnderSignal");
        a(16, "potion", EntityPotion.class, "ThrownPotion");
        a(17, "xp_bottle", EntityThrownExpBottle.class, "ThrownExpBottle");
        a(18, "item_frame", EntityItemFrame.class, "ItemFrame");
        a(19, "wither_skull", EntityWitherSkull.class, "WitherSkull");
        a(20, "tnt", EntityTNTPrimed.class, "PrimedTnt");
        a(21, "falling_block", EntityFallingBlock.class, "FallingSand");
        a(22, "fireworks_rocket", EntityFireworks.class, "FireworksRocketEntity");
        a(23, "husk", EntityZombieHusk.class, "Husk");
        a(24, "spectral_arrow", EntitySpectralArrow.class, "SpectralArrow");
        a(25, "shulker_bullet", EntityShulkerBullet.class, "ShulkerBullet");
        a(26, "dragon_fireball", EntityDragonFireball.class, "DragonFireball");
        a(27, "zombie_villager", EntityZombieVillager.class, "ZombieVillager");
        a(28, "skeleton_horse", EntityHorseSkeleton.class, "SkeletonHorse");
        a(29, "zombie_horse", EntityHorseZombie.class, "ZombieHorse");
        a(30, "armor_stand", EntityArmorStand.class, "ArmorStand");
        a(31, "donkey", EntityHorseDonkey.class, "Donkey");
        a(32, "mule", EntityHorseMule.class, "Mule");
        a(33, "evocation_fangs", EntityEvokerFangs.class, "EvocationFangs");
        a(34, "evocation_illager", EntityEvoker.class, "EvocationIllager");
        a(35, "vex", EntityVex.class, "Vex");
        a(36, "vindication_illager", EntityVindicator.class, "VindicationIllager");
        a(37, "illusion_illager", EntityIllagerIllusioner.class, "IllusionIllager");
        a(40, "commandblock_minecart", EntityMinecartCommandBlock.class, EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK.b());
        a(41, "boat", EntityBoat.class, "Boat");
        a(42, "minecart", EntityMinecartRideable.class, EntityMinecartAbstract.EnumMinecartType.RIDEABLE.b());
        a(43, "chest_minecart", EntityMinecartChest.class, EntityMinecartAbstract.EnumMinecartType.CHEST.b());
        a(44, "furnace_minecart", EntityMinecartFurnace.class, EntityMinecartAbstract.EnumMinecartType.FURNACE.b());
        a(45, "tnt_minecart", EntityMinecartTNT.class, EntityMinecartAbstract.EnumMinecartType.TNT.b());
        a(46, "hopper_minecart", EntityMinecartHopper.class, EntityMinecartAbstract.EnumMinecartType.HOPPER.b());
        a(47, "spawner_minecart", EntityMinecartMobSpawner.class, EntityMinecartAbstract.EnumMinecartType.SPAWNER.b());
        a(50, "creeper", EntityCreeper.class, "Creeper");
        a(51, "skeleton", EntitySkeleton.class, "Skeleton");
        a(52, "spider", EntitySpider.class, "Spider");
        a(53, "giant", EntityGiantZombie.class, "Giant");
        a(54, "zombie", EntityZombie.class, "Zombie");
        a(55, "slime", EntitySlime.class, "Slime");
        a(56, "ghast", EntityGhast.class, "Ghast");
        a(57, "zombie_pigman", EntityPigZombie.class, "PigZombie");
        a(58, "enderman", EntityEnderman.class, "Enderman");
        a(59, "cave_spider", EntityCaveSpider.class, "CaveSpider");
        a(60, "silverfish", EntitySilverfish.class, "Silverfish");
        a(61, "blaze", EntityBlaze.class, "Blaze");
        a(62, "magma_cube", EntityMagmaCube.class, "LavaSlime");
        a(63, "ender_dragon", EntityEnderDragon.class, "EnderDragon");
        a(64, "wither", EntityWither.class, "WitherBoss");
        a(65, "bat", EntityBat.class, "Bat");
        a(66, "witch", EntityWitch.class, "Witch");
        a(67, "endermite", EntityEndermite.class, "Endermite");
        a(68, "guardian", EntityGuardian.class, "Guardian");
        a(69, "shulker", EntityShulker.class, "Shulker");
        a(90, "pig", EntityPig.class, "Pig");
        a(91, "sheep", EntitySheep.class, "Sheep");
        a(92, "cow", EntityCow.class, "Cow");
        a(93, "chicken", EntityChicken.class, "Chicken");
        a(94, "squid", EntitySquid.class, "Squid");
        a(95, "wolf", EntityWolf.class, "Wolf");
        a(96, "mooshroom", EntityMushroomCow.class, "MushroomCow");
        a(97, "snowman", EntitySnowman.class, "SnowMan");
        a(98, "ocelot", EntityOcelot.class, "Ozelot");
        a(99, "villager_golem", EntityIronGolem.class, "VillagerGolem");
        a(100, "horse", EntityHorse.class, "Horse");
        a(101, "rabbit", EntityRabbit.class, "Rabbit");
        a(102, "polar_bear", EntityPolarBear.class, "PolarBear");
        a(103, "llama", EntityLlama.class, "Llama");
        a(104, "llama_spit", EntityLlamaSpit.class, "LlamaSpit");
        a(105, "parrot", EntityParrot.class, "Parrot");
        a(120, "villager", EntityVillager.class, "Villager");
        a(200, "ender_crystal", EntityEnderCrystal.class, "EnderCrystal");
        a("bat", 4996656, 986895);
        a("blaze", 16167425, 16775294);
        a("cave_spider", 803406, 11013646);
        a("chicken", 10592673, 16711680);
        a("cow", 4470310, 10592673);
        a("creeper", 894731, 0);
        a("donkey", 5457209, 8811878);
        a("elder_guardian", 13552826, 7632531);
        a("enderman", 1447446, 0);
        a("endermite", 1447446, 7237230);
        a("evocation_illager", 9804699, 1973274);
        a("ghast", 16382457, 12369084);
        a("guardian", 5931634, 15826224);
        a("horse", 12623485, 15656192);
        a("husk", 7958625, 15125652);
        a("llama", 12623485, 10051392);
        a("magma_cube", 3407872, 16579584);
        a("mooshroom", 10489616, 12040119);
        a("mule", 1769984, 5321501);
        a("ocelot", 15720061, 5653556);
        a("parrot", 894731, 16711680);
        a("pig", 15771042, 14377823);
        a("polar_bear", 15921906, 9803152);
        a("rabbit", 10051392, 7555121);
        a("sheep", 15198183, 16758197);
        a("shulker", 9725844, 5060690);
        a("silverfish", 7237230, 3158064);
        a("skeleton", 12698049, 4802889);
        a("skeleton_horse", 6842447, 15066584);
        a("slime", 5349438, 8306542);
        a("spider", 3419431, 11013646);
        a("squid", 2243405, 7375001);
        a("stray", 6387319, 14543594);
        a("vex", 8032420, 15265265);
        a("villager", 5651507, 12422002);
        a("vindication_illager", 9804699, 2580065);
        a("witch", 3407872, 5349438);
        a("wither_skeleton", 1315860, 4672845);
        a("wolf", 14144467, 13545366);
        a("zombie", '\uafaf', 7969893);
        a("zombie_horse", 3232308, 9945732);
        a("zombie_pigman", 15373203, 5009705);
        a("zombie_villager", 5651507, 7969893);
        EntityTypes.d.add(EntityTypes.a);
    }

    private static void a(int i, String s, Class<? extends Entity> oclass, String s1) {
        try {
            oclass.getConstructor(new Class[] { World.class});
        } catch (NoSuchMethodException nosuchmethodexception) {
            throw new RuntimeException("Invalid class " + oclass + " no constructor taking " + World.class.getName());
        }

        if ((oclass.getModifiers() & 1024) == 1024) {
            throw new RuntimeException("Invalid abstract class " + oclass);
        } else {
            MinecraftKey minecraftkey = new MinecraftKey(s);

            EntityTypes.b.a(i, minecraftkey, oclass);
            EntityTypes.d.add(minecraftkey);

            while (EntityTypes.g.size() <= i) {
                EntityTypes.g.add((Object) null);
            }

            EntityTypes.g.set(i, s1);
        }
    }

    protected static EntityTypes.MonsterEggInfo a(String s, int i, int j) {
        MinecraftKey minecraftkey = new MinecraftKey(s);

        return (EntityTypes.MonsterEggInfo) EntityTypes.eggInfo.put(minecraftkey, new EntityTypes.MonsterEggInfo(minecraftkey, i, j));
    }

    public static class MonsterEggInfo {

        public final MinecraftKey a;
        public final int b;
        public final int c;
        public final Statistic killEntityStatistic;
        public final Statistic killedByEntityStatistic;

        public MonsterEggInfo(MinecraftKey minecraftkey, int i, int j) {
            this.a = minecraftkey;
            this.b = i;
            this.c = j;
            this.killEntityStatistic = StatisticList.a(this);
            this.killedByEntityStatistic = StatisticList.b(this);
        }
    }
}
