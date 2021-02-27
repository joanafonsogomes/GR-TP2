package sample;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private static ArrayList<ArrayList<Object>> monot = new ArrayList<ArrayList<Object>>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("TP2 GR");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static  ArrayList<ArrayList<Object>> getMonot() { return monot; }

    public static void setMonot(ArrayList<ArrayList<Object>> monoti) {
        monot  = monoti;
    }

    public static void processData(File logFile) throws FileNotFoundException {
        String read = null;
        try {

            BufferedReader br = new BufferedReader(new FileReader(logFile));

            //read first 2 lines (trash)
            read = br.readLine();
            read = br.readLine();

            int i = 0;
            while((read = br.readLine()) != null){
                // if linha sem comma
                if(!read.equals(",")){
                    Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(read);
                    while (m.find()) {
                        ArrayList<Object> nameMemCPU = new ArrayList<Object>();
                        String fullLine = m.group(1);
                        String[] params = fullLine.split("," );

                        nameMemCPU.add(0,params[0]);
                        nameMemCPU.add(1,params[1]);
                        nameMemCPU.add(2,params[2]);
                        nameMemCPU.add(3,params[3]);

                    monot.add(i,nameMemCPU);
                    i++;
                    }
                }
            }

            br.close();

            } catch (IOException ioException) {
            ioException.printStackTrace();
            }
    // DEBUG
    for(ArrayList<Object> ao : monot){
        for(Object o : ao){
            System.out.print(o + " ");
        }
        System.out.println("\n -------");
    }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
