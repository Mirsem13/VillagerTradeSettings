package t.me.p1azmer.plugin.vts;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.command.GeneralCommand;
import t.me.p1azmer.engine.command.list.ReloadSubCommand;
import t.me.p1azmer.plugin.vts.commands.EditorCommand;
import t.me.p1azmer.plugin.vts.config.Config;
import t.me.p1azmer.plugin.vts.editor.EditorLocales;
import t.me.p1azmer.plugin.vts.editor.EditorMainMenu;
import t.me.p1azmer.plugin.vts.lang.Lang;
import t.me.p1azmer.plugin.vts.recipeitems.RecipeItem;
import t.me.p1azmer.plugin.vts.recipeitems.RecipeItemManager;
import t.me.p1azmer.plugin.vts.tradeitem.TradeItemManager;

public final class VTSPlugin extends NexPlugin<VTSPlugin> {
    private TradeItemManager tradeItemManager;
    private RecipeItemManager recipeItemManager;
    private EditorMainMenu editor;

    @Override
    protected @NotNull VTSPlugin getSelf() {
        return this;
    }

    @Override
    public void enable() {
        this.recipeItemManager = new RecipeItemManager(this);
        this.recipeItemManager.setup();

        this.tradeItemManager = new TradeItemManager(this);
        this.tradeItemManager.setup();

        this.editor = new EditorMainMenu(this);
    }

    @Override
    public void disable() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.tradeItemManager != null) {
            this.tradeItemManager.shutdown();
            this.tradeItemManager = null;
        }
        if (this.recipeItemManager != null) {
            this.recipeItemManager.shutdown();
            this.recipeItemManager = null;
        }
    }

    @Override
    public void loadConfig() {
        this.getConfig().initializeOptions(Config.class);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(Lang.class);
        this.getLangManager().loadEditor(EditorLocales.class);
        this.getLangManager().loadEnum(RecipeItem.Profession.class);
        this.getLang().saveChanges();
    }

    @Override
    public void registerHooks() {

    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<VTSPlugin> generalCommand) {
        generalCommand.addChildren(new EditorCommand(this));
        generalCommand.addChildren(new ReloadSubCommand<>(this, Perms.COMMAND_RELOAD));
    }

    @Override
    public void registerPermissions() {
        this.registerPermissions(Perms.class);
    }

    @NotNull
    public EditorMainMenu getEditor() {
        if (this.editor == null) {
            this.editor = new EditorMainMenu(this);
        }
        return this.editor;
    }

    @NotNull
    public TradeItemManager getTradeItemManager() {
        return tradeItemManager;
    }

    @NotNull
    public RecipeItemManager getRecipeItemManager() {
        return recipeItemManager;
    }
}