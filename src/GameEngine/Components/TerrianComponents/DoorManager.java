package GameEngine.Components.TerrianComponents;


import GameEngine.Components.Component;
import GameEngine.GameObjects.Core.Door;
import GameEngine.GameObjects.GameObject;


public class DoorManager extends Component {
   // Attributes
   private final Door door;
   private float time_to_close;

   private boolean should_close;

   // Constructor
   public DoorManager(Door parent) {
      super(parent);

      // init attributes
      door = parent;
   }


   // Methods 
   public void start() {}
   public void draw() {}

   public void update() {
      // Check if should close
      if(!door.is_open || !should_close)
         return;

      time_to_close -= sys.DELTA_TIME;

      if(time_to_close < 0) {
         // Todo: to implement this. Need to check both monster and player not in door
         // close();
      }
   }


   public void open(float time_to_close){
      // Check if should close
      should_close = time_to_close > 0;
      this.time_to_close = time_to_close;

      door.is_open = true;
      door.lower.active = false;
      door.middle.active = false;
   }

   public void close() {
      door.is_open = false;
      door.lower.active = true;
      door.middle.active = true;
   }
}
