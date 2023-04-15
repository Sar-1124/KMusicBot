package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.commands.DJCommand;


public class JoinCmd extends DJCommand {
    public JoinCmd(Bot bot) {
        super(bot);
        this.name = "join";
        this.help = "봇이 지정된 채널을 연결해요.";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    public void doCommand(CommandEvent event)  {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();

        if(event.getArgs().isEmpty()) {
            if(event.getMember().getVoiceState().inVoiceChannel()) {
                if(handler.getPlayingTrack() != null) {
                    handler.getPlayer().setPaused(true);

                    event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());

                    handler.getPlayer().setPaused(false);

                    event.replySuccess(event.getGuild().getSelfMember().getVoiceState().getChannel().getAsMention() + "채널에 입장했어요!");
                } else {
                    event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());

                    event.replySuccess(event.getGuild().getSelfMember().getVoiceState().getChannel().getAsMention() + "채널에 입장했어요!");
                }
            } else {
                event.replyError("일치하는 채널이 없어요.");
            }
        } else {
            try {
                if(!event.getGuild().getVoiceChannelById(event.getArgs()).getId().isEmpty()
                        && event.getGuild().getVoiceChannelById(event.getArgs()).getIdLong() != 0)
                {
                    if(handler.getPlayingTrack() != null) {
                        handler.getPlayer().setPaused(true);

                        event.getGuild().getAudioManager().openAudioConnection(event.getGuild()
                                .getVoiceChannelById(event.getArgs()));

                        handler.getPlayer().setPaused(false);

                        event.replySuccess(event.getGuild().getSelfMember().getVoiceState().getChannel().getAsMention() + "채널에 입장했어요!.");
                    } else {
                        event.getGuild().getAudioManager().openAudioConnection(event.getGuild()
                                .getVoiceChannelById(event.getArgs()));

                        event.replySuccess(event.getGuild().getSelfMember().getVoiceState().getChannel().getAsMention() + "채널에 입장했어요!");
                    }
                } else {
                    event.replyError("알수 없는 채널이에요.");
                }
            } catch (Exception ignored) {}
        }
    }
}


