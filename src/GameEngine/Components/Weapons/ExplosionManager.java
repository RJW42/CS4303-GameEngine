package GameEngine.Components.Weapons;


import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Bullets.Explosion;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class ExplosionManager extends Component {
   // Attributes
   public static final PVector EXPLOSION_COLOUR = new PVector(255, 0, 0);

   public float speed;
   public float rad;
   public float max_rad;

   private CircleCollisionComponent col;

   // Constructor
   public ExplosionManager(Explosion parent, float speed, float max_rad) {
      super(parent);

      // init attributes
      this.speed = speed;
      this.rad = 0;
      this.max_rad = max_rad;
   }


   // Methods 
   public void start() {
      col = parent.getComponent(CircleCollisionComponent.class);
   }

   public void update() {
      if(rad >= max_rad){
         ((Explosion)parent).is_dead = true;
         return;
      }


      rad += speed;
      col.rad = rad;
      col.offset.x = -rad;
      col.offset.y = rad;
      col.bound_box_width = rad * 2;
      col.bound_box_height = rad * 2;
   }

   public void draw() {
      sys.stroke(EXPLOSION_COLOUR.x, EXPLOSION_COLOUR.y, EXPLOSION_COLOUR.z);
      sys.strokeWeight(0.15f);
      sys.fill(255, 50);
      sys.circle(parent.pos.x, parent.pos.y, rad * 2f);
      sys.strokeWeight(1f / GameEngine.PIXEL_TO_METER);
   }
}
