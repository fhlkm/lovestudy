package lovestudy.com.lovestudy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.avos.avoscloud.SaveCallback;
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
    ListViewAdapter mAdapter;
    private View mlistLayout;
    private View askLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton email = (FloatingActionButton) findViewById(R.id.fab);
        if(email != null)
        email.setVisibility(View.GONE);
        new DrawerBuilder().withActivity(this).build();
        //if you want to update the items at a later time it is recommended to keep it in a variable

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(Util.getSP(getApplicationContext(),Util.email));
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
                        if(position ==2){
                            dialog = Util.showDialog(MainActivity.this,R.string.wait,R.string.connecting_server);
                            mlistLayout.setVisibility(View.VISIBLE);
                            askLayout.setVisibility(View.GONE);
                            retriveTeacherList();
                        }else if(position ==3){
                            askQuestion();

                        }else if(position ==4){
                            logout();
                            finish();
                        }
                        return true;
                    }


                })
                .build();
        init();
    }

    private void init(){

        mlistView = (ListView)findViewById(R.id.mlist);
        askeQuestion = (EditText)findViewById(R.id.ask_question);
        submitQuesiton = (Button)findViewById(R.id.submit_question);
        submitQuesiton.setOnClickListener(Listener);
        mAdapter= new ListViewAdapter(this,R.layout.item_question);
        mlistLayout = (View)findViewById(R.id.list_layout);
        askLayout =(View)findViewById(R.id.ask_layout);
        askLayout.setVisibility(View.GONE);
        mlistView.setAdapter(mAdapter);
    }

    View.OnClickListener Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            dialog = Util.showDialog(MainActivity.this,R.string.wait,R.string.connecting_server);
            createQuestion(askeQuestion.getText().toString());
        }
    };
    private void askQuestion(){
        mlistLayout.setVisibility(View.GONE);
        askLayout.setVisibility(View.VISIBLE);

    }


    private void showQuestion(){

        askLayout.setVisibility(View.GONE);
        dialog = Util.showDialog(this,R.string.wait,R.string.connecting_server);
//        mlistView.setAdapter(mAdapter);
        retriveTeacherList();

    }
    private void logout(){
        Util.clearSP(getApplicationContext());
        Intent mIntent = new Intent(this, LoginActivity.class);
        startActivity(mIntent);

    }

    private void createQuestion(String quesiton){
        AVObject testObject = new AVObject(Util.bbsTableName);
        testObject.put(Util.QUESTION,quesiton);
        testObject.put(Util.ANSWER,"");
        testObject.put(Util.ANSWERPerson,"");
        testObject.put(Util.email,Util.getSP(getApplicationContext(),Util.email));
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Log.d("saved","success!");
                    mlistLayout.setVisibility(View.VISIBLE);
                    askLayout.setVisibility(View.GONE);
                    retriveTeacherList();

                }else{
                    Util.showToast(getApplicationContext(), e.getMessage());
                }
            }
        });
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
                        mAdapter.notifyDataSetChanged();
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
            int size = questionList.size();
            return size;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = null;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(layoutResourceId, null);
            final ViewHolder mholder = new ViewHolder();
            mholder.text = (TextView)rowView.findViewById(R.id.question);
            mholder.answer = (TextView)rowView.findViewById(R.id.answer);

            final AVObject mObject = questionList.get(position);
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
                        customerDialog(mObject);
                        return true;
                    }
                });
            }
            mholder.text.setText(mObject.get(Util.email)+":"+question);


            return rowView;
        }

    }

    public void customerDialog(final AVObject mobject){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom);
        dialog.setTitle("问题");

        final EditText mEditText = (EditText) dialog.findViewById(R.id.add_comment);

        Button dialogButton = (Button) dialog.findViewById(R.id.add_button);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobject.put(Util.ANSWER, mEditText.getText().toString());
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
