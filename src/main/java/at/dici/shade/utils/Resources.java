package at.dici.shade.utils;

import at.dici.shade.Start;

import java.awt.*;
import java.util.Set;

public class Resources {

	//Botmain
	public static final Color color = Color.decode("#0a0a0d");
	public static final String LINK_BOT_INVITE = "https://discord.com/api/oauth2/authorize?client_id=1026383352162430996&permissions=280640&scope=bot";
	public static final String LINK_VOTE = "https://top.gg/bot/1026383352162430996/vote";
	public static final String LINK_SUPPORT_INVITE = "https://discord.gg/5FQXqyvdc5";


	// Channel IDs:
	public static final long CHANNEL_ADMINLOG_SUPPORT_SERVER = 1305236719393832990L;
	//public static final long CHANNEL_ADMIN_LEGENDS_TEST = 1038626675900043354L;
	private static final long CHANNEL_LOG = 1305236719393832990L;
	private static final long CHANNEL_LOG_TEST = 1104625975477678110L;

	//User IDs:
	public static Set<Long> BOT_ADMINS = Set.of(208885009514364928L);


	public static long getLogChannelId() {
		if (Start.TEST_MODE) return CHANNEL_LOG_TEST;
		else return CHANNEL_LOG;
	}
}
