package com.bmob.acl;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

import com.bmob.acl.adapter.BlogAdapter;
import com.bmob.acl.bean.Blog;
import com.bmob.acl.view.listview.SimpleFooter;
import com.bmob.acl.view.listview.SimpleHeader;
import com.bmob.acl.view.listview.ZrcListView;
import com.bmob.acl.view.listview.ZrcListView.OnItemClickListener;
import com.bmob.acl.view.listview.ZrcListView.OnStartListener;

public class MainActivity extends BaseActivity implements OnClickListener,OnItemClickListener{

	private ZrcListView listView;
	BlogAdapter adapter;
	
	List<Blog> lists = new ArrayList<Blog>();

	private Handler handler = new Handler();
	
	TextView tv_submit,tv_logout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		tv_logout = (TextView)findViewById(R.id.tv_logout);
		tv_logout.setOnClickListener(this);
		
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);
		listView = (ZrcListView) findViewById(R.id.zListView);
		// 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
		float density = getResources().getDisplayMetrics().density;
		listView.setFirstTopOffset((int) (50 * density));

		// 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
		SimpleHeader header = new SimpleHeader(this);
		header.setTextColor(0xff0066aa);
		header.setCircleColor(0xff33bbee);
		listView.setHeadable(header);

		// 设置加载更多的样式（可选）
		SimpleFooter footer = new SimpleFooter(this);
		footer.setCircleColor(0xff33bbee);
		listView.setFootable(footer);

		// 设置列表项出现动画（可选）
		listView.setItemAnimForTopIn(R.anim.topitem_in);
		listView.setItemAnimForBottomIn(R.anim.bottomitem_in);

		// 下拉刷新事件回调（可选）
		listView.setOnRefreshStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				queryAllBlogs(0);
			}
		});

		// 加载更多事件回调（可选）
		listView.setOnLoadMoreStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				queryMoreBlogs();
			}
		});
		
		listView.setOnItemClickListener(this);
		
		adapter = new BlogAdapter(this, lists);
		listView.setAdapter(adapter);
		
		listView.refresh(); // 主动下拉刷新
	}

	int curPage = 0;
	int limit = 10;
	
	/**
	 * 查询适合当前用户权限和角色的博客：这里的查询是结合当前用户所属的用户组（BmobRole）和博客的读写权限来查询符合条件的博客，
	 * 解释：如果某篇文章的读写权限是：只限于android组的人才能看，只限于博客发布者才能写，而你又正好处于android组（注册时候选择的），那么这时候你就能看见这篇博客了。
	 * 
	 * 不需要填写其他额外的查询条件，由服务端根据ACL和用户角色来自动分配数据
	 * @Title: queryAllBlogs
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void queryAllBlogs(final int page) {
		BmobQuery<Blog> query = new BmobQuery<Blog>();
		query.include("author,ACL");
		query.setLimit(limit);
		query.setSkip(page * limit);
		query.order("-createdAt");
		curPage = page;
		query.findObjects(this, new FindListener<Blog>() {

			@Override
			public void onSuccess(List<Blog> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					if(page==0){
						lists.clear();
					}
					lists.addAll(arg0);
					adapter.notifyDataSetChanged();
					if (arg0.size() < limit) {
						ShowToast("博客已加载完成");
					} else {
						listView.startLoadMore(); // 开启LoadingMore功能
					}
                    listView.setRefreshSuccess("加载成功"); // 通知加载成功
				}else{
					listView.setRefreshFail("暂无博客");
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						listView.setRefreshFail("加载失败");
					}
				}, 1000);
			}
		});
	}
	
	private void queryMoreBlogs() {
		BmobQuery<Blog> query = new BmobQuery<Blog>();
		query.count(this, Blog.class, new CountListener() {

			@Override
			public void onSuccess(int arg0) {
				// TODO Auto-generated method stub
				if (arg0 > lists.size()) {
					curPage++;
					queryAllBlogs(curPage);
				} else {
					 listView.stopLoadMore();
				}
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				 listView.stopLoadMore();
				 ShowToast("加载失败："+arg1);
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0==tv_logout){
			BmobUser.logOut(this);
			startAnimActivity(LoginActivity.class);
			finish();
		}else{
			Intent intent = new Intent(this, SubmitActivity.class);
			startActivityForResult(intent, SubmitActivity.REQUEST_CODE_SUBMIT);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case SubmitActivity.REQUEST_CODE_SUBMIT:
			if (resultCode == SubmitActivity.RESULT_CODE_SUBMIT) {
				listView.refresh();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(ZrcListView parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}
	
}
