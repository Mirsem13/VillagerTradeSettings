package t.me.p1azmer.plugin.vts.listeners;

import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.config.Config;
import t.me.p1azmer.plugin.vts.recipeitems.RecipeItem;
import t.me.p1azmer.plugin.vts.recipeitems.RecipeItemManager;
import t.me.p1azmer.plugin.vts.tradeitem.TradeItem;
import t.me.p1azmer.plugin.vts.tradeitem.TradeItemManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VillagerListener extends AbstractListener<VTSPlugin> {

    private final TradeItemManager tradeItemManager;
    private final RecipeItemManager recipeItemManager;
    private final AutoRemovalCollection<UUID> replacedVillagersCache = AutoRemovalCollection.newHashSet(3, TimeUnit.SECONDS);

    public VillagerListener(@NotNull TradeItemManager tradeItemManager) {
        super(tradeItemManager.plugin());
        this.tradeItemManager = tradeItemManager;
        this.recipeItemManager = this.plugin.getRecipeItemManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVillagerSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof AbstractVillager villager) {
            if (Config.VILLAGER_SETTINGS_CLEAR_OTHER.get()) {
                villager.resetOffers();
            }
            villager.getRecipes().forEach(merchantRecipe -> merchantRecipe.setIgnoreDiscounts(Config.DISABLED_ALL_DISCOUNTS.get()));
            this.addCustomRecipes(villager, false);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onProfessionChange(VillagerCareerChangeEvent event) {
        Villager villager = event.getEntity();
        if (Config.VILLAGER_SETTINGS_CLEAR_OTHER.get()) {
            villager.resetOffers();
        }
        villager.getRecipes().forEach(merchantRecipe -> merchantRecipe.setIgnoreDiscounts(Config.DISABLED_ALL_DISCOUNTS.get()));
        this.addCustomRecipes(villager, false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onVillagerGenerateNewTrade(VillagerAcquireTradeEvent event) {
        AbstractVillager villager = event.getEntity();
        MerchantRecipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        if (!Config.SETTINGS_DEMAND.get()) {
            if (Version.isAbove(Version.V1_18_R2)) recipe.setDemand(0);
        }
        if (!Config.SETTINGS_SPECIAL_PRICE.get()) {
            if (Version.isAbove(Version.V1_18_R2)) recipe.setSpecialPrice(0);
        }
        if (Config.DISABLE_PRICE_MULTIPLIER.get()) recipe.setPriceMultiplier(0);
        if (Config.DISABLED_ALL_DISCOUNTS.get()) recipe.setIgnoreDiscounts(true);
        if (result.getType().isAir() || replacedVillagersCache.contains(villager.getUniqueId())) {
            return;
        }

        TradeItem tradeItem = this.tradeItemManager.getTradeItem(result, villager);
        if (tradeItem == null) return;

        event.setRecipe(this.generateRecipe(tradeItem, recipe));
        replacedVillagersCache.add(villager.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onVillagerRegenerateTrade(VillagerReplenishTradeEvent event) {
        AbstractVillager villager = event.getEntity();
        MerchantRecipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        if (!Config.SETTINGS_DEMAND.get())
            if (Version.isAbove(Version.V1_18_R2)) recipe.setDemand(0);
        if (!Config.SETTINGS_SPECIAL_PRICE.get()) {
            if (Version.isAbove(Version.V1_18_R2)) recipe.setSpecialPrice(0);
        }

        if (Config.DISABLE_PRICE_MULTIPLIER.get()) recipe.setPriceMultiplier(0);

        if (result.getType().isAir() || replacedVillagersCache.contains(villager.getUniqueId())) {
            return;
        }

        TradeItem tradeItem = this.tradeItemManager.getTradeItem(result, villager);
        if (tradeItem == null) return;

        event.setRecipe(this.generateRecipe(tradeItem, recipe));
        replacedVillagersCache.add(villager.getUniqueId());
    }

    @NotNull
    public MerchantRecipe generateRecipe(@NotNull TradeItem tradeItem, @NotNull MerchantRecipe recipe) {
        List<ItemStack> ingredients = new ArrayList<>(recipe.getIngredients());
        List<ItemStack> sellItems = new ArrayList<>(tradeItem.getSellItems());
        List<ItemStack> replaceItems = new ArrayList<>(tradeItem.getReplaceItems());
        List<ItemStack> replacedIngredients = new ArrayList<>();

        for (ItemStack ingredient : ingredients) {
            if (ingredient.getType().isAir()) continue;

            for (int i = 0; i < replaceItems.size(); i++) {
                ItemStack replaceItem = replaceItems.get(i);

                if (ingredient.isSimilar(replaceItem) || ingredient.getType().equals(replaceItem.getType())) {
                    if (i < sellItems.size()) {
                        ItemStack sellItem = sellItems.get(i);
                        ItemStack replaced = sellItem.clone();
                        replaced.setAmount(sellItem.getAmount());
                        replacedIngredients.add(replaced);
                        replacedIngredients.add(ingredient);
                        break;
                    }
                }
            }
        }

        if (replacedIngredients.size() < sellItems.size()) {
            for (int i = replacedIngredients.size(); i < sellItems.size(); i++) {
                replacedIngredients.add(tradeItem.getMissingSellItem(replacedIngredients));
            }
        }

        MerchantRecipe newRecipe = new MerchantRecipe(recipe.getResult(), recipe.getMaxUses());
        newRecipe.setIngredients(replacedIngredients.isEmpty() ? sellItems : replacedIngredients);
        newRecipe.setExperienceReward(recipe.hasExperienceReward());
        newRecipe.setVillagerExperience(recipe.getVillagerExperience());
        newRecipe.setUses(recipe.getUses());
        newRecipe.setMaxUses(recipe.getMaxUses());
        return newRecipe;
    }

    public void addCustomRecipes(@NotNull AbstractVillager villager, boolean checkSettings) {
        List<MerchantRecipe> merchantRecipes = new ArrayList<>(villager.getRecipes());
        if (!checkSettings) {
            int limit = Config.VILLAGER_SETTINGS_LIMIT_OF_CUSTOM_RECIPES.get();
            List<RecipeItem> recipeItems = new ArrayList<>(this.recipeItemManager.getRecipeItems());
            Collections.shuffle(recipeItems);

            for (RecipeItem recipeItem : recipeItems) {
                if (recipeItem.getProfession().isAllowed(villager)
                  && !containsRecipe(recipeItem, merchantRecipes)
                  && Rnd.chance(recipeItem.getChance())) {
                    if (limit-- <= -1) break;

                    MerchantRecipe merchantRecipe = getMerchantByRecipeItem(recipeItem);
                    merchantRecipes.add(merchantRecipe);
                }
            }
            villager.setRecipes(merchantRecipes);
        }
    }

    @NotNull
    private MerchantRecipe getMerchantByRecipeItem(@NotNull RecipeItem recipeItem) {
        MerchantRecipe merchantRecipe = new MerchantRecipe(recipeItem.getProduct(), recipeItem.getMaxUses());
        merchantRecipe.setIngredients(recipeItem.getSellItems());
        merchantRecipe.setExperienceReward(recipeItem.getExpReward() > 0);
        merchantRecipe.setVillagerExperience(recipeItem.getExpReward());
        merchantRecipe.setMaxUses(recipeItem.getMaxUses());
        merchantRecipe.setIgnoreDiscounts(recipeItem.isDiscounts());
        return merchantRecipe;
    }

    private boolean containsRecipe(@NotNull RecipeItem recipeItem, @NotNull List<MerchantRecipe> recipes) {
        return recipes.stream().anyMatch(merchantRecipe -> {
            ItemStack product = recipeItem.getProduct();
            return merchantRecipe.getResult().isSimilar(product);
        });
    }
}