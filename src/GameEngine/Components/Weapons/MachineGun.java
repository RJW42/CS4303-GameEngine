package GameEngine.Components.Weapons;

import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameObjects.Bullet;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import javax.crypto.Mac;

public class MachineGun {
   public static final float SPREAD_ANGLE = 0.1f;
   public static final int NUM_BARRELS    = 1;
   public static final int FIRE_RATE      = 10;
   public static final int MUZZLE_SPEED   = 50;
   public static final float BULLET_MASS  = 0.1f;
   public static final float BULLET_RAD   = 0.05f;
   
   
   private MachineGun(){
      // Utility class no constructor
   };
   
   
   public static Gun create(GameObject parent, PVector location) {
      return new Gun(
              parent,
              new RectRenderer(parent, new PVector(0, 0, 0), 0.5f, 0.1f),
              new BulletFactory() {
                 @Override
                 public Bullet newBullet(PVector spawn_location, PVector spawn_velocity) {
                    return new Bullet(parent.sys, parent, spawn_velocity, spawn_location, BULLET_RAD, BULLET_MASS);
                 }
              },
              new PVector(),
              SPREAD_ANGLE,
              NUM_BARRELS,
              FIRE_RATE,
              MUZZLE_SPEED
      );
   }


}
