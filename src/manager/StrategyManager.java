package manager;

import bwapi.*;
import util.Utils;
/**
 * Created by dhawo on 07/04/2017.
 */
public class StrategyManager extends Manager {
    public StrategyManager(Game game, Player self) {
        super(game, self);
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
