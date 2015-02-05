package gui;

import dto.*;
import exception.MovieServiceException;
import exception.ServiceException;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.action.Action;
import org.springframework.beans.factory.annotation.Autowired;
import service.*;
import service.impl.TrailerServiceImpl;
import service.subtitle.WordCloud;
import service.vlcj.VLCUtil;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * Created by powerowle on 10.12.2014.
 */
public class DetailMovieGuiController extends DialogManager implements Initializable {

    private static final Logger logger = LogManager.getLogger(DetailMovieGuiController.class);
    @FXML
    Tab informationTab;
    @FXML
    Tab ratingTab;
    @FXML
    Tab trailerTab;
    @FXML
    Tab subtitleTab;
    private Stage mainStage;
    private Main main;
    private Stage stage;
    private Thread thread;
    @Autowired
    private IMovieService movieService;
    @Autowired
    private IRatingService ratingService;
    @Autowired
    private IRottenTomatoService tomatoService;
    @Autowired
    private ITMDBService apiService;
    @Autowired
    private ISubtitleService subtitleService;
    @Autowired
    private ITrailerService trailerService;
    @Autowired
    private ICrawlerService crawlerService;

    private Stage showPictureAnchor;
    private Movie selectedFile = null;
    private MainGuiController mainGuiController;
    private ObservableList<Role> roles = FXCollections.observableList(new ArrayList<Role>());
    private ObservableList<SubtitleSearch> searchSubs;
    private XYChart.Series<String, Integer> series = new XYChart.Series<String, Integer>();
    //Oben:
    @FXML
    private Label title;
    @FXML
    private Label path;
    @FXML
    private ImageView detailsFavouriteEmpty;
    @FXML
    private ImageView detailsFavouriteFull;

    @FXML
    private Button previous;
    @FXML
    private Button next;

    //Information-Tab:

    @FXML
    private TextField detailsTitle;
    @FXML
    private TextField detailsRuntime;
    @FXML
    private TextField detailsYear;
    @FXML
    private TextArea detailsPlot;
    @FXML
    private TextField detailsLanguage;
    @FXML
    private TextField detailsBudget;
    @FXML
    private TextField detailsRevenue;
    @FXML
    private TextField detailsTagline;
    @FXML
    private TextField detailsGenres;
    @FXML
    private ImageView detailsPoster;
    @FXML
    private TextArea detailsSimilarMovies;

    //Personen:
    @FXML
    private TableView<Role> detailsStaff;
    @FXML
    private ImageView detailsPhoto;
    @FXML
    private TextArea detailsBiography;
    @FXML
    private TableColumn<Role, String> detailsName;
    @FXML
    private TableColumn<Role, String> detailsRole;


    //Rating-Tab:

    @FXML
    private TableView detailsRating;
    @FXML
    private TableColumn ActualRatingFrom;
    @FXML
    private TableColumn ActualRatingValue;
    @FXML
    private TextField InsertRatingValue;
    @FXML
    private Label rotenTomatosReview;
    @FXML
    private Slider sliderRating;
    @FXML
    private ComboBox SelectExternRating;

    //Subtitles-Tab:
    @FXML
    private TableView subtitles;
    @FXML
    private TableColumn subtitleLanguage;
    @FXML
    private TableColumn subtitleComment;
    @FXML
    private BarChart wordCloudNumber;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private PieChart wordCloudMostWords;
    @FXML
    private TableView searchSubtitlesTable;
    @FXML
    private TableColumn searchSubLanguage;
    @FXML
    private TableColumn searchSubtitleFormat;
    @FXML
    private TableColumn searchSubHearingImpaired;
    @FXML
    private Label notavailable;

    // TrailerService
    @FXML
    private WebView trailerWebView;
    @FXML
    private TextField trailerSearch;
    @FXML
    private TableView<Trailer> trailerListTable;
    @FXML
    private TableColumn<Trailer, String> trailerListColumn;
    @FXML
    private TableColumn<Trailer, String> webLinkColumn;

    /*
        Handlers
    */

