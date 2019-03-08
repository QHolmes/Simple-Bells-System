/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import exceptions.IncorrectVersionException;
import helperClasses.Helper;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.concurrent.locks.StampedLock;
import javafx.collections.ObservableMap;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Quinten Holmes
 */
public class SoundFile implements Serializable{
    private static final long serialVersionUID = 2;
    
    private Double playtime;
    private final String filePath;
    private String fileName;
    private String artist = "unKnown";
    private String album = "unKnown";
    private final long ID;
    private int version = 0;
    private StampedLock lock = new StampedLock();
    
    public SoundFile(long ID, String filePath, String fileName){
        this.ID = ID;
        this.fileName = fileName;
        File file = new File(filePath);
        this.filePath = file.toURI().toString();
        getFileDuration();
    }
    
    public SoundFile(long ID, URL url, String fileName){
        this.ID = ID;
        this.fileName = fileName;
        this.filePath = url.toExternalForm();
        getFileDuration();
    }
    
    public SoundFile(long ID, File file){
        this.ID = ID;
        this.fileName = file.getName();
        this.filePath = file.toURI().toString();
        getFileDuration();
    }

    public void setFileName(int version, String fileName) throws InterruptedException, IncorrectVersionException {
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.version++;
            this.fileName = fileName;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    /**
     * Returns the playtime of the given file in seconds.
     * @return 
     */
    public Double getPlayTime() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return playtime; 
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    public String getFileName() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return fileName;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public String getFilePath() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return filePath;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    public Media getMedia() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return new Media(filePath);
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
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
        boolean b = false;
        boolean gate = false;
        
        while(!gate)
            try {
                b = this.filePath.equalsIgnoreCase(other.getFilePath());
                gate = true;
            } catch (InterruptedException ex) {}
        
        return b;
    }

    public String getArtist() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return artist;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setArtist(int version, String artist) throws IncorrectVersionException, InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.version++;
            this.artist = artist;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }

    public String getAlbum() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return album;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setAlbum(int version, String album) throws InterruptedException, IncorrectVersionException {
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.version++;
            this.album = album;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }

    public long getID(){
        return ID;
    }
    
    public int getVersion() throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            return version;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    protected void checkVersion(int version) throws IncorrectVersionException{
        if(version != this.version)
                throw new IncorrectVersionException(String.format("The given version %d does not match the current version %d.%n", 
                        version, this.version));
    }
    
    
}
