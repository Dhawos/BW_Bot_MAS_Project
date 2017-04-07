package manager;

import bwapi.*;
import util.*;

/**
 * Created by dhawo on 07/04/2017.
 */
public class IncomeManager extends Manager {
    public IncomeManager(Game game, Player self) {
        super(game, self);
    }

    @Override
    public void processMessage(String message) {

    }

    @Override
    public void run() {
        Unit commandCenter = null;
        for (Unit myUnit : self.getUnits()) {
            if(myUnit.getType() == UnitType.Terran_Command_Center){
                commandCenter = myUnit;
                break;
            }
        }
        while (true){
            System.out.println("Supply used : " + self.supplyUsed());
            System.out.println("Supply total : " + self.supplyTotal());
            if(self.supplyTotal() - self.supplyUsed() > 2){
                if(self.minerals() >= 50 && !commandCenter.isTraining()){
                    commandCenter.train(UnitType.Terran_SCV);
                }
            }else if(self.minerals()>=100){
                //Build supply
                //iterate over units to find a worker
                for (Unit myUnit : self.getUnits()) {
                    if (myUnit.getType() == UnitType.Terran_SCV) {
                        //get a nice place to build a supply depot
                        TilePosition buildTile = util.Utils.getBuildTile(game,myUnit, UnitType.Terran_Supply_Depot, self.getStartLocation());
                        //and, if found, send the worker to build it (and leave others alone - break;)
                        if (buildTile != null) {
                            myUnit.build(UnitType.Terran_Supply_Depot, buildTile);
                            break;
                        }
                    }
                }
            }
            for (Unit myUnit : self.getUnits()) {
                //if it's a worker and it's idle, send it to the closest mineral patch
                if (myUnit.getType().isWorker() && myUnit.isIdle()) {
                    Unit closestMineral = null;

                    //find the closest mineral
                    for (Unit neutralUnit : game.neutral().getUnits()) {
                        if (neutralUnit.getType().isMineralField()) {
                            if (closestMineral == null || myUnit.getDistance(neutralUnit) < myUnit.getDistance(closestMineral)) {
                                closestMineral = neutralUnit;
                            }
                        }
                    }

                    //if a mineral patch was found, send the worker to gather it
                    if (closestMineral != null) {
                        myUnit.gather(closestMineral, false);
                    }
                }
            }
        }
    }
}
