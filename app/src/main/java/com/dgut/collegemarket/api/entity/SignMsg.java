package com.dgut.collegemarket.api.entity;


public class SignMsg {
	Page<Sign> page;
	int ranking;
	int count;
	public Page<Sign> getPage() {
		return page;
	}
	public void setPage(Page<Sign> page) {
		this.page = page;
	}
	public int getRanking() {
		return ranking;
	}
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
