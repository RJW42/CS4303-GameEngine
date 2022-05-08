package GameEngine.Components.PlayerComponents;

public enum Powerups {
   HEALTH_PACK("health_pack", 0),
   GUN_BONUS("gun_powerup", 1);

   // Attributes
   public final String sprite_name;
   public final int save_number;

   // Constructor
   Powerups(String sprite_name, int save_number) {
      this.sprite_name = sprite_name;
      this.save_number = save_number;
   }


   // Methods
   public static Powerups get_from_save_number(int i){
      if(i == 0){
         return HEALTH_PACK;
      } else {
         return GUN_BONUS;
      }
   }
}
