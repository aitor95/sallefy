package com.salle.android.sallefy.controller.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;
import java.util.List;


public class MusicService extends Service {

    public static final String TAG = MusicService.class.getName();

    //Reproductor de contenido en streaming.
    private MediaPlayer mediaPlayer;

    private final IBinder mBinder = new MusicBinder();

    //Para poder pedir el focus del audio.
    private AudioManager audioManager;

    //Flag para el control de las interrupciones de audio.
    private boolean playingBeforeInterruption;

    //Lista de canciones a reproducir
    private List<Track> mTracks;
    private int currentTrack;

    //Comunicacion con el cliente.
    private MusicCallback mCallback;

    public class MusicBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    //Funcion que llama el primer cliente que se quiere conectar al servicio.
    //Returns: El canal de comunicacion con el servicio.
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Interfaz que permite comunicar el servicio con el cliente.
    public void setCallback(MusicCallback callback) {
        mCallback = callback;
    }

    public void stopService() {
        pauseSong();

        //Finaliza el servicio
        stopSelf();
    }

    public void setSongList(ArrayList<Track> tracks){
        this.mTracks = tracks;
    }

    //Carga una cancion es streaming.
    public void loadSong(String url) {
        boolean mediaPlayerWasNull = (mediaPlayer == null);
        if (mediaPlayerWasNull) {

            //Init class values

            currentTrack = 0;
            playingBeforeInterruption = false;

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mCallback != null) {
                        mCallback.onSongFinishedPlaying();
                    }
                }
            });
        }

        try {
            //Si ya existia el media player, resetealo.
            if(!mediaPlayerWasNull) mediaPlayer.reset();

            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();

            //Como no existia el player, define el onPrepared listener.
            if(mediaPlayerWasNull){
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mCallback != null) {
                            mCallback.
                                    onMusicPlayerPrepared();
                            playSong();
                        }
                    }
                });
            }
        } catch(Exception e) {
            Log.d(TAG, "playStream: EXCEPTION" + e.getMessage());
        }
    }

    public int getAudioSession() {
        return mediaPlayer.getAudioSessionId();
    }

    public Track getCurrentTrack() {
        if(mTracks == null) return null;
        return mTracks.size() > 0 ? mTracks.get(currentTrack):null;
    }


    //Permite mover el current track offset veces.
    // Nota: offset puede ser negativo para reproducir canciones antiguas
    //       y puede ser positivo, para reproducir las siguientes de la lista.
    public Track changeTrack(int offset) {
        if(mTracks == null) return null;

        int size = mTracks.size(); //Only query size once.

        currentTrack = (currentTrack + offset) % size;

        //Si nos pasamos por encima, pon el indice al inicio.
        currentTrack = currentTrack >= size ? 0 : currentTrack;

        //Si nos pasamos por debajo, pon el indice al final.
        currentTrack = currentTrack < 0 ? (size - 1) : currentTrack;

        Track track = mTracks.get(currentTrack);
        loadSong(track.getUrl());

        return track;
    }


    private void nextTrack() {
        int size = mTracks.size(); //Only query size once.
        currentTrack = (currentTrack - 1) % size;

        //Si nos pasamos por debajo, pon el indice al final.
        currentTrack = currentTrack < 0 ? (mTracks.size() - 1) : currentTrack;
        changeTrack(currentTrack);
    }

    private void prevTrack() {
        int size = mTracks.size(); //Only query size once.
        currentTrack = (currentTrack + 1) % size;

        //Si nos pasamos por encima, pon el indice al inicio.
        currentTrack = currentTrack >= size ? 0 : currentTrack;
        changeTrack(currentTrack);
    }


    public void pauseSong() {
        try {
            mediaPlayer.pause();
           // showNotification();
        } catch (Exception e) {
            Log.d(TAG, "failed to ic_pause media player.");
        }
    }

    // Inicia la reproducion del audio.
    // Nota: Antes de llamar a play, se ha de cargar la cancion con playStream() y esperar
    // al callback onPrepared.
    public void playSong() {
        try {
            getAudioFocusAndPlay();
            //showNotification();
        } catch (Exception e) {
            Log.d(TAG, "failed to start media player.");
        }
    }

    public void togglePlayer() {
        try {
            if (mediaPlayer.isPlaying()) {
                pauseSong();
            } else {
                playSong();
            }
        }catch(Exception e) {
            Log.d(TAG, "failed to toggle media player.");
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    //Premite definir el instante actual de reproduccion de una cancion.
    //Time es en segundos
    public void setCurrentPosition(int time) {
        try {
            int seekToValue = time *1000;
            if (seekToValue > mediaPlayer.getDuration()){
                Log.d(TAG, "setCurrentPosition: Alguien ha intentado reproducir un instante" +
                        "de tiempo fuera de la longitud de la canción.");
                return;
            }
            mediaPlayer.seekTo(seekToValue);
        } catch (Exception e) {
            Log.d(TAG, "Failed to set the duration");
        }
    }

    //Devuelve el instante de tiempo actual de la cancion en segundos
    public int getCurrentPosition() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition()/1000;
            } else {
                return 0;
            }
        }catch(Exception e) {
            Log.d(TAG, "Failed to get the position");
        }
        return 0;
    }

    //Devuelve el numero de segundos que tiene una canción
    public int getMaxDuration() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getDuration()/1000;
            } else {
                return 0;
            }
        }catch(Exception e) {
            Log.d(TAG, "Failed to get the duration");
        }
        return 0;
    }


    //Request audio focus. If succes, play the song.
    private void getAudioFocusAndPlay () {
        audioManager = (AudioManager) this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start();
        }
    }


    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                if (mediaPlayer.isPlaying()) {
                    playingBeforeInterruption = true;
                } else {
                    playingBeforeInterruption = false;
                }
                pauseSong();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (playingBeforeInterruption) {
                    playSong();
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                pauseSong();
                audioManager.abandonAudioFocus(afChangeListener);
            }
        }
    };
}
