package lovestudy.com.lovestudy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

public class LoginActivity extends Activity {

    private EditText usrName;
    private EditText password;
    private Button login;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initUI();
        login.setOnClickListener(Listener);
    }

    private void initUI(){
        usrName = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText2);
        login = (Button)findViewById(R.id.button);
    }
    View.OnClickListener Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog = Util.showDialog(LoginActivity.this,R.string.wait,R.string.connecting_server);
            loginAction();
        }
    };


    private void loginAction(){
        AVUser.logInInBackground(usrName.getText().toString(), password.getText().toString(), new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                closeDialog();
                if(e ==null){
                    String email =  avUser.getEmail();
                    Log.i("Login email is: ",email);
                    Util.saveSP(getApplicationContext(), Util.email, email);
                    Util.saveSP(getApplicationContext(), Util.userName, usrName.getText().toString());
                    Util.saveSP(getApplicationContext(), Util.password, password.getText().toString());
                    startActivity();
                    finish();

                }else{
                    Util.showToast(LoginActivity.this,e.getMessage());
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
