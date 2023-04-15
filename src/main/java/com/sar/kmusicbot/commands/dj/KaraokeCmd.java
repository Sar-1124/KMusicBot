package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.commands.DJCommand;
import com.sar.kmusicbot.settings.Settings;

public class KaraokeCmd extends DJCommand {

    public KaraokeCmd(Bot bot) {
        super(bot);
        this.name = "karaoke";
        this.help = "노래방 필터를 현재 트랙에 적용해요";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = false;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        Settings settings = bot.getSettingsManager().getSettings(event.getGuild());

        if(!settings.getKaraoke()) {
            handler.enableKaraoke(true);
            event.replySuccess("노래방 필터 활성화됨!");
        } else {
            handler.enableKaraoke(false);
            event.replySuccess("노래방 필터 비활성화됨!");
        }

    }
}
