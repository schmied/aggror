package org.schmied.aggror;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.schmied.aggror.type.ArticlePk;
import org.schmied.app.V;
import org.slf4j.*;

public class AggrorTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

	private static void articlePk() {

		final String pk = Long.toHexString(V.RANDOM.nextLong());
		final String time = pk.substring(0, pk.length() - 11);
		final String sitePk = pk.substring(pk.length() - 11, pk.length() - 8);
		final String urlPathHash = pk.substring(pk.length() - 8, pk.length());

		LOGGER.info("1:  {} {} {} {}", pk, time, sitePk, urlPathHash);

		final ArticlePk apk = ArticlePk.valueOf(pk);

		assertEquals(new BigInteger(time, 16), //
				new BigInteger(apk.time().toString(), 16));

		assertEquals(new BigInteger(sitePk, 16), //
				new BigInteger(apk.sitePk().toString(), 16));

		assertEquals(new BigInteger(urlPathHash, 16), //
				new BigInteger(apk.urlPathHash().toString(), 16));

		final ArticlePk apk2 = ArticlePk.valueOf(apk.time().toString() + apk.sitePk().toString() + apk.urlPathHash());
		assertEquals(apk, apk2);

		LOGGER.info("2:  {} {} {} {}", apk.toString(), apk.time().toString(), apk.sitePk().toString(), apk.urlPathHash().toString());
	}

	@Test
	public void test() {
		for (int i = 0; i < 1000; i++)
			articlePk();
	}
}
