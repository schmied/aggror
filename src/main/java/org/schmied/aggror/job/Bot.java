package org.schmied.aggror.job;

import java.util.concurrent.TimeUnit;

import org.knowm.sundial.Job;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.schmied.aggror.*;
import org.slf4j.*;

@SimpleTrigger(repeatInterval = 13350, timeUnit = TimeUnit.SECONDS)
public class Bot extends Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

	public void doRun(final Site site) {
		try {
			site.download();
		} catch (final Exception e) {
			LOGGER.warn(e.getMessage());
		}
	}

	@Override
	public void doRun() throws JobInterruptException {
		System.out.println(">>>>>>>>>>>> START JOB " + Aggror.app() + " " + Aggror.app().sites.size());
		for (final Site site : Aggror.app().sites)
			doRun(site);
	}
}
