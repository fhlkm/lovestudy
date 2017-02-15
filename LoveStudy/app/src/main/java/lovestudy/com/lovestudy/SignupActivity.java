package lovestudy.com.lovestudy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

public class SignupActivity extends Activity {

    private EditText userName;
    private EditText password;
    private EditText email;
    private Button signup;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        init();
        signup.setOnClickListener(Listener);
    }

    private void init(){
        userName = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText2);
        email = (EditText)findViewById(R.id.register_email);
        signup = (Button)findViewById(R.id.button);
    }



    View.OnClickListener Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(userName.getText().toString().length() ==0|| password.getText().toString().length() ==0|| email.getText().toString().length() ==0){
                Util.showToast(SignupActivity.this,getResources().getString(R.string.please_check_info));
                return;
            }
            dialog = Util.showDialog(SignupActivity.this,R.string.wait,R.string.connecting_server);
            signupAction();
        }
    };

    private void  signupAction(){

        AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(userName.getText().toString());// 设置用户名
        user.setPassword(password.getText().toString());// 设置密码
        user.setEmail(email.getText().toString());// 设置邮箱
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                closeDialog();
                if (e == null) {
                    // 注册成功
                    Util.saveSP(getApplicationContext(),Util.email,email.getText().toString());
                    startActivity();
                    finish();
                } else {
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    Util.showToast(SignupActivity.this,e.getMessage());
                }
            }
        });
    }

    public void startActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void closeDialog(){
        if(null != dialog){
            dialog.dismiss();
        }
    }
}
