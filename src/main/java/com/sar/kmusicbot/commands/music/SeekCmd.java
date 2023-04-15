package com.sar.kmusicbot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.audio.PlayerManager;
import com.sar.kmusicbot.commands.MusicCommand;
import com.sar.kmusicbot.settings.Settings;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;

import java.util.regex.Pattern;

public class SeekCmd extends MusicCommand {
    private AudioPlayer audioPlayer;
    private PlayerManager manager;
    CommandEvent guild;

    public SeekCmd(Bot bot) {
        super(bot);
        this.audioPlayer = audioPlayer;
        this.manager = manager;
        this.name = "seek";
        this.arguments = "<HH:MM:SS>|<MM:SS>|<SS>";
        this.help = "해당 시간에 무슨 노래가 재생되었는지 찾아요.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }


    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        if (handler.getPlayer().getPlayingTrack().isSeekable()) {
            AudioTrack currentTrack = handler.getPlayer().getPlayingTrack();
            Settings settings = event.getClient().getSettingsFor(event.getGuild());

            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                if (!event.getMember().getRoles().contains(settings.getRole(event.getGuild()))) {
                    if (currentTrack.getUserData(Long.class) != event.getAuthor().getIdLong()) {
                        event.replyError("**" + currentTrack.getInfo().title + "**에 대한것을 찾을 수 없어요.");
                        return;
                    }
                }
            }
        }
        String args = event.getArgs();
        long track_duration = handler.getPlayer().getPlayingTrack().getDuration();
        int seek_milliseconds = 0;
        int seconds;
        int minutes = 0;
        int hours = 0;

        if (Pattern.matches("^([0-9]\\d):([0-5]\\d):([0-5]\\d)$", args)) {
            hours = Integer.parseInt(args.substring(0, 2));
            minutes = Integer.parseInt(args.substring(3, 5));
            seconds = Integer.parseInt(args.substring(6));
        } else if (Pattern.matches("^([0-5]\\d):([0-5]\\d)$", args)) {
            minutes = Integer.parseInt(args.substring(0, 2));
            seconds = Integer.parseInt(args.substring(3, 5));
        } else if (Pattern.matches("^([0-5]\\d)$", args)) {
            seconds = Integer.parseInt(args.substring(0, 2));
        } else if(args.equals("0")) {
            seconds = 0;
        } else {
            event.replyError("잘못된 검색이에요.");
            return;
        }

        seek_milliseconds += hours * 3600000 + minutes * 60000 + seconds * 1000;
        if (seek_milliseconds <= track_duration) {
            handler.getPlayer().getPlayingTrack().setPosition(seek_milliseconds);
            event.replySuccess("성공적으로 찾았어요!");
        } else {
            event.replyError("현재 트랙은 그렇게 길지 않아요.");
        }
    }
}
