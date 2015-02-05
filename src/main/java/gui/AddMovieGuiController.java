package gui;

import dto.Movie;
import dto.Role;
import exception.MovieServiceException;
import exception.ServiceException;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.IMovieService;
import service.IRatingService;
import service.IRottenTomatoService;
import service.ITMDBService;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by powerowle on 04.12.2014.
 */


public class AddMovieGuiController extends DialogManager implements Initializable {

    private static final Logger logger = LogManager.getLogger(AddMovieGuiController.class);
    private Stage mainStage;
    private Stage stage;
    private MainGuiController mainGuiController;

    @Autowired
    private ITMDBService api;
    @Autowired
    private IMovieService movieService;
    @Autowired
    private IRottenTomatoService rotten;
    @Autowired
    private IRatingService ratingService;

    private ObservableList<Movie> cached = FXCollections.observableList(new ArrayList<Movie>());
    private ObservableList<Role> roles = FXCollections.observableList(new ArrayList<Role>());
    private Movie cachedMovie = null;
    private Movie selectedFile = null;

    /*LINKS!!!*/
    @FXML
    private TableView<Movie> files;
    @FXML
    private TableColumn<Movie, String> fileNames;

    /*Oben*/
    @FXML
    private TextField crawlerTitle;
    @FXML
    private Label crawlerPath;
    @FXML
    private TextField searchLanguage;
    @FXML
    private Label mainNotification;
    @FXML
    private Label moviedbNotification;

    /*MovieDB*/
    @FXML
    private TableView<Movie> themoviedb_list;
    @FXML
    private TableColumn<Movie, String> themoviedb_list_title;
    @FXML
    private TableColumn<Movie, Integer> themoviedb_list_year;


    /*MovieDB Metadaten*/
    @FXML
    private Label themoviedbTitle;
    @FXML
    private Label themoviedbRuntime;
    @FXML
    private Label themoviedbYear;
    @FXML
    private Label themoviedbPoster;
    @FXML
    private Label themoviedbLanguage;
    @FXML
    private Label themoviedbBudget;
    @FXML
    private Label themoviedbRevenue;
    @FXML
    private Label themoviedbTagline;
    @FXML
    private Label themoviedbGenres;
    @FXML
    private Label themoviedbPlot;
    @FXML
    private Label themoviedbSimilarmovies;

