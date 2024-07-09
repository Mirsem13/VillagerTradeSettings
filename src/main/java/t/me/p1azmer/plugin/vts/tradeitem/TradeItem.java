package t.me.p1azmer.plugin.vts.tradeitem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.vts.Placeholders;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.tradeitem.editor.TradeItemEditorMain;

import java.util.*;

@Setter
@Getter
public class TradeItem extends AbstractConfigHolder<VTSPlugin> implements Placeholder {
    private TradeItemEditorMain editor;
    private ItemStack product;
    private List<ItemStack> sellItems;
    private List<ItemStack> replaceItems;
    private Map<Enchantment, Integer> enchantments;

    private final PlaceholderMap placeholderMap;

    public TradeItem(@NotNull VTSPlugin plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
        this.product = new ItemStack(Material.STONE);
        this.sellItems = new ArrayList<>();
        this.replaceItems = new ArrayList<>();
        this.enchantments = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
          .add(Placeholders.TRADE_ITEM_ID, this::getId);
    }

    @Override
    public boolean load() {
        this.product = cfg.getItemEncoded("Product");

        this.sellItems = new ArrayList<>();
        this.sellItems.addAll(Arrays.stream(cfg.getItemsEncoded("Sell_Items")).toList());

        this.replaceItems = new ArrayList<>();
        this.replaceItems.addAll(Arrays.stream(cfg.getItemsEncoded("Replaced_Items")).toList());

        this.enchantments = new HashMap<>();
        for (String enchantId : cfg.getSection("Enchantment")) {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantId));
            if (enchantment == null) continue;
            int level = cfg.getInt("Enchantment." + enchantId);
            this.enchantments.put(enchantment, level);
        }
        return true;
    }

    @Override
    public void onSave() {
        cfg.setItemEncoded("Product", this.getProduct());

        cfg.setItemsEncoded("Sell_Items", this.getSellItems());
        cfg.setItemsEncoded("Replaced_Items", this.getReplaceItems());

        this.getEnchantments().forEach((enchantment, level) -> {
            cfg.set("Enchantment." + enchantment.getKey().getKey(), level);
        });
    }

    @NotNull
    public TradeItemEditorMain getEditor() {
        if (this.editor == null) {
            this.editor = new TradeItemEditorMain(this);
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

    @NotNull
    public List<ItemStack> getSellItems() {
        return sellItems;
    }

    @NotNull
    public List<ItemStack> getReplaceItems() {
        return replaceItems;
    }

    @NotNull
    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public boolean isAllowedToAdd(@NotNull ItemStack item) {
        return !this.getProduct().getType().equals(item.getType())
          && this.getReplaceItems()
                 .stream()
                 .noneMatch(found -> found.getType().equals(item.getType()))
          && this.getSellItems()
                 .stream()
                 .noneMatch(found -> found.getType().equals(item.getType()));
    }

    @NotNull
    public ItemStack getMissingSellItem(@NotNull List<ItemStack> replaceItems) {
        return this.getSellItems()
                   .stream()
                   .filter(f -> !replaceItems.contains(f))
                   .findFirst()
                   .orElse(new ItemStack(Material.AIR));
    }

    public void setProduct(@NotNull ItemStack product) {
        this.product = product;
    }

    public void setSellItems(@NotNull List<ItemStack> sellItems) {
        this.sellItems = sellItems;
    }

    public void setReplaceItems(@NotNull List<ItemStack> replaceItems) {
        this.replaceItems = replaceItems;
    }

    public void setEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }
}