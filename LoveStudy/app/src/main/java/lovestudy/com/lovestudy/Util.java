package lovestudy.com.lovestudy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;



public class Util {
    public static String email ="email";

    public static String password ="password";
    public static String userName ="username";
    public static final String QUESTION ="questiontext";
    public static final String ANSWER ="answer";
    public static final String ANSWERPerson ="answerPerson";
    public static final String USEREMAIL = "email";
    public static final String STUDENTANSER="studentAnswer";
    public static final String COMMENT="comment";
    public static final String bbsTableName ="BBS";
    public static final String student_questionTableName ="StudentQuestionInfo";
    public static void showToast(Context mContext , String info){

        Toast.makeText(mContext, info, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context mContext , int info){
        String mtext =   mContext.getResources().getString(info);
        showToast(mContext,mtext);
    }

    public static void saveSP(Context context, String keyStr, String value){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(keyStr,value);
        editor.commit();
    }

    public static String getSP(Context context,String keyStr){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        return pref.getString(keyStr,null);
    }
    public static void clearSP(Context context){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().commit();
    }

    public static ProgressDialog showDialog(Context context, String title, String content) {
        final ProgressDialog progDailog = ProgressDialog.show(context,
                title,
                content , true);
        return progDailog;
    }

    public static ProgressDialog showDialog(Context context,int title,int content) {
        final ProgressDialog progDailog = ProgressDialog.show(context,
                context.getResources().getString(title),
                context.getResources().getString(content) , true);
        return progDailog;
    }
}
