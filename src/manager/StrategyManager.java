package manager;

import bwapi.*;
import util.Utils;
/**
 * Created by dhawo on 07/04/2017.
 */
public class StrategyManager extends Manager {

    private static StrategyManager instance;

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
        initiateBO();
    }

    @Override
    public void onFrame() {
        if (self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Marine).count() >= 20){
            ask(TacticsManager.getInstance(), "attacks");
        }
    }

    public void initiateBO(){
        ask(ProductionManager.getInstance(),"build 4 barracks");
        ask(ProductionManager.getInstance(),"build 20 marines");
    }
}
