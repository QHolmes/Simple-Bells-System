/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import dataStructures.schedules.DayScheduled;
import dataStructures.schedules.EventScheduled;
import dataStructures.schedules.WeekScheduled;
import dataStructures.templates.DayTemplate;
import dataStructures.templates.EventTemplate;
import dataStructures.templates.WeekTemplate;
import exceptions.IncorrectVersionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quinten Holmes
 */
public class RegionDataCore implements Serializable{
    private static final long serialVersionUID = 1;
    
    private final HashMap<Long, EventScheduled> eventScheduledMap = new HashMap();
    private final HashMap<Long, EventTemplate> eventTemplateMap = new HashMap();
    private final HashMap<Long, DayScheduled> dayScheduledMap = new HashMap();
    private final HashMap<Long, DayTemplate> dayTemplateMap = new HashMap();
    private final HashMap<Long, WeekScheduled> weekScheduledMap = new HashMap();
    private final HashMap<Long, WeekTemplate> weekTemplateMap = new HashMap();
    private final HashMap<Long, EventSegment> segmentMap = new HashMap();
    private final HashMap<Long, SoundFile> musicMap = new HashMap();
    private final HashMap<Long, SoundFile> bellMap = new HashMap();
    private final HashMap<Long, PlayList> playlistMap = new HashMap();
    
    /**
     * Returns the event scheduled with the given ID or null if non exists.
     * @param eventID ID of event to return
     * @return 
     */
    public EventScheduled getEventScheduled(long eventID){
        return eventScheduledMap.get(eventID);
    }
    
