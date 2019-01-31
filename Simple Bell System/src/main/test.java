/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import audioSystem.AudioPlayer;
import audioSystem.ScheduledEvent;
import dataStructures.SoundFile;
import exceptions.StartDateInPast;
import exceptions.TimeOutOfBounds;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quinten Holmes
 */
public class test {
    
    private SoundFile song;
    private ScheduledEvent event;
    private final String FILEPATH = "C:\\Users\\Belva\\OneDrive\\Music\\MC Hammer\\Greatest Hits\\01 U Can't Touch This.m4a";
    private int ADDEDSEC = 5;
    
    public test(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 0, 30, 22, 47, 50);
        
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        System.out.println(format1.format(calendar.getTime()));
        Date startTime = calendar.getTime();
        
        song = new SoundFile(FILEPATH, "Test Song");
        try {
            event = new ScheduledEvent(startTime, false, null, false, null, true, -1.0, song);
            AudioPlayer player = AudioPlayer.getPlayer();
            player.setBellEvent(event);
        }catch (StartDateInPast ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
