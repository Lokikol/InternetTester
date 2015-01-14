package eu.derloki.internetchecker;


import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class RootFrameController {

	@FXML
	TextField tfUrl;
	
	@FXML
	TextField tfInterval;
	
	@FXML
	CheckBox chAutostart;
	
	@FXML
	ComboBox<String> timeUnits;
	
	@FXML
	CheckBox chAutopopup;
	
	private Main m;
	
	public void setMain(Main m){
		this.m = m;
	}
	
	public void init() {
		timeUnits.getItems().addAll(TimeUnit.SECONDS.name(),TimeUnit.MINUTES.name());
		handleCancel();
	}
	
	@FXML
	public void handleSave(){
		String newUrl = tfUrl.getText();
		String newInterval = tfInterval.getText();
		String newTimeUnit = timeUnits.getValue();
		String newAutostart = ""+chAutostart.isSelected();
		String newAutopopup = ""+chAutopopup.isSelected();
		
		boolean validation = true;
		try{
			int test = Integer.parseInt(newInterval);
			boolean auto = Boolean.parseBoolean(newAutostart);
			boolean auto2 = Boolean.parseBoolean(newAutopopup);
		}
		catch(Exception e){
			validation = false;
		}
		
		if(validation)
			m.setNewInput(new String[]{newUrl,newInterval,newTimeUnit,newAutostart,newAutopopup});
		else{
			handleCancel();
		}
	}
	
	@FXML
	public void handleCancel(){
		String[] oldValues = m.cancelInput();
		
		String oldUrl = oldValues[0];
		String oldInterval = oldValues[1];
		String oldTimeValue = oldValues[2];
		String oldAutostart = oldValues[3];
		String oldAutopopup = oldValues[4];
		
		tfUrl.setText(oldUrl);
		tfInterval.setText(oldInterval);
		timeUnits.setValue(oldTimeValue);
		
		if(oldAutostart.equals("true")){
			chAutostart.setSelected(true);
		}
		else{
			chAutostart.setSelected(false);
		}
		
		if(oldAutopopup.equals("true")){
			chAutopopup.setSelected(true);
		}
		else{
			chAutopopup.setSelected(false);
		}
	}
	
}
