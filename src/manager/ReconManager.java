package manager;

import agent.GroundAgent;
import agent.UnitAgent;
import agent.UnitJob;
import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;


import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thoma on 07/04/2017.
 */
public class ReconManager extends Manager {
    private static ReconManager instance;
    private List<BaseLocation> bases;
    private ArrayList<Pair<Integer,Unit>> enemyBuildings;
    private int lastFrameScouting = -1;
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
        instance.enemyBuildings = new ArrayList<>();
    }


    private UnitAgent pickScoutingUnit() {
        ask(IncomeManager.getInstance(), "free scv");
        Unit scout = IncomeManager.getInstance().getFreeSCVs().get(IncomeManager.getInstance().getFreeSCVs().size() - 1);
        return new GroundAgent(scout);
    }

    public List<Position> getScoutingPosition() {
        if (enemyBuildings.size() == 0) {
            //retrieve starting locations' position
            return bases.stream().filter(b -> b.isStartLocation()
                    && !b.getPosition().equals(ProductionManager.getInstance().getCommandCenter().getPosition()))
                    .map(BaseLocation::getPosition).collect(Collectors.toList());
        }
        return new ArrayList<Position>() {{
            enemyBuildings.stream().forEach(b -> add(b.second.getPosition()));
        }};

    }

    public Position getEnemyPositon() {
        return enemyBuildings.stream().findAny().get().second.getPosition();
    }

    @Override
    public void processMessage(String message) {

    }

    @Override
    public void run() {
    }

    @Override
    public void onFrame() {
        //go scouting every 5 minutes
        if (game.getFrameCount() - lastFrameScouting > 6000 || lastFrameScouting == -1) {
            System.out.println("pick a scv to scout");
            scout = pickScoutingUnit();
            scout.setJob(UnitJob.SCOUT);
            scout.act();
            lastFrameScouting = game.getFrameCount();
        } else if (scout != null && scout.isJobDone() && game.getFrameCount() - lastFrameScouting > 30) {
            IncomeManager.getInstance().getFreeSCVs().remove(scout.getUnit());
            scout = null;
            System.out.println("scv finished scouting");
            IncomeManager.getInstance().sendIdleWorkersToWork();
        }
    }

    public void onUnitShow(Unit unit){
        if(!unit.getType().isNeutral() && unit.getType().isBuilding() && unit.getPlayer() != self){
            enemyBuildings.add(new Pair<>(game.getFrameCount(), unit));
        }
    }
}