    public void initialize(URL location, ResourceBundle resources) {
        this.detailsPlot.setWrapText(true);
        this.path.setWrapText(true);
        this.path.setWrapText(true);

        //Setting values for the Rating sliders
        sliderRating.setMin(0.0);
        sliderRating.setMax(10.0);
        sliderRating.setShowTickLabels(true);
        sliderRating.setShowTickMarks(true);
        sliderRating.setMajorTickUnit(5);
        sliderRating.setMinorTickCount(4);
        sliderRating.setBlockIncrement(9);

        //AddListener fuer die Metadaten:
        this.detailsBudget.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsBudget.setText(newValue));
        this.detailsYear.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsYear.setText(newValue));
        this.detailsTitle.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsTitle.setText(newValue));
        this.detailsRuntime.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsRuntime.setText(newValue));
        this.detailsPlot.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsPlot.setText(newValue));
        this.detailsLanguage.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsLanguage.setText(newValue));
        this.detailsBudget.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsBudget.setText(newValue));
        this.detailsRevenue.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsRevenue.setText(newValue));
        this.detailsTagline.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsTagline.setText(newValue));
        this.detailsGenres.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsGenres.setText(newValue));
        this.detailsSimilarMovies.textProperty().addListener(
                (observable, oldValue, newValue) -> this.detailsSimilarMovies.setText(newValue));


        this.detailsName.setCellValueFactory(cellData -> new SimpleObjectProperty<String>(cellData.getValue().getPerson().getName()));
        this.detailsRole.setCellValueFactory(new PropertyValueFactory<Role, String>("role"));

        this.detailsStaff.setItems(this.roles);
        this.detailsStaff.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> changeRole(newValue));

        this.detailsBiography.setWrapText(true);

        detailsPoster.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.getClickCount() >= 1 && detailsPoster.getImage() != null) {
                    showPicture(detailsPoster.getImage());
                }
            }
        });
        detailsPhoto.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.getClickCount() >= 1 && detailsPhoto.getImage() != null) {
                    showPicture(detailsPhoto.getImage());
                }
            }
        });
        detailsBiography.setEditable(false);

        ObservableList<String> externalSources = FXCollections.observableArrayList("RottenTomatoes");
        SelectExternRating.setPromptText("Select source");
        SelectExternRating.setItems(externalSources);

        this.subtitles.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> commonWords((Subtitle) newValue));
        this.subtitles.getSortOrder().add(subtitleLanguage);

        wordCloudNumber.setLegendVisible(false);
        wordCloudNumber.setAnimated(false);
        wordCloudMostWords.setLegendVisible(false);

        this.commonWords((Subtitle) this.subtitles.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void onActionPrevious() {
        logger.info("Select previous Movie from the DB-List");
        Movie tmp = selectedFile;
        int selInt = mainGuiController.getMovieTable().getSelectionModel().getSelectedIndex();

        if (selInt == 0 && mainGuiController.getMovieTable().getItems().size() == 1) {
            this.createInfoDialog("This is your first and only movie in the list!");
        } else {
            mainGuiController.getMovieTable().getSelectionModel().selectPrevious();
            selectedFile = mainGuiController.getMovieTable().getSelectionModel().getSelectedItem();
            if (tmp == selectedFile) {
                this.createInfoDialog("First movie in the list already selected!");
            } else {
                this.cleanup();
                mainGuiController.getMovieTable().getSelectionModel().clearSelection(selInt);
                setFields();
            }
        }
    }

    @FXML
    public void onActionNext() {
        logger.info("Select next Movie from the DB-List");
        Movie tmp = selectedFile;
        int selInt = mainGuiController.getMovieTable().getSelectionModel().getSelectedIndex();

        if (selInt == 0 && mainGuiController.getMovieTable().getItems().size() == 1) {
            this.createInfoDialog("This is your last and only movie in the list!");
        } else {
            mainGuiController.getMovieTable().getSelectionModel().selectNext();
            selectedFile = mainGuiController.getMovieTable().getSelectionModel().getSelectedItem();
            if (tmp == selectedFile) {
                this.createInfoDialog("Last movie in the list already selected!");
            } else {
                mainGuiController.getMovieTable().getSelectionModel().clearSelection(selInt);
                this.cleanup();
                setFields();
            }
        }
    }

    @FXML
    public void onActionPlay() {
        this.stage.close();
        this.mainGuiController.showInternalPlayerGui(this.selectedFile);
    }

    /**
     * Refreshes the metadate of the movie by the given TMDBID.
     */
    @FXML
    public void onActionRefreshData() {
        logger.debug("Updating metadata!");
        try {
            if (this.selectedFile.getPath() != null) {
                movieService.deleteMovie(this.selectedFile);
                File toScan = new File(this.selectedFile.getPath());
                crawlerService.setPath(Paths.get(toScan.getParent()));
                crawlerService.setFile(this.selectedFile.getPath());
                crawlerService.scan();
                this.mainGuiController.getScanned().clear();
                this.mainGuiController.getScanned().addAll(crawlerService.getMovies());
                this.mainGuiController.showAddMovieGui();
                this.stage.close();
                mainGuiController.showAddMovieGui();
            }
        } catch (ServiceException e) {
            logger.error(e);
        }

//        Movie selected = this.mainGuiController.getMovieTable().getSelectionModel().getSelectedItem();
//        Movie movie = null;
//        try {
//            //ArrayList<Movie> movies = apiService.search(this.detailsTitle.getText(), 0, detailsLanguage.getText(), false, 0);
//            int year = 0;
//            try {
//                year = Integer.parseInt(detailsYear.getText());
//            } catch (Exception e) {
//                // Year creation failed.
//            }
//            ArrayList<Movie> movies = apiService.search(this.detailsTitle.getText(), year, detailsLanguage.getText(), false, 0);
//            if (movies.size() > 0) {
//                movie = apiService.getMovieByTMDBID(movies.get(0).getIdTMDB(), detailsLanguage.getText());
//                //Movie movie = apiService.getMovieByTMDBID(selected.getIdTMDB(), selected.getLanguage());
//                movie.setMid(selected.getMid());
//                movie.setPath(selected.getPath());
//                try {
//                    movieService.updateMovie(movie);
//                    createInfoDialog("Metadata update successful!");
//                } catch (ServiceException e) {
//                    logger.error(e);
//                    createInfoDialog("Refreshing faild.");
//                } catch (MovieServiceException e) {
//                    createInfoDialog("Refreshing faild.");
//                    logger.error(e);
//                }
//                this.selectedFile = movie;
//                this.setFields();
//            }
//        } catch (ServiceException e) {
//            logger.error(e);
//            createInfoDialog("Sorry, no matches, could not update!");
//        }
    }

    @FXML
    public void onActionOpenFile() {
        logger.info("OpenFile Button Pressed");
        try {
            movieService.showMovieExplorer(selectedFile);
        } catch (ServiceException e) {
            this.createExceptionDialog(e);
            logger.error(e.getMessage());
        }

    }

    @FXML
    public void onActionChangePoster() {
        logger.info("Press Change Poster Button");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select new poster");

        if (Files.exists(Paths.get("/home/markus"))) {
            fileChooser.setInitialDirectory(new File("/home/markus/"));
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Formats", "*.jpg", "*.png", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        File selectedNewPoster = fileChooser.showOpenDialog(this.mainStage);
        logger.info("Selected File: " + selectedNewPoster);

        if (selectedFile != null && Files.exists(Paths.get(selectedNewPoster.toURI()))) {
            logger.info("File-Path:" + selectedNewPoster.getPath());
            File outImage = new File((this.getClass().getClassLoader().getResource("poster/").getPath().replaceAll("%20", " ").toString() + this.selectedFile.getPosterPath()));
            File inImage = new File(selectedNewPoster.getPath());

            Action response = this.createConfirmationDialog("Are you sure you want to change the poster of this movie?");

            if (this.dialogBoxConfirmation(response)) {

                boolean fail = false;
                try {
                    movieService.changePoster(this.selectedFile, inImage, outImage);

                } catch (ServiceException e) {
                    //Dialog
                    this.createExceptionDialog(e);
                    logger.info("Change Poster failed");
                    logger.error(e.getMessage());
                    fail = true;
                }

                if (!fail) {
                    logger.info("Change Poster successfully");
                    this.detailsPoster.setImage(loadImageFromPath(this.getClass().getClassLoader().getResource("poster/").getPath().replaceAll("%20", " ").toString() + this.selectedFile.getPosterPath()));
                }
            }
        }
    }

    @FXML
    public void onActionChangeMetaData() {

        logger.info("Change Metadata manually");
        if (validateForm()) {
            selectedFile.setTitle(this.detailsTitle.getText());
            selectedFile.setGenre(selectedFile.getGenre().replace(", ", ","));
            selectedFile.setLanguage(this.detailsLanguage.getText());
            selectedFile.setPlot(this.detailsPlot.getText());
            selectedFile.setBudget(this.detailsBudget.getText().replaceAll("[.$]", ""));
            selectedFile.setRevenue(this.detailsRevenue.getText().replaceAll("[.$]", ""));
            try {
                selectedFile.setRunTime(Integer.parseInt(this.detailsRuntime.getText()));
            } catch (NumberFormatException e) {
                logger.error(e);
            }
            selectedFile.setTagLine(this.detailsTagline.getText());
            selectedFile.setSimilarFilms(this.detailsSimilarMovies.getText());
            selectedFile.setYear(Integer.valueOf(this.detailsYear.getText()));

            try {
                movieService.updateMovie(selectedFile);
                this.title.setText(selectedFile.getTitle());
                logger.debug(mainGuiController);
                logger.info("Changes saved!");
                this.createInfoDialog("Changes saved");

            } catch (MovieServiceException e) {
                logger.error(e.getMessage());
                this.createExceptionDialog(e);
            } catch (ServiceException e) {
                logger.error(e.getMessage());
                this.createExceptionDialog(e);
            }
            mainGuiController.refreshItem();
        }
    }

    @FXML
    public void onActionInsertUserRating() {
        try {
            double rating = sliderRating.getValue();
            rating = Math.ceil(rating * 2) / 2;
            ratingService.createUpdateUserRating(this.selectedFile.getMid(), rating);
            setRatingTable();
        } catch (ServiceException e) {
            this.createExceptionDialog(e);
            logger.error(e.getMessage());
        }
        mainGuiController.refreshItem();
    }

    @FXML
    public void onActionInsertExternRating() {
        try {
            String selectedSource = (String) SelectExternRating.getSelectionModel().getSelectedItem();
            switch (selectedSource) {
                case "RottenTomatoes": {
                    logger.info("SELECTED SOURCE = RottenTomatoes");
                    ratingService.createUpdateTomatoRating(this.selectedFile.getMid(),
                            tomatoService.getMovieScore(tomatoService.searchMovie(this.selectedFile.getTitle(), this.selectedFile.getYear())));
                    setRatingTable();
                }
            }
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            this.createInfoDialog(e.getMessage());
        } catch (NullPointerException e) {
            this.createInfoDialog("Please select source before adding new external rating");
            logger.error(e.getMessage());
        }
        mainGuiController.refreshItem();
    }

    @FXML
    public void onActiondeleteRatings() {
        logger.info("Delete button pressed");

        List<Rating> selectedList = detailsRating.getSelectionModel().getSelectedItems();
        if (selectedList.isEmpty()) {
            this.createInfoDialog("Please select a rating you want to delete.");
            logger.error("no Rating is selected");
        }
        for (Rating r : selectedList) {
            try {
                ratingService.deleteRating(r);
                detailsRating.getItems().remove(r);
                this.createInfoDialog("Selected rating(s) successfully deleted.");
            } catch (ServiceException e) {
                this.createExceptionDialog(e);
                logger.error(e.getMessage());
            }
        }
        mainGuiController.refreshItem();
    }

    @FXML
    public void OnActionFavourite() {
        try {
            if (detailsFavouriteEmpty.isVisible()) {
                movieService.addToFavorite(this.selectedFile);
                detailsFavouriteEmpty.setVisible(false);
                detailsFavouriteEmpty.setFitWidth(0);
                detailsFavouriteEmpty.setFitHeight(0);
                detailsFavouriteEmpty.setManaged(false);

                detailsFavouriteFull.setVisible(true);
                detailsFavouriteFull.setFitWidth(25);
                detailsFavouriteFull.setFitHeight(25);
                detailsFavouriteFull.setManaged(true);
            } else {
                movieService.removeFromFavorite(this.selectedFile);
                detailsFavouriteFull.setVisible(false);
                detailsFavouriteFull.setFitWidth(0);
                detailsFavouriteFull.setFitHeight(0);
                detailsFavouriteFull.setManaged(false);

                detailsFavouriteEmpty.setVisible(true);
                detailsFavouriteEmpty.setFitWidth(25);
                detailsFavouriteEmpty.setFitHeight(25);
                detailsFavouriteEmpty.setManaged(true);
            }
            mainGuiController.refreshItem();
        } catch (ServiceException e) {
            this.createExceptionDialog(e);
            logger.error(e.getMessage());
        }
    }

    public void setButtonInvisible() {
        previous.setVisible(false);
        next.setVisible(false);
    }

    @FXML
    public void onActionClearChanges() {
        if (selectedFile != null) {
            logger.info("Changes deleted before saving it.");

            try {
                selectedFile = movieService.readMovie(selectedFile.getMid());
                setFields();
            } catch (ServiceException e) {
                this.createExceptionDialog(e);
                logger.error("File not founded");
            }
        }
    }

    @FXML
    public void onActionAddSubtitle() {
        logger.info("Add Subtitle Button pressed");
        logger.info("Select local Modus");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select new Subtitle");
        if (Files.exists(Paths.get("/home/markus"))) {
            fileChooser.setInitialDirectory(new File("/home/markus/"));
        }
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Formats", "*.srt", "*.sub"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        final String[] dialogLang = {""};
        final String[] dialogCom = {""};

        File selectedSubtitle = fileChooser.showOpenDialog(this.mainStage);
        logger.info("Selected File: " + selectedSubtitle);


        if (selectedFile != null && Files.exists(Paths.get(selectedSubtitle.toURI()))) {

            try {
                String language = subtitleService.addSubtitleLocal(this.selectedFile, selectedSubtitle);
                Optional<Pair<String, String>> result = this.subtitleDialog(language, "");

                result.ifPresent(pair -> {
                    dialogLang[0] = pair.getKey();
                    dialogCom[0] = pair.getValue();
                });

                subtitleService.create(selectedSubtitle.getName(), this.selectedFile, dialogLang[0], dialogCom[0]);
                setSubtitleTable();
                mainGuiController.refreshItem();

            } catch (ServiceException e) {
                this.createExceptionDialog(e);
            }
        }
    }

    @FXML
    public void onActionDeleteSubtitle() {
        logger.info("Delete Subtitle button pressed");

        List<Subtitle> selectedList = this.subtitles.getSelectionModel().getSelectedItems();
        if (selectedList.isEmpty()) {
            this.createInfoDialog("Please select a Subtitle you want to delete.");
            logger.error("no Subtitle is selected");
        } else {
            Action response = this.createConfirmationDialog("Are you sure you want to delete the selected subtitles(s)?");
            if (this.dialogBoxConfirmation(response)) {
                boolean fail = false;
                File f = null;
                for (Subtitle s : selectedList) {
                    try {
                        f = new File(this.getClass().getClassLoader().getResource("subtitles/").getPath().replaceAll("%20", " ").toString() + s.getLanguagePath());
                        subtitleService.delete(s);
                    } catch (ServiceException e) {
                        //Dialog
                        this.createExceptionDialog(e);
                        logger.error(e);
                        fail = true;
                    }
                }
                if (!fail) {
                    this.subtitles.getItems().removeAll(selectedList);
                    this.subtitles.getSelectionModel().clearSelection();
                    this.countDifferentWords(this.subtitles.getItems());
                    f.delete();
                    mainGuiController.refreshItem();
                }
            }
        }
    }

    @FXML
    public void onActionUpdateSubtitle() {
        logger.info("Update Subtitle Button pressed");
        List<Subtitle> selectedList = this.subtitles.getSelectionModel().getSelectedItems();

        final String[] dialogLang = {""};
        final String[] dialogCom = {""};

        Subtitle original = null;

        if (selectedList.isEmpty()) {
            this.createInfoDialog("Please select a Subtitle you want to update.");
            logger.error("no Subtitle is selected");
        } else {
            for (Subtitle s : selectedList) {
                original = s;
                Optional<Pair<String, String>> result = this.subtitleDialog(s.getLanguage(), s.getComment());

                result.ifPresent(pair -> {
                    dialogLang[0] = pair.getKey();
                    dialogCom[0] = pair.getValue();
                });

                if (dialogLang[0].length() < 2) {

                } else {
                    if (dialogLang[0].equals(original.getLanguage()) && dialogCom[0].equals(original.getComment())) {
                        this.createInfoDialog("No changes saved.");
                    } else {
                        s.setComment(dialogCom[0]);
                        s.setLanguage(dialogLang[0]);
                        try {
                            subtitleService.update(this.selectedFile, s);
                            this.createInfoDialog("Update Subtitle data was successfull!");
                            setSubtitleTable();
                            this.countDifferentWords(this.subtitles.getItems());
                        } catch (ServiceException e) {
                            this.createExceptionDialog(e);
                        }
                    }
                }
            }
        }
    }

    @FXML
    public void onActionSearchSubtitles() {
        logger.info("Search Subtitles Button pressed");

        List<SubtitleSearch> searchList = null;
        try {
            subtitleService.login();
            searchList = subtitleService.searchSubtitles(this.selectedFile);
            subtitleService.logout();
        } catch (ServiceException e) {
            this.createExceptionDialog(e);
        }

        searchSubLanguage.setCellValueFactory(new PropertyValueFactory<SubtitleSearch, String>("languageName"));
        searchSubtitleFormat.setCellValueFactory(new PropertyValueFactory<SubtitleSearch, String>("subFormat"));
        searchSubHearingImpaired.setCellValueFactory(new PropertyValueFactory<SubtitleSearch, String>("subHearingImpaired"));
        this.searchSubs = FXCollections.observableList(searchList);
        logger.info(this.searchSubs.size());

        searchSubtitlesTable.setItems(this.searchSubs);
        searchSubtitlesTable.getSortOrder().add(searchSubLanguage);
        searchSubtitlesTable.sort();
    }

    @FXML
    public void onActionAddSubtitleExtern() {
        logger.info("Add Subtitles Button pressed");

        String idSubtitleFile = "";
        SubtitleSearch file = null;

        List<SubtitleSearch> selectedList = this.searchSubtitlesTable.getSelectionModel().getSelectedItems();
        if (selectedList.isEmpty()) {
            this.createInfoDialog("Please select a Subtitle you want to add.");
            logger.error("no Subtitle is selected");
        } else {
            for (SubtitleSearch s : selectedList) {
                file = s;
            }

            try {
                subtitleService.login();
                subtitleService.addSubtitleExtern(file, this.selectedFile);
                subtitleService.logout();
                setSubtitleTable();
                this.countDifferentWords(this.subtitles.getItems());
                this.createInfoDialog("Add Subtitle-File Successfully");
                mainGuiController.refreshItem();
            } catch (ServiceException e) {
                this.createExceptionDialog(e);
            }
        }
    }

    @FXML
    public void onActionSearch() {
        if (trailerSearch.getText() != null && !trailerSearch.getText().trim().isEmpty()) {
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call()
                                throws InterruptedException {
                            loadTrailers();
                            return null;
                        }
                    };
                }
            };
            createDialogForService("Loading Trailers", service);
            service.start();
        } else {
            this.createInfoDialog("The textfield can not be empty when loading. Please insert a text to load the trailerlist.");
            trailerSearch.setText(this.selectedFile.getTitle() + " trailer");
        }
    }

    private void loadTrailers() {
        this.trailerService = new TrailerServiceImpl();
        List<Trailer> trailerList = null;
        trailerList = trailerService.getAllTrailers(trailerSearch.getText());
        ObservableList<Trailer> oTrailerList = FXCollections.observableArrayList(trailerList);
        this.trailerListColumn.setCellValueFactory(new PropertyValueFactory<Trailer, String>("linkText"));
        this.webLinkColumn.setCellValueFactory(new PropertyValueFactory<Trailer, String>("webLink"));
        trailerListTable.setItems(oTrailerList);
    }

    @FXML
    public void onActionPlayWithBrowser() {
        Trailer trailer = null;
        trailer = trailerListTable.getSelectionModel().getSelectedItem();
        if (trailer == null) {
            this.createInfoDialog("Please select a item to play.");
            logger.error("select trailer before play");
        } else {
            try {
                Desktop.getDesktop().browse(new URI(trailer.getWebLink()));
            } catch (IOException e) {
                logger.error(e.getMessage());
            } catch (URISyntaxException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @FXML
    public void onActionPlayWithWebView() {
        Trailer trailer = null;
        trailer = trailerListTable.getSelectionModel().getSelectedItem();
        if (trailer == null) {
            this.createInfoDialog("Please select a item to play.");
            logger.error("select trailer before play");
        } else {
            String src = "https://www.youtube.com/embed/" + trailer.getWebLink().substring(32);
            trailerWebView.getEngine().load(src);
            //trailerWebView.getEngine().loadContent("<iframe width='480' height='360' src='"+src+"' frameborder='0' allowfullscreen></iframe>");
        }
    }

    @FXML
    public void onActionPlayWithVLC() {
        Trailer trailer = null;
        trailer = trailerListTable.getSelectionModel().getSelectedItem();
        if (trailer == null) {
            this.createInfoDialog("Please select a item to play.");
            logger.error("select trailer before play");
        } else {
            try {
                VLCUtil.runVLC(trailer.getWebLink());
            } catch (Exception e) {
                this.createInfoDialog("Please install the newest VLC-Player Version, if you run a 64bit-Windows. Should work with VLC 2.2.0 and above");
                logger.error(e.getMessage());
                this.createExceptionDialog(e);
            }
        }
    }



    /*
        Window-Openers ("Showers")
     */

    @FXML
    public void onActionTrailer() {

    }

    /*
        Helpers
     */


    @FXML
    public void handleTabChange() {
        if (informationTab.isSelected()) {
            logger.debug("InfoTab selected");
//            onActionClearChanges();
            setFields();
        } else if (ratingTab.isSelected()) {
            logger.debug("RatingTab selected");
        } else if (trailerTab.isSelected()) {
            logger.debug("trailerTab selected");
            if (this.trailerListTable.getItems().size() == 0) {
                onActionSearch();
            }
        } else if (subtitleTab.isSelected()) {
            logger.debug("SubtitleTab selected");
        }
    }

    @FXML
    public void showPicture(Image image) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ShowPicture.fxml"));
        AnchorPane page = null;
        try {
            page = loader.load();
            ShowPictureController controller = loader.getController();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Picture");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            if (this.showPictureAnchor != null) {
                this.showPictureAnchor.close();
            }
            this.showPictureAnchor = dialogStage;
            if (image != null) {
                controller.setStage(dialogStage);
                controller.setImage(image);
            } else {
                logger.error("No image set.");
            }

            // Show the dialog and wait until the user closes it
            dialogStage.show();
        } catch (IOException e) {
            logger.error("Reading a necessary file failed: " + e);
            e.printStackTrace();
        }
    }

    private void setFields() {
        this.thread = new Thread() {
            @Override
            public void run() {
                logger.info("Updating all tabs.");

                Platform.runLater(() -> {
                    title.setText(selectedFile.getTitle());
                    path.setText(selectedFile.getPath());

                    detailsTitle.setText(selectedFile.getTitle());
                    detailsGenres.setText(selectedFile.getGenre().replace(",", ", "));
                    detailsLanguage.setText(selectedFile.getLanguage());
                    detailsPlot.setText(selectedFile.getPlot());
                    detailsYear.setText(String.valueOf(selectedFile.getYear()));
                    detailsBudget.setText("$" + NumberFormat.getNumberInstance(Locale.GERMAN).format(Integer.parseInt(selectedFile.getBudget())));
                    detailsRevenue.setText("$" + NumberFormat.getNumberInstance(Locale.GERMAN).format(Integer.parseInt(selectedFile.getRevenue())));
                    detailsRuntime.setText(String.valueOf(selectedFile.getRunTime()));
                    detailsTagline.setText(selectedFile.getTagLine());
//                    detailsSimilarMovies.setText(selectedFile.getSimilarFilms());        // set rating from external sources
                });
                Platform.runLater(() -> {
                    roles.clear();
                    roles.addAll(selectedFile.getStaff());
                    if (roles.size() >= 1) {
                        detailsStaff.getSelectionModel().select(0);
                    }
                });
                Platform.runLater(() -> {
                    Image img = loadImageFromPath(getClass().getClassLoader().getResource("poster/").getPath().replaceAll("%20", " ").toString() + selectedFile.getPosterPath());
                    detailsPoster.setImage(img);
                    trailerSearch.setText(selectedFile.getTitle() + " trailer");
                });
                Platform.runLater(() -> {
                    setRatingTable();
                    setSubtitleTable();
                    searchSubtitlesTable.getItems().clear();
                    countDifferentWords(subtitles.getItems());
                    notavailable.setVisible(false);
                });
                Platform.runLater(() -> {
                    //set rotten tomatoes review
                    String title = selectedFile.getTitle();
                    int year = selectedFile.getYear();
                    Platform.runLater(() -> {
                        String rev = "";
                        try {
                            for (String s : tomatoService.getMovieReview(tomatoService.searchMovie(title, year))) {
                                rev = rev + "\n" + "---" + "\n" + s;
                            }
                            rotenTomatosReview.setText(rev.substring(4));
                        } catch (ServiceException e) {
                            //createExceptionDialog(e);
                            logger.error(e.getMessage());
                        }
                    });
                    //logger.info("This is my review: "+rotenTomatosReview.getText());
                });
            }
        };
        thread.start();

    }

    private void countDifferentWords(ObservableList list) {
        ObservableList<String> all = FXCollections.observableArrayList(new ArrayList<String>());
        wordCloudNumber.getData().clear();
        series.getData().clear();

        for (int i = 0; i < subtitles.getItems().size(); i++) {
            Subtitle s = (Subtitle) subtitles.getItems().get(i);
            if (!s.getCommonWords().isEmpty()) {
                XYChart.Data<String, Integer> data = new XYChart.Data<String, Integer>(s.getLanguage(), s.getNumberOfWords());
                series.getData().add(data);
                all.add(s.getLanguage());
            }
        }
        xAxis.setCategories(all);
        xAxis.setVisible(true);
        xAxis.setAutoRanging(true);
        yAxis.setUpperBound(1400);
        yAxis.setTickUnit(100);
        yAxis.setMinorTickLength(2);
        yAxis.setAutoRanging(false);
        wordCloudNumber.getData().add(series);
    }

    private void commonWords(Subtitle s) {
        try {
            if (s != null) {
                if (!s.getCommonWords().isEmpty()) {
                    List<WordCloud> commonlist = subtitleService.commonSortedList(s);
                    ObservableList<PieChart.Data> list = FXCollections.observableList(new ArrayList<PieChart.Data>());
                    for (WordCloud w : commonlist) {
                        list.add(new PieChart.Data(w.getWord(), w.getCount()));
                    }
                    this.notavailable.setVisible(false);
                    this.notavailable.setManaged(false);
                    wordCloudMostWords.setVisible(true);
                    wordCloudMostWords.setManaged(true);
                    wordCloudMostWords.setData(list);
                } else {
                    wordCloudMostWords.setVisible(false);
                    wordCloudMostWords.setManaged(false);
                    this.notavailable.setVisible(true);
                    this.notavailable.setManaged(true);
                }
            }
        } catch (ServiceException e) {
            this.createExceptionDialog(e);
        }
    }

    private void changeRole(Role role) {
        if (role != null) {
//            logger.info("Role: " + role.getRole());
//            logger.info("Person: " + role.getPerson());
//            logger.info("Pfad: " + this.getClass().getClassLoader().getResource("portraits").getPath().replaceAll("%20", " ").toString() + role.getPerson().getPhotoPath());
            this.detailsPhoto.setImage(loadImageFromPath(this.getClass().getClassLoader().getResource("portraits").getPath().replaceAll("%20", " ").toString() + role.getPerson().getPhotoPath()));
            this.detailsBiography.setText(role.getPerson().getBiography());
        }
    }

    private void setRatingTable() {
        try {
            List<Rating> ratingList = new ArrayList<Rating>();
//            logger.info(ratingList.size());
//            logger.debug(ratingService);
//            logger.debug(movieService);
//            logger.debug(tomatoService);
            ratingList = ratingService.readRatingsForMovie(this.selectedFile);
//            logger.info(ratingList.size());

            ActualRatingFrom.setCellValueFactory(new PropertyValueFactory<Rating, String>("source"));
            ActualRatingValue.setCellValueFactory(new PropertyValueFactory<Rating, Double>("rating"));
            this.detailsFavouriteFull.setImage(loadImageFromPath(this.getClass().getClassLoader().getResource("pics/full.gif").getPath().replaceAll("%20", " ").toString()));
            this.detailsFavouriteEmpty.setImage(loadImageFromPath(this.getClass().getClassLoader().getResource("pics/empty.gif").getPath().replaceAll("%20", " ").toString()));

            //show correct state of favourite checkbox on startup
            try {
                if (movieService.isFavourite(this.selectedFile)) {
                    detailsFavouriteEmpty.setVisible(false);
                    detailsFavouriteEmpty.setFitWidth(0);
                    detailsFavouriteEmpty.setFitHeight(0);
                    detailsFavouriteEmpty.setManaged(false);

                    detailsFavouriteFull.setVisible(true);
                    detailsFavouriteFull.setFitHeight(25);
                    detailsFavouriteFull.setFitWidth(25);
                    detailsFavouriteFull.setManaged(true);

                } else {
                    detailsFavouriteFull.setVisible(false);
                    detailsFavouriteFull.setFitHeight(0);
                    detailsFavouriteFull.setFitWidth(0);
                    detailsFavouriteFull.setManaged(false);

                    detailsFavouriteEmpty.setVisible(true);
                    detailsFavouriteEmpty.setFitHeight(25);
                    detailsFavouriteEmpty.setFitWidth(25);
                    detailsFavouriteEmpty.setManaged(true);
                }
            } catch (ServiceException e) {
                this.createExceptionDialog(e);
                logger.error(e.getMessage());
            }


            ObservableList ratingObservableList = FXCollections.observableList(ratingList);
            detailsRating.setItems(ratingObservableList);
//            logger.info(ratingObservableList.size());

            detailsRating.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } catch (ServiceException e) {
            // TODO FIX THIS
            //this.createExceptionDialog(e);
            logger.error(e.getMessage());
        }
    }

    private void setSubtitleTable() {
        List<Subtitle> subtitleList = new ArrayList<Subtitle>();
//        logger.info(subtitleList.size());
        try {
            subtitleList = subtitleService.readSubtitlesForMovie(this.selectedFile);
        } catch (ServiceException e) {
            logger.info(e.getMessage());
        }
//        logger.info(subtitleList.size());
        subtitleLanguage.setCellValueFactory(new PropertyValueFactory<Subtitle, String>("language"));
        subtitleComment.setCellValueFactory(new PropertyValueFactory<Subtitle, String>("comment"));
        ObservableList subtitlesObservableList = FXCollections.observableList(subtitleList);
        subtitles.setItems(subtitlesObservableList);
        subtitles.getSortOrder().add(subtitleLanguage);
        subtitles.sort();
        logger.info(subtitlesObservableList.size());
        subtitles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private Image loadImageFromPath(String path) {
        Image img = null;
        File file = new File(path);
        if (file.exists()) {
            File photo = new File(file.toString());
            try {
                InputStream io = new FileInputStream(photo);
                img = new Image(io);
            } catch (FileNotFoundException e) {
                logger.error("Image file not found!");
            }
        } else {
            logger.info("No image file available available.");
        }
        return img;
    }

    public void cleanup() {
        this.trailerWebView.getEngine().load("");
        this.trailerListTable.getItems().clear();
    }

    private boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean isEmpty(String str) {
        return (str == null || str.equals(""));
    }

    private boolean validateForm() {
        try {
            if (isEmpty(detailsTitle.getText()) ||
                    !isNumeric(this.detailsBudget.getText().replaceAll("[.$]", "")) ||
                    !isNumeric(this.detailsRevenue.getText().replaceAll("[.$]", "")) ||
                    !isNumeric(this.detailsRuntime.getText()) ||
                    (Integer.parseInt(this.detailsRuntime.getText()) < 0) ||
                    !isNumeric(this.detailsYear.getText()) ||
                    Integer.parseInt(this.detailsYear.getText()) < 1800 ||
                    Integer.parseInt(this.detailsYear.getText()) > 2200
                    ) {
                createInfoDialog("Error while saving your information, please check your input!\n" +
                        "Title must be set.\n" +
                        "Budget, Revenue and Runtime need to be positive numbers.\n" +
                        "The year must be a number between 1800 and 2200.");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*
        Getters & Setters
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

    public void setMovie(Movie movie, MainGuiController maingui) {
        mainGuiController = maingui;
        this.selectedFile = movie;
//            setFields();
    }

}