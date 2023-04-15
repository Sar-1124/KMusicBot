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
package com.sar.kmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.commands.OwnerCommand;
import com.sar.kmusicbot.settings.Settings;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class AutoplaylistCmd extends OwnerCommand
{
    private final Bot bot;
    
    public AutoplaylistCmd(Bot bot)
    {
        this.bot = bot;
        this.guildOnly = true;
        this.name = "autoplaylist";
        this.arguments = "<name|NONE>";
        this.help = "서버의 기본 재생 목록을 설정해요.";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    public void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError()+" 재생 목록 이름 또는 NONE을 포함해야해요.");
            return;
        }
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setDefaultPlaylist(null);
            event.reply(event.getClient().getSuccess()+" 기본 재생 목록을 지웠어요. -> **"+event.getGuild().getName()+"**");
            return;
        }
        String pname = event.getArgs().replaceAll("\\s+", "_");
        if(bot.getPlaylistLoader().getPlaylist(pname)==null)
        {
            event.reply(event.getClient().getError()+"`"+pname+".txt`파일을 찾을수 없어요.");
        }
        else
        {
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setDefaultPlaylist(pname);
            event.reply(event.getClient().getSuccess()+" **"+event.getGuild().getName()+"**서버의 재생목록은`"+pname+"`이에요.");
        }
    }
}
