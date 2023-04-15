package com.sar.kmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.commands.AdminCommand;
import com.sar.kmusicbot.settings.Settings;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class SkipratioCmd extends AdminCommand
{
    public SkipratioCmd(Bot bot)
    {
        this.name = "setskip";
        this.help = "서버별 스킵 백분율을 설정해요.";
        this.arguments = "<0 - 100>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(CommandEvent event)
    {
        try
        {
            int val = Integer.parseInt(event.getArgs().endsWith("%") ? event.getArgs().substring(0,event.getArgs().length()-1) : event.getArgs());
            if( val < 0 || val > 100)
            {
                event.replyError("사용할 값은 0에서 100 사이여야 해요.");
                return;
            }
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setSkipRatio(val / 100.0);
            event.replySuccess("스킵 백분율이 다음으로 설정되었어요. 청취자의 ` %`. *" + + val + event.getGuild().getName() + "*");
        }
        catch(NumberFormatException ex)
        {
            event.replyError("0에서 100 사이의 정수를 포함하십시오(기본값은 55). 이 숫자는 노래를 스킵하기 위해 투표해야 하는 청취 사용자의 비율입니다.");
        }
    }
}