package com.example.ajou.quizcard;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Ridwan on 7/10/16.
 */
public class CustomDialogResult extends Dialog implements View.OnClickListener {

    public MainActivity c;
    public Dialog d;
    public Button yes, no,share;

    int score;
    String msg;
    public CustomDialogResult(MainActivity a,int score, String msg) {
        super(a);
        this.c = a;
        this.score=score;
        this.msg=msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_result);

        TextView scoreTV = (TextView) findViewById(R.id.scoreTV);
        scoreTV.setText(score+"/"+c.questions.length);
        TextView resultTV = (TextView) findViewById(R.id.resultTV);
        resultTV.setText(msg);
        resultTV.setMovementMethod(new ScrollingMovementMethod());

        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        share = (Button) findViewById(R.id.btn_share);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        share.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                Intent intent = new Intent(c.getApplicationContext(), MainActivity.class);
                c.startActivity(intent);
                c.finish();
                break;
            case R.id.btn_no:
                c.finish();
                break;
            case R.id.btn_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "I got " + score + "/" + c.questions.length + " on " + c.quizCategory + " quiz from RV Quiz Card App";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                c.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            default:
                break;
        }
        dismiss();
    }
}