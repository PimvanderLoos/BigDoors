package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
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

    private static final int MAX_TEST_AREA = 32768;
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.execute.blocks.toobig", new Object[]{object, object1});
    });
    private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.execute.conditional.fail"));
    private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.execute.conditional.fail_count", new Object[]{object});
    });
    private static final BinaryOperator<ResultConsumer<CommandListenerWrapper>> CALLBACK_CHAINER = (resultconsumer, resultconsumer1) -> {
        return (commandcontext, flag, i) -> {
            resultconsumer.onCommandComplete(commandcontext, flag, i);
            resultconsumer1.onCommandComplete(commandcontext, flag, i);
        };
    };
    private static final SuggestionProvider<CommandListenerWrapper> SUGGEST_PREDICATE = (commandcontext, suggestionsbuilder) -> {
        LootPredicateManager lootpredicatemanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPredicateManager();

        return ICompletionProvider.suggestResource((Iterable) lootpredicatemanager.getKeys(), suggestionsbuilder);
    };

    public CommandExecute() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("execute").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        }));

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("execute").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("run").redirect(commanddispatcher.getRoot()))).then(addConditionals(literalcommandnode, net.minecraft.commands.CommandDispatcher.literal("if"), true))).then(addConditionals(literalcommandnode, net.minecraft.commands.CommandDispatcher.literal("unless"), false))).then(net.minecraft.commands.CommandDispatcher.literal("as").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            Iterator iterator = ArgumentEntity.getOptionalEntities(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).withEntity(entity));
            }

            return list;
        })))).then(net.minecraft.commands.CommandDispatcher.literal("at").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            Iterator iterator = ArgumentEntity.getOptionalEntities(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).withLevel((WorldServer) entity.level).withPosition(entity.position()).withRotation(entity.getRotationVector()));
            }

            return list;
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("store").then(wrapStores(literalcommandnode, net.minecraft.commands.CommandDispatcher.literal("result"), true))).then(wrapStores(literalcommandnode, net.minecraft.commands.CommandDispatcher.literal("success"), false)))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("positioned").then(net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentVec3.vec3()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).withPosition(ArgumentVec3.getVec3(commandcontext, "pos")).withAnchor(ArgumentAnchor.Anchor.FEET);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("as").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            Iterator iterator = ArgumentEntity.getOptionalEntities(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).withPosition(entity.position()));
            }

            return list;
        }))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("rotated").then(net.minecraft.commands.CommandDispatcher.argument("rot", ArgumentRotation.rotation()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).withRotation(ArgumentRotation.getRotation(commandcontext, "rot").getRotation((CommandListenerWrapper) commandcontext.getSource()));
        }))).then(net.minecraft.commands.CommandDispatcher.literal("as").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            Iterator iterator = ArgumentEntity.getOptionalEntities(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).withRotation(entity.getRotationVector()));
            }

            return list;
        }))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("facing").then(net.minecraft.commands.CommandDispatcher.literal("entity").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).then(net.minecraft.commands.CommandDispatcher.argument("anchor", ArgumentAnchor.anchor()).fork(literalcommandnode, (commandcontext) -> {
            List<CommandListenerWrapper> list = Lists.newArrayList();
            ArgumentAnchor.Anchor argumentanchor_anchor = ArgumentAnchor.getAnchor(commandcontext, "anchor");
            Iterator iterator = ArgumentEntity.getOptionalEntities(commandcontext, "targets").iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                list.add(((CommandListenerWrapper) commandcontext.getSource()).facing(entity, argumentanchor_anchor));
            }

            return list;
        }))))).then(net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentVec3.vec3()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).facing(ArgumentVec3.getVec3(commandcontext, "pos"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("align").then(net.minecraft.commands.CommandDispatcher.argument("axes", ArgumentRotationAxis.swizzle()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).withPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition().align(ArgumentRotationAxis.getSwizzle(commandcontext, "axes")));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("anchored").then(net.minecraft.commands.CommandDispatcher.argument("anchor", ArgumentAnchor.anchor()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).withAnchor(ArgumentAnchor.getAnchor(commandcontext, "anchor"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("in").then(net.minecraft.commands.CommandDispatcher.argument("dimension", ArgumentDimension.dimension()).redirect(literalcommandnode, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).withLevel(ArgumentDimension.getDimension(commandcontext, "dimension"));
        }))));
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> wrapStores(LiteralCommandNode<CommandListenerWrapper> literalcommandnode, LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder, boolean flag) {
        literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.literal("score").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).redirect(literalcommandnode, (commandcontext) -> {
            return storeValue((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "targets"), ArgumentScoreboardObjective.getObjective(commandcontext, "objective"), flag);
        }))));
        literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.literal("bossbar").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("id", ArgumentMinecraftKeyRegistered.id()).suggests(CommandBossBar.SUGGEST_BOSS_BAR).then(net.minecraft.commands.CommandDispatcher.literal("value").redirect(literalcommandnode, (commandcontext) -> {
            return storeValue((CommandListenerWrapper) commandcontext.getSource(), CommandBossBar.getBossBar(commandcontext), true, flag);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("max").redirect(literalcommandnode, (commandcontext) -> {
            return storeValue((CommandListenerWrapper) commandcontext.getSource(), CommandBossBar.getBossBar(commandcontext), false, flag);
        }))));
        Iterator iterator = CommandData.TARGET_PROVIDERS.iterator();

        while (iterator.hasNext()) {
            CommandData.c commanddata_c = (CommandData.c) iterator.next();

            commanddata_c.wrap(literalargumentbuilder, (argumentbuilder) -> {
                return argumentbuilder.then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("path", ArgumentNBTKey.nbtPath()).then(net.minecraft.commands.CommandDispatcher.literal("int").then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return storeData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"), (i) -> {
                        return NBTTagInt.valueOf((int) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale")));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.literal("float").then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return storeData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"), (i) -> {
                        return NBTTagFloat.valueOf((float) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale")));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.literal("short").then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return storeData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"), (i) -> {
                        return NBTTagShort.valueOf((short) ((int) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale"))));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.literal("long").then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return storeData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"), (i) -> {
                        return NBTTagLong.valueOf((long) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale")));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.literal("double").then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return storeData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"), (i) -> {
                        return NBTTagDouble.valueOf((double) i * DoubleArgumentType.getDouble(commandcontext, "scale"));
                    }, flag);
                })))).then(net.minecraft.commands.CommandDispatcher.literal("byte").then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalcommandnode, (commandcontext) -> {
                    return storeData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"), (i) -> {
                        return NBTTagByte.valueOf((byte) ((int) ((double) i * DoubleArgumentType.getDouble(commandcontext, "scale"))));
                    }, flag);
                }))));
            });
        }

        return literalargumentbuilder;
    }

    private static CommandListenerWrapper storeValue(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection, ScoreboardObjective scoreboardobjective, boolean flag) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();

        return commandlistenerwrapper.withCallback((commandcontext, flag1, i) -> {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                ScoreboardScore scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);
                int j = flag ? i : (flag1 ? 1 : 0);

                scoreboardscore.setScore(j);
            }

        }, CommandExecute.CALLBACK_CHAINER);
    }

    private static CommandListenerWrapper storeValue(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, boolean flag, boolean flag1) {
        return commandlistenerwrapper.withCallback((commandcontext, flag2, i) -> {
            int j = flag1 ? i : (flag2 ? 1 : 0);

            if (flag) {
                bossbattlecustom.setValue(j);
            } else {
                bossbattlecustom.setMax(j);
            }

        }, CommandExecute.CALLBACK_CHAINER);
    }

    private static CommandListenerWrapper storeData(CommandListenerWrapper commandlistenerwrapper, CommandDataAccessor commanddataaccessor, ArgumentNBTKey.g argumentnbtkey_g, IntFunction<NBTBase> intfunction, boolean flag) {
        return commandlistenerwrapper.withCallback((commandcontext, flag1, i) -> {
            try {
                NBTTagCompound nbttagcompound = commanddataaccessor.getData();
                int j = flag ? i : (flag1 ? 1 : 0);

                argumentnbtkey_g.set(nbttagcompound, () -> {
                    return (NBTBase) intfunction.apply(j);
                });
                commanddataaccessor.setData(nbttagcompound);
            } catch (CommandSyntaxException commandsyntaxexception) {
                ;
            }

        }, CommandExecute.CALLBACK_CHAINER);
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> addConditionals(CommandNode<CommandListenerWrapper> commandnode, LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder, boolean flag) {
        ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.literal("block").then(net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentPosition.blockPos()).then(addConditional(commandnode, net.minecraft.commands.CommandDispatcher.argument("block", ArgumentBlockPredicate.blockPredicate()), flag, (commandcontext) -> {
            return ArgumentBlockPredicate.getBlockPredicate(commandcontext, "block").test(new ShapeDetectorBlock(((CommandListenerWrapper) commandcontext.getSource()).getLevel(), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), true));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("score").then(net.minecraft.commands.CommandDispatcher.argument("target", ArgumentScoreholder.scoreHolder()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targetObjective", ArgumentScoreboardObjective.objective()).then(net.minecraft.commands.CommandDispatcher.literal("=").then(net.minecraft.commands.CommandDispatcher.argument("source", ArgumentScoreholder.scoreHolder()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(addConditional(commandnode, net.minecraft.commands.CommandDispatcher.argument("sourceObjective", ArgumentScoreboardObjective.objective()), flag, (commandcontext) -> {
            return checkScore(commandcontext, Integer::equals);
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("<").then(net.minecraft.commands.CommandDispatcher.argument("source", ArgumentScoreholder.scoreHolder()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(addConditional(commandnode, net.minecraft.commands.CommandDispatcher.argument("sourceObjective", ArgumentScoreboardObjective.objective()), flag, (commandcontext) -> {
            return checkScore(commandcontext, (integer, integer1) -> {
                return integer < integer1;
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("<=").then(net.minecraft.commands.CommandDispatcher.argument("source", ArgumentScoreholder.scoreHolder()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(addConditional(commandnode, net.minecraft.commands.CommandDispatcher.argument("sourceObjective", ArgumentScoreboardObjective.objective()), flag, (commandcontext) -> {
            return checkScore(commandcontext, (integer, integer1) -> {
                return integer <= integer1;
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.literal(">").then(net.minecraft.commands.CommandDispatcher.argument("source", ArgumentScoreholder.scoreHolder()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(addConditional(commandnode, net.minecraft.commands.CommandDispatcher.argument("sourceObjective", ArgumentScoreboardObjective.objective()), flag, (commandcontext) -> {
            return checkScore(commandcontext, (integer, integer1) -> {
                return integer > integer1;
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.literal(">=").then(net.minecraft.commands.CommandDispatcher.argument("source", ArgumentScoreholder.scoreHolder()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(addConditional(commandnode, net.minecraft.commands.CommandDispatcher.argument("sourceObjective", ArgumentScoreboardObjective.objective()), flag, (commandcontext) -> {
            return checkScore(commandcontext, (integer, integer1) -> {
                return integer >= integer1;
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("matches").then(addConditional(commandnode, net.minecraft.commands.CommandDispatcher.argument("range", ArgumentCriterionValue.intRange()), flag, (commandcontext) -> {
            return checkScore(commandcontext, ArgumentCriterionValue.b.getRange(commandcontext, "range"));
        }))))))).then(net.minecraft.commands.CommandDispatcher.literal("blocks").then(net.minecraft.commands.CommandDispatcher.argument("start", ArgumentPosition.blockPos()).then(net.minecraft.commands.CommandDispatcher.argument("end", ArgumentPosition.blockPos()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("destination", ArgumentPosition.blockPos()).then(addIfBlocksConditional(commandnode, net.minecraft.commands.CommandDispatcher.literal("all"), flag, false))).then(addIfBlocksConditional(commandnode, net.minecraft.commands.CommandDispatcher.literal("masked"), flag, true))))))).then(net.minecraft.commands.CommandDispatcher.literal("entity").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("entities", ArgumentEntity.entities()).fork(commandnode, (commandcontext) -> {
            return expect(commandcontext, flag, !ArgumentEntity.getOptionalEntities(commandcontext, "entities").isEmpty());
        })).executes(createNumericConditionalHandler(flag, (commandcontext) -> {
            return ArgumentEntity.getOptionalEntities(commandcontext, "entities").size();
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("predicate").then(addConditional(commandnode, net.minecraft.commands.CommandDispatcher.argument("predicate", ArgumentMinecraftKeyRegistered.id()).suggests(CommandExecute.SUGGEST_PREDICATE), flag, (commandcontext) -> {
            return checkCustomPredicate((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.getPredicate(commandcontext, "predicate"));
        })));
        Iterator iterator = CommandData.SOURCE_PROVIDERS.iterator();

        while (iterator.hasNext()) {
            CommandData.c commanddata_c = (CommandData.c) iterator.next();

            literalargumentbuilder.then(commanddata_c.wrap(net.minecraft.commands.CommandDispatcher.literal("data"), (argumentbuilder) -> {
                return argumentbuilder.then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("path", ArgumentNBTKey.nbtPath()).fork(commandnode, (commandcontext) -> {
                    return expect(commandcontext, flag, checkMatchingData(commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path")) > 0);
                })).executes(createNumericConditionalHandler(flag, (commandcontext) -> {
                    return checkMatchingData(commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"));
                })));
            }));
        }

        return literalargumentbuilder;
    }

    private static Command<CommandListenerWrapper> createNumericConditionalHandler(boolean flag, CommandExecute.a commandexecute_a) {
        return flag ? (commandcontext) -> {
            int i = commandexecute_a.test(commandcontext);

            if (i > 0) {
                ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(new ChatMessage("commands.execute.conditional.pass_count", new Object[]{i}), false);
                return i;
            } else {
                throw CommandExecute.ERROR_CONDITIONAL_FAILED.create();
            }
        } : (commandcontext) -> {
            int i = commandexecute_a.test(commandcontext);

            if (i == 0) {
                ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(new ChatMessage("commands.execute.conditional.pass"), false);
                return 1;
            } else {
                throw CommandExecute.ERROR_CONDITIONAL_FAILED_COUNT.create(i);
            }
        };
    }

    private static int checkMatchingData(CommandDataAccessor commanddataaccessor, ArgumentNBTKey.g argumentnbtkey_g) throws CommandSyntaxException {
        return argumentnbtkey_g.countMatching(commanddataaccessor.getData());
    }

    private static boolean checkScore(CommandContext<CommandListenerWrapper> commandcontext, BiPredicate<Integer, Integer> bipredicate) throws CommandSyntaxException {
        String s = ArgumentScoreholder.getName(commandcontext, "target");
        ScoreboardObjective scoreboardobjective = ArgumentScoreboardObjective.getObjective(commandcontext, "targetObjective");
        String s1 = ArgumentScoreholder.getName(commandcontext, "source");
        ScoreboardObjective scoreboardobjective1 = ArgumentScoreboardObjective.getObjective(commandcontext, "sourceObjective");
        ScoreboardServer scoreboardserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getScoreboard();

        if (scoreboardserver.hasPlayerScore(s, scoreboardobjective) && scoreboardserver.hasPlayerScore(s1, scoreboardobjective1)) {
            ScoreboardScore scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);
            ScoreboardScore scoreboardscore1 = scoreboardserver.getOrCreatePlayerScore(s1, scoreboardobjective1);

            return bipredicate.test(scoreboardscore.getScore(), scoreboardscore1.getScore());
        } else {
            return false;
        }
    }

    private static boolean checkScore(CommandContext<CommandListenerWrapper> commandcontext, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) throws CommandSyntaxException {
        String s = ArgumentScoreholder.getName(commandcontext, "target");
        ScoreboardObjective scoreboardobjective = ArgumentScoreboardObjective.getObjective(commandcontext, "targetObjective");
        ScoreboardServer scoreboardserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getScoreboard();

        return !scoreboardserver.hasPlayerScore(s, scoreboardobjective) ? false : criterionconditionvalue_integerrange.matches(scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective).getScore());
    }

    private static boolean checkCustomPredicate(CommandListenerWrapper commandlistenerwrapper, LootItemCondition lootitemcondition) {
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder(worldserver)).withParameter(LootContextParameters.ORIGIN, commandlistenerwrapper.getPosition()).withOptionalParameter(LootContextParameters.THIS_ENTITY, commandlistenerwrapper.getEntity());

        return lootitemcondition.test(loottableinfo_builder.create(LootContextParameterSets.COMMAND));
    }

    private static Collection<CommandListenerWrapper> expect(CommandContext<CommandListenerWrapper> commandcontext, boolean flag, boolean flag1) {
        return (Collection) (flag1 == flag ? Collections.singleton((CommandListenerWrapper) commandcontext.getSource()) : Collections.emptyList());
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> addConditional(CommandNode<CommandListenerWrapper> commandnode, ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, boolean flag, CommandExecute.b commandexecute_b) {
        return argumentbuilder.fork(commandnode, (commandcontext) -> {
            return expect(commandcontext, flag, commandexecute_b.test(commandcontext));
        }).executes((commandcontext) -> {
            if (flag == commandexecute_b.test(commandcontext)) {
                ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(new ChatMessage("commands.execute.conditional.pass"), false);
                return 1;
            } else {
                throw CommandExecute.ERROR_CONDITIONAL_FAILED.create();
            }
        });
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> addIfBlocksConditional(CommandNode<CommandListenerWrapper> commandnode, ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, boolean flag, boolean flag1) {
        return argumentbuilder.fork(commandnode, (commandcontext) -> {
            return expect(commandcontext, flag, checkRegions(commandcontext, flag1).isPresent());
        }).executes(flag ? (commandcontext) -> {
            return checkIfRegions(commandcontext, flag1);
        } : (commandcontext) -> {
            return checkUnlessRegions(commandcontext, flag1);
        });
    }

    private static int checkIfRegions(CommandContext<CommandListenerWrapper> commandcontext, boolean flag) throws CommandSyntaxException {
        OptionalInt optionalint = checkRegions(commandcontext, flag);

        if (optionalint.isPresent()) {
            ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(new ChatMessage("commands.execute.conditional.pass_count", new Object[]{optionalint.getAsInt()}), false);
            return optionalint.getAsInt();
        } else {
            throw CommandExecute.ERROR_CONDITIONAL_FAILED.create();
        }
    }

    private static int checkUnlessRegions(CommandContext<CommandListenerWrapper> commandcontext, boolean flag) throws CommandSyntaxException {
        OptionalInt optionalint = checkRegions(commandcontext, flag);

        if (optionalint.isPresent()) {
            throw CommandExecute.ERROR_CONDITIONAL_FAILED_COUNT.create(optionalint.getAsInt());
        } else {
            ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(new ChatMessage("commands.execute.conditional.pass"), false);
            return 1;
        }
    }

    private static OptionalInt checkRegions(CommandContext<CommandListenerWrapper> commandcontext, boolean flag) throws CommandSyntaxException {
        return checkRegions(((CommandListenerWrapper) commandcontext.getSource()).getLevel(), ArgumentPosition.getLoadedBlockPos(commandcontext, "start"), ArgumentPosition.getLoadedBlockPos(commandcontext, "end"), ArgumentPosition.getLoadedBlockPos(commandcontext, "destination"), flag);
    }

    private static OptionalInt checkRegions(WorldServer worldserver, BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, boolean flag) throws CommandSyntaxException {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.fromCorners(blockposition, blockposition1);
        StructureBoundingBox structureboundingbox1 = StructureBoundingBox.fromCorners(blockposition2, blockposition2.offset(structureboundingbox.getLength()));
        BlockPosition blockposition3 = new BlockPosition(structureboundingbox1.minX() - structureboundingbox.minX(), structureboundingbox1.minY() - structureboundingbox.minY(), structureboundingbox1.minZ() - structureboundingbox.minZ());
        int i = structureboundingbox.getXSpan() * structureboundingbox.getYSpan() * structureboundingbox.getZSpan();

        if (i > 32768) {
            throw CommandExecute.ERROR_AREA_TOO_LARGE.create(32768, i);
        } else {
            int j = 0;

            for (int k = structureboundingbox.minZ(); k <= structureboundingbox.maxZ(); ++k) {
                for (int l = structureboundingbox.minY(); l <= structureboundingbox.maxY(); ++l) {
                    for (int i1 = structureboundingbox.minX(); i1 <= structureboundingbox.maxX(); ++i1) {
                        BlockPosition blockposition4 = new BlockPosition(i1, l, k);
                        BlockPosition blockposition5 = blockposition4.offset(blockposition3);
                        IBlockData iblockdata = worldserver.getBlockState(blockposition4);

                        if (!flag || !iblockdata.is(Blocks.AIR)) {
                            if (iblockdata != worldserver.getBlockState(blockposition5)) {
                                return OptionalInt.empty();
                            }

                            TileEntity tileentity = worldserver.getBlockEntity(blockposition4);
                            TileEntity tileentity1 = worldserver.getBlockEntity(blockposition5);

                            if (tileentity != null) {
                                if (tileentity1 == null) {
                                    return OptionalInt.empty();
                                }

                                if (tileentity1.getType() != tileentity.getType()) {
                                    return OptionalInt.empty();
                                }

                                NBTTagCompound nbttagcompound = tileentity.saveWithoutMetadata();
                                NBTTagCompound nbttagcompound1 = tileentity1.saveWithoutMetadata();

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
    private interface b {

        boolean test(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;
    }

    @FunctionalInterface
    private interface a {

        int test(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;
    }
}
