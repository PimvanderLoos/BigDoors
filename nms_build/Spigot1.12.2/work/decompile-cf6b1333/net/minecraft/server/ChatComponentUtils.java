package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class ChatComponentUtils {

    public static IChatBaseComponent filterForDisplay(ICommandListener icommandlistener, IChatBaseComponent ichatbasecomponent, Entity entity) throws CommandException {
        Object object;

        if (ichatbasecomponent instanceof ChatComponentScore) {
            ChatComponentScore chatcomponentscore = (ChatComponentScore) ichatbasecomponent;
            String s = chatcomponentscore.g();

            if (PlayerSelector.isPattern(s)) {
                List list = PlayerSelector.getPlayers(icommandlistener, s, Entity.class);

                if (list.size() != 1) {
                    throw new ExceptionEntityNotFound("commands.generic.selector.notFound", new Object[] { s});
                }

                Entity entity1 = (Entity) list.get(0);

                if (entity1 instanceof EntityHuman) {
                    s = entity1.getName();
                } else {
                    s = entity1.bn();
                }
            }

            String s1 = entity != null && s.equals("*") ? entity.getName() : s;

            object = new ChatComponentScore(s1, chatcomponentscore.h());
            ((ChatComponentScore) object).b(chatcomponentscore.getText());
            ((ChatComponentScore) object).a(icommandlistener);
        } else if (ichatbasecomponent instanceof ChatComponentSelector) {
            String s2 = ((ChatComponentSelector) ichatbasecomponent).g();

            object = PlayerSelector.getPlayerNames(icommandlistener, s2);
            if (object == null) {
                object = new ChatComponentText("");
            }
        } else if (ichatbasecomponent instanceof ChatComponentText) {
            object = new ChatComponentText(((ChatComponentText) ichatbasecomponent).g());
        } else if (ichatbasecomponent instanceof ChatComponentKeybind) {
            object = new ChatComponentKeybind(((ChatComponentKeybind) ichatbasecomponent).h());
        } else {
            if (!(ichatbasecomponent instanceof ChatMessage)) {
                return ichatbasecomponent;
            }

            Object[] aobject = ((ChatMessage) ichatbasecomponent).j();

            for (int i = 0; i < aobject.length; ++i) {
                Object object1 = aobject[i];

                if (object1 instanceof IChatBaseComponent) {
                    aobject[i] = filterForDisplay(icommandlistener, (IChatBaseComponent) object1, entity);
                }
            }

            object = new ChatMessage(((ChatMessage) ichatbasecomponent).i(), aobject);
        }

        ChatModifier chatmodifier = ichatbasecomponent.getChatModifier();

        if (chatmodifier != null) {
            ((IChatBaseComponent) object).setChatModifier(chatmodifier.clone());
        }

        Iterator iterator = ichatbasecomponent.a().iterator();

        while (iterator.hasNext()) {
            IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) iterator.next();

            ((IChatBaseComponent) object).addSibling(filterForDisplay(icommandlistener, ichatbasecomponent1, entity));
        }

        return (IChatBaseComponent) object;
    }
}
