package t.me.p1azmer.plugin.vts.recipeitems.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.config.Config;
import t.me.p1azmer.plugin.vts.editor.EditorLocales;
import t.me.p1azmer.plugin.vts.lang.Lang;
import t.me.p1azmer.plugin.vts.recipeitems.RecipeItem;
import t.me.p1azmer.plugin.vts.recipeitems.RecipeItemManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class RecipeItemEditorList extends EditorMenu<VTSPlugin, RecipeItemManager> implements AutoPaged<RecipeItem> {

    public RecipeItemEditorList(@NotNull RecipeItemManager manager) {
        super(manager.plugin(), manager, Config.RECIPE_EDITOR_GUI_NAME.get(), 45);

        this.addReturn(39).setClick((viewer, event) -> {
            this.plugin.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.RECIPE_PRODUCT_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.Editor_Recipe_Enter_Create, wrapper -> {
                if (!this.object.create(wrapper.getTextRaw())) {
                    EditorManager.error(viewer.getPlayer(), this.plugin.getMessage(Lang.Editor_Recipe_Error_Exist).getLocalized());
                    return false;
                }
                return true;
            });
        });
    }

    private void save(@NotNull MenuViewer viewer, @Nullable RecipeItem recipeItem) {
        if (recipeItem != null)
            recipeItem.save();
        this.openNextTick(viewer.getPlayer(), viewer.getPage());
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<RecipeItem> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getRecipeItems().stream().sorted(Comparator.comparing(RecipeItem::getId)).toList());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull RecipeItem tradeItem) {
        ItemStack item = tradeItem.getProduct();
        ItemUtil.editMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.RECIPE_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.RECIPE_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemReplacer.replace(meta, tradeItem.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull RecipeItem tradeItem) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.delete(tradeItem);
                this.save(viewer, null);
                return;
            }

            if (event.isLeftClick()) {
                tradeItem.getEditor().openNextTick(viewer, 1);
            }
        };
    }
}