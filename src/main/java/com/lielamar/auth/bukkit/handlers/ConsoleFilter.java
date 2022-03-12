package com.lielamar.auth.bukkit.handlers;

import com.lielamar.lielsutils.numbers.NumbersUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import java.util.Locale;

public class ConsoleFilter implements Filter {

    private static final String[] BLOCKED_STRING = {
            "issued server command: /2fa ".toLowerCase(Locale.ROOT),
            "issued server command: /2fa login ".toLowerCase(Locale.ROOT)
    };

    private State state = State.STARTED;

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }


    private Result check(String message) {
        message = message.toLowerCase(Locale.ROOT);

        if(!message.contains("2fa"))
            return Result.NEUTRAL;

        for(String blockedString : BLOCKED_STRING) {
            if(message.contains(blockedString)) {
                String[] args = message.split(blockedString);

                if(args.length > 1) {
                    String code;

                    if(args[1].contains(" ")) code = args[1].split(" ")[0];
                    else                      code = args[1];

                    if(code.length() > 0 && NumbersUtils.isInteger(code))
                        return Result.DENY;
                }
            }
        }

        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object... params) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return this.check(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object message, Throwable t) {
        return this.check(message.toString());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable t) {
        return this.check(message.getFormattedMessage());
    }

    @Override
    public Result filter(LogEvent event) {
        return this.check(event.getMessage().getFormattedMessage());
    }


    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void initialize() {}

    @Override
    public void start() {}

    @Override
    public void stop() {
        this.state = State.STOPPED;
    }

    @Override
    public boolean isStarted() { return true; }

    @Override
    public boolean isStopped() { return false; }
}

