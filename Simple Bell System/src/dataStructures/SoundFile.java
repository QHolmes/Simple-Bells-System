/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import javafx.collections.ObservableMap;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Quinten Holmes
 */
public class SoundFile implements Serializable{
    private static final long serialVersionUID = 1;
    
    private Double playtime;
    private final String filePath;
    private String fileName;
    private String artist = "unKnown";
    private String album = "unKnown";
    
    public SoundFile(String filePath, String fileName){
        this.fileName = fileName;
        File file = new File(filePath);
        this.filePath = file.toURI().toString();
        getFileDuration();
    }
    
    public SoundFile(URL url, String fileName){
        this.fileName = fileName;
        this.filePath = url.toExternalForm();
        getFileDuration();
    }
    
    public SoundFile(File file){
        this.fileName = file.getName();
        this.filePath = file.toURI().toString();
        getFileDuration();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Returns the playtime of the given file in seconds.
     * @return 
     */
    public Double getPlayTime(){
        return playtime;
    }
    
    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }
    
    public Media getMedia(){
        return new Media(filePath);
    }
    
    @Override
    public String toString(){
        return fileName;
    }
    
    private void getFileDuration(){
        Media file = new Media(filePath); 
        MediaPlayer mediaPlayer = new MediaPlayer(file);
        mediaPlayer.setOnReady(() -> {
            playtime = file.getDuration().toSeconds();
            ObservableMap<String,Object> mp = file.getMetadata();
            try{
                if(mp.containsKey("title"))
                    fileName = mp.get("title").toString();
                if(mp.containsKey("artist"))
                    artist = mp.get("artist").toString();
                if(mp.containsKey("album"))
                    album = mp.get("album").toString();
                
            }catch (Exception e){}
            mediaPlayer.dispose();
        });
    }
    
     @Override
    public int hashCode() {
        return filePath.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SoundFile other = (SoundFile) obj;
        boolean b = this.filePath.equalsIgnoreCase(other.getFilePath());
        return b;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    
    
    
    
}
