package GameEngine.Components.AIComponents.AIMovement;

import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.GameEngine;
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

   // Jump attributes
   private boolean reached_jump_point;
   private boolean jumped;
   private float jump_vel;
   private float jump_time;



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

      this.reached_jump_point = false;
      this.jumped = false;
   }


   // Methods
   public void start() {
      // Get desired components
      force_manager = parent.getComponent(ForceManager.class);
      director = sys.getGameObject(Director.class);
      path_manager = director.getComponent(AIPathManager.class);
   }


   public void update() {
      // Todo: if lagging add some check to only update every 5 frames
      if(current_path == null || (can_update)){
         refresh_path();
      }

      walk_path();
   }

   private void refresh_path(){
      // Get refreshed path
      Path path = path_manager.astar_search(new PVector(get_x(), get_y()), path_manager.player_ground_tile);
      Path.Point next_current_point = path.getCurrentPoint();
      Path.Point point_after_next = path.peekNextPoint();
      float x = get_x();

      if(point_after_next != null && !next_current_point.is_jump &&
         ((x >= next_current_point.pos.x && x < point_after_next.pos.x && force_manager.velocity.x > 0) ||
          (x <= next_current_point.pos.x && x > point_after_next.pos.x && force_manager.velocity.x < 0))
          ){
         path.getNextPoint();
      }

      // Update path
      current_path = path;
      current_point = null;

      // Reset attributes
      in_jump = false;
      in_drop = false;
      can_update = true;

      reached_drop_point = false;
      dropped = false;

      reached_jump_point = false;
      jumped = false;
   }


   public void draw(){
      sys.fill(0, 0, 255);
      sys.circle(parent.pos.x + pos_offset.x, parent.pos.y + pos_offset.y, 0.1f);

      if(current_path == null)
         return;

      for(int i = 0; i < current_path.points.size(); i++) {
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
      if(current_point == null || (reached_point(current_point.pos) && !in_drop && !in_jump)){ // Todo: use centre of parent
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
      if(current_point == null) current_point = current_path.getCurrentPoint();
      else current_point = current_path.getNextPoint();

      // Check if end of path
      if(current_point == null) {
         System.out.println("Reached end");
         current_path = null;
         return;
      }

      // Not end of path check point type
      in_jump = current_point.is_jump && current_point.is_bottom;
      in_drop = current_point.is_jump && !current_point.is_bottom;
      can_update = !(in_jump || in_drop);
   }

   private void perform_walk(PVector pos){
      if(pos.x > get_x()) force_manager.applyForce(new PVector(speed, 0));
      else force_manager.applyForce(new PVector(-speed, 0));
   }

   private void perform_jump(){
      // Check what section of jump to perform
      if(!reached_jump_point){
         // Walk to start of the jump
         perform_walk(current_point.pos);
         reached_jump_point = reached_point(current_point.pos);

         if(reached_jump_point)
            force_manager.velocity.x = 0;

         return;
      }

      // Get next point
      PVector next_pos = current_path.peekNextPoint().pos;

      // Jump to top
      if(!jumped){
         jumped = true;

         float dist = (0.1f + current_point.upper_pos.y - get_y());
         float y_vel = (float)Math.sqrt(2.0 * sys.GRAVITY * dist);
         float time = (float) Math.sqrt((2 * dist) / sys.GRAVITY + (y_vel * y_vel) / (sys.GRAVITY * sys.GRAVITY));
         jump_time = sys.TOTAL_TIME + time * 2f;
         jump_vel = (0.75f / time) * ((next_pos.x < current_point.pos.x) ? -1 : 1);

         force_manager.velocity.y = y_vel;
         return;
      }

      // Reached top move to next point
      force_manager.velocity.x = jump_vel;
      perform_walk(next_pos);

      if(reached_point(next_pos)){
         reached_jump_point = false;
         jumped = false;
         can_update = true;
         advance_current_point();
         return;
      }

      if(sys.TOTAL_TIME > jump_time){
         refresh_path();
      }
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

         if(dropped)
            force_manager.velocity.x /= 2f;
         return;
      }

      // Walked of the edge now wait till reached bottom
      if(Math.abs(get_x() - current_point.upper_pos.x) <= OFFSET_DIST) {
         force_manager.velocity.x /= 2f;
      }

      // Check if dropped
      if(force_manager.grounded){
         dropped = false;
         in_drop = false;
         reached_drop_point = false;
         can_update = true;
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
