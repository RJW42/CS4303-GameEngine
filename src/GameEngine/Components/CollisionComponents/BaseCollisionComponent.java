package GameEngine.Components.CollisionComponents;

import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.Triggers.CollisionTrigger;
import processing.core.PVector;

import java.util.ArrayList;

public abstract class BaseCollisionComponent extends Component {
   public int bound_box_width;
   public int bound_box_height;
   public PVector offset;
   public ArrayList<Integer> curr_grids;
   public CollisionTrigger trigger;

   protected BaseCollisionComponent(GameObject parent, CollisionTrigger trigger, int bound_box_width) {
      this(parent, trigger, bound_box_width, bound_box_width);
   }

   protected BaseCollisionComponent(GameObject parent, CollisionTrigger trigger, int bound_box_width, int bound_box_height){
      this(parent, trigger, new PVector(0, 0), bound_box_width, bound_box_height);
   }

   protected BaseCollisionComponent(GameObject parent, CollisionTrigger trigger, PVector offset, int bound_box_width, int bound_box_height) {
      super(parent);

      this.curr_grids = new ArrayList<>(4);
      this.bound_box_width = bound_box_width;
      this.bound_box_height = bound_box_height;
      this.offset = offset;
      this.trigger = trigger;
   }

   public void trigger(BaseCollisionComponent other){
      if(isTrigger())
         this.trigger(other);
   }

   public boolean isTrigger(){
      return trigger != null;
   }

   /* ***** Implemented Methods ***** */
   public float get_x(){
      return parent.pos.x + offset.x;
   }

   public float get_y(){
      return parent.pos.y + offset.y;
   }

   public boolean collidesWith(BaseCollisionComponent other){
      // Check both objects are collidable
//      if(!other.collidable || !collidable) // Todo change this to method can collide which takes in other
//         return false;

      // Both Objects Have Circular Collision Meshes
      if(other instanceof CircleCollisionComponent && this instanceof CircleCollisionComponent){
         return circleCircleCollision((CircleCollisionComponent)this, (CircleCollisionComponent)other);
      }

      // Both Objects Have Rectangular Collision Meshes
      if(other instanceof RectCollisionComponent && this instanceof RectCollisionComponent){
         return rectRectCollision((RectCollisionComponent)this, (RectCollisionComponent)other);
      }

      // One of the Objects is Circular and the Other is Rectangular.
      if(other instanceof CircleCollisionComponent){
         return circleRectCollision((CircleCollisionComponent)other, (RectCollisionComponent)this);
      }else{
         return circleRectCollision((CircleCollisionComponent)this, (RectCollisionComponent)other);
      }
   }

   protected boolean circleCircleCollision(CircleCollisionComponent c1, CircleCollisionComponent c2){
      // Get Distance between circles
      float dis = circleCircleDistance(c1.get_x(), c1.get_y(), c1.rad, c2.get_x(), c2.get_y(), c2.rad);

      // Check for overlap
      return dis < c1.rad + c2.rad;
   }

   protected boolean circleRectCollision(CircleCollisionComponent c, RectCollisionComponent r){
      // Init test variables
      float c_x = c.get_x() + c.rad;
      float c_y = c.get_y() - c.rad;
      float test_x = c_x;
      float test_y = c_y;

      if (c_x < r.get_x())                      test_x = r.get_x(); // Left Edge
      else if (c_x > r.get_x() + r.width)  test_x = r.get_x() + r.width; // Right edge
      if (c_y < r.get_y() - r.height)      test_y = r.get_y() - r.height; // Top Edge
      else if (c_y > r.get_y())                 test_y = r.get_y(); // Bottom Edge

      // Get Distance
      float dis = distance(c_x, c_y, test_x, test_y);

      return dis < c.rad;
   }

   protected boolean rectRectCollision(RectCollisionComponent r1, RectCollisionComponent r2){
      return (
         r1.get_x() + r1.width > r2.get_x() &&   // R1 Right Edge Past R2 Left
         r1.get_x() < r2.get_x() + r2.width &&   // R1 Left Edge past R2 Right
         r1.get_y() > r2.get_y() - r2.height &&  // R1 Top Edge Past R2 Bottom
         r1.get_y() - r1.height < r2.get_y()     // R1 Bottom Edge Past R2 Top
      );
   }


   protected float circleCircleDistance(float c1_x, float c1_y, float c1_rad, float c2_x, float c2_y, float c2_rad){
      float c1_mid_x = c1_x + c1_rad;
      float c1_mid_y = c1_y - c1_rad;
      float c2_mid_x = c2_x + c2_rad;
      float c2_mid_y = c2_y - c2_rad;

      // Get Distance between circles
      return distance(c1_mid_x, c1_mid_y, c2_mid_x, c2_mid_y);
   }

   protected float distance(float x1, float y1, float x2, float y2){
      return (float)Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
   }

   public void updateCollisionGrids() {
      // Init Some Constants
      int mesh_width = bound_box_width;
      int mesh_height = bound_box_height;
      int x_increase = Math.min(mesh_width, GameEngine.GRID_SIZE);
      int y_decrease = Math.min(mesh_height, GameEngine.GRID_SIZE);
      int grid_x = -1;
      int grid_y = -1;
      boolean stop_y = false;
      boolean stop_x = false;

      // Clear Current Grids
      for(var i : this.curr_grids)
         sys.removeGridObject(i, this);
      this.curr_grids.clear();

      // Calc Overlapping Grids
      for(int y = (int)this.get_y(); !stop_y && y > 0; y -= y_decrease, stop_x = false) {
         // Check if at final position
         if(y < this.get_y() - mesh_height) {
            y = (int)this.get_y() - mesh_height;
            stop_y = true;
         }

         // Check Y Position within the world
         if(y >= GameEngine.SCREEN_HEIGHT)
            continue;

         int new_grid_y = y / GameEngine.GRID_SIZE;

         if(new_grid_y == grid_y)
            continue;

         for(int x = (int)this.get_x(); !stop_x && x < GameEngine.SCREEN_WIDTH; x += x_increase) {
            // Check if at final position
            if(x > this.get_x() + mesh_width){
               x = (int)this.get_x() + mesh_width;
               stop_x = true;
            }

            // Check X Position within the world
            if (x < 0)
               continue;

            int new_grid_x = x / GameEngine.GRID_SIZE;

            if (new_grid_x == grid_x)
               continue;


            // Found New Grid Position
            grid_x = new_grid_x;
            grid_y = new_grid_y;

            int index = sys.getGridIndex(grid_x, grid_y);
            this.curr_grids.add(index);
            sys.setGridObject(index, this);
         }
         grid_x = -1;
      }
   }

   /**
    * Used when an object is killed to remove it from the collision system
    */
   public void clearCollsionGrids(){
      for(var i : this.curr_grids){
         sys.removeGridObject(i, this);
      }
   }
}
