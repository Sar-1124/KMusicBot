/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
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
import net.dv8tion.jda.api.entities.Activity;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetgameCmd extends OwnerCommand
{
    public SetgameCmd(Bot bot)
    {
        this.name = "setgame";
        this.help = "봇이 플레이하는 게임을 설정해요.";
        this.arguments = "[ Playing | Stream | Listen | Watch ] [게임이름]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        this.children = new OwnerCommand[]{
            new SetlistenCmd(),
            new SetstreamCmd(),
            new SetwatchCmd()
        };
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        String title = event.getArgs().toLowerCase().startsWith("플레이 중이에요.") ? event.getArgs().substring(7).trim() : event.getArgs();
        try
        {
            event.getJDA().getPresence().setActivity(title.isEmpty() ? null : Activity.playing(title));
            event.reply(event.getClient().getSuccess()+" **"+event.getSelfUser().getName()
                    +"** 은 "+(title.isEmpty() ? "더이상 아무것도 플레이 하지 않아요." : "현재 플레이중 `"+title+"`"));
        }
        catch(Exception e)
        {
            event.reply(event.getClient().getError()+" 게임을 설정할 수 없어요.");
        }
    }
    
    private class SetstreamCmd extends OwnerCommand
    {
        private SetstreamCmd()
        {
            this.name = "stream";
            this.aliases = new String[]{"twitch","streaming"};
            this.help = "봇이 플레이하고 있는 게임을 실시간 스트림으로 설정해요.";
            this.arguments = "<유저 이름> <게임>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<2)
            {
                event.replyError("트위치 사용자 이름과 '스트리밍'할 게임 이름을 포함하세요.");
                return;
            }
            try
            {
                event.getJDA().getPresence().setActivity(Activity.streaming(parts[1], "https://twitch.tv/"+parts[0]));
                event.replySuccess("**"+event.getSelfUser().getName()
                        +"** 현재 스트리밍중 `"+parts[1]+"`");
            }
            catch(Exception e)
            {
                event.reply(event.getClient().getError()+" 게임을 설정할 수 없어요.");
            }
        }
    }
    
    private class SetlistenCmd extends OwnerCommand
    {
        private SetlistenCmd()
        {
            this.name = "listen";
            this.aliases = new String[]{"listening"};
            this.help = "봇이 듣고 있는 게임을 설정해요.";
            this.arguments = "<제목>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyError("듣고있을 제목을 추가하세요.");
                return;
            }
            String title = event.getArgs().toLowerCase().startsWith("to") ? event.getArgs().substring(2).trim() : event.getArgs();
            try
            {
                event.getJDA().getPresence().setActivity(Activity.listening(title));
                event.replySuccess("**"+event.getSelfUser().getName()+"** 는 `"+title+"`를(을) 듣고있어요.");
            } catch(Exception e) {
                event.reply(event.getClient().getError()+" 게임을 설정할 수 없어요.");
            }
        }
    }
    
    private class SetwatchCmd extends OwnerCommand
    {
        private SetwatchCmd()
        {
            this.name = "watch";
            this.aliases = new String[]{"watching"};
            this.help = "봇이 보고 있는 게임을 설정해요.";
            this.arguments = "<제목>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyError("시청할 제목을 포함해 주세요!");
                return;
            }
            String title = event.getArgs();
            try
            {
                event.getJDA().getPresence().setActivity(Activity.watching(title));
                event.replySuccess("**"+event.getSelfUser().getName()+"** 은 `"+title+"`를 보고있어요.");
            } catch(Exception e) {
                event.reply(event.getClient().getError()+" 게임을 설정할 수 없어요.");
            }
        }
    }
}
