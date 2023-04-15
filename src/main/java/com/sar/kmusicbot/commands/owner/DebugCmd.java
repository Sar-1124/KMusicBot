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
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.commands.OwnerCommand;
import com.sar.kmusicbot.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class DebugCmd extends OwnerCommand 
{
    private final static String[] PROPERTIES = {"java.version", "java.vm.name", "java.vm.specification.version", 
        "java.runtime.name", "java.runtime.version", "java.specification.version",  "os.arch", "os.name"};
    
    private final Bot bot;
    
    public DebugCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "debug";
        this.help = "디버그 정보를 표시해요.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("```\n시스템 속성:");
        for(String key: PROPERTIES)
            sb.append("\n  ").append(key).append(" = ").append(System.getProperty(key));
        sb.append("\n\nKMusicBot 정보:")
                .append("\n  버전 = ").append(OtherUtil.getCurrentVersion())
                .append("\n  봇의 주인 = ").append(bot.getConfig().getOwnerId())
                .append("\n  접두사 = ").append(bot.getConfig().getPrefix())
                .append("\n  임시 접두사 = ").append(bot.getConfig().getAltPrefix())
                .append("\n  곡의 최대 길이 = ").append(bot.getConfig().getMaxSeconds())
                .append("\n  현재 재생중 이미지 = ").append(bot.getConfig().useNPImages())
                .append("\n  상태에 곡을 표시함 = ").append(bot.getConfig().getSongInStatus())
                .append("\n  채널에 남아있음 = ").append(bot.getConfig().getStay())
                .append("\n  평가사용 = ").append(bot.getConfig().useEval())
                .append("\n  업데이트 알림 = ").append(bot.getConfig().useUpdateAlerts());
        long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long used = total - (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        sb.append("\n\n런타임 정보:")
                .append("\n  총 메모리 = ").append(total)
                .append("\n  사용중인 메모리 = ").append(used);
        sb.append("\n\n디스코드 정보:")
                .append("\n  봇 ID = ").append(event.getJDA().getSelfUser().getId())
                .append("\n  서버수 = ").append(event.getJDA().getGuildCache().size())
                .append("\n  유저수 = ").append(event.getJDA().getUserCache().size());
        sb.append("\n```");
        
        if(event.isFromType(ChannelType.PRIVATE) 
                || event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES))
            event.getChannel().sendFile(sb.toString().getBytes(), "debug_information.txt").queue();
        else
            event.reply("디버그 정보에요. " + sb.toString());
    }
}
