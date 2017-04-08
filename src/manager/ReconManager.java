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

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Thoma on 07/04/2017.
 */
public class ReconManager extends Manager {
    private static ReconManager instance;
    private boolean currentlyScouting = false;
    private List<BaseLocation> bases;
    private Position enemyPositon;
    private int lastFrameScouting = Integer.MAX_VALUE;
    private UnitAgent scout;
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
    }

    @Override
    public void onFrame() {
        //go scouting every 2 minutes
        if (lastFrameScouting - game.getFrameCount() > 3600) {
            scout = pickScoutingUnit();
            scout.setJob(UnitJob.SCOUT);
            scout.act();
            lastFrameScouting = game.getFrameCount();
        } else if (scout != null && scout.isJobDone()){
            scout = null;

            IncomeManager.getInstance().sendIdleWorkersToWork();
        }
    }

    private UnitAgent pickScoutingUnit() {
        ask(IncomeManager.getInstance(), "free scv");
        Unit scout = IncomeManager.getInstance().getFreeSCVs().stream().findAny().get();
        return new GroundAgent(scout);
    }

    public List<Position> getScoutingPosition(){
        if(enemyPositon == null){
            //retrieve starting locations' position
            return bases.stream().filter(b -> b.isStartLocation()
                        && !b.getPosition().equals(ProductionManager.getInstance().getCommandCenter().getPosition()))
                        .map(BaseLocation::getPosition).collect(Collectors.toList());
        }
        return new ArrayList<Position>(){{add(enemyPositon);}};

    }

    public Position getEnemyPositon() {
        return enemyPositon;
    }
}
