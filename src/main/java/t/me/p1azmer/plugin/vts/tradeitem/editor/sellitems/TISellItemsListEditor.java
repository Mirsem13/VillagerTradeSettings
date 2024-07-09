package t.me.p1azmer.plugin.vts.tradeitem.editor.sellitems;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.config.Config;
import t.me.p1azmer.plugin.vts.editor.EditorLocales;
import t.me.p1azmer.plugin.vts.lang.Lang;
import t.me.p1azmer.plugin.vts.tradeitem.TradeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TISellItemsListEditor extends EditorMenu<VTSPlugin, TradeItem> implements AutoPaged<ItemStack> {

    public TISellItemsListEditor(@NotNull TradeItem tradeItem) {
        super(tradeItem.plugin(), tradeItem, Config.VILLAGER_TRADE_EDITOR_GUI_NAME.get(), 5);
        this.getOptions().setType(InventoryType.BREWING);

        this.addReturn(3).setClick((viewer, event) -> {
            tradeItem.getEditor().openNextTick(viewer, 4);
        });

        this.addCreation(EditorLocales.CREATE_ITEM, 2).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                if (tradeItem.getSellItems().size() >= 2) {
                    plugin.getMessage(Lang.Editor_Error_Items_Limit).send(viewer.getPlayer());
                    return;
                }
                if (!tradeItem.isAllowedToAdd(cursor)) {
                    plugin().getMessage(Lang.Editor_Error_Items_Already_Has).send(viewer.getPlayer());
                    return;
                }
                tradeItem.getSellItems().add(cursor);
                PlayerUtil.addItem(viewer.getPlayer(), cursor);
                event.getView().setCursor(null);
                this.save(viewer);
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 2).toArray();
    }

    @Override
    @NotNull
    public List<ItemStack> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getSellItems());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ItemStack itemStack) {
        ItemStack item = new ItemStack(itemStack);
        List<String> lore = ItemUtil.getLore(item);
        lore.addAll(EditorLocales.ITEMS_OBJECT.getLocalizedLore());
        ItemUtil.editMeta(item, meta -> meta.setLore(lore));
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ItemStack reward) {
        return (viewer, event) -> {
            if (event.isRightClick() && event.isShiftClick()) {
                this.object.getSellItems().remove(reward);
                this.save(viewer);
                return;
            }

            if (event.isLeftClick()) {
                this.openNextTick(viewer, 1);
            }
        };
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}