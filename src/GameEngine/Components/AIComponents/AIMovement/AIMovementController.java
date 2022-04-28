package GameEngine.Components.AIComponents.AIMovement;

import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.GameObjects.Core.Director;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

public class AIMovementController extends Component {
   // Attributes
   public static final float OFFSET_DIST = 0.2f;
   private final PVector pos_offset;

   private ForceManager force_manager;
   private Director director;
   private AIPathManager path_manager;

   private Path current_path;
   private Path.Point current_point;
   private int refresh_frame = 2;

   public float speed;

   // Walk attributes
   public boolean in_jump;
   public boolean in_drop;
   public boolean can_update;

   // Drop attributes
   private boolean reached_drop_point;
   private boolean dropped;


   // Constructor
   public AIMovementController(GameObject parent, PVector position_offset, float speed) {
      super(parent);


      // Init attributes
      this.pos_offset = position_offset;
      this.speed = speed;
      this.in_jump = false;
      this.in_drop = false;
      this.can_update = false;

      this.reached_drop_point = false;
      this.dropped = false;
   }


   // Methods
   public void start() {
      // Get desired components
      force_manager = parent.getComponent(ForceManager.class);
      director = sys.getGameObject(Director.class);
      path_manager = director.getComponent(AIPathManager.class);
   }


   public void update() {
//      // Todo: need to change this as there is an issue when the AI is not fully on a node
//      if(force_manager.grounded && sys.frameCount % 15 == refresh_frame) {
//         current_path = path_manager.astar_search(parent.pos, path_manager.player_ground_tile);
//         current_path.getNextPoint();
//         current_point = current_path.getCurrentPoint();
//      }
      if(current_path == null){
         current_path = path_manager.astar_search(parent.pos, path_manager.player_ground_tile);
         current_path.getNextPoint();
      }

      walk_path();
   }


   public void draw(){
      sys.fill(0, 0, 255);
      sys.circle(parent.pos.x + pos_offset.x, parent.pos.y + pos_offset.y, 0.1f);

      if(current_path == null)
         return;

      for(int i = current_path.current_index; i < current_path.points.size(); i++) {
         var point = current_path.points.get(i);
         if(point.is_jump)
            if(point.is_bottom)  sys.fill(255, 0, 0);
            else sys.fill(0, 255, 0);
         else sys.fill(0);
         sys.circle(point.pos.x, point.pos.y, 0.1f);
         if(point.upper_pos != null) {
            sys.stroke(255);
            sys.line(
                    point.pos.x, point.pos.y,
                    point.upper_pos.x, point.upper_pos.y
            );
         }
      }
   }


   private void walk_path(){
      // Check if finished
      if(current_path == null)
         return;

      // Check if point reached
      if((current_point == null || reached_point(current_point.pos) && !in_drop)){ // Todo: use centre of parent
         // Reached point
         advance_current_point();

         if(current_path == null)
            return;
      }

      // Walk to point
      if(in_jump) perform_jump();
      else if(in_drop) perform_drop();
      else perform_walk(current_point.pos);
   }

   private void advance_current_point(){
      current_point = current_path.getNextPoint();

      // Check if end of path
      if(current_point == null) {
         current_path = null;
         return;
      }

      // Not end of path check point type
      in_jump = current_point.is_jump && current_point.is_bottom;
      in_drop = current_point.is_jump && !current_point.is_bottom;
   }

   private void perform_walk(PVector pos){
      if(pos.x > get_x()) force_manager.applyForce(new PVector(speed, 0));
      else force_manager.applyForce(new PVector(-speed, 0));
   }

   private void perform_jump(){

   }

   private void perform_drop(){
      // Check what section of drop to perform
      if(!reached_drop_point){
         // Walk to the start of the drop
         perform_walk(current_point.pos);
         reached_drop_point = reached_point(current_point.pos);
         return;
      }


      if(!dropped){
         // Walk of the edge
         perform_walk(current_point.upper_pos); // Todo: make this an arrive maybe
         dropped = !force_manager.grounded;
         return;
      }

      // Walked of the edge now wait till reached bottom
      if(Math.abs(get_x() - current_point.upper_pos.x) <= OFFSET_DIST) {
         force_manager.velocity.x = 0;
      }

      // Check if dropped
      if(force_manager.grounded){
         dropped = false;
         in_drop = false;
         reached_drop_point = false;
         advance_current_point();
      }
   }

   private float get_x(){
      return parent.pos.x + pos_offset.x;
   }

   private float get_y(){
      return parent.pos.y + pos_offset.y;
   }

   private boolean reached_point(PVector pos){
      return PVector.dist(new PVector(get_x(), get_y()), pos) <= OFFSET_DIST;
   }
}
