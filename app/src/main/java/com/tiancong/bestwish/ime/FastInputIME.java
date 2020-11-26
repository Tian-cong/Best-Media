package com.tiancong.bestwish.ime;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.tiancong.bestwish.R;
import com.tiancong.bestwish.media.AudioPlayer;
import com.tiancong.bestwish.media.AudioRecorder;

import java.util.Arrays;

public class FastInputIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private static final String TAG = "input";

    boolean isPlay = false;
    boolean isBack = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        android.media.AudioManager am = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    public View onCreateInputView() {
        KeyboardView keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        Keyboard keyboard = new Keyboard(this, R.xml.qwerty);

        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);

        Log.d(TAG, "onCreateInputView()");
        return keyboardView;
    }

    @Override
    public View onCreateCandidatesView() {
        Log.d(TAG, "onCreateCandidatesView()");
        return super.onCreateCandidatesView();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        Log.d(TAG, "onStartInputView()");
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        Log.d(TAG, "onFinishInputView: ");
    }

    @Override
    protected void onCurrentInputMethodSubtypeChanged(InputMethodSubtype newSubtype) {
        super.onCurrentInputMethodSubtypeChanged(newSubtype);
        Log.d(TAG, "onCurrentInputMethodSubtypeChanged()");
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
        if (!isPlay) {
            if (isBack) {
                AudioRecorder.getInstance().playBackSounds();
            } else {
                AudioRecorder.getInstance().playRecorderSounds();
            }
        }
        isPlay = true;
        Log.d(TAG, "onFinishInput()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onPress(int primaryCode) {
        Log.d(TAG, "onPress: " + primaryCode);
        switch (primaryCode) {
            case 32:
                AudioRecorder.getInstance().startRecorder();
                isPlay = false;
                break;
            case 22:
                AudioRecorder.getInstance().playRecorderSounds();
                isBack = false;
                break;
            case 33:
                AudioRecorder.getInstance().playBackSounds();
                isBack = true;
                break;
            case 44:
                Toast.makeText(this,"敬请期待", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onRelease(int primaryCode) {
        Log.d(TAG, "onRelease: " + primaryCode);
        if (AudioRecorder.getInstance().isRecording) {
            AudioRecorder.getInstance().stopRecorder();
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        Log.d(TAG, "onKey: primaryCode" + primaryCode);
        Log.d(TAG, "onKey: keyCodes"+ Arrays.toString(keyCodes));
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE: //删除键
                inputConnection.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_DONE: //完成键
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                //hideWindow(); //隐藏键盘
                break;
            case 32:
                // 录音
                Log.d(TAG, "onKey: 32");
                Toast.makeText(this,"sfafgg", Toast.LENGTH_SHORT).show();
            case 22:
                Log.d(TAG, "onKey: 22");

            case 33:
                Log.d(TAG, "onKey: 33");

            case 44:
                Log.d(TAG, "onKey: 44");

            default: //普通输入
                char code = (char) primaryCode;
                inputConnection.commitText(String.valueOf(code), 1); //可以对输入的字符串做 加密等等处理
        }
    }

    @Override
    public void onText(CharSequence text) {
        Log.d(TAG, "onText: ");
    }

    @Override
    public void swipeLeft() {
        Log.d(TAG, "swipeLeft: ");
    }

    @Override
    public void swipeRight() {
        Log.d(TAG, "swipeRight: ");
    }

    @Override
    public void swipeDown() {
        Log.d(TAG, "swipeDown: ");
    }

    @Override
    public void swipeUp() {
        Log.d(TAG, "swipeUp: ");
    }
}
