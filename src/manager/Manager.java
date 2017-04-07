package manager;

import bwapi.Game;
import bwapi.Player;

/**
 * Created by dhawo on 07/04/2017.
 */
public abstract class Manager implements Runnable {
    protected Game game;
    protected Player self;

    public Manager(Game game, Player self) {
        this.game = game;
        this.self = self;
    }

    public abstract void processMessage(String message);
    public void ask(Manager other,String message){
        other.processMessage(message);
    };
}
