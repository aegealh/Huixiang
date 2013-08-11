package com.pybeta.huixiang.model;

public class Piece {

	private String content;
	private String link;
	private long id;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("[id=%d, content=%s, link=%s]", id, content, link);
	}
}
