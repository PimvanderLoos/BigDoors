package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class PlayerSelector {

    private static final Map<String, PlayerSelector.b> i = Maps.newHashMap();
    public static final DynamicCommandExceptionType a = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.unknown", new Object[] { object});
    });
    public static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.inapplicable", new Object[] { object});
    });
    public static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("argument.entity.options.distance.negative", new Object[0]));
    public static final SimpleCommandExceptionType d = new SimpleCommandExceptionType(new ChatMessage("argument.entity.options.level.negative", new Object[0]));
    public static final SimpleCommandExceptionType e = new SimpleCommandExceptionType(new ChatMessage("argument.entity.options.limit.toosmall", new Object[0]));
    public static final DynamicCommandExceptionType f = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.sort.irreversible", new Object[] { object});
    });
    public static final DynamicCommandExceptionType g = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.mode.invalid", new Object[] { object});
    });
    public static final DynamicCommandExceptionType h = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.type.invalid", new Object[] { object});
    });

    private static void a(String s, PlayerSelector.a playerselector_a, Predicate<ArgumentParserSelector> predicate, IChatBaseComponent ichatbasecomponent) {
        PlayerSelector.i.put(s, new PlayerSelector.b(playerselector_a, predicate, ichatbasecomponent, null));
    }

    public static void a() {
        if (PlayerSelector.i.isEmpty()) {
            a("name", (argumentparserselector) -> {
                int i = argumentparserselector.f().getCursor();
                boolean flag = argumentparserselector.e();
                String s = argumentparserselector.f().readString();

                if (argumentparserselector.v() && !flag) {
                    argumentparserselector.f().setCursor(i);
                    throw PlayerSelector.b.createWithContext(argumentparserselector.f(), "name");
                } else {
                    if (flag) {
                        argumentparserselector.d(true);
                    } else {
                        argumentparserselector.c(true);
                    }

                    argumentparserselector.a((entity) -> {
                        return entity.getDisplayName().getText().equals(s) != flag;
                    });
                }
            }, (argumentparserselector) -> {
                return !argumentparserselector.u();
            }, new ChatMessage("argument.entity.options.name.description", new Object[0]));
            a("distance", (argumentparserselector) -> {
                int i = argumentparserselector.f().getCursor();
                CriterionConditionValue.c criterionconditionvalue_c = CriterionConditionValue.c.a(argumentparserselector.f());

                if ((criterionconditionvalue_c.a() == null || ((Float) criterionconditionvalue_c.a()).floatValue() >= 0.0F) && (criterionconditionvalue_c.b() == null || ((Float) criterionconditionvalue_c.b()).floatValue() >= 0.0F)) {
                    argumentparserselector.a(criterionconditionvalue_c);
                    argumentparserselector.g();
                } else {
                    argumentparserselector.f().setCursor(i);
                    throw PlayerSelector.c.createWithContext(argumentparserselector.f());
                }
            }, (argumentparserselector) -> {
                return argumentparserselector.h().c();
            }, new ChatMessage("argument.entity.options.distance.description", new Object[0]));
            a("level", (argumentparserselector) -> {
                int i = argumentparserselector.f().getCursor();
                CriterionConditionValue.d criterionconditionvalue_d = CriterionConditionValue.d.a(argumentparserselector.f());

                if ((criterionconditionvalue_d.a() == null || ((Integer) criterionconditionvalue_d.a()).intValue() >= 0) && (criterionconditionvalue_d.b() == null || ((Integer) criterionconditionvalue_d.b()).intValue() >= 0)) {
                    argumentparserselector.a(criterionconditionvalue_d);
                    argumentparserselector.a(false);
                } else {
                    argumentparserselector.f().setCursor(i);
                    throw PlayerSelector.d.createWithContext(argumentparserselector.f());
                }
            }, (argumentparserselector) -> {
                return argumentparserselector.i().c();
            }, new ChatMessage("argument.entity.options.level.description", new Object[0]));
            a("x", (argumentparserselector) -> {
                argumentparserselector.g();
                argumentparserselector.a(argumentparserselector.f().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.l() == null;
            }, new ChatMessage("argument.entity.options.x.description", new Object[0]));
            a("y", (argumentparserselector) -> {
                argumentparserselector.g();
                argumentparserselector.b(argumentparserselector.f().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.m() == null;
            }, new ChatMessage("argument.entity.options.y.description", new Object[0]));
            a("z", (argumentparserselector) -> {
                argumentparserselector.g();
                argumentparserselector.c(argumentparserselector.f().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.n() == null;
            }, new ChatMessage("argument.entity.options.z.description", new Object[0]));
            a("dx", (argumentparserselector) -> {
                argumentparserselector.g();
                argumentparserselector.d(argumentparserselector.f().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.o() == null;
            }, new ChatMessage("argument.entity.options.dx.description", new Object[0]));
            a("dy", (argumentparserselector) -> {
                argumentparserselector.g();
                argumentparserselector.e(argumentparserselector.f().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.p() == null;
            }, new ChatMessage("argument.entity.options.dy.description", new Object[0]));
            a("dz", (argumentparserselector) -> {
                argumentparserselector.g();
                argumentparserselector.f(argumentparserselector.f().readDouble());
            }, (argumentparserselector) -> {
                return argumentparserselector.q() == null;
            }, new ChatMessage("argument.entity.options.dz.description", new Object[0]));
            a("x_rotation", (argumentparserselector) -> {
                argumentparserselector.a(CriterionConditionRange.a(argumentparserselector.f(), true, MathHelper::g));
            }, (argumentparserselector) -> {
                return argumentparserselector.j() == CriterionConditionRange.a;
            }, new ChatMessage("argument.entity.options.x_rotation.description", new Object[0]));
            a("y_rotation", (argumentparserselector) -> {
                argumentparserselector.b(CriterionConditionRange.a(argumentparserselector.f(), true, MathHelper::g));
            }, (argumentparserselector) -> {
                return argumentparserselector.k() == CriterionConditionRange.a;
            }, new ChatMessage("argument.entity.options.y_rotation.description", new Object[0]));
            a("limit", (argumentparserselector) -> {
                int i = argumentparserselector.f().getCursor();
                int j = argumentparserselector.f().readInt();

                if (j < 1) {
                    argumentparserselector.f().setCursor(i);
                    throw PlayerSelector.e.createWithContext(argumentparserselector.f());
                } else {
                    argumentparserselector.a(j);
                    argumentparserselector.e(true);
                }
            }, (argumentparserselector) -> {
                return !argumentparserselector.t() && !argumentparserselector.w();
            }, new ChatMessage("argument.entity.options.limit.description", new Object[0]));
            a("sort", (argumentparserselector) -> {
                int i = argumentparserselector.f().getCursor();
                String s = argumentparserselector.f().readUnquotedString();

                argumentparserselector.a((suggestionsbuilder, consumer) -> {
                    return ICompletionProvider.b(Arrays.asList(new String[] { "nearest", "furthest", "random", "arbitrary"}), suggestionsbuilder);
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
                    biconsumer = ArgumentParserSelector.h;
                    break;

                case 1:
                    biconsumer = ArgumentParserSelector.i;
                    break;

                case 2:
                    biconsumer = ArgumentParserSelector.j;
                    break;

                case 3:
                    biconsumer = ArgumentParserSelector.g;
                    break;

                default:
                    argumentparserselector.f().setCursor(i);
                    throw PlayerSelector.f.createWithContext(argumentparserselector.f(), s);
                }

                argumentparserselector.a(biconsumer);
                argumentparserselector.f(true);
            }, (argumentparserselector) -> {
                return !argumentparserselector.t() && !argumentparserselector.x();
            }, new ChatMessage("argument.entity.options.sort.description", new Object[0]));
            a("gamemode", (argumentparserselector) -> {
                argumentparserselector.a((suggestionsbuilder, consumer) -> {
                    String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);
                    boolean flag = !argumentparserselector.z();
                    boolean flag1 = true;

                    if (!s.isEmpty()) {
                        if (s.charAt(0) == 33) {
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

                        if (enumgamemode != EnumGamemode.NOT_SET && enumgamemode.b().toLowerCase(Locale.ROOT).startsWith(s)) {
                            if (flag1) {
                                suggestionsbuilder.suggest('!' + enumgamemode.b());
                            }

                            if (flag) {
                                suggestionsbuilder.suggest(enumgamemode.b());
                            }
                        }
                    }

                    return suggestionsbuilder.buildFuture();
                });
                int i = argumentparserselector.f().getCursor();
                boolean flag = argumentparserselector.e();

                if (argumentparserselector.z() && !flag) {
                    argumentparserselector.f().setCursor(i);
                    throw PlayerSelector.b.createWithContext(argumentparserselector.f(), "gamemode");
                } else {
                    String s = argumentparserselector.f().readUnquotedString();
                    EnumGamemode enumgamemode = EnumGamemode.a(s, EnumGamemode.NOT_SET);

                    if (enumgamemode == EnumGamemode.NOT_SET) {
                        argumentparserselector.f().setCursor(i);
                        throw PlayerSelector.g.createWithContext(argumentparserselector.f(), s);
                    } else {
                        argumentparserselector.a(false);
                        argumentparserselector.a((entity) -> {
                            if (!(entity instanceof EntityPlayer)) {
                                return false;
                            } else {
                                EnumGamemode enumgamemode = ((EntityPlayer) entity).playerInteractManager.getGameMode();

                                return flag ? enumgamemode != enumgamemode1 : enumgamemode == enumgamemode1;
                            }
                        });
                        if (flag) {
                            argumentparserselector.h(true);
                        } else {
                            argumentparserselector.g(true);
                        }

                    }
                }
            }, (argumentparserselector) -> {
                return !argumentparserselector.y();
            }, new ChatMessage("argument.entity.options.gamemode.description", new Object[0]));
            a("team", (argumentparserselector) -> {
                boolean flag = argumentparserselector.e();
                String s = argumentparserselector.f().readUnquotedString();

                argumentparserselector.a((entity) -> {
                    if (!(entity instanceof EntityLiving)) {
                        return false;
                    } else {
                        ScoreboardTeamBase scoreboardteambase = entity.be();
                        String s = scoreboardteambase == null ? "" : scoreboardteambase.getName();

                        return s.equals(s1) != flag;
                    }
                });
                if (flag) {
                    argumentparserselector.j(true);
                } else {
                    argumentparserselector.i(true);
                }

            }, (argumentparserselector) -> {
                return !argumentparserselector.A();
            }, new ChatMessage("argument.entity.options.team.description", new Object[0]));
            a("type", (argumentparserselector) -> {
                argumentparserselector.a((suggestionsbuilder, consumer) -> {
                    ICompletionProvider.a((Iterable) EntityTypes.REGISTRY.keySet(), suggestionsbuilder, String.valueOf('!'));
                    if (!argumentparserselector.F()) {
                        ICompletionProvider.a((Iterable) EntityTypes.REGISTRY.keySet(), suggestionsbuilder);
                    }

                    return suggestionsbuilder.buildFuture();
                });
                int i = argumentparserselector.f().getCursor();
                boolean flag = argumentparserselector.e();

                if (argumentparserselector.F() && !flag) {
                    argumentparserselector.f().setCursor(i);
                    throw PlayerSelector.b.createWithContext(argumentparserselector.f(), "type");
                } else {
                    MinecraftKey minecraftkey = MinecraftKey.a(argumentparserselector.f());
                    EntityTypes entitytypes = (EntityTypes) EntityTypes.REGISTRY.get(minecraftkey);

                    if (entitytypes == null) {
                        argumentparserselector.f().setCursor(i);
                        throw PlayerSelector.h.createWithContext(argumentparserselector.f(), minecraftkey.toString());
                    } else {
                        if (Objects.equals(EntityTypes.PLAYER, entitytypes) && !flag) {
                            argumentparserselector.a(false);
                        }

                        argumentparserselector.a((entity) -> {
                            return Objects.equals(entitytypes, entity.P()) != flag;
                        });
                        if (flag) {
                            argumentparserselector.C();
                        } else {
                            argumentparserselector.a(entitytypes.c());
                        }

                    }
                }
            }, (argumentparserselector) -> {
                return !argumentparserselector.E();
            }, new ChatMessage("argument.entity.options.type.description", new Object[0]));
            a("tag", (argumentparserselector) -> {
                boolean flag = argumentparserselector.e();
                String s = argumentparserselector.f().readUnquotedString();

                argumentparserselector.a((entity) -> {
                    return "".equals(s) ? entity.getScoreboardTags().isEmpty() != flag : entity.getScoreboardTags().contains(s) != flag;
                });
            }, (argumentparserselector) -> {
                return true;
            }, new ChatMessage("argument.entity.options.tag.description", new Object[0]));
            a("nbt", (argumentparserselector) -> {
                boolean flag = argumentparserselector.e();
                NBTTagCompound nbttagcompound = (new MojangsonParser(argumentparserselector.f())).f();

                argumentparserselector.a((entity) -> {
                    NBTTagCompound nbttagcompound = entity.save(new NBTTagCompound());

                    if (entity instanceof EntityPlayer) {
                        ItemStack itemstack = ((EntityPlayer) entity).inventory.getItemInHand();

                        if (!itemstack.isEmpty()) {
                            nbttagcompound.set("SelectedItem", itemstack.save(new NBTTagCompound()));
                        }
                    }

                    return GameProfileSerializer.a(nbttagcompound1, nbttagcompound, true) != flag;
                });
            }, (argumentparserselector) -> {
                return true;
            }, new ChatMessage("argument.entity.options.nbt.description", new Object[0]));
            a("scores", (argumentparserselector) -> {
                StringReader stringreader = argumentparserselector.f();
                HashMap hashmap = Maps.newHashMap();

                stringreader.expect('{');
                stringreader.skipWhitespace();

                while (stringreader.canRead() && stringreader.peek() != 125) {
                    stringreader.skipWhitespace();
                    String s = stringreader.readUnquotedString();

                    stringreader.skipWhitespace();
                    stringreader.expect('=');
                    stringreader.skipWhitespace();
                    CriterionConditionValue.d criterionconditionvalue_d = CriterionConditionValue.d.a(stringreader);

                    hashmap.put(s, criterionconditionvalue_d);
                    stringreader.skipWhitespace();
                    if (stringreader.canRead() && stringreader.peek() == 44) {
                        stringreader.skip();
                    }
                }

                stringreader.expect('}');
                if (!hashmap.isEmpty()) {
                    argumentparserselector.a((entity) -> {
                        ScoreboardServer scoreboardserver = entity.bK().getScoreboard();
                        String s = entity.getName();
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

                            if (!scoreboardserver.b(s, scoreboardobjective)) {
                                return false;
                            }

                            ScoreboardScore scoreboardscore = scoreboardserver.getPlayerScoreForObjective(s, scoreboardobjective);

                            i = scoreboardscore.getScore();
                        } while (((CriterionConditionValue.d) entry.getValue()).d(i));

                        return false;
                    });
                }

                argumentparserselector.k(true);
            }, (argumentparserselector) -> {
                return !argumentparserselector.G();
            }, new ChatMessage("argument.entity.options.scores.description", new Object[0]));
            a("advancements", (argumentparserselector) -> {
                StringReader stringreader = argumentparserselector.f();
                HashMap hashmap = Maps.newHashMap();

                stringreader.expect('{');
                stringreader.skipWhitespace();

                while (stringreader.canRead() && stringreader.peek() != 125) {
                    stringreader.skipWhitespace();
                    MinecraftKey minecraftkey = MinecraftKey.a(stringreader);

                    stringreader.skipWhitespace();
                    stringreader.expect('=');
                    stringreader.skipWhitespace();
                    if (stringreader.canRead() && stringreader.peek() == 123) {
                        HashMap hashmap1 = Maps.newHashMap();

                        stringreader.skipWhitespace();
                        stringreader.expect('{');
                        stringreader.skipWhitespace();

                        while (stringreader.canRead() && stringreader.peek() != 125) {
                            stringreader.skipWhitespace();
                            String s = stringreader.readUnquotedString();

                            stringreader.skipWhitespace();
                            stringreader.expect('=');
                            stringreader.skipWhitespace();
                            boolean flag = stringreader.readBoolean();

                            hashmap1.put(s, (criterionprogress) -> {
                                return criterionprogress.a() == flag;
                            });
                            stringreader.skipWhitespace();
                            if (stringreader.canRead() && stringreader.peek() == 44) {
                                stringreader.skip();
                            }
                        }

                        stringreader.skipWhitespace();
                        stringreader.expect('}');
                        stringreader.skipWhitespace();
                        hashmap.put(minecraftkey, (advancementprogress) -> {
                            Iterator iterator = map.entrySet().iterator();

                            Entry entry;
                            CriterionProgress criterionprogress;

                            do {
                                if (!iterator.hasNext()) {
                                    return true;
                                }

                                entry = (Entry) iterator.next();
                                criterionprogress = advancementprogress.getCriterionProgress((String) entry.getKey());
                            } while (criterionprogress != null && ((Predicate) entry.getValue()).test(criterionprogress));

                            return false;
                        });
                    } else {
                        boolean flag1 = stringreader.readBoolean();

                        hashmap.put(minecraftkey, (advancementprogress) -> {
                            return advancementprogress.isDone() == flag;
                        });
                    }

                    stringreader.skipWhitespace();
                    if (stringreader.canRead() && stringreader.peek() == 44) {
                        stringreader.skip();
                    }
                }

                stringreader.expect('}');
                if (!hashmap.isEmpty()) {
                    argumentparserselector.a((entity) -> {
                        if (!(entity instanceof EntityPlayer)) {
                            return false;
                        } else {
                            EntityPlayer entityplayer = (EntityPlayer) entity;
                            AdvancementDataPlayer advancementdataplayer = entityplayer.getAdvancementData();
                            AdvancementDataWorld advancementdataworld = entityplayer.bK().getAdvancementData();
                            Iterator iterator = map.entrySet().iterator();

                            Entry entry;
                            Advancement advancement;

                            do {
                                if (!iterator.hasNext()) {
                                    return true;
                                }

                                entry = (Entry) iterator.next();
                                advancement = advancementdataworld.a((MinecraftKey) entry.getKey());
                            } while (advancement != null && ((Predicate) entry.getValue()).test(advancementdataplayer.getProgress(advancement)));

                            return false;
                        }
                    });
                    argumentparserselector.a(false);
                }

                argumentparserselector.l(true);
            }, (argumentparserselector) -> {
                return !argumentparserselector.H();
            }, new ChatMessage("argument.entity.options.advancements.description", new Object[0]));
        }
    }

    public static PlayerSelector.a a(ArgumentParserSelector argumentparserselector, String s, int i) throws CommandSyntaxException {
        PlayerSelector.b playerselector_b = (PlayerSelector.b) PlayerSelector.i.get(s);

        if (playerselector_b != null) {
            if (playerselector_b.b.test(argumentparserselector)) {
                return playerselector_b.a;
            } else {
                throw PlayerSelector.b.createWithContext(argumentparserselector.f(), s);
            }
        } else {
            argumentparserselector.f().setCursor(i);
            throw PlayerSelector.a.createWithContext(argumentparserselector.f(), s);
        }
    }

    public static void a(ArgumentParserSelector argumentparserselector, SuggestionsBuilder suggestionsbuilder) {
        String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);
        Iterator iterator = PlayerSelector.i.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((PlayerSelector.b) entry.getValue()).b.test(argumentparserselector) && ((String) entry.getKey()).toLowerCase(Locale.ROOT).startsWith(s)) {
                suggestionsbuilder.suggest((String) entry.getKey() + '=', ((PlayerSelector.b) entry.getValue()).c);
            }
        }

    }

    static class b {

        public final PlayerSelector.a a;
        public final Predicate<ArgumentParserSelector> b;
        public final IChatBaseComponent c;

        private b(PlayerSelector.a playerselector_a, Predicate<ArgumentParserSelector> predicate, IChatBaseComponent ichatbasecomponent) {
            this.a = playerselector_a;
            this.b = predicate;
            this.c = ichatbasecomponent;
        }

        b(PlayerSelector.a playerselector_a, Predicate predicate, IChatBaseComponent ichatbasecomponent, Object object) {
            this(playerselector_a, predicate, ichatbasecomponent);
        }
    }

    public interface a {

        void handle(ArgumentParserSelector argumentparserselector) throws CommandSyntaxException;
    }
}
