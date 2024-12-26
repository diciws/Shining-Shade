package at.dici.shade;

import at.dici.shade.commandSys.CommandHandler;
import at.dici.shade.core.cache.*;
import at.dici.shade.core.connector.BotStartListener;
import at.dici.shade.core.mysql.MySqlClass;
import at.dici.shade.guild.BotInvite;
import at.dici.shade.guild.BotLeave;
import at.dici.shade.guild.Eastereggs;
import at.dici.shade.guild.botJoinSetup.Configurator;
import at.dici.shade.tasks.dailychallenges.cache.DCCache;
import at.dici.shade.twitchbot.TwitchBot;
import at.dici.shade.userapplications.controlpanel.ControlPanel;
import at.dici.shade.userapplications.controlpanel.StatsTracker;
import at.dici.shade.userapplications.ghostevidenceanalyzer.GeaListener;
import at.dici.shade.userapplications.inventory.InventoryManager;
import at.dici.shade.userapplications.phasmoGuessr.PgManager;
import at.dici.shade.userapplications.phasmoGuessr.cache.PhasmoGuessrCache;
import at.dici.shade.userapplications.phasmoQuiz.PQManager;
import at.dici.shade.userapplications.phasmoQuiz.cache.PhasmoQuizCache;
import at.dici.shade.userapplications.privacy.DataDeletionRequestListener;
import at.dici.shade.userapplications.profile.ProfileButtonListener;
import at.dici.shade.userapplications.profile.steamverify.SteamVerifyModalListener;
import at.dici.shade.utils.debug.DebugEventWatcher;
import at.dici.shade.utils.debug.SErrorHandler;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import lombok.Getter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

@Getter
public class Start {
	@Getter
	private static Start instance;
	public TwitchBot twitchBot;
	private final MySqlClass mySqlClass;
	private final SettingsCache settingsCache;
	private final UserCache userCache;
	private final LanguageCache languageCache;
	private final PhasmoGuessrCache phasmoGuessrCache;
	private final GuildCache guildCache;
	private final DCCache dailychallengesCache;
	private final SteamVerifyCache steamverifyCache;
	private final PhasmoQuizCache phasmoQuizCache;
	private ShardManager shardManager;
	public static String VERSION;
	public static final boolean TEST_MODE = false;
	private static final Class<Start> selfClass = Start.class;

	private PgManager phasmoGuessr;

	public static void main(String[] args) {
		Logger.log(LogLevel.BOT, "------------------------------------------");
		Logger.log(LogLevel.BOT, "STARTING Bot. . .");

		// VERSION META
		if(!TEST_MODE){
			String version;
			InputStream in = selfClass.getClassLoader()
					.getResourceAsStream("VERSION");

			if(in != null)
				try(Scanner scanner = new Scanner(in)) {
					version = scanner.nextLine();
				} catch(Exception e) {
					Logger.log(LogLevel.FATAL, "Keine Botversion-file gefunden in shadebot!"+e);
					version = "unknown";
				}
			else
				version = "unknown";

			VERSION = version;
			Logger.log(LogLevel.BOT, "Version: "+version);
		}else{
			String version;
			InputStream in = selfClass.getClassLoader()
					.getResourceAsStream("TEST-VERSION");

			if(in != null)
				try(Scanner scanner = new Scanner(in)) {
					version = scanner.nextLine();
				} catch(Exception e) {
					Logger.log(LogLevel.FATAL, "Keine Bot-test-version-file gefunden in shadebot!"+e);
					version = "unknown";
				}
			else
				version = "unknown";

			VERSION = version;
			Logger.log(LogLevel.BOT, "Version: "+version);
		}

		new Start();
	}

