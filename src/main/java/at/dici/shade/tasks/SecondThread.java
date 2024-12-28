package at.dici.shade.tasks;

import at.dici.shade.Start;
import at.dici.shade.utils.PhasResources;
import at.dici.shade.utils.Util;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;

public class SecondThread extends Thread {
	@Override
	public void run() {
		Logger.log(LogLevel.BOT, "SecondThread started. Anxiously running into while(true)...");
		UpdateActivity updActivity = new UpdateActivity();
		while(true) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				Logger.log(LogLevel.ERROR, "SecondThread interrupted!");
			}
			if(System.currentTimeMillis() >= updActivity.interval + updActivity.lastTime) updActivity.execute();
		}
	}

	private static class UpdateActivity {
		private final String[] playing = {
				"with doof & dici Phasmophobia",
				"try /shade",
				"nothing, because I'm shy :c",
				"discovering myself",
				"with a sweet Deogen",
				"Spectrophobia!"
		};
		private final ShardManager sManager = Start.getInstance().getShardManager();
		private final long interval = 1000 * 25;
		public long lastTime;
		private int counter;

		public UpdateActivity(){
			counter = 0;
			lastTime = 0;
		}

		public void execute(){
			if(counter > playing.length) counter = 0;
			Start.getInstance().getShardManager().setActivity(Activity.playing(getString()));
			lastTime = System.currentTimeMillis();
		}

		private String getString() { // Wird noch schöner gelöst irgendwann
			if (Util.getRandomInt(2) == 0) {
				String[] facts = PhasResources.statusFacts;
				return facts[Util.getRandomInt(facts.length)];
			}
			counter++;
			if (counter >= playing.length) return "with " + sManager.getGuildCache().size() + " bones";
			else return playing[counter - 1];
		}

	}

}
