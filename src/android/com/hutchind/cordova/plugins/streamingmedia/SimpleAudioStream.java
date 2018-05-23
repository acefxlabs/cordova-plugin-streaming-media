package com.hutchind.cordova.plugins.streamingmedia;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SimpleAudioStream extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mMediaPlayer;
    private String TAG = getClass().getSimpleName();

    public SimpleAudioStream() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(TAG, "Starting player command...");
        //Bundle bundle = intent.getExtras();
        //if (bundle != null) {
            String mediaUrl = "http://radio.domi.org.ng:8000/domi_media";// bundle.getString("mediaUrl");
            play(mediaUrl);
        //}
        return super.onStartCommand(intent, flags, startId);
    }

    private void play(String mAudioUrl) {
            Log.d(TAG, "Play called...");
        if (mMediaPlayer == null) {
                Log.d(TAG, "Player initialized...");
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        try {
                Log.d(TAG, "Set player datasource...");
            mMediaPlayer.setDataSource(mAudioUrl); // Go to Initialized state
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnErrorListener(this);

            mMediaPlayer.prepareAsync();

            Log.d(TAG, "LoadClip Done");
        } catch (Exception t) {
            Log.d(TAG, t.getMessage());
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mMediaPlayer.reset();
        StringBuilder sb = new StringBuilder();
        sb.append("Media Player Error: ");
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                sb.append("Not Valid for Progressive Playback");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                sb.append("Server Died");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                sb.append("Unknown");
                break;
            default:
                sb.append(" Non standard (");
                sb.append(what);
                sb.append(")");
        }
        sb.append(" (").append(what).append(") ");
        sb.append(extra);
        Log.e(TAG, sb.toString());
        wrapItUp(RESULT_CANCELED, sb.toString());
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "Stream is prepared");
        mMediaPlayer.start();
    }

    public int getDuration() {
        return (mMediaPlayer != null) ? mMediaPlayer.getDuration() : 0;
    }

    public int getCurrentPosition() {
        return (mMediaPlayer != null) ? mMediaPlayer.getCurrentPosition() : 0;
    }

    public void seekTo(int pos) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(pos);
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            try {
                return mMediaPlayer.isPlaying();
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        return false;
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    public int getAudioSessionId() {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            mMediaPlayer = null;
        }
    }

    private void stop() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private void wrapItUp(int resultCode, String message) {
        Intent intent = new Intent();
        intent.putExtra("message", message);
        //setResult(resultCode, intent);
        //finish();
    }

    public void onBackPressed() {
        wrapItUp(RESULT_OK, null);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "PlayerService onBufferingUpdate : " + percent + "%");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stop();
        //if (mShouldAutoClose) {
        Log.v(TAG, "FINISHING ACTIVITY");
        wrapItUp(RESULT_OK, null);
        //}
    }
}
