import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import manager.*;

public class TestBot1 extends DefaultBWListener {

    private Mirror mirror = new Mirror();

    private Game game;

    private Player self;

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();

        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        
        int i = 0;
        for(BaseLocation baseLocation : BWTA.getBaseLocations()){
        	System.out.println("Base location #" + (++i) + ". Printing location's region polygon:");
        	for(Position position : baseLocation.getRegion().getPolygon().getPoints()){
        		System.out.print(position + ", ");
        	}
        	System.out.println();
        }
        game.setLocalSpeed(30);
        //Setting up and starting managers
        StrategyManager.init(game,self);
        TacticsManager.init(game,self);
        IncomeManager.init(game,self);
        ProductionManager.init(game,self);
        ReconManager.init(game, self);
        Thread SMThread = new Thread(StrategyManager.getInstance());
        //SMThread.start();
        Thread IMThread = new Thread(IncomeManager.getInstance());
        //IMThread.start();
        Thread RMThread = new Thread(ReconManager.getInstance());
        //RMThread.start();
        Thread PMThread = new Thread(ProductionManager.getInstance());
        //PMThread.start();
        Thread TMThread = new Thread(TacticsManager.getInstance());
        //TMThread.start();
    }

    @Override
    public void onFrame() {
        //game.setTextSize(10);
        /*
        game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());
        StringBuilder units = new StringBuilder("My units:\n");
        for (Unit myUnit : self.getUnits()) {
            units.append(myUnit.getType()).append(" ").append(myUnit.getTilePosition()).append("\n");
        }*/
        IncomeManager.getInstance().onFrame();
        ProductionManager.getInstance().onFrame();
        ReconManager.getInstance().onFrame();
        StrategyManager.getInstance().onFrame();
        TacticsManager.getInstance().onFrame();
        /*
        //draw my units on screen
        game.drawTextScreen(10, 25, units.toString());*/
    }

    @Override
    public void onUnitComplete(Unit unit) {
        super.onUnitComplete(unit);
        ProductionManager.getInstance().onUnitComplete(unit);
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        super.onUnitDiscover(unit);

    }

    public static void main(String[] args) {
        new TestBot1().run();
    }
}