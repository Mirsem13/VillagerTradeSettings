package t.me.p1azmer.plugin.vts.recipeitems;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.vts.VTSPlugin;

import java.util.*;

public class RecipeItemManager extends AbstractManager<VTSPlugin> {
    public static final String DIR_ITEMS = "/recipe_items/";
    private Map<String, RecipeItem> recipeItemMap;

    public RecipeItemManager(@NotNull VTSPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.recipeItemMap = new HashMap<>();

        this.plugin.getConfigManager().extractResources(DIR_ITEMS);
        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + DIR_ITEMS, false)) {
            RecipeItem recipeItem = new RecipeItem(plugin, cfg);
            if (recipeItem.load()) {
                this.recipeItemMap.put(recipeItem.getId().toLowerCase(), recipeItem);
            } else this.plugin.warn("Recipe Item not loaded: '" + cfg.getFile().getName() + "'!");
        }
        plugin.info("Recipe Items Loaded: " + recipeItemMap.size());
    }

    @Override
    protected void onShutdown() {
        this.recipeItemMap.forEach((s, tradeItem) -> tradeItem.clear());
        this.recipeItemMap.clear();
    }

    public boolean create(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getRecipeItemsById(id) != null) return false;

        JYML cfg = new JYML(this.plugin.getDataFolder() + DIR_ITEMS, id + ".yml");
        RecipeItem recipeItem = new RecipeItem(plugin, cfg);

        recipeItem.save();
        recipeItem.load();
        this.getRecipeItemsMap().put(recipeItem.getId(), recipeItem);
        return true;
    }

    public void delete(@NotNull RecipeItem recipeItem) {
        if (recipeItem.getFile().delete()) {
            recipeItem.clear();
            this.getRecipeItemsMap().remove(recipeItem.getId());
        }
    }

    @NotNull
    public List<String> getRecipeItemsIds() {
        return new ArrayList<>(this.recipeItemMap.keySet());
    }

    @NotNull
    public Map<String, RecipeItem> getRecipeItemsMap() {
        return this.recipeItemMap;
    }

    public void setRecipeItemMap(@NotNull Map<String, RecipeItem> recipeItemMap) {
        this.recipeItemMap = recipeItemMap;
    }

    @NotNull
    public Collection<RecipeItem> getRecipeItems() {
        return this.getRecipeItemsMap().values();
    }

    @Nullable
    public RecipeItem getRecipeItemsById(@NotNull String id) {
        return this.recipeItemMap.get(id.toLowerCase());
    }
}