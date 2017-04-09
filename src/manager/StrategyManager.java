package manager;

import bwapi.*;
import util.Utils;
/**
 * Created by dhawo on 07/04/2017.
 */
public class StrategyManager extends Manager {

    private static StrategyManager instance;
    private boolean isStrategySet = false;
    public static void init(Game game, Player player) {
        instance = new StrategyManager(game, player);
    }
    private StrategyManager(Game game, Player self) {
        super(game, self);
    }
    public static StrategyManager getInstance() {
        return instance;
    }


    @Override
    public void processMessage(String message) {

    }

    @Override
    public void run() {
    }

    @Override
    public void onFrame() {
        if(!isStrategySet){
            initiateBO();
            isStrategySet = true;
        }
        if (self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Marine).count() >= 20){
            ask(TacticsManager.getInstance(), "attacks");
        }
    }

    public void initiateBO(){
        ask(ProductionManager.getInstance(),"build 2 barracks");
        ask(ProductionManager.getInstance(),"build 20 marines");
    }
}
