package com.example.ajou.quizcard;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Ridwan on 7/10/16.
 */
public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    public MainActivity c;
    public Dialog d;
    public Button yes, no;

    public CustomDialog(MainActivity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        TextView text = (TextView) findViewById(R.id.txt_dia);
        text.setText("There are "+(c.questions.length-c.answerSubmitted) +" answers that haven't been submitted. Do you want to submit all the answers?");
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.result();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}