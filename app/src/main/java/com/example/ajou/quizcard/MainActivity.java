package com.example.ajou.quizcard;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int questionNumber = 0;
    int answerSubmitted = 0;
    TextView questionTv;
    TextView titleQuestionTV;
    ImageButton leftButton;
    ImageButton rightButton;
    Button skipButton;
    Button enterButton;

    EditText answerEt;
    TextView mark;

    Menu menu;


    String quizCategory = "Sample Quiz";
    String[] questions = {"What is the Smart phone platform most widely used? ", "What is the name of UI component that disappear automatically?", "What is the name of the super class of all Android UI component?", "In which file format all the resources for Android application is stored?", "Who is in charge of managing Android platform?", "What is the latest version number of Android?"};
    String[] answers = {"Android", "Toast", "View", "XML", "Google", "23"};
    String[] yourAnswers = new String[questions.length];
    boolean[] submitted = new boolean[questions.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTv = (TextView) findViewById(R.id.questionTV);
        questionTv.setMovementMethod(new ScrollingMovementMethod());

        titleQuestionTV = (TextView) findViewById(R.id.questionTitle);
        leftButton = (ImageButton) findViewById(R.id.leftButton);
        rightButton = (ImageButton) findViewById(R.id.rightButton);
        leftButton = (ImageButton) findViewById(R.id.leftButton);

        skipButton = (Button) findViewById(R.id.skipButton);
        enterButton = (Button) findViewById(R.id.enterButton);
        answerEt = (EditText) findViewById(R.id.answerText);

        mark = (TextView) findViewById(R.id.markQuestion);

        updateQuestion();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*if (id == R.id.action_about) {
            return true;
        }if (id == R.id.action_how) {
            return true;
        }
        */
        if (id == R.id.action_browse) {
            launchBrowseFile();
            return true;
        }

        if (id == R.id.action_submit) {
            if (answerSubmitted >= questions.length) {
                result();
            } else {
                notAllSubmitted();
            }
            return true;
        }
        if (id == R.id.action_save) {
            saveFile();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private static final int PICK_FILE = 1;

    public void launchBrowseFile() {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);


        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            startActivityForResult(Intent.createChooser(intent, "chooser"), PICK_FILE);
        } else {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/quiz.txt");
            customToast(R.layout.custom_warning_toast, "You didn't have any file manager. Loading file from" + f.getAbsolutePath());

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                readFile(br);
            } catch (FileNotFoundException e) {
                customToast(R.layout.custom_warning_toast, "There's no file at" + f.getAbsolutePath());
                e.printStackTrace();
            }

        }

    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void readFile(BufferedReader br) {
        try {
            ArrayList<String> question = new ArrayList<String>();
            ArrayList<String> answer = new ArrayList<String>();

            this.quizCategory = br.readLine();
            String questionTemp;
            while ((questionTemp = br.readLine()) != null) {
                question.add(questionTemp);
                answer.add(br.readLine());
            }
            if (question.size() <= 0) {
                customToast(R.layout.custom_warning_toast, "The imported file has wrong format");

                return;
            }

            this.questions = question.toArray(new String[0]);
            this.answers = answer.toArray(new String[0]);
            this.yourAnswers = new String[questions.length];
            this.submitted = new boolean[questions.length];


        } catch (Exception e) {
            Log.e("e", e.getMessage());
            customToast(R.layout.custom_warning_toast, "The imported file has wrong format");
        }
    }

    final int REQUEST_WRITE_STORAGE = 2;

    public void saveFile() {

        if (!isExternalStorageWritable()) {
            customToast(R.layout.custom_warning_toast, "The external storage is not writable");
            return;
        }

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
            return;
        }

        File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + quizCategory + ".txt");
        try {
            FileWriter fw = new FileWriter(output);
            fw.write(quizCategory);
            for (int i = 0; i < questions.length; i++) {
                fw.write("\n" + questions[i] + "\n" + answers[i]);
            }
            fw.close();

            customToast(R.layout.custom_correct_toast, "The file has been saved at" + output.getPath());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ea", e.toString());
            Log.e("e", e.getMessage());
            customToast(R.layout.custom_warning_toast, "There's a problem on writing the file");
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveFile();
                } else {
                    customToast(R.layout.custom_warning_toast, "The app was not allowed to write to your storage.");
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_FILE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                final Uri uri = data.getData();
                InputStream in = null;
                try {
                    in = getContentResolver().openInputStream(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (in != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    readFile(br);
                } else {
                    customToast(R.layout.custom_warning_toast, "The imported file is not found");
                }

            }
        }
        questionNumber = 0;
        updateQuestion();
    }


    public void updateQuestion() {
        if (questionNumber == 0) {
            leftButton.setEnabled(false);
            skipButton.setEnabled(true);
            rightButton.setEnabled(true);

        } else if (questionNumber >= questions.length - 1) {
            rightButton.setEnabled(false);
            skipButton.setEnabled(false);
            leftButton.setEnabled(true);
        } else {
            leftButton.setEnabled(true);
            rightButton.setEnabled(true);
            skipButton.setEnabled(true);
        }

        if (!submitted[questionNumber]) {
            mark.setText("Not Submitted");
            mark.setBackgroundColor(0xFFFFFFFF);
        } else if (yourAnswers[questionNumber].trim().equalsIgnoreCase(answers[questionNumber])) {
            mark.setText("Correct");
            mark.setBackgroundColor(0xFF00FF00);

        } else {
            mark.setText("Wrong");
            mark.setBackgroundColor(0xFFFF0000);

        }

        titleQuestionTV.setText("Question " + (questionNumber + 1) + "/" + questions.length);
        questionTv.setText(questions[questionNumber]);
        questionTv.scrollTo(0, 0);
        answerEt.setText(yourAnswers[questionNumber]);
        answerEt.setEnabled(!submitted[questionNumber]);
        enterButton.setEnabled(!submitted[questionNumber]);
    }

    public void leftClick(View v) {
        if (questionNumber <= 0) {
            questionNumber = 0;
            return;
        }
        yourAnswers[questionNumber--] = answerEt.getText().toString();
        updateQuestion();
    }

    public void rightClick(View v) {
        if (questionNumber >= questions.length - 1) {
            questionNumber = questions.length - 1;
            return;
        }
        yourAnswers[questionNumber++] = answerEt.getText().toString();
        updateQuestion();
    }

    public void submit(View v) {

        if (submitted[questionNumber]) {
            return;
        }
        yourAnswers[questionNumber] = answerEt.getText().toString();
        if (answerEt.getText().toString() == null || answerEt.getText().toString().trim().equalsIgnoreCase("")) {
            customToast(R.layout.custom_warning_toast, "You haven't answer the question");

            return;
        }
        answerSubmitted++;
        submitted[questionNumber] = true;
        updateQuestion();

        if (answerEt.getText().toString().trim().equalsIgnoreCase(answers[questionNumber])) {
            customToast(R.layout.custom_correct_toast, "You are correct");
        } else {
            customToast(R.layout.custom_error_toast, "You are wrong");
        }

        if (answerSubmitted >= questions.length) {
            result();
        } else if (questionNumber == questions.length - 1) {
            notAllSubmitted();
        }


    }


    public void customToast(int id, String msg) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(id,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    public void notAllSubmitted() {
        CustomDialog cdd = new CustomDialog(this);
        cdd.show();
        //builder.setMessage("There are "+(questions.length-answerSubmitted) +" answers that haven't been submitted. Do you want to submit all the answers?");


    }

    public void result() {
        answerEt.setEnabled(false);
        MenuItem submitAll = menu.findItem(R.id.action_submit);
        submitAll.setTitle("Result");

        StringBuilder sb = new StringBuilder();
        int score = 0;
        answerSubmitted = questions.length;
        for (int i = 0; i < questions.length; i++) {
            if (yourAnswers[i] == null || yourAnswers[i].trim().equalsIgnoreCase("")) {
                yourAnswers[i] = "-";
            }
            sb.append(questions[i]).append("\nYour answer: ").append(yourAnswers[i]).append("\nTrue Answer: ").append(answers[i]).append("\n\n");
            submitted[i] = true;
            try {
                if (yourAnswers[i].trim().equalsIgnoreCase(answers[i])) {
                    score++;
                }
            } catch (Exception e) {

            }
        }
        updateQuestion();
        final int finalScore = score;
        CustomDialogResult cdd = new CustomDialogResult(this,finalScore,sb.toString());
        cdd.show();
    }

}


