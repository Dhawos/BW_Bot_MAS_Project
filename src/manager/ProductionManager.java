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
        private int nbBarracksGoal = 0;
        private int nbFactoriesGoal = 0;
        private int nbStartportGoal = 0;
        private int nbBarracksBuilt = 0;
        private int nbFactoriesBuilt = 0;
        private int nbStartportBuilt = 0;
    }
    private ProductionBuildingsRatio buildingsRatio = new ProductionBuildingsRatio();
    private int lockedMinerals = 400;
    private int lockedGas = 0;
    private LinkedList<UnitType> queue;
    private LinkedList<UnitType> inProductionQueue;
    private Unit commandCenter = null;

    public static void init(Game game, Player player) {
        instance = new ProductionManager(game, player);
        instance.queue.add(UnitType.Terran_SCV);
        instance.retrieveCommandCenter();
    }

    public static ProductionManager getInstance() {
        return instance;
    }

    private ProductionManager(Game game, Player self) {
        super(game,self);
        queue = new LinkedList<>();
        inProductionQueue = new LinkedList<>();
    }

    @Override
    public void processMessage(String message) {
        String[] args = message.split(" ");
        if(args[0].equals("build")){
            int count = Integer.parseInt(args[1]);
            if(args[2].equals("barracks")){
                buildingsRatio.nbBarracksGoal = count;
            }else if(args[2].equals("marines")){
                for(int i = 0; i < count;i++){
                    inProductionQueue.add(UnitType.Terran_Marine);
                }
            }
        }
    }

    public Unit getCommandCenter() {
        return commandCenter;
    }

    public void retrieveCommandCenter() {
        commandCenter = self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Command_Center).findFirst().get();
    }

    public void buildSCV() {
        if (!commandCenter.isTraining()) {
            if(commandCenter.train(UnitType.Terran_SCV)){
                inProductionQueue.add(UnitType.Terran_SCV);
            }
        }
    }

    public void buildSupply() {
        Unit builder = null;
        try{
            builder = IncomeManager.getInstance().getFreeSCVs().stream().filter(u -> u.isIdle()).findAny().get();
        }catch(NoSuchElementException ex){
            ask(IncomeManager.getInstance(),"free scv");
            builder = IncomeManager.getInstance().getFreeSCVs().stream().filter(u -> u.isIdle()).findAny().get();

        }
        TilePosition buildTile = util.Utils.getBuildTile(game, builder, UnitType.Terran_Supply_Depot, self.getStartLocation());
        if (buildTile != null) {
            if(builder.build(UnitType.Terran_Supply_Depot, buildTile)){
                inProductionQueue.add(UnitType.Terran_Supply_Depot);
            }
        }
    }

    public void buildBarrack() {
        Unit builder = null;
        try{
            builder = IncomeManager.getInstance().getFreeSCVs().stream().filter(u -> u.isIdle()).findAny().get();
        }catch(NoSuchElementException ex){
            ask(IncomeManager.getInstance(),"free scv");
            builder = IncomeManager.getInstance().getFreeSCVs().stream().filter(u -> u.isIdle()).findAny().get();
        }
        TilePosition buildTile = util.Utils.getBuildTile(game, builder, UnitType.Terran_Barracks, self.getStartLocation());
        if (buildTile != null) {
            if(builder.build(UnitType.Terran_Barracks, buildTile)){
                inProductionQueue.add(UnitType.Terran_Barracks);
            }
        }
    }

    public void buildMarine() {
        Unit Barracks = self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Barracks).findAny().get();
        Barracks.train(UnitType.Terran_Marine);
        inProductionQueue.add(UnitType.Terran_Marine);
    }

    public void build(UnitType type){
        if(type == UnitType.Terran_SCV){
            buildSCV();
        }else if(type == UnitType.Terran_Supply_Depot){
            buildSupply();
        }else if(type == UnitType.Terran_Barracks){
            buildBarrack();
        }else if(type == UnitType.Terran_Marine){
            buildMarine();
        }
    }

    public void startBuildingsInQueue(){
        UnitType unitType = queue.getFirst();
        if(unitType.mineralPrice() <= self.minerals()-lockedMinerals && unitType.gasPrice() <= self.gas()-lockedGas && unitType.supplyRequired() <= (self.supplyTotal() - self.supplyUsed())){
            if(unitType.isBuilding()){
                lockedMinerals += unitType.mineralPrice();
                lockedGas += unitType.gasPrice();
            }
            build(unitType);
            queue.remove(unitType);
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
        long nbBarracksInProd = inProductionQueue.stream().filter(u -> u == UnitType.Terran_Barracks).count();
        long nbBarracksinQueue = queue.stream().filter(u -> u == UnitType.Terran_Barracks).count();
        if(!commandCenter.isTraining() && (!instance.queue.contains(UnitType.Terran_SCV) && !instance.inProductionQueue.contains(UnitType.Terran_SCV))){
            instance.queue.addFirst(UnitType.Terran_SCV);
        }
        else if(self.supplyTotal() - self.supplyUsed() <= 2 && (!instance.queue.contains(UnitType.Terran_Supply_Depot) && !instance.inProductionQueue.contains(UnitType.Terran_Supply_Depot))){
            instance.queue.addFirst(UnitType.Terran_Supply_Depot);
        }
        else if(buildingsRatio.nbBarracksBuilt + nbBarracksInProd + nbBarracksinQueue < buildingsRatio.nbBarracksGoal){
            instance.queue.add(UnitType.Terran_Barracks);
        }
        startBuildingsInQueue();
    }


    public void onUnitComplete(Unit unit) {
        UnitType type = unit.getType();
        if(type == UnitType.Terran_Barracks){
            buildingsRatio.nbBarracksBuilt++;
            buildingsRatio.nbBarracksGoal++;
        }
        inProductionQueue.remove(type);
    }

    public void onUnitDiscover(Unit unit) {
        UnitType unitType = unit.getType();
        if(unit.getPlayer() == self && unitType.isBuilding()){
            lockedMinerals -= unitType.mineralPrice();
            lockedGas -= unitType.gasPrice();
        }
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
