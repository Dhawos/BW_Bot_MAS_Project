package manager;

import bwapi.*;
import util.*;

import java.util.ArrayList;

/**
 * Created by dhawo on 07/04/2017.
 */
public class IncomeManager extends Manager {
    private static IncomeManager instance;
    private Unit commandCenter = null;
    private ArrayList<Unit> idleSCVs = new ArrayList<>();

    public static void init(Game game, Player player) {
        instance = new IncomeManager(game, player);
    }

    private IncomeManager(Game game, Player self) {
        super(game, self);
    }

    public static IncomeManager getInstance() {
        return instance;
    }

    @Override
    public void processMessage(String message) {
        switch (message) {
            case "free scv":
                freeSCV();
                break;
        }
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
    }

    public void freeSCV() {
        self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_SCV).forEach(u -> {
                    idleSCVs.add(u);
                    u.stop();
                }
        );
    }

    public void sendIdleWorkersToWork() {
        for (Unit myUnit : self.getUnits()) {
            //if it's a worker and it's idle, send it to the closest mineral patch
            if (idleSCVs.contains(myUnit) && myUnit.getType().isWorker() && myUnit.isIdle()) {
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

    public ArrayList<Unit> getIdleSCVs() {
        return idleSCVs;
    }

    @Override
    public void run() {
        getCommandCenter();
        while (true) {
            if (self.supplyTotal() - self.supplyUsed() > 2) {
                buildSCV();
            } else if (self.minerals() >= 100) {
                buildSupply();
            }
            sendIdleWorkersToWork();
        }
    }
}