    @FXML
    private TableView<Role> themoviedb_staff;
    @FXML
    private TableColumn<Role, String> themoviedb_staff_name;
    @FXML
    private TableColumn<Role, String> themoviedb_staff_biography;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        files.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleChangeMovieTitles(newValue));
        fileNames.setCellValueFactory(new PropertyValueFactory<Movie, String>("title"));

        themoviedb_list_title.setCellValueFactory(new PropertyValueFactory<Movie, String>("title"));
        themoviedb_list_year.setCellValueFactory(new PropertyValueFactory<Movie, Integer>("year"));
        themoviedb_list.setItems(this.cached);

        themoviedb_list.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleChangeTMDBTitles(newValue));

        themoviedb_staff.setItems(this.roles);
        themoviedb_staff_name.setCellValueFactory(cellData -> new SimpleObjectProperty<String>(cellData.getValue().getPerson().getName()));
        themoviedb_staff_biography.setCellValueFactory(new PropertyValueFactory<Role, String>("role"));

        this.themoviedbPlot.setWrapText(true);
        this.moviedbNotification.setText("");
        this.mainNotification.setText("");
        this.searchLanguage.setText("en");

    }

    /*
        Handlers
    */

    @FXML
    private void handleChangeMovieTitles(Movie newMovie) {
        logger.info("Loading new title to search metadata for: " + newMovie);
        if (files.getSelectionModel().getSelectedItem() != null) {
            Movie selected = files.getSelectionModel().getSelectedItem();
            this.selectedFile = selected;
            this.crawlerTitle.setText(selected.getTitle());
            this.crawlerPath.setText(selected.getPath());
            this.cached.clear();
            this.moviedbNotification.setTextFill(Paint.valueOf("red"));
            this.moviedbNotification.setText("");
            this.handleSearchMetadata(null);
        }
    }

    @FXML
    private void handleSearchMetadata(ActionEvent event) {
        logger.info("Searching for metadata.");
        this.moviedbNotification.setText("Searching for metadata.");
        if (this.crawlerTitle.getText().length() >= 2) {
            try {
                this.cached.clear();
                this.cached.addAll(api.search(this.crawlerTitle.getText(), 0, this.searchLanguage.getText(), true, 0));
                logger.info(this.cached);
            } catch (ServiceException e) {
                this.createExceptionDialog(e);
                logger.error("ServiceException: " + e);

                this.moviedbNotification.setText("Search-error!");
            }
            this.moviedbNotification.setText("Done. " + cached.size() + " results.");
            if (cached.size() > 0) {
                this.moviedbNotification.setTextFill(Paint.valueOf("green"));
            } else {
                this.moviedbNotification.setTextFill(Paint.valueOf("red"));
            }
        }
    }

    @FXML
    private void handleChangeTMDBTitles(Movie newMovie) {
        logger.info("Checking on TMDB for details!");
        if (themoviedb_list.getSelectionModel().getSelectedItem() != null) {

//            this.cachedMovie = themoviedb_list.getSelectionModel().getSelectedItem();
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws InterruptedException {
                            try {

                                cachedMovie = api.getMovieByTMDBID(themoviedb_list.getSelectionModel().getSelectedItem().getIdTMDB(), searchLanguage.getText());
                                cachedMovie.setPath(crawlerPath.getText());
                                if (cachedMovie.getIdIMDB() == null) {
                                    cachedMovie.setIdIMDB(0);
                                }
                                Platform.runLater(() -> {
                                    themoviedbTitle.setText(cachedMovie.getTitle());
                                    themoviedbRuntime.setText(cachedMovie.getRunTime() + "");
                                    themoviedbYear.setText(cachedMovie.getYear() + "");
                                    if (cachedMovie != null && !cachedMovie.getPosterPath().equals("")) {
                                        themoviedbPoster.setText("Available!");
                                    } else {
                                        themoviedbPoster.setText("Not available!");
                                    }
                                    themoviedbLanguage.setText(cachedMovie.getLanguage());
                                    themoviedbBudget.setText("$" + NumberFormat.getNumberInstance(Locale.GERMAN).format(Integer.parseInt(cachedMovie.getBudget())));
                                    themoviedbTagline.setText(cachedMovie.getTagLine());
                                    themoviedbRevenue.setText("$" + NumberFormat.getNumberInstance(Locale.GERMAN).format(Integer.parseInt(cachedMovie.getRevenue())));
                                    themoviedbGenres.setText(cachedMovie.getGenre().replaceAll(",", ", "));
                                    themoviedbPlot.setText(cachedMovie.getPlot());
                                    //themoviedbSimilarmovies.setText(cachedMovie.getSimilarFilms());

                                    roles.clear();
                                    roles.addAll(cachedMovie.getStaff());
                                    moviedbNotification.setText("Metadata loaded!");
                                    logger.info("Film selected: " + cachedMovie);
                                });
                            } catch (ServiceException e) {
                                logger.error(e);
                            }
                            return null;
                        }
                    };
                }
            };
            createDialogForService("Loading Metadata", service);
            service.start();
        }
    }

    @FXML
    private void handleAddMetadata(ActionEvent event) {
        if (cachedMovie != null) {
            logger.info("Saving movie into the DB!");
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws InterruptedException {
                            try {
                                Movie movie = movieService.createMovie(cachedMovie);
                                logger.info("Saved!");
                                Platform.runLater(() -> {
                                    mainGuiController.refreshTable();
                                    cached.clear();
                                    moviedbNotification.setText("");
                                    logger.info("Removing because found metadata: " + files.getSelectionModel().getSelectedItem());
                                    mainGuiController.getScanned().remove(files.getSelectionModel().getSelectedItem());
                                });
                                selectedFile = null;
                                cachedMovie = null;
                                try {
                                    logger.info("SELECTED SOURCE = RottenTomatoes");
                                    ratingService.createUpdateTomatoRating(movie.getMid(), rotten.getMovieScore(rotten.searchMovie(movie.getTitle(), movie.getYear())));
                                } catch (ServiceException e) {
                                    logger.debug(e.getMessage());
                                }
                                logger.debug("Files remaining: " + files.getItems().size());
                                if (files.getItems().size() == 0) {
                                    Platform.runLater(() -> {
                                        stage.close();
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        createInfoDialog("Movie '" + movie.getTitle() + "' added!");
                                    });
                                }
                            } catch (MovieServiceException e) {
                                createExceptionDialog(e);
                                logger.error(e.getMessage(), e);
                            } catch (ServiceException e) {
                                createExceptionDialog(e);
                                logger.error(e);
                            }
                            return null;
                        }
                    };
                }
            };
            createDialogForService("Saving!", service);
            service.start();
        }

    }

    @FXML
    public void handleIgnoreButton() {
        if (selectedFile != null) {
            if (dialogBoxConfirmation(createConfirmationDialog("Do you really want to ignore this file?"))) {
                try {
                    this.movieService.setMovieIgnored(selectedFile);
                    this.mainGuiController.getScanned().remove(selectedFile);
                } catch (ServiceException e) {
                    logger.error(e);
                    createInfoDialog("An error occurred: " + e);
                }
            }
        }
    }

    /*
    * Getter & Setter
    */

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainGuiController(MainGuiController mainGuiController) {
        this.mainGuiController = mainGuiController;
        this.files.setItems(mainGuiController.getScanned());
        if (this.files.getItems().size() == 1) {
            this.files.getSelectionModel().select(0);
        }
    }
}