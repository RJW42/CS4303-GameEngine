package GameEngine.Components.Weapons;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class Gun extends Component {
   // Attributes
   public float muzzle_speed;
   public float fire_time;
   public float prev_fire_time;
   public float spread_angle;
   public int num_barrels;
   public BulletFactory bullet_factory;
   public PVector bullet_spawn_offset;
   public Component renderer;
   public boolean active;

   // Constructor
   public Gun(GameObject parent, Component renderer, BulletFactory bullet_factory, PVector bullet_spawn_offset, float spread_angle, int num_barrels, int fire_rate, int muzzle_speed) {
      super(parent);

      // Init attributes
      this.active = true;
      this.renderer = renderer;

      // Init bullets per second from fire rate
      this.fire_time = (1f / fire_rate);
      this.prev_fire_time = 0f;

      this.muzzle_speed = muzzle_speed;
      this.spread_angle = spread_angle / 2;
      this.num_barrels = num_barrels;

      // Init factory
      this.bullet_factory = bullet_factory;
      this.bullet_spawn_offset = bullet_spawn_offset;
   }


   // Methods
   public void start() {
      renderer.start();
   }


   public void update() {
      // Update prev fire time
      prev_fire_time += sys.DELTA_TIME;

      // Todo: want to test.json to see if prev_fire_time should change when not active
      renderer.update();
   }

   public void draw(){
      if(active)
         renderer.draw();
   }


   public void fire(PVector target){
      if(prev_fire_time >= fire_time){
         for(int i = 0; i < num_barrels; i++)
            spawn_bullet(target);
      }
   }


   private void spawn_bullet(PVector target){
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
