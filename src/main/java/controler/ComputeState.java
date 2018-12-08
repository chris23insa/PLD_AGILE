package controler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import entities.DemandeDeLivraisons;
import entities.Itinerary;
import view.MainWindow;



/**
 * The state after the rounds have been computed and displayed.
 * @author PLD-HEXA-301
 *
 */
public class ComputeState extends DefaultState {
	
	@Override
	public void compute(Controler controler, MainWindow mainWindow) {
            int numberOfDeliveryMen =(int)mainWindow.getInputView().getNumOfRounds().getValue();
            int numberOfDeliveries = mainWindow.getGraphicalView().getMap().getTabDeliveryPoints().size();
            boolean compute = true;
            if(numberOfDeliveryMen>numberOfDeliveries) {
                int userAgree = mainWindow.showInformationDelivery(
                        numberOfDeliveryMen-numberOfDeliveries +
                        " delivery men will have nothing to do, "
                                + "do you want to continue ?");
                if (userAgree!=0) {
                    compute = false;
                }
            }
            if (compute) {
                List<Itinerary> itineraries = controler.getPathFinder().findPathTSP(mainWindow.getGraphicalView().getMap(),
                                Integer.min(numberOfDeliveries, numberOfDeliveryMen));
                if(itineraries != null) {
                        mainWindow.getGraphicalView().setItineraries(itineraries);
                        mainWindow.getGraphicalView().setDeliveryPointIndex(null);
                        mainWindow.getGraphicalView().setItineraryIndex(null);
                        mainWindow.getGraphicalView().repaint();
                        mainWindow.getTextualView().setItineraries(itineraries);
                        mainWindow.getTextualView().setDeliveryPointIndex(null);
                        mainWindow.getTextualView().setItineraryIndex(null);
                        mainWindow.getTextualView().displayListOfRounds();
                        mainWindow.getTextualView().revalidate();
                        mainWindow.getTextualView().repaint();
                        // On est deja dans ce etat
                        // controler.setCurState(controler.computeState);
                }
                else {
                        //TODO : pop up erreur de calcul
                        mainWindow.showError("Error when calculating"
                                + " routes");
                }
            }
            mainWindow.requestFocus();
        }
	
