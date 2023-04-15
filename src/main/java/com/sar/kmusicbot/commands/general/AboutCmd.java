/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sar.kmusicbot.commands.general;

import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class AboutCmd extends Command {
    private boolean IS_AUTHOR = true;
    private String REPLACEMENT_ICON = "+";
    private final Color color;
    private final String description;
    private final Permission[] perms;
    private String oauthLink;
    private final String[] features;

    public AboutCmd(Color color, String description, String[] features, Permission... perms)
    {
        this.color = color;
        this.description = description;
        this.features = features;
        this.name = "about";
        this.help = "봇의 정보를 불러와요.";
        this.guildOnly = false;
        this.perms = perms;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    public AboutCmd(Color color, String description, Permission[] perms, String[] features) {
        this.color = color;
        this.description = description;
        this.perms = perms;
        this.features = features;
    }

    public void setIsAuthor(boolean value)
    {
        this.IS_AUTHOR = value;
    }

    public void setReplacementCharacter(String value)
    {
        this.REPLACEMENT_ICON = value;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (oauthLink == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("초대링크를 만들 수 없어요. ", e);
                oauthLink = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.isFromType(ChannelType.TEXT) ? event.getGuild().getSelfMember().getColor() : color);
        builder.setAuthor(event.getSelfUser().getName() + "의 정보에요.", null, event.getSelfUser().getAvatarUrl());
        boolean join = !(event.getClient().getServerInvite() == null || event.getClient().getServerInvite().isEmpty());
        boolean inv = !oauthLink.isEmpty();
        String invline = "\n" + (inv ? (join ? ", or " : "") + "[`invite`](" + oauthLink + ") " : "") + "링크를 통해 서버에 저를 추가하세요!";
        String author = event.getJDA().getUserById(event.getClient().getOwnerId())==null ? "<@" + event.getClient().getOwnerId()+">"
                : event.getJDA().getUserById(event.getClient().getOwnerId()).getName();
        StringBuilder descr = new StringBuilder().append("안녕하세요! **").append(event.getSelfUser().getName()).append("**이에요.")
                .append( "\n`").append(event.getClient().getTextualPrefix()).append(event.getClient().getHelpWord())
                .append("`명령어를 사용하여 제 모든 명령어를 확인하세요.").append(join || inv ? invline : "").append("\n\n제가 포함한 기능은요!: ```css");
        for (String feature : features)
            descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? REPLACEMENT_ICON : event.getClient().getSuccess()).append(" ").append(feature);
        descr.append(" ```");
        builder.setDescription(descr);
        if (event.getJDA().getShardInfo() == null)
        {
            builder.addField("Stats", event.getJDA().getGuilds().size() + " servers\n1 shard", true);
            builder.addField("Users", event.getJDA().getUsers().size() + " unique\n" + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers().size()).sum() + " total", true);
            builder.addField("Channels", event.getJDA().getTextChannels().size() + " Text\n" + event.getJDA().getVoiceChannels().size() + " Voice", true);
        }
        else
        {
            builder.addField("상태",  "총 "+(event.getClient()).getTotalGuilds() + "개의 서버\n 참여중" + (event.getJDA().getShardInfo().getShardId() + 1)
                    + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
            builder.addField("현재 정보", event.getJDA().getUsers().size() + "명의 유저\n" + event.getJDA().getGuilds().size() + " 개의 서버", true);
            builder.addField("총 채널 정보", event.getJDA().getTextChannels().size() + "개의 텍스트채널\n" + event.getJDA().getVoiceChannels().size() + " 개의 음성채널", true);
        }
        builder.setFooter("마지막 재시작", null);
        builder.setTimestamp(event.getClient().getStartTime());
        event.reply(builder.build());
    }

}
