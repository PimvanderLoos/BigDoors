package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.advancements.critereon.CriterionConditionValue;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentAnchor;
import net.minecraft.commands.arguments.ArgumentCriterionValue;
import net.minecraft.commands.arguments.ArgumentDimension;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.commands.arguments.ArgumentScoreboardObjective;
import net.minecraft.commands.arguments.ArgumentScoreholder;
import net.minecraft.commands.arguments.blocks.ArgumentBlockPredicate;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.commands.arguments.coordinates.ArgumentRotation;
import net.minecraft.commands.arguments.coordinates.ArgumentRotationAxis;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.bossevents.BossBattleCustom;
import net.minecraft.server.commands.data.CommandData;
import net.minecraft.server.commands.data.CommandDataAccessor;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.storage.loot.LootPredicateManager;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;

public class CommandExecute {

    private static final Dynamic2CommandExceptionType a = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.execute.blocks.toobig", new Object[]{object, object1});
    });
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.execute.conditional.fail"));
    private static final DynamicCommandExceptionType c = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.execute.conditional.fail_count", new Object[]{object});
    });
    private static final BinaryOperator<ResultConsumer<CommandListenerWrapper>> d = (resultconsumer, resultconsumer1) -> {
        return (commandcontext, flag, i) -> {
            resultconsumer.onCommandComplete(commandcontext, flag, i);
            resultconsumer1.onCommandComplete(commandcontext, flag, i);
        };
    };
    private static final SuggestionProvider<CommandListenerWrapper> e = (commandcontext, suggestionsbuilder) -> {
        LootPredicateManager lootpredicatemanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getLootPredicateManager();

        return ICompletionProvider.a((Iterable) lootpredicatemanager.a(), suggestionsbuilder);
    };

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("execute").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        }));

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("execute").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("run").redirect(commanddispatcher.getRoot()))).then(a((CommandNode) literalcommandnode, net.minecraft.commands.CommandDispatcher.a("if"), true))).then(a((CommandNode) literalcommandnode, net.minecraft.commands.CommandDispatcher.a("unless"), false))).then(net.minecraft.commands.CommandDispatcher.a("as").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            Iterator iterator = ArgumentEntity.c(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).a(entity));
            }

            return list;
        })))).then(net.minecraft.commands.CommandDispatcher.a("at").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            Iterator iterator = ArgumentEntity.c(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).a((WorldServer) entity.world).a(entity.getPositionVector()).a(entity.bi()));
            }

            return list;
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("store").then(a(literalcommandnode, net.minecraft.commands.CommandDispatcher.a("result"), true))).then(a(literalcommandnode, net.minecraft.commands.CommandDispatcher.a("success"), false)))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("positioned").then(net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentVec3.a()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).a(ArgumentVec3.a(commandcontext, "pos")).a(ArgumentAnchor.Anchor.FEET);
        }))).then(net.minecraft.commands.CommandDispatcher.a("as").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            Iterator iterator = ArgumentEntity.c(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).a(entity.getPositionVector()));
            }

            return list;
        }))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("rotated").then(net.minecraft.commands.CommandDispatcher.a("rot", (ArgumentType) ArgumentRotation.a()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).a(ArgumentRotation.a(commandcontext, "rot").b((CommandListenerWrapper) commandcontext.getSource()));
        }))).then(net.minecraft.commands.CommandDispatcher.a("as").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            Iterator iterator = ArgumentEntity.c(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).a(entity.bi()));
            }

            return list;
        }))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("facing").then(net.minecraft.commands.CommandDispatcher.a("entity").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).then(net.minecraft.commands.CommandDispatcher.a("anchor", (ArgumentType) ArgumentAnchor.a()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            ArgumentAnchor.Anchor argumentanchor_anchor = ArgumentAnchor.a(commandcontext, "anchor");
            Iterator iterator = ArgumentEntity.c(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).a(entity, argumentanchor_anchor));
            }

            return list;
        }))))).then(net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentVec3.a()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).b(ArgumentVec3.a(commandcontext, "pos"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("align").then(net.minecraft.commands.CommandDispatcher.a("axes", (ArgumentType) ArgumentRotationAxis.a()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).a(((CommandListenerWrapper) commandcontext.getSource()).getPosition().a(ArgumentRotationAxis.a(commandcontext, "axes")));
        })))).then(net.minecraft.commands.CommandDispatcher.a("anchored").then(net.minecraft.commands.CommandDispatcher.a("anchor", (ArgumentType) ArgumentAnchor.a()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).a(ArgumentAnchor.a(commandcontext, "anchor"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("in").then(net.minecraft.commands.CommandDispatcher.a("dimension", (ArgumentType) ArgumentDimension.a()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).a(ArgumentDimension.a(commandcontext, "dimension"));
        }))));
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> a(LiteralCommandNode<CommandListenerWrapper> literalcommandnode, LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder, boolean flag) {
        literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.a("score").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentScoreholder.b()).suggests(ArgumentScoreholder.a).then(net.minecraft.commands.CommandDispatcher.a("objective", (ArgumentType) ArgumentScoreboardObjective.a()).redirect(literalcommandnode, (commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.c(commandcontext, "targets"), ArgumentScoreboardObjective.a(commandcontext, "objective"), flag);
        }))));
        literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.a("bossbar").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("id", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandBossBar.a).then(net.minecraft.commands.CommandDispatcher.a("value").redirect(literalcommandnode, (commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), CommandBossBar.a(commandcontext), true, flag);
        }))).then(net.minecraft.commands.CommandDispatcher.a("max").redirect(literalcommandnode, (commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), CommandBossBar.a(commandcontext), false, flag);
        }))));
        Iterator iterator = CommandData.b.iterator();

        while (iterator.hasNext()) {
            CommandData.c commanddata_c = (CommandData.c) iterator.next();

            commanddata_c.a(literalargumentbuilder, (argumentbuilder) -> {
                return argumentbuilder.then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("path", (ArgumentType) ArgumentNBTKey.a()).then(net.minecraft.commands.CommandDispatcher.a("int").then(net.minecraft.commands.CommandDispatcher.a("scale", (ArgumentType) DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return a((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.a(commandcontext), ArgumentNBTKey.a(commandcontext, "path"), (i) -> {
                        return NBTTagInt.a((int) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale")));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.a("float").then(net.minecraft.commands.CommandDispatcher.a("scale", (ArgumentType) DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return a((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.a(commandcontext), ArgumentNBTKey.a(commandcontext, "path"), (i) -> {
                        return NBTTagFloat.a((float) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale")));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.a("short").then(net.minecraft.commands.CommandDispatcher.a("scale", (ArgumentType) DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return a((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.a(commandcontext), ArgumentNBTKey.a(commandcontext, "path"), (i) -> {
                        return NBTTagShort.a((short) ((int) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale"))));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.a("long").then(net.minecraft.commands.CommandDispatcher.a("scale", (ArgumentType) DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return a((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.a(commandcontext), ArgumentNBTKey.a(commandcontext, "path"), (i) -> {
                        return NBTTagLong.a((long) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale")));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.a("double").then(net.minecraft.commands.CommandDispatcher.a("scale", (ArgumentType) DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return a((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.a(commandcontext), ArgumentNBTKey.a(commandcontext, "path"), (i) -> {
                        return NBTTagDouble.a((double) i * DoubleArgumentType.getDouble(commandcontext, "scale"));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.a("byte").then(net.minecraft.commands.CommandDispatcher.a("scale", (ArgumentType) DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return a((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.a(commandcontext), ArgumentNBTKey.a(commandcontext, "path"), (i) -> {
                        return NBTTagByte.a((byte) ((int) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale"))));
                    }, flag);
                }))));
            });
        }

        return literalargumentbuilder;
    }

    private static CommandListenerWrapper a(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection, ScoreboardObjective scoreboardobjective, boolean flag) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();

        return commandlistenerwrapper.a((commandcontext, flag1, i) -> {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                ScoreboardScore scoreboardscore = scoreboardserver.getPlayerScoreForObjective(s, scoreboardobjective);
                int j = flag ? i : (flag1 ? 1 : 0);

                scoreboardscore.setScore(j);
            }

        }, CommandExecute.d);
    }

    private static CommandListenerWrapper a(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, boolean flag, boolean flag1) {
        return commandlistenerwrapper.a((commandcontext, flag2, i) -> {
            int j = flag1 ? i : (flag2 ? 1 : 0);

            if (flag) {
                bossbattlecustom.a(j);
            } else {
                bossbattlecustom.b(j);
            }

        }, CommandExecute.d);
    }

    private static CommandListenerWrapper a(CommandListenerWrapper commandlistenerwrapper, CommandDataAccessor commanddataaccessor, ArgumentNBTKey.h argumentnbtkey_h, IntFunction<NBTBase> intfunction, boolean flag) {
        return commandlistenerwrapper.a((commandcontext, flag1, i) -> {
            try {
                NBTTagCompound nbttagcompound = commanddataaccessor.a();
                int j = flag ? i : (flag1 ? 1 : 0);

                argumentnbtkey_h.b(nbttagcompound, () -> {
                    return (NBTBase) intfunction.apply(j);
                });
                commanddataaccessor.a(nbttagcompound);
            } catch (CommandSyntaxException commandsyntaxexception) {
                ;
            }

        }, CommandExecute.d);
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> a(CommandNode<CommandListenerWrapper> commandnode, LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder, boolean flag) {
        ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.a("block").then(net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentPosition.a()).then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("block", (ArgumentType) ArgumentBlockPredicate.a()), flag, (commandcontext) -> {
            return ArgumentBlockPredicate.a(commandcontext, "block").test(new ShapeDetectorBlock(((CommandListenerWrapper) commandcontext.getSource()).getWorld(), ArgumentPosition.a(commandcontext, "pos"), true));
        }))))).then(net.minecraft.commands.CommandDispatcher.a("score").then(net.minecraft.commands.CommandDispatcher.a("target", (ArgumentType) ArgumentScoreholder.a()).suggests(ArgumentScoreholder.a).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targetObjective", (ArgumentType) ArgumentScoreboardObjective.a()).then(net.minecraft.commands.CommandDispatcher.a("=").then(net.minecraft.commands.CommandDispatcher.a("source", (ArgumentType) ArgumentScoreholder.a()).suggests(ArgumentScoreholder.a).then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("sourceObjective", (ArgumentType) ArgumentScoreboardObjective.a()), flag, (commandcontext) -> {
            return a(commandcontext, Integer::equals);
        }))))).then(net.minecraft.commands.CommandDispatcher.a("<").then(net.minecraft.commands.CommandDispatcher.a("source", (ArgumentType) ArgumentScoreholder.a()).suggests(ArgumentScoreholder.a).then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("sourceObjective", (ArgumentType) ArgumentScoreboardObjective.a()), flag, (commandcontext) -> {
            return a(commandcontext, (integer, integer1) -> {
                return integer < integer1;
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.a("<=").then(net.minecraft.commands.CommandDispatcher.a("source", (ArgumentType) ArgumentScoreholder.a()).suggests(ArgumentScoreholder.a).then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("sourceObjective", (ArgumentType) ArgumentScoreboardObjective.a()), flag, (commandcontext) -> {
            return a(commandcontext, (integer, integer1) -> {
                return integer <= integer1;
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.a(">").then(net.minecraft.commands.CommandDispatcher.a("source", (ArgumentType) ArgumentScoreholder.a()).suggests(ArgumentScoreholder.a).then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("sourceObjective", (ArgumentType) ArgumentScoreboardObjective.a()), flag, (commandcontext) -> {
            return a(commandcontext, (integer, integer1) -> {
                return integer > integer1;
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.a(">=").then(net.minecraft.commands.CommandDispatcher.a("source", (ArgumentType) ArgumentScoreholder.a()).suggests(ArgumentScoreholder.a).then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("sourceObjective", (ArgumentType) ArgumentScoreboardObjective.a()), flag, (commandcontext) -> {
            return a(commandcontext, (integer, integer1) -> {
                return integer >= integer1;
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.a("matches").then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("range", (ArgumentType) ArgumentCriterionValue.a()), flag, (commandcontext) -> {
            return a(commandcontext, ArgumentCriterionValue.b.a(commandcontext, "range"));
        }))))))).then(net.minecraft.commands.CommandDispatcher.a("blocks").then(net.minecraft.commands.CommandDispatcher.a("start", (ArgumentType) ArgumentPosition.a()).then(net.minecraft.commands.CommandDispatcher.a("end", (ArgumentType) ArgumentPosition.a()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("destination", (ArgumentType) ArgumentPosition.a()).then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("all"), flag, false))).then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("masked"), flag, true))))))).then(net.minecraft.commands.CommandDispatcher.a("entity").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("entities", (ArgumentType) ArgumentEntity.multipleEntities()).fork(commandnode, (commandcontext) -> {
            return a(commandcontext, flag, !ArgumentEntity.c(commandcontext, "entities").isEmpty());
        })).executes(a(flag, (commandcontext) -> {
            return ArgumentEntity.c(commandcontext, "entities").size();
        }))))).then(net.minecraft.commands.CommandDispatcher.a("predicate").then(a(commandnode, (ArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("predicate", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandExecute.e), flag, (commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.c(commandcontext, "predicate"));
        })));
        Iterator iterator = CommandData.c.iterator();

        while (iterator.hasNext()) {
            CommandData.c commanddata_c = (CommandData.c) iterator.next();

            literalargumentbuilder.then(commanddata_c.a(net.minecraft.commands.CommandDispatcher.a("data"), (argumentbuilder) -> {
                return argumentbuilder.then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("path", (ArgumentType) ArgumentNBTKey.a()).fork(commandnode, (commandcontext) -> {
                    return a(commandcontext, flag, a(commanddata_c.a(commandcontext), ArgumentNBTKey.a(commandcontext, "path")) > 0);
                })).executes(a(flag, (commandcontext) -> {
                    return a(commanddata_c.a(commandcontext), ArgumentNBTKey.a(commandcontext, "path"));
                })));
            }));
        }

        return literalargumentbuilder;
    }

    private static Command<CommandListenerWrapper> a(boolean flag, CommandExecute.a commandexecute_a) {
        return flag ? (commandcontext) -> {
            int i = commandexecute_a.test(commandcontext);

            if (i > 0) {
                ((CommandListenerWrapper) commandcontext.getSource()).sendMessage(new ChatMessage("commands.execute.conditional.pass_count", new Object[]{i}), false);
                return i;
            } else {
                throw CommandExecute.b.create();
            }
        } : (commandcontext) -> {
            int i = commandexecute_a.test(commandcontext);

            if (i == 0) {
                ((CommandListenerWrapper) commandcontext.getSource()).sendMessage(new ChatMessage("commands.execute.conditional.pass"), false);
                return 1;
            } else {
                throw CommandExecute.c.create(i);
            }
        };
    }

    private static int a(CommandDataAccessor commanddataaccessor, ArgumentNBTKey.h argumentnbtkey_h) throws CommandSyntaxException {
        return argumentnbtkey_h.b(commanddataaccessor.a());
    }

    private static boolean a(CommandContext<CommandListenerWrapper> commandcontext, BiPredicate<Integer, Integer> bipredicate) throws CommandSyntaxException {
        String s = ArgumentScoreholder.a(commandcontext, "target");
        ScoreboardObjective scoreboardobjective = ArgumentScoreboardObjective.a(commandcontext, "targetObjective");
        String s1 = ArgumentScoreholder.a(commandcontext, "source");
        ScoreboardObjective scoreboardobjective1 = ArgumentScoreboardObjective.a(commandcontext, "sourceObjective");
        ScoreboardServer scoreboardserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getScoreboard();

        if (scoreboardserver.b(s, scoreboardobjective) && scoreboardserver.b(s1, scoreboardobjective1)) {
            ScoreboardScore scoreboardscore = scoreboardserver.getPlayerScoreForObjective(s, scoreboardobjective);
            ScoreboardScore scoreboardscore1 = scoreboardserver.getPlayerScoreForObjective(s1, scoreboardobjective1);

            return bipredicate.test(scoreboardscore.getScore(), scoreboardscore1.getScore());
        } else {
            return false;
        }
    }

    private static boolean a(CommandContext<CommandListenerWrapper> commandcontext, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) throws CommandSyntaxException {
        String s = ArgumentScoreholder.a(commandcontext, "target");
        ScoreboardObjective scoreboardobjective = ArgumentScoreboardObjective.a(commandcontext, "targetObjective");
        ScoreboardServer scoreboardserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getScoreboard();

        return !scoreboardserver.b(s, scoreboardobjective) ? false : criterionconditionvalue_integerrange.d(scoreboardserver.getPlayerScoreForObjective(s, scoreboardobjective).getScore());
    }

    private static boolean a(CommandListenerWrapper commandlistenerwrapper, LootItemCondition lootitemcondition) {
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder(worldserver)).set(LootContextParameters.ORIGIN, commandlistenerwrapper.getPosition()).setOptional(LootContextParameters.THIS_ENTITY, commandlistenerwrapper.getEntity());

        return lootitemcondition.test(loottableinfo_builder.build(LootContextParameterSets.COMMAND));
    }

    private static Collection<CommandListenerWrapper> a(CommandContext<CommandListenerWrapper> commandcontext, boolean flag, boolean flag1) {
        return (Collection) (flag1 == flag ? Collections.singleton(commandcontext.getSource()) : Collections.emptyList());
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> a(CommandNode<CommandListenerWrapper> commandnode, ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, boolean flag, CommandExecute.b commandexecute_b) {
        return argumentbuilder.fork(commandnode, (commandcontext) -> {
            return a(commandcontext, flag, commandexecute_b.test(commandcontext));
        }).executes((commandcontext) -> {
            if (flag == commandexecute_b.test(commandcontext)) {
                ((CommandListenerWrapper) commandcontext.getSource()).sendMessage(new ChatMessage("commands.execute.conditional.pass"), false);
                return 1;
            } else {
                throw CommandExecute.b.create();
            }
        });
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> a(CommandNode<CommandListenerWrapper> commandnode, ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, boolean flag, boolean flag1) {
        return argumentbuilder.fork(commandnode, (commandcontext) -> {
            return a(commandcontext, flag, c(commandcontext, flag1).isPresent());
        }).executes(flag ? (commandcontext) -> {
            return a(commandcontext, flag1);
        } : (commandcontext) -> {
            return b(commandcontext, flag1);
        });
    }

    private static int a(CommandContext<CommandListenerWrapper> commandcontext, boolean flag) throws CommandSyntaxException {
        OptionalInt optionalint = c(commandcontext, flag);

        if (optionalint.isPresent()) {
            ((CommandListenerWrapper) commandcontext.getSource()).sendMessage(new ChatMessage("commands.execute.conditional.pass_count", new Object[]{optionalint.getAsInt()}), false);
            return optionalint.getAsInt();
        } else {
            throw CommandExecute.b.create();
        }
    }

    private static int b(CommandContext<CommandListenerWrapper> commandcontext, boolean flag) throws CommandSyntaxException {
        OptionalInt optionalint = c(commandcontext, flag);

        if (optionalint.isPresent()) {
            throw CommandExecute.c.create(optionalint.getAsInt());
        } else {
            ((CommandListenerWrapper) commandcontext.getSource()).sendMessage(new ChatMessage("commands.execute.conditional.pass"), false);
            return 1;
        }
    }

    private static OptionalInt c(CommandContext<CommandListenerWrapper> commandcontext, boolean flag) throws CommandSyntaxException {
        return a(((CommandListenerWrapper) commandcontext.getSource()).getWorld(), ArgumentPosition.a(commandcontext, "start"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), flag);
    }

    private static OptionalInt a(WorldServer worldserver, BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, boolean flag) throws CommandSyntaxException {
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(blockposition, blockposition1);
        StructureBoundingBox structureboundingbox1 = new StructureBoundingBox(blockposition2, blockposition2.a(structureboundingbox.c()));
        BlockPosition blockposition3 = new BlockPosition(structureboundingbox1.a - structureboundingbox.a, structureboundingbox1.b - structureboundingbox.b, structureboundingbox1.c - structureboundingbox.c);
        int i = structureboundingbox.d() * structureboundingbox.e() * structureboundingbox.f();

        if (i > 32768) {
            throw CommandExecute.a.create(32768, i);
        } else {
            int j = 0;

            for (int k = structureboundingbox.c; k <= structureboundingbox.f; ++k) {
                for (int l = structureboundingbox.b; l <= structureboundingbox.e; ++l) {
                    for (int i1 = structureboundingbox.a; i1 <= structureboundingbox.d; ++i1) {
                        BlockPosition blockposition4 = new BlockPosition(i1, l, k);
                        BlockPosition blockposition5 = blockposition4.a((BaseBlockPosition) blockposition3);
                        IBlockData iblockdata = worldserver.getType(blockposition4);

                        if (!flag || !iblockdata.a(Blocks.AIR)) {
                            if (iblockdata != worldserver.getType(blockposition5)) {
                                return OptionalInt.empty();
                            }

                            TileEntity tileentity = worldserver.getTileEntity(blockposition4);
                            TileEntity tileentity1 = worldserver.getTileEntity(blockposition5);

                            if (tileentity != null) {
                                if (tileentity1 == null) {
                                    return OptionalInt.empty();
                                }

                                NBTTagCompound nbttagcompound = tileentity.save(new NBTTagCompound());

                                nbttagcompound.remove("x");
                                nbttagcompound.remove("y");
                                nbttagcompound.remove("z");
                                NBTTagCompound nbttagcompound1 = tileentity1.save(new NBTTagCompound());

                                nbttagcompound1.remove("x");
                                nbttagcompound1.remove("y");
                                nbttagcompound1.remove("z");
                                if (!nbttagcompound.equals(nbttagcompound1)) {
                                    return OptionalInt.empty();
                                }
                            }

                            ++j;
                        }
                    }
                }
            }

            return OptionalInt.of(j);
        }
    }

    @FunctionalInterface
    interface a {

        int test(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;
    }

    @FunctionalInterface
    interface b {

        boolean test(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;
    }
}
