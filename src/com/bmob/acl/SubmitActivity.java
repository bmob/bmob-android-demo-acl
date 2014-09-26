package com.bmob.acl;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobRole;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.acl.bean.Blog;
import com.bmob.acl.bean.User;
import com.bmob.acl.view.HeaderLayout.onRightImageButtonClickListener;

/** 发布博客
  * @ClassName: SubmitActivity
  * @Description: TODO
  * @author smile
  * @date 2014-9-25 下午5:47:30
  */
public class SubmitActivity extends BaseActivity{

	EditText edit_title,edit_content;
	
	RadioGroup radio;
	
	RadioButton rb_all,rb_other;
	RadioButton rb_android,rb_ios,rb_me;
	
	LinearLayout layout;
	
	public final static int REQUEST_CODE_SUBMIT =1;
	public final static int RESULT_CODE_SUBMIT = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit);
		initTopBarForBoth("发表博客", R.drawable.base_action_bar_true_bg_selector, new onRightImageButtonClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				submit();
			}
		});
		initView();
	}
	
	private void initView(){
		layout = (LinearLayout)findViewById(R.id.layout);
		
		edit_title = (EditText)findViewById(R.id.edit_title);
		edit_content = (EditText)findViewById(R.id.edit_content);
		radio = (RadioGroup)findViewById(R.id.radio);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1==R.id.rb_all){
					layout.setVisibility(View.GONE);
				}else if(arg1==R.id.rb_other){
					layout.setVisibility(View.VISIBLE);
				}
			}
		});
		
		rb_all = (RadioButton)findViewById(R.id.rb_all);
		rb_other = (RadioButton)findViewById(R.id.rb_other);
		
		rb_android = (RadioButton)findViewById(R.id.rb_android);
		rb_ios = (RadioButton)findViewById(R.id.rb_ios);
		rb_me = (RadioButton)findViewById(R.id.rb_me);
	}
	
	/** 发布博客
	  * @Title: submit
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	private void submit(){
		String title = edit_title.getText().toString();
		String content = edit_content.getText().toString();
		
		if (TextUtils.isEmpty(title)) {
			ShowToast(R.string.toast_error_title);
			return;
		}

		if (TextUtils.isEmpty(content)) {
			ShowToast(R.string.toast_error_content);
			return;
		}
		
		Blog blog = new Blog();
		blog.setTitle(title);
		blog.setContent(content);
		
		BmobACL acl = new BmobACL();    //创建一个ACL对象
		
		//设置可读可写的权限--针对人
		if(rb_all.isChecked()){//所有人可见
			acl.setPublicReadAccess(true);    //设置所有人可读的权限
		}else if(rb_other.isChecked()){//其他
			//设置角色管理权限-针对组
			if(rb_android.isChecked()){//这篇博客只有属于android分区这个组里面的人才可以看得到
				BmobRole androidRole = new BmobRole("android");
				acl.setRoleReadAccess(androidRole, true);
				blog.setCategory("android");
			}else if(rb_ios.isChecked()){//这篇博客只有属于ios分区这个组里面的人才可以看得到
				BmobRole iosRole = new BmobRole("ios");
				acl.setRoleReadAccess(iosRole, true);
				blog.setCategory("ios");
			}else if(rb_me.isChecked()){//仅自己可见
				acl.setReadAccess(BmobUser.getCurrentUser(this), true); // 设置当前用户可读的权限
			}
		}
		acl.setWriteAccess(BmobUser.getCurrentUser(this), true);// 设置当前用户可写的权限
		//设置作者为当前用户
		blog.setAuthor(BmobUser.getCurrentUser(this, User.class));
		blog.setACL(acl);    //设置这条数据的ACL信息
		blog.save(this, new SaveListener() {
		    @Override
		    public void onSuccess() {
		        //添加成功
		    	ShowToast("文章发布成功");
		    	Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				setResult(RESULT_CODE_SUBMIT, intent);
				finish();
		    }
		    @Override
		    public void onFailure(int code, String msg) {
		        //添加失败
		    	ShowToast("文章发布失败："+msg);
		    }
		});
	}
	
}
