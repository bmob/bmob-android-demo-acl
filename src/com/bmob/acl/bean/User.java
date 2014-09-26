package com.bmob.acl.bean;

import cn.bmob.v3.BmobUser;

/**
  * @ClassName: User
  * @Description: TODO
  * @author smile
  * @date 2014-9-25 обнГ5:16:07
  */
public class User extends BmobUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer gender;

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}
	
}
