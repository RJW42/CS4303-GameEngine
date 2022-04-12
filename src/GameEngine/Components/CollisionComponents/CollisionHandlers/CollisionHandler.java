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
import java.util.TreeSet;

public class CollisionHandler {
   // Track possible collisions
   private static final TreeSet<PossibleCollision> player_terrain_collisions = new TreeSet<>();


   public static void handle_collision(BaseCollisionComponent obj1, BaseCollisionComponent obj2){
      // Todo: implement. This could be done way more efficent maybe use hashmap

      // Player Terrian Collision
      if(obj1.parent instanceof Player && obj2.parent instanceof Terrain){
         player_wall_collision((RectCollisionComponent) obj1, (RectCollisionComponent) obj2);
      } else if(obj2.parent instanceof Player && obj1.parent instanceof Terrain){
         player_wall_collision((RectCollisionComponent) obj2, (RectCollisionComponent) obj1);
      }
   }

   public static void start_collisions(){
      player_wall_collision_start();
   }

   public static void end_collisions(){
      player_wall_collision_end();
   }

   private static void player_wall_collision(RectCollisionComponent player_col, RectCollisionComponent terrain){
      // Don't track players triggers, these are not used for physics collisions
      if(player_col.isTrigger())
         return; // Todo: this may become an issue need to maybe add another tag

      // Get distance between player and terrain
      float distance = PVector.dist(
              PVector.add(player_col.pos(), new PVector(player_col.width / 2f, player_col.height / -2f)),
              PVector.add(terrain.pos(), new PVector(terrain.width /2f, terrain.height / -2f))
      );

      player_terrain_collisions.add(new PossibleCollision(player_col, terrain, distance));
   }

   private static void player_wall_collision_start(){
      player_terrain_collisions.clear();
   }

   private static void player_wall_collision_end(){
      for(PossibleCollision pc : player_terrain_collisions) {
         // Get objects out of collision and check if collision still occours
         RectCollisionComponent player_col = pc.comp1;
         RectCollisionComponent terrain = pc.comp2;
         Player player_obj = (Player) player_col.parent;

         if(!player_col.collidesWith(terrain))
            continue;

         // Still colliding resolve collision
         ForceManager force_manager = player_obj.getComponent(ForceManager.class);
         Optional<RayRectResult> opt_collision_info = dynamic_rect_vs_rect_col(player_col, force_manager.velocity, terrain);

         if (opt_collision_info.isPresent()) {
            RayRectResult collision_info = opt_collision_info.get();

            player_obj.pos.x = collision_info.contact_normal.x != 0 ?
                    collision_info.contact_point.x - player_col.width / 2f : player_obj.pos.x;
            player_obj.pos.y = collision_info.contact_normal.y != 0 ?
                    collision_info.contact_point.y + player_col.height - player_col.height / 2f : player_obj.pos.y;

            force_manager.velocity.x += collision_info.contact_normal.x * Math.abs(force_manager.velocity.x);
            force_manager.velocity.y += collision_info.contact_normal.y * Math.abs(force_manager.velocity.y);

//         if(collision_info.contact_normal.y == 1){
//            force_manager.grounded = true;
//            // Todo: Works but not easy to get the non grounded. Could set to grounded each frame, will test
//         }
         }
      }
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

   private static class PossibleCollision implements Comparable<PossibleCollision> {
      RectCollisionComponent comp1;
      RectCollisionComponent comp2;
      float distance;

      public PossibleCollision(RectCollisionComponent comp1, RectCollisionComponent comp2, float distance) {
         this.comp1 = comp1;
         this.comp2 = comp2;
         this.distance = distance;
      }

      @Override
      public int compareTo(PossibleCollision possibleCollision) {
         return Float.compare(distance, possibleCollision.distance);
      }
   }

}
