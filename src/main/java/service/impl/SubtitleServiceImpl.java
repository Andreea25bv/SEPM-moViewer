package service.impl;

import dao.ISubtitleDao;
import dto.Movie;
import dto.Subtitle;
import dto.SubtitleSearch;
import exception.ServiceException;
import exception.SubtitlePersistenceException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import service.ISubtitleService;
import service.subtitle.WordCloud;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by powerowle on 06.01.2015.
 */
public class SubtitleServiceImpl implements ISubtitleService {
    private final Logger logger = LogManager.getLogger(SubtitleServiceImpl.class.getName());

    private XmlRpcClientConfigImpl xmlrpc;
    private XmlRpcClient client;
    private String status;
    private String token;
    private Object[] params;
    private Map result;

    @Autowired
    private ISubtitleDao subtitleDao;

    // Set by Spring, used to check for common words and disregard
    private String[] germanwords;
    private String[] englishwords;
    private String[] frenchwords;
    private String[] italianwords;

    public SubtitleServiceImpl() {

        xmlrpc = new XmlRpcClientConfigImpl();
        client = new XmlRpcClient();

    }

    public void login() throws ServiceException {
        logger.info("Connect to the xmlRpc with user agent data");
        try {
            xmlrpc.setServerURL(new URL("http://api.opensubtitles.org/xml-rpc"));
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
        client.setConfig(xmlrpc);
        Object username = "sepmGroup";
        Object password = "sepm";
        Object language = "en";
        Object useragent = "sepmGroupWorkAustria";
        params = new Object[]{username, password, language, useragent};

        try {
            result = (Map) client.execute("LogIn", params);
        } catch (XmlRpcException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        this.status = (String) result.get("status");
        this.token = (String) result.get("token");

        logger.info("Status xmlrpc: " + this.status);
        logger.info("Token xmlrpc: " + this.token);
    }

    public void logout() throws ServiceException {
        logger.info("Disconnect from the xmlRpc");
        params = new Object[]{this.token};
        try {
            result = (Map) client.execute("LogOut", params);
            logger.info("Disconnect from the xmlRpc successfull");
        } catch (XmlRpcException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public List<SubtitleSearch> searchSubtitles(Movie m) throws ServiceException {
        logger.info("Search Subtitles on opensubtitles.org for Movie " + m.getMid());
        Map<String, Object> mapQuery = new HashMap<String, Object>();
        mapQuery.put("imdbid", m.getIdIMDB());

        params = new Object[]{this.token, new Object[]{mapQuery}};
        try {
            result = (Map) client.execute("SearchSubtitles", params);
        } catch (XmlRpcException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        List<SubtitleSearch> list = new ArrayList<SubtitleSearch>();
        Object[] data = (Object[]) result.get("data");

        for (int i = 0; i < data.length; i++) {
            Map dataMap = (Map) data[i];
            String imp = (String) dataMap.get("SubHearingImpaired");
            if (imp.equals("0")) {
                imp = "no";
            } else {
                imp = "yes";
            }
            list.add(new SubtitleSearch((String) dataMap.get("LanguageName"), (String) dataMap.get("SubFormat"), imp, (String) dataMap.get("SubDownloadLink"), (String) dataMap.get("IDSubtitleFile")));
        }
        return list;

    }

    public void addSubtitleExtern(SubtitleSearch s, Movie m) throws ServiceException {
        logger.info("Add Subtitle from opensubtitles.org");
        params = new Object[]{this.token, new Object[]{s.getIdSubtitleFile()}};
        try {
            result = (Map) client.execute("DownloadSubtitles", params);
        } catch (XmlRpcException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        Object[] data = (Object[]) result.get("data");
        Map dataMap = (Map) data[0];
        BASE64Decoder decoder = new BASE64Decoder();
        String path = this.getClass().getClassLoader().getResource("subtitles/").getPath().replaceAll("%20", " ").toString() + s.getIdSubtitleFile() + "." + s.getSubFormat();
        logger.debug("Path to save: "+path);
        try {

            if (!(getLangCode(s.getLanguageName()).equals("not supported"))) {
                File f = new File(path);
                f.createNewFile();
                FileWriter writer = new FileWriter(f);
                byte[] gzip = decoder.decodeBuffer((String) dataMap.get("data"));
                GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(gzip));

                IOUtils.copy(gis, writer, getLangCode(s.getLanguageName()));
                writer.flush();
                writer.close();
                gis.close();
            } else {
                throw new ServiceException("This language is not supported");
            }

        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }

        String comment = "";
        if (s.getSubHearingImpaired().equals("yes")) {
            comment = "Subtitle for Hearing Impaired";
        }

        create(s.getIdSubtitleFile()+"."+s.getSubFormat(),m,s.getLanguageName(),comment);
    }

    public String addSubtitleLocal(Movie m, File in) throws ServiceException {
        logger.info("Add Subtitle Local");
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            File out = new File(this.getClass().getClassLoader().getResource("subtitles/").getPath().replaceAll("%20", " ").toString()+in.getName());
            out.createNewFile();

            inChannel = new FileInputStream(in).getChannel();
            outChannel = new FileOutputStream(out).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);

        } catch (IOException e) {
            throw new ServiceException(e.getMessage(),e);
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                }

                if (outChannel != null) {
                    outChannel.close();
                }

            } catch (IOException e) {
                logger.error("Copy File from Original-Path to Resource failed");
                throw new ServiceException(e.getMessage(),e);
            }
        }

        String language = "";

        if (in.getName().toLowerCase().contains("german") || in.getName().toLowerCase().contains("ger")) {
            language = "German";
        } else if (in.getName().toLowerCase().contains("english") || in.getName().toLowerCase().contains("eng")) {
            language = "English";
        } else if (in.getName().toLowerCase().contains("italian") || in.getName().toLowerCase().contains("ita")) {
            language = "Italian";
        } else if (in.getName().toLowerCase().contains("french") || in.getName().toLowerCase().contains("fre")) {
            language = "French";
        }

        return language;
    }

    public Subtitle create(String filename, Movie m, String language, String comment) throws ServiceException {
        logger.info("AddNewSubtitle");
        validateSubtitle(language);
        Subtitle s = null;

        int countLang = this.subtitleLangCount(m, language) + 1;
        String countLangString = Integer.toString(countLang);

        try {
            Map<String, Integer> unsortedwords = getUnsortedListWords(filename,language);

            //Count of the different words in a movie:
            int wordcloudCount = unsortedwords.size();
            String commonWordString = "";

            if (getLangOK(language)) {

                if (wordcloudCount > 0) {
                    commonWordString = getCommonWordString(unsortedwords);
                }
            }
            s = subtitleDao.createSubtitle(new Subtitle(1, m.getMid(), language + " " + countLangString, commonWordString, filename, 30, comment, wordcloudCount));

        } catch (SubtitlePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
        return s;
    }

    /**
     * This method reads the content of the Subtitle-File and returns a list of specified words with their frequency.
     * Specified words are all words who has more than two letters and are not on the vorbiddenList in their own
     * languages (vorbidden words are available for English, German, French and Italian).
     * @param filename
     * @param language
     * @return
     * @throws ServiceException
     */
    private Map<String, Integer> getUnsortedListWords(String filename, String language) throws ServiceException {
        try {
            FileReader fr = new FileReader(this.getClass().getClassLoader().getResource("subtitles/").getPath().replaceAll("%20", " ").toString() + filename);
            BufferedReader br = new BufferedReader(fr);

            double line = 0;
            String allcontent = "";

            while (line != 3000) {
                allcontent = allcontent + br.readLine() + " ";
                line++;
            }

            allcontent = allcontent.replaceAll("[0-9]+", "");
            allcontent = allcontent.replaceAll("<i>", "");
            allcontent = allcontent.replaceAll(">", "");
            allcontent = allcontent.replaceAll(":", "");
            allcontent = allcontent.replaceAll("-", "");
            allcontent = allcontent.replaceAll(",", "");
            allcontent = allcontent.replaceAll("null", "");
            allcontent = allcontent.toLowerCase();

            Map<String, Integer> unsortedwords = new HashMap<String, Integer>();


            for (String oneword : allcontent.split("\\W+")) {
                int anzahl = 0;
                if (oneword.length() > 2 && !Arrays.asList(setVorbiddenWords(language)).contains(oneword)) {
                    Integer value = unsortedwords.get(oneword);
                    if (value != null) {
                        anzahl = value;
                    }
                    unsortedwords.put(oneword, anzahl + 1);
                }
            }
            return unsortedwords;

        } catch (FileNotFoundException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * This methods convert a Map-List to an ArrayList, sort it and returns the 30 last words (who are
     * the 30 common words in the movie) of the list.
     * @param map
     * @return
     */
    private List<WordCloud> getCommonWordList(Map<String, Integer> map) {

        List<WordCloud> newList = new ArrayList<WordCloud>();
        for (String word : map.keySet()) {
            WordCloud w = new WordCloud(word, map.get(word));
            newList.add(w);
        }
        Collections.sort(newList);

        //30 common words:
        ArrayList<WordCloud> commonWordsList = new ArrayList<WordCloud>();
        for (int i = newList.size() - 1; i > newList.size() - 30; i--) {
            commonWordsList.add(newList.get(i));
        }

        return commonWordsList;
    }

    /**
     * This methods convert the Map-List to an String and return the 30 common words of the movie.
     * @param map
     * @return
     */
    private String getCommonWordString(Map<String, Integer> map) {

        List<WordCloud> newList = new ArrayList<WordCloud>();
        for (String word : map.keySet()) {
            WordCloud w = new WordCloud(word, map.get(word));
            newList.add(w);
        }

        Collections.sort(newList);

        //30 common words:
        String commonwords = "";

       for (int i = newList.size() - 1; i > newList.size() - 30; i--) {
            commonwords = commonwords + " " + newList.get(i).getWord();
        }

        return commonwords;
    }

    public List<Subtitle> readSubtitlesForMovie(Movie m) throws ServiceException {
        logger.info("Read Subtitles Service for Movie");
        try {
            if (m == null) {
                throw new ServiceException();
            } else {
                return subtitleDao.readSubtitle(m.getMid());
            }
        } catch (SubtitlePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * This methods return a list of vorbidden words in the languages German, English, French and Italian.
     * @param language
     * @return
     */
    private String[] setVorbiddenWords(String language) {
        String[] lang;
        if (language.contains("German")) {
            lang = this.germanwords;
        } else if (language.contains("English")) {
            lang = this.englishwords;
        } else if (language.contains("French")) {
            lang = this.frenchwords;
        } else if (language.contains("Italian")) {
            lang = this.italianwords;
        } else {
            lang = new String[]{""};
        }
        return lang;
    }

    private int subtitleLangCount(Movie m, String la) throws ServiceException {
        logger.info("Count the amount of subtitles-files in this language: " + la);
        try {
            List<Subtitle> list = this.readSubtitlesForMovie(m);
            int i = 0;

            for (Subtitle s : list) {
                if (s.getLanguage().contains(la)) {
                    i++;
                }
            }
            return i;

        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }


    public void delete(Subtitle s) throws ServiceException {
        logger.info("Delete Subtitle Service");
        try {
            subtitleDao.deleteSubtitle(s);
           } catch (SubtitlePersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public List<WordCloud> commonSortedList(Subtitle s) throws ServiceException {
        logger.info("CommonWords for this Language: " + s.getLanguage());
        try {
            if (getLangOK(s.getLanguage())) {
                return getCommonWordList(getUnsortedListWords(s.getLanguagePath(), s.getLanguage()));
            } else {
                List<WordCloud> wc = new ArrayList<>();
                WordCloud w = new WordCloud("not available", 0);
                wc.add(w);
                return wc;
            }
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void update(Movie m, Subtitle s) throws ServiceException {
        logger.info("Update Subtitle Service");
        try {
            if (readSubtitleExist(m,s.getLanguage(), s.getSid())){
                validateSubtitle(s.getLanguage());
                subtitleDao.updateSubtitle(s);
            }
        } catch (SubtitlePersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * This method returns the language-decoding-code of the selected language.
     * @param language
     * @return
     */
    private String getLangCode(String language) {

        if (language.equals("Afrikaans") || language.equals("Albanian") || language.equals("Brazilian") || language.equals("Basque") || language.equals("Breton") || language.equals("Catalan") || language.equals("Corsican") || language.equals("Danish") || language.equals("Dutch") || language.equals("English") || language.equals("Faroese") || language.equals("Galician") || language.equals("German") || language.equals("Icelandic") || language.equals("Indonesian") || language.equals("Italian") || language.equals("Latin") || language.equals("Leonese") || language.equals("Luxembourgish") || language.equals("Malay") || language.equals("Manx") || language.equals("Norwegian") || language.equals("Occitan") || language.equals("Portuguese") || language.equals("Scottish Gaelic") || language.equals("Spanish") || language.equals("Swahili") || language.equals("Swedish") || language.equals("Walloon")) {
            return "ISO-8859-1";
        } else if (language.equals("Bosnian") || language.equals("Croatian") || language.equals("Czech") || language.equals("Czech") || language.equals("Hungarian") || language.equals("Polish") || language.equals("Romanian") || language.equals("Serbian") || language.equals("Slovak") || language.equals("Slovene") || language.equals("Slovenian") || language.equals("Upper Sorbian") || language.equals("Lower Sorbian") || language.equals("Turkmen") || language.equals("Romanian")) {
            return "ISO-8859-2";
        }else if (language.equals("Maltese") || language.equals("Esperanto")) {
            return "ISO-8859-3";
        } else if (language.equals("Estonian") || language.equals("Latvian") || language.equals("Lithuanian") || language.equals("Greenlandic")|| language.equals("Sami")) {
            return "ISO-8859-4";
        } else if (language.equals("Bulgarian") || language.equals("Belarusian") || language.equals("Russian") || language.equals("Serbian") || language.equals("Maceddonian")) {
            return "ISO-8859-5";
        } else if (language.equals("Arabic")) {
            return "windows-1256";
        } else if (language.equals("Greek")) {
            return "ISO-8859-7";
        } else if (language.equals("Hebrew")) {
            return "ISO-8859-8";

        } else if (language.equals("Turkish")) {
            return "ISO-8859-9";
        } else if (language.equals("Vietnamese")) {
            return "ISO-8859-11";
        } else if (language.equals("Estonian")) {
            return "ISO-8859-13";
        } else if (language.equals("Irish")) {
            return "ISO-8859-14";
        } else if (language.equals("French") || language.equals("Finnish")) {
            return "ISO-8859-15";
        } else if (language.equals("Chinese (simplified)") || language.equals("Chinese + English")) {
            return "windows-936";
        } else if (language.equals("Chinese")) {
            return "windows-950";
        } else if (language.equals("Persian")) {
            return "not supported";
        } else {
            return "not supported";
        }
    }

    /**
     * This methods returns true if the selected language has letters who could work for the WordCloud. If the
     * selected language is false, it wouldn't work for the WordCloud.
     * @param language
     * @return
     */
    private boolean getLangOK(String language) {

        if (language.contains("Afrikaans") || language.contains("Albanian") || language.contains("Brazilian") || language.contains("Basque") || language.contains("Breton") || language.contains("Catalan") || language.contains("Corsican") || language.contains("Danish") || language.contains("Dutch") ||language.contains("English") || language.contains("Faroese") || language.contains("Galician") || language.contains("German") || language.contains("Icelandic") || language.contains("Indonesian") || language.contains("Italian") || language.contains("Latin") || language.contains("Leonese") || language.contains("Luxembourgish") || language.contains("Malay") || language.contains("Manx") || language.contains("Norwegian") || language.contains("Occitan") || language.contains("Portuguese") || language.contains("Scottish Gaelic") || language.contains("Spanish") || language.contains("Swahili") || language.contains("Swedish") || language.contains("Walloon")) {
            return true;
        } else if (language.contains("Bosnian") || language.contains("Croatian") || language.contains("Czech") || language.contains("Czech") || language.contains("Hungarian") || language.contains("Polish") || language.contains("Romanian") || language.contains("Serbian") || language.contains("Slovak") || language.contains("Slovene") || language.contains("Slovenian") || language.contains("Upper Sorbian") || language.contains("Lower Sorbian") || language.contains("Turkmen") || language.contains("Romanian")) {
            return true;
        } else if (language.contains("Turkish")) {
            return true;
        }  else if (language.contains("Irish")) {
            return true;
        }   else if (language.contains("French") || language.contains("Finnish")) {
            return true;
        } else {
            return false;
        }
    }


    /*
        Getter & Setter
     */

    public void setGermanwords(String[] germanwords) {
        this.germanwords = germanwords;
    }

    public void setEnglishwords(String[] englishwords) {
        this.englishwords = englishwords;
    }

    public void setFrenchwords(String[] frenchwords) {
        this.frenchwords = frenchwords;
    }

    public void setItalianwords(String[] italianwords) {
        this.italianwords = italianwords;
    }

    private boolean readSubtitleExist(Movie m, String lang, int sid) throws ServiceException {
        List<Subtitle> list = readSubtitlesForMovie(m);
        for (Subtitle s : list) {
            if (s.getLanguage().equals(lang) && !s.getSid().equals(sid)) {
                throw new ServiceException("This Language is already defined, please choose another Name for this Subtitle");
            }else {
            }
        }
        return true;
    }

    /**
     * This method validate the length of the language-Title of the Subtitle.
     * @param s
     * @throws ServiceException
     */
    private void validateSubtitle(String s) throws ServiceException {

        if (!s.isEmpty()) {
            if (s.length() < 2) {
                throw new ServiceException("Language of Subtitle cannot be smaller than 2");
            }
        }
    }
}
