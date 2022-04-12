package GameEngine.Components.CollisionComponents.CollisionHandlers;

import GameEngine.Components.AIComponents.AIMovementController.AIMovementController;
import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.ForceManager;
import GameEngine.Components.PlayerComponents.CharacterController;
import GameEngine.GameObjects.Terrain;
import GameEngine.GameObjects.Player;
import processing.core.PVector;

import javax.swing.text.html.Option;
import java.util.Optional;

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
      // Track list of collisions
      // Handle this list in order of closeness
      ForceManager force_manager = player_obj.getComponent(ForceManager.class);
      Optional<RayRectResult> collision_info = dynamic_rect_vs_rect_col(player_col, force_manager.velocity, terrain);

      if(collision_info.isPresent()){
         player_obj.pos.x -= force_manager.velocity.x * player_obj.sys.DELTA_TIME;
         player_obj.pos.y -= force_manager.velocity.y * player_obj.sys.DELTA_TIME;
         force_manager.velocity = new PVector(0, 0);
      }
   }

   public static void start_collisions(){

   }

   public static void end_collisions(){

   }

   public static Optional<RayRectResult> dynamic_rect_vs_rect_col(RectCollisionComponent in, PVector in_vel, RectCollisionComponent target){
      if(in_vel.x == 0 && in_vel.y == 0)
         return Optional.empty();

      PVector target_pos = target.pos();
      target_pos.x -= in.width / 2f;
      target_pos.y -= target.height + in.height / 2f;

      float target_width = target.width + in.width;
      float target_height = target.height + in.height;

      PVector ray_origin = PVector.add(in.pos(), new PVector(in.width /2f, -in.height /2f));

      Optional<RayRectResult> result = ray_vs_rect_col(ray_origin, PVector.mult(in_vel, in.sys.DELTA_TIME), target_pos, target_width, target_height);

      if(result.isEmpty() || result.get().t_hit_near > 1f){
         return Optional.empty();
      }

      return result;
   }

   public static Optional<RayRectResult> ray_vs_rect_col(PVector ray_origin, PVector ray_direction, PVector target_pos, float target_width, float target_height){
      // Note expects target_pos to be bottom left, in our game it is actually the top left

      PVector t_near = PVector.sub(target_pos, ray_origin);
      PVector t_far = PVector.sub(PVector.add(target_pos, new PVector(target_width, target_height)), ray_origin);

      vector_div(t_near, ray_direction);
      vector_div(t_far, ray_direction);

      if(t_near.x > t_far.x) {
         float tmp = t_far.x;
         t_far.x = t_near.x;
         t_near.x = tmp;
      }
      if(t_near.y > t_far.y) {
         float tmp = t_far.y;
         t_far.y = t_near.y;
         t_near.y = tmp;
      }

      if (t_near.x > t_far.y || t_near.y > t_far.x) return Optional.empty();

      float t_hit_near = Math.max(t_near.x, t_near.y);
      float t_hit_far = Math.min(t_far.x, t_far.y);

      if (t_hit_far < 0) return Optional.empty();

      PVector contact_point = PVector.add(PVector.mult(ray_direction, t_hit_near), ray_origin);
      PVector contact_normal = new PVector(0, 0);

      if (t_near.x > t_near.y){
         if (ray_direction.x < 0)
            contact_normal = new PVector(1, 0);
         else
            contact_normal = new PVector(-1, 0);
      } else if (t_near.x < t_near.y) {
         if (ray_direction.y < 0)
            contact_normal = new PVector(0, 1);
         else
            contact_normal = new PVector(0, -1);
      }

      return Optional.of(new RayRectResult(contact_point, contact_normal, t_hit_near));
   }

   public static void vector_div(PVector source, PVector divedor){
      source.x /= divedor.x;
      source.y /= divedor.y;
   }


   public static class RayRectResult {
      public PVector contact_point;
      public PVector contact_normal;
      public float t_hit_near;

      public RayRectResult(PVector contact_point, PVector contact_normal, float t_hit_near) {
         this.contact_point = contact_point;
         this.contact_normal = contact_normal;
         this.t_hit_near = t_hit_near;
      }
   }

}
