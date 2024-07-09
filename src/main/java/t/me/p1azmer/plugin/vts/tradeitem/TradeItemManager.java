package t.me.p1azmer.plugin.vts.tradeitem;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.Pair;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.listeners.VillagerListener;

import java.util.*;

public class TradeItemManager extends AbstractManager<VTSPlugin> {
    public static final String DIR_ITEMS = "/trade_items/";
    private Map<String, TradeItem> tradeItemMap;
    private Map<Pair<AbstractVillager, ItemStack>, TradeItem> tradeRequestCache;

    public TradeItemManager(@NotNull VTSPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.tradeItemMap = new HashMap<>();
        this.tradeRequestCache = new HashMap<>();

        this.plugin.getConfigManager().extractResources(DIR_ITEMS);
        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + DIR_ITEMS, false)) {
            TradeItem tradeItem = new TradeItem(plugin, cfg);
            if (tradeItem.load()) {
                this.tradeItemMap.put(tradeItem.getId().toLowerCase(), tradeItem);
            } else this.plugin.warn("Trade Item not loaded: '" + cfg.getFile().getName() + "'!");
        }
        plugin.info("Trade Items Loaded: " + tradeItemMap.size());

        this.addListener(new VillagerListener(this));
    }

    @Override
    protected void onShutdown() {
        this.tradeRequestCache.clear();
        this.tradeItemMap.forEach((s, tradeItem) -> tradeItem.clear());
        this.tradeItemMap.clear();
    }

    public boolean createTradeItem(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getTradeItemsById(id) != null) return false;

        JYML cfg = new JYML(this.plugin.getDataFolder() + DIR_ITEMS, id + ".yml");
        TradeItem tradeItem = new TradeItem(plugin, cfg);

        tradeItem.save();
        tradeItem.load();
        this.getTradeItemsMap().put(tradeItem.getId(), tradeItem);
        return true;
    }

    public void delete(@NotNull TradeItem tradeItem) {
        if (tradeItem.getFile().delete()) {
            tradeItem.clear();
            this.getTradeItemsMap().remove(tradeItem.getId());
        }
    }

    @NotNull
    public List<String> getTradeItemsIds() {
        return new ArrayList<>(this.tradeItemMap.keySet());
    }

    @NotNull
    public Map<String, TradeItem> getTradeItemsMap() {
        return this.tradeItemMap;
    }

    public void setTradeItemMap(@NotNull Map<String, TradeItem> tradeItemMap) {
        this.tradeItemMap = tradeItemMap;
    }

    @NotNull
    public Collection<TradeItem> getTradeItems() {
        return this.getTradeItemsMap().values();
    }

    @Nullable
    public TradeItem getTradeItemsById(@NotNull String id) {
        return this.tradeItemMap.get(id.toLowerCase());
    }

    @Nullable
    public TradeItem getTradeItem(@NotNull ItemStack requestItem, @NotNull AbstractVillager villager) {
        Pair<AbstractVillager, ItemStack> cacheKey = Pair.of(villager, requestItem);
        TradeItem cachedTradeItem = tradeRequestCache.get(cacheKey);
        if (cachedTradeItem != null) {
            return cachedTradeItem;
        }

        for (TradeItem tradeItem : this.getTradeItems()) {
            if (containsEnchants(requestItem, tradeItem) || tradeItem.getProduct().isSimilar(requestItem)) {
                tradeRequestCache.put(cacheKey, tradeItem);
                return tradeItem;
            }
        }
        return null;
    }

    public boolean containsEnchants(ItemStack requestItem, TradeItem tradeItem) {
        ItemMeta requestItemMeta = requestItem.getItemMeta();
        if (requestItemMeta == null) {
            return false;
        }

        Map<Enchantment, Integer> tradeEnchants = tradeItem.getEnchantments();
        ItemMeta tradeItemMeta = tradeItem.getProduct().getItemMeta();
        if (tradeItemMeta == null && tradeEnchants.isEmpty()) {
            return false;
        }

        if (!(requestItemMeta instanceof EnchantmentStorageMeta requestEnchantMeta)) {
            return false;
        }

        // check for enchants with meta
        for (Map.Entry<Enchantment, Integer> entry : tradeEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (requestEnchantMeta.hasStoredEnchant(enchantment) && requestEnchantMeta.getStoredEnchantLevel(enchantment) == level) {
                return true;
            }
        }

        if (!(tradeItemMeta instanceof EnchantmentStorageMeta tradeEnchantMeta)) {
            return false;
        }

        if (!requestEnchantMeta.hasStoredEnchants() && !tradeEnchantMeta.hasStoredEnchants()) {
            return false;
        }
        if (!requestEnchantMeta.hasStoredEnchants() && tradeEnchantMeta.hasStoredEnchants()) {
            return false;
        }
        if (requestEnchantMeta.hasStoredEnchants() && !tradeEnchantMeta.hasStoredEnchants()) {
            return false;
        }

        for (Map.Entry<Enchantment, Integer> entry : requestEnchantMeta.getStoredEnchants().entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (!tradeEnchantMeta.hasStoredEnchant(enchantment) || tradeEnchantMeta.getStoredEnchantLevel(enchantment) != level) {
                return false;
            }
        }

        for (Map.Entry<Enchantment, Integer> entry : tradeEnchantMeta.getStoredEnchants().entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (!requestEnchantMeta.hasStoredEnchant(enchantment) || requestEnchantMeta.getStoredEnchantLevel(enchantment) != level) {
                return false;
            }
        }
        return true;
    }
}