package net.minecraft.world.scores.criteria;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.stats.StatisticWrapper;

public class IScoreboardCriteria {

    private static final Map<String, IScoreboardCriteria> CUSTOM_CRITERIA = Maps.newHashMap();
    public static final Map<String, IScoreboardCriteria> CRITERIA_CACHE = Maps.newHashMap();
    public static final IScoreboardCriteria DUMMY = b("dummy");
    public static final IScoreboardCriteria TRIGGER = b("trigger");
    public static final IScoreboardCriteria DEATH_COUNT = b("deathCount");
    public static final IScoreboardCriteria KILL_COUNT_PLAYERS = b("playerKillCount");
    public static final IScoreboardCriteria KILL_COUNT_ALL = b("totalKillCount");
    public static final IScoreboardCriteria HEALTH = a("health", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.HEARTS);
    public static final IScoreboardCriteria FOOD = a("food", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria AIR = a("air", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria ARMOR = a("armor", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria EXPERIENCE = a("xp", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria LEVEL = a("level", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria[] TEAM_KILL = new IScoreboardCriteria[]{b("teamkill." + EnumChatFormat.BLACK.f()), b("teamkill." + EnumChatFormat.DARK_BLUE.f()), b("teamkill." + EnumChatFormat.DARK_GREEN.f()), b("teamkill." + EnumChatFormat.DARK_AQUA.f()), b("teamkill." + EnumChatFormat.DARK_RED.f()), b("teamkill." + EnumChatFormat.DARK_PURPLE.f()), b("teamkill." + EnumChatFormat.GOLD.f()), b("teamkill." + EnumChatFormat.GRAY.f()), b("teamkill." + EnumChatFormat.DARK_GRAY.f()), b("teamkill." + EnumChatFormat.BLUE.f()), b("teamkill." + EnumChatFormat.GREEN.f()), b("teamkill." + EnumChatFormat.AQUA.f()), b("teamkill." + EnumChatFormat.RED.f()), b("teamkill." + EnumChatFormat.LIGHT_PURPLE.f()), b("teamkill." + EnumChatFormat.YELLOW.f()), b("teamkill." + EnumChatFormat.WHITE.f())};
    public static final IScoreboardCriteria[] KILLED_BY_TEAM = new IScoreboardCriteria[]{b("killedByTeam." + EnumChatFormat.BLACK.f()), b("killedByTeam." + EnumChatFormat.DARK_BLUE.f()), b("killedByTeam." + EnumChatFormat.DARK_GREEN.f()), b("killedByTeam." + EnumChatFormat.DARK_AQUA.f()), b("killedByTeam." + EnumChatFormat.DARK_RED.f()), b("killedByTeam." + EnumChatFormat.DARK_PURPLE.f()), b("killedByTeam." + EnumChatFormat.GOLD.f()), b("killedByTeam." + EnumChatFormat.GRAY.f()), b("killedByTeam." + EnumChatFormat.DARK_GRAY.f()), b("killedByTeam." + EnumChatFormat.BLUE.f()), b("killedByTeam." + EnumChatFormat.GREEN.f()), b("killedByTeam." + EnumChatFormat.AQUA.f()), b("killedByTeam." + EnumChatFormat.RED.f()), b("killedByTeam." + EnumChatFormat.LIGHT_PURPLE.f()), b("killedByTeam." + EnumChatFormat.YELLOW.f()), b("killedByTeam." + EnumChatFormat.WHITE.f())};
    private final String name;
    private final boolean readOnly;
    private final IScoreboardCriteria.EnumScoreboardHealthDisplay renderType;

    private static IScoreboardCriteria a(String s, boolean flag, IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        IScoreboardCriteria iscoreboardcriteria = new IScoreboardCriteria(s, flag, iscoreboardcriteria_enumscoreboardhealthdisplay);

        IScoreboardCriteria.CUSTOM_CRITERIA.put(s, iscoreboardcriteria);
        return iscoreboardcriteria;
    }

    private static IScoreboardCriteria b(String s) {
        return a(s, false, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    }

    protected IScoreboardCriteria(String s) {
        this(s, false, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    }

    protected IScoreboardCriteria(String s, boolean flag, IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        this.name = s;
        this.readOnly = flag;
        this.renderType = iscoreboardcriteria_enumscoreboardhealthdisplay;
        IScoreboardCriteria.CRITERIA_CACHE.put(s, this);
    }

    public static Set<String> c() {
        return ImmutableSet.copyOf(IScoreboardCriteria.CUSTOM_CRITERIA.keySet());
    }

    public static Optional<IScoreboardCriteria> a(String s) {
        IScoreboardCriteria iscoreboardcriteria = (IScoreboardCriteria) IScoreboardCriteria.CRITERIA_CACHE.get(s);

        if (iscoreboardcriteria != null) {
            return Optional.of(iscoreboardcriteria);
        } else {
            int i = s.indexOf(58);

            return i < 0 ? Optional.empty() : IRegistry.STAT_TYPE.getOptional(MinecraftKey.a(s.substring(0, i), '.')).flatMap((statisticwrapper) -> {
                return a(statisticwrapper, MinecraftKey.a(s.substring(i + 1), '.'));
            });
        }
    }

    private static <T> Optional<IScoreboardCriteria> a(StatisticWrapper<T> statisticwrapper, MinecraftKey minecraftkey) {
        Optional optional = statisticwrapper.getRegistry().getOptional(minecraftkey);

        Objects.requireNonNull(statisticwrapper);
        return optional.map(statisticwrapper::b);
    }

    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public IScoreboardCriteria.EnumScoreboardHealthDisplay f() {
        return this.renderType;
    }

    public static enum EnumScoreboardHealthDisplay {

        INTEGER("integer"), HEARTS("hearts");

        private final String id;
        private static final Map<String, IScoreboardCriteria.EnumScoreboardHealthDisplay> BY_ID;

        private EnumScoreboardHealthDisplay(String s) {
            this.id = s;
        }

        public String a() {
            return this.id;
        }

        public static IScoreboardCriteria.EnumScoreboardHealthDisplay a(String s) {
            return (IScoreboardCriteria.EnumScoreboardHealthDisplay) IScoreboardCriteria.EnumScoreboardHealthDisplay.BY_ID.getOrDefault(s, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        }

        static {
            Builder<String, IScoreboardCriteria.EnumScoreboardHealthDisplay> builder = ImmutableMap.builder();
            IScoreboardCriteria.EnumScoreboardHealthDisplay[] aiscoreboardcriteria_enumscoreboardhealthdisplay = values();
            int i = aiscoreboardcriteria_enumscoreboardhealthdisplay.length;

            for (int j = 0; j < i; ++j) {
                IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay = aiscoreboardcriteria_enumscoreboardhealthdisplay[j];

                builder.put(iscoreboardcriteria_enumscoreboardhealthdisplay.id, iscoreboardcriteria_enumscoreboardhealthdisplay);
            }

            BY_ID = builder.build();
        }
    }
}
