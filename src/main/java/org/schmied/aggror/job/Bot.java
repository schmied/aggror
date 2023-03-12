package org.schmied.aggror.job;

import java.util.concurrent.TimeUnit;

import org.knowm.sundial.Job;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.schmied.aggror.*;
import org.schmied.app.Log;
import org.slf4j.*;

@SimpleTrigger(repeatInterval = 13350, timeUnit = TimeUnit.SECONDS)
public class Bot extends Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

	public void doRun(final Site site) {
		try {
			site.download();
		} catch (final Exception e) {
			Log.warn(LOGGER, e);
		}
	}

	@Override
	public void doRun() throws JobInterruptException {
		LOGGER.info("Start bot run.");
		for (final Site site : Aggror.app().sites)
			doRun(site);
	}
}
