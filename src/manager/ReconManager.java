package manager;

import agent.GroundAgent;
import agent.UnitAgent;
import agent.UnitJob;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thoma on 07/04/2017.
 */
public class ReconManager extends Manager {
    private static ReconManager instance;
    private boolean currentlyScouting = false;
    private List<BaseLocation> bases;

    public static ReconManager getInstance() {
        return instance;
    }

    private ReconManager(Game game, Player player) {
        super(game, player);
    }

    public static void init(Game game, Player player) {
        instance = new ReconManager(game, player);
        instance.bases = BWTA.getBaseLocations();
    }

    @Override
    public void processMessage(String message) {

    }

    @Override
    public void run() {
        while (true) {
            if (!currentlyScouting) {
                UnitAgent scout = pickScoutingUnit();
                scout.setJob(UnitJob.SCOUT);
                scout.act();
                //IncomeManager.getInstance().sendIdleWorkersToWork();
                currentlyScouting = true;
            }
        }

    }

    @Override
    public void onFrame() {

    }

    private UnitAgent pickScoutingUnit() {
        ask(IncomeManager.getInstance(), "free scv");
        Unit scout = IncomeManager.getInstance().getFreeSCVs().stream().findAny().get();
        //IncomeManager.getInstance().getIdleSCVs().remove(scout);
        return new GroundAgent(scout);
    }

    public Position getScoutingPosition(){
        return bases.stream().findAny().get().getPosition();
    }
}
