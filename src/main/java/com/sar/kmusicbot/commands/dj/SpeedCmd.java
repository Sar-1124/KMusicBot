package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.commands.DJCommand;

public class SpeedCmd extends DJCommand {
    public SpeedCmd(Bot bot) {
        super(bot);
        this.name = "speed";
        this.help = "트랙의 속도를 변경해요.";
        this.arguments = "1|2.5";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        double args = 0;

        try {
            args = Double.parseDouble(event.getArgs());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(event.getArgs().length() != 0) {
            if(!Double.isNaN(args)) {
                if(args <= 0) {
                    event.replyError("숫자는 '0'보다 커야해요.");
                } else {
                    handler.setSpeed(event.getGuild(), Double.parseDouble(event.getArgs()));
                    event.replySuccess("현재 속도는" + "`" + args + "`" + "이에요.");
                }
            } else {
                event.replyError("숫자만 사용하실 수 있어요.");
            }

        } else {
            event.replySuccess("현재 속도는" + "`" + bot.getSettingsManager().getSettings(event.getGuild().getIdLong()).getSpeed() + "`" + "이에요.");
        }

    }
}
