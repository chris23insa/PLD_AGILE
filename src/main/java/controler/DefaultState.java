package controler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import entities.Coordinate;
import entities.Map;
import entities.Reseau;
import entities.Segment;
import view.MainWindow;

public class DefaultState implements State {

	@Override
	public void loadPlan(Controler controler, MainWindow mainWindow) {
		mainWindow.displayMessage("Load plan");
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("/"));
		chooser.changeToParentDirectory();
		mainWindow.add(chooser);
		int returnValue = chooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			// Ici rajouter l'appel � la m�thode qui traite l'xml
			Reseau reseau = controler.getParser().parseCityPlan(selectedFile.toString());
			Map map = new Map();
			map.fillMapIdAndCoordinate(reseau);
			map.fillGraph(reseau);
			mainWindow.getTextualView().setItineraries(null);
			mainWindow.getTextualView().repaint();
			mainWindow.getGraphicalView().setItineraries(null);
			mainWindow.getGraphicalView().setMap(map);
			mainWindow.getGraphicalView().repaint();
			controler.setCurState(controler.planState);
		}
	}

	@Override
	public void loadDeliveries(Controler controler, MainWindow mainWindow) {
	}

	@Override
	public void compute(Controler controler, MainWindow mainWindow) {

	}

}
