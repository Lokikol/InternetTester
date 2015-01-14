package eu.derloki.internetchecker;
	
import java.util.Properties;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import eu.derloki.util.language.Lang;
import eu.derloki.util.properties.PropertiesHelper;


public class Main extends Application {
	private Stage primaryStage;
	private Properties config;
	private String language;
	private Controller con;
	
	private AnchorPane root;
	private RootFrameController controller;
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Internet Tester");
		this.primaryStage = primaryStage;
		Platform.setImplicitExit(false);
		
		this.primaryStage.setOnCloseRequest((event)->{
			primaryStage.hide();
			event.consume();
		});
		
		initRootLayout();
		
		myInit();
	
		

	}
	
	private void myInit(){
		//Properties setup ------------------------------------
				config = PropertiesHelper
						.getProperties("resources/config.properties");
				//-----------------------------------------------------

				//Language Setup --------------------------------------
				language = config.getProperty("language", "en_US");
				String[] localeStuff = language.split("_");
				Lang.INSTANCE.setup(localeStuff[0], localeStuff[1], "i18n.strings",
						true);
				//-----------------------------------------------------
				
				con = new Controller(this);
				con.afterUI();
				controller.init();
	}
	
	public void initRootLayout(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("RootFrame.fxml"));
			
			
			root = loader.load();
			
			controller = loader.getController();
			controller.setMain(this);
			
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		exit(false);
	}
	
	public void exit(boolean hardExit){
		
		con.exit();
		// TODO Auto-generated method stub
		Lang.INSTANCE.saveUndefinedStrings("src/i18n/strings.properties");
		Lang.INSTANCE.saveUndefinedStrings("src/i18n/strings_de_DE.properties");

		config.setProperty("language", language);
		PropertiesHelper.save("config", "Language setup");
		
		if(hardExit)
			System.exit(0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void showWhenHidden(){
		if(!primaryStage.isShowing())
			primaryStage.show();
	}

	public String[] cancelInput() {
		// TODO Auto-generated method stub
		return con.cancelInput();
	}

	public void setNewInput(String[] strings) {
		con.setNewInput(strings);
		
	}
}
