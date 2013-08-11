package com.pybeta.huixiang.model;


public class Account {
	
	private long id;
	private String email;
	private String name;
	private long weibo_id;
	private long douban_id;
	private String avatar;
	private String password;
	private String douban_access_token;
	private String weibo_access_token;
	private String client_hash;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getWeibo_id() {
		return weibo_id;
	}

	public void setWeibo_id(long weibo_id) {
		this.weibo_id = weibo_id;
	}

	public long getDouban_id() {
		return douban_id;
	}

	public void setDouban_id(long douban_id) {
		this.douban_id = douban_id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDouban_access_token() {
		return douban_access_token;
	}

	public void setDouban_access_token(String douban_access_token) {
		this.douban_access_token = douban_access_token;
	}

	public String getWeibo_access_token() {
		return weibo_access_token;
	}

	public void setWeibo_access_token(String weibo_access_token) {
		this.weibo_access_token = weibo_access_token;
	}

	public String getClient_hash() {
		return client_hash;
	}

	public void setClient_hash(String client_hash) {
		this.client_hash = client_hash;
	}
}
