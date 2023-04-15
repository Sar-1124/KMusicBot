/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sar.kmusicbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.audio.QueuedTrack;
import com.sar.kmusicbot.commands.DJCommand;
import com.sar.kmusicbot.commands.MusicCommand;
import com.sar.kmusicbot.playlist.PlaylistLoader.Playlist;
import com.sar.kmusicbot.utils.FormatUtil;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlayCmd extends MusicCommand
{
    private final static String LOAD = "\uD83D\uDCE5"; // 📥
    private final static String CANCEL = "\uD83D\uDEAB"; // 🚫
    private final static String SPEAKER_LOW = "\uD83D\uDD08"; // 🔉
    private final static String SPEAKER_HIGH = "\uD83D\uDD09";
    private final static String LIST = "\uD83D\uDCC3"; // 📃
    private final static String RULER = "\uD83D\uDCCF"; // 📏
    private final static String STOP = "\u23F9\uFE0F"; // ⏹️
    private final static String PLAYPAUSE = "\u23EF\uFE0F"; // ⏯️️
    private final static String SKIP = "\u23E9"; // ⏩️️
    private final static String REPEAT = "\uD83D\uDD01"; // 🔁️️


    Logger log = LoggerFactory.getLogger("MusicBot");

    private final String loadingEmoji;
    
    public PlayCmd(Bot bot)
    {
        super(bot);
        this.loadingEmoji = bot.getConfig().getLoading();
        this.name = "play";
        this.arguments = "<제목|URL|subcommand>";
        this.help = "노래를 재생해요.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {

        if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty())
        {
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            if(handler.getPlayer().getPlayingTrack()!=null && handler.getPlayer().isPaused())
            {
                if(DJCommand.checkDJPermission(event))
                {
                    handler.getPlayer().setPaused(false);
                    event.replySuccess("**"+handler.getPlayer().getPlayingTrack().getInfo().title+"**를 다시 재생할게요.");
                }
                else
                    event.replyError("DJ만 플레이어의 일시 중지를 해제할 수 있어요.");
                return;
            }
            StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" 재생 명령어에요.:\n");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <곡 제목>` -유튜브의 첫 번째 결과를 재생해요.");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <URL>` - 인식한 노래, 재생 목록 또는 파일을 재생해요.");
            for(Command cmd: children)
                builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
            event.reply(builder.toString());
            return;
        }
        String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">") 
                ? event.getArgs().substring(1,event.getArgs().length()-1) 
                : event.getArgs().isEmpty() ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();
        event.reply(loadingEmoji+" **로딩중이에요...** `["+args+"]`", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m,event,false)));
    }

    private class ResultHandler implements AudioLoadResultHandler
    {
        private final Message m;
        private final CommandEvent event;
        private final boolean ytsearch;
        
        private ResultHandler(Message m, CommandEvent event, boolean ytsearch)
        {
            this.m = m;
            this.event = event;
            this.ytsearch = ytsearch;
        }
        
        private void loadSingle(AudioTrack track, AudioPlaylist playlist)
        {
            if(bot.getConfig().isTooLong(track))
            {
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+"재생 요청하신 (**"+track.getInfo().title+"**)은 허용된 길이보다 길어요: `"
                        +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`")).queue();
                return;
            }
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            EmbedBuilder mb = new EmbedBuilder();
            mb.setAuthor(track.getInfo().author, null);
            mb.setTitle(track.getInfo().title, track.getInfo().uri);
            mb.setColor(Color.red);
            if(event.getSelfMember().getVoiceState() != null) {
                mb.setFooter(SPEAKER_HIGH + " 채널: " + "#" + event.getSelfMember().getVoiceState().getChannel().getName() + ";" + LIST + "대기열 위치: " + track.getPosition() + ";" + RULER + " 트랙 길이: " + FormatUtil.formatTime(track.getDuration()) , event.getAuthor().getAvatarUrl());
            }
            if(track instanceof YoutubeAudioTrack) mb.setImage("https://img.youtube.com/vi/"+track.getIdentifier()+"/mqdefault.jpg");

            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
            String addMsg = FormatUtil.filter(event.getClient().getSuccess()+track.getInfo().title+"가 추가되었어요. **"
                    +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos==0?"곧 재생돼요.":+pos)+" 다음 대기열 위치에 추가되었어요.");
            if(playlist==null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
                try {
                    m.editMessageComponents(
                            ActionRow.of(Button.secondary("play:volume_minus", SPEAKER_LOW), Button.secondary("play:volume_plus", SPEAKER_HIGH), Button.secondary("play:stop", STOP), Button.secondary("play:play_pause", PLAYPAUSE), Button.secondary("play:skip", SKIP)), // 1st row below message
                            ActionRow.of(Button.secondary("play:repeat", REPEAT))
                    ).queue();
                    m.editMessage(event.getClient().getSuccess() + "`" + track.getInfo().title + "`"+"** 곡을 로드했어요.** " ).queue();
                    m.editMessageEmbeds(mb.build()).complete();
                } catch (Exception e) {
                      e.printStackTrace();
                }
            else
            {
                new ButtonMenu.Builder()
                        .setText(addMsg+"\n"+event.getClient().getWarning()+" This track has a playlist of **"+playlist.getTracks().size()+"** tracks attached. Select "+LOAD+" to load playlist.")
                        .setChoices(LOAD, CANCEL)
                        .setEventWaiter(bot.getWaiter())
                        .setTimeout(30, TimeUnit.SECONDS)
                        .setAction(re ->
                        {
                            if(re.getName().equals(LOAD))
                                m.editMessage(addMsg+"\n"+event.getClient().getSuccess()+" 로드됨. **"+loadPlaylist(playlist, track)+"** 추가 트랙들을!").queue();
                            else
                                m.editMessage(addMsg).queue();
                        }).setFinalAction(m ->
                        {
                            try{ m.clearReactions().queue(); }catch(PermissionException ignore) {}
                        }).build().display(m);
            }
        }
        
        private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude)
        {
            int[] count = {0};
            playlist.getTracks().stream().forEach((track) -> {
                if(!bot.getConfig().isTooLong(track) && !track.equals(exclude))
                {
                    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                    handler.addTrack(new QueuedTrack(track, event.getAuthor()));
                    count[0]++;
                }
            });
            return count[0];
        }
        
        @Override
        public void trackLoaded(AudioTrack track)
        {
            loadSingle(track, null);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist)
        {
            if(playlist.getTracks().size()==1 || playlist.isSearchResult())
            {
                AudioTrack single = playlist.getSelectedTrack()==null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                loadSingle(single, null);
            }
            else if (playlist.getSelectedTrack()!=null)
            {
                AudioTrack single = playlist.getSelectedTrack();
                loadSingle(single, playlist);
            }
            else
            {
                int count = loadPlaylist(playlist, null);
                if(count==0)
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" All entries in this playlist "+(playlist.getName()==null ? "" : "(**"+playlist.getName()
                            +"**) ")+"were longer than the allowed maximum (`"+bot.getConfig().getMaxTime()+"`)")).queue();
                }
                else
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getSuccess()+" Found "
                            +(playlist.getName()==null?"a playlist":"playlist **"+playlist.getName()+"**")+" with `"
                            + playlist.getTracks().size()+"` entries; added to the queue!"
                            + (count<playlist.getTracks().size() ? "\n"+event.getClient().getWarning()+" Tracks longer than the allowed maximum (`"
                            + bot.getConfig().getMaxTime()+"`) have been omitted." : ""))).queue();
                }
            }
        }

        @Override
        public void noMatches()
        {
            if(ytsearch)
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+"`"+event.getArgs()+"`에 대한 검색 결과가 없어요.")).queue();
            else
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+event.getArgs(), new ResultHandler(m,event,true));
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            if(throwable.severity==Severity.COMMON) {
                m.editMessage(event.getClient().getError() + " 로드중 오류가 발생했어요. " + throwable.getMessage()).queue();
                log.error("로드중 오류가 발생했어요. " + throwable.getMessage());
            } else {
                m.editMessage(event.getClient().getError() + " 트랙 로드중 오류가 발생했어요.").queue();
                log.error("트랙 로드중 오류가 발생했어요.: " + throwable.getMessage());
            }
        }
    }
}
