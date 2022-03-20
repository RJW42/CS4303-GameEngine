package GameEngine.Components.AIComponents;


import GameEngine.Components.AIComponents.AIDecisionController.AIDecisionController;
import GameEngine.Components.AIComponents.AIDecisionController.AIDecisionNode;
import GameEngine.Components.Component;
import GameEngine.Components.Renderers.ImageRenderer;
import GameEngine.GameObjects.GameObject;
import processing.core.PImage;


public class HumanSpriteUpdater extends Component {
   // Attributes
   public String sad_sprite_name;

   private ImageRenderer renderer;
   private AIDecisionController controller;
   private PImage happy_sprite;
   private PImage sad_sprite;
   private int sad_node;
   private AIDecisionNode sad_node_ref;

   // Constructor
   public HumanSpriteUpdater(GameObject parent, String sad_sprite, int sad_node) {
      super(parent);
      this.sad_sprite_name = sad_sprite;
      this.sad_node = sad_node;
   }


   // Methods 
   public void start() {
      // Get renderer and controller
      this.renderer = parent.getComponent(ImageRenderer.class);
      this.controller = parent.getComponent(AIDecisionController.class);

      // Load sprites
      this.happy_sprite = renderer.image;
      this.sad_sprite = sys.sprite_manager.get_sprite(sad_sprite_name, renderer.width_pixels, renderer.height_pixels);
   }

   public void update() {
      // Check we have borth sprites
      if(happy_sprite == null) {
         happy_sprite = renderer.image;
         return;
      }

      // Update the renderer image
      if(sad_node_ref == null){
         if(controller.decision_nodes == null || controller.decision_nodes.size() <= sad_node){
            return;
         }
         sad_node_ref = controller.decision_nodes.get(sad_node);
      }

      // Check what node to set
      if(sad_node_ref == controller.current_node){
         this.renderer.image = sad_sprite;
      }else{
         this.renderer.image = happy_sprite;
      }
   }

   public void draw() {
   }
}
