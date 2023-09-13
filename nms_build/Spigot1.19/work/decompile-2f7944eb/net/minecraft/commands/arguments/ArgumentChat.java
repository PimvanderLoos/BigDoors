package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.FilteredText;
import org.slf4j.Logger;

public class ArgumentChat implements SignedArgument<ArgumentChat.b> {

    private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");
    static final Logger LOGGER = LogUtils.getLogger();

    public ArgumentChat() {}

    public static ArgumentChat message() {
        return new ArgumentChat();
    }

    public static IChatBaseComponent getMessage(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        ArgumentChat.b argumentchat_b = (ArgumentChat.b) commandcontext.getArgument(s, ArgumentChat.b.class);

        return argumentchat_b.resolveComponent((CommandListenerWrapper) commandcontext.getSource());
    }

    public static ArgumentChat.a getChatMessage(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        ArgumentChat.b argumentchat_b = (ArgumentChat.b) commandcontext.getArgument(s, ArgumentChat.b.class);
        IChatBaseComponent ichatbasecomponent = argumentchat_b.resolveComponent((CommandListenerWrapper) commandcontext.getSource());
        CommandSigningContext commandsigningcontext = ((CommandListenerWrapper) commandcontext.getSource()).getSigningContext();
        MessageSignature messagesignature = commandsigningcontext.getArgumentSignature(s);
        boolean flag = commandsigningcontext.signedArgumentPreview(s);
        ChatSender chatsender = ((CommandListenerWrapper) commandcontext.getSource()).asChatSender();

        return messagesignature.isValid(chatsender.uuid()) ? new ArgumentChat.a(argumentchat_b.text, ichatbasecomponent, messagesignature, flag) : new ArgumentChat.a(argumentchat_b.text, ichatbasecomponent, MessageSignature.unsigned(), false);
    }

    public ArgumentChat.b parse(StringReader stringreader) throws CommandSyntaxException {
        return ArgumentChat.b.parseText(stringreader, true);
    }

    public Collection<String> getExamples() {
        return ArgumentChat.EXAMPLES;
    }

    public IChatBaseComponent getPlainSignableComponent(ArgumentChat.b argumentchat_b) {
        return IChatBaseComponent.literal(argumentchat_b.getText());
    }

    public CompletableFuture<IChatBaseComponent> resolvePreview(CommandListenerWrapper commandlistenerwrapper, ArgumentChat.b argumentchat_b) throws CommandSyntaxException {
        return argumentchat_b.resolveDecoratedComponent(commandlistenerwrapper);
    }

    @Override
    public Class<ArgumentChat.b> getValueType() {
        return ArgumentChat.b.class;
    }

    static void logResolutionFailure(CommandListenerWrapper commandlistenerwrapper, CompletableFuture<?> completablefuture) {
        completablefuture.exceptionally((throwable) -> {
            ArgumentChat.LOGGER.error("Encountered unexpected exception while resolving chat message argument from '{}'", commandlistenerwrapper.getDisplayName().getString(), throwable);
            return null;
        });
    }

    public static class b {

        final String text;
        private final ArgumentChat.c[] parts;

        public b(String s, ArgumentChat.c[] aargumentchat_c) {
            this.text = s;
            this.parts = aargumentchat_c;
        }

        public String getText() {
            return this.text;
        }

        public ArgumentChat.c[] getParts() {
            return this.parts;
        }

        CompletableFuture<IChatBaseComponent> resolveDecoratedComponent(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
            IChatBaseComponent ichatbasecomponent = this.resolveComponent(commandlistenerwrapper);
            CompletableFuture<IChatBaseComponent> completablefuture = commandlistenerwrapper.getServer().getChatDecorator().decorate(commandlistenerwrapper.getPlayer(), ichatbasecomponent);

            ArgumentChat.logResolutionFailure(commandlistenerwrapper, completablefuture);
            return completablefuture;
        }

        IChatBaseComponent resolveComponent(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
            return this.toComponent(commandlistenerwrapper, commandlistenerwrapper.hasPermission(2));
        }

