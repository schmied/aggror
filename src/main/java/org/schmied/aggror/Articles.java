package org.schmied.aggror;

import org.schmied.aggror.type.ArticlePk;

import com.github.benmanes.caffeine.cache.*;

public class Articles {

	private final Cache<ArticlePk, Article> articles;

	public Articles() {
		articles = Caffeine.newBuilder().maximumSize(10000).build();
	}

	public Article article(final ArticlePk articlePk) {
		return articles.get(articlePk, t -> null);
	}
}
