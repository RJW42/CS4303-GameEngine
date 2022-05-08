package GameEngine.Components;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.PGif;
import processing.core.PImage;
import processing.core.PVector;


public class MovingSpriteManager extends Component {
   // Attributes
   public ForceManager force_manager;
   public PImage left;
   public PImage right;
   public PGif left_m;
   public PGif right_m;
   public PVector offset;
   public int width_pixels, height_pixels;
   public int width_pixels_m, height_pixels_m;
   public float width, height;
   public float m_width, m_height;
   public int damage_tint;

   private final String l_name;
   private final String r_name;
   private final String l_name_moving;
   private final String r_name_moving;
   private final boolean has_moving;
   private PImage current;
   private PGif current_gif;
   private PVector m_offset;
   private boolean is_moving;

   // Constructor
   public MovingSpriteManager(GameObject parent, PVector offset, String l_name, String r_name, float width, float height) {
      this(parent, offset, l_name, r_name, null, null, width, height, width, height, null);
   }

   public MovingSpriteManager(GameObject parent, PVector offset, String l_name, String r_name, String l_name_moving, String r_name_moving, float width, float height, float m_width, float m_height, PVector m_offset) {
      super(parent);

      // Init attributes
      this.l_name = l_name;
      this.r_name = r_name;
      this.l_name_moving = l_name_moving;
      this.r_name_moving = r_name_moving;
      this.offset = offset;
      this.width_pixels = Math.round(GameEngine.PIXEL_TO_METER * width);
      this.height_pixels = Math.round(GameEngine.PIXEL_TO_METER * height);
      this.width_pixels_m = Math.round(GameEngine.PIXEL_TO_METER * m_width);
      this.height_pixels_m = Math.round(GameEngine.PIXEL_TO_METER * m_height);
      this.width = width;
      this.height = height;
      this.m_height = m_height;
      this.m_width = m_width;
      this.has_moving = l_name_moving != null;
      this.m_offset = m_offset;
      this.is_moving = true;
   }


   // Methods 
   public void start() {
      force_manager = parent.getComponent(ForceManager.class);
      left = sys.sprite_manager.get_sprite(l_name, width_pixels, height_pixels);
      right = sys.sprite_manager.get_sprite(r_name, width_pixels, height_pixels);
      if(has_moving) {
         left_m = sys.sprite_manager.get_gif(l_name_moving, width_pixels_m, height_pixels_m);
         right_m = sys.sprite_manager.get_gif(r_name_moving, width_pixels_m, height_pixels_m);
         current_gif = left_m;
      }
      current = left;
   }

   public void update() {
      // Update current sprite
      if(!has_moving) {
         if (force_manager.velocity.x > 0 && current == left) {
            current = right;
         } else if (force_manager.velocity.x < 0 && current == right) {
            current = left;
         }
      } else {
         if (force_manager.grounded && force_manager.velocity.x > 0.1f && (current_gif == left_m || !is_moving)) {
            current_gif = right_m;
            is_moving = true;
         } else if (force_manager.grounded && force_manager.velocity.x < -0.1f && (current_gif == right_m || !is_moving)) {
            current_gif = left_m;
            is_moving = true;
         } else if(((force_manager.velocity.x <= 0.1f && force_manager.velocity.x >= -0.1f) || (!force_manager.grounded)) && is_moving) {
            is_moving = false;
            if(current_gif == left_m) {
               current = left;
            } else {
               current = right;
            }
         }
      }
   }

   public void draw() {
      // Get draw position
      PVector pos = PVector.add(offset, parent.pos);

      // Draw sprite
      sys.push();
      if(!has_moving){
         // No animations
         sys.image(current, pos.x, pos.y, 0);
         if (damage_tint > 0) {
            sys.tint(255, 0, 0, damage_tint);
            sys.image(current, pos.x, pos.y, 0);
         }
      } else {
         // Has animations
         if(is_moving) current_gif.play(sys, PVector.add(m_offset, parent.pos), 0, damage_tint);
         else sys.image(current, pos.x, pos.y, 0);
         if (damage_tint > 0 && !is_moving) {
            sys.tint(255, 0, 0, damage_tint);
            sys.image(current, pos.x, pos.y, 0);
         }
      }
      sys.pop();
   }
}
