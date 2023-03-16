package bd.com.expresshub.posx.print;

public class DeviceModel {
    String name;
    String mac;
    public DeviceModel(String name,String mac){
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }
}
