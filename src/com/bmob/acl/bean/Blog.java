package com.bmob.acl.bean;

import cn.bmob.v3.BmobObject;

/** 发表的博客
  * @ClassName: Blog
  * @Description: TODO
  * @author smile
  * @date 2014-9-25 下午5:44:26
  */
public class Blog extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title ;
	private String content;
	
	private User author;//发布者
	
	private String category;//分类
	
	
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
