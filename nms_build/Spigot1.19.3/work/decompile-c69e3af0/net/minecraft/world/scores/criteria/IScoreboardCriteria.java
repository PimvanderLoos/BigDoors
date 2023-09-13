package net.minecraft.world.scores.criteria;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.stats.StatisticWrapper;
import net.minecraft.util.INamable;

public class IScoreboardCriteria {

    private static final Map<String, IScoreboardCriteria> CUSTOM_CRITERIA = Maps.newHashMap();
    public static final Map<String, IScoreboardCriteria> CRITERIA_CACHE = Maps.newHashMap();
    public static final IScoreboardCriteria DUMMY = registerCustom("dummy");
    public static final IScoreboardCriteria TRIGGER = registerCustom("trigger");
    public static final IScoreboardCriteria DEATH_COUNT = registerCustom("deathCount");
    public static final IScoreboardCriteria KILL_COUNT_PLAYERS = registerCustom("playerKillCount");
    public static final IScoreboardCriteria KILL_COUNT_ALL = registerCustom("totalKillCount");
    public static final IScoreboardCriteria HEALTH = registerCustom("health", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.HEARTS);
    public static final IScoreboardCriteria FOOD = registerCustom("food", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria AIR = registerCustom("air", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria ARMOR = registerCustom("armor", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria EXPERIENCE = registerCustom("xp", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria LEVEL = registerCustom("level", true, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    public static final IScoreboardCriteria[] TEAM_KILL = new IScoreboardCriteria[]{registerCustom("teamkill." + EnumChatFormat.BLACK.getName()), registerCustom("teamkill." + EnumChatFormat.DARK_BLUE.getName()), registerCustom("teamkill." + EnumChatFormat.DARK_GREEN.getName()), registerCustom("teamkill." + EnumChatFormat.DARK_AQUA.getName()), registerCustom("teamkill." + EnumChatFormat.DARK_RED.getName()), registerCustom("teamkill." + EnumChatFormat.DARK_PURPLE.getName()), registerCustom("teamkill." + EnumChatFormat.GOLD.getName()), registerCustom("teamkill." + EnumChatFormat.GRAY.getName()), registerCustom("teamkill." + EnumChatFormat.DARK_GRAY.getName()), registerCustom("teamkill." + EnumChatFormat.BLUE.getName()), registerCustom("teamkill." + EnumChatFormat.GREEN.getName()), registerCustom("teamkill." + EnumChatFormat.AQUA.getName()), registerCustom("teamkill." + EnumChatFormat.RED.getName()), registerCustom("teamkill." + EnumChatFormat.LIGHT_PURPLE.getName()), registerCustom("teamkill." + EnumChatFormat.YELLOW.getName()), registerCustom("teamkill." + EnumChatFormat.WHITE.getName())};
    public static final IScoreboardCriteria[] KILLED_BY_TEAM = new IScoreboardCriteria[]{registerCustom("killedByTeam." + EnumChatFormat.BLACK.getName()), registerCustom("killedByTeam." + EnumChatFormat.DARK_BLUE.getName()), registerCustom("killedByTeam." + EnumChatFormat.DARK_GREEN.getName()), registerCustom("killedByTeam." + EnumChatFormat.DARK_AQUA.getName()), registerCustom("killedByTeam." + EnumChatFormat.DARK_RED.getName()), registerCustom("killedByTeam." + EnumChatFormat.DARK_PURPLE.getName()), registerCustom("killedByTeam." + EnumChatFormat.GOLD.getName()), registerCustom("killedByTeam." + EnumChatFormat.GRAY.getName()), registerCustom("killedByTeam." + EnumChatFormat.DARK_GRAY.getName()), registerCustom("killedByTeam." + EnumChatFormat.BLUE.getName()), registerCustom("killedByTeam." + EnumChatFormat.GREEN.getName()), registerCustom("killedByTeam." + EnumChatFormat.AQUA.getName()), registerCustom("killedByTeam." + EnumChatFormat.RED.getName()), registerCustom("killedByTeam." + EnumChatFormat.LIGHT_PURPLE.getName()), registerCustom("killedByTeam." + EnumChatFormat.YELLOW.getName()), registerCustom("killedByTeam." + EnumChatFormat.WHITE.getName())};
    private final String name;
    private final boolean readOnly;
    private final IScoreboardCriteria.EnumScoreboardHealthDisplay renderType;

    private static IScoreboardCriteria registerCustom(String s, boolean flag, IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        IScoreboardCriteria iscoreboardcriteria = new IScoreboardCriteria(s, flag, iscoreboardcriteria_enumscoreboardhealthdisplay);

        IScoreboardCriteria.CUSTOM_CRITERIA.put(s, iscoreboardcriteria);
        return iscoreboardcriteria;
    }

    private static IScoreboardCriteria registerCustom(String s) {
        return registerCustom(s, false, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
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

    public static Set<String> getCustomCriteriaNames() {
        return ImmutableSet.copyOf(IScoreboardCriteria.CUSTOM_CRITERIA.keySet());
    }

    public static Optional<IScoreboardCriteria> byName(String s) {
        IScoreboardCriteria iscoreboardcriteria = (IScoreboardCriteria) IScoreboardCriteria.CRITERIA_CACHE.get(s);

        if (iscoreboardcriteria != null) {
            return Optional.of(iscoreboardcriteria);
        } else {
            int i = s.indexOf(58);

            return i < 0 ? Optional.empty() : BuiltInRegistries.STAT_TYPE.getOptional(MinecraftKey.of(s.substring(0, i), '.')).flatMap((statisticwrapper) -> {
                return getStat(statisticwrapper, MinecraftKey.of(s.substring(i + 1), '.'));
            });
        }
    }

    private static <T> Optional<IScoreboardCriteria> getStat(StatisticWrapper<T> statisticwrapper, MinecraftKey minecraftkey) {
        Optional optional = statisticwrapper.getRegistry().getOptional(minecraftkey);

        Objects.requireNonNull(statisticwrapper);
        return optional.map(statisticwrapper::get);
    }

    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public IScoreboardCriteria.EnumScoreboardHealthDisplay getDefaultRenderType() {
        return this.renderType;
    }

    public static enum EnumScoreboardHealthDisplay implements INamable {

        INTEGER("integer"), HEARTS("hearts");

        private final String id;
        public static final INamable.a<IScoreboardCriteria.EnumScoreboardHealthDisplay> CODEC = INamable.fromEnum(IScoreboardCriteria.EnumScoreboardHealthDisplay::values);

        private EnumScoreboardHealthDisplay(String s) {
            this.id = s;
        }

        public String getId() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }

        public static IScoreboardCriteria.EnumScoreboardHealthDisplay byId(String s) {
            return (IScoreboardCriteria.EnumScoreboardHealthDisplay) IScoreboardCriteria.EnumScoreboardHealthDisplay.CODEC.byName(s, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        }
    }
}
