package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.commands.DJCommand;

public class RemoveRangeCmd extends DJCommand {
    public RemoveRangeCmd(Bot bot) {
        super(bot);
        this.name = "removerange";
        this.help = "Deletes tracks in queue that are specified in the range";
        this.arguments = "x x|1 3";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = false;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

    }
}
