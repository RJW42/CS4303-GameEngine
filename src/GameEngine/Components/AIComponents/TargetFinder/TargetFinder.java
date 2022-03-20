package GameEngine.Components.AIComponents.TargetFinder;

import GameEngine.Components.Component;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Terrain;
import GameEngine.Utils.Pair;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class TargetFinder extends Component {
   // Attributes
   public static final int DEFAULT_UPDATE_RATE = 30; // Number of frames before update

   public float view_range;
   public boolean always_see;

   private int[] world;
   private TerrainGenerator generator;
   private ArrayList<GameObject> targets;
   private TargetUpdater updater;
   private int updater_rate;

   // Constructor
   public TargetFinder(GameObject parent, float view_range, TargetUpdater updater){
      this(parent, view_range, updater, DEFAULT_UPDATE_RATE, false);
   }

   public TargetFinder(GameObject parent, float view_range, TargetUpdater updater, boolean always_see){
      this(parent, view_range, updater, DEFAULT_UPDATE_RATE, always_see);
   }

   public TargetFinder(GameObject parent, float view_range, TargetUpdater updater, int updater_rate, boolean always_see) {
      super(parent);
      this.view_range = view_range;
      this.updater = updater;
      this.updater_rate = updater_rate;
      this.always_see = always_see;
   }

   // Methods
   @Override
   public void start() {
      generator = sys.terrain.getComponent(TerrainGenerator.class);
      world = generator.getWorld();
      targets = new ArrayList<>();
      targets = updater.getTargets(targets, this);
   }

   @Override
   public void update() {
      // Check if the targets should be updated
      if(sys.frameCount % updater_rate == 0)
         targets = updater.getTargets(targets, this);
   }

   @Override
   public void draw() {
//      // Todo: this is for debugging remove at some point
//      if(targets.size() == 0)
//         return;
//
//      sys.stroke(255, 0, 0);
//      GameObject obj = targets.get(0);
//      if(getClosestViewableTarget().isPresent()){
//         sys.stroke(0, 255, 0);
//         obj = getClosestViewableTarget().get();
//      }
//
//      if(obj == null)
//         return;
//      sys.line(parent.pos.x, parent.pos.y, obj.pos.x, obj.pos.y);
   }


   public Optional<GameObject> getClosestViewableTarget(){
      if(always_see)
         return getClosetTarget();

      // Adapted from https://www.youtube.com/watch?v=NbSee-XM7WA
      return targets.stream()
         .map(target -> {
            // Case a ray from the game object to the target to get its position
            Pair<GameObject, Float> output = new Pair<>(target, 0f);

            // Init values
            PVector ray_start = parent.pos;
            PVector ray_direction = PVector.sub(target.pos, ray_start).normalize();
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
            float max_distance = view_range;
            float curr_distance = 0f;

            while(!hit_wall && curr_distance < max_distance){
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
            if(hit_wall){
               // Hit wall determine if this is before or after the target
               PVector hit_point = PVector.add(ray_start, ray_direction.mult(curr_distance));
               float wall_dis = PVector.dist(hit_point, parent.pos);
               float target_dis = PVector.dist(target.pos, parent.pos);

               output.second = (wall_dis > target_dis) ? target_dis : view_range * 2;
            } else {
               // Didn't Hit wall get distance to target
               output.second = PVector.dist(parent.pos, target.pos);
            }

            // Finished
            return output;

         }) // Get The Game Object With the Minimum view rage under the boundary
        .filter(pair -> pair.second < view_range)
        .min(Comparator.comparingDouble(Pair::getSecond))
        .map(Pair::getFirst);
   }

   public Optional<GameObject> getClosetTarget(){
      return targets.stream()
         .map(target ->
            new Pair<Float, GameObject>(PVector.dist(parent.pos, target.pos), target))
         .min(Comparator.comparing(Pair::getFirst))
         .map(Pair::getSecond);
   }
}
