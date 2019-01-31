/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.io.File;
import javafx.scene.media.Media;

/**
 *
 * @author Quinten Holmes
 */
public class SoundFile {
    private int playtime;
    private String filePath;
    private String fileName;
    
    
    public int getPlayTime(){
        return playtime;
    }

    public int getPlaytime() {
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
}
