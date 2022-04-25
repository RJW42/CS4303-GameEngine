package GameEngine.Components;


import GameEngine.Components.Component;
import GameEngine.Components.UIComponents.UITextRenderer;
import GameEngine.GameObjects.GameObject;


public class WarningTimer extends Component {
   // Attributes
   public static final float WARNING_TIME = 4f;

   private UITextRenderer renderer;
   private float warning_time;

   // Constructor
   public WarningTimer(GameObject parent) {
      super(parent);

      // Init attributes
      warning_time = 0;
   }


   // Methods 
   public void start() {
      renderer = parent.getComponent(UITextRenderer.class);
   }

   public void update() {
      if(warning_time <= 0)
         return;

      warning_time -= sys.DELTA_TIME;

      if(warning_time > 0) {
         renderer.text_alpha = (int)((255) * (warning_time / WARNING_TIME));
         return;
      }

      // Remove warning
      renderer.text = "";
      renderer.text_alpha = 255;
   }

   public void draw() {}

   public void display_warning(String string){
      renderer.text = string;
      renderer.reset_text_size();
      renderer.text_alpha = 255;
      warning_time = WARNING_TIME;
   }
}
