package GameEngine.Components.AIComponents.AIDecisionController;

import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;

import java.util.ArrayList;

public class AIDecisionController extends Component {
   // Attributes
   public ArrayList<AIDecisionNode> decision_nodes;
   public AIDecisionNode current_node;

   // Constructor
   public AIDecisionController(GameObject parent, ArrayList<AIDecisionNode> decision_nodes, AIDecisionNode start_node) {
      super(parent);
      this.decision_nodes = decision_nodes;
      this.current_node = start_node;
   }

   // Methods
   @Override
   public void start() {
      // Init all the decision nodes
      decision_nodes.forEach(node -> node.start(this));
      current_node.enter();
   }

   @Override
   public void update() {
      // Update the current node and check if it can advance
      current_node.update();
      current_node.advance().ifPresent(new_current -> {
         current_node.exit();
         current_node = decision_nodes.get(new_current);
         current_node.enter();
      });
   }
}
