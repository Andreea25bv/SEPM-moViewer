package gui;

import dto.FilterParameter;
import dto.Movie;
import dto.Rating;
import dto.Subtitle;
import exception.ServiceException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaView;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.action.Action;
import org.springframework.beans.factory.annotation.Autowired;
import service.*;
import service.vlcj.VLCUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by powerowle on 04.12.2014.
 */

public class MainGuiController extends DialogManager {


    private static final Logger logger = LogManager.getLogger(MainGuiController.class);
    //mock subtitle object to be used when no subtitles are desired
    private final Subtitle mocksub = new Subtitle(-1, -1, "No Subtitle", "", " ", -1, " ", -1);
    @Autowired
    private IMovieService movieService;
    @Autowired
    private ICrawlerService crawler;
    @Autowired
    private IRatingService rating;
    @Autowired
    private ISubtitleService subtitles;
    @Autowired
    private ISoundRecognizeService soundService;
    @Autowired
    private IVoiceRecognizeService voiceService;

    private ObservableList<Movie> scanned = FXCollections.observableList(new ArrayList<Movie>());
    private AddMovieGuiController addMovieGuiController;
    private AddingProgressController addingProgressController;
    private DetailMovieGuiController detailMovieGuiController;
    private SleepGuiController sleepGuiController;
    private Stage mainStage;
    private Main main;
    private double defaultHeight;
    private double defaultWidth;
    private ObservableList<Movie> movieObservableList;
    private List<Movie> movieList;
    private List<Movie> selectedList;
    private int yearNow = Calendar.getInstance().get(Calendar.YEAR);
    @FXML
    private TableView<Movie> movieTable;
    @FXML
    private TableColumn<Movie, String> titleField;
    @FXML
    private TableColumn<Movie, String> languageField;
    @FXML
    private TableColumn<Movie, String> genreField;
    @FXML
    private TableColumn<Movie, Integer> yearField;
    @FXML
    private TableColumn<Movie, Double> ratingField;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField runTimeField;
    @FXML
    private TextField yearTextField;
    @FXML
    private TextField searchTitle;
    @FXML
    private TextField searchRuntime;
    @FXML
    private TextField searchLanguage;
    @FXML
    private TextField searchYear;
    @FXML
    private TextField searchGenre;
    @FXML
    private ComboBox searchRating;
    @FXML
    private Slider searchRatingFrom;
    @FXML
    private Slider searchRatingTo;
    @FXML
    private ComboBox searchFavourite;
    @FXML
    private ComboBox<Subtitle> subtitleBox;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MediaView youTube;
    @FXML
    private TableColumn<Movie, String> favoriteField;
    //Heuristic plus Random Search Parameters
    @FXML
    private TextField hTitle;
    @FXML
    private TextField hName;
    @FXML
    private TextField hGenre;
    @FXML
    private TextField hYear;
    private List<Subtitle> subtitleList = new ArrayList<Subtitle>();
    private Boolean soundServiceStarted = false;

