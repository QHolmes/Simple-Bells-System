/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.io.Serializable;

/**
 *
 * @author Quinten Holmes
 */
public class Generator implements Serializable{
    protected static final long serialVersionUID = 4;
    
    private Long eventID = new Long(1);
    private Long dayID = new Long(1);
    private Long weekID = new Long(1);
    private Long segID = new Long(1);
    private Long fileID = new Long(1);
    private Long playlistID = new Long(1);
    
    
    private final String event = "";
    private final String day = "";
    private final String week = "";
    private final String seg = "";
    private final String file = "";
    private final String playlist = "";
    
    public long getNewEventID(){
        synchronized(event){
            return eventID++;
        }
    }
    
    public long getNewWeekID(){
        synchronized(week){
            return weekID++;
        }
    }
    
    public long getNewDayID(){
        synchronized(day){
            return dayID++;
        }
    }
    
    public long getNewSegID(){
        synchronized(seg){
            return segID++;
        }
    }
    
    public long getFileID(){
        synchronized(file){
            return fileID++;
        }
    }
    
    public long getNewPlayListID(){
        synchronized(playlist){
            return playlistID++;
        }
    }
    
    
}
