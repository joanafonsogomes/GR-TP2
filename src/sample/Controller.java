package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Controller {
    @FXML
    private ArrayList<ArrayList<Object>> mapIter = Main.getMonot();
    @FXML
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

        Pane secondPane = FXMLLoader.load(getClass().getResource("menu.fxml"));
        firstWindow.getChildren().setAll(secondPane);
    }
    @FXML
    private Button moreCPU;
    @FXML
    private Button buttonCPUFiveChart;
    @FXML
    private AreaChart chartCPU;
    @FXML
    private AreaChart chartRAM;

    /**
     * Creates the graphic of the 5 processes that consume more CPU
     */
    @FXML
    public void graphFiveMoreCPU(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("CPU");

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

        final NumberAxis xAxis = new NumberAxis(0,maxSec,1);
        final NumberAxis yAxis = new NumberAxis("Number (in centi-seconds) of system's CPU resources consumed",0,max+2000,1000);

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

    /**
     * Creates the graphic of the 5 processes that consume more RAM Memory
     */
    @FXML
    public void graphFiveMoreRAM(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("RAM");

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
        HashMap<String, Integer> namesAndRAM = new HashMap<String, Integer>();

        for (ArrayList<Object> ao : mapIter) {
            Object nameP = ao.get(1);
            String namePS = nameP.toString();

            Object ramO = ao.get(2);
            String stringRamV = ramO.toString();
            int ramV =Integer.parseInt(stringRamV);
            if (namesAndRAM.keySet().contains(namePS)) {
                if (namesAndRAM.get(namePS) < ramV) {
                    namesAndRAM.put(namePS, ramV);
                }
            } else namesAndRAM.put(namePS, ramV);
        }

        // Takes the 10 highest ram values from the namesAndRAM map and puts them into the namesList
        int countTen = 0;
        int max = 0;
        int min = 0;
        while(countTen < 5){
            int compare = 0;
            String nameS = " ";
            for (String k : namesAndRAM.keySet()) {
                if (namesAndRAM.get(k).compareTo(compare) > 0) {
                    compare = namesAndRAM.get(k);
                    nameS = k;
                }
            }
            namesList.add(nameS);

            namesAndRAM.remove(nameS,compare);

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

        final NumberAxis xAxis = new NumberAxis(0,maxSec,1);
        final NumberAxis yAxis = new NumberAxis("RAM Memory used",0,max+2000,1000);

        this.chartRAM = new AreaChart<>(xAxis,yAxis);
        this.chartRAM.setTitle("RAM usage");


        // creates a map with a process name as key and their chart as value
        for(String name : namesList){
            XYChart.Series chart = new XYChart.Series();
            chartsMap.put(name,chart);
        }

        // takes ram data from those 10 processes and makes a graph
        for(String name : namesList){
            XYChart.Series chartSeries = chartsMap.get(name);
            chartSeries.setName(name);
            for (ArrayList<Object> ao : mapIter) {
                if ((ao.get(1)).equals(name)) {
                    String testString2 = ao.get(0).toString();
                    int test2 = Integer.parseInt(testString2);
                    String testString3 = ao.get(2).toString();
                    int test3 = Integer.parseInt(testString3);
                    chartSeries.getData().add(new XYChart.Data(test2, test3));
                }
            }

            this.chartRAM.getData().addAll(chartSeries);
        }

        Scene scene  = new Scene(this.chartRAM,800,600);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void backToMenu(MouseEvent mouseEvent) throws IOException {
        Pane secondPane = FXMLLoader.load(getClass().getResource("menu.fxml"));
        thirdWindow.getChildren().setAll(secondPane);
    }
    @FXML
    private AreaChart chartMem;
    @FXML
    public void allProcessesPage(MouseEvent mouseEvent) throws IOException {
        Pane allProcessesPan = FXMLLoader.load(getClass().getResource("allprocesses.fxml"));
        Scene scene  = new Scene(allProcessesPan);
        Stage stage = new Stage();
        stage.setTitle("All Processes Monotoring");
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    Button backToLogPickButton;
    @FXML
    TableView allProcessesTable;
    @FXML
    TableColumn nomeProcessoColuna;
    @FXML
    TableColumn cpuColuna;
    @FXML
    TableColumn ramColuna;
    @FXML
    private TableView<Entrada> tableTry = new TableView<Entrada>();

    /**
     * Creates the list that will go into the table of processes
     */
    @FXML
    private final ObservableList<Entrada> data =
            FXCollections.observableArrayList(
            );

    /**
     * Populates the table of processes
     */
    public void populateProcTable(){
        for(ArrayList<Object> ao : mapIter) {
            String nomeE = String.valueOf(ao.get(1));
            String cpuE = String.valueOf(ao.get(3));
            String ramE = String.valueOf(ao.get(2));
            Entrada e = new Entrada(nomeE, cpuE, ramE);
            this.data.add(e);
        }
    }

    /**
     * Populates the table of processes when filtered by name
     */
    public void populateProcTableFiltered(){
        for(ArrayList<Object> ao : mapIter) {
            String nomeE = String.valueOf(ao.get(1));
            String cpuE = String.valueOf(ao.get(3));
            String ramE = String.valueOf(ao.get(2));
            Entrada e = new Entrada(nomeE, cpuE, ramE);
            this.data.add(e);
        }
    }

    @FXML
    private TableView<Entrada> tableCopy = new TableView<Entrada>();

    /**
     * Class with the entities of the table of processes
     */
    public static class Entrada {

        private final SimpleStringProperty nameP;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty email;

        private Entrada(String name, String lName, String email) {
            this.nameP = new SimpleStringProperty(name);
            this.lastName = new SimpleStringProperty(lName);
            this.email = new SimpleStringProperty(email);
        }

        public String getFirstName() {
            return nameP.get();
        }

        public void setFirstName(String name) {
            nameP.set(name);
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String name) {
            lastName.set(name);
        }

        public String getEmail() {
            return email.get();
        }

        public void setEmail(String name) {
            email.set(name);
        }
    }

    /**
     * Shows the table with all the processes
     */
    @FXML
    public void showTable(MouseEvent mouseEvent) throws IOException {
        tableTry.setEditable(true);

        TableColumn firstNameCol = new TableColumn("Processo");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Entrada, String>("firstName"));

        TableColumn lastNameCol = new TableColumn("CPU");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<Entrada, String>("lastName"));

        TableColumn emailCol = new TableColumn("RAM");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<Entrada, String>("email"));

        populateProcTable();
        tableTry.setItems(data);
        tableTry.getColumns().addAll(firstNameCol, lastNameCol, emailCol);

        Pane pane = FXMLLoader.load(getClass().getResource("tabletry.fxml"));
        pane.getChildren().add(this.tableTry);
        Scene scene  = new Scene(pane);
        Stage stage = new Stage();
        stage.setTitle("Monotoring all processes");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    TextField filterByName;
    @FXML
    Button filterByNameButton;

    /**
     * Shows the table filtered by name
     */
    public void actualizeTable(MouseEvent mouseEvent) throws IOException {
        tableTry.setEditable(true);

        TableColumn firstNameCol = new TableColumn("Processo");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Entrada, String>("firstName"));

        TableColumn lastNameCol = new TableColumn("CPU");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<Entrada, String>("lastName"));

        TableColumn emailCol = new TableColumn("RAM");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<Entrada, String>("email"));

        populateProcTableFiltered();

        tableTry.setItems(data);
        tableTry.getColumns().addAll(firstNameCol, lastNameCol, emailCol);

    }

    /**
     * Shows the table filtered by name
     */
    public void filterTable(MouseEvent mouseEvent) throws IOException {
        tableTry.setEditable(true);

        TableColumn firstNameCol = new TableColumn("Processo");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Entrada, String>("firstName"));

        TableColumn lastNameCol = new TableColumn("CPU");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<Entrada, String>("lastName"));

        TableColumn emailCol = new TableColumn("RAM");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<Entrada, String>("email"));

        String pesquisa = filterByName.getText();

        if(!pesquisa.isEmpty()) {
            // put the entries in the data
            for (ArrayList<Object> ao : this.mapIter) {
                String s = String.valueOf(ao.get(1));
                if (s.contains(pesquisa)) {
                    String nome = String.valueOf(ao.get(1));
                    String cpu = String.valueOf(ao.get(3));
                    String ram = String.valueOf(ao.get(2));
                    Entrada e = new Entrada(nome, cpu, ram);
                    this.data.add(e);
                }
            }


            this.tableTry.setItems(data);
            this.tableTry.getColumns().addAll(firstNameCol, lastNameCol, emailCol);

            Stage stage = new Stage();
            stage.setTitle("Filter processes by '" + pesquisa + "'");
            Scene scene = new Scene(this.tableTry);
            stage.setScene(scene);
            stage.show();
        }

    }

}