package agent;

import bwapi.Position;
import bwapi.Unit;
import manager.ReconManager;

import java.util.List;

/**
 * Created by Thoma on 07/04/2017.
 */
public class UnitAgent {
    private Unit unit;
    private Position lastPosition;
    private UnitJob job = UnitJob.IDLE;

    public UnitAgent(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

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
        List<Position> targetPositions = ReconManager.getInstance().getScoutingPosition();
        lastPosition = unit.getPosition();
        targetPositions.stream().forEach(p -> unit.move(p, true));
        //queue move back to base
        unit.move(lastPosition, true);
    }

    public boolean isJobDone(){
        switch (job){
            case SCOUT:
                if(unit.getPosition().equals(lastPosition) || !unit.exists()){
                    return true;
                }else {
                    return false;
                }
            case IDLE:
                return true;
            default:
                return true;
        }
    }
}
