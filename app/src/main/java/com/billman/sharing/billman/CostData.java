package com.billman.sharing.billman;

/**
 * Created by snote on 2017-08-26.
 */

public class CostData {
    String title;
    int cost;
    String costPerson;
    String[] participants;
    public CostData(String t,String c,String cp,String p){
        title=t; cost = Integer.valueOf(c); costPerson=cp; participants=p.split("::");
    }
    public String getTitle(){
        return title;
    }
    public int getCost(){
        return cost;
    }
    public String getCostPerson(){
        return costPerson;
    }
    public String[] getParticipants(){
        return participants;
    }
}

