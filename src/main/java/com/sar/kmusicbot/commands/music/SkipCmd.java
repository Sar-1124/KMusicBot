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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.audio.AudioHandler;
import com.sar.kmusicbot.commands.MusicCommand;
import net.dv8tion.jda.api.entities.User;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkipCmd extends MusicCommand 
{
    public SkipCmd(Bot bot)
    {
        super(bot);
        this.name = "skip";
        this.help = "현재 노래를 스킵하기 위한 투표를 해요.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(event.getAuthor().getIdLong()==handler.getRequester())
        {
            event.reply(event.getClient().getSuccess()+"**"+handler.getPlayer().getPlayingTrack().getInfo().title+"를(을) 건너뛰었어요.**");
            handler.getPlayer().stopTrack();
        }
        else
        {
            int listeners = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).count();
            String msg;
            if(handler.getVotes().contains(event.getAuthor().getId()))
                msg = event.getClient().getWarning()+" 이미 이 노래를 건너뛰기 위한 투표를 하셨어요. `[";
            else
            {
                msg = event.getClient().getSuccess()+" 이 노래를 건너뛰는 것에 투표하셨어요. `[";
                handler.getVotes().add(event.getAuthor().getId());
            }
            int skippers = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();
            int required = (int)Math.ceil(listeners * .55);
            msg+= skippers+" 투표자 수가, "+required+"/"+listeners+" 명 더 필요해요.]`";
            if(skippers>=required)
            {
                User u = event.getJDA().getUserById(handler.getRequester());
                msg+="\n"+event.getClient().getSuccess()+"**"+handler.getPlayer().getPlayingTrack().getInfo().title
                    +"**을(를) 건너뛰었어요."+(handler.getRequester()==0 ? "" : (u==null ? "someone" : "**"+u.getName()+"**")+" (요청자. "+")");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg);
        }
    }
    
}
