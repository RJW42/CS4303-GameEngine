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
   private Path.Point current_point;
   private int refresh_frame = 2;

   private float speed;

   // Constructor
   public AIMovementController(GameObject parent, PVector position_offset, float speed) {
      super(parent);

      // Init attributes
      this.speed = speed;
   }


   // Methods
   public void start() {
      // Get desired components
      force_manager = parent.getComponent(ForceManager.class);
      director = sys.getGameObject(Director.class);
      path_manager = director.getComponent(AIPathManager.class);
   }


   public void update() {
      // Todo: need to change this as there is an issue when the AI is not fully on a node
//      if(force_manager.grounded && sys.frameCount % 15 == refresh_frame) {
//         System.out.println("A");
//         current_path = path_manager.astar_search(parent.pos, path_manager.player_ground_tile);
//         System.out.println("B");
//         current_path.getNextPoint();
//         current_point = current_path.getCurrentPoint();
//      }
//
//      walk_path();
   }


   public void draw(){
      if(current_path == null)
         return;

      for(int i = current_path.current_index; i < current_path.points.size(); i++) {
         var point = current_path.points.get(i);
         if(point.is_jump)
            if(point.is_bottom)  sys.fill(255, 0, 0);
            else sys.fill(0, 255, 0);
         else sys.fill(0);
         sys.circle(point.pos.x, point.pos.y, 0.1f);
      }
   }


   private void walk_path(){
      // Check if finished
      if(current_point == null)
         return;

      // Check if point reached
      if(Math.abs(parent.pos.x - current_point.pos.x) <= 0.1){ // Todo: use centre of parent
         // Reached point
         current_point = current_path.getNextPoint();
         if(current_point == null)
            return;
      }

      // Walk to current point
      if(current_point.pos.x > parent.pos.x)
         force_manager.applyForce(new PVector(speed, 0));
      else
         force_manager.applyForce(new PVector(-speed, 0));
   }
}
