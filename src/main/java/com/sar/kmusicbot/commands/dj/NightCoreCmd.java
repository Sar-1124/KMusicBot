package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.commands.DJCommand;
import com.sar.kmusicbot.settings.Settings;

public class NightCoreCmd extends DJCommand {
    public NightCoreCmd(Bot bot) {
        super(bot);
        this.name = "nightcore";
        this.help = "현재 트랙에 나이트코어 필터를 적용해요.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = false;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        Settings settings = bot.getSettingsManager().getSettings(event.getGuild());

        if(!settings.getNightcore()) {
            settings.setNightcore(true);
            event.replySuccess("나이트코어가 활성화 되었어요!");
        } else {
            settings.setNightcore(false);
            event.replySuccess("나이트코어가 비활성화 되었어요.");
        }

    }
}
