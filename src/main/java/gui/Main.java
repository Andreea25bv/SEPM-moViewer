package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.CreateTargetFolderUtil;

import java.io.FileNotFoundException;

/**
 * Created by powerowle on 03.12.2014.
 */
public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private Stage mainStage;
    private static ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");

    public static void main(String[] args) {
        CreateTargetFolderUtil util = new CreateTargetFolderUtil();
        util.createTargetFolders();
        launch(args);
        //((ConfigurableApplicationContext)ctx).registerShutdownHook();
    }

    public void start(Stage primaryStage) throws Exception {
        this.mainStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainGui.fxml"));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                return ctx.getBean(clazz);
            }
        });
        Parent root = loader.load();

        primaryStage.setTitle("moViewer");
        primaryStage.setScene(new Scene(root, 1000, 700));
        //primaryStage.setResizable(false);
        primaryStage.show();

        MainGuiController mainGuiController = loader.getController();
        mainGuiController.setMainStage(mainStage);
        mainGuiController.setMain(this);

    }

    public ApplicationContext getCtx() {
        return ctx;
    }
}
