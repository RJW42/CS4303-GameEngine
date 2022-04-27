package GameEngine.Components.AIComponents;


import GameEngine.Components.Component;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameObjects.Core.Door;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.GameObject;

import java.util.ArrayList;


public class KillDoorManager extends Component {
   // Attributes
   private TerrainGenerator generator;
   private ArrayList<TerrainGenerator.Room> rooms;

   // Constructor
   public KillDoorManager(GameObject parent) {
      super(parent);
   }


   // Methods 
   public void start() {
      generator = sys.terrain.getComponent(TerrainGenerator.class);

      if(generator.rooms == null) generator.init_rooms();

      rooms = new ArrayList<>(generator.rooms);
   }

   public void update() {
      // Check if still rooms to check
      if(rooms == null) return;
      if(rooms.size() == 0) {
         rooms = null;
         return;
      }

      for(int i = rooms.size() - 1; i >= 0; i--){
         var room = rooms.get(i);
         boolean all_dead = true;

         for(var monster : room.monsters){
            if(!monster.is_dead){
               all_dead = false;
               break;
            }
         }

         if(!all_dead)
            continue;

         // Open all doors and stop tracking room
         room.doors.forEach(d -> d.manager.open(0));
         rooms.remove(i);
      }
   }

   public void draw() {}
}
