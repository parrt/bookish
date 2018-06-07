package us.parr.bookish.model;

import us.parr.bookish.semantics.Article;

// todo: delete. don't gen main page for article
public class ArticleFile extends OutputModelObject {
	public Article article;

	public ArticleFile(Article article) {
		this.article = article;
	}

	public String getTitle() { return article.title; }
}
