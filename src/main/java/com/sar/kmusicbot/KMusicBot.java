/*
 * Copyright 2016 John Grosh (jagrosh).
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
package com.sar.kmusicbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sar.kmusicbot.commands.admin.*;
import com.sar.kmusicbot.commands.dj.*;
import com.sar.kmusicbot.commands.general.*;
import com.sar.kmusicbot.commands.music.*;
import com.sar.kmusicbot.commands.owner.*;
import com.sar.kmusicbot.entities.Prompt;
import com.sar.kmusicbot.gui.GUI;
import com.sar.kmusicbot.listeners.InteractionListener;
import com.sar.kmusicbot.listeners.Listener;
import com.sar.kmusicbot.settings.SettingsManager;
import com.sar.kmusicbot.utils.OtherUtil;
import java.awt.Color;
import java.util.Arrays;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class KMusicBot
{
    public final static String PLAY_EMOJI  = "\u25B6"; // ▶
    public final static String PAUSE_EMOJI = "\u23F8"; // ⏸
    public final static String STOP_EMOJI  = "\u23F9"; // ⏹
    public final static Permission[] RECOMMENDED_PERMS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
                                Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
    public final static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // startup log
        Logger log = LoggerFactory.getLogger("Startup");
        
        // create prompt to handle startup
        Prompt prompt = new Prompt("KMusicBot", "nogui 모드로 전환합니다. -Dnogui=true 플래그를 포함하여 nogui 모드에서 수동으로 시작할 수 있습니다.");
        
        // get and check latest version
        String version = OtherUtil.checkVersion(prompt);
        
        // check for valid java version
        if(!System.getProperty("java.vm.name").contains("64"))
            prompt.alert(Prompt.Level.WARNING, "Java Version", "지원되는 Java 버전을 사용하지 않는 것 같아요. 64비트 java를 사용하세요.");
        
        // load config
        BotConfig config = new BotConfig(prompt);
        config.load();
        if(!config.isValid())
            return;
        
        // set up the listener
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);
        
        AboutCmd aboutCommand = new AboutCmd(Color.BLUE.brighter(),
                                "이 음악봇은 다음 링크에서 설치하실 수 있어요. [Github](https://github.com/sar-1124/KMusicBot) (v"+version+")",
                                new String[]{"고품질 음악", "FairQueue™ 기술"},
                                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6"); // 🎶
        
        // set up the command client
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(Long.toString(config.getOwnerId()))
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .setHelpWord(config.getHelp())
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(settings)
                .addCommands(aboutCommand,
                        new PingCmd(),
                        new SettingsCmd(bot),
                        
                        new LyricsCmd(bot),
                        new NowplayingCmd(bot),
                        new SeekCmd(bot),
                        new PlayCmd(bot),
                        new PlaylistsCmd(bot),
                        new QueueCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot),
                        new ShuffleCmd(bot),
                        new SkipCmd(bot),
//                        new TestCmd(),

                        new ForceRemoveCmd(bot),
                        new ForceskipCmd(bot),
                        new PreviousCmd(bot),
                        new SpeedCmd(bot),
                        new MoveTrackCmd(bot),
                        new PauseCmd(bot),
                        new PlaynextCmd(bot),
                        new RepeatCmd(bot),
//                        new DepthCmd(bot),
                        new SkiptoCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),
                        new BassBoostCmd(bot),
                        new KaraokeCmd(bot),
//                        new NightCoreCmd(bot),
                        new DisconnectCmd(bot),
                        new JoinCmd(bot),

                        new PrefixCmd(bot),
                        new SetdjCmd(bot),
                        new SettcCmd(bot),
                        new SetvcCmd(bot),
                        new SkipratioCmd(bot),
                        
                        new AutoplaylistCmd(bot),
                        new DebugCmd(bot),
                        new PlaylistCmd(bot),
                        new SetavatarCmd(bot),
                        new SetgameCmd(bot),
                        new SetnameCmd(bot),
                        new SetstatusCmd(bot),
                        new ShutdownCmd(bot)
                );
        if(config.useEval())
            cb.addCommand(new EvalCmd(bot));
        boolean nogame = false;
        if(config.getStatus()!=OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        if(config.getGame()==null)
            cb.useDefaultGame();
        else if(config.getGame().getName().equalsIgnoreCase("none"))
        {
            cb.setActivity(null);
            nogame = true;
        }
        else
            cb.setActivity(config.getGame());
        
        if(!prompt.isNoGUI())
        {
            try 
            {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
            } 
            catch(Exception e) 
            {
                log.error("GUI를 시작할 수 없습니다. 만약 당신이 "
                        + "서버 또는 표시할 수 없는 위치에서 실행 중이라면, "
                        + "-Dnogui=true 플래그를 사용하여 nogui 모드로 실행하십시오.");
            }
        }
        
        log.info("설정을 다음경로에서 로드했습니다. " + config.getConfigLocation());
        
        // attempt to log in and start
        try
        {
            JDA jda = JDABuilder.create(config.getToken(), Arrays.asList(INTENTS))
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ONLINE_STATUS)
                    .setActivity(nogame ? null : Activity.playing("로딩중..."))
                    .setStatus(config.getStatus()==OnlineStatus.INVISIBLE || config.getStatus()==OnlineStatus.OFFLINE 
                            ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(cb.build(), waiter, new Listener(bot), new InteractionListener(bot))
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);
        }
        catch (LoginException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "KMusicBot", ex + "\n올바른지 확인하세요. "
                    + "올바른 'config.txt' 파일 편집 및 사용 여부를 확인하세요. "
                    + "올바른 토큰('Secret Token'이 아니에요.)이에요.\n설정 경로: " + config.getConfigLocation());
            System.exit(1);
        }
        catch(IllegalArgumentException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "KMusicBot", "구성의 일부가 "
                    + "유효하지 않아요: " + ex + "\n설정 경로: " + config.getConfigLocation());
            System.exit(1);
        }
    }
}
