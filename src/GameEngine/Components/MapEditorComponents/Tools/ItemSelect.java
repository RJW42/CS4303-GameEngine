package GameEngine.Components.MapEditorComponents.Tools;

import GameEngine.Components.UIComponents.UIButton;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;

import static GameEngine.Components.MapEditorComponents.ToolMenu.*;


public class ItemSelect extends Tool {
   // Attributes
   public static final int BUTTON_WIDTH = 100;
   public static final int BUTTON_HEIGHT = 20;

   public Item current_item;

   private ArrayList<UIButton> buttons;
   private UIButton active_button;

   // Constructor
   public ItemSelect(GameObject parent) {
      super(parent);

      // Init Attributes
      this.icon_text = "Select";
      this.current_item = Item.WALL;
   }


   // Methods 
   public void start() {
      create_buttons();
   }

   public void update() {
      if(active) buttons.forEach(UIButton::update);
   }

   public void draw() {
      if(active) buttons.forEach(UIButton::draw);
   }


   private void item_selected(Item item, UIButton button){

      current_item = item;
      icon_text = item.item_name;
      active = false;
      active_button.rect_colour = BACKGROUND_C;
      active_button = button;
      active_button.rect_colour = ACTIVE_C;
   }

   private void create_buttons(){
      // Create a button for each item
      buttons = new ArrayList<>();
      float x = pos.x + BUTTON_WIDTH * GameEngine.UI_SCALE / 2f - ITEM_WIDTH * GameEngine.UI_SCALE / 2f;
      float y = pos.y + BUTTON_HEIGHT * GameEngine.UI_SCALE;

      for(Item item : Item.values()){
         // Create button and set position
         UIButton button = create_button(item);
         buttons.add(button);

         button.pos.y = y;
         button.pos.x = x;

         y += button.height + 2;

         button.start();
      }
   }


   private UIButton create_button(Item item){
      UIButton button = new UIButton(
              parent,null, item.item_name, new PVector(),
              TEXT_C, BACKGROUND_C, BORDER_C, TEXT_C, BACKGROUND_H_C, BORDER_H_C,
              ITEM_PADDING, ITEM_BORDER_WIDTH, BUTTON_WIDTH, BUTTON_HEIGHT, true
      );

      button.callback = () -> this.item_selected(item, button);

      if(item == Item.WALL) {
         active_button = button;
         active_button.rect_colour = ACTIVE_C;
      }


      return button;
   }
}
