package t.me.p1azmer.plugin.vts.recipeitems;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.vts.Placeholders;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.recipeitems.editor.RecipeItemEditorMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class RecipeItem extends AbstractConfigHolder<VTSPlugin> implements Placeholder {
    private RecipeItemEditorMain editor;
    private ItemStack product;
    private int limitPerVillager;
    private int expReward;
    private int maxUses;
    private double chance;
    private boolean discounts;
    private Profession profession;
    private List<ItemStack> sellItems;

    private final PlaceholderMap placeholders;

    public RecipeItem(@NotNull VTSPlugin plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
        this.product = new ItemStack(Material.STONE);
        this.chance = 100;
        this.limitPerVillager = 3;
        this.maxUses = 5;
        this.expReward = 2;
        this.discounts = false;
        this.profession = Profession.CARTOGRAPHER;
        this.sellItems = Collections.emptyList();

        this.placeholders = new PlaceholderMap()
          .add(Placeholders.RECIPE_ID, this::getId)
          .add(Placeholders.RECIPE_CHANCE, () -> String.valueOf(this.getChance()))
          .add(Placeholders.RECIPE_DISCOUNT, () -> LangManager.getBoolean(this.isDiscounts()))
          .add(Placeholders.RECIPE_MAX_USES, () -> String.valueOf(this.getMaxUses()))
          .add(Placeholders.RECIPE_EXP_REWARD, () -> String.valueOf(this.getExpReward()))
          .add(Placeholders.RECIPE_LIMIT_PER_VILLAGER, () -> String.valueOf(this.getLimitPerVillager()))
          .add(Placeholders.RECIPE_PROFESSION, () -> plugin.getLangManager().getEnum(this.getProfession()));
    }

    @Override
    public boolean load() {
        this.product = cfg.getItemEncoded("Product");
        this.chance = cfg.getDouble("Chance");
        this.limitPerVillager = cfg.getInt("Limit_Per_Villager");
        this.maxUses = cfg.getInt("Max_Uses");
        this.discounts = cfg.getBoolean("Discounts");
        this.expReward = cfg.getInt("Exp_Reward");
        this.profession = cfg.getEnum("Profession", Profession.class, Profession.CARTOGRAPHER);

        this.sellItems = new ArrayList<>();
        this.sellItems.addAll(Arrays.stream(cfg.getItemsEncoded("Sell_Items")).toList());

        return true;
    }

    @Override
    public void onSave() {
        cfg.setItemEncoded("Product", this.getProduct());
        cfg.set("Chance", this.getChance());
        cfg.set("Limit_Per_Villager", this.getLimitPerVillager());
        cfg.set("Max_Uses", this.getMaxUses());
        cfg.set("Discounts", this.isDiscounts());
        cfg.set("Exp_Reward", this.getExpReward());
        cfg.set("Profession", this.getProfession());

        cfg.setItemsEncoded("Sell_Items", this.getSellItems());
    }

    @NotNull
    public RecipeItemEditorMain getEditor() {
        if (this.editor == null) {
            this.editor = new RecipeItemEditorMain(this);
        }
        return editor;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public ItemStack getProduct() {
        return new ItemStack(product);
    }

    public boolean isAllowedToAdd(@NotNull ItemStack item) {
        Material itemType = item.getType();
        Material productType = this.getProduct().getType();
        return !productType.equals(itemType) && this.getSellItems()
                                                    .stream()
                                                    .noneMatch(found -> found.getType().equals(itemType));
    }


    public enum Profession {
        /**
         * Wandering is not a resident's profession, it is his type.
         */
        WANDERING,
        /**
         * Armorer profession. Wears a black apron. Armorers primarily trade for
         * iron armor, chainmail armor, and sometimes diamond armor.
         */
        ARMORER,
        /**
         * Butcher profession. Wears a white apron. Butchers primarily trade for
         * raw and cooked food.
         */
        BUTCHER,
        /**
         * Cartographer profession. Wears a white robe. Cartographers primarily
         * trade for explorer maps and some paper.
         */
        CARTOGRAPHER,
        /**
         * Cleric profession. Wears a purple robe. Clerics primarily trade for
         * rotten flesh, gold ingot, redstone, lapis, ender pearl, glowstone,
         * and bottle o' enchanting.
         */
        CLERIC,
        /**
         * Farmer profession. Wears a brown robe. Farmers primarily trade for
         * food-related items.
         */
        FARMER,
        /**
         * Fisherman profession. Wears a brown robe. Fisherman primarily trade
         * for fish, as well as possibly selling string and/or coal.
         */
        FISHERMAN,
        /**
         * Fletcher profession. Wears a brown robe. Fletchers primarily trade
         * for string, bows, and arrows.
         */
        FLETCHER,
        /**
         * Leatherworker profession. Wears a white apron. Leatherworkers
         * primarily trade for leather, and leather armor, as well as saddles.
         */
        LEATHERWORKER,
        /**
         * Librarian profession. Wears a white robe. Librarians primarily trade
         * for paper, books, and enchanted books.
         */
        LIBRARIAN,
        /**
         * Mason profession.
         */
        MASON,
        /**
         * Sheperd profession. Wears a brown robe. Shepherds primarily trade for
         * wool items, and shears.
         */
        SHEPHERD,
        /**
         * Toolsmith profession. Wears a black apron. Tool smiths primarily
         * trade for iron and diamond tools.
         */
        TOOLSMITH,
        /**
         * Weaponsmith profession. Wears a black apron. Weapon smiths primarily
         * trade for iron and diamond weapons, sometimes enchanted.
         */
        WEAPONSMITH;

        public boolean isAllowed(@NotNull AbstractVillager villager) {
            if (villager instanceof Villager defaultVillager)
                return defaultVillager.getProfession().name().equals(this.name());
            else if (villager instanceof WanderingTrader) {
                return this.equals(WANDERING);
            }
            return false;
        }
    }
}
