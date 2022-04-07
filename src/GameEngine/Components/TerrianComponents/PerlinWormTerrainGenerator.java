package GameEngine.Components.TerrianComponents;

import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Terrain;
import processing.core.PConstants;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PVector;

import java.util.Arrays;
import java.util.Random;

public class PerlinWormTerrainGenerator extends TerrainGenerator{
   // Attributes
   private int[] world;
   private Random random;

   // Constructor
   public PerlinWormTerrainGenerator(GameObject parent, int seed) {
      super(parent, seed);
      random = new Random();
   }

   // Methods
   @Override
   public int[] createWorld() {
      // Init world object
      world = new int[width * height];
      Arrays.fill(world, Terrain.WALL);

      // Perlin worms
      sys.noiseSeed(seed);

      float worm_x = 1;
      float worm_y = 1;

      PVector direction = PVector.random2D().normalize();
      int i = 0;

      while(worm_x < width && worm_y < height && worm_x >= 0 && worm_y >= 0 && i++ < 300){
         world[getIndex((int)worm_x, (int)worm_y)] = Terrain.AIR;

         //PVector move_dir = get_move_direction(direction, 1f, worm_x, worm_y, PConstants.PI / 4f);
         PVector move_dir = move_towards_point(new PVector(width, height), 0.4f, direction, 1f, worm_x, worm_y, PConstants.HALF_PI);

         worm_x += move_dir.x;
         worm_y += move_dir.y;

         direction = move_dir;
      }



      return world;
   }

   private PVector get_move_direction(PVector direction, float scale, float x, float y, float max_angle_change){
      // Get direction based of perlin noise
      float noise = sys.noise(x * scale, y * scale);
      float angle = ((max_angle_change * 2f) * noise) - max_angle_change;
      System.out.println(angle * PConstants.RAD_TO_DEG);
      //return PVector.fromAngle(angle, PVector.mult(direction, -1)).normalize();
      return new PVector((float)(direction.x * Math.cos(angle) - direction.y * Math.sin(angle)), (float)(direction.x * Math.sin(angle) + direction.y * Math.cos(angle)));
   }

   private PVector move_towards_point(PVector convergence_point, float weight, PVector direction, float scale, float x, float y, float max_angle_change){
      // Get perlin direction, then adjust to point towards
      PVector new_direction = get_move_direction(direction, scale, x, y, max_angle_change);
      PVector dir_to_point = PVector.sub(convergence_point, new PVector(x, y)).normalize();
      return PVector.add(PVector.mult(new_direction, (1 - weight)), PVector.mult(dir_to_point, weight)).normalize();
   }

   @Override
   public int[] getWorld() {
      if(world == null)
         return createWorld();
      return world;
   }
}
