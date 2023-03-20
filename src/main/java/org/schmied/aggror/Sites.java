package org.schmied.aggror;

import java.util.*;

import org.schmied.aggror.type.*;
import org.slf4j.*;

public class Sites {

	private static final Logger LOGGER = LoggerFactory.getLogger(Sites.class);

	private final SortedSet<Site> sites;
	private final Map<SitePk, Site> sitesByPk;
	private final SortedMap<String, Site> sitesByName;
	private final SitePk[] sitePks;

	public Sites(final Aggror app) throws Exception {
		sites = Site.sites(app);
		sitesByPk = new HashMap<>();
		sitesByName = new TreeMap<>();
		sitePks = new SitePk[sites.size() + 10];
		for (final Site site : sites) {
			LOGGER.info("{}", site.toString());
			sitesByPk.put(site.pk, site);
			sitesByName.put(site.name, site);
			sitePks[site.pk.value] = site.pk;
		}
	}

	public SortedSet<Site> sortedSet() {
		return sites;
	}

	public Site site(final SitePk sitePk) {
		return sitesByPk.get(sitePk);
	}

	public Site site(final String siteName) {
		return sitesByName.get(siteName);
	}

	public SitePk sitePk(final int sitePk) {
		IntBase.checkBits(sitePk, SitePk.BIT_LENGTH);
		return sitePks[sitePk];
	}
}
