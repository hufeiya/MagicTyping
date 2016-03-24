package com.hufeiya.magictyping;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.hufeiya.magictyping.model.KeyInputInfo;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements KeyView.KeyOutputListener {

    @Bind(R.id.editText)
    EditText displayText;
    @Bind(R.id.button_0)
    KeyView button_0;
    @Bind(R.id.button_1)
    KeyView button_1;
    @Bind(R.id.button_2)
    KeyView button_2;
    @Bind(R.id.button_3)
    KeyView button_3;
    @Bind(R.id.button_4)
    KeyView button_4;
    @Bind(R.id.button_5)
    KeyView button_5;
    @Bind(R.id.button_6)
    KeyView button_6;
    @Bind(R.id.button_7)
    KeyView button_7;
    @Bind(R.id.button_8)
    KeyView button_8;
    @Bind(R.id.button_9)
    KeyView button_9;
    private List<KeyView> buttonList = new ArrayList<>();
    private static Map<String,String> keyMap = new HashMap<>();
    private static BlockingQueue<KeyInputInfo> keyInputQueue = new ArrayBlockingQueue<>(50);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        hideTheBottomBar();
        addAllKeyViewToList();
        setAllListener();
        setAllKey();
        setKeyMapping();
        KeyInfoProcessTask task = new KeyInfoProcessTask();
        task.execute();
    }

    private void setKeyMapping(){
        String[] rawKeyMaping = getResources().getStringArray(R.array.key_mapping);
        Map<String,String> tempMap = new HashMap<>();
        for(String temp : rawKeyMaping){
            String[] splitResult = temp.split("\\|",2);
            tempMap.put(splitResult[1],splitResult[0]);
        }
        for(Map.Entry<String,String> entry : tempMap.entrySet()){
            permutation("",entry.getKey(),entry.getValue());
        }
    }

    private void permutation(String prefix, String str,String value) {
        int n = str.length();
        if (n == 0){
            keyMap.put(prefix,value);
        }
        else {
            for (int i = 0; i < n; i++)
                permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n),value);
        }
    }

    private void addAllKeyViewToList() {
        buttonList.add(button_0);
        buttonList.add(button_1);
        buttonList.add(button_2);
        buttonList.add(button_3);
        buttonList.add(button_4);
        buttonList.add(button_5);
        buttonList.add(button_6);
        buttonList.add(button_7);
        buttonList.add(button_8);
        buttonList.add(button_9);
    }

    private void setAllListener() {
        for (KeyView button : buttonList) {
            button.setKeyOutputListener(this);
        }
    }

    private void setAllKey(){
        for(int i = 0;i < buttonList.size();i++){
            buttonList.get(i).setKey(i);
        }
    }

    private void hideTheBottomBar() {
        final View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Log.d("fuck", "visibility:  " + visibility);
                if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                    Log.d("fuck", "visibility in if:  " + visibility);
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            }
        });

    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    @Override
    public void getOutput(int key) {
        KeyInputInfo info = new KeyInputInfo((char)(key + '0'),System.currentTimeMillis());
        keyInputQueue.offer(info);
    }

    public class KeyInfoProcessTask extends AsyncTask<Void,String,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";
            long firstKeyInputTime = -1;
            while(true){
                try {
                    KeyInputInfo info = keyInputQueue.poll(200, TimeUnit.MILLISECONDS);
                    if(info == null ){
                        if(keyMap.containsKey(result)){
                            publishProgress(keyMap.get(result));
                            result = "";
                            firstKeyInputTime = -1;
                        }
                        continue;
                    }
                    if(firstKeyInputTime == -1){
                        result = String.valueOf(info.getKey());
                        firstKeyInputTime = info.getTime();
                    }else{
                        if(info.getTime() - firstKeyInputTime < 300){
                            result += info.getKey();
                        }else{
                            if(keyMap.containsKey(result)){
                                publishProgress(keyMap.get(result));
                            }
                            result = String.valueOf(info.getKey());
                            firstKeyInputTime = info.getTime();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            displayText.append(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
