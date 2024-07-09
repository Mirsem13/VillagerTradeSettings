package t.me.p1azmer.plugin.vts.editor;

import t.me.p1azmer.engine.api.editor.EditorLocale;
import t.me.p1azmer.plugin.vts.Placeholders;

import static t.me.p1azmer.engine.utils.Colors2.*;

public class EditorLocales extends t.me.p1azmer.engine.api.editor.EditorLocales {

    private static final String PREFIX = "Editor.";

    public static final EditorLocale VILLAGER_EDITOR = builder(PREFIX + "Villager")
      .name("Villager")
      .text("Setup villagers trade items here")
      .build();

    public static final EditorLocale RECIPE_EDITOR = builder(PREFIX + "Recipe")
      .name("Recipe")
      .text("Setup recipe trade items here")
      .build();

    // trade item
    public static final EditorLocale TRADE_PRODUCT_CREATE = builder(PREFIX + "Villager.Trade.Product.Create")
      .name("New Product")
      .build();

    public static final EditorLocale TRADE_ITEM_OBJECT = builder(PREFIX + "Villager.Trade.Item.Object")
      .name(WHITE + Placeholders.TRADE_ITEM_ID)
      .emptyLine()
      .click(LMB, "Configure")
      .click(SHIFT_RMB, "Delete " + RED + "(No Undo)")
      .build();

    public static final EditorLocale TRADE_PRODUCT = builder(PREFIX + "Villager.Trade.Product.Object")
      .name("Product")
      .emptyLine()
      .text("Sets the product for which we", "will change the purchase items")
      .emptyLine()
      .click(DRAG_DROP, "Replace Item")
      .click(RMB, "Get a Copy")
      .build();

    public static final EditorLocale TRADE_SELL_ITEMS_OBJECT = builder(PREFIX + "Villager.Trade.Sell_Items.Object")
      .name("Sell Items")
      .text("Sets the items that will be used", "as a way to purchase an item from " + LIGHT_YELLOW + "Product")
      .emptyLine()
      .click(LMB, "Configure")
      .click(SHIFT_RMB, "Clear")
      .build();
    public static final EditorLocale TRADE_REPLACED_ITEMS_OBJECT = builder(PREFIX + "Villager.Trade.Replaced_Items.Object")
      .name("Replaced Items")
      .text(WHITE + "Sets the items to be replaced with Sell items",
        "",
        RED + "If this list is empty" + GRAY + ",",
        GRAY + "then all items will be replaced",
        GRAY + "with items from Sell Items")
      .emptyLine()
      .click(LMB, "Configure")
      .click(SHIFT_RMB, "Clear")
      .build();

    // recipe
    public static final EditorLocale RECIPE_OBJECT = builder(PREFIX + "Recipe.Object")
      .name(WHITE + Placeholders.RECIPE_ID)
      .emptyLine()
      .click(LMB, "Configure")
      .click(SHIFT_RMB, "Delete " + RED + "(No Undo)")
      .build();

    public static final EditorLocale RECIPE_PRODUCT_CREATE = builder(PREFIX + "Recipe.Product.Create")
      .name("New Product")
      .build();

    public static final EditorLocale RECIPE_CHANCE = builder(PREFIX + "Recipe.Chance")
      .name("Chance")
      .text("Sets the chance with which this", "item will be added to the trader")
      .emptyLine()
      .currentHeader()
      .current("Value", Placeholders.RECIPE_CHANCE + GRAY + " (" + WHITE + LMB + GRAY + ")")
      .build();

    public static final EditorLocale RECIPE_DISCOUNT = builder(PREFIX + "Recipe.Discount")
      .name("Discount")
      .text(
        "Sets whether there will be",
        "discounts on this recipe"
      )
      .emptyLine()
      .currentHeader()
      .current("Value", Placeholders.RECIPE_DISCOUNT + GRAY + " (" + WHITE + LMB + GRAY + ")")
      .build();

    public static final EditorLocale RECIPE_MAX_USES = builder(PREFIX + "Recipe.Max_Uses")
      .name("Max Uses")
      .text("Sets how many uses are", "available for this recipe")
      .emptyLine()
      .currentHeader()
      .current("Value", Placeholders.RECIPE_MAX_USES + GRAY + " (" + WHITE + LMB + GRAY + ")")
      .build();
    public static final EditorLocale RECIPE_EXP_REWARD = builder(PREFIX + "Recipe.Exp_Reward")
      .name("Experience Reward")
      .text("Sets the amount of experience the", "villager earns from this recipe")
      .emptyLine()
      .currentHeader()
      .current("Value", Placeholders.RECIPE_EXP_REWARD + GRAY + " (" + WHITE + LMB + GRAY + ")")
      .build();
    public static final EditorLocale RECIPE_PROFESSION = builder(PREFIX + "Recipe.Profession")
      .name("Villager Profession")
      .text("Sets the profession to which", "this product will be added")
      .emptyLine()
      .currentHeader()
      .current("Profession", Placeholders.RECIPE_PROFESSION + GRAY + " (" + WHITE + LMB + GRAY + ")")
      .build();
    public static final EditorLocale RECIPE_PRODUCT = builder(PREFIX + "Recipe.Product.Object")
      .name("Product")
      .emptyLine()
      .text("Sets the item to be added as", "a product to the trader")
      .emptyLine()
      .click(DRAG_DROP, "Replace Item")
      .click(RMB, "Get a Copy")
      .build();

    public static final EditorLocale RECIPE_SELL_ITEMS = builder(PREFIX + "Recipe.Sell_Items.Object")
      .name("Sell Items")
      .text("Sets the items that will be used", "as a way to purchase an item from " + LIGHT_YELLOW + "Product")
      .emptyLine()
      .click(LMB, "Configure")
      .click(SHIFT_RMB, "Clear")
      .build();

    // other
    public static final EditorLocale ITEMS_OBJECT = builder(PREFIX + "Items.Object")
      .emptyLine()
      .click(SHIFT_RMB, RED + "Remove")
      .build();

    public static final EditorLocale CREATE_ITEM = builder(PREFIX + "Items.Create")
      .name(GREEN + "New Item" + GRAY + " (" + WHITE + DRAG_DROP + GRAY + ")")
      .build();
}