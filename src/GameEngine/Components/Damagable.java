package GameEngine.Components;


import GameEngine.Components.Component;
import GameEngine.GameObjects.Bullets.Explosion;
import GameEngine.GameObjects.GameObject;


public class Damagable extends Component {
   // Attributes
   public final int starting_health;
   public int health;

   // Constructor
   public Damagable(GameObject parent, int starting_health) {
      super(parent);

      // Init attributes
      this.starting_health = starting_health;
      this.health = starting_health;
   }


   // Methods 
   public void start() {}
   public void update() {}

   public void draw() {
      // Todo: draw health bar maybe
      //       or draw a damage flash
   }

   public void shot(float momentum) {
      health -= momentum;
   }

   public void blast(Explosion explosion){
      System.out.println(explosion.explosion_size);
   }
}
