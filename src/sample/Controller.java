package sample;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Controller {
    private File logFileNow;
    @FXML
    private Pane firstWindow;
    @FXML
    private Pane secondWindow;
    @FXML
    private Pane thirdWindow;
    @FXML
    public void pickLogFile(MouseEvent mouseEvent) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pick log file");
        File file = chooser.showOpenDialog(new Stage());
        this.logFileNow = file;
        Main.processData(file);

        Pane secondPane = FXMLLoader.load(getClass().getResource("sample2.fxml"));
        firstWindow.getChildren().setAll(secondPane);
    }
    @FXML
    private Button moreCPU;
    @FXML
    private AreaChart chartCPU;
    @FXML
    public void listMoreCPU(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("CPU");
        ArrayList<ArrayList<Object>> mapIter = Main.getMonot();

        ArrayList<String> namesList = new ArrayList<String>();
        HashMap<String, XYChart.Series> chartsMap = new HashMap<String, XYChart.Series>();

        // Get the interval of monotoring
        int maxSec = 0;
        for(ArrayList<Object> ao : mapIter){
            Object secO = ao.get(0);
            String secS = secO.toString();
            int sec =Integer.parseInt(secS);
            if(maxSec < sec){
                maxSec = sec;
            }
        }

        // Creates a map with the names of processes associated with the highest cpu values registed
        HashMap<String, Integer> namesAndCPU = new HashMap<String, Integer>();

        for (ArrayList<Object> ao : mapIter) {
            Object nameP = ao.get(1);
            String namePS = nameP.toString();

            Object cpuO = ao.get(3);
            String stringCpuV = cpuO.toString();
            int cpuV =Integer.parseInt(stringCpuV);
            if (namesAndCPU.keySet().contains(namePS)) {
                if (namesAndCPU.get(namePS) < cpuV) {
                    namesAndCPU.put(namePS, cpuV);
                }
            } else namesAndCPU.put(namePS, cpuV);
        }

        //DEBUG -- ate aqui tudo bem. namesAndCPU formed well
        for(String s : namesAndCPU.keySet()){
            System.out.print("Ola tudo bem, " + s + " // ");
            int x = namesAndCPU.get(s);
            System.out.println(x);
        }

        // Takes the 10 highest cpu values from the namesAndCPU map and puts them into the namesList
        int countTen = 0;
        int max = 0;
        int min = 0;
        while(countTen < 5){
            int compare = 0;
            String nameS = " ";
                for (String k : namesAndCPU.keySet()) {
                    if (namesAndCPU.get(k).compareTo(compare) > 0) {
                        compare = namesAndCPU.get(k);
                        nameS = k;
                    }
                }
            namesList.add(nameS);
            System.out.println("a key maxima é: " + nameS + " || o seu value é:" + namesAndCPU.get(nameS));
            namesAndCPU.remove(nameS,compare);

            if (compare > max) max = compare;

            if ( min == 0){
                min = compare;
            }
            else{
                if(compare < min){
                    min = compare;
                }
            }
            countTen++;
        }

        // ---- DEBUG -----
        for(int countNames = 0; countNames<namesList.size(); countNames++){
            System.out.println("NOME: " + namesList.get(countNames));
        }

        final NumberAxis xAxis = new NumberAxis(0,maxSec,1);
        final NumberAxis yAxis = new NumberAxis("Number (in centi-seconds) of the total system's CPU resources consumed",0,max+2000,1000);

        this.chartCPU = new AreaChart<>(xAxis,yAxis);
        this.chartCPU.setTitle("CPU usage");


        // creates a map with a process name as key and their chart as value
        for(String name : namesList){
            XYChart.Series chart = new XYChart.Series();
            chartsMap.put(name,chart);
        }

        // takes cpu data from those 10 processes and makes a graph
        for(String name : namesList){
                XYChart.Series chartSeries = chartsMap.get(name);
                chartSeries.setName(name);
                for (ArrayList<Object> ao : mapIter) {
                    if ((ao.get(1)).equals(name)) {
                        String testString2 = ao.get(0).toString();
                        int test2 = Integer.parseInt(testString2);
                        String testString3 = ao.get(3).toString();
                        int test3 = Integer.parseInt(testString3);
                        chartSeries.getData().add(new XYChart.Data(test2, test3));
                    }
                }

        this.chartCPU.getData().addAll(chartSeries);
        }

        Scene scene  = new Scene(this.chartCPU,800,600);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private Button backs3;
    @FXML
    public void backToMenu(MouseEvent mouseEvent) throws IOException {
        Pane secondPane = FXMLLoader.load(getClass().getResource("sample2.fxml"));
        thirdWindow.getChildren().setAll(secondPane);
    }
    @FXML
    private AreaChart chartMem;
    @FXML
    public void listMoreRAM(MouseEvent mouseEvent) {
        System.out.println("ola tudo bem");
    }

}
