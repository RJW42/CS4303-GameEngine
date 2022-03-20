package GameEngine.Components.PlayerComponents;

import GameEngine.Components.Component;
import GameEngine.Components.Renderers.ImageRenderer;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.util.List;

public class CharacterController extends Component {
   // Attributes
   public static final int FORWARD     = 87;
   public static final int BACKWARDS   = 83;
   public static final int LEFT        = 65;
   public static final int RIGHT       = 68;
   public PVector velocity;
   public float speed;
   public List<ImageRenderer> renderers;

   // Constructor
   public CharacterController(GameObject parent, float speed) {
      super(parent);
      this.velocity = new PVector(0f, 0f);
      this.speed = speed;
   }

   // Methods


   @Override
   public void start() {
      this.renderers = parent.getComponents(ImageRenderer.class);
   }

   public void update() {
      updateVelocity();
      updatePosition();
      updateAngle();
   }

   private void updateVelocity(){
      if(sys.input_manager.keys_pressed[FORWARD]){
         velocity.y = speed;
      } else if(sys.input_manager.keys_pressed[BACKWARDS]){
         velocity.y = -speed;
      } else {
         velocity.y = 0;
      }

      if(sys.input_manager.keys_pressed[LEFT]){
         velocity.x = -speed;
      } else if(sys.input_manager.keys_pressed[RIGHT]){
         velocity.x = speed;
      } else {
         velocity.x = 0;
      }
   }

   private void updatePosition(){
      parent.pos.x += velocity.x * sys.DELTA_TIME;
      parent.pos.y += velocity.y * sys.DELTA_TIME;
   }

   private void updateAngle(){
      float angle = (float)Math.atan2(parent.pos.x - sys.mouse_x,parent.pos.y - sys.mouse_y);
      for(var renderer : renderers)
         renderer.rotation_angle = angle;
   }
}
