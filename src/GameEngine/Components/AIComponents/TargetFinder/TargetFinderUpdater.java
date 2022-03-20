package GameEngine.Components.AIComponents.TargetFinder;

import GameEngine.GameObjects.GameObject;

import java.util.ArrayList;

public class TargetFinderUpdater implements TargetUpdater{
   // Attributes
   Class[] types;

   // Constructor
   public TargetFinderUpdater(Class... types){
      this.types = types;
   }

   // Methods
   @Override
   public ArrayList<GameObject> getTargets(ArrayList<GameObject> current_targets, TargetFinder target_finder) {
      ArrayList<GameObject> out = new ArrayList<>();
      for(var t : types){
         out.addAll(target_finder.sys.getGameObjects(t));
      }
      return out;
   }
}
