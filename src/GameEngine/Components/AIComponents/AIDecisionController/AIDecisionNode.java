package GameEngine.Components.AIComponents.AIDecisionController;

import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;

import java.util.Optional;

public abstract class AIDecisionNode {
   // Attributes
   public GameEngine sys;
   public AIDecisionController controller;

   // Constructor
   AIDecisionNode(GameEngine sys){
      this.sys = sys;
   }

   // Methods
   public void start(AIDecisionController controller) {
      // Set the controller for this decision node
      this.controller = controller;
   }
   public abstract void enter(); // Called this node is set as the starting node
   public abstract void update();
   public abstract void exit(); // Called if this node is exited from
   public abstract Optional<Integer> advance(); // Returns an index into a list of the AIDecisionNode that should become the current state
}
