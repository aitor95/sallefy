package com.salle.android.sallefy.controller.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.salle.android.sallefy.controller.activities.MusicPlayerActivity;
import com.salle.android.sallefy.controller.location.UserLocation;
import com.salle.android.sallefy.controller.notifications.CustomNotification;
import com.salle.android.sallefy.controller.restapi.manager.TrackManager;
import com.salle.android.sallefy.model.LatLong;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.Track;

import java.util.ArrayList;
import java.util.Collections;
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
    private MusicCallback mCallbackMini;

    //Define el comportamiento al acabar de reproducir una cancion
    private MusicPlayerActivity.LoopButtonState loopMode = MusicPlayerActivity.LoopButtonState.LOOP_NOT_ACTIVATED;
    private boolean shufflePlay = false;
    private ArrayList<Integer> shufflePlaylistInices;

    //Para controlar si hay que reproducir la primera cancion al finalizar la playlist.
    private boolean loopPlaylist;

    //Para controlar la reproduccion en loop de una sola cancion.
    private boolean loopSong;

    //Inidca si se esta gestionando el callback de songFinished.
    private boolean songFinished;
    private Playlist mPlaylist;

    public int getPlaylistSize() {
        return mTracks != null ? mTracks.size() : 0;
    }

    public void songUpdateLike(boolean isLiked) {
        Track t = mTracks.get(currentTrack);
        t.setLiked(isLiked);
    }

    public void setLoopMode(MusicPlayerActivity.LoopButtonState mode) {
        this.loopMode = mode;
        //TODO: This could be optimized. Needs revision.
        switch (loopMode) {
            case LOOP_NOT_ACTIVATED:
                //En el interior se controla si hay shuffle o no.
                loopPlaylist = false;
                loopSong = false;
                break;

            case LOOP_PLAYLIST_ON:
                loopPlaylist = true;
                loopSong = false;
                break;

            case LOOP_SONG_ON:
                loopPlaylist = false;
                loopSong = true;
                break;
        }
        Log.d(TAG, "setLoopMode: Loop mode is " + loopMode.toString());
    }

    public void setShuffle(boolean shuffle) {
        this.shufflePlay = shuffle;
        shufflePlaylistInices = new ArrayList<Integer>();

        int size = mTracks.size();

        //Create the new list of indices
        for(int i = size - 1; i >= 0; i--) shufflePlaylistInices.add(i);

        //Remove the current track
        shufflePlaylistInices.remove(size - 1 - currentTrack);

        //Shuffle the playlist.
        Collections.shuffle(shufflePlaylistInices);
    }

    public void playlistOrSongDeleted() {
        stopMusic();
        mTracks.clear();
        mTracks = null;
        mPlaylist = null;
    }

    public Playlist getPlaylist() {
        return mPlaylist;
    }

    public boolean hasTrack() {
        return mTracks != null && !mTracks.isEmpty();
    }

    public class MusicBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved: TASK REMOVED!!");
        super.onTaskRemoved(rootIntent);
        stopService();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: DESTRoY");
        super.onDestroy();
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
    public void setCallbackMini(MusicCallback callback) {
        mCallbackMini = callback;
    }
    public void setCallback(MusicCallback callback) {
        mCallback = callback;
    }


    public void stopMusic(){
        pauseSong();
        CustomNotification.close();
    }
    public void stopService() {
        stopMusic();
        //Finaliza el servicio
        stopSelf();
    }

    public void loadSongs(Playlist playlist, Track initTrack, boolean startSong){
        mPlaylist = playlist;
        ArrayList<Track> tracks = (ArrayList<Track>) playlist.getTracks();

        mTracks = (List<Track>) tracks.clone();

        for (int i = mTracks.size() - 1; i >= 0; i--){
            if(mTracks.get(i).getId().intValue() == initTrack.getId()){
                currentTrack = i;
                break;
            }
        }

        if(startSong) {
            loadSong(initTrack);
        }
    }

    public void removePlaylist(){
        mTracks = null;
        mPlaylist = null;
        System.gc();
    }


    //Carga una cancion en streaming.
    public void loadSong(Track track) {

        LatLong latLong = UserLocation.getInstance(this).getLocation();

        TrackManager.getInstance(this).playTrack(track, latLong);

        boolean mediaPlayerWasNull = (mediaPlayer == null);
        if (mediaPlayerWasNull) {
            //Init class values
            playingBeforeInterruption = false;

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    onSongFinished();
                }
            });
        }

        //Crea una llista de reproduccio nova si no existeix
        if(mTracks == null) {
            mPlaylist = null;
            currentTrack = 0;
            mTracks = new ArrayList<>();
            mTracks.add(track);
        }

        try {
            //Si ya existia el media player, resetealo.
            if(!mediaPlayerWasNull) mediaPlayer.reset();

            mediaPlayer.setDataSource(track.getUrl());
            mediaPlayer.prepare();

            //Como no existia el player, define el onPrepared listener.
            if(mediaPlayerWasNull){
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mCallback != null) {
                            playSong();
                            try {
                                mCallback.onMusicPlayerPrepared();

                            }catch (Exception e){
                                //Ignoramos. Simplemente la activity del callback ya no existe.
                            }

                            mCallbackMini.onMusicPlayerPrepared();

                        }
                    }
                });
            }
        } catch(Exception e) {
            Log.d(TAG, "playStream: EXCEPTION" + e.getMessage());
        }
    }

    //Decide el comportamiento al acabar una cancion.
    private void onSongFinished() {
        songFinished = true;
        switch (loopMode) {
            case LOOP_NOT_ACTIVATED:
                loopPlaylist = false;
                loopSong = false;
                break;

            case LOOP_PLAYLIST_ON:
                loopPlaylist = true;
                loopSong = false;
                break;

            case LOOP_SONG_ON:
                loopPlaylist = false;
                loopSong = true;
                break;
        }
        //En el interior se controla si hay shuffle o no.
        changeTrack(1);
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
        if(mTracks == null){
            Log.d(TAG, "changeTrack: MTracks is null buddy!");
            return null;
        }
        int size = mTracks.size(); //Only query size once.

        if(!loopPlaylist && songFinished && currentTrack + offset >= size){
            //Dont reproduce more songs.
            pauseSong();
            updateSongData();
            updatePlayButton();
            //We done!
            return null;
        }

        int currentTrackMemorized = currentTrack;

        if (shufflePlay && !loopSong) {
            if(shufflePlaylistInices.isEmpty()){
                ///Mini hack para llenar la lista random otra vez.
                Log.d(TAG, "changeTrack: List empty, shuffling again!");
                setShuffle(true);

            }

            currentTrack = shufflePlaylistInices.remove(0);
            Log.d(TAG, "changeTrack: Track selected is " + currentTrack);

        } else if(!loopSong){

            Log.d(TAG, "changeTrack: Size is " + size);
            currentTrack = (currentTrack + offset) % size;

            //Si nos pasamos por encima, pon el indice al inicio.
            currentTrack = currentTrack >= size ? 0 : currentTrack;

            //Si nos pasamos por debajo, pon el indice al final.
            currentTrack = currentTrack < 0 ? (size - 1) : currentTrack;
        }

        //Nota: En el caso de LoopSong, no modificamos la current track, asi que nos querdamos igual.

        Track track = mTracks.get(currentTrack);
        loadSong(track);
        updateSongData();
        songFinished = false;

        return track;
    }

    //Tell everyPlayer to update the button.
    public void updatePlayButton(){
        if(mCallback != null)
            mCallback.onUpdatePlayButton();
        if(mCallbackMini != null)
            mCallbackMini.onUpdatePlayButton();
    }

    public void updateSongAfterEditing(Track t){
        for(int i = mTracks.size() - 1; i >= 0; i--){
            if(mTracks.get(i).getId()==t.getId().intValue()){
                mTracks.set(i,t);
                break;
            }
        }
        if(mPlaylist != null)
            mPlaylist.setTracks(mTracks);
    }

    //Tell everyPlayer to update the song data(prev/skip buttons pressed).
    public void updateSongData(){

        try{
            if(mCallback != null)
                mCallback.onSongChanged();
        }catch (Exception e){

        }

        try {
            if(mCallbackMini != null)
                mCallbackMini.onSongChanged();
        }catch (Exception e){

        }
    }



    public void pauseSong() {
        try {
            mediaPlayer.pause();
            CustomNotification.createNotification(this, getCurrentTrack(),false);

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
            CustomNotification.createNotification(this, getCurrentTrack(),true);

        } catch (Exception e) {
            Log.d(TAG, "failed to start media player." + e.getMessage() );
            e.printStackTrace();
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
            Log.d(TAG, "failed to toggle media player." + e.getMessage());
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
                playingBeforeInterruption = mediaPlayer.isPlaying();
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
