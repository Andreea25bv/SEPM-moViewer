package service.impl;

import dto.Trailer;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import service.ITrailerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by skYeYe on 08.01.2015.
 */
public class TrailerServiceImpl implements ITrailerService {

    private String youtube = "https://www.youtube.com/";
    private List<WebElement> trailerWebList;
    private static final By searchTextFieldLocator = By.xpath("//form/div/label/input");
    private static final By searchButton = By.xpath("//div/div[3]/form/button");
    private static final By trailerResults = By.xpath("//li/div/div/div[2]/h3/a");
    WebDriver driver = null;

    public TrailerServiceImpl() { }

    @Override
    public List<WebElement> youtubeTrailerList(String searchText) {

        Logger logger = Logger.getLogger("");
        logger.setLevel (Level.OFF);
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        Logger.getLogger("org.apache.http").setLevel(Level.OFF);
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        driver = new HtmlUnitDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.navigate().to(youtube);
        driver.findElement(searchTextFieldLocator).clear();
        driver.findElement(searchTextFieldLocator).sendKeys(searchText);
        driver.findElement(searchButton).click();
        return trailerWebList = driver.findElements(trailerResults);
    }

    @Override
    public List<Trailer> getAllTrailers(String searchText) {
        List<WebElement> tempWebList = youtubeTrailerList(searchText);
        List<Trailer> trailerList = new ArrayList<Trailer>();
        for(int i = 0; i < tempWebList.size(); i++){
            Trailer trailer = new Trailer(tempWebList.get(i).getText(), tempWebList.get(i).getAttribute("href"));
            trailerList.add(trailer);
        }
        return trailerList;
    }

    public String getCurrentUrl(){
        return driver.getCurrentUrl();
    }

    static {
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
    }
}
