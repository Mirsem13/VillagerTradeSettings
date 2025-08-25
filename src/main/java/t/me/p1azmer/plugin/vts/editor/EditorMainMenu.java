package t.me.p1azmer.plugin.vts.editor;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.config.Config;
import t.me.p1azmer.plugin.vts.recipeitems.editor.RecipeItemEditorList;
import t.me.p1azmer.plugin.vts.tradeitem.editor.TradeItemEditorList;

public class EditorMainMenu extends EditorMenu<VTSPlugin, VTSPlugin> {
    private static final String TEXTURE_VILLAGER = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhOGVmMjQ1OGEyYjEwMjYwYjg3NTY1NThmNzY3OWJjYjdlZjY5MWQ0MWY1MzRlZmVhMmJhNzUxMDczMTVjYyJ9fX0=";

    private TradeItemEditorList tradeItemEditorList;
    private RecipeItemEditorList recipeItemEditorList;

    public EditorMainMenu(@NotNull VTSPlugin plugin) {
        super(plugin, plugin, Config.VTS_EDITOR_GUI_NAME.get(), 27);

        this.addExit(22);

        this.addItem(ItemUtil.createCustomHead(TEXTURE_VILLAGER), EditorLocales.VILLAGER_EDITOR, 11)
            .setClick((viewer, event) -> this.getTradeItemEditor().openAsync(viewer.getPlayer(), 1));
        this.addItem(Material.WRITTEN_BOOK, EditorLocales.RECIPE_EDITOR, 15)
            .setClick((viewer, event) -> this.getRecipeItemEditorList().openAsync(viewer.getPlayer(), 1));
    }

    @Override
    public void clear() {
        if (this.tradeItemEditorList != null) {
            this.tradeItemEditorList.clear();
            this.tradeItemEditorList = null;
        }
        super.clear();
    }

    @NotNull
    public TradeItemEditorList getTradeItemEditor() {
        if (this.tradeItemEditorList == null) {
            this.tradeItemEditorList = new TradeItemEditorList(this.plugin.getTradeItemManager());
        }
        return this.tradeItemEditorList;
    }

    @NotNull
    public RecipeItemEditorList getRecipeItemEditorList() {
        if (this.recipeItemEditorList == null) {
            this.recipeItemEditorList = new RecipeItemEditorList(this.plugin.getRecipeItemManager());
        }
        return this.recipeItemEditorList;
    }
}