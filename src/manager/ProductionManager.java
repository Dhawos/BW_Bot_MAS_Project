package manager;

import bwapi.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by dhawo on 08/04/2017.
 */
public class ProductionManager extends Manager {
    private static ProductionManager instance;
    private class ProductionBuildingsRatio{
        private int nbBarracksGoal = 5;
        private int nbFactoriesGoal = 0;
        private int nbStartportGoal = 0;
        private int nbBarracksBuilt = 0;
        private int nbFactoriesBuilt = 0;
        private int nbStartportBuilt = 0;
    }
    private ProductionBuildingsRatio buildingsRatio = new ProductionBuildingsRatio();
    private LinkedList<UnitType> queue;
    private LinkedList<UnitType> inProductionQueue;
    private Unit commandCenter = null;

    public static void init(Game game, Player player) {
        instance = new ProductionManager(game, player);
        instance.queue.add(UnitType.Terran_SCV);
    }

    public static ProductionManager getInstance() {
        return instance;
    }

    private ProductionManager(Game game, Player self) {
        super(game,self);
        queue = new LinkedList<>();
    }

    @Override
    public void processMessage(String message) {

    }


    public void getCommandCenter() {
        for (Unit myUnit : self.getUnits()) {
            if (myUnit.getType() == UnitType.Terran_Command_Center) {
                commandCenter = myUnit;
                break;
            }
        }
    }

    public void buildSCV() {
        if (self.minerals() >= 50 && !commandCenter.isTraining()) {
            commandCenter.train(UnitType.Terran_SCV);
        }

    }

    public void buildSupply() {
        /*
        //Build supply
        //iterate over units to find a worker
        for (Unit myUnit : self.getUnits()) {
            if (myUnit.getType() == UnitType.Terran_SCV) {
                //get a nice place to build a supply depot
                TilePosition buildTile = util.Utils.getBuildTile(game, myUnit, UnitType.Terran_Supply_Depot, self.getStartLocation());
                //and, if found, send the worker to build it (and leave others alone - break;)
                if (buildTile != null) {
                    myUnit.build(UnitType.Terran_Supply_Depot, buildTile);
                    break;
                }
            }
        }
        */
        Unit builder = null;
        ask(IncomeManager.getInstance(),"free scv");
        try{
            builder = IncomeManager.getInstance().getFreeSCVs().stream().findAny().get();
        }catch(NoSuchElementException ex){
            System.out.println("No builder available");

        }
        TilePosition buildTile = util.Utils.getBuildTile(game, builder, UnitType.Terran_Supply_Depot, self.getStartLocation());
        if (buildTile != null) {
            builder.build(UnitType.Terran_Barracks, buildTile);
        }
    }

    public void buildBarrack() {
        //Build supply
        //iterate over units to find a worker
        Unit builder = null;
        ask(IncomeManager.getInstance(),"free scv");
        try{
            builder = IncomeManager.getInstance().getFreeSCVs().stream().findAny().get();
        }catch(NoSuchElementException ex){
            System.out.println("No builder available");

        }
        TilePosition buildTile = util.Utils.getBuildTile(game, builder, UnitType.Terran_Barracks, self.getStartLocation());
        if (buildTile != null) {
            builder.build(UnitType.Terran_Barracks, buildTile);
        }
    }

    public void build(UnitType type){
        if(type == UnitType.Terran_SCV){
            buildSCV();
        }else if(type == UnitType.Terran_Supply_Depot){
            buildSupply();
        }else if(type == UnitType.Terran_Barracks){
            buildBarrack();
        }
    }

    public void startBuildingsInQueue(){
        LinkedList<UnitType> copy_list = new LinkedList<>(queue);
        for(UnitType unitType : copy_list){
            if(unitType.mineralPrice() <= self.minerals() && unitType.gasPrice() <= self.gas() && unitType.supplyRequired() <= (self.supplyTotal() - self.supplyUsed())){
                build(unitType);
                queue.remove(unitType);
                inProductionQueue.add(unitType);
            }else{
                break;
            }
        }
    }

    public void updateProductionBuildingsRatio() {
        self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Barracks).forEach(u -> {
                    buildingsRatio.nbBarracksBuilt++;
                }
        );
        self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Factory).forEach(u -> {
                    buildingsRatio.nbFactoriesBuilt++;
                }
        );
        self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Starport).forEach(u -> {
                    buildingsRatio.nbStartportBuilt++;
                }
        );
    }

    @Override
    public void onFrame() {
        getCommandCenter();
        /*
        if (self.supplyTotal() - self.supplyUsed() > 2) {
            buildSCV();
            if(self.minerals() > 150 && buildingsRatio.nbBarracksBuilt < buildingsRatio.nbBarracksGoal){
                buildBarrack();
            }
        } else if (self.minerals() >= 100) {
            buildSupply();
        }
        */
        if(!commandCenter.isTraining()){
            instance.queue.add(UnitType.Terran_SCV);
        }
        if(self.supplyTotal() - self.supplyUsed() <= 2 && (!instance.queue.contains(UnitType.Terran_Supply_Depot) || !instance.inProductionQueue.contains(UnitType.Terran_Supply_Depot))){
            instance.queue.add(UnitType.Terran_Supply_Depot);
        }
        if(self.minerals() > 150 && buildingsRatio.nbBarracksBuilt < buildingsRatio.nbBarracksGoal){
            instance.queue.add(UnitType.Terran_Barracks);
        }
        startBuildingsInQueue();
    }


    public void onUnitComplete(Unit unit) {
        UnitType type = unit.getType();
        inProductionQueue.remove(type);
    }

    @Override
    public void run() {
        getCommandCenter();
        while (true) {
            if (self.supplyTotal() - self.supplyUsed() > 2) {
                buildSCV();
                if(self.minerals() > 150 && buildingsRatio.nbBarracksBuilt < buildingsRatio.nbBarracksGoal){
                    buildBarrack();
                }
            } else if (self.minerals() >= 100) {
                buildSupply();
            }
        }
    }
}
