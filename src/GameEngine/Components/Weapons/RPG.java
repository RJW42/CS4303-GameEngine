package GameEngine.Components.Weapons;

import GameEngine.GameObjects.Bullets.Bullet;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

public class RPG {
   public static final float SPREAD_ANGLE   = 0.1f;
   public static final int NUM_BARRELS      = 1;
   public static final int FIRE_RATE        = 2;
   public static final int MUZZLE_SPEED     = 15;
   public static final float BULLET_MASS    = 0.5f;
   public static final float BULLET_RAD     = 0.1f;
   public static final float BARREL_LENGTH  = 0.6f;
   public static final float BARREL_HEIGHT  = 0.15f;
   public static final float EXPLOSION_SIZE = 100;

   private RPG(){
      // Utility class no constructor
   };
   
   
   public static Gun create(GameObject parent, PVector location) {
      // Create renderer component
      RectGunRenderer renderer = new RectGunRenderer(parent, location, BARREL_LENGTH, BARREL_HEIGHT);
      renderer.rect_col = new PVector(41, 77, 51);

      // Create gun
      return new Gun(
              parent,
              renderer,
              null,
              new BulletFactory() {
                 @Override
                 public Bullet newBullet(PVector spawn_location, PVector spawn_velocity) {
                    return new Bullet(parent.sys, parent, spawn_velocity, spawn_location, BULLET_RAD, BULLET_MASS, EXPLOSION_SIZE, false);
                 }
              },
              location,
              SPREAD_ANGLE,
              NUM_BARRELS,
              FIRE_RATE,
              MUZZLE_SPEED,
              BARREL_LENGTH,
              BARREL_LENGTH,
              "rpg_fire"
      );
   }


}
