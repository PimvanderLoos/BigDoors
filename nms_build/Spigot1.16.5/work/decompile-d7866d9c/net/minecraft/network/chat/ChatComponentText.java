package net.minecraft.network.chat;

public class ChatComponentText extends ChatBaseComponent {

    public static final IChatBaseComponent d = new ChatComponentText("");
    private final String e;

    public ChatComponentText(String s) {
        this.e = s;
    }

    public String h() {
        return this.e;
    }

    @Override
    public String getText() {
        return this.e;
    }

    @Override
    public ChatComponentText g() {
        return new ChatComponentText(this.e);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentText)) {
            return false;
        } else {
            ChatComponentText chatcomponenttext = (ChatComponentText) object;

            return this.e.equals(chatcomponenttext.h()) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "TextComponent{text='" + this.e + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatModifier() + '}';
    }
}
