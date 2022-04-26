package GameEngine.Components.AIComponents.AIMovement;

import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.GameObjects.Core.Director;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

public class AIMovementController extends Component {
   // Attributes
   private ForceManager force_manager;
   private Director director;
   private AIPathManager path_manager;

   private Path current_path;
   private int refresh_frame = 2;

   // Constructor
   public AIMovementController(GameObject parent, PVector position_offset, float speed) {
      super(parent);

      // Init attributes
   }


   // Methods
   public void start() {
      // Get desired components
      force_manager = parent.getComponent(ForceManager.class);
      director = sys.getGameObject(Director.class);
      path_manager = director.getComponent(AIPathManager.class);
   }


   public void update() {
      if(force_manager.grounded && sys.frameCount % 15 == refresh_frame) {
         current_path = path_manager.astar_search(parent.pos, path_manager.player_ground_tile);
         current_path.getNextPoint();
      }
   }

   public void draw(){
      if(current_path == null)
         return;

      for(int i = current_path.current_index; i < current_path.points.size(); i++) {
         var point = current_path.points.get(i);
         sys.fill(0);
         sys.circle(point.pos.x, point.pos.y, 0.1f);
      }
   }

}