        public IChatBaseComponent toComponent(CommandListenerWrapper commandlistenerwrapper, boolean flag) throws CommandSyntaxException {
            if (this.parts.length != 0 && flag) {
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal(this.text.substring(0, this.parts[0].getStart()));
                int i = this.parts[0].getStart();
                ArgumentChat.c[] aargumentchat_c = this.parts;
                int j = aargumentchat_c.length;

                for (int k = 0; k < j; ++k) {
                    ArgumentChat.c argumentchat_c = aargumentchat_c[k];
                    IChatBaseComponent ichatbasecomponent = argumentchat_c.toComponent(commandlistenerwrapper);

                    if (i < argumentchat_c.getStart()) {
                        ichatmutablecomponent.append(this.text.substring(i, argumentchat_c.getStart()));
                    }

                    if (ichatbasecomponent != null) {
                        ichatmutablecomponent.append(ichatbasecomponent);
                    }

                    i = argumentchat_c.getEnd();
                }

                if (i < this.text.length()) {
                    ichatmutablecomponent.append(this.text.substring(i));
                }

                return ichatmutablecomponent;
            } else {
                return IChatBaseComponent.literal(this.text);
            }
        }

        public static ArgumentChat.b parseText(StringReader stringreader, boolean flag) throws CommandSyntaxException {
            String s = stringreader.getString().substring(stringreader.getCursor(), stringreader.getTotalLength());

            if (!flag) {
                stringreader.setCursor(stringreader.getTotalLength());
                return new ArgumentChat.b(s, new ArgumentChat.c[0]);
            } else {
                List<ArgumentChat.c> list = Lists.newArrayList();
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

                        list.add(new ArgumentChat.c(j - i, stringreader.getCursor() - i, entityselector));
                    } else {
                        stringreader.skip();
                    }
                }

                return new ArgumentChat.b(s, (ArgumentChat.c[]) list.toArray(new ArgumentChat.c[0]));
            }
        }
    }

    public static record a(String plain, IChatBaseComponent formatted, MessageSignature signature, boolean signedPreview) {

        public CompletableFuture<FilteredText<PlayerChatMessage>> resolve(CommandListenerWrapper commandlistenerwrapper) {
            CompletableFuture<FilteredText<PlayerChatMessage>> completablefuture = this.filterComponent(commandlistenerwrapper, this.formatted).thenComposeAsync((filteredtext) -> {
                ChatDecorator chatdecorator = commandlistenerwrapper.getServer().getChatDecorator();

                return chatdecorator.decorateChat(commandlistenerwrapper.getPlayer(), filteredtext, this.signature, this.signedPreview);
            }, commandlistenerwrapper.getServer()).thenApply((filteredtext) -> {
                PlayerChatMessage playerchatmessage = this.getSignedMessage(filteredtext);

                if (playerchatmessage != null) {
                    this.verify(commandlistenerwrapper, playerchatmessage);
                }

                return filteredtext;
            });

            ArgumentChat.logResolutionFailure(commandlistenerwrapper, completablefuture);
            return completablefuture;
        }

        @Nullable
        private PlayerChatMessage getSignedMessage(FilteredText<PlayerChatMessage> filteredtext) {
            return this.signature.isValid() ? (this.signedPreview ? (PlayerChatMessage) filteredtext.raw() : PlayerChatMessage.signed(this.plain, this.signature)) : null;
        }

        private void verify(CommandListenerWrapper commandlistenerwrapper, PlayerChatMessage playerchatmessage) {
            if (!playerchatmessage.verify(commandlistenerwrapper)) {
                ArgumentChat.LOGGER.warn("{} sent message with invalid signature: '{}'", commandlistenerwrapper.getDisplayName().getString(), playerchatmessage.signedContent().getString());
            }

        }

        private CompletableFuture<FilteredText<IChatBaseComponent>> filterComponent(CommandListenerWrapper commandlistenerwrapper, IChatBaseComponent ichatbasecomponent) {
            EntityPlayer entityplayer = commandlistenerwrapper.getPlayer();

            return entityplayer != null ? entityplayer.getTextFilter().processStreamComponent(ichatbasecomponent) : CompletableFuture.completedFuture(FilteredText.passThrough(ichatbasecomponent));
        }
    }

    public static class c {

        private final int start;
        private final int end;
        private final EntitySelector selector;

        public c(int i, int j, EntitySelector entityselector) {
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
