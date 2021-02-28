package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

    @FXML
    Pane tablePane;
    @FXML
    public void tabelaTry() {
        Stage stage = new Stage();
        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setWidth(500);
        stage.setHeight(600);

        Label label = new Label("ola");

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

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, tableTry);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private TableView<Entrada> tableCopy = new TableView<Entrada>();

    @FXML
    public void tryingTable() throws IOException {
        Pane tableTryPane = FXMLLoader.load(getClass().getResource("tabletry.fxml"));
        Scene scene  = new Scene(tableTryPane);
        Stage stage = new Stage();
        stage.setTitle("Tentativa de table");
        tabelaTry();
        this.tableCopy = this.tableTry;
        stage.setScene(scene);
        stage.show();
    }

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

    @FXML
    public void showTable(MouseEvent mouseEvent) throws IOException {
        tabelaTry();

        Pane pane = FXMLLoader.load(getClass().getResource("tabletry.fxml"));
        pane.getChildren().add(this.tableTry);
        Scene scene  = new Scene(pane);
        Stage stage = new Stage();
        stage.setTitle("Monotoring all processes");
        stage.setScene(scene);
        stage.show();
    }
}