    /**
     * Adds the given EventSchedule to the Map and returns true if there are no 
     * conflicts, else it returns false. All new events must have a unique ID.
     * @param newEvent event to add to the Map
     * @return true if the event was added, else false
     */
    public boolean addEventScheduled(EventScheduled newEvent){
        if(eventScheduledMap.get(newEvent.getID()) == null){
            eventScheduledMap.put(newEvent.getID(), newEvent);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the Event with the given eventID.If there is an event with the
     * ID it will be removed and returns true, else it will return false. Removing
     * an event will also remove all event segments
     * @param eventID ID of event to remove
     * @return true if the event was found and removed.
     * @throws java.lang.InterruptedException
     */
    public boolean removeEventScheduled(long eventID) throws InterruptedException{
        EventScheduled e = eventScheduledMap.remove(eventID);
        
        if(e == null)
            return false;
        
        ArrayList<Long> segments = e.getSegments();
        segments.forEach(s -> removeSegment(s));
        return true;
    }
    
    /**
     * Returns a soft copy array list of all scheduled event IDs
     * @return 
     */
    public ArrayList<Long> getEventsScheduled(){
        ArrayList<Long> list = new ArrayList();
        list.addAll(eventScheduledMap.keySet());
        return list;
    }
    
    /**
     * Returns the event template with the given ID or null if non exists.
     * @param eventID ID of event to return
     * @return 
     */
    public EventTemplate getEventTemplate(long eventID){
        return eventTemplateMap.get(eventID);
    }
    
     /**
     * Adds the given EventTemplate to the Map and returns true if there are no 
     * conflicts, else it returns false. All new events must have a unique ID.
     * @param newEvent event to add to the Map
     * @return true if the event was added, else false
     */
    public boolean addEventTemplate(EventTemplate newEvent){
        if(eventTemplateMap.get(newEvent.getID()) == null){
            eventTemplateMap.put(newEvent.getID(), newEvent);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the Event with the given eventID.If there is an event with the
     * ID it will be removed and returns true, else it will return false. Will
     * also remove any attached event segments.
     * @param eventID ID of event to remove
     * @return true if the event was found and removed.
     * @throws java.lang.InterruptedException
     */
    public boolean removeEventTemplate(long eventID) throws InterruptedException{
        EventTemplate e = eventTemplateMap.remove(eventID);
        
        if(e == null)
            return false;
        
        ArrayList<Long> segments = e.getSegments();
        segments.forEach(s -> removeSegment(s));
        
        return true;
    }
    
    /**
     * Returns a soft copy array list of all event template IDs
     * @return 
     */
    public ArrayList<Long> getEventTemplates(){
        ArrayList<Long> list = new ArrayList();
        list.addAll(eventTemplateMap.keySet());
        return list;
    }
    
    /**
     * Returns the day scheduled with the given ID or null if non exists.
     * @param dayID ID of day to return
     * @return 
     */
    public DayScheduled getDayScheduled(long dayID){
        return dayScheduledMap.get(dayID);
    }
    
    /**
     * Adds the given DaySchedule to the Map and returns true if there are no 
     * conflicts, else it returns false. All new days must have a unique ID.
     * @param newDay day to add to the Map
     * @return true if the event was added, else false
     */
    public boolean addDayScheduled(DayScheduled newDay){
        if(dayScheduledMap.get(newDay.getID()) == null){
            dayScheduledMap.put(newDay.getID(), newDay);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the Day with the given dayID.If there is a day with the
     * ID it will be removed and returns true, else it will return false. Removing
     * a day will also cause it's events and event segments to be removed as well.
     * @param dayID ID of day to remove
     * @return true if the day was found and removed.
     * @throws java.lang.InterruptedException
     */
    public boolean removeDayScheduled(long dayID) throws InterruptedException{
        DayScheduled d = dayScheduledMap.remove(dayID);
        if(d == null)
            return false;
        
        //remove all events before removing day
        ArrayList<Long> events = d.getEvents();
        events.forEach(e-> {
            try {
                removeEventScheduled(e);
            } catch (InterruptedException ex) {}
        });
        
        return true;
    }
    
    /**
     * Returns a soft copy array list of all scheduled day IDs
     * @return 
     */
    public ArrayList<Long> getDaysScheduled(){
        ArrayList<Long> list = new ArrayList();
        list.addAll(dayScheduledMap.keySet());
        return list;
    }
    
    /**
     * Returns the day template with the given ID or null if non exists.
     * @param dayID ID of day to return
     * @return 
     */
    public DayTemplate getDayTemplate(long dayID){
        return dayTemplateMap.get(dayID);
    }
    
     /**
     * Adds the given DayTemplate to the Map and returns true if there are no 
     * conflicts, else it returns false. All new days must have a unique ID.
     * @param newDay day to add to the Map
     * @return true if the day was added, else false
     */
    public boolean addDayTemplate(DayTemplate newDay){
        if(dayTemplateMap.get(newDay.getID()) == null){
            dayTemplateMap.put(newDay.getID(), newDay);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the Day with the given dayID.If there is a day with the
     * ID it will be removed and returns true, else it will return false.
     * @param dayID ID of day to remove
     * @return true if the day was found and removed.
     * @throws java.lang.InterruptedException
     */
    public boolean removeDayTemplate(long dayID) throws InterruptedException{
        DayTemplate d = dayTemplateMap.remove(dayID);
        
        if(d == null)
            return false;
        
        ArrayList<Long> events = d.getEvents();
        events.forEach(e -> {
            try {
                removeEventTemplate(e);
            } catch (InterruptedException ex) {}
        });
        
        return true;
    }
    
    /**
     * Returns a soft copy array list of all day template IDs
     * @return 
     */
    public ArrayList<Long> getDaysTemplates(){
        ArrayList<Long> list = new ArrayList();
        list.addAll(dayTemplateMap.keySet());
        return list;
    }
    
    /**
     * Returns the week scheduled with the given ID or null if non exists.
     * @param weekID ID of week to return
     * @return 
     */
    public WeekScheduled getWeekScheduled(long weekID){
        return weekScheduledMap.get(weekID);
    }
    
    /**
     * Adds the given WeekSchedule to the Map and returns true if there are no 
     * conflicts, else it returns false. All new weeks must have a unique ID.
     * @param newWeek week to add to the Map
     * @return true if the week was added, else false
     */
    public boolean addWeekScheduled(WeekScheduled newWeek){
        if(weekScheduledMap.get(newWeek.getID()) == null){
            
            for(WeekScheduled w: weekScheduledMap.values()){
                while(true)
                    try {
                        if(newWeek.getDate().isEqual(w.getDate()))
                            return false;

                        break;
                    } catch (InterruptedException ex) {}
            }
            weekScheduledMap.put(newWeek.getID(), newWeek);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the Week with the given weekID.If there is an event with the
     * ID it will be removed and returns true, else it will return false. Removed
     * weeks will also trigger all days, which will remove all their events, which will 
     * remove all their event segments.
     * as well.
     * @param weekID ID of week to remove
     * @return true if the week was found and removed.
     * @throws java.lang.InterruptedException
     */
    public boolean removeWeekScheduled(long weekID) throws InterruptedException{
        WeekScheduled w = weekScheduledMap.remove(weekID);
        if(w == null)
            return false;
        
        DayScheduled[] days = w.getDays();
        
        for (DayScheduled day : days)
            removeDayScheduled(day.getID());
        
        return true;
    }
    
    /**
     * Returns a soft copy array list of all scheduled day IDs
     * @return 
     */
    public ArrayList<Long> getWeeksScheduled(){
        ArrayList<Long> list = new ArrayList();
        list.addAll(weekScheduledMap.keySet());
        return list;
    }
    
    /**
     * Returns the week template with the given ID or null if non exists.
     * @param weekID ID of week to return
     * @return 
     */
    public WeekTemplate getWeekTemplate(long weekID){
        return weekTemplateMap.get(weekID);
    }
    
     /**
     * Adds the given WeekTemplate to the Map and returns true if there are no 
     * conflicts, else it returns false. All new weeks must have a unique ID.
     * @param newWeek week to add to the Map
     * @return true if the week was added, else false
     */
    public boolean addWeekTemplate(WeekTemplate newWeek){
        if(weekTemplateMap.get(newWeek.getID()) == null){
            weekTemplateMap.put(newWeek.getID(), newWeek);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the Week with the given weekID.If there is a week with the
     * ID it will be removed and returns true, else it will return false.
     * This will also remove all days, which will remove all events, which will
     * remove all event segments. 
     * @param weekID ID of week to remove
     * @return true if the week was found and removed.
     * @throws java.lang.InterruptedException
     */
    public boolean removeWeekTemplate(long weekID) throws InterruptedException{
        WeekTemplate w = weekTemplateMap.remove(weekID);
        
        if(w == null)
            return false;
        
        DayTemplate[] days = w.getDays();
        
        for(DayTemplate day: days)
            removeDayTemplate(day.getID());
        
        
        return true;
    }
    
    /**
     * Returns a soft copy array list of all day template IDs
     * @return 
     */
    public ArrayList<Long> getWeekTemplates(){
        ArrayList<Long> list = new ArrayList();
        list.addAll(weekTemplateMap.keySet());
        return list;
    }
    
    /**
     * Returns the segment with the given ID or null if non exists.
     * @param segmentID ID of segment to return
     * @return 
     */
    public EventSegment getSegment(long segmentID){
        return segmentMap.get(segmentID);
    }
    
     /**
     * Adds the given segment to the Map and returns true if there are no 
     * conflicts, else it returns false. All new segments must have a unique ID.
     * @param newSegment segment to add to the Map
     * @return true if the segment was added, else false
     */
    public boolean addSegment(EventSegment newSegment){
        if(segmentMap.get(newSegment.getID()) == null){
            segmentMap.put(newSegment.getID(), newSegment);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the segment with the given segmentID. If there is a segment with the
     * ID it will be removed and returns true, else it will return false.
     * @param weekID ID of segment to remove
     * @return true if the segment was found and removed.
     */
    public boolean removeSegment(long weekID){
        return segmentMap.remove(weekID) != null;
    }
    
    /**
     * Returns the SoundFile with the given ID or null if non exists.
     * @param fileID ID of SoundFile to return
     * @return 
     */
    public SoundFile getMusicFile(long fileID){
        return musicMap.get(fileID);
    }
    
     /**
     * Adds the given SoundFile to the Map and returns true if there are no 
     * conflicts, else it returns false. All new SoundFiles must have a unique ID.
     * @param newSoundFile SoundFile to add to the Map
     * @return true if the SoundFile was added, else false
     */
    public boolean addMusicFile(SoundFile newSoundFile){
        if(musicMap.get(newSoundFile.getID()) == null){
            musicMap.put(newSoundFile.getID(), newSoundFile);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the SoundFile with the given fileID. If there is a week with the
     * ID it will be removed and returns true, else it will return false.
     * @param fileID ID of SoundFile to remove
     * @return true if the SoundFile was found and removed.
     */
    public boolean removeMusicFile(long fileID){
        SoundFile f = musicMap.remove(fileID);
        
        if(f == null)
            return false;
        
        weekScheduledMap.forEach( (i, w) -> {
            while(true)
            try {
                w.removeFile(w.getVersion(), fileID, this);
                return;
            } catch (InterruptedException | IncorrectVersionException ex) {}
        });
        
        weekTemplateMap.forEach( (i, w) -> {
            while(true)
            try {
                w.removeFile(w.getVersion(), fileID, this);
                return;
            } catch (InterruptedException | IncorrectVersionException ex) {}
        });
        
        playlistMap.forEach((i, p) -> {
            p.removeSoundFile(f);
        });
        
        return true;
    }
    
    /**
     * Returns a soft copy array list of all music file IDs
     * @return 
     */
    public ArrayList<Long> getMusicFiles(){
        ArrayList<Long> list = new ArrayList();
        list.addAll(musicMap.keySet());
        return list;
    }
    
    /**
     * Returns the SoundFile with the given ID or null if non exists.
     * @param fileID ID of SoundFile to return
     * @return 
     */
    public SoundFile getBellFile(long fileID){
        return bellMap.get(fileID);
    }
    
     /**
     * Adds the given SoundFile to the Map and returns true if there are no 
     * conflicts, else it returns false. All new SoundFiles must have a unique ID.
     * @param newSoundFile SoundFile to add to the Map
     * @return true if the SoundFile was added, else false
     */
    public boolean addBellFile(SoundFile newSoundFile){
        if(bellMap.get(newSoundFile.getID()) == null){
            bellMap.put(newSoundFile.getID(), newSoundFile);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the SoundFile with the given fileID. If there is a week with the
     * ID it will be removed and returns true, else it will return false.
     * @param fileID ID of SoundFile to remove
     * @return true if the SoundFile was found and removed.
     */
    public boolean removeBellFile(long fileID){
        SoundFile f = bellMap.remove(fileID);
        
        if(f == null)
            return false;
        
        weekScheduledMap.forEach( (i, w) -> {
            while(true)
            try {
                w.removeFile(w.getVersion(), fileID, this);
                return;
            } catch (InterruptedException | IncorrectVersionException ex) {}
        });
        
        weekTemplateMap.forEach( (i, w) -> {
            while(true)
            try {
                w.removeFile(w.getVersion(), fileID, this);
                return;
            } catch (InterruptedException | IncorrectVersionException ex) {}
        });
        
        return true;
    }
    
    /**
     * Returns a soft copy array list of all bell file IDs
     * @return 
     */
    public ArrayList<Long> getBellFiles(){
        ArrayList<Long> list = new ArrayList();
        list.addAll(bellMap.keySet());
        return list;
    }
    
    /**
     * Returns the PlayList with the given ID or null if non exists.
     * @param playlistID ID of PlayList to return
     * @return 
     */
    public PlayList getPlayList(long playlistID){
        return playlistMap.get(playlistID);
    }
    
     /**
     * Adds the given PlayList to the Map and returns true if there are no 
     * conflicts, else it returns false. All new PlayLists must have a unique ID.
     * @param newPlayList PlayList to add to the Map
     * @return true if the PlayList was added, else false
     */
    public boolean addPlayList(PlayList newPlayList){
        if(playlistMap.get(newPlayList.getID()) == null){
            playlistMap.put(newPlayList.getID(), newPlayList);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the PlayList with the given playlistID. If there is a week with the
     * ID it will be removed and returns true, else it will return false.
     * @param playlistID ID of PlayList to remove
     * @return true if the PlayList was found and removed.
     */
    public boolean removePlayList(long playlistID){
        PlayList l = playlistMap.remove(playlistID);
        
        if(l == null)
            return false;
        
        weekScheduledMap.forEach( (i, w) -> {
            while(true)
            try {
                w.removePlayList(w.getVersion(), playlistID, this);
                return;
            } catch (InterruptedException | IncorrectVersionException ex) {}
        });
        
        weekTemplateMap.forEach( (i, w) -> {
            while(true)
            try {
                w.removePlayList(w.getVersion(), playlistID, this);
                return;
            } catch (InterruptedException | IncorrectVersionException ex) {}
        });
        
        return true;
    }
    
    /**
     * Returns a soft copy array list of all play list IDs
     * @return 
     */
    public ArrayList<Long> getPlayLists(){
        ArrayList<Long> list = new ArrayList();
        list.addAll(playlistMap.keySet());
        return list;
    }
}