	/**
	 * Constructor of ShadeBot.
	 */
	private Start( ) {
		Start.instance = this;

		//Init MySqlClass
		this.mySqlClass = new MySqlClass( );

		//Connect MySqlClass
		this.getMySqlClass().connect();

		//Settings Cache
		this.settingsCache = new SettingsCache();

		//User Cache
		this.userCache = new UserCache();

		//Guild Cache
		this.guildCache = new GuildCache();

		//Language Cache
		this.languageCache = new LanguageCache();

		//DailyChallenges Cache
		this.dailychallengesCache = new DCCache();

		//PhasmoQuiz Cache
		this.phasmoQuizCache = new PhasmoQuizCache();

		//Steam-Verifcation Cache
		this.steamverifyCache = new SteamVerifyCache();


		/*
		 * other Cache components:
		 */
		//PhasmoGuessr Cache
		this.phasmoGuessrCache = new PhasmoGuessrCache();

		AsyncMySqlCache.addGlobalListener( ( ) -> {
			/*
			 * Global setup
			 */
			System.setProperty("file.encoding", "UTF-8"); //Trigger Linux to force use UTF-8
			Logger.log(LogLevel.BOT, "Building ShadeBot");

			DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(SettingsCache.getProperty( "bot.key" ));

			RestAction.setDefaultFailure(SErrorHandler::handleRestActionError);

			if(TEST_MODE) {
				builder.setToken(SettingsCache.getProperty("bot.test.key"));

				builder.addEventListeners(new DebugEventWatcher());

				builder.addEventListeners(new PQManager());
				Logger.log(LogLevel.BOT, "PhasmoQuiz registered!");

			}else{
				builder.setToken(SettingsCache.getProperty( "bot.key" ));
			}
			Logger.log(LogLevel.BOT, "Development version: " + TEST_MODE);

			builder.setStatus(OnlineStatus.ONLINE);
			builder.setAutoReconnect(true).setMaxReconnectDelay(32);
			builder.addEventListeners(new BotStartListener());

			builder.addEventListeners(new CommandHandler());

			/*
			 * Bot Guild setup
			 */
			builder.addEventListeners(new BotInvite());
			builder.addEventListeners(new BotLeave());
			builder.addEventListeners(new Configurator());
			Logger.log(LogLevel.BOT, "Bot Invite check registered!");

			/*
			 * Bot user registry setup
			 */

			phasmoGuessr = new PgManager();
			builder.addEventListeners(phasmoGuessr);
			Logger.log(LogLevel.BOT, "PhasmoGuessr registered!");
			builder.addEventListeners(new InventoryManager());
			Logger.log(LogLevel.BOT, "Inventory system registered!");
			builder.addEventListeners(new GeaListener());
			Logger.log(LogLevel.BOT, "added GEA as event listener");
			builder.addEventListeners(new DataDeletionRequestListener());
			Logger.log(LogLevel.BOT, "added privacy event listener");

			builder.addEventListeners(new Eastereggs());
			Logger.log(LogLevel.BOT,"Eastereggs registered");

			builder.addEventListeners(new ControlPanel());
			builder.addEventListeners(new StatsTracker());
			Logger.log(LogLevel.BOT,"ControlPanel & StatsTracker registered");

			builder.addEventListeners(new ProfileButtonListener());
			Logger.log(LogLevel.BOT, "Profile listener added!");

			builder.addEventListeners(new SteamVerifyModalListener());
			Logger.log(LogLevel.BOT, "Steam Verify added!");

			/*
			 * Init builder
			 */
			this.shardManager = builder.build();
			Logger.log(LogLevel.BOT, "Discord ShardManager built.");
			this.shutdownTask();

			if (TEST_MODE) {
				Logger.log(LogLevel.BOT, "Initializing TwitchBot...");
				this.twitchBot = new TwitchBot();
			}
			Logger.log(LogLevel.BOT, "Init done.");
		});

	}

	public MySqlClass getMySqlClass() {
		return this.mySqlClass;
	}

	private void shutdownTask() {

		new Thread(() -> {
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				while((line = reader.readLine()) != null) {
					if(line.equalsIgnoreCase("exit")) {
						if (this.phasmoGuessr != null) {
							phasmoGuessr.saveAllGames(true, b -> shutdown());
						}
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void shutdown() {
		if (this.shardManager != null) {
			Logger.log(LogLevel.FATAL, "Stopping Bot");
			this.shardManager.shutdown();
			System.exit(0);
		}
	}


}

