package manager;

import bwapi.*;
import util.Utils;
/**
 * Created by edouard on 08/04/2017.
 */
public class TacticsManager extends Manager {
    private static TacticsManager instance;

    public static void init(Game game, Player player) {
        instance = new TacticsManager(game, player);
    }

    private TacticsManager(Game game, Player self) {
        super(game, self);
    }

    public static TacticsManager getInstance() {
        return instance;
    }

    @Override
    public void processMessage(String message) {
        switch(message) {
            case "attacks":
                attacks();
                break;
        }

    }
    @Override
    public void onFrame() {

    }

    public void attacks() {
        for (Unit myUnit : self.getUnits()) {
            if (myUnit.getType() == UnitType.Terran_Marine) {
                myUnit.attack(ReconManager.getInstance().getEnemyPositon());
            }
        }
    }
}
