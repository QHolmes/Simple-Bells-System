/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public class PlayList implements Serializable{
    private static final long serialVersionUID = 2;
    
    private final HashSet<SoundFile> soundFiles;
    private final HashSet<SoundFile> playFiles;
    private String playListName;
    private final Queue<SoundFile> lastPlayed;
    private final StampedLock queueLock;
    private final StampedLock hashLock;
    private final StampedLock nameLock;

    public PlayList(String playListName) {
        this.playListName = playListName;
        playFiles = new HashSet();
        lastPlayed = new LinkedList<>(); 
        soundFiles = new HashSet();
        queueLock = new StampedLock();
        nameLock = new StampedLock();
        hashLock = new StampedLock();
    }

    /**
     * Returns the given name of the playlist. 
     * @return name of playlist
     */
    public String getPlayListName() {
        long stamp = nameLock.readLock();
        try{
            return playListName;
        }finally{
            nameLock.unlockRead(stamp);
        }
    }

    /**
     * Changes the play list name and return a boolean value if the name is valid.Null names are not accepted, no other restrictions.
     * @param playListName the new desired name
     * @return true if if the named was changed
     */
    public boolean setPlayListName(String playListName) {
        if(playListName == null)
            return false;
        
        long stamp = nameLock.writeLock();
        try{
            this.playListName = playListName;
        }finally{
            nameLock.unlockWrite(stamp);
        }
        
        return true;
    }
    
    /**
     * Tries to add the given SoundFile to the play list and returns the success
     * of the attempt. If the list already has the SoundFile or the file is null
     * this will return false.
     * @param s
     * @return 
     */
    public boolean addSoundFile(SoundFile s){
        long stamp = hashLock.writeLock();
        try{
            boolean b = soundFiles.add(s);
            if(b)
                playFiles.add(s);
            return b;
        }finally{
            hashLock.unlockWrite(stamp);
        }
    }
    
    /**
     * Removes the given SoundFile from the playlist.
     * @param s SoundFile to remove
     * @return true if the SoundFile was removed from the play list
     */
    public boolean removeSoundFile(SoundFile s){
        long stamp = hashLock.writeLock();
        boolean b;
        try{
            b = soundFiles.remove(s);
        }finally{
            hashLock.unlockWrite(stamp);
        }
        checkQueueSize();
        return b;
    }
    
    /**
     * Returns a soft copy of all SoundFiles in the play list.
     * @return all SoundFiles
     */
    public ArrayList<SoundFile> getSoundFiles(){
        ArrayList<SoundFile> a = new ArrayList();
        a.addAll(soundFiles);
        return a;
    }

    /**
     * Returns a sudo random SoundFile from the play list. Recently played SoundFiles
     * are excluded from being returned.
     * @return SoundFile in play list
     */
    public SoundFile getRandomSoundFile(){
        if(soundFiles.isEmpty())
            return null;
        
        if(playFiles.isEmpty())
            playFiles.addAll(soundFiles);
        
        SoundFile choice = null;
        long stampQueue = queueLock.readLock();
        long stampHash = hashLock.readLock();
        try{
            int max = playFiles.size();
            Random rand = new Random();
            Iterator<SoundFile> iter;

            while(choice == null && !playFiles.isEmpty()){
                int num = rand.nextInt(max);
                iter = playFiles.iterator();
                try{
                    for(int i = 0; i <= num; i++)
                        choice = iter.next();
                    }catch (java.util.NoSuchElementException e){

                    }
                if(lastPlayed.contains(choice)){
                    playFiles.remove(choice);
                    choice = null;
                }
            }
            
            if(playFiles.isEmpty()){
                playFiles.addAll(soundFiles);
                choice = playFiles.iterator().next();
            }
                
        }finally{
            queueLock.unlockRead(stampQueue);
            hashLock.unlockRead(stampHash);
        }
            addToQueue(choice);
        
        return choice;
    }
    
    private void addToQueue(SoundFile s){
        if(s == null || soundFiles.size() <= 1)
            return;
        
        long stampQueue = queueLock.writeLock();
        try{
            lastPlayed.add(s);
            playFiles.remove(s);
        }finally{
            queueLock.unlockWrite(stampQueue);
        }
            
            checkQueueSize();            
        
    }
    
    private int maxQueueSize(){
        if(soundFiles.size() > 1)
            return (int) Math.ceil(soundFiles.size() / 2.0);
        else
            return 0;
    }
    
    private void checkQueueSize(){
        long stampQueue = queueLock.writeLock();
        try{
            while(lastPlayed.size() > maxQueueSize())
                playFiles.add(lastPlayed.poll());            
        }finally{
            queueLock.unlockWrite(stampQueue);
        }
    }
    
    @Override
    public String toString(){
        return playListName;
    }  
    
    public boolean isEmpty(){
        return soundFiles.isEmpty();
    }
    
    
    
}
