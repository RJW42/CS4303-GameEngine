package GameEngine.GameObjects.Guide;

import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.GameObject;

public enum Guides {
   MONSTER("Monster", null, "monster_walking_right", Player.RENDER_WIDTH, Player.RENDER_HEIGHT, "These monsters will attack you. They are faster than you so just running away will not create enough of a gap. Try using the grapple and rpg to create space"),
   PLAYER("Player", null, "player_walking_right", Player.RENDER_WIDTH, Player.RENDER_HEIGHT, "This is u"),
   GRAPPLE_HOOK("Grapple hook", null, null, 1f, 1f, "The grapple hook allows you to swing over gaps and avoid touching monsters. To fire the grapple hook click the right mouse button. Once fired it will remain until it is blocked by a wall or the player clicks right mouse button again. You CANNOT fire the grapple onto non highlighted walls"),
   WEAPONS("Weapons", null, null, 1f, 1f, "You have access to two weapons. The machine gun and the RPG. The machine gun is small and brown and the RPG is big and green. To switch between weapons press q. To fire press left mouse button"),
   LAVE("Lava", null, "lava", 1f, 1f, "Lava is hot. You do not like hot things"),
   GOAL("Goal", null, "goal", 1f, 1f, "To complete a level you must reach this point"),
   BASIC_DOOR("Door", "basic_door_closed", null, 1f, 3f, "This is a basic door. To open this door you need to shoot the red circle. This can be done with either the machine gun or the rpg"),
   KILL_DOOR("Kill door", "kill_door_closed", null, 1f, 3f, "This is a kill door. For this door to open you need to kill all monsters in the room to the left of the door"),
   HEALTH_PACK("Health Pack", "health_pack", null, 1f, 1f, "Collect one of these to restore your health to full"),
   AMMO_POWERUP("Powerup", "gun_powerup", null, 1f, 1f, "Collecting one of these will temporary increase your current guns fire rate by 2 times");

   // Attributes


   Guides(String title, String sprite_name, String gif_name, float i_width, float i_height, String text) {
      this.title = title;
      this.sprite_name = sprite_name;
      this.gif_name = gif_name;
      this.i_width = i_width;
      this.i_height = i_height;
      this.text = text;
   }

   public final String title;
   public final String sprite_name;
   public final String gif_name;
   public final float i_width;
   public final float i_height;
   public final String text;
}
