package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.chat.ChatModifier;

@FunctionalInterface
public interface FormattedString {

    FormattedString EMPTY = (formattedstringempty) -> {
        return true;
    };

    boolean accept(FormattedStringEmpty formattedstringempty);

    static FormattedString a(int i, ChatModifier chatmodifier) {
        return (formattedstringempty) -> {
            return formattedstringempty.accept(0, chatmodifier, i);
        };
    }

    static FormattedString a(String s, ChatModifier chatmodifier) {
        return s.isEmpty() ? FormattedString.EMPTY : (formattedstringempty) -> {
            return StringDecomposer.a(s, chatmodifier, formattedstringempty);
        };
    }

    static FormattedString a(String s, ChatModifier chatmodifier, Int2IntFunction int2intfunction) {
        return s.isEmpty() ? FormattedString.EMPTY : (formattedstringempty) -> {
            return StringDecomposer.a(s, chatmodifier, a(formattedstringempty, int2intfunction));
        };
    }

    static FormattedString b(String s, ChatModifier chatmodifier) {
        return s.isEmpty() ? FormattedString.EMPTY : (formattedstringempty) -> {
            return StringDecomposer.b(s, chatmodifier, formattedstringempty);
        };
    }

    static FormattedString b(String s, ChatModifier chatmodifier, Int2IntFunction int2intfunction) {
        return s.isEmpty() ? FormattedString.EMPTY : (formattedstringempty) -> {
            return StringDecomposer.b(s, chatmodifier, a(formattedstringempty, int2intfunction));
        };
    }

    static FormattedStringEmpty a(FormattedStringEmpty formattedstringempty, Int2IntFunction int2intfunction) {
        return (i, chatmodifier, j) -> {
            return formattedstringempty.accept(i, chatmodifier, (Integer) int2intfunction.apply(j));
        };
    }

    static FormattedString a() {
        return FormattedString.EMPTY;
    }

    static FormattedString a(FormattedString formattedstring) {
        return formattedstring;
    }

    static FormattedString a(FormattedString formattedstring, FormattedString formattedstring1) {
        return b(formattedstring, formattedstring1);
    }

    static FormattedString a(FormattedString... aformattedstring) {
        return b(ImmutableList.copyOf(aformattedstring));
    }

    static FormattedString a(List<FormattedString> list) {
        int i = list.size();

        switch (i) {
            case 0:
                return FormattedString.EMPTY;
            case 1:
                return (FormattedString) list.get(0);
            case 2:
                return b((FormattedString) list.get(0), (FormattedString) list.get(1));
            default:
                return b(ImmutableList.copyOf(list));
        }
    }

    static FormattedString b(FormattedString formattedstring, FormattedString formattedstring1) {
        return (formattedstringempty) -> {
            return formattedstring.accept(formattedstringempty) && formattedstring1.accept(formattedstringempty);
        };
    }

    static FormattedString b(List<FormattedString> list) {
        return (formattedstringempty) -> {
            Iterator iterator = list.iterator();

            FormattedString formattedstring;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                formattedstring = (FormattedString) iterator.next();
            } while (formattedstring.accept(formattedstringempty));

            return false;
        };
    }
}
