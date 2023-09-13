package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatClickable {

    private final ChatClickable.EnumClickAction a;
    private final String b;

    public ChatClickable(ChatClickable.EnumClickAction chatclickable_enumclickaction, String s) {
        this.a = chatclickable_enumclickaction;
        this.b = s;
    }

    public ChatClickable.EnumClickAction a() {
        return this.a;
    }

    public String b() {
        return this.b;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            ChatClickable chatclickable = (ChatClickable) object;

            if (this.a != chatclickable.a) {
                return false;
            } else {
                if (this.b != null) {
                    if (this.b.equals(chatclickable.b)) {
                        return true;
                    }
                } else if (chatclickable.b == null) {
                    return true;
                }

                return false;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        return "ClickEvent{action=" + this.a + ", value='" + this.b + '\'' + '}';
    }

    public int hashCode() {
        int i = this.a.hashCode();

        i = 31 * i + (this.b != null ? this.b.hashCode() : 0);
        return i;
    }

    public static enum EnumClickAction {

        OPEN_URL("open_url", true), OPEN_FILE("open_file", false), RUN_COMMAND("run_command", true), SUGGEST_COMMAND("suggest_command", true), CHANGE_PAGE("change_page", true), COPY_TO_CLIPBOARD("copy_to_clipboard", true);

        private static final Map<String, ChatClickable.EnumClickAction> g = (Map) Arrays.stream(values()).collect(Collectors.toMap(ChatClickable.EnumClickAction::b, (chatclickable_enumclickaction) -> {
            return chatclickable_enumclickaction;
        }));
        private final boolean h;
        private final String i;

        private EnumClickAction(String s, boolean flag) {
            this.i = s;
            this.h = flag;
        }

        public boolean a() {
            return this.h;
        }

        public String b() {
            return this.i;
        }

        public static ChatClickable.EnumClickAction a(String s) {
            return (ChatClickable.EnumClickAction) ChatClickable.EnumClickAction.g.get(s);
        }
    }
}
