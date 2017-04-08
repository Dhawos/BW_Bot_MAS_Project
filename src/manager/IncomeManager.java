package manager;

import bwapi.*;
import util.*;

import java.util.ArrayList;

/**
 * Created by dhawo on 07/04/2017.
 */
public class IncomeManager extends Manager {
    private static IncomeManager instance;
    private ArrayList<Unit> freeSCVs = new ArrayList<>();

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

    public void freeSCV() {
        Unit freedSCV = self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_SCV && !freeSCVs.contains(u)).findAny().get();
        freedSCV.stop();
        freeSCVs.add(freedSCV);

        /*
        self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_SCV).forEach(u -> {
                    idleSCVs.add(u);
                    u.stop();
                }
        );
        */
    }

    public void sendIdleWorkersToWork() {
        for (Unit myUnit : self.getUnits()) {
            //if it's a worker and it's idle, send it to the closest mineral patch
            if (!freeSCVs.contains(myUnit) && myUnit.getType().isWorker() && myUnit.isIdle()) {
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

    /*public ArrayList<Unit> getIdleSCVs() {
        return idleSCVs;
    }*/

    public ArrayList<Unit> getFreeSCVs() {
        return freeSCVs;
    }

    @Override
    public void onFrame() {
        sendIdleWorkersToWork();
    }

    @Override
    public void run() {
        while(true){
            sendIdleWorkersToWork();
        }
    }
}
