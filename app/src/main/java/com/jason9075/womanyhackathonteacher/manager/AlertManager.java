package com.jason9075.womanyhackathonteacher.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;

import com.jason9075.womanyhackathonteacher.MyApp;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jason9075 on 2016/12/4.
 */

public class AlertManager {

    private final Context applicationContext;

    private static final long[] PATTERN = new long[]{0, 100, 1000};
    private boolean isDialogShow = false;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Uri defaultRingtoneUri;

    @Inject
    public AlertManager(Context context) {
        this.applicationContext = context;

        defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mediaPlayer = new MediaPlayer();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        try {
            mediaPlayer.setDataSource(context, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start(); // repeat
                }
            });
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlertView(String message) {
        isDialogShow = true;
        mediaPlayer.start();
        vibrator.vibrate(PATTERN, 0);
        new AlertDialog.Builder(((MyApp) applicationContext).getCurrentActivity())
                .setTitle("警報")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mediaPlayer.isPlaying())
                            mediaPlayer.pause();
                        vibrator.cancel();

                        Observable.just(1)
                                .delay(15, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Integer>() {
                                    @Override
                                    public void onCompleted() {
                                        isDialogShow = false;
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Integer integer) {

                                    }
                                });
                    }
                })
                .show();
    }

    public void checkIsUserTriggerAlertTemp(Context activity, Date lastDate) {
        if (((MyApp) applicationContext).getCurrentActivity() == null)
            return;
        if (!((MyApp) applicationContext).getCurrentActivity().equals(activity)) //此Activity 不是在畫面最上層的Activity時 不做事
            return;

        if ((new Date().getTime() - lastDate.getTime()) / 1000 < 10) {//少於10秒 還算是安全
            return;
        }
        if (!isDialogShow) {
            showAlertView("警報! 學生位置訊號不明");
        }
    }

    public void closeRingtonIfNeed() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }


}
