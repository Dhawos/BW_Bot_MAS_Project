package agent;

import bwapi.Position;
import bwapi.Unit;
import manager.ReconManager;

import java.util.Queue;

/**
 * Created by Thoma on 07/04/2017.
 */
public abstract class UnitAgent {
    private Unit unit;
    private Queue<Position> path;
    private UnitJob job = UnitJob.IDLE;

    public UnitAgent(Unit unit) {
        this.unit = unit;
    }

    public abstract void findPath(Position target);


    public void setJob(UnitJob job) {
        this.job = job;
    }

    public void act(){
        switch (job){
            case SCOUT:
                scout();
        }
    }

    private void scout(){
        Position targetPosition = ReconManager.getInstance().getScoutingPosition();
        Position returnPostion = unit.getPosition();
        unit.move(targetPosition);

        //unit.move(returnPostion);
    }
}