	@Override
	public void loadDeliveries(Controler controler, MainWindow mainWindow) {
		mainWindow.displayMessage("Load deliveries");
		mainWindow.displayMessage("Load plan");
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("/"));
		chooser.changeToParentDirectory();
		mainWindow.add(chooser);
		int returnValue = chooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			controler.setCurState(controler.planState);
			DemandeDeLivraisons ddl = controler.getParser().parseDelivery(selectedFile.toString());
                        if (ddl != null) {
                            mainWindow.getGraphicalView().getMap().setTabDeliveryPoints(new ArrayList<>());
                            mainWindow.getGraphicalView().getMap().fillTabDeliveryPoint(ddl);
                            mainWindow.getGraphicalView().setItineraries(null);
                            mainWindow.getGraphicalView().setDeliveryPointIndex(null);
                            mainWindow.getGraphicalView().setItineraryIndex(null);
                            mainWindow.getGraphicalView().repaint();
                            mainWindow.getTextualView().setItineraries(null);
                            mainWindow.getTextualView().setDeliveryPointIndex(null);
                            mainWindow.getTextualView().setItineraryIndex(null);
                            mainWindow.getTextualView().displayListOfRounds();
                            mainWindow.getTextualView().revalidate();
                            mainWindow.getTextualView().repaint();
                            controler.setCurState(controler.deliveriesState);
                        } else {
                            mainWindow.showError("The input xml file"
                                    + " is invalid");
                            // TODO : Prendre en compte que le parsing du fichier xml ddl
                            // n'est pas bien formé
                        } 
		}
	}
	
	@Override
	public void mouseClick(Controler controler, MainWindow mainWindow,int x,int y) {
		double latitude = mainWindow.getGraphicalView().getLatMax()-(y+mainWindow.getGraphicalView().getPointradius())/mainWindow.getGraphicalView().getWidthScale();
		double longitude =mainWindow.getGraphicalView().getLongMax()-(mainWindow.getGraphicalView().getWidth()-x-mainWindow.getGraphicalView().getPointradius())/mainWindow.getGraphicalView().getHeightScale();
		double minDistance=0.0062; // distance minimale 
		double distance;
		Integer nearestDeliveryPoint = null;
		int numberOfDeliveryPoints = mainWindow.getGraphicalView().getMap().getTabDeliveryPoints().size();
		for(int i=0;i<numberOfDeliveryPoints;i++) {
			double curLatitude = mainWindow.getGraphicalView().getMap().getCoordinates()[mainWindow.getGraphicalView().getMap().getTabDeliveryPoints().get(i).getKey()].getLatitude();
			double curLongitude = mainWindow.getGraphicalView().getMap().getCoordinates()[mainWindow.getGraphicalView().getMap().getTabDeliveryPoints().get(i).getKey()].getLongitude();
			distance=Math.sqrt(Math.pow(latitude-curLatitude, 2)+Math.pow(longitude-curLongitude, 2));
			if(distance <minDistance) {
				minDistance=distance;
				nearestDeliveryPoint=i;
			}
		}
		if(nearestDeliveryPoint != null) {
			double curLatitude = mainWindow.getGraphicalView().getMap().getCoordinates()[mainWindow.getGraphicalView().getMap().getTabDeliveryPoints().get(nearestDeliveryPoint).getKey()].getLatitude();
			double curLongitude = mainWindow.getGraphicalView().getMap().getCoordinates()[mainWindow.getGraphicalView().getMap().getTabDeliveryPoints().get(nearestDeliveryPoint).getKey()].getLongitude();
			latitude = (mainWindow.getGraphicalView().getLatMax() - mainWindow.getGraphicalView().getMap().getCoordinates()[mainWindow.getGraphicalView().getMap().getTabDeliveryPoints().get(nearestDeliveryPoint).getKey()].getLatitude()) * mainWindow.getGraphicalView().getWidthScale();
			longitude = (mainWindow.getGraphicalView().getLongMax() - mainWindow.getGraphicalView().getMap().getCoordinates()[mainWindow.getGraphicalView().getMap().getTabDeliveryPoints().get(nearestDeliveryPoint).getKey()].getLongitude()) * mainWindow.getGraphicalView().getHeightScale();
			//Finding the itinerary that includes this delivery point
			boolean globalFound = false;
			boolean localFound = false;
			int itineraryNumber=0;
			int deliveryPointNumber=0;
			int numberOfStops;
			List<Itinerary> itineraries = mainWindow.getGraphicalView().getItineraries();
			int numberOfItineraries = itineraries.size();
			while (!globalFound && itineraryNumber< numberOfItineraries) {
				deliveryPointNumber=0;
				numberOfStops = itineraries.get(itineraryNumber).getGeneralPath().size();
				while(!localFound && deliveryPointNumber < numberOfStops) {
					//If it's the good delivery point
					if(itineraries.get(itineraryNumber).getGeneralPath().get(deliveryPointNumber).getCoordinate() == mainWindow.getGraphicalView().getMap().getCoordinates()[mainWindow.getGraphicalView().getMap().getTabDeliveryPoints().get(nearestDeliveryPoint).getKey()]) {
						localFound=true;
						globalFound=true;
					}
					else {
						deliveryPointNumber++;
					}
				}
				if(!localFound) {
					itineraryNumber++;
				}
			}
			mainWindow.getGraphicalView().setItineraryIndex(itineraryNumber);
			mainWindow.getGraphicalView().setDeliveryPointIndex(deliveryPointNumber);
			mainWindow.getGraphicalView().repaint();
			mainWindow.requestFocus();
			mainWindow.getTextualView().setItineraryIndex(itineraryNumber);
			mainWindow.getTextualView().setDeliveryPointIndex(deliveryPointNumber);
			mainWindow.getTextualView().revalidate();
            mainWindow.getTextualView().repaint();
			controler.setCurState(controler.detailState);
		}
		
	}
}
