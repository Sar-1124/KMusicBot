package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.commands.DJCommand;

public class DntLeaveCmd extends DJCommand {
    public DntLeaveCmd(Bot bot) {
        super(bot);
        this.name = "24/7";
        this.help = "24/7 모드를 활성화해요.";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    public void doCommand(CommandEvent event) {

    }
}
