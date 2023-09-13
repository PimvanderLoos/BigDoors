package net.minecraft.commands;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.ChatMessage;

public class CommandExceptionProvider implements BuiltInExceptionProvider {

    private static final Dynamic2CommandExceptionType DOUBLE_TOO_SMALL = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.double.low", new Object[]{object1, object});
    });
    private static final Dynamic2CommandExceptionType DOUBLE_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.double.big", new Object[]{object1, object});
    });
    private static final Dynamic2CommandExceptionType FLOAT_TOO_SMALL = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.float.low", new Object[]{object1, object});
    });
    private static final Dynamic2CommandExceptionType FLOAT_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.float.big", new Object[]{object1, object});
    });
    private static final Dynamic2CommandExceptionType INTEGER_TOO_SMALL = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.integer.low", new Object[]{object1, object});
    });
    private static final Dynamic2CommandExceptionType INTEGER_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.integer.big", new Object[]{object1, object});
    });
    private static final Dynamic2CommandExceptionType LONG_TOO_SMALL = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.long.low", new Object[]{object1, object});
    });
    private static final Dynamic2CommandExceptionType LONG_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.long.big", new Object[]{object1, object});
    });
    private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.literal.incorrect", new Object[]{object});
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType(new ChatMessage("parsing.quote.expected.start"));
    private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType(new ChatMessage("parsing.quote.expected.end"));
    private static final DynamicCommandExceptionType READER_INVALID_ESCAPE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("parsing.quote.escape", new Object[]{object});
    });
    private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("parsing.bool.invalid", new Object[]{object});
    });
    private static final DynamicCommandExceptionType READER_INVALID_INT = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("parsing.int.invalid", new Object[]{object});
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType(new ChatMessage("parsing.int.expected"));
    private static final DynamicCommandExceptionType READER_INVALID_LONG = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("parsing.long.invalid", new Object[]{object});
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_LONG = new SimpleCommandExceptionType(new ChatMessage("parsing.long.expected"));
    private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("parsing.double.invalid", new Object[]{object});
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new ChatMessage("parsing.double.expected"));
    private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("parsing.float.invalid", new Object[]{object});
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType(new ChatMessage("parsing.float.expected"));
    private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType(new ChatMessage("parsing.bool.expected"));
    private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("parsing.expected", new Object[]{object});
    });
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType(new ChatMessage("command.unknown.command"));
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType(new ChatMessage("command.unknown.argument"));
    private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType(new ChatMessage("command.expected.separator"));
    private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("command.exception", new Object[]{object});
    });

    public CommandExceptionProvider() {}

    public Dynamic2CommandExceptionType doubleTooLow() {
        return CommandExceptionProvider.DOUBLE_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType doubleTooHigh() {
        return CommandExceptionProvider.DOUBLE_TOO_BIG;
    }

    public Dynamic2CommandExceptionType floatTooLow() {
        return CommandExceptionProvider.FLOAT_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType floatTooHigh() {
        return CommandExceptionProvider.FLOAT_TOO_BIG;
    }

    public Dynamic2CommandExceptionType integerTooLow() {
        return CommandExceptionProvider.INTEGER_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType integerTooHigh() {
        return CommandExceptionProvider.INTEGER_TOO_BIG;
    }

    public Dynamic2CommandExceptionType longTooLow() {
        return CommandExceptionProvider.LONG_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType longTooHigh() {
        return CommandExceptionProvider.LONG_TOO_BIG;
    }

    public DynamicCommandExceptionType literalIncorrect() {
        return CommandExceptionProvider.LITERAL_INCORRECT;
    }

    public SimpleCommandExceptionType readerExpectedStartOfQuote() {
        return CommandExceptionProvider.READER_EXPECTED_START_OF_QUOTE;
    }

    public SimpleCommandExceptionType readerExpectedEndOfQuote() {
        return CommandExceptionProvider.READER_EXPECTED_END_OF_QUOTE;
    }

    public DynamicCommandExceptionType readerInvalidEscape() {
        return CommandExceptionProvider.READER_INVALID_ESCAPE;
    }

    public DynamicCommandExceptionType readerInvalidBool() {
        return CommandExceptionProvider.READER_INVALID_BOOL;
    }

    public DynamicCommandExceptionType readerInvalidInt() {
        return CommandExceptionProvider.READER_INVALID_INT;
    }

    public SimpleCommandExceptionType readerExpectedInt() {
        return CommandExceptionProvider.READER_EXPECTED_INT;
    }

    public DynamicCommandExceptionType readerInvalidLong() {
        return CommandExceptionProvider.READER_INVALID_LONG;
    }

    public SimpleCommandExceptionType readerExpectedLong() {
        return CommandExceptionProvider.READER_EXPECTED_LONG;
    }

    public DynamicCommandExceptionType readerInvalidDouble() {
        return CommandExceptionProvider.READER_INVALID_DOUBLE;
    }

    public SimpleCommandExceptionType readerExpectedDouble() {
        return CommandExceptionProvider.READER_EXPECTED_DOUBLE;
    }

    public DynamicCommandExceptionType readerInvalidFloat() {
        return CommandExceptionProvider.READER_INVALID_FLOAT;
    }

    public SimpleCommandExceptionType readerExpectedFloat() {
        return CommandExceptionProvider.READER_EXPECTED_FLOAT;
    }

    public SimpleCommandExceptionType readerExpectedBool() {
        return CommandExceptionProvider.READER_EXPECTED_BOOL;
    }

    public DynamicCommandExceptionType readerExpectedSymbol() {
        return CommandExceptionProvider.READER_EXPECTED_SYMBOL;
    }

    public SimpleCommandExceptionType dispatcherUnknownCommand() {
        return CommandExceptionProvider.DISPATCHER_UNKNOWN_COMMAND;
    }

    public SimpleCommandExceptionType dispatcherUnknownArgument() {
        return CommandExceptionProvider.DISPATCHER_UNKNOWN_ARGUMENT;
    }

    public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
        return CommandExceptionProvider.DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
    }

    public DynamicCommandExceptionType dispatcherParseException() {
        return CommandExceptionProvider.DISPATCHER_PARSE_EXCEPTION;
    }
}
