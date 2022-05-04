package GameEngine.Components.AIComponents;


import GameEngine.Components.Component;
import GameEngine.Components.Damagable;
import GameEngine.Components.ForceManager;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class Attacker extends Component {
   // Attributes
   private ForceManager player_force_manager;
   private Damagable player_damagable;

   public final float punch_force;
   public final float punch_damage;
   private final float time_per_punch;
   private float time_since_last_punch;
   private final PVector offset;

   // Constructor
   public Attacker(GameObject parent, PVector offset, int punch_rate, float punch_force, float punch_damage) {
      super(parent);

      // Init attributes
      this.time_per_punch = 1f / punch_rate;
      this.offset = offset;
      this.punch_force = punch_force;
      this.punch_damage = punch_damage;
      this.time_since_last_punch = 0;
   }


   // Methods 
   public void start() {}

   public void update() {
      this.time_since_last_punch += sys.DELTA_TIME;
   }

   public void draw() {
      // Todo: could do a punch animation
   }


   public void punch(Player player){
      // Check if have force manager
      if(player_force_manager == null){
         player_force_manager = player.getComponent(ForceManager.class);
         player_damagable = player.getComponent(Damagable.class);
      }

      // Check if cool down reached
      if(time_since_last_punch < time_per_punch){
         // cannot punch
         return;
      }
      time_since_last_punch = 0;

      // Punch the player
      PVector pos = PVector.add(offset, parent.pos);
      PVector player_pos = new PVector(Player.COLLISION_WIDTH / 2f, -Player.COLLISION_HEIGHT / 2f).add(player.pos);

      PVector vel = player_pos.sub(pos).normalize().mult(punch_force);

      // Apply punch force
      player_force_manager.applyForce(vel);

      // Damage the player
      player_damagable.punch(punch_damage);
   }
}
