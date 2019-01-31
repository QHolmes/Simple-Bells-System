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
    private Double playtime;
    private String filePath;
    private String fileName;
    
    public SoundFile(String filePath, String fileName){
        this.fileName = fileName;
        File file = new File(filePath);
        this.filePath = file.toURI().toString();
        Media md = new Media(this.filePath);
        playtime = md.getDuration().toSeconds();
    }
    
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
}
