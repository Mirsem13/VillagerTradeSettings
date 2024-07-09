package t.me.p1azmer.plugin.vts.tradeitem.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.NexEngine;
import t.me.p1azmer.engine.api.manager.EventListener;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.utils.*;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.config.Config;
import t.me.p1azmer.plugin.vts.editor.EditorLocales;
import t.me.p1azmer.plugin.vts.tradeitem.TradeItem;
import t.me.p1azmer.plugin.vts.tradeitem.editor.replaceditems.ReplacedItemsListEditor;
import t.me.p1azmer.plugin.vts.tradeitem.editor.sellitems.TISellItemsListEditor;

import java.util.ArrayList;
import java.util.List;

public class TradeItemEditorMain extends EditorMenu<VTSPlugin, TradeItem> implements EventListener {

    private TISellItemsListEditor TISellItemsListEditor;
    private ReplacedItemsListEditor replacedItemsListEditor;

    public TradeItemEditorMain(@NotNull TradeItem tradeItem) {
        super(tradeItem.plugin(), tradeItem, Config.VILLAGER_TRADE_EDITOR_GUI_NAME.get(), 26);

        this.addReturn(22).setClick((viewer, event) -> {
            this.plugin.getEditor().getTradeItemEditor().openNextTick(viewer.getPlayer(), 1);
        });


        this.addItem(Material.ITEM_FRAME, EditorLocales.TRADE_PRODUCT, 0).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                PlayerUtil.addItem(viewer.getPlayer(), tradeItem.getProduct());
                return;
            }

            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                tradeItem.setProduct(cursor);
                PlayerUtil.addItem(viewer.getPlayer(), cursor);
                event.getView().setCursor(null);
                this.save(viewer);
            }
        }).getOptions().setDisplayModifier(((viewer, item) -> {
            item.setType(tradeItem.getProduct().getType());
            item.setItemMeta(tradeItem.getProduct().getItemMeta());
            item.setAmount(tradeItem.getProduct().getAmount());

            item.setItemMeta(tradeItem.getProduct().getItemMeta());

            List<String> lore = ItemUtil.getLore(tradeItem.getProduct());
            lore.addAll(EditorLocales.TRADE_PRODUCT.getLocalizedLore());

            ItemUtil.editMeta(item, meta -> {
                meta.setDisplayName(Colorizer.apply(Colors2.GRAY + "(&r" + ItemUtil.getItemName(item) + Colors2.GRAY + ") " + EditorLocales.TRADE_PRODUCT.getLocalizedName()));
                meta.setLore(lore);

                //meta.addItemFlags(ItemFlag.values());
            });
        }));

        this.addItem(Material.EMERALD, EditorLocales.TRADE_SELL_ITEMS_OBJECT, 8).setClick((viewer, event) -> {
            if (event.isRightClick() && event.isShiftClick()) {
                this.object.setSellItems(new ArrayList<>());
            }
            if (NexEngine.isFolia)
                this.getSellItemsListEditor().open(viewer, 1);
            else
                this.getSellItemsListEditor().openNextTick(viewer, 1);
        });
        this.addItem(Material.ENCHANTED_BOOK, EditorLocales.TRADE_REPLACED_ITEMS_OBJECT, 7).setClick((viewer, event) -> {
            if (event.isRightClick() && event.isShiftClick()) {
                this.object.setReplaceItems(new ArrayList<>());
            }
            if (NexEngine.isFolia)
                this.getReplacedItemsListEditor().open(viewer, 1);
            else
                this.getReplacedItemsListEditor().openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
                    ItemReplacer.replace(item, tradeItem.replacePlaceholders());
                }));
            }
        });

        this.registerListeners();
    }

    @NotNull
    public TISellItemsListEditor getSellItemsListEditor() {
        if (this.TISellItemsListEditor == null)
            this.TISellItemsListEditor = new TISellItemsListEditor(this.object);
        return this.TISellItemsListEditor;
    }

    @NotNull
    public ReplacedItemsListEditor getReplacedItemsListEditor() {
        if (this.replacedItemsListEditor == null)
            this.replacedItemsListEditor = new ReplacedItemsListEditor(this.object);
        return this.replacedItemsListEditor;
    }

    @Override
    public void registerListeners() {
        this.plugin.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void clear() {
        super.clear();
        this.unregisterListeners();
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}