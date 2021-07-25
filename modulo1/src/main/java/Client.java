import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.snmp4j.CommunityTarget;

import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;

import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class Client {
    private String address;
    private int port;

    private String runName = ".1.3.6.1.2.1.25.4.2.1.2"; //hrSWRunName

    private String runPerfMem=".1.3.6.1.2.1.25.5.1.1.2."; //hrSWRunPerfMem
    private String runPerfCPU=".1.3.6.1.2.1.25.5.1.1.1."; //hrSWRunPerfCPU

    private String community = "gr2020";

    private int snmpVersion = SnmpConstants.version2c;

    private CommunityTarget target;
    private TransportMapping transport;

    int secs;

    // if called w/o args
    public Client() throws IOException {
        this.address="127.0.0.1";
        this.port=161;

        this.target = new CommunityTarget();
        configTarget();

        this.transport = new DefaultUdpTransportMapping();
        this.transport.listen();
    }

    public Client(String address,String port) throws IOException {
        this.address=address;
        this.port=Integer.parseInt(port);

        this.target = new CommunityTarget();
        configTarget();

        this.transport = new DefaultUdpTransportMapping();
        this.transport.listen();
    }

    public void configTarget(){
        this.target.setCommunity(new OctetString(community));
        this.target.setVersion(snmpVersion);
        this.target.setAddress(new UdpAddress(this.address + "/" + port));
        this.target.setRetries(2);
        this.target.setTimeout(1000);
    }

    public String snmpWalk(){

        String result = new String();
        String str= new String();

        try{
            Map<String,String> nameHash =doWalk(runName,target);

            Map<String,String> memHash =doWalk(runPerfMem,target);
            Map<String,String> cpuHash =doWalk(runPerfCPU,target);

            Map<String,String> hashres=new TreeMap<>();

            for(String v: nameHash.keySet()){
                hashres.put(v,nameHash.get(v));
            }
            for(String v: memHash.keySet()){
                if(hashres.containsKey(v)){
                    str=hashres.get(v);
                    str=str+","+memHash.get(v);
                }else{
                    str=memHash.get(v);
                }
                hashres.put(v,str);
            }
            for(String v: cpuHash.keySet()){
                if(hashres.containsKey(v)){
                    str=hashres.get(v);
                    str=str+","+cpuHash.get(v);
                }else{
                    str=cpuHash.get(v);
                }
                hashres.put(v,str);
            }
            result = "SECS | NAME | MEM | CPU \n";
            for(String v: hashres.keySet()){
                String d=hashres.get(v);
                String sub[]=d.split(",");
                result=result+",\n["+this.secs+","+v+"-"+sub[0]+","+Integer.parseInt(sub[1])+","+Integer.parseInt(sub[2])+"]\n";
            }
        }catch(IOException e){}
        return result;
    }

    public Map<String, String> doWalk(String tableOid, CommunityTarget target) throws IOException {
        Map<String, String> result = new TreeMap<>();
        TransportMapping transport = new DefaultUdpTransportMapping();

        Snmp snmp = new Snmp(transport);
        transport.listen();

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));

        if (events == null || events.size() == 0) {
            System.out.println("Error: Unable to read table...");
            return result;
        }

        for (TreeEvent event : events) {
            if (event == null) {
                continue;
            }
            if (event.isError()) {
                System.out.println("Error: table OID [" + tableOid + "] " + event.getErrorMessage());
                continue;
            }

            VariableBinding[] varBindings = event.getVariableBindings();
            if (varBindings == null || varBindings.length == 0) {
                continue;
            }
            for (VariableBinding varBinding : varBindings) {
                if (varBinding == null) {
                    continue;
                }
                String v=varBinding.getOid().toString();
                String[] sub=v.split("\\.");
                String s=sub[sub.length-1];
                result.put("." + s, varBinding.getVariable().toString());
            }

        }
        snmp.close();

        return result;
    }

    public void setSeconds(int s){
        this.secs=s;
    }

}