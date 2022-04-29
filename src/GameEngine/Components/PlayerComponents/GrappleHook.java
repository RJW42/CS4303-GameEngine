package GameEngine.Components.PlayerComponents;


import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PVector;

import java.util.Optional;


public class GrappleHook extends Component {
   // Attributes
   private static final int MAX_DISTANCE = 1000; // Todo: wanna check this at some point

   private ForceManager force_manager;
   private InputManager.Key fire;

   private TerrainGenerator generator;
   private int[] world;

   public boolean fired;
   private PVector base;
   private boolean can_release;
   private PVector player_point; // Todo: <- this is the point the grapple hooks to the player, used for non grapple check and other stuff

   // Constructor
   public GrappleHook(GameObject parent) {
      super(parent);

      // Init attributes
      this.fire = sys.input_manager.getKey("grapple");
      this.fired = false;
      this.can_release = false;
   }


   // Methods 
   public void start() {
      this.force_manager = parent.getComponent(ForceManager.class);
      this.generator = sys.terrain.getComponent(TerrainGenerator.class);
      this.world = generator.getWorld();
   }

   public void update() {
      if(!Player.ACTIVE) return;
      player_point = parent.pos;

      if(!fired)
         handle_firing();
      else
         handle_swing();
   }

   public void draw() {
      if(!fired)
         return;

      // Draw grapple
      sys.stroke(255);
      sys.line(player_point.x, player_point.y, base.x, base.y);
   }


   private void handle_firing(){
      // Wait for fire key
      if(!fire.pressed) {
         can_release = false;
         return;
      }

      if(can_release)
         return;

      // User fired check for collision
      Optional<PVector> maybe_point = cast_ray(
              player_point, PVector.sub(new PVector(sys.mouse_x, sys.mouse_y), player_point).normalize()
      );

      if(maybe_point.isEmpty())
         return;

      // Received point, check if this point can be grappled to
      base = maybe_point.get();
      PVector offset = PVector.sub(base, player_point).normalize().mult(0.1f);
      int index = generator.getIndexFromWorldPos(base.x + offset.x, base.y + offset.y);

      if(generator.getSpecialTiles()[index] == Terrain.NON_GRAPPLE)
         return; // Todo: maybe want an animation for this

      // Received valid point, create grapple
      fired = true;
      force_manager.grapple_base = base;
      force_manager.grapple_length = PVector.dist(player_point, base);
   }

   private void handle_swing(){
      // Check grapple still connected
      Optional<PVector> maybe_point = cast_ray(
              base, PVector.sub(player_point, base).normalize()
      );

      if(!fire.pressed)
         can_release = true;

      if((maybe_point.isPresent() && PVector.dist(maybe_point.get(), base) < PVector.dist(base, player_point)) || (can_release && fire.pressed)){
         // No longer fired
         force_manager.grapple_base = null;
         fired = false;
      }
   }

   private Optional<PVector> cast_ray(PVector ray_start, PVector ray_direction){
      // Cast a ray until it hits a wall. Get the position of the hit on the wall
      // Init values
      PVector ray_unit_step_size = new PVector(
              (float)Math.sqrt((float)1 + Math.pow(ray_direction.y / ray_direction.x, 2)),
              (float)Math.sqrt((float)1 + Math.pow(ray_direction.x / ray_direction.y, 2))
      );
      PVector current_cell = new PVector((int)(ray_start.x / Terrain.CELL_SIZE), (int)(ray_start.y / Terrain.CELL_SIZE));
      PVector ray_length_1d = new PVector(0, 0);
      PVector step = new PVector(0, 0);

      // Init Starting Conditions.
      //    This Will determine the direction we will walk along the ray
      //    and initialise the starting values for the length of the ray
      //    Todo: may need to mul current_cell by Terrian.Size if changes in future
      if(ray_direction.x < 0){ // Determine what step size to use on the x-axis
         step.x = -1; // Step Left
         ray_length_1d.x = (ray_start.x - current_cell.x) * ray_unit_step_size.x; // How much left in cell to left
      } else {
         step.x = 1; // Step Right
         ray_length_1d.x = ((current_cell.x + 1) - ray_start.x) * ray_unit_step_size.x; // How much left in cell to right
      }

      if(ray_direction.y < 0){ // Determine what step size to use on the y-axis
         step.y = -1; // Step Down
         ray_length_1d.y = (ray_start.y - current_cell.y) * ray_unit_step_size.y; // How much left in cell below
      } else {
         step.y = 1; // Step Up
         ray_length_1d.y = ((current_cell.y + 1) - ray_start.y) * ray_unit_step_size.y; // How much left in cell above
      }

      // Determine Length of array
      boolean hit_wall = false;
      float curr_distance = 0f;

      while(!hit_wall && curr_distance < (float) MAX_DISTANCE){
         if(ray_length_1d.x < ray_length_1d.y){
            // Walk In X Direction
            current_cell.x += step.x;
            curr_distance = ray_length_1d.x;
            ray_length_1d.x += ray_unit_step_size.x;

         } else {
            // Walk in Y Direction
            current_cell.y += step.y;
            curr_distance = ray_length_1d.y;
            ray_length_1d.y += ray_unit_step_size.y;
         }

         // Check for collision
         if(current_cell.x >= 0 && current_cell.y >= 0 && current_cell.x < Terrain.WIDTH && current_cell.y < Terrain.HEIGHT)
            if(world[generator.getIndex((int)current_cell.x, (int)current_cell.y)] == Terrain.WALL){
               // Collided With Wall
               //System.out.println("\nCELL: " + current_cell.x + "," + current_cell.y);
               hit_wall = true;
            }
      }

      // Update output
      if(!hit_wall)
         return Optional.empty();

      // Hit wall determine if this is before or after the target
      PVector hit_point = PVector.add(ray_start, ray_direction.mult(curr_distance));
      return Optional.of(hit_point);
      // Todo: This isn't exactly perfect. if the wall blocking the ray is the one the grapple is attached to
   }
}
