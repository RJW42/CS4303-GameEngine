package GameEngine.Components.AIComponents;


import GameEngine.Components.Component;
import GameEngine.Components.Renderers.GifRenderer;
import GameEngine.GameObjects.GameObject;


public class Damageable extends Component {
   // Attributes
   public static final int DEFAULT_START_HEALTH    = 100;
   public static final float MOMENTUM_MUL_FACTOR   = 6f;

   public float health;
   public GifRenderer flash_renderer;

   // Constructor
   public Damageable(GameObject parent, float health, GifRenderer damage_renderer) {
      super(parent);
      this.health = health;
      this.flash_renderer = damage_renderer;
   }

   public Damageable(GameObject parent, float health) {
      this(parent, health, null);
   }

   public Damageable(GameObject parent) {
      this(parent, DEFAULT_START_HEALTH, null);
   }


   // Methods
   public void hitByBullet(float momentum){
      // Called when hit by a bullet.

      // First calculate the health loss from this bullet
      float damage_dec = momentum * MOMENTUM_MUL_FACTOR;
      health -= damage_dec;

      // Check if should flash
      if(flash_renderer != null){
         flash_renderer.gif.restart();
      }
   }
}
