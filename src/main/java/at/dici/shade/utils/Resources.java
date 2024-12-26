package at.dici.shade.utils;

import at.dici.shade.Start;

import java.awt.*;
import java.util.Set;

public class Resources {

	//Botmain
	public static final Color color = Color.decode("#0a0a0d");
	public static final String LINK_BOT_INVITE = "";
	public static final String LINK_VOTE = "";
	public static final String LINK_SUPPORT_INVITE = "";


	// Channel IDs:
	public static final long CHANNEL_ADMINLOG_SUPPORT_SERVER = 1010101010101010101L;
	//public static final long CHANNEL_ADMIN_LEGENDS_TEST = 1010101010101010101L;
	private static final long CHANNEL_LOG = 1010101010101010101L;
	private static final long CHANNEL_LOG_TEST = 1010101010101010101L;

	//User IDs:
	public static Set<Long> BOT_ADMINS = Set.of(208885009514364928L);


	public static long getLogChannelId() {
		if (Start.TEST_MODE) return CHANNEL_LOG_TEST;
		else return CHANNEL_LOG;
	}
}
