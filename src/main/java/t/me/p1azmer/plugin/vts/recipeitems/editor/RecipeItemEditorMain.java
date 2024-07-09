package t.me.p1azmer.plugin.vts.recipeitems.editor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.NexEngine;
import t.me.p1azmer.engine.api.manager.EventListener;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.*;
import t.me.p1azmer.engine.utils.collections.Lists;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.config.Config;
import t.me.p1azmer.plugin.vts.editor.EditorLocales;
import t.me.p1azmer.plugin.vts.lang.Lang;
import t.me.p1azmer.plugin.vts.recipeitems.RecipeItem;
import t.me.p1azmer.plugin.vts.recipeitems.editor.sellitems.RISellItemsListEditor;

import java.util.ArrayList;
import java.util.List;

public class RecipeItemEditorMain extends EditorMenu<VTSPlugin, RecipeItem> implements EventListener {

    private RISellItemsListEditor sellItemsListEditor;

    public RecipeItemEditorMain(@NotNull RecipeItem recipeItem) {
        super(recipeItem.plugin(), recipeItem, Config.RECIPE_EDITOR_GUI_NAME.get(), 26);

        this.addReturn(22).setClick((viewer, event) ->
          this.plugin.getEditor().getRecipeItemEditorList().openNextTick(viewer.getPlayer(), 1));


        this.addItem(Material.ITEM_FRAME, EditorLocales.RECIPE_PRODUCT, 0).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                PlayerUtil.addItem(viewer.getPlayer(), recipeItem.getProduct());
                return;
            }

            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                recipeItem.setProduct(cursor);
                PlayerUtil.addItem(viewer.getPlayer(), cursor);
                event.getView().setCursor(null);
                this.save(viewer);
            }
        }).getOptions().setDisplayModifier(((viewer, item) -> {
            item.setType(recipeItem.getProduct().getType());
            item.setItemMeta(recipeItem.getProduct().getItemMeta());
            item.setAmount(recipeItem.getProduct().getAmount());

            item.setItemMeta(recipeItem.getProduct().getItemMeta());

            List<String> lore = ItemUtil.getLore(recipeItem.getProduct());
            lore.addAll(EditorLocales.RECIPE_PRODUCT.getLocalizedLore());

            ItemUtil.editMeta(item, meta -> {
                meta.setDisplayName(Colorizer.apply(Colors2.GRAY + "(&r" + ItemUtil.getItemName(item) + Colors2.GRAY + ") " + EditorLocales.RECIPE_PRODUCT.getLocalizedName()));
                meta.setLore(lore);

                //meta.addItemFlags(ItemFlag.values());
            });
        }));

        this.addItem(Material.DROPPER, EditorLocales.RECIPE_CHANCE, 3).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.Generic_Write_Value, wrapper -> {
                double chance = wrapper.asDouble(1);
                this.object.setChance(chance);
                this.object.save();
                return true;
            });
        });

        this.addItem(Material.TOTEM_OF_UNDYING, EditorLocales.RECIPE_MAX_USES, 4).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.Generic_Write_Value, wrapper -> {
                int amount = wrapper.asInt(1);
                this.object.setMaxUses(amount);
                this.object.save();
                return true;
            });
        });
        this.addItem(Material.EXPERIENCE_BOTTLE, EditorLocales.RECIPE_EXP_REWARD, 5).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.Generic_Write_Value, wrapper -> {
                int amount = wrapper.asInt(1);
                this.object.setExpReward(amount);
                this.object.save();
                return true;
            });
        });

        this.addItem(Material.BOOK, EditorLocales.RECIPE_PROFESSION, 13).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), Lists.getEnums(RecipeItem.Profession.class), true);

            this.handleInput(viewer, Lang.Editor_Recipe_Enter_Profession, wrapper -> {
                RecipeItem.Profession profession = wrapper.asEnum(RecipeItem.Profession.class, RecipeItem.Profession.CARTOGRAPHER);
                this.object.setProfession(profession);
                this.object.save();
                return true;
            });
        }).getOptions().setDisplayModifier((viewer, itemStack) -> {
            StringUtil.getEnum(this.getObject().getProfession().name(), ProfessionItem.class)
                      .ifPresent(professionItem -> itemStack.setType(professionItem.getMaterial()));
            ItemReplacer.replace(itemStack, this.getObject().replacePlaceholders());
        });

        this.addItem(Material.EMERALD, EditorLocales.RECIPE_SELL_ITEMS, 8).setClick((viewer, event) -> {
            if (event.isRightClick() && event.isShiftClick()) {
                this.object.setSellItems(new ArrayList<>());
            }
            if (NexEngine.isFolia)
                this.getSellItemsListEditor().open(viewer, 1);
            else
                this.getSellItemsListEditor().openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> ItemReplacer.replace(item, recipeItem.replacePlaceholders())));
            }
        });

        this.registerListeners();
    }

    @NotNull
    public RISellItemsListEditor getSellItemsListEditor() {
        if (this.sellItemsListEditor == null)
            this.sellItemsListEditor = new RISellItemsListEditor(this.object);
        return this.sellItemsListEditor;
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

    @Getter
    @AllArgsConstructor
    public enum ProfessionItem {
        ARMORED(Material.IRON_CHESTPLATE),
        BUTCHER(Material.COOKED_BEEF),
        CARTOGRAPHER(Material.FILLED_MAP),
        CLERIC(Material.BOOK),
        FARMER(Material.FERN),
        FISHERMAN(Material.TROPICAL_FISH),
        FLETCHER(Material.BOW),
        LEATHERWORKER(Material.LEATHER_CHESTPLATE),
        LIBRARIAN(Material.ENCHANTED_BOOK),
        MASON(Material.STONE),
        SHEPHERD(Material.WHITE_WOOL),
        TOOLSMITH(Material.DIAMOND_PICKAXE),
        WEAPONSMITH(Material.DIAMOND_SWORD),
        WANDERING(Material.POTION);

        private final Material material;
    }
}