package GameEngine.Components.PlayerComponents;

public enum Powerups {
   HEALTH_PACK("health_pack"),
   GUN_BONUS("gun_powerup");

   // Attributes
   public final String sprite_name;

   // Constructor
   Powerups(String sprite_name) {
      this.sprite_name = sprite_name;
   }


   // Methods
}
