package eu.derloki.internetchecker;


import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
	
	@FXML
	Label lStatus;
	
	@FXML
	Label lAverage;
	
	@FXML
	Slider sAverage;
	
	@FXML
	Button saveButton;
	
	private Main m;
	private int sliderVal = 1;
	
	public void setMain(Main m){
		this.m = m;
	}
	
	public void init() {
		timeUnits.getItems().addAll(TimeUnit.SECONDS.name(),TimeUnit.MINUTES.name());
		sAverage.setMin(1);
		sAverage.setMax(100);
		sAverage.valueProperty().addListener((ov,oldval,newval)->{
			sliderVal = newval.intValue();
			changeAverage();
		});
		
		chAutostart.armedProperty().addListener((ov,oldval,newval)->{
			setTextOfButton();
		});
		handleCancel();
		setTextOfButton();
	}
	
	@FXML
	public void changeAverage(){
		lAverage.setText(String.format("Average of %d Packets",sliderVal));
	}
	
	@FXML
	public void handleSave(){
		String newUrl = tfUrl.getText();
		String newInterval = tfInterval.getText();
		String newTimeUnit = timeUnits.getValue();
		String newAutostart = ""+chAutostart.isSelected();
		String newAutopopup = ""+chAutopopup.isSelected();
		
		
		boolean validation = true;
		boolean auto = false;
		try{
			int test = Integer.parseInt(newInterval);
			auto = Boolean.parseBoolean(newAutostart);
			boolean auto2 = Boolean.parseBoolean(newAutopopup);
		}
		catch(Exception e){
			validation = false;
		}
		
		if(validation){
			m.setNewInput(new String[]{newUrl,newInterval,newTimeUnit,newAutostart,newAutopopup,""+sliderVal});
			if(auto)
				m.hideWhenShown();
		}
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
		String oldSliderval = oldValues[5];
		
		tfUrl.setText(oldUrl);
		tfInterval.setText(oldInterval);
		timeUnits.setValue(oldTimeValue);
		//sAverage.setValue(Double.parseDouble(oldSliderval));
		sAverage.valueProperty().set(Double.parseDouble(oldSliderval));
		sliderVal = Integer.parseInt(oldSliderval);
		changeAverage();
		
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

	public void showStatusReport(String p_statusReport) {
		// TODO Auto-generated method stub
		lStatus.setText(p_statusReport);
	}
	
	
	private void setTextOfButton(){
		if(chAutostart.isSelected()){
			saveButton.setText("Save&Start");
		}
		else
			saveButton.setText("Save");
	}
}
