package GameEngine.Components.Weapons;


import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.GifRenderer;
import GameEngine.GameObjects.GameObject;
import ddf.minim.AudioSample;
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
   public GifRenderer muzzle_flash;

   private PVector gun_pos;
   private AudioSample on_fire;
   private ForceManager force_manager;

   // Constructor
   public Gun(GameObject parent, GunRenderer renderer, GifRenderer muzzle_flash, BulletFactory bullet_factory, PVector offset, float spread_angle, int num_barrels, int fire_rate, int muzzle_speed, float barrel_length, float barrel_height, String fire_noise) {
      super(parent);

      // Init attributes
      this.muzzle_flash = muzzle_flash;
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

      if(fire_noise != null){
         this.on_fire = sys.audio_manager.get_sample(fire_noise);
         this.on_fire.setGain(-22);
      }

      // Init factory
      this.bullet_factory = bullet_factory;
      this.offset = offset;

      this.renderer.gun = this;
   }


   // Methods
   public void start() {
      renderer.start();
      force_manager = parent.getComponent(ForceManager.class);
      if(muzzle_flash != null) muzzle_flash.start();
   }


   public void update() {
      renderer.update();
      if(muzzle_flash != null) muzzle_flash.update();

      // Update prev fire time
      prev_fire_time += sys.DELTA_TIME;

      // Get rotation angle
      gun_pos = PVector.add(offset, parent.pos);

      PVector v1 = new PVector(1, 0);
      PVector v2 = PVector.sub(target_pos, gun_pos).normalize();

      float dot = v1.x * v2.x + v1.y * v2.y;
      float det = v1.x * v2.y - v1.y * v2.x;

      rotation_angle = (float) Math.atan2(det, dot);

      if(muzzle_flash != null) {
         PVector muzzle_offset = PVector.fromAngle(rotation_angle).mult(barrel_length + muzzle_flash.width / 2f);
         muzzle_flash.rotation_angle = PConstants.TWO_PI - rotation_angle;
         muzzle_flash.offset = muzzle_offset.add(offset);
      }
   }

   public void draw(){
      if(active) {
         renderer.draw();
         if(muzzle_flash != null) muzzle_flash.draw();
      }
   }


   public void fire(){
      if(prev_fire_time >= fire_time){
         if(muzzle_flash != null)  muzzle_flash.gif.restart();
         for(int i = 0; i < num_barrels; i++) {
            spawn_bullet();
            renderer.fire();
            on_fire.trigger();
         }
      }
   }


   private void spawn_bullet(){
      // Reset fire time
      prev_fire_time = 0;

      // Create bullet velocity
      PVector bullet_offset = PVector.fromAngle(rotation_angle).mult(0.5f);
      PVector bullet_spawn_location = bullet_offset.add(gun_pos);
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
