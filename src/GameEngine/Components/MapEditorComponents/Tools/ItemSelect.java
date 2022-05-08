package GameEngine.Components.MapEditorComponents.Tools;

import GameEngine.Components.MapEditorComponents.ToolMenu;
import GameEngine.Components.UIComponents.UIButton;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static GameEngine.Components.MapEditorComponents.ToolMenu.*;
import static GameEngine.GameEngine.UI_SCALE;


public class ItemSelect extends Tool {
   // Attributes
   public static final int MAX_ITEMS_ON_LIST = 8;
   public static final int BUTTON_WIDTH = 100;
   public static final int BUTTON_HEIGHT = 20;

   public Item current_item;

   private UIButton[] buttons;
   private UIButton active_button;
   private UIButton up_button;
   private UIButton down_button;
   private int root_position = 0;
   private int active_index = 0;

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
      if(active) {
         Arrays.stream(buttons).forEach(UIButton::update);
         up_button.update();
         down_button.update();
      }
   }

   public void draw() {
      if(active) {
         Arrays.stream(buttons).forEach(UIButton::draw);
         up_button.draw();
         down_button.draw();
      }
   }


   private void item_selected(int id, UIButton button){
      Item item = Item.values()[id + root_position];
      current_item = item;
      icon_text = item.item_name;
      active = false;
      if(active_index >= root_position && active_index < root_position + buttons.length) {
         buttons[active_index - root_position].rect_colour = BACKGROUND_C;
      }
      active_index = id + root_position;
      buttons[id].rect_colour = ACTIVE_C;
   }


   private void create_buttons(){
      // Create a button for each item
      buttons = new UIButton[Math.min(Item.values().length, MAX_ITEMS_ON_LIST)];
      float x = pos.x + BUTTON_WIDTH * GameEngine.UI_SCALE / 2f - ITEM_WIDTH * GameEngine.UI_SCALE * 1.5f - 0.5f * UI_SCALE;
      float y = pos.y + ITEM_HEIGHT * GameEngine.UI_SCALE;

      if(Item.values().length > MAX_ITEMS_ON_LIST) {
         y = add_up_and_down_buttons(y);
         up_button.pos.x = x - up_button.width / 2f;
         down_button.pos.x = x + up_button.width / 2f + 2;
      }

      for(int i = 0; i < buttons.length; i++){
         // Create button and set position
         Item item = Item.values()[i];
         UIButton button = create_button(item, i);
         buttons[i] = button;

         button.pos.y = y;
         button.pos.x = x;

         y += button.height + 2;

         button.start();
      }
   }


   private UIButton create_button(Item item, int id){
      UIButton button = new UIButton(
              parent,null, item.item_name, new PVector(),
              TEXT_C, BACKGROUND_C, BORDER_C, TEXT_H_C, BACKGROUND_H_C, BORDER_H_C,
              ITEM_PADDING, ITEM_BORDER_WIDTH, BUTTON_WIDTH, BUTTON_HEIGHT, true
      );

      button.callback = () -> this.item_selected(id, button);

      if(item == Item.WALL) {
         active_button = button;
         active_button.rect_colour = ACTIVE_C;
      }


      return button;
   }


   private void up_pressed(){
      // Move all files up by one
      if(root_position >= Item.values().length - buttons.length)
         return; // Can't do that as already at top

      if(root_position == 0) {
         down_button.text_colour = TEXT_C;
         down_button.hover_text_colour = TEXT_H_C;
      }

      if(active_index >= root_position && active_index < root_position + buttons.length) {
         buttons[active_index - root_position].rect_colour = BACKGROUND_C;
      }

      root_position += 1;

      if(active_index >= root_position && active_index < root_position + buttons.length) {
         buttons[active_index - root_position].rect_colour = ACTIVE_C;
      }

      for(int i = 0; i < buttons.length; i++){
         buttons[i].text = Item.values()[i + root_position].item_name;
         buttons[i].reset_text_size();
      }

      if(root_position >= Item.values().length - buttons.length) {
         up_button.text_colour = new PVector(127, 127, 127);
         up_button.hover_text_colour = new PVector(127, 127, 127);
      }
   }


   private void down_pressed(){
      // Move all files up by one
      if(root_position == 0)
         return; // Can't do that as already at top

      if(root_position >= Item.values().length - buttons.length) {
         up_button.text_colour = TEXT_C;
         up_button.hover_text_colour = TEXT_H_C;
      }

      if(active_index >= root_position && active_index < root_position + buttons.length) {
         buttons[active_index - root_position].rect_colour = BACKGROUND_C;
      }

      root_position -= 1;

      if(active_index >= root_position && active_index < root_position + buttons.length) {
         buttons[active_index - root_position].rect_colour = ACTIVE_C;
      }

      for(int i = 0; i < buttons.length; i++){
         buttons[i].text = Item.values()[i + root_position].item_name;
         buttons[i].reset_text_size();
      }

      if(root_position == 0) {
         down_button.text_colour = new PVector(127, 127, 127);
         down_button.hover_text_colour = new PVector(127, 127, 127);
      }
   }


   private float add_up_and_down_buttons(float button_y){
      // Create two buttons for moving up and down
      up_button = new UIButton(
              parent, this::up_pressed, "↑",
              new PVector(pos.x, button_y),
              TEXT_C, BACKGROUND_C, BORDER_C, TEXT_H_C, BACKGROUND_H_C, BORDER_H_C,
              ITEM_PADDING, ITEM_BORDER_WIDTH, BUTTON_WIDTH / 2f, BUTTON_HEIGHT, true
      );

      down_button = new UIButton(
              parent, this::down_pressed, "↓",
              new PVector(pos.x, button_y),
              TEXT_C, BACKGROUND_C, BORDER_C, TEXT_H_C, BACKGROUND_H_C, BORDER_H_C,
              ITEM_PADDING, ITEM_BORDER_WIDTH, BUTTON_WIDTH / 2f, BUTTON_HEIGHT, true
      );

      down_button.text_colour = new PVector(127, 127, 127);

      up_button.pos.x -= up_button.width / 2 + 0.5f * UI_SCALE;
      down_button.pos.x += down_button.width / 2 + 0.5f * UI_SCALE;

      return button_y + down_button.height + 2;
   }
}