    @FXML
    public void initialize() {
        try {
            movieList = new ArrayList<Movie>();
            movieList = movieService.readAllMovies();
        } catch (ServiceException e) {
            logger.error(e.getMessage());
        }

        //add event listener for subtitle box
        addEventListener();
        //init soundservice/lines
        try {
            soundService.init();
            voiceService.init();
        } catch (ServiceException e) {
            logger.error(e.getMessage());
        }

        titleField.setCellValueFactory(new PropertyValueFactory<Movie, String>("title"));
        languageField.setCellValueFactory(new PropertyValueFactory<Movie, String>("language"));
        genreField.setCellValueFactory(cellData -> new SimpleObjectProperty<String>(cellData.getValue().getGenre().replaceAll(",", ", ")));
        yearField.setCellValueFactory(new PropertyValueFactory<Movie, Integer>("year"));
        favoriteField.setCellValueFactory(cellData -> new SimpleObjectProperty<String>(convertBoolean(cellData.getValue().getFavorite())));

        ratingField.setCellValueFactory(callData -> {
            double r = 0.0;
            Rating t = null;
            try {
                t = rating.readRating(callData.getValue().getMid(), "User");
                r = t.getRating();
            } catch (ServiceException e) {
                if (!e.getMessage().equals("Rating for Movie with this mid does not exist")) {
                    logger.error(e.getMessage());
                } else {
                    //logger.debug(e);
                    try {
                        t = rating.readRating(callData.getValue().getMid(), "RottenTomato");
                        r = t.getRating();
                    } catch (ServiceException e1) {
                        //logger.debug(e);
                    }
                }
            }
            return new SimpleObjectProperty<Double>(r);
        });

        movieObservableList = FXCollections.observableList(movieList);
        movieTable.setItems(movieObservableList);

        movieTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        movieTable.getSortOrder().add(titleField);
        movieTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.getClickCount() == 2) {
                    if (movieTable.getSelectionModel().getSelectedItem() != null) {
                        onActionShowDetails();
                    }
                }
            }
        });

        //Setting values for the Rating sliders
        searchRatingFrom.setMin(0.0);
        searchRatingFrom.setMax(10.0);
        searchRatingFrom.setShowTickLabels(true);
        searchRatingFrom.setShowTickMarks(true);
        searchRatingFrom.setMajorTickUnit(5);
        searchRatingFrom.setMinorTickCount(4);
        searchRatingFrom.setBlockIncrement(9);

        searchRatingTo.setMin(0.0);
        searchRatingTo.setMax(10.0);
        searchRatingTo.setShowTickLabels(true);
        searchRatingTo.setShowTickMarks(true);
        searchRatingTo.setMajorTickUnit(5);
        searchRatingTo.setMinorTickCount(4);
        searchRatingTo.setBlockIncrement(9);
        searchRatingTo.setValue(10.0);


        //Setting the rating sources ComboBox
        ObservableList<String> options = FXCollections.observableArrayList(new ArrayList<String>());
        try {
            options = FXCollections.observableArrayList(movieService.readAllSources());
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        options.add(" ");
        searchRating.setItems(options);
        searchRating.getSelectionModel().selectLast();


        //Setting available subtitles for subtitlebox
        subtitleBox.getItems().add(null);
        for (Subtitle s : subtitleList) {
            subtitleBox.getItems().add(s);
        }
        // Define rendering of the list of values in ComboBox drop down.
        subtitleBoxFormat();
        // Define rendering of selected value shown in ComboBox.
        subtitleBox.setConverter(new StringConverter<Subtitle>() {
            @Override
            public String toString(Subtitle s) {


                return s.getLanguage();

            }

            @Override
            public Subtitle fromString(String subString) {
                return null; // No conversion fromString needed.
            }
        });


        //Setting the promptText for genre
        searchGenre.setPromptText("Action, Sci-Fi, Romance");

        //Setting the PromptText for Year
        searchYear.setPromptText(yearNow + "");
        ObservableList<String> options2 = FXCollections.observableArrayList(new ArrayList<String>());
        //set ComboBox of fav.
        options2.add("All");
        options2.add("Yes");
        options2.add("No");
        searchFavourite.setItems(options2);
        searchFavourite.getSelectionModel().selectFirst();
    }

    /*
        Action Handlers
     */

    @FXML
    public void onActionDeleteMovie() {
        logger.info("Delete button pressed");

        selectedList = movieTable.getSelectionModel().getSelectedItems();
        if (selectedList.isEmpty()) {
            this.createInfoDialog("Please select a movie(s) you want to delete.");
            logger.error("no Movie is selected");
        } else {
            Action response = this.createConfirmationDialog("Are you sure you want to delete the selected movie(s)?");
            if (this.dialogBoxConfirmation(response)) {
                boolean fail = false;
                for (Movie m : selectedList) {
                    try {
                        movieService.deleteMovie(m);
                    } catch (ServiceException e) {
                        //Dialog
                        this.createExceptionDialog(e);
                        logger.error(e);
                        fail = true;
                    }
                }
                if (!fail) {
                    movieTable.getItems().removeAll(selectedList);
                    movieTable.getSelectionModel().clearSelection();
                    this.createInfoDialog("Selected movie(s) successfully deleted.");
                }
            }
        }
    }

    @FXML
    public void onActionAddMovies() {
        logger.info("Adding Movies");

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Add Files from Folder");
        if (Files.exists(Paths.get("/home/markus"))) {
            directoryChooser.setInitialDirectory(new File("/home/markus/"));
        }
        File selectedDirectory = directoryChooser.showDialog(this.mainStage);
        logger.info("Selected directory: " + selectedDirectory);
        if (selectedDirectory != null && Files.exists(Paths.get(selectedDirectory.toURI()))) {
            crawler.clear();
            crawler.setPath(selectedDirectory.toPath());
            crawler.scan();
            if (crawler.getMovies().size() == 0) {
                this.createInfoDialog("No new movies found!");
            } else {
                this.getScanned().clear();
                this.getScanned().addAll(crawler.getMovies());
                Action response = this.createConfirmationDialog("Do you want to read data in automatically?");
                if (this.dialogBoxConfirmation(response)) {
                    logger.info("Auto selected");
                    this.showAddingProgressGui();
                } else {
                    logger.info("Manual selected");
                    this.showAddMovieGui();
                }
            }
        }
    }

    @FXML
    public void onActionAddFile() {
        this.scanFile();
    }

    @FXML
    public void onActionShowDetails() {
        logger.info("Details button pressed");
        Movie movie = null;
        selectedList = movieTable.getSelectionModel().getSelectedItems();

        if (selectedList.isEmpty()) {
            this.createInfoDialog("Please select a movie to see the details.");
            logger.error("no Movie is selected");
        } else if (selectedList.size() > 1) {
            this.createInfoDialog("Please select only one movie to see the details.");
            logger.error("You can only select 1 Movie for Details");
        } else {
            this.showDetailMovieGui(movieTable.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    public void onActionOpenFileInExplorer() {
        logger.info("OpenFile button pressed");
        selectedList = movieTable.getSelectionModel().getSelectedItems();
        if (selectedList.isEmpty()) {
            this.createInfoDialog("Please select a movie to open it.");
            logger.error("no Movie is selected");
        } else if (selectedList.size() > 1) {
            this.createInfoDialog("Please select only one movie to open it.");
            logger.error("You can only select 1 Movie to open it");
        } else {
            try {
                movieService.showMovieExplorer(movieTable.getSelectionModel().getSelectedItem());
            } catch (ServiceException e) {
                this.createExceptionDialog(e);
                logger.error(e.getMessage());
            }
        }
    }

    @FXML
    public void onActionPlayMovie() {
        Movie movie = null;
        movie = movieTable.getSelectionModel().getSelectedItem();
        if (movie == null) {
            this.createInfoDialog("Please select a movie to play.");
            logger.error("select movie before play");
        } else try {

            if (subtitleBox.getSelectionModel().getSelectedItem() == null) {
                movieService.playMovie(movie);
            } else {
                movieService.playMovieWithSubs(movie, subtitleBox.getSelectionModel().getSelectedItem().getLanguagePath());
            }
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            this.createExceptionDialog(e);
        }
    }

    @FXML
    public void onActionClear() {
        searchTitle.clear();
        searchLanguage.clear();
        searchFavourite.getSelectionModel().selectFirst();
        searchGenre.clear();
        hGenre.clear();
        hName.clear();
        hName.clear();
        hYear.clear();
        searchRating.getSelectionModel().selectLast();
        searchRatingFrom.setValue(0.0);
        searchRatingTo.setValue(10.0);
        searchYear.clear();
        searchRuntime.clear();
        refresh();
    }

    /**
     * when user click on Find button the method is activated
     * if user didnot give any Parameter and clicked find button
     * the method returns all the movies which has Rating from any
     * kind of Rating source. The rating range will be set from min
     * to max ( from 0.0 to 10.0)
     * <p/>
     * if the user gives Title but not full title maybe just giving
     * Bat the method return movies where Bat in it.
     * <p/>
     * the result will be set in the TableView for Movies and shown to
     * to the user
     */
    @FXML
    public void onActionFind() {
        logger.info("user has clicked Find Button!");
        FilterParameter fp = new FilterParameter();
        String title = null;
        String language = null;
        String genre = null;
        Integer year = null;
        Integer runTime = null;
        String source = null;
        Double ratingFrom;
        Double ratingTo;
        Boolean favo = null;
        try {

            if (!searchTitle.getText().isEmpty()) {
                title = searchTitle.getText();
                //logger.debug("Title " + title);
            }
            if (!searchLanguage.getText().isEmpty()) {
                language = searchLanguage.getText();
                //logger.debug("language " + language);
            }
            if (!searchGenre.getText().isEmpty()) {
                genre = searchGenre.getText();
                //logger.debug("Genre " + genre);
            }

            if (!searchYear.getText().isEmpty()) {
                year = Integer.parseInt(searchYear.getText());
            }
            if (!searchRuntime.getText().isEmpty()) {
                runTime = Integer.parseInt(searchRuntime.getText());
            }

            if (!searchRating.getSelectionModel().getSelectedItem().equals(" ")) {
                source = searchRating.getSelectionModel().getSelectedItem().toString();
            }

            ratingFrom = searchRatingFrom.getValue();
            if (searchRatingTo.getValue() == 0.0 && ratingFrom == 0.0) {
                ratingTo = 10.0;
            } else {
                ratingTo = searchRatingTo.getValue();
            }
            if (!searchFavourite.getSelectionModel().getSelectedItem().equals("All")) {
                if (searchFavourite.getSelectionModel().getSelectedItem().equals("Yes")) {
                    favo = true;
                } else {
                    favo = false;
                }
            } else {
                favo = null;
            }

            fp.setTitle(title);
            fp.setGenre(genre);
            fp.setLanguage(language);
            fp.setFavorite(favo);
            fp.setRunTime(runTime);
            fp.setYear(year);
            fp.setSourceRating(source);
            fp.setRatingFrom(ratingFrom);
            fp.setRatingTill(ratingTo);

            logger.debug(fp);
            movieObservableList = FXCollections.observableList(movieService.find(fp));
            movieTable.setItems(movieObservableList);
//            logger.info(movieObservableList.size());
        } catch (ServiceException s) {
            logger.error(s.getMessage());
            this.createExceptionDialog(s);
        } catch (NumberFormatException n) {
            logger.error(n.getMessage());
            this.createExceptionDialog(n);
        } catch (IllegalArgumentException i) {
            this.createInfoDialog(i.getMessage());
        }
    }

    @FXML
    public void onActionHeuristic() {
        String title = null;
        String name = null;
        String genre = null;
        Integer year = null;
        try {
            if (!hTitle.getText().isEmpty()) {
                title = hTitle.getText();
            }
            if (!hName.getText().isEmpty()) {
                name = hName.getText();
            }
            if (!hGenre.getText().isEmpty()) {
                genre = hGenre.getText();
            }
            if (!hYear.getText().isEmpty()) {
                year = Integer.parseInt(hYear.getText());
            }


            movieObservableList = FXCollections.
                    observableList(movieService.
                            heuristic(title, name, genre, year));
            movieTable.setItems(movieObservableList);
        } catch (ServiceException e) {
            if (e.getMessage().equals("One parameter is required to start the search!")) {
                this.createInfoDialog(e.getMessage());
            } else {
                //logger.error(e.getMessage());
                this.createExceptionDialog(e);
            }
        } catch (NumberFormatException n) {
            //logger.error(n.getMessage());
            this.createExceptionDialog(n);
        }

    }

    @FXML
    public void onActionRandom() {
        String title = null;
        String name = null;
        String genre = null;
        Integer year = null;
        try {
            if (!hTitle.getText().isEmpty()) {
                title = hTitle.getText();
            }
            if (!hName.getText().isEmpty()) {
                name = hName.getText();
            }
            if (!hGenre.getText().isEmpty()) {
                genre = hGenre.getText();
            }
            if (!hYear.getText().isEmpty()) {
                year = Integer.parseInt(hYear.getText());
            }
            Movie movie = movieService.random(title, name, genre, year);
            //List<Movie> temp = new ArrayList<>();
            //temp.add(movie);
            // movieObservableList.clear();
            // movieObservableList.addAll(movie);
            //movieTable.setItems(movieObservableList);
            showDetailMovieGui(movie, true);
        } catch (ServiceException e) {
            if (e.getMessage().equals("one Parameter is required to start the search")) {
                this.createInfoDialog(e.getMessage());
            } else if (e.getMessage().equals(" No sutible movie found. ")) {
                this.createInfoDialog(e.getMessage());

            } else


                //logger.error(e.getMessage());
                this.createExceptionDialog(e);

        } catch (NumberFormatException n) {
            //logger.error(n.getMessage());
            this.createExceptionDialog(n);
        }
    }

    private void randomShowDetails(Movie movie) {
        showDetailMovieGui(movie);
        this.detailMovieGuiController.setButtonInvisible();
    }

    @FXML
    public void onActionPlayMovieIntern() {

        logger.info("Play internal button pressed");
        Movie movie = null;
        selectedList = movieTable.getSelectionModel().getSelectedItems();

        if (selectedList.isEmpty()) {
            this.createInfoDialog("Select movie to play");
            logger.error("no Movie is selected");
        } else if (selectedList.size() > 1) {
            logger.error("You can only select 1 Movie for Details");
            this.createInfoDialog("Please select only 1 movie");
        } else {
            if (soundServiceStarted == false) {
                soundService.startWhistle();
            } else {
                soundService.restartWhistle();
            }
            movie = selectedList.get(0);
            this.showInternalPlayerGui(movie);
        }

    }

    @FXML
    public void onActionClose() {
        this.mainStage.close();
    }

    @FXML
    public void onActionSleepTimer() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SleepGui.fxml"));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                return main.getCtx().getBean(clazz);
            }
        });
        AnchorPane page = null;
        try {

            page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("See Details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainStage);

            Scene scene = new Scene(page);

            //Go Backto MainPage if Press Key ESC:
            scene.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
                public void handle(javafx.scene.input.KeyEvent event) {
                    if (event.getCode().equals(KeyCode.ESCAPE)) {
                        dialogStage.close();

                    }
                }
            });


            dialogStage.setResizable(false);

            dialogStage.setScene(scene);


            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();


        } catch (IOException e) {
            logger.error("Reading the necessary file failed: " + e);
            e.printStackTrace();
        }
    }

    @FXML
    public void onActionWindowSize() {
        List<String> choices = new ArrayList<>();
        choices.add("Default (700 x 1000)");
        choices.add("Large (800 x 1200)");
        choices.add("FullScreen");

        try {
            String choice = this.createInputBox("Choose your preferred window size:", choices).get();
            if (choice.equals("FullScreen")) {
                mainStage.setFullScreen(true);

            } else if (choice.equals("Default (700 x 1000)")) {
                mainStage.setHeight(this.defaultHeight);
                mainStage.setWidth(this.defaultWidth);
            } else if (choice.equals("Large (800 x 1200)")) {
                mainStage.setHeight(800.0);
                mainStage.setWidth(1200.0);
            }
        } catch (java.util.NoSuchElementException e) {
            logger.error("No changed on Window size");
        }

    }

    @FXML
    public void onActionSubtitle() {

    }

    /*
        Windows-Openers ("Showers")
    */

    public void showAddMovieGui() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddMovieGui.fxml"));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                return main.getCtx().getBean(clazz);
            }
        });
        AnchorPane page = null;
        try {
            page = loader.load();
            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Movies");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainStage);


            Scene scene = new Scene(page);

            //Go Backto MainPage if Press Key ESC:
            scene.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
                public void handle(javafx.scene.input.KeyEvent event) {
                    if (event.getCode().equals(KeyCode.ESCAPE)) {
                        dialogStage.close();
                    }
                }
            });

            //If MainPage is FullScreen, next Window is also FullScreen
            //if (mainStage.isFullScreen()) {
            //   dialogStage.setFullScreen(true);
            //}

            dialogStage.setHeight(this.defaultHeight);
            dialogStage.setWidth(this.defaultWidth);
            dialogStage.setResizable(false);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            this.addMovieGuiController = loader.getController();
            this.addMovieGuiController.setStage(dialogStage);
            this.addMovieGuiController.setMainGuiController(this);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        } catch (IOException e) {
            logger.error("Reading a necessary file failed: " + e);
            this.createExceptionDialog(e);

        }
    }

    private void showAddingProgressGui() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddingProgressGui.fxml"));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                return main.getCtx().getBean(clazz);
            }
        });
        AnchorPane page = null;
        try {
            page = loader.load();
            AddingProgressController controller = loader.getController();
            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Adding Movies");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainStage);

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setOnCloseRequest(event -> controller.handleCancelButton());

            // Set the person into the controller.
            this.addingProgressController = loader.getController();
            this.addingProgressController.setStage(dialogStage);
            this.addingProgressController.setMainGuiController(this);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        } catch (IOException e) {
            logger.error("Reading a necessary file failed: " + e);
            e.printStackTrace();
        }
    }

    public void showDetailMovieGui(Movie movie) {
        showDetailMovieGui(movie, false);
    }

    public void showDetailMovieGui(Movie movie, Boolean invisibleButton) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DetailMovieGui.fxml"));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                return main.getCtx().getBean(clazz);
            }
        });
        AnchorPane page = null;
        try {

            page = loader.load();
            DetailMovieGuiController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("See Details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainStage);

            Scene scene = new Scene(page);

            //Go Backto MainPage if Press Key ESC:
            scene.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
                public void handle(javafx.scene.input.KeyEvent event) {
                    if (event.getCode().equals(KeyCode.ESCAPE)) {
                        dialogStage.close();
                    }
                }
            });
            dialogStage.setOnCloseRequest(event -> controller.cleanup());

            dialogStage.setHeight(this.defaultHeight);
            dialogStage.setWidth(this.defaultWidth);
            dialogStage.setResizable(false);


            dialogStage.setScene(scene);

            // Set the person into the controller.
            this.detailMovieGuiController = loader.getController();
            this.detailMovieGuiController.setStage(dialogStage);
            this.detailMovieGuiController.setMovie(movie, this);
            if (invisibleButton) {
                this.detailMovieGuiController.setButtonInvisible();
            }
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();


        } catch (IOException e) {
            logger.error("Reading the necessary file failed: " + e);
            e.printStackTrace();
        }
    }

    public void showInternalPlayerGui(Movie movie) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InternalPlayer.fxml"));
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> clazz) {
                    return main.getCtx().getBean(clazz);
                }
            });

            Parent root = null;

            root = fxmlLoader.load(getClass().getResource("InternalPlayer.fxml").openStream());


            InternalPlayerController controller = fxmlLoader.getController();
            final Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle("moViewer Player");
            stage.setScene(scene);
            //stage.setMaximized(true);

            //set movie
            controller.setMovie(movie, this);


            //set subList
            controller.setSubMenu(subtitles.readSubtitlesForMovie(movie), this);

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    controller.stopMedia();
                    controller.cancelServices();

                }
            });

            stage.show();
            VLCUtil.discover();

            if (subtitleBox.getSelectionModel().getSelectedItem() == null) {
                controller.playMedia();
            } else {
                controller.playMediaWithSubs(subtitleBox.getSelectionModel().getSelectedItem());
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            this.createExceptionDialog(e);
        } catch (ServiceException e) {
            logger.error(e);
        }

    }

    /*
        Helpers
    */

    public String convertBoolean(boolean bool) {
        if (bool) {
            return "yes";
        } else {
            return "no";
        }
    }

    public void scanFile() {
        logger.info("Scanning single file.");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Files");
        if (Files.exists(Paths.get("/home/markus"))) {
            fileChooser.setInitialDirectory(new File("/home/markus/"));
        }
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Videos", "*.avi", "*.mpeg", "*.mpeg1", "*.mpeg2", "*.mpeg3", "*.mpeg4", "*.mpg", "*.mp2", "*.mp3", "*.mp4", "*.thra", "*.ogg", "*.ogm", "*.wmv", "*.divx", "*.xvid", "*.mov", "*.mkv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(this.mainStage);
        logger.info("Selected File: " + selectedFile);
        if (selectedFile != null && Files.exists(Paths.get(selectedFile.toURI()))) {
            crawler.clear();
            crawler.setPath(Paths.get(selectedFile.getParent()));
            crawler.setFile(selectedFile.getName());
            crawler.scan();
            if (crawler.getMovies().size() == 0) {
                createInfoDialog("No new movies found!");
            } else {
                this.getScanned().clear();
                this.getScanned().addAll(crawler.getMovies());
                this.showAddMovieGui();
            }
        }
    }

    public void refresh() {
        this.refreshTable();
//        try {
//            movieList.clear();
//            logger.info(movieList.size());
//
//            movieList.addAll(movieService.readAllMovies());
//            logger.info(movieList.size());
//        } catch (ServiceException e) {
//            this.createExceptionDialog(e);
//            logger.error(e.getMessage());
//        }
    }

    public void refreshTable() {
        try {
            int sel = this.movieTable.getSelectionModel().getSelectedIndex();
            this.movieObservableList.clear();
            this.movieObservableList.addAll(movieService.readAllMovies());
            this.movieTable.sort();
            this.movieTable.getSelectionModel().clearSelection();
            this.movieTable.getSelectionModel().selectIndices(sel);
        } catch (ServiceException e) {
            logger.error("Table-Update failed: " + e.getMessage());
        }
    }

    public void refreshItem() {
        Movie selectedMovie = this.movieTable.getSelectionModel().getSelectedItem();
        try {
            if(selectedMovie!=null) {
                subtitleBoxFormat();
                int sel = this.movieTable.getSelectionModel().getSelectedIndex();
                this.movieObservableList.remove(sel);
                this.movieObservableList.add(movieService.readMovie(selectedMovie.getMid()));
                this.movieTable.sort();
                this.movieTable.getSelectionModel().clearSelection();
                if (movieTable.getSortOrder().size() != 0) {
                    this.movieTable.getSelectionModel().selectIndices(sel);
                } else {
                    this.movieTable.getSelectionModel().selectIndices(movieObservableList.size() - 1);
                }
            }
        } catch (ServiceException e) {
            logger.error("Table-Update failed: " + e.getMessage());
        }
    }

    public void refreshItem(Movie movie) {
        Movie selectedMovie = this.movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie != null && movie != null && selectedMovie.getMid() == movie.getMid()) ;
        try {
            int sel = this.movieTable.getSelectionModel().getSelectedIndex();
            this.movieObservableList.remove(sel);
            this.movieObservableList.add(movieService.readMovie(selectedMovie.getMid()));
            this.movieTable.sort();
            this.movieTable.getSelectionModel().clearSelection();
            if (movieTable.getSortOrder().size() != 0) {
                this.movieTable.getSelectionModel().selectIndices(sel);
            } else {
                this.movieTable.getSelectionModel().selectIndices(movieObservableList.size() - 1);
            }
        } catch (ServiceException e) {
            logger.error("Table-Update failed: " + e.getMessage());
        }
    }

    /*
        Getter & Setter
    */
    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        this.defaultHeight = this.mainStage.getHeight();
        this.defaultWidth = this.mainStage.getWidth();
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public ObservableList<Movie> getScanned() {
        return scanned;
    }

    //Gets all subtitles for selected movie
    public void setSubtitleList(Movie m) {

        try {
            this.subtitleList.clear();
            this.subtitleList.add(mocksub);
            this.subtitleList.addAll(subtitles.readSubtitlesForMovie(m));
        } catch (ServiceException e) {
            createExceptionDialog(e);
        }

    }

    public TableView<Movie> getMovieTable() {
        return movieTable;
    }


    private void addEventListener() {

        subtitleBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {


                if (selectedList.isEmpty()) {
                    createInfoDialog("Please select a movie!");
                }
                if (selectedList.size() > 1) {
                    createInfoDialog("Please select only one movie!");
                }
            }
        });

        movieTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (movieTable.getSelectionModel().getSelectedItem() != null) {
                selectedList = movieTable.getSelectionModel().getSelectedItems();

                subtitleBoxFormat();
                // subtitleBox.setPromptText("No Subtitles");
            }
        });

    }

    private void subtitleBoxFormat() {
        if (movieTable.getSelectionModel().getSelectedItem() != null) {
            setSubtitleList(movieTable.getSelectionModel().getSelectedItem());
        }
        if (subtitleList.size() < 2) {
            subtitleBox.setDisable(true);
        } else {
            subtitleBox.setDisable(false);
            subtitleBox.getItems().removeAll(subtitleBox.getItems());
            for (Subtitle s : subtitleList) {
                subtitleBox.getItems().add(s);
            }

            // Define rendering of the list of values in ComboBox drop down.
            subtitleBox.setCellFactory((comboBox) -> {
                return new ListCell<Subtitle>() {
                    @Override
                    protected void updateItem(Subtitle item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            subtitleBox.getItems().remove(item);
                        } else {
                            setText(item.getLanguage() + "(" + item.getComment() + ")");
                        }
                    }
                };
            });

        }

    }

    public ISoundRecognizeService getSoundService() {
        return soundService;
    }
}