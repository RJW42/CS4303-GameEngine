package GameEngine.Components.Weapons;


import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.GameObjects.GameObject;
import processing.core.PConstants;
import processing.core.PVector;


public class Gun extends Component {
   // Attributes
   public float muzzle_speed;
   public float fire_time;
   public float prev_fire_time;
   public float spread_angle;
   public float rotation_angle;
   public int num_barrels;
   public BulletFactory bullet_factory;
   public PVector offset;
   public GunRenderer renderer;
   public boolean active;
   public float barrel_length;
   public float barrel_height;
   public PVector target_pos;

   private PVector gun_pos;
   private ForceManager force_manager;

   // Constructor
   public Gun(GameObject parent, GunRenderer renderer, BulletFactory bullet_factory, PVector offset, float spread_angle, int num_barrels, int fire_rate, int muzzle_speed, float barrel_length, float barrel_height) {
      super(parent);

      // Init attributes
      this.active = true;
      this.renderer = renderer;
      this.barrel_length = barrel_length;
      this.barrel_height = barrel_height;
      this.target_pos = new PVector();

      // Init bullets per second from fire rate
      this.fire_time = (1f / fire_rate);
      this.prev_fire_time = 0f;

      this.muzzle_speed = muzzle_speed;
      this.spread_angle = spread_angle / 2;
      this.num_barrels = num_barrels;

      // Init factory
      this.bullet_factory = bullet_factory;
      this.offset = offset;

      this.renderer.gun = this;
   }


   // Methods
   public void start() {
      renderer.start();
      force_manager = parent.getComponent(ForceManager.class);
   }


   public void update() {
      renderer.update();

      // Update prev fire time
      prev_fire_time += sys.DELTA_TIME;

      // Get rotation angle
      gun_pos = PVector.add(offset, parent.pos);

      PVector v1 = new PVector(1, 0);
      PVector v2 = PVector.sub(target_pos, gun_pos).normalize();

      float dot = v1.x * v2.x + v1.y * v2.y;
      float det = v1.x * v2.y - v1.y * v2.x;

      rotation_angle = (float) Math.atan2(det, dot);
   }

   public void draw(){
      if(active) renderer.draw();
   }


   public void fire(){
      if(prev_fire_time >= fire_time){
         for(int i = 0; i < num_barrels; i++) {
            spawn_bullet();
            renderer.fire();
         }
      }
   }


   private void spawn_bullet(){
      // Reset fire time
      prev_fire_time = 0;

      // Create bullet velocity
      PVector bullet_offset = PVector.fromAngle(rotation_angle).mult(barrel_length);
      PVector bullet_spawn_location = PVector.add(gun_pos, bullet_offset);
      PVector vel_norm = PVector.sub(bullet_spawn_location, target_pos).normalize().mult(-1);

      // Add spread if needed
      if(spread_angle != 0)
         vel_norm.rotate(sys.random(-spread_angle, spread_angle));

      PVector bullet_vel = vel_norm.setMag(muzzle_speed);

//      if(force_manager != null)
//         bullet_vel.add(force_manager.velocity);


      // Create new bullet
      sys.spawn(bullet_factory.newBullet(bullet_spawn_location.copy(), bullet_vel), 2);
   }
}
