/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
package com.sar.kmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.commands.DJCommand;
import com.sar.kmusicbot.settings.RepeatMode;
import com.sar.kmusicbot.settings.Settings;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RepeatCmd extends DJCommand
{
    public RepeatCmd(Bot bot)
    {
        super(bot);
        this.name = "repeat";
        this.help = "재생이 끝나면 음악을 대기열에 다시 재생합니다.";
        this.arguments = "[track|queue|off]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }
    
    // override musiccommand's execute because we don't actually care where this is used
    @Override
    protected void execute(CommandEvent event) 
    {
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        switch (event.getArgs()) {
            case "track":
                settings.setRepeatMode(RepeatMode.TRACK);
                event.replySuccess("반복 모드는 **track**으로 설정되어 있어요.");
                    break;
            case "queue":
                settings.setRepeatMode(RepeatMode.QUEUE);
                event.replySuccess("반복 모드는 **queue**으로 설정되어 있어요.");
                    break;
            case "off":
                settings.setRepeatMode(RepeatMode.OFF);
                event.replySuccess("반복 모드는 **off**으로 설정되어 있어요.");
                    break;
            default:
                event.replySuccess("현재 반복 모드는" + "**" + settings.getRepeatMode().getName() + "**" + "로 설정되어있어요.");
        }
    }

    @Override
    public void doCommand(CommandEvent event) { /* Intentionally Empty */ }
}
