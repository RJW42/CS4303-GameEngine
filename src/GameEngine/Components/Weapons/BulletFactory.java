package GameEngine.Components.Weapons;

import GameEngine.GameObjects.Bullets.Bullet;
import processing.core.PVector;

public interface BulletFactory {
   public Bullet newBullet(PVector spawn_location, PVector spawn_velocity);
}
