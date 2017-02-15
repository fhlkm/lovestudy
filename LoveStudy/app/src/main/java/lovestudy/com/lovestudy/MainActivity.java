package lovestudy.com.lovestudy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private List<AVObject> questionList = new ArrayList<>();
    private List<AVObject> updateList = new ArrayList<>();
    private ProgressDialog dialog;
    private ListView mlistView ;
    private EditText askeQuestion;
    private Button submitQuesiton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton email = (FloatingActionButton) findViewById(R.id.fab);
        email.setVisibility(View.GONE);
        new DrawerBuilder().withActivity(this).build();
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Email");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.check_bss);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.ask_question);
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.logout);
//create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        item3,
                        item4
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.i("Position: ",position+"");
                        return false;
                    }


                })
                .build();
        init();
    }

    private void init(){

        mlistView = (ListView)findViewById(R.id.mlist);
        askeQuestion = (EditText)findViewById(R.id.ask_question);
        submitQuesiton = (Button)findViewById(R.id.submit_question);
    }
    private void askQuestion(){

    }
    private void showQuestion(){

    }
    private void logout(){

    }
    private void retriveTeacherList(){
        AVQuery<AVObject> avQuery = new AVQuery<>(Util.bbsTableName);
        avQuery.orderByDescending("createdAt");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {

                closeDialog();
                if (e == null) {
                    if(null != list && list.size()>0){
                        questionList = list;
                        questionList.notifyDataSetChanged();
                    }
                } else {
                    e.printStackTrace();
                }
//                retrieveStudent();
            }

        });
    }

    private  class ListViewAdapter extends ArrayAdapter<String> {
        Context mContext;
        //        ArrayList<Question> mList = new ArrayList<>();
        int layoutResourceId;
        public  class ViewHolder {
            public TextView text;
            public TextView answer;
        }

        public ListViewAdapter(Context context , int layoutResourceId) {
            super(context,layoutResourceId);
            this.mContext = context;
            this.layoutResourceId = layoutResourceId;
//            this.mList = mList;
        }

        @Override
        public int getCount() {
            return questionList.size();
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = null;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(layoutResourceId, null);
            final ViewHolder mholder = new ViewHolder();
            mholder.text = (TextView)rowView.findViewById(R.id.question);
            mholder.answer = (TextView)rowView.findViewById(R.id.answer);

            AVObject mObject = questionList.get(position);
            String question = mObject.get(Util.QUESTION).toString();
            String answer = mObject.get(Util.ANSWER).toString();

            if(answer != null && answer.length()>0){
                String answerPerson = mObject.get(Util.ANSWERPerson).toString();
                mholder.answer.setVisibility(View.VISIBLE);
                mholder.answer.setText(answerPerson+":"+answer);
            }else{
                mholder.text.setLongClickable(true);
                mholder.text.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });
            }
            mholder.text.setText(Util.getSP(getApplicationContext(),Util.email)+":"+question);


            return rowView;
        }

    }

    public void customerDialog(final AVObject mobject){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom);
        dialog.setTitle("批注");

        final EditText mEditText = (EditText) dialog.findViewById(R.id.add_comment);

        Button dialogButton = (Button) dialog.findViewById(R.id.add_button);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobject.put(Util.COMMENT, mEditText.getText().toString());
                if(updateList.indexOf(mobject) ==-1) {
                    updateList.add(mobject);
                }
                dialog.dismiss();
                action();
            }
        });

        dialog.show();
    }

    public void action(){
        dialog.show();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeDialog();
                retriveTeacherList();
            }
        }, 3000);

            for (AVObject mObject : updateList) {
                if (mObject.get(Util.ANSWER) != null && mObject.get(Util.ANSWER).toString().length() > 0) {
                    mObject.saveInBackground();
                }
            }
        updateList.clear();
    }

    private void closeDialog(){
        if(null != dialog){
            dialog.dismiss();
        }
    }

}
