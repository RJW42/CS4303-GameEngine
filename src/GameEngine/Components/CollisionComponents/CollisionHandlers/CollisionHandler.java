package GameEngine.Components.CollisionComponents.CollisionHandlers;

import GameEngine.Components.AIComponents.AIMovementController.AIMovementController;
import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.PlayerComponents.CharacterController;
import GameEngine.GameObjects.Terrain;
import GameEngine.GameObjects.Player;
import processing.core.PVector;

public class CollisionHandler {
   public static void handle_collision(BaseCollisionComponent obj1, BaseCollisionComponent obj2){
      // Todo: implement. This could be done way more efficent maybe use hashmap

      // Player Terrian Collision
      if(obj1.parent instanceof Player && obj2.parent instanceof Terrain){
         player_wall_collision((RectCollisionComponent) obj1, (Player) obj1.parent, (RectCollisionComponent) obj2);
      } else if(obj2.parent instanceof Player && obj1.parent instanceof Terrain){
         player_wall_collision((RectCollisionComponent) obj2, (Player) obj2.parent, (RectCollisionComponent) obj1);
      }
   }

   public static void player_wall_collision(RectCollisionComponent player_col, Player player_obj, RectCollisionComponent terrain){
      System.out.println("COLL");
   }

}
