package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.commands.DJCommand;

public class BassBoostCmd extends DJCommand {

    public BassBoostCmd(Bot bot) {
        super(bot);
        this.name = "bassboost";
        this.help = "현재 곡에대한 베이스를 부스트해요.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = false;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        if(!handler.getBassboostState()) {
            handler.enableBassboost(true);
            event.replySuccess("'bassboost'를 활성화했어요!");
        } else {
            handler.enableBassboost(false);
            event.replySuccess("'bassboost'를 비활성화했어요.");
        }

    }
}
