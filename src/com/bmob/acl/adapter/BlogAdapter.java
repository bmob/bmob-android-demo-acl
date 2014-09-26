package com.bmob.acl.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmob.acl.R;
import com.bmob.acl.bean.Blog;
import com.bmob.acl.bean.User;

/**
 * 附近的人
 * 
 * @ClassName: BlackListAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-6-24 下午5:27:14
 */
public class BlogAdapter extends BaseListAdapter<Blog> {

	public BlogAdapter(Context context, List<Blog> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_blog, null);
		}
		final Blog blog = getList().get(arg0);
		TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
		TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
		TextView tv_class = ViewHolder.get(convertView, R.id.tv_class);
		
		tv_title.setText(blog.getTitle());
		tv_class.setText(blog.getCategory());
		
		User user = blog.getAuthor();
		tv_name.setText(user.getUsername());
		
		return convertView;
	}


}
