package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class PlayerSelector {

    private static final Pattern a = Pattern.compile("^@([pares])(?:\\[([^ ]*)\\])?$");
    private static final Splitter b = Splitter.on(',').omitEmptyStrings();
    private static final Splitter c = Splitter.on('=').limit(2);
    private static final Set<String> d = Sets.newHashSet();
    private static final String e = c("r");
    private static final String f = c("rm");
    private static final String g = c("l");
    private static final String h = c("lm");
    private static final String i = c("x");
    private static final String j = c("y");
    private static final String k = c("z");
    private static final String l = c("dx");
    private static final String m = c("dy");
    private static final String n = c("dz");
    private static final String o = c("rx");
    private static final String p = c("rxm");
    private static final String q = c("ry");
    private static final String r = c("rym");
    private static final String s = c("c");
    private static final String t = c("m");
    private static final String u = c("team");
    private static final String v = c("name");
    private static final String w = c("type");
    private static final String x = c("tag");
    private static final Predicate<String> y = new Predicate() {
        public boolean a(@Nullable String s) {
            return s != null && (PlayerSelector.d.contains(s) || s.length() > "score_".length() && s.startsWith("score_"));
        }

        public boolean apply(@Nullable Object object) {
            return this.a((String) object);
        }
    };
    private static final Set<String> z = Sets.newHashSet(new String[] { PlayerSelector.i, PlayerSelector.j, PlayerSelector.k, PlayerSelector.l, PlayerSelector.m, PlayerSelector.n, PlayerSelector.f, PlayerSelector.e});

    private static String c(String s) {
        PlayerSelector.d.add(s);
        return s;
    }

    @Nullable
    public static EntityPlayer getPlayer(ICommandListener icommandlistener, String s) throws CommandException {
        return (EntityPlayer) getEntity(icommandlistener, s, EntityPlayer.class);
    }

    public static List<EntityPlayer> b(ICommandListener icommandlistener, String s) throws CommandException {
        return getPlayers(icommandlistener, s, EntityPlayer.class);
    }

    @Nullable
    public static <T extends Entity> T getEntity(ICommandListener icommandlistener, String s, Class<? extends T> oclass) throws CommandException {
        List list = getPlayers(icommandlistener, s, oclass);

        return list.size() == 1 ? (Entity) list.get(0) : null;
    }

    @Nullable
    public static IChatBaseComponent getPlayerNames(ICommandListener icommandlistener, String s) throws CommandException {
        List list = getPlayers(icommandlistener, s, Entity.class);

        if (list.isEmpty()) {
            return null;
        } else {
            ArrayList arraylist = Lists.newArrayList();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                arraylist.add(entity.getScoreboardDisplayName());
            }

            return CommandAbstract.a((List) arraylist);
        }
    }

    public static <T extends Entity> List<T> getPlayers(ICommandListener icommandlistener, String s, Class<? extends T> oclass) throws CommandException {
        Matcher matcher = PlayerSelector.a.matcher(s);

        if (matcher.matches() && icommandlistener.a(1, "@")) {
            Map map = d(matcher.group(2));

            if (!b(icommandlistener, map)) {
                return Collections.emptyList();
            } else {
                String s1 = matcher.group(1);
                BlockPosition blockposition = a(map, icommandlistener.getChunkCoordinates());
                Vec3D vec3d = b(map, icommandlistener.d());
                List list = a(icommandlistener, map);
                ArrayList arraylist = Lists.newArrayList();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    World world = (World) iterator.next();

                    if (world != null) {
                        ArrayList arraylist1 = Lists.newArrayList();

                        arraylist1.addAll(a(map, s1));
                        arraylist1.addAll(b(map));
                        arraylist1.addAll(c(map));
                        arraylist1.addAll(d(map));
                        arraylist1.addAll(c(icommandlistener, map));
                        arraylist1.addAll(e(map));
                        arraylist1.addAll(f(map));
                        arraylist1.addAll(a(map, vec3d));
                        arraylist1.addAll(g(map));
                        if ("s".equalsIgnoreCase(s1)) {
                            Entity entity = icommandlistener.f();

                            if (entity != null && oclass.isAssignableFrom(entity.getClass())) {
                                if (map.containsKey(PlayerSelector.l) || map.containsKey(PlayerSelector.m) || map.containsKey(PlayerSelector.n)) {
                                    int i = a(map, PlayerSelector.l, 0);
                                    int j = a(map, PlayerSelector.m, 0);
                                    int k = a(map, PlayerSelector.n, 0);
                                    AxisAlignedBB axisalignedbb = a(blockposition, i, j, k);

                                    if (!axisalignedbb.c(entity.getBoundingBox())) {
                                        return Collections.emptyList();
                                    }
                                }

                                Iterator iterator1 = arraylist1.iterator();

                                Predicate predicate;

                                do {
                                    if (!iterator1.hasNext()) {
                                        return Lists.newArrayList(new Entity[] { entity});
                                    }

                                    predicate = (Predicate) iterator1.next();
                                } while (predicate.apply(entity));

                                return Collections.emptyList();
                            }

                            return Collections.emptyList();
                        }

                        arraylist.addAll(a(map, oclass, (List) arraylist1, s1, world, blockposition));
                    }
                }

                return a((List) arraylist, map, icommandlistener, oclass, s1, vec3d);
            }
        } else {
            return Collections.emptyList();
        }
    }

    private static List<World> a(ICommandListener icommandlistener, Map<String, String> map) {
        ArrayList arraylist = Lists.newArrayList();

        if (h(map)) {
            arraylist.add(icommandlistener.getWorld());
        } else {
            Collections.addAll(arraylist, icommandlistener.C_().worldServer);
        }

        return arraylist;
    }

    private static <T extends Entity> boolean b(ICommandListener icommandlistener, Map<String, String> map) {
        String s = b(map, PlayerSelector.w);

        if (s == null) {
            return true;
        } else {
            MinecraftKey minecraftkey = new MinecraftKey(s.startsWith("!") ? s.substring(1) : s);

            if (EntityTypes.b(minecraftkey)) {
                return true;
            } else {
                ChatMessage chatmessage = new ChatMessage("commands.generic.entity.invalidType", new Object[] { minecraftkey});

                chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
                icommandlistener.sendMessage(chatmessage);
                return false;
            }
        }
    }

    private static List<Predicate<Entity>> a(Map<String, String> map, String s) {
        String s1 = b(map, PlayerSelector.w);

        if (s1 != null && (s.equals("e") || s.equals("r") || s.equals("s"))) {
            final boolean flag = s1.startsWith("!");
            final MinecraftKey minecraftkey = new MinecraftKey(flag ? s1.substring(1) : s1);

            return Collections.singletonList(new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    return EntityTypes.a(entity, minecraftkey) != flag;
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            });
        } else {
            return !s.equals("e") && !s.equals("s") ? Collections.singletonList(new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    return entity instanceof EntityHuman;
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            }) : Collections.emptyList();
        }
    }

    private static List<Predicate<Entity>> b(Map<String, String> map) {
        ArrayList arraylist = Lists.newArrayList();
        final int i = a(map, PlayerSelector.h, -1);
        final int j = a(map, PlayerSelector.g, -1);

        if (i > -1 || j > -1) {
            arraylist.add(new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    if (!(entity instanceof EntityPlayer)) {
                        return false;
                    } else {
                        EntityPlayer entityplayer = (EntityPlayer) entity;

                        return (i <= -1 || entityplayer.expLevel >= i) && (j <= -1 || entityplayer.expLevel <= j);
                    }
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            });
        }

        return arraylist;
    }

    private static List<Predicate<Entity>> c(Map<String, String> map) {
        ArrayList arraylist = Lists.newArrayList();
        String s = b(map, PlayerSelector.t);

        if (s == null) {
            return arraylist;
        } else {
            final boolean flag = s.startsWith("!");

            if (flag) {
                s = s.substring(1);
            }

            final EnumGamemode enumgamemode;

            try {
                int i = Integer.parseInt(s);

                enumgamemode = EnumGamemode.a(i, EnumGamemode.NOT_SET);
            } catch (Throwable throwable) {
                enumgamemode = EnumGamemode.a(s, EnumGamemode.NOT_SET);
            }

            arraylist.add(new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    if (!(entity instanceof EntityPlayer)) {
                        return false;
                    } else {
                        EntityPlayer entityplayer = (EntityPlayer) entity;
                        EnumGamemode enumgamemode = entityplayer.playerInteractManager.getGameMode();

                        return flag ? enumgamemode != enumgamemode1 : enumgamemode == enumgamemode1;
                    }
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            });
            return arraylist;
        }
    }

    private static List<Predicate<Entity>> d(Map<String, String> map) {
        ArrayList arraylist = Lists.newArrayList();
        final String s = b(map, PlayerSelector.u);
        final boolean flag = s != null && s.startsWith("!");

        if (flag) {
            s = s.substring(1);
        }

        if (s != null) {
            arraylist.add(new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    if (!(entity instanceof EntityLiving)) {
                        return false;
                    } else {
                        EntityLiving entityliving = (EntityLiving) entity;
                        ScoreboardTeamBase scoreboardteambase = entityliving.aY();
                        String s = scoreboardteambase == null ? "" : scoreboardteambase.getName();

                        return s.equals(s1) != flag;
                    }
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            });
        }

        return arraylist;
    }

    private static List<Predicate<Entity>> c(final ICommandListener icommandlistener, Map<String, String> map) {
        final Map map1 = a(map);

        return (List) (map1.isEmpty() ? Collections.emptyList() : Lists.newArrayList(new Predicate[] { new Predicate() {
            public boolean a(@Nullable Entity entity) {
                if (entity == null) {
                    return false;
                } else {
                    Scoreboard scoreboard = icommandlistener.C_().getWorldServer(0).getScoreboard();
                    Iterator iterator = map.entrySet().iterator();

                    Entry entry;
                    boolean flag;
                    int i;

                    do {
                        if (!iterator.hasNext()) {
                            return true;
                        }

                        entry = (Entry) iterator.next();
                        String s = (String) entry.getKey();

                        flag = false;
                        if (s.endsWith("_min") && s.length() > 4) {
                            flag = true;
                            s = s.substring(0, s.length() - 4);
                        }

                        ScoreboardObjective scoreboardobjective = scoreboard.getObjective(s);

                        if (scoreboardobjective == null) {
                            return false;
                        }

                        String s1 = entity instanceof EntityPlayer ? entity.getName() : entity.bn();

                        if (!scoreboard.b(s1, scoreboardobjective)) {
                            return false;
                        }

                        ScoreboardScore scoreboardscore = scoreboard.getPlayerScoreForObjective(s1, scoreboardobjective);

                        i = scoreboardscore.getScore();
                        if (i < ((Integer) entry.getValue()).intValue() && flag) {
                            return false;
                        }
                    } while (i <= ((Integer) entry.getValue()).intValue() || flag);

                    return false;
                }
            }

            public boolean apply(@Nullable Object object) {
                return this.a((Entity) object);
            }
        }}));
    }

    private static List<Predicate<Entity>> e(Map<String, String> map) {
        ArrayList arraylist = Lists.newArrayList();
        final String s = b(map, PlayerSelector.v);
        final boolean flag = s != null && s.startsWith("!");

        if (flag) {
            s = s.substring(1);
        }

        if (s != null) {
            arraylist.add(new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    return entity != null && entity.getName().equals(s) != flag;
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            });
        }

        return arraylist;
    }

    private static List<Predicate<Entity>> f(Map<String, String> map) {
        ArrayList arraylist = Lists.newArrayList();
        final String s = b(map, PlayerSelector.x);
        final boolean flag = s != null && s.startsWith("!");

        if (flag) {
            s = s.substring(1);
        }

        if (s != null) {
            arraylist.add(new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    return entity == null ? false : ("".equals(s) ? entity.getScoreboardTags().isEmpty() != flag : entity.getScoreboardTags().contains(s) != flag);
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            });
        }

        return arraylist;
    }

    private static List<Predicate<Entity>> a(Map<String, String> map, final Vec3D vec3d) {
        double d0 = (double) a(map, PlayerSelector.f, -1);
        double d1 = (double) a(map, PlayerSelector.e, -1);
        final boolean flag = d0 < -0.5D;
        final boolean flag1 = d1 < -0.5D;

        if (flag && flag1) {
            return Collections.emptyList();
        } else {
            double d2 = Math.max(d0, 1.0E-4D);
            final double d3 = d2 * d2;
            double d4 = Math.max(d1, 1.0E-4D);
            final double d5 = d4 * d4;

            return Lists.newArrayList(new Predicate[] { new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    if (entity == null) {
                        return false;
                    } else {
                        double d0 = vec3d.c(entity.locX, entity.locY, entity.locZ);

                        return (flag || d0 >= d1) && (flag1 || d0 <= d2);
                    }
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            }});
        }
    }

    private static List<Predicate<Entity>> g(Map<String, String> map) {
        ArrayList arraylist = Lists.newArrayList();
        final int i;
        final int j;

        if (map.containsKey(PlayerSelector.r) || map.containsKey(PlayerSelector.q)) {
            i = MathHelper.b(a(map, PlayerSelector.r, 0));
            j = MathHelper.b(a(map, PlayerSelector.q, 359));
            arraylist.add(new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    if (entity == null) {
                        return false;
                    } else {
                        int i = MathHelper.b(MathHelper.d(entity.yaw));

                        return j > k ? i >= j || i <= k : i >= j && i <= k;
                    }
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            });
        }

        if (map.containsKey(PlayerSelector.p) || map.containsKey(PlayerSelector.o)) {
            i = MathHelper.b(a(map, PlayerSelector.p, 0));
            j = MathHelper.b(a(map, PlayerSelector.o, 359));
            arraylist.add(new Predicate() {
                public boolean a(@Nullable Entity entity) {
                    if (entity == null) {
                        return false;
                    } else {
                        int i = MathHelper.b(MathHelper.d(entity.pitch));

                        return j > k ? i >= j || i <= k : i >= j && i <= k;
                    }
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((Entity) object);
                }
            });
        }

        return arraylist;
    }

    private static <T extends Entity> List<T> a(Map<String, String> map, Class<? extends T> oclass, List<Predicate<Entity>> list, String s, World world, BlockPosition blockposition) {
        ArrayList arraylist = Lists.newArrayList();
        String s1 = b(map, PlayerSelector.w);

        s1 = s1 != null && s1.startsWith("!") ? s1.substring(1) : s1;
        boolean flag = !s.equals("e");
        boolean flag1 = s.equals("r") && s1 != null;
        int i = a(map, PlayerSelector.l, 0);
        int j = a(map, PlayerSelector.m, 0);
        int k = a(map, PlayerSelector.n, 0);
        int l = a(map, PlayerSelector.e, -1);
        Predicate predicate = Predicates.and(list);
        Predicate predicate1 = Predicates.and(IEntitySelector.a, predicate);
        final AxisAlignedBB axisalignedbb;

        if (!map.containsKey(PlayerSelector.l) && !map.containsKey(PlayerSelector.m) && !map.containsKey(PlayerSelector.n)) {
            if (l >= 0) {
                axisalignedbb = new AxisAlignedBB((double) (blockposition.getX() - l), (double) (blockposition.getY() - l), (double) (blockposition.getZ() - l), (double) (blockposition.getX() + l + 1), (double) (blockposition.getY() + l + 1), (double) (blockposition.getZ() + l + 1));
                if (flag && !flag1) {
                    arraylist.addAll(world.b(oclass, predicate1));
                } else {
                    arraylist.addAll(world.a(oclass, axisalignedbb, predicate1));
                }
            } else if (s.equals("a")) {
                arraylist.addAll(world.b(oclass, predicate));
            } else if (!s.equals("p") && (!s.equals("r") || flag1)) {
                arraylist.addAll(world.a(oclass, predicate1));
            } else {
                arraylist.addAll(world.b(oclass, predicate1));
            }
        } else {
            axisalignedbb = a(blockposition, i, j, k);
            if (flag && !flag1) {
                Predicate predicate2 = new Predicate() {
                    public boolean a(@Nullable Entity entity) {
                        return entity != null && axisalignedbb.c(entity.getBoundingBox());
                    }

                    public boolean apply(@Nullable Object object) {
                        return this.a((Entity) object);
                    }
                };

                arraylist.addAll(world.b(oclass, Predicates.and(predicate1, predicate2)));
            } else {
                arraylist.addAll(world.a(oclass, axisalignedbb, predicate1));
            }
        }

        return arraylist;
    }

    private static <T extends Entity> List<T> a(List<T> list, Map<String, String> map, ICommandListener icommandlistener, Class<? extends T> oclass, String s, final Vec3D vec3d) {
        int i = a(map, PlayerSelector.s, !s.equals("a") && !s.equals("e") ? 1 : 0);

        if (!s.equals("p") && !s.equals("a") && !s.equals("e")) {
            if (s.equals("r")) {
                Collections.shuffle((List) list);
            }
        } else {
            Collections.sort((List) list, new Comparator() {
                public int a(Entity entity, Entity entity1) {
                    return ComparisonChain.start().compare(entity.d(vec3d.x, vec3d.y, vec3d.z), entity1.d(vec3d.x, vec3d.y, vec3d.z)).result();
                }

                public int compare(Object object, Object object1) {
                    return this.a((Entity) object, (Entity) object1);
                }
            });
        }

        Entity entity = icommandlistener.f();

        if (entity != null && oclass.isAssignableFrom(entity.getClass()) && i == 1 && ((List) list).contains(entity) && !"r".equals(s)) {
            list = Lists.newArrayList(new Entity[] { entity});
        }

        if (i != 0) {
            if (i < 0) {
                Collections.reverse((List) list);
            }

            list = ((List) list).subList(0, Math.min(Math.abs(i), ((List) list).size()));
        }

        return (List) list;
    }

    private static AxisAlignedBB a(BlockPosition blockposition, int i, int j, int k) {
        boolean flag = i < 0;
        boolean flag1 = j < 0;
        boolean flag2 = k < 0;
        int l = blockposition.getX() + (flag ? i : 0);
        int i1 = blockposition.getY() + (flag1 ? j : 0);
        int j1 = blockposition.getZ() + (flag2 ? k : 0);
        int k1 = blockposition.getX() + (flag ? 0 : i) + 1;
        int l1 = blockposition.getY() + (flag1 ? 0 : j) + 1;
        int i2 = blockposition.getZ() + (flag2 ? 0 : k) + 1;

        return new AxisAlignedBB((double) l, (double) i1, (double) j1, (double) k1, (double) l1, (double) i2);
    }

    private static BlockPosition a(Map<String, String> map, BlockPosition blockposition) {
        return new BlockPosition(a(map, PlayerSelector.i, blockposition.getX()), a(map, PlayerSelector.j, blockposition.getY()), a(map, PlayerSelector.k, blockposition.getZ()));
    }

    private static Vec3D b(Map<String, String> map, Vec3D vec3d) {
        return new Vec3D(a(map, PlayerSelector.i, vec3d.x, true), a(map, PlayerSelector.j, vec3d.y, false), a(map, PlayerSelector.k, vec3d.z, true));
    }

    private static double a(Map<String, String> map, String s, double d0, boolean flag) {
        return map.containsKey(s) ? (double) MathHelper.a((String) map.get(s), MathHelper.floor(d0)) + (flag ? 0.5D : 0.0D) : d0;
    }

    private static boolean h(Map<String, String> map) {
        Iterator iterator = PlayerSelector.z.iterator();

        String s;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            s = (String) iterator.next();
        } while (!map.containsKey(s));

        return true;
    }

    private static int a(Map<String, String> map, String s, int i) {
        return map.containsKey(s) ? MathHelper.a((String) map.get(s), i) : i;
    }

    @Nullable
    private static String b(Map<String, String> map, String s) {
        return (String) map.get(s);
    }

    public static Map<String, Integer> a(Map<String, String> map) {
        HashMap hashmap = Maps.newHashMap();
        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            if (s.startsWith("score_") && s.length() > "score_".length()) {
                hashmap.put(s.substring("score_".length()), Integer.valueOf(MathHelper.a((String) map.get(s), 1)));
            }
        }

        return hashmap;
    }

    public static boolean isList(String s) throws CommandException {
        Matcher matcher = PlayerSelector.a.matcher(s);

        if (!matcher.matches()) {
            return false;
        } else {
            Map map = d(matcher.group(2));
            String s1 = matcher.group(1);
            int i = !"a".equals(s1) && !"e".equals(s1) ? 1 : 0;

            return a(map, PlayerSelector.s, i) != 1;
        }
    }

    public static boolean isPattern(String s) {
        return PlayerSelector.a.matcher(s).matches();
    }

    private static Map<String, String> d(@Nullable String s) throws CommandException {
        HashMap hashmap = Maps.newHashMap();

        if (s == null) {
            return hashmap;
        } else {
            Iterator iterator = PlayerSelector.b.split(s).iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();
                Iterator iterator1 = PlayerSelector.c.split(s1).iterator();
                String s2 = (String) iterator1.next();

                if (!PlayerSelector.y.apply(s2)) {
                    throw new CommandException("commands.generic.selector_argument", new Object[] { s1});
                }

                hashmap.put(s2, iterator1.hasNext() ? (String) iterator1.next() : "");
            }

            return hashmap;
        }
    }
}
