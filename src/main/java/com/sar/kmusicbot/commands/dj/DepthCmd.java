package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.commands.DJCommand;

public class DepthCmd extends DJCommand {
    public DepthCmd(Bot bot) {
        super(bot);
        this.name = "depth";
        this.help = "곡의 깊이를 변경해요.";
        this.arguments = "1|0.75";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        if(event.getArgs().length() != 0) {
            if (Float.isNaN(Float.parseFloat(event.getArgs()))) {
                event.replyError("플로트만 지원돼요.");
            } else if (handler.getPlayer().getPlayingTrack() == null) {
                event.replyError("음악을 재생중이지 않고있어요.");
            } if(Float.parseFloat(event.getArgs()) > 1) {
                event.replyError("깊이는 1보다 클 수 없어요.");
            } if(Float.parseFloat(event.getArgs()) <= 0) {
                event.replyError("깊이는 0보다 작을 수 없어요.");
            } else {
                event.replySuccess("깊이는: " + "`" + event.getArgs() + "`");
            }
        } else {
            event.replyError("유효하지 않은 인수에요.");
        }

    }
}
