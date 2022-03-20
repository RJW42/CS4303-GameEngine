package GameEngine.Components.AIComponents.AIDecisionTree;

import GameEngine.Components.AIComponents.AIDecisionController.AIDecisionNode;

public class DecisionTreeNode {
   // Attributes
   public DecisionTreeNode parent;
   public DecisionTreeNode left_child;
   public DecisionTreeNode right_child;
   public DecisionFunction decider;
   public DecisionNodeLeaf method;

   // Constructor
   public DecisionTreeNode(DecisionFunction decider){
      // Init vars
      this.parent = null;
      this.left_child = null;
      this.right_child = null;
      this.decider = decider;
   }

   public DecisionTreeNode(DecisionNodeLeaf method){
      // Init vars
      this.parent = null;
      this.left_child = null;
      this.right_child = null;
      this.method = method;
   }

   // Methods
   public void update(AIDecisionTree tree){
      // Check if this is a leaf or
      if(decider != null){
         // Determine if go traverse left or right
         if(decider.chooseLeftChild()){
            left_child.update(tree);
         }else{
            right_child.update(tree);
         }
         return;
      }

      // Is a leaf node update method
      method.update(tree);
   }

   public void setChildren(DecisionTreeNode left_child, DecisionTreeNode right_child){
      // Set parents
      left_child.parent = this;
      right_child.parent = this;

      this.left_child = left_child;
      this.right_child = right_child;
   }
}
