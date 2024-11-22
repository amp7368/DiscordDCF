package discord.util.dcf.util;

import discord.util.dcf.util.defer.DCFUtilsDeferReply;

public interface DCFUtils extends DCFUtilsDeferReply {

    static DCFUtils get() {
        return new DCFUtils() {
        };
    }
}
