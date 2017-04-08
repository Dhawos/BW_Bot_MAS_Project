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

    public void initiateBO(){
    }
}
