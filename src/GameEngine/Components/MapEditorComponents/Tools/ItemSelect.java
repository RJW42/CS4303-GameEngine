package GameEngine.Components.MapEditorComponents.Tools;

import GameEngine.Components.UIComponents.UIButton;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.util.ArrayList;

import static GameEngine.Components.MapEditorComponents.ToolMenu.*;


public class ItemSelect extends Tool {
   // Attributes
   public static final int BUTTON_WIDTH = 100;

   public Item current_item;

   private ArrayList<UIButton> buttons;
   private float button_width;

   // Constructor
   public ItemSelect(GameObject parent) {
      super(parent);

      // Init Attributes
      this.icon = (UIButton button) -> this.render_item(current_item, button);
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


   private void item_selected(Item item){
      this.current_item = item;
      this.active = false;
   }


   private void render_item(Item item, UIButton button){
      // Todo: implement
      sys.uiText(item.name(), button.pos.x, button.pos.y);
   }


   private void create_buttons(){
      // Create a button for each item
      buttons = new ArrayList<>();
      float x = pos.x + BUTTON_WIDTH * GameEngine.UI_SCALE / 2f - ITEM_WIDTH * GameEngine.UI_SCALE / 2f;
      float y = pos.y + ITEM_HEIGHT * GameEngine.UI_SCALE;

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
      return new UIButton(
              parent, () -> item_selected(item), item.name(), new PVector(),
              TEXT_C, BACKGROUND_C, BORDER_C, TEXT_C, BACKGROUND_H_C, BORDER_H_C,
              ITEM_PADDING, ITEM_BORDER_WIDTH, BUTTON_WIDTH, ITEM_HEIGHT, true
      );
   }
}
