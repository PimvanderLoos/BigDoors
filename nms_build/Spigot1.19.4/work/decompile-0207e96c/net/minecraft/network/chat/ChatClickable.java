package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatClickable {

    private final ChatClickable.EnumClickAction action;
    private final String value;

    public ChatClickable(ChatClickable.EnumClickAction chatclickable_enumclickaction, String s) {
        this.action = chatclickable_enumclickaction;
        this.value = s;
    }

    public ChatClickable.EnumClickAction getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            ChatClickable chatclickable = (ChatClickable) object;

            if (this.action != chatclickable.action) {
                return false;
            } else {
                if (this.value != null) {
                    if (this.value.equals(chatclickable.value)) {
                        return true;
                    }
                } else if (chatclickable.value == null) {
                    return true;
                }

                return false;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        return "ClickEvent{action=" + this.action + ", value='" + this.value + "'}";
    }

    public int hashCode() {
        int i = this.action.hashCode();

        i = 31 * i + (this.value != null ? this.value.hashCode() : 0);
        return i;
    }

    public static enum EnumClickAction {

        OPEN_URL("open_url", true), OPEN_FILE("open_file", false), RUN_COMMAND("run_command", true), SUGGEST_COMMAND("suggest_command", true), CHANGE_PAGE("change_page", true), COPY_TO_CLIPBOARD("copy_to_clipboard", true);

        private static final Map<String, ChatClickable.EnumClickAction> LOOKUP = (Map) Arrays.stream(values()).collect(Collectors.toMap(ChatClickable.EnumClickAction::getName, (chatclickable_enumclickaction) -> {
            return chatclickable_enumclickaction;
        }));
        private final boolean allowFromServer;
        private final String name;

        private EnumClickAction(String s, boolean flag) {
            this.name = s;
            this.allowFromServer = flag;
        }

        public boolean isAllowedFromServer() {
            return this.allowFromServer;
        }

        public String getName() {
            return this.name;
        }

        public static ChatClickable.EnumClickAction getByName(String s) {
            return (ChatClickable.EnumClickAction) ChatClickable.EnumClickAction.LOOKUP.get(s);
        }
    }
}
