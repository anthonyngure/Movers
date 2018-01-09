package ke.co.thinksynergy.movers.model;

/**
 * Created by Anthony Ngure on 08/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class TransportOption {

    private String name;
    private double maxWight;
    private double maxVolume;
    private double cost;
    private boolean selected;
    private String time;


    public TransportOption() {
    }

    public String getName() {
        return name;
    }

    public double getMaxWight() {
        return maxWight;
    }

    public double getMaxVolume() {
        return maxVolume;
    }

    public double getCost() {
        return cost;
    }

    public String getTime() {
        return time;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
