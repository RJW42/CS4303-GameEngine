package GameEngine.Components.AIComponents.TargetFinder;

import GameEngine.GameObjects.GameObject;

import java.util.ArrayList;

public interface TargetUpdater {
   public ArrayList<GameObject> getTargets(ArrayList<GameObject> current_targets, TargetFinder target_finder);
}
