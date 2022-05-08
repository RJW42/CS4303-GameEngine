package GameEngine.Components.PlayerComponents;


import GameEngine.Components.Component;
import GameEngine.Components.Damagable;
import GameEngine.Components.Weapons.Gun;
import GameEngine.Components.Weapons.RectGunRenderer;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Powerup;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.util.ArrayList;


public class PowerupManager extends Component {
   // Attributes
   public static final float GUN_POWERUP_TIME = 10f;

   public Damagable damagable;
   public GunController gun_controller;
   public ArrayList<ActivePowerup> powerups;


   // Constructor
   public PowerupManager(Player parent) {
      super(parent);

      // Init attributes
      this.powerups = new ArrayList<>();
   }


   // Methods 
   public void start() {
      // Get all required classes
      this.damagable = parent.getComponent(Damagable.class);
      this.gun_controller = parent.getComponent(GunController.class);
   }

   public void draw() { }
   public void update() {
      for(int i = powerups.size() - 1; i >= 0; i--){
         var powerup = powerups.get(i);

         if(powerup instanceof GunPowerup && update_gun_powerup((GunPowerup) powerup)){
            powerups.remove(i);
         }
      }
   }


   public boolean update_gun_powerup(GunPowerup powerup){
      if(powerup.time_remaning <= 0){
         powerup.renderer.border_col = RectGunRenderer.BORDER_COL;
         powerup.gun.fire_time *= 2f;
         return true;
      }

      powerup.renderer.border_col = new PVector(212, 175, 55);
      powerup.time_remaning -= sys.DELTA_TIME;

      return false;
   }


   public void activate_powerup(Powerups type){
      switch (type){
         case HEALTH_PACK:
            damagable.health = Player.HEALTH;
            break;
         case GUN_BONUS:
            var powerup = new GunPowerup(
                GUN_POWERUP_TIME, gun_controller.active_gun
            );

            powerups.add(powerup);
            powerup.gun.fire_time /= 2f;
            break;
      }
   }


   public void collision(Powerup powerup){
      if(powerup.is_dead)
         return;
      powerup.is_dead = true;
      activate_powerup(powerup.type);
   }



   // Util Classes
   private class ActivePowerup {
      public float time_remaning;

      public ActivePowerup(float time_remaning) {
         this.time_remaning = time_remaning;
      }
   }


   private class GunPowerup extends ActivePowerup{
      public final Gun gun;
      public final RectGunRenderer renderer;

      public GunPowerup(float time_remaning, Gun gun) {
         super(time_remaning);
         this.gun = gun;
         this.renderer = (RectGunRenderer) gun.renderer;
      }
   }
}
