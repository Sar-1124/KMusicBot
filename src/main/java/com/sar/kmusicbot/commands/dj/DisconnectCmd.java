package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.commands.DJCommand;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class DisconnectCmd extends DJCommand {
    public DisconnectCmd(Bot bot) {
        super(bot);
        this.name = "disconnect";
        this.help = "봇이 채널을 나가요.";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    public void doCommand(CommandEvent event) {
        VoiceChannel current = event.getGuild().getSelfMember().getVoiceState().getChannel();
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();

        if(current != null) {
            try {
                handler.stopAndClear();
                event.getGuild().getAudioManager().closeAudioConnection();
            } finally {
                event.replySuccess("잘있어요!");
            }
        } else {
            event.replyError("음성채널에 들어가있지 않아요.");
        }
    }
}
