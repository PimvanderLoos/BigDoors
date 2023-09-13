package net.minecraft.commands.arguments.selector.options;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.critereon.CriterionConditionRange;
import net.minecraft.advancements.critereon.CriterionConditionValue;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.AdvancementDataWorld;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;
import net.minecraft.world.scores.ScoreboardTeamBase;

public class PlayerSelector {

    private static final Map<String, PlayerSelector.b> OPTIONS = Maps.newHashMap();
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.unknown", new Object[]{object});
    });
    public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.inapplicable", new Object[]{object});
    });
    public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType(new ChatMessage("argument.entity.options.distance.negative"));
    public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE = new SimpleCommandExceptionType(new ChatMessage("argument.entity.options.level.negative"));
    public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType(new ChatMessage("argument.entity.options.limit.toosmall"));
    public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.sort.irreversible", new Object[]{object});
    });
    public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.mode.invalid", new Object[]{object});
    });
    public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.type.invalid", new Object[]{object});
    });

    public PlayerSelector() {}

    private static void register(String s, PlayerSelector.a playerselector_a, Predicate<ArgumentParserSelector> predicate, IChatBaseComponent ichatbasecomponent) {
        PlayerSelector.OPTIONS.put(s, new PlayerSelector.b(playerselector_a, predicate, ichatbasecomponent));
    }

    public static void bootStrap() {
        if (PlayerSelector.OPTIONS.isEmpty()) {
            register("name", (argumentparserselector) -> {
                int i = argumentparserselector.getReader().getCursor();
                boolean flag = argumentparserselector.shouldInvertValue();
                String s = argumentparserselector.getReader().readString();

                if (argumentparserselector.hasNameNotEquals() && !flag) {
                    argumentparserselector.getReader().setCursor(i);
                    throw PlayerSelector.ERROR_INAPPLICABLE_OPTION.createWithContext(argumentparserselector.getReader(), "name");
                } else {
                    if (flag) {
                        argumentparserselector.setHasNameNotEquals(true);
                    } else {
                        argumentparserselector.setHasNameEquals(true);
                    }

                    argumentparserselector.addPredicate((entity) -> {
                        return entity.getName().getString().equals(s) != flag;
                    });
                }
            }, (argumentparserselector) -> {
                return !argumentparserselector.hasNameEquals();
            }, new ChatMessage("argument.entity.options.name.description"));
            register("distance", (argumentparserselector) -> {
                int i = argumentparserselector.getReader().getCursor();
                CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange = CriterionConditionValue.DoubleRange.fromReader(argumentparserselector.getReader());

                if ((criterionconditionvalue_doublerange.getMin() == null || (Double) criterionconditionvalue_doublerange.getMin() >= 0.0D) && (criterionconditionvalue_doublerange.getMax() == null || (Double) criterionconditionvalue_doublerange.getMax() >= 0.0D)) {
                    argumentparserselector.setDistance(criterionconditionvalue_doublerange);
                    argumentparserselector.setWorldLimited();
                } else {
                    argumentparserselector.getReader().setCursor(i);
                    throw PlayerSelector.ERROR_RANGE_NEGATIVE.createWithContext(argumentparserselector.getReader());
                }
            }, (argumentparserselector) -> {
                return argumentparserselector.getDistance().isAny();
            }, new ChatMessage("argument.entity.options.distance.description"));
            register("level", (argumentparserselector) -> {
                int i = argumentparserselector.getReader().getCursor();
                CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromReader(argumentparserselector.getReader());

                if ((criterionconditionvalue_integerrange.getMin() == null || (Integer) criterionconditionvalue_integerrange.getMin() >= 0) && (criterionconditionvalue_integerrange.getMax() == null || (Integer) criterionconditionvalue_integerrange.getMax() >= 0)) {
                    argumentparserselector.setLevel(criterionconditionvalue_integerrange);
                    argumentparserselector.setIncludesEntities(false);
                } else {
                    argumentparserselector.getReader().setCursor(i);
                    throw PlayerSelector.ERROR_LEVEL_NEGATIVE.createWithContext(argumentparserselector.getReader());
                }
            }, (argumentparserselector) -> {
                return argumentparserselector.getLevel().isAny();
            }, new ChatMessage("argument.entity.options.level.description"));
            register("x", (argumentparserselector) -> {
                argumentparserselector.setWorldLimited();
                argumentparserselector.setX(argumentparserselector.getReader().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.getX() == null;
            }, new ChatMessage("argument.entity.options.x.description"));
            register("y", (argumentparserselector) -> {
                argumentparserselector.setWorldLimited();
                argumentparserselector.setY(argumentparserselector.getReader().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.getY() == null;
            }, new ChatMessage("argument.entity.options.y.description"));
            register("z", (argumentparserselector) -> {
                argumentparserselector.setWorldLimited();
                argumentparserselector.setZ(argumentparserselector.getReader().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.getZ() == null;
            }, new ChatMessage("argument.entity.options.z.description"));
            register("dx", (argumentparserselector) -> {
                argumentparserselector.setWorldLimited();
                argumentparserselector.setDeltaX(argumentparserselector.getReader().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.getDeltaX() == null;
            }, new ChatMessage("argument.entity.options.dx.description"));
            register("dy", (argumentparserselector) -> {
                argumentparserselector.setWorldLimited();
                argumentparserselector.setDeltaY(argumentparserselector.getReader().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.getDeltaY() == null;
            }, new ChatMessage("argument.entity.options.dy.description"));
            register("dz", (argumentparserselector) -> {
                argumentparserselector.setWorldLimited();
                argumentparserselector.setDeltaZ(argumentparserselector.getReader().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.getDeltaZ() == null;
            }, new ChatMessage("argument.entity.options.dz.description"));
            register("x_rotation", (argumentparserselector) -> {
                argumentparserselector.setRotX(CriterionConditionRange.fromReader(argumentparserselector.getReader(), true, MathHelper::wrapDegrees));
            }, (argumentparserselector) -> {
                return argumentparserselector.getRotX() == CriterionConditionRange.ANY;
            }, new ChatMessage("argument.entity.options.x_rotation.description"));
            register("y_rotation", (argumentparserselector) -> {
                argumentparserselector.setRotY(CriterionConditionRange.fromReader(argumentparserselector.getReader(), true, MathHelper::wrapDegrees));
            }, (argumentparserselector) -> {
                return argumentparserselector.getRotY() == CriterionConditionRange.ANY;
            }, new ChatMessage("argument.entity.options.y_rotation.description"));
            register("limit", (argumentparserselector) -> {
                int i = argumentparserselector.getReader().getCursor();
                int j = argumentparserselector.getReader().readInt();

                if (j < 1) {
                    argumentparserselector.getReader().setCursor(i);
                    throw PlayerSelector.ERROR_LIMIT_TOO_SMALL.createWithContext(argumentparserselector.getReader());
                } else {
                    argumentparserselector.setMaxResults(j);
                    argumentparserselector.setLimited(true);
                }
            }, (argumentparserselector) -> {
                return !argumentparserselector.isCurrentEntity() && !argumentparserselector.isLimited();
            }, new ChatMessage("argument.entity.options.limit.description"));
            register("sort", (argumentparserselector) -> {
                int i = argumentparserselector.getReader().getCursor();
                String s = argumentparserselector.getReader().readUnquotedString();

                argumentparserselector.setSuggestions((suggestionsbuilder, consumer) -> {
                    return ICompletionProvider.suggest((Iterable) Arrays.asList("nearest", "furthest", "random", "arbitrary"), suggestionsbuilder);
                });
                byte b0 = -1;

                switch (s.hashCode()) {
                    case -938285885:
                        if (s.equals("random")) {
                            b0 = 2;
                        }
                        break;
                    case 1510793967:
                        if (s.equals("furthest")) {
                            b0 = 1;
                        }
                        break;
                    case 1780188658:
                        if (s.equals("arbitrary")) {
                            b0 = 3;
                        }
                        break;
                    case 1825779806:
                        if (s.equals("nearest")) {
                            b0 = 0;
                        }
                }

                BiConsumer biconsumer;

                switch (b0) {
                    case 0:
                        biconsumer = ArgumentParserSelector.ORDER_NEAREST;
                        break;
                    case 1:
                        biconsumer = ArgumentParserSelector.ORDER_FURTHEST;
                        break;
                    case 2:
                        biconsumer = ArgumentParserSelector.ORDER_RANDOM;
                        break;
                    case 3:
                        biconsumer = ArgumentParserSelector.ORDER_ARBITRARY;
                        break;
                    default:
                        argumentparserselector.getReader().setCursor(i);
                        throw PlayerSelector.ERROR_SORT_UNKNOWN.createWithContext(argumentparserselector.getReader(), s);
                }

                argumentparserselector.setOrder(biconsumer);
                argumentparserselector.setSorted(true);
            }, (argumentparserselector) -> {
                return !argumentparserselector.isCurrentEntity() && !argumentparserselector.isSorted();
            }, new ChatMessage("argument.entity.options.sort.description"));
            register("gamemode", (argumentparserselector) -> {
                argumentparserselector.setSuggestions((suggestionsbuilder, consumer) -> {
                    String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);
                    boolean flag = !argumentparserselector.hasGamemodeNotEquals();
                    boolean flag1 = true;

                    if (!s.isEmpty()) {
                        if (s.charAt(0) == '!') {
                            flag = false;
                            s = s.substring(1);
                        } else {
                            flag1 = false;
                        }
                    }

                    EnumGamemode[] aenumgamemode = EnumGamemode.values();
                    int i = aenumgamemode.length;

                    for (int j = 0; j < i; ++j) {
                        EnumGamemode enumgamemode = aenumgamemode[j];

                        if (enumgamemode.getName().toLowerCase(Locale.ROOT).startsWith(s)) {
                            if (flag1) {
                                suggestionsbuilder.suggest("!" + enumgamemode.getName());
                            }

                            if (flag) {
                                suggestionsbuilder.suggest(enumgamemode.getName());
                            }
                        }
                    }

                    return suggestionsbuilder.buildFuture();
                });
                int i = argumentparserselector.getReader().getCursor();
                boolean flag = argumentparserselector.shouldInvertValue();

                if (argumentparserselector.hasGamemodeNotEquals() && !flag) {
                    argumentparserselector.getReader().setCursor(i);
                    throw PlayerSelector.ERROR_INAPPLICABLE_OPTION.createWithContext(argumentparserselector.getReader(), "gamemode");
                } else {
                    String s = argumentparserselector.getReader().readUnquotedString();
                    EnumGamemode enumgamemode = EnumGamemode.byName(s, (EnumGamemode) null);

                    if (enumgamemode == null) {
                        argumentparserselector.getReader().setCursor(i);
                        throw PlayerSelector.ERROR_GAME_MODE_INVALID.createWithContext(argumentparserselector.getReader(), s);
                    } else {
                        argumentparserselector.setIncludesEntities(false);
                        argumentparserselector.addPredicate((entity) -> {
                            if (!(entity instanceof EntityPlayer)) {
                                return false;
                            } else {
                                EnumGamemode enumgamemode1 = ((EntityPlayer) entity).gameMode.getGameModeForPlayer();

                                return flag ? enumgamemode1 != enumgamemode : enumgamemode1 == enumgamemode;
                            }
                        });
                        if (flag) {
                            argumentparserselector.setHasGamemodeNotEquals(true);
                        } else {
                            argumentparserselector.setHasGamemodeEquals(true);
                        }

                    }
                }
            }, (argumentparserselector) -> {
                return !argumentparserselector.hasGamemodeEquals();
            }, new ChatMessage("argument.entity.options.gamemode.description"));
            register("team", (argumentparserselector) -> {
                boolean flag = argumentparserselector.shouldInvertValue();
                String s = argumentparserselector.getReader().readUnquotedString();

                argumentparserselector.addPredicate((entity) -> {
                    if (!(entity instanceof EntityLiving)) {
                        return false;
                    } else {
                        ScoreboardTeamBase scoreboardteambase = entity.getTeam();
                        String s1 = scoreboardteambase == null ? "" : scoreboardteambase.getName();

                        return s1.equals(s) != flag;
                    }
                });
                if (flag) {
                    argumentparserselector.setHasTeamNotEquals(true);
                } else {
                    argumentparserselector.setHasTeamEquals(true);
                }

            }, (argumentparserselector) -> {
                return !argumentparserselector.hasTeamEquals();
            }, new ChatMessage("argument.entity.options.team.description"));
            register("type", (argumentparserselector) -> {
                argumentparserselector.setSuggestions((suggestionsbuilder, consumer) -> {
                    ICompletionProvider.suggestResource(IRegistry.ENTITY_TYPE.keySet(), suggestionsbuilder, String.valueOf('!'));
                    ICompletionProvider.suggestResource(TagsEntity.getAllTags().getAvailableTags(), suggestionsbuilder, "!#");
                    if (!argumentparserselector.isTypeLimitedInversely()) {
                        ICompletionProvider.suggestResource((Iterable) IRegistry.ENTITY_TYPE.keySet(), suggestionsbuilder);
                        ICompletionProvider.suggestResource(TagsEntity.getAllTags().getAvailableTags(), suggestionsbuilder, String.valueOf('#'));
                    }

                    return suggestionsbuilder.buildFuture();
                });
                int i = argumentparserselector.getReader().getCursor();
                boolean flag = argumentparserselector.shouldInvertValue();

                if (argumentparserselector.isTypeLimitedInversely() && !flag) {
                    argumentparserselector.getReader().setCursor(i);
                    throw PlayerSelector.ERROR_INAPPLICABLE_OPTION.createWithContext(argumentparserselector.getReader(), "type");
                } else {
                    if (flag) {
                        argumentparserselector.setTypeLimitedInversely();
                    }

                    MinecraftKey minecraftkey;

                    if (argumentparserselector.isTag()) {
                        minecraftkey = MinecraftKey.read(argumentparserselector.getReader());
                        argumentparserselector.addPredicate((entity) -> {
                            return entity.getType().is(entity.getServer().getTags().getOrEmpty(IRegistry.ENTITY_TYPE_REGISTRY).getTagOrEmpty(minecraftkey)) != flag;
                        });
                    } else {
                        minecraftkey = MinecraftKey.read(argumentparserselector.getReader());
                        EntityTypes<?> entitytypes = (EntityTypes) IRegistry.ENTITY_TYPE.getOptional(minecraftkey).orElseThrow(() -> {
                            argumentparserselector.getReader().setCursor(i);
                            return PlayerSelector.ERROR_ENTITY_TYPE_INVALID.createWithContext(argumentparserselector.getReader(), minecraftkey.toString());
                        });

                        if (Objects.equals(EntityTypes.PLAYER, entitytypes) && !flag) {
                            argumentparserselector.setIncludesEntities(false);
                        }

                        argumentparserselector.addPredicate((entity) -> {
                            return Objects.equals(entitytypes, entity.getType()) != flag;
                        });
                        if (!flag) {
                            argumentparserselector.limitToType(entitytypes);
                        }
                    }

                }
            }, (argumentparserselector) -> {
                return !argumentparserselector.isTypeLimited();
            }, new ChatMessage("argument.entity.options.type.description"));
            register("tag", (argumentparserselector) -> {
                boolean flag = argumentparserselector.shouldInvertValue();
                String s = argumentparserselector.getReader().readUnquotedString();

                argumentparserselector.addPredicate((entity) -> {
                    return "".equals(s) ? entity.getTags().isEmpty() != flag : entity.getTags().contains(s) != flag;
                });
            }, (argumentparserselector) -> {
                return true;
            }, new ChatMessage("argument.entity.options.tag.description"));
            register("nbt", (argumentparserselector) -> {
                boolean flag = argumentparserselector.shouldInvertValue();
                NBTTagCompound nbttagcompound = (new MojangsonParser(argumentparserselector.getReader())).readStruct();

                argumentparserselector.addPredicate((entity) -> {
                    NBTTagCompound nbttagcompound1 = entity.saveWithoutId(new NBTTagCompound());

                    if (entity instanceof EntityPlayer) {
                        ItemStack itemstack = ((EntityPlayer) entity).getInventory().getSelected();

                        if (!itemstack.isEmpty()) {
                            nbttagcompound1.put("SelectedItem", itemstack.save(new NBTTagCompound()));
                        }
                    }

                    return GameProfileSerializer.compareNbt(nbttagcompound, nbttagcompound1, true) != flag;
                });
            }, (argumentparserselector) -> {
                return true;
            }, new ChatMessage("argument.entity.options.nbt.description"));
            register("scores", (argumentparserselector) -> {
                StringReader stringreader = argumentparserselector.getReader();
                Map<String, CriterionConditionValue.IntegerRange> map = Maps.newHashMap();

                stringreader.expect('{');
                stringreader.skipWhitespace();

                while (stringreader.canRead() && stringreader.peek() != '}') {
                    stringreader.skipWhitespace();
                    String s = stringreader.readUnquotedString();

                    stringreader.skipWhitespace();
                    stringreader.expect('=');
                    stringreader.skipWhitespace();
                    CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromReader(stringreader);

                    map.put(s, criterionconditionvalue_integerrange);
                    stringreader.skipWhitespace();
                    if (stringreader.canRead() && stringreader.peek() == ',') {
                        stringreader.skip();
                    }
                }

                stringreader.expect('}');
                if (!map.isEmpty()) {
                    argumentparserselector.addPredicate((entity) -> {
                        ScoreboardServer scoreboardserver = entity.getServer().getScoreboard();
                        String s1 = entity.getScoreboardName();
                        Iterator iterator = map.entrySet().iterator();

                        Entry entry;
                        int i;

                        do {
                            if (!iterator.hasNext()) {
                                return true;
                            }

                            entry = (Entry) iterator.next();
                            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjective((String) entry.getKey());

                            if (scoreboardobjective == null) {
                                return false;
                            }

                            if (!scoreboardserver.hasPlayerScore(s1, scoreboardobjective)) {
                                return false;
                            }

                            ScoreboardScore scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s1, scoreboardobjective);

                            i = scoreboardscore.getScore();
                        } while (((CriterionConditionValue.IntegerRange) entry.getValue()).matches(i));

                        return false;
                    });
                }

                argumentparserselector.setHasScores(true);
            }, (argumentparserselector) -> {
                return !argumentparserselector.hasScores();
            }, new ChatMessage("argument.entity.options.scores.description"));
            register("advancements", (argumentparserselector) -> {
                StringReader stringreader = argumentparserselector.getReader();
                Map<MinecraftKey, Predicate<AdvancementProgress>> map = Maps.newHashMap();

                stringreader.expect('{');
                stringreader.skipWhitespace();

                while (stringreader.canRead() && stringreader.peek() != '}') {
                    stringreader.skipWhitespace();
                    MinecraftKey minecraftkey = MinecraftKey.read(stringreader);

                    stringreader.skipWhitespace();
                    stringreader.expect('=');
                    stringreader.skipWhitespace();
                    if (stringreader.canRead() && stringreader.peek() == '{') {
                        Map<String, Predicate<CriterionProgress>> map1 = Maps.newHashMap();

                        stringreader.skipWhitespace();
                        stringreader.expect('{');
                        stringreader.skipWhitespace();

                        while (stringreader.canRead() && stringreader.peek() != '}') {
                            stringreader.skipWhitespace();
                            String s = stringreader.readUnquotedString();

                            stringreader.skipWhitespace();
                            stringreader.expect('=');
                            stringreader.skipWhitespace();
                            boolean flag = stringreader.readBoolean();

                            map1.put(s, (criterionprogress) -> {
                                return criterionprogress.isDone() == flag;
                            });
                            stringreader.skipWhitespace();
                            if (stringreader.canRead() && stringreader.peek() == ',') {
                                stringreader.skip();
                            }
                        }

                        stringreader.skipWhitespace();
                        stringreader.expect('}');
                        stringreader.skipWhitespace();
                        map.put(minecraftkey, (advancementprogress) -> {
                            Iterator iterator = map1.entrySet().iterator();

                            Entry entry;
                            CriterionProgress criterionprogress;

                            do {
                                if (!iterator.hasNext()) {
                                    return true;
                                }

                                entry = (Entry) iterator.next();
                                criterionprogress = advancementprogress.getCriterion((String) entry.getKey());
                            } while (criterionprogress != null && ((Predicate) entry.getValue()).test(criterionprogress));

                            return false;
                        });
                    } else {
                        boolean flag1 = stringreader.readBoolean();

                        map.put(minecraftkey, (advancementprogress) -> {
                            return advancementprogress.isDone() == flag1;
                        });
                    }

                    stringreader.skipWhitespace();
                    if (stringreader.canRead() && stringreader.peek() == ',') {
                        stringreader.skip();
                    }
                }

                stringreader.expect('}');
                if (!map.isEmpty()) {
                    argumentparserselector.addPredicate((entity) -> {
                        if (!(entity instanceof EntityPlayer)) {
                            return false;
                        } else {
                            EntityPlayer entityplayer = (EntityPlayer) entity;
                            AdvancementDataPlayer advancementdataplayer = entityplayer.getAdvancements();
                            AdvancementDataWorld advancementdataworld = entityplayer.getServer().getAdvancements();
                            Iterator iterator = map.entrySet().iterator();

                            Entry entry;
                            Advancement advancement;

                            do {
                                if (!iterator.hasNext()) {
                                    return true;
                                }

                                entry = (Entry) iterator.next();
                                advancement = advancementdataworld.getAdvancement((MinecraftKey) entry.getKey());
                            } while (advancement != null && ((Predicate) entry.getValue()).test(advancementdataplayer.getOrStartProgress(advancement)));

                            return false;
                        }
                    });
                    argumentparserselector.setIncludesEntities(false);
                }

                argumentparserselector.setHasAdvancements(true);
            }, (argumentparserselector) -> {
                return !argumentparserselector.hasAdvancements();
            }, new ChatMessage("argument.entity.options.advancements.description"));
            register("predicate", (argumentparserselector) -> {
                boolean flag = argumentparserselector.shouldInvertValue();
                MinecraftKey minecraftkey = MinecraftKey.read(argumentparserselector.getReader());

                argumentparserselector.addPredicate((entity) -> {
                    if (!(entity.level instanceof WorldServer)) {
                        return false;
                    } else {
                        WorldServer worldserver = (WorldServer) entity.level;
                        LootItemCondition lootitemcondition = worldserver.getServer().getPredicateManager().get(minecraftkey);

                        if (lootitemcondition == null) {
                            return false;
                        } else {
                            LootTableInfo loottableinfo = (new LootTableInfo.Builder(worldserver)).withParameter(LootContextParameters.THIS_ENTITY, entity).withParameter(LootContextParameters.ORIGIN, entity.position()).create(LootContextParameterSets.SELECTOR);

                            return flag ^ lootitemcondition.test(loottableinfo);
                        }
                    }
                });
            }, (argumentparserselector) -> {
                return true;
            }, new ChatMessage("argument.entity.options.predicate.description"));
        }
    }

    public static PlayerSelector.a get(ArgumentParserSelector argumentparserselector, String s, int i) throws CommandSyntaxException {
        PlayerSelector.b playerselector_b = (PlayerSelector.b) PlayerSelector.OPTIONS.get(s);

        if (playerselector_b != null) {
            if (playerselector_b.predicate.test(argumentparserselector)) {
                return playerselector_b.modifier;
            } else {
                throw PlayerSelector.ERROR_INAPPLICABLE_OPTION.createWithContext(argumentparserselector.getReader(), s);
            }
        } else {
            argumentparserselector.getReader().setCursor(i);
            throw PlayerSelector.ERROR_UNKNOWN_OPTION.createWithContext(argumentparserselector.getReader(), s);
        }
    }

    public static void suggestNames(ArgumentParserSelector argumentparserselector, SuggestionsBuilder suggestionsbuilder) {
        String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);
        Iterator iterator = PlayerSelector.OPTIONS.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, PlayerSelector.b> entry = (Entry) iterator.next();

            if (((PlayerSelector.b) entry.getValue()).predicate.test(argumentparserselector) && ((String) entry.getKey()).toLowerCase(Locale.ROOT).startsWith(s)) {
                suggestionsbuilder.suggest((String) entry.getKey() + "=", ((PlayerSelector.b) entry.getValue()).description);
            }
        }

    }

    private static class b {

        public final PlayerSelector.a modifier;
        public final Predicate<ArgumentParserSelector> predicate;
        public final IChatBaseComponent description;

        b(PlayerSelector.a playerselector_a, Predicate<ArgumentParserSelector> predicate, IChatBaseComponent ichatbasecomponent) {
            this.modifier = playerselector_a;
            this.predicate = predicate;
            this.description = ichatbasecomponent;
        }
    }

    public interface a {

        void handle(ArgumentParserSelector argumentparserselector) throws CommandSyntaxException;
    }
}
