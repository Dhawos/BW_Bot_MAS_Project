package manager;

import bwapi.*;
import util.Utils;
/**
 * Created by dhawo on 07/04/2017.
 */
public class StrategyManager extends Manager {

    private static StrategyManager instance;

    private boolean attackable = false;
    public static void init(Game game, Player player) {
        instance = new StrategyManager(game, player);
    }
    private StrategyManager(Game game, Player self) {
        super(game, self);
    }
    public static StrategyManager getInstance() {
        return instance;
    }


    public boolean isAttackable(){
        return attackable;
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

    }

    public void initiateBO(){
    }
}
