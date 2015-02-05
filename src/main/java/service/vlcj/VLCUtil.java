package service.vlcj;

import exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.File;
import java.io.IOException;

public class VLCUtil {

    private static final Logger logger = LogManager.getLogger(VLCUtil.class);
    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * VLC lib discovery method for current OS.
     */
    public static void discover() {
        String osName = "";

        if (isMac()) osName = "Running on OS X";
        if (isUnix()) osName = "Running on Unix";
        if (isWindows()) osName = "Running on Windows";

        logger.info(osName);

        new NativeDiscovery().discover();
    }

    /**
     * Checks if VLC is installed
     *
     * @return false if not.
     */
    public static boolean isVlcInstalled() {
        String vlc = getVLCPath();
        if (vlc != null) {
            File libDir = new File(vlc);
            if (libDir.exists())
                return true;
        }
        return false;
    }

    public static String getVLCPath() {
        if (isWindows()) {
            String path = getVLCPathWindows();
            if (!path.isEmpty()) {
                File vlcFolder = new File(path);
                return (new File(vlcFolder, "vlc.exe")).toString();
            }
        } else if (isMac()) {
            return "/Applications/VLC.app/Contents/MacOS/VLC";
        } else if (isUnix()) {
            return "/usr/bin/vlc";
        }
        return null;
    }

    /**
     * Returns x32 or x64 VLC install path.
     * @return
     */
    public static String getVLCPathWindows() {
        // VLC player may be installed into different folder.
        String arch = System.getProperty("os.arch");

        if (arch.indexOf("64") >= 0) {
            return "C://Program Files//VideoLAN//VLC";
        }

        return "C://Program Files (x86)//VideoLAN//VLC";
        //return "C://Program Files//VideoLAN//VLC"; //Toan bei mir geht es nur so obwohl 32 bit
    }

    /**
     * Run external and play media
     *
     * @param media media to play
     */
    public static void runVLC(String media) throws ServiceException {
        ProcessBuilder pb = new ProcessBuilder(VLCUtil.getVLCPath(), media);
        try {
            Process start = pb.start();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * Run external and play media with subtitles
     *
     * @param media media to play
     * @param sub_path path to the subtitle file
     */
    public static void runVLCWithsubs(String media,String sub_path) throws ServiceException {
        ProcessBuilder pb = new ProcessBuilder(VLCUtil.getVLCPath(), media,"--sub-file="+sub_path);
        try {
            Process start = pb.start();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            throw new ServiceException(e.getMessage());
        }
    }


    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nux"));
    }
}