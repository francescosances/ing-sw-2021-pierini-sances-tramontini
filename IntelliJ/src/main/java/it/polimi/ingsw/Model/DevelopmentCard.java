package it.polimi.ingsw.Model;

import Utils.Pair;
import Utils.Serializable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class DevelopmentCard extends Card implements Producer, Serializable<DevelopmentCard> {

    public static final int MAX_LEVEL = 3;

    private Requirements cost;
    private int level;
    private DevelopmentColorType color;

    private Requirements productionCost;
    private Map<Resource,Integer> productionGain;

    private DevelopmentCard(int victoryPoints){
        super(victoryPoints);
    }

    public DevelopmentCard(int victoryPoints, Requirements requirements, int level, DevelopmentColorType color, Requirements productionCost, Pair<Resource,Integer> ... productionGain) {
        super(victoryPoints);
        this.cost = requirements;
        this.level = level;
        this.color = color;
        this.productionCost = productionCost;
        this.productionGain = new HashMap<>();
        for(Pair<Resource,Integer> x : productionGain){
            this.productionGain.put(x.fst,x.snd);
        }
    }

    public Requirements getCost() {
        return cost;
    }

    public int getLevel() {
        return level;
    }

    public DevelopmentColorType getColor() {
        return color;
    }

    public Requirements getProductionCost() {
        return productionCost;
    }

    public Map<Resource, Integer> getProductionGain() {
        return productionGain;
    }

    @Override
    public void produce() {

    }

    @Override
    public JsonObject toJSON() {
        JsonObject ret = new JsonObject();
        ret.addProperty("level",level);
        return ret;
    }

    @Override
    public DevelopmentCard fromJSON(String jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject,DevelopmentCard.class);
    }
}
