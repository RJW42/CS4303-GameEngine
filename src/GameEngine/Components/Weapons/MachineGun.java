package GameEngine.Components.Weapons;

import GameEngine.GameObjects.Bullets.Bullet;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

public class MachineGun {
   public static final float SPREAD_ANGLE = 0.1f;
   public static final int NUM_BARRELS    = 1;
   public static final int FIRE_RATE      = 10;
   public static final int MUZZLE_SPEED   = 2;
   public static final float BULLET_MASS  = 0.1f;
   public static final float BULLET_RAD   = 0.05f;
   public static final float BARREL_LENGTH= 0.5f;
   public static final float BARREL_HEIGHT= 0.1f;
   
   private MachineGun(){
      // Utility class no constructor
   };
   
   
   public static Gun create(GameObject parent, PVector location) {
      // Create renderer component
      GunRenderer renderer = new RectGunRenderer(parent, location, BARREL_LENGTH, BARREL_HEIGHT);

      parent.addComponent(renderer);

      // Create gun
      return new Gun(
              parent,
              renderer,
              new BulletFactory() {
                 @Override
                 public Bullet newBullet(PVector spawn_location, PVector spawn_velocity) {
                    return new Bullet(parent.sys, parent, spawn_velocity, spawn_location, BULLET_RAD, BULLET_MASS, 0, true);
                 }
              },
              location,
              SPREAD_ANGLE,
              NUM_BARRELS,
              FIRE_RATE,
              MUZZLE_SPEED,
              BARREL_LENGTH,
              BARREL_LENGTH
      );
   }


}
