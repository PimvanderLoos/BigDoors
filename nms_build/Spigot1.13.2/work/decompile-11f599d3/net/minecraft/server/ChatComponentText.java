package net.minecraft.server;

public class ChatComponentText extends ChatBaseComponent {

    private final String b;

    public ChatComponentText(String s) {
        this.b = s;
    }

    public String i() {
        return this.b;
    }

    public String getText() {
        return this.b;
    }

    public ChatComponentText g() {
        return new ChatComponentText(this.b);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentText)) {
            return false;
        } else {
            ChatComponentText chatcomponenttext = (ChatComponentText) object;

            return this.b.equals(chatcomponenttext.i()) && super.equals(object);
        }
    }

    public String toString() {
        return "TextComponent{text='" + this.b + '\'' + ", siblings=" + this.a + ", style=" + this.getChatModifier() + '}';
    }
}
