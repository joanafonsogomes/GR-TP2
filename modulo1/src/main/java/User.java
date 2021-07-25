import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class User {

    private Map<Integer, List<String>> hostsMap;
    private int secondsInterval;

    /**
     * Creates a map with the hosts in the config file
     */
    private void inputUsers() throws IOException, ParseException {
        int count = 0;

        JSONParser parser = new JSONParser();
        File confFile = new File("config.json");
        String confFilePath = confFile.getAbsolutePath();

        Object obj = parser.parse(new FileReader(confFilePath));

        JSONObject jsonObject = (JSONObject) obj;
        JSONArray hosts = (JSONArray) jsonObject.get("Hosts");

        hostsMap = new HashMap<Integer, List<String>>();
        Iterator<JSONObject> iterator = hosts.iterator();

        while(iterator.hasNext()) {
            List<String> addport = new ArrayList<>();
            JSONObject host = iterator.next();
            String address = (String) host.get("Address");
            addport.add(address);
            String port = (String) host.get("Port");
            addport.add(port);
            hostsMap.put(count, addport);
            count++;
        }
        String interval = (String) jsonObject.get("Interval");
        this.secondsInterval = Integer.parseInt(interval);
    }

    /**
     * Monotoring of the hosts
     */
    private void monotoring() {
        int sizeMap = this.hostsMap.size();
        System.out.print(" ");
        System.out.println("Monotoring...");
        for(int i=0 ; i<sizeMap; i++) {

            List<String> addAndPort = this.hostsMap.get(i);

            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss");
            String formattedDate = myDateObj.format(myFormatObj);

            new Thread(() -> {

                int interval = 0, monotor=0;

                String address = addAndPort.get(0);
                String port = addAndPort.get(1);

                Client client = null;
                try {
                    client = new Client(address, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String output = new String();

                if(this.secondsInterval<50){
                    monotor=1;
                }else{
                    if(this.secondsInterval<200){
                        monotor=5;
                    }else{
                        monotor=10;
                    }
                }
                while (interval < this.secondsInterval) {
                    client.setSeconds(interval);
                    output = output + client.snmpWalk();
                    interval += monotor;
                    try {
                        Thread.sleep(monotor * 1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }

                Logger logFile = Logger.getLogger("MyLog" + address);
                FileHandler fh;

                try {
                    String linkLog = "../logger"+ address + "_" + formattedDate + ".log";
                    fh = new FileHandler(linkLog);
                    logFile.addHandler(fh);
                    SimpleFormatter formatter = new SimpleFormatter();
                    fh.setFormatter(formatter);
                    logFile.info(output);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();
        }
    }


    public void run() throws IOException, ParseException {
        inputUsers();
        monotoring();
    }

    public static void main(String[] args) throws IOException, ParseException {
        try {
            User user = new User();
            user.run();
        }
        catch(IOException|ParseException e){
            e.printStackTrace();
        }

    }

}