package GameEngine.Components;


import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class RandomMover extends Component {
   // Attributes
   public static final float SPEED = 1.5f;

   public PVector target_pos;

   // Constructor
   public RandomMover(GameObject parent) {
      super(parent);
   }


   // Methods 
   public void start() {}
   public void draw() {}

   public void update() {
      // Change position
      if(update_goal())
         target_pos = random_pos();

      // Walk to pos
      PVector vel = PVector.sub(target_pos, parent.pos).normalize().mult(SPEED);
      parent.pos.add(vel.mult(sys.DELTA_TIME));
   }


   private boolean update_goal(){
      if(target_pos == null) return true;

      return PVector.dist(target_pos, parent.pos) < 0.5f;
   }

   private PVector random_pos(){
      return new PVector(sys.random(1, Terrain.WIDTH - 1), sys.random(1, Terrain.HEIGHT - 1));
   }
}
