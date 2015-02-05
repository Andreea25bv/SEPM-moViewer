package gui;

import dto.Movie;
import exception.MovieServiceException;
import exception.ServiceException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.IMovieService;
import service.IRatingService;
import service.IRottenTomatoService;
import service.ITMDBService;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by markus on 07.01.15.
 */
public class AddingProgressController extends DialogManager implements Initializable {

    private static final Logger logger = LogManager.getLogger(AddingProgressController.class);
    final private AddingProgressController controller = this;
    @Autowired
    IRatingService ratingService;
    @Autowired
    IRottenTomatoService rottenTomatoService;
    private int failed = 0;
    private int success = 0;
    private Boolean cancel = false;
    private Thread thread;
    private Stage mainStage;
    private Stage stage;
    private MainGuiController mainGuiController;
    private Movie added;
    @Autowired
    private ITMDBService api;
    @Autowired
    private IMovieService service;
    private String title = "";
    private String count = "";
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label addingLabel;
    @FXML
    private Label countLabel;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        failed = 0;
        success = 0;
        cancel = false;
    }

    @FXML
    public void handleCancelButton() {
        this.cancel = true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getFailed() {
        return failed;
    }

    public void increaseFailed() {
        this.failed++;
    }

    public void close() {
        showSummary();
        mainGuiController.refreshTable();
        this.stage.close();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void showSummary() {
        createInfoDialog("Movies added: " + success + "\n" +
                "Movies failed: " + failed);
    }

    public void setMainGuiController(MainGuiController mainGuiController) {
        this.mainGuiController = mainGuiController;
        this.cancel = false;

        this.countLabel.setText("1/" + this.mainGuiController.getScanned().size());
        this.addingLabel.setText(this.mainGuiController.getScanned().get(0).getTitle());

        this.thread = new Thread() {
            @Override
            public void run() {

                int x = 1;
                logger.info("Movies to be scanned in: " + mainGuiController.getScanned().size());
                for (Movie movie : mainGuiController.getScanned()) {

                    setTitle(movie.getTitle());
                    setCount(x + "/" + mainGuiController.getScanned().size());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addingLabel.setText("Searching for: '" + controller.getTitle() + "'");
                            countLabel.setText(controller.getCount());
                            progressBar.setProgress(((100.00 / mainGuiController.getScanned().size()) * (success + failed)) / 100);
                            logger.info("Progress %: " + (100.00 / mainGuiController.getScanned().size()) * (success + failed));
                        }
                    });

                    if (cancel == true) {
                        break;
                    }
                    logger.debug("Checking movie #" + x);
                    logger.debug("Titel: " + movie.getTitle());

                    ArrayList<Movie> movie_api = null;
                    try {
                        movie_api = api.searchAppend(movie.getTitle(), 0, "en", false, 0);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    if (movie_api != null && movie_api.size() != 0 && movie_api.get(0) != null) {
                        controller.setTitle(movie_api.get(0).getTitle());
                        controller.setCount(x + "/" + mainGuiController.getScanned().size());

                        try {
                            Movie tmp = api.getMovieByTMDBID(movie_api.get(0).getIdTMDB(), "en");
                            if (tmp.getPosterPath() == null) {
                                tmp.setPosterPath("");
                            }
                            tmp.setPath(movie.getPath());
                            Movie add = service.createMovie(tmp);
                            added = service.readMovie(add.getMid());

                            try {
                                logger.info("SELECTED SOURCE = RottenTomatoes");
                                ratingService.createUpdateTomatoRating(added.getMid(), rottenTomatoService.getMovieScore(rottenTomatoService.searchMovie(added.getTitle(), added.getYear())));
                            } catch (ServiceException e) {
                                logger.debug(e.getMessage());
                            }

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    mainGuiController.getMovieTable().getItems().add(added);
                                    mainGuiController.refreshTable();
                                }
                            });

                            success++;
                        } catch (ServiceException e) {
                            logger.error("Movie adding failed!");
                            logger.error(e);
                            failed++;
                        } catch (MovieServiceException e) {
                            logger.error("Movie adding failed!");
                            logger.error(e);
                            failed++;
                        }
                    } else {
                        failed++;
                    }
                    x++;
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        close();
                    }
                });
            }
        };
        thread.start();
    }
}
