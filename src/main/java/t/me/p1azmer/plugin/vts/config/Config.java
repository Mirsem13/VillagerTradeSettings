package t.me.p1azmer.plugin.vts.config;

import t.me.p1azmer.engine.api.config.JOption;

public class Config {
    public static final JOption<String> VTS_EDITOR_GUI_NAME = JOption.create(
      "Settings.Editor.Main.Gui.Name",
      "Villager Trade Settings",
      "Sets main editor gui name");
    public static final JOption<String> VILLAGER_TRADE_EDITOR_GUI_NAME = JOption.create(
      "Settings.Editor.Villager.Trade.Item.Gui.Name",
      "Villager Trade Items Settings",
      "Sets the editor gui name");
    public static final JOption<String> RECIPE_EDITOR_GUI_NAME = JOption.create(
      "Settings.Editor.Recipe.Gui.Name",
      "Recipe Items Settings",
      "Sets the editor gui name");
    public static final JOption<Boolean> VILLAGER_SETTINGS_CLEAR_OTHER = JOption.create(
      "Settings.Villager.Recipe.Clear_Other",
      false,
      "Sets whether non-custom recipes will be removed from the merchant");
    public static final JOption<Integer> VILLAGER_SETTINGS_LIMIT_OF_CUSTOM_RECIPES = JOption.create(
      "Settings.Villager.Recipe.Limit",
      3,
      "Sets a limit on adding custom recipes to the trader",
      "Be careful, because if the chance of items is low and you have a limit,",
      "then a resident may not get more than one custom recipe");
    public static final JOption<Boolean> VILLAGER_SETTINGS_ADD_CUSTOM_ONE_TIME = JOption.create(
      "Settings.Villager.Recipe.One_Time",
      true,
      "Sets whether custom recipes will be added only when the merchant spawns or when it is updated too.");
    public static final JOption<Boolean> SETTINGS_DEMAND = JOption.create(
      "Settings.Demand.Enabled",
      false,
      "Sets whether merchant demand is enabled/disabled",
      "Doc.Info:",
      "Demand: This value is periodically updated by the villager that owns this merchant recipe based on",
      "how often the recipe has been used since it has been last restocked in relation to its maximum uses.",
      "The amount by which the demand influences the amount of the first ingredient is scaled by",
      "the recipe's price multiplier, and can never be below zero.");
    public static final JOption<Boolean> SETTINGS_SPECIAL_PRICE = JOption.create(
      "Settings.Special_Price.Enabled",
      false,
      "Sets whether merchant special price is enabled/disabled",
      "Doc.Info:",
      "Special price: This value is dynamically updated whenever a player starts and stops trading with",
      "a villager that owns this merchant recipe. It is based on the player's individual reputation with the",
      "villager, and the player's currently active status effects (see PotionEffectType.HERO_OF_THE_VILLAGE).",
      "The influence of the player's reputation on the special price is scaled by the recipe's price multiplier.");

    public static final JOption<Boolean> DISABLE_PRICE_MULTIPLIER = JOption.create(
      "Settings.Disable_Price_Multiplier",
      false,
      "Disables the price multiplier for the merchant recipe");
}