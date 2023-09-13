package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.FilteredText;

public class ArgumentChat implements SignedArgument<ArgumentChat.a> {

    private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

    public ArgumentChat() {}

    public static ArgumentChat message() {
        return new ArgumentChat();
    }

    public static IChatBaseComponent getMessage(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        ArgumentChat.a argumentchat_a = (ArgumentChat.a) commandcontext.getArgument(s, ArgumentChat.a.class);

        return argumentchat_a.resolveComponent((CommandListenerWrapper) commandcontext.getSource());
    }

    public static void resolveChatMessage(CommandContext<CommandListenerWrapper> commandcontext, String s, Consumer<PlayerChatMessage> consumer) throws CommandSyntaxException {
        ArgumentChat.a argumentchat_a = (ArgumentChat.a) commandcontext.getArgument(s, ArgumentChat.a.class);
        CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
        IChatBaseComponent ichatbasecomponent = argumentchat_a.resolveComponent(commandlistenerwrapper);
        CommandSigningContext commandsigningcontext = commandlistenerwrapper.getSigningContext();
        PlayerChatMessage playerchatmessage = commandsigningcontext.getArgument(s);

        if (playerchatmessage != null) {
            resolveSignedMessage(consumer, commandlistenerwrapper, playerchatmessage.withUnsignedContent(ichatbasecomponent));
        } else {
            resolveDisguisedMessage(consumer, commandlistenerwrapper, PlayerChatMessage.system(argumentchat_a.text).withUnsignedContent(ichatbasecomponent));
        }

    }

    private static void resolveSignedMessage(Consumer<PlayerChatMessage> consumer, CommandListenerWrapper commandlistenerwrapper, PlayerChatMessage playerchatmessage) {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        CompletableFuture<FilteredText> completablefuture = filterPlainText(commandlistenerwrapper, playerchatmessage);
        CompletableFuture<IChatBaseComponent> completablefuture1 = minecraftserver.getChatDecorator().decorate(commandlistenerwrapper.getPlayer(), playerchatmessage.decoratedContent());

        commandlistenerwrapper.getChatMessageChainer().append((executor) -> {
            return CompletableFuture.allOf(completablefuture, completablefuture1).thenAcceptAsync((ovoid) -> {
                PlayerChatMessage playerchatmessage1 = playerchatmessage.withUnsignedContent((IChatBaseComponent) completablefuture1.join()).filter(((FilteredText) completablefuture.join()).mask());

                consumer.accept(playerchatmessage1);
            }, executor);
        });
    }

    private static void resolveDisguisedMessage(Consumer<PlayerChatMessage> consumer, CommandListenerWrapper commandlistenerwrapper, PlayerChatMessage playerchatmessage) {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        CompletableFuture<IChatBaseComponent> completablefuture = minecraftserver.getChatDecorator().decorate(commandlistenerwrapper.getPlayer(), playerchatmessage.decoratedContent());

        commandlistenerwrapper.getChatMessageChainer().append((executor) -> {
            return completablefuture.thenAcceptAsync((ichatbasecomponent) -> {
                consumer.accept(playerchatmessage.withUnsignedContent(ichatbasecomponent));
            }, executor);
        });
    }

    private static CompletableFuture<FilteredText> filterPlainText(CommandListenerWrapper commandlistenerwrapper, PlayerChatMessage playerchatmessage) {
        EntityPlayer entityplayer = commandlistenerwrapper.getPlayer();

        return entityplayer != null && playerchatmessage.hasSignatureFrom(entityplayer.getUUID()) ? entityplayer.getTextFilter().processStreamMessage(playerchatmessage.signedContent()) : CompletableFuture.completedFuture(FilteredText.passThrough(playerchatmessage.signedContent()));
    }

    public ArgumentChat.a parse(StringReader stringreader) throws CommandSyntaxException {
        return ArgumentChat.a.parseText(stringreader, true);
    }

    public Collection<String> getExamples() {
        return ArgumentChat.EXAMPLES;
    }

    public static class a {

        final String text;
        private final ArgumentChat.b[] parts;

        public a(String s, ArgumentChat.b[] aargumentchat_b) {
            this.text = s;
            this.parts = aargumentchat_b;
        }

        public String getText() {
            return this.text;
        }

        public ArgumentChat.b[] getParts() {
            return this.parts;
        }

        IChatBaseComponent resolveComponent(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
            return this.toComponent(commandlistenerwrapper, commandlistenerwrapper.hasPermission(2));
        }

        public IChatBaseComponent toComponent(CommandListenerWrapper commandlistenerwrapper, boolean flag) throws CommandSyntaxException {
            if (this.parts.length != 0 && flag) {
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal(this.text.substring(0, this.parts[0].getStart()));
                int i = this.parts[0].getStart();
                ArgumentChat.b[] aargumentchat_b = this.parts;
                int j = aargumentchat_b.length;

                for (int k = 0; k < j; ++k) {
                    ArgumentChat.b argumentchat_b = aargumentchat_b[k];
                    IChatBaseComponent ichatbasecomponent = argumentchat_b.toComponent(commandlistenerwrapper);

                    if (i < argumentchat_b.getStart()) {
                        ichatmutablecomponent.append(this.text.substring(i, argumentchat_b.getStart()));
                    }

                    if (ichatbasecomponent != null) {
                        ichatmutablecomponent.append(ichatbasecomponent);
                    }

                    i = argumentchat_b.getEnd();
                }

                if (i < this.text.length()) {
                    ichatmutablecomponent.append(this.text.substring(i));
                }

                return ichatmutablecomponent;
            } else {
                return IChatBaseComponent.literal(this.text);
            }
        }

        public static ArgumentChat.a parseText(StringReader stringreader, boolean flag) throws CommandSyntaxException {
            String s = stringreader.getString().substring(stringreader.getCursor(), stringreader.getTotalLength());

            if (!flag) {
                stringreader.setCursor(stringreader.getTotalLength());
                return new ArgumentChat.a(s, new ArgumentChat.b[0]);
            } else {
                List<ArgumentChat.b> list = Lists.newArrayList();
                int i = stringreader.getCursor();

                while (stringreader.canRead()) {
                    if (stringreader.peek() == '@') {
                        int j = stringreader.getCursor();

                        EntitySelector entityselector;

                        try {
                            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(stringreader);

                            entityselector = argumentparserselector.parse();
                        } catch (CommandSyntaxException commandsyntaxexception) {
                            if (commandsyntaxexception.getType() != ArgumentParserSelector.ERROR_MISSING_SELECTOR_TYPE && commandsyntaxexception.getType() != ArgumentParserSelector.ERROR_UNKNOWN_SELECTOR_TYPE) {
                                throw commandsyntaxexception;
                            }

                            stringreader.setCursor(j + 1);
                            continue;
                        }

                        list.add(new ArgumentChat.b(j - i, stringreader.getCursor() - i, entityselector));
                    } else {
                        stringreader.skip();
                    }
                }

                return new ArgumentChat.a(s, (ArgumentChat.b[]) list.toArray(new ArgumentChat.b[0]));
            }
        }
    }

    public static class b {

        private final int start;
        private final int end;
        private final EntitySelector selector;

        public b(int i, int j, EntitySelector entityselector) {
            this.start = i;
            this.end = j;
            this.selector = entityselector;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }

        public EntitySelector getSelector() {
            return this.selector;
        }

        @Nullable
        public IChatBaseComponent toComponent(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
            return EntitySelector.joinNames(this.selector.findEntities(commandlistenerwrapper));
        }
    }
}
