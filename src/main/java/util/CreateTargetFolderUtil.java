package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by skYeYe on 25.01.2015.
 */
public class CreateTargetFolderUtil {

    private static final Logger logger = LogManager.getLogger(CreateTargetFolderUtil.class);

    public void createTargetFolders() {
        if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            String path = CreateTargetFolderUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String poster = path + "poster";
            createFolder(Paths.get(poster.substring(1)));
            String portraits = path + "portraits";
            createFolder(Paths.get(portraits.substring(1)));
            String subtitles = path + "subtitles";
            createFolder(Paths.get(subtitles.substring(1)));
        }else if(System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0){
        }else if(System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0){
        //not implemented
        }
    }

    private void createFolder(Path path){
        if(Files.notExists(path)){
            File file = new File(path.toString());
            file.mkdir();
        }
    }
}
