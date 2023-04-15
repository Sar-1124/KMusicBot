package com.sar.kmusicbot.listeners;

import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.settings.RepeatMode;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InteractionListener extends ListenerAdapter {
    private Bot bot;
    private String success;

    public InteractionListener(Bot bot)
    {
        this.bot = bot;
        this.success = bot.getConfig().getSuccess();
    }

    public void onButtonClick(ButtonClickEvent event) {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        int volume = bot.getSettingsManager().getSettings(event.getGuild().getIdLong()).getVolume();

        assert handler != null;
        if(handler.getPlayer().getPlayingTrack() == null) {
            event.getMessage().editMessageComponents().queue();
        } else {
            switch (event.getComponentId()) {
                case "play:volume_minus":
                    bot.getSettingsManager().getSettings(event.getGuild()
                            .getIdLong()).setVolume(volume -= 10);

                    handler.getPlayer().setVolume(volume -=  10);

                    event.reply(success + "현재 볼륨은: " +  "`" +  handler.getPlayer().getVolume()
                            + "`").setEphemeral(true).queue();
                    break;
                case "play:volume_plus":
                    bot.getSettingsManager().getSettings(event.getGuild()
                            .getIdLong()).setVolume(volume += 10);

                    handler.getPlayer().setVolume(volume += 10);

                    event.reply(success + "현재 볼륨은: " +  "`" +  handler.getPlayer().getVolume()
                            + "`").setEphemeral(true).queue();
                    break;
                case "play:stop":
                    handler.getPlayer().stopTrack();

                    event.reply(success + "멈춤.").setEphemeral(true).queue();
                    break;
                case "play:play_pause":
                    if (handler.getPlayer().isPaused()) {
                        handler.getPlayer().setPaused(false);
                        event.reply(success + "일시정지 해제됨!").setEphemeral(true).queue();
                    } else {
                        handler.getPlayer().setPaused(true);
                        event.reply( success + "일시정지됨!").setEphemeral(true).queue();
                    }
                    break;
                case "play:skip":
                    handler.getPlayer().getPlayingTrack().stop();

                    event.reply(success + "스킵됨!").setEphemeral(true).queue();
                    break;
                case "play:repeat":
                    switch (bot.getSettingsManager().getSettings(event.getGuild().getIdLong()).getRepeatMode()) {
                        case OFF:
                            bot.getSettingsManager().getSettings(event.getGuild().getIdLong())
                                    .setRepeatMode(RepeatMode.TRACK);

                            event.reply("현재 반복 모드: " + "**" + RepeatMode.TRACK.getName() + "**").setEphemeral(true).queue();
                            break;
                        case QUEUE:
                            bot.getSettingsManager().getSettings(event.getGuild().getIdLong())
                                    .setRepeatMode(RepeatMode.OFF);

                            event.reply("현재 반복 모드: " + "**" + RepeatMode.OFF.getName() + "**").setEphemeral(true).queue();
                            break;
                        case TRACK:
                            bot.getSettingsManager().getSettings(event.getGuild().getIdLong())
                                    .setRepeatMode(RepeatMode.QUEUE);

                            event.reply("현재 반복 모드: " + "**" + RepeatMode.QUEUE.getName() + "**").setEphemeral(true).queue();
                            break;
                    }
            }
        }
    }
}
