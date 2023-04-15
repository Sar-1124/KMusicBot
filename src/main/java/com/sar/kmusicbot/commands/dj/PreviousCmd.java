package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.audio.QueuedTrack;
import com.sar.kmusicbot.commands.DJCommand;
import net.dv8tion.jda.api.entities.GuildVoiceState;

public class PreviousCmd extends DJCommand {
    public PreviousCmd(Bot bot) {
        super(bot);
        this.name = "previous";
        this.help = "이전 곡을 재생해요.";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        GuildVoiceState userState = event.getMember().getVoiceState();

        if(handler.getPreviousTrack() != null) {
            if(handler.getNowPlaying(bot.getJDA()) != null) {
                try {
                    handler.getQueue().add(new QueuedTrack(handler.getPreviousTrack().makeClone(),
                            handler.getPreviousTrack().getUserData(Long.class) == null ? 0L
                                    : handler.getPreviousTrack().getUserData(Long.class)));

                    handler.getPlayer().stopTrack();
                } finally {
                    event.replySuccess("이전 곡을 불러왔어요.");
                }
            } else {
                try {
                    if(userState.inVoiceChannel()) {

                        event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());

                        handler.getQueue().add(new QueuedTrack(handler.getPreviousTrack().makeClone(),
                                handler.getPreviousTrack().getUserData(Long.class) == null ? 0L
                                        : handler.getPreviousTrack().getUserData(Long.class)));

                        handler.getPlayer().stopTrack();
                    } else {
                        event.replyError("음성 채널에 입장해 주세요.");
                    }
                } finally {
                    event.replySuccess("이전 곡을 불러왔어요.");
                }
            }
        } else {
            event.replyError("이전 곡이 없어요.");
        }
    }
}
