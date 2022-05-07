package GameEngine.Components;


import GameEngine.Components.Component;
import GameEngine.GameObjects.Bullets.Explosion;
import GameEngine.GameObjects.GameObject;
import ddf.minim.AudioSample;
import processing.core.PVector;


public class Damagable extends Component {
   // Attributes
   public static final float FLASH_LENGTH = 0.5f;

   public final int starting_health;
   public int health;

   public float time_in_flash;
   public float width;
   public float height;
   public AudioSample punch;

   // Constructor
   public Damagable(GameObject parent, float width, float height, int starting_health) {
      super(parent);

      // Init attributes
      this.starting_health = starting_health;
      this.health = starting_health;
      this.width = width;
      this.height = height;
      this.punch = sys.audio_manager.get_sample("punch");
      this.punch.setGain(-10);
   }


   // Methods 
   public void start() {}
   public void update() {}

   public void draw() {
      if(time_in_flash > 0){
         sys.fill(255, 0, 0, 127f * (time_in_flash / FLASH_LENGTH));
         sys.rect(parent.pos.x, parent.pos.y, width, -height);
         time_in_flash -= sys.DELTA_TIME;
      }
   }

   public void shot(float momentum) {
      health -= momentum;
      time_in_flash = FLASH_LENGTH;
   }
   public void punch(float damage){
      punch.trigger();
      health -= damage;
      time_in_flash = FLASH_LENGTH;
   }


   public void blast(Explosion explosion){
      System.out.println(explosion.explosion_size);
   }

   public void lava(){
      // todo: could draw a fire animation or some thing
      health = 0;
   }
}
