package GameEngine.Components.Weapons;


import GameEngine.Components.Component;
import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Bullet;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class Gun extends Component {
   // Attributes
   public static final int FIRE_RATE            = 15;
   public static final float MUZZLE_SPEED       = 15f;

   public float muzzle_speed;
   public float fire_time;
   public float prev_fire_time;
   public float spread_angle;
   public int num_barrels;
   public BulletFactory bullet_factory;
   public PVector bullet_spawn_offset;
   public boolean fire_on_key;

   // Constructor
   public Gun(GameObject parent, BulletFactory bullet_factory, PVector bullet_spawn_offset){
      this(parent, bullet_factory, bullet_spawn_offset, 0, 1, FIRE_RATE, true);
   }


   public Gun(GameObject parent, BulletFactory bullet_factory, PVector bullet_spawn_offset, float spread_angle, int num_barrels, int fire_rate, boolean fire_on_key) {
      super(parent);

      // Init bullets per second from fire rate
      this.fire_time = (1f / fire_rate);
      this.prev_fire_time = 0f;

      this.muzzle_speed = MUZZLE_SPEED;
      this.spread_angle = spread_angle / 2;
      this.num_barrels = num_barrels;
      this.fire_on_key = fire_on_key;

      // Init factory
      this.bullet_factory = bullet_factory;
      this.bullet_spawn_offset = bullet_spawn_offset;
   }


   // Methods
   public void start() {
   }

   public void update() {
      // Update prev fire time
      prev_fire_time += sys.DELTA_TIME;
   }


   public void fire(PVector target){
      if(prev_fire_time >= fire_time){
         for(int i = 0; i < num_barrels; i++)
            fire_barrel(target);
      }
   }

   private void fire_barrel(PVector target){
      // Reset fire time
      prev_fire_time = 0;

      // Create bullet velocity
      PVector bullet_spawn_location = PVector.add(parent.pos, bullet_spawn_offset);
      PVector vel_norm = PVector.sub(bullet_spawn_location, target).normalize().mult(-1);

      // Add spread if needed
      if(spread_angle != 0)
         vel_norm.rotate(sys.random(-spread_angle, spread_angle));

      PVector bullet_vel = vel_norm.setMag(muzzle_speed);

      // Create new bullet
      sys.spawn(bullet_factory.newBullet(bullet_spawn_location.copy(), bullet_vel), 2);
   }
}
