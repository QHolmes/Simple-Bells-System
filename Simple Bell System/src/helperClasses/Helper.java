/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperClasses;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public class Helper {
    
    public static String dayOfWeekName(int i){
        switch(i){
            case(1):
                return "Sunday";
            case(2):
                return "Monday";
            case(3):
                return "Tuesday";
            case(4):
                return "Wednesday";
            case(5):
                return "Thursday";
            case(6):
                return "Friday";
            case(7):
                return "Saturday";
            default:
                return "Not a day";
        }
    }
    
    public static Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
    
    public static long getWriteLock(StampedLock lock) throws InterruptedException{
        long stamp = 0;
         
        Random rand = new Random();
        while(stamp == 0){
            stamp = lock.tryWriteLock(200, TimeUnit.MILLISECONDS);
            if(stamp != 0)
                break;
            Thread.sleep(rand.nextInt(100));
        }
        
        return stamp;
    }
    
    public static long getReadLock(StampedLock lock) throws InterruptedException{
        long stamp = 0;
         
        Random rand = new Random();
        while(stamp == 0){
            stamp = lock.tryReadLock(200, TimeUnit.MILLISECONDS);
            if(stamp != 0)
                break;
            Thread.sleep(rand.nextInt(100));
        }
        
        return stamp;
    }
    
}
