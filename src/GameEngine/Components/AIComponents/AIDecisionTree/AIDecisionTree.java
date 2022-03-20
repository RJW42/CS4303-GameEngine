package GameEngine.Components.AIComponents.AIDecisionTree;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;


public class AIDecisionTree extends Component {
   // Attributes
   public DecisionTreeNode tree;

   // Constructor
   public AIDecisionTree(GameObject parent, DecisionTreeNode tree) {
      super(parent);

      this.tree = tree;
   }


   // Methods 
   public void start() {
   }

   public void update() {
      tree.update(this);
   }

   public void draw() {
   }
}
