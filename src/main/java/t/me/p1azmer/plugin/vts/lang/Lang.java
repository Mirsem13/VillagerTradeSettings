package t.me.p1azmer.plugin.vts.lang;

import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.lang.EngineLang;

import static t.me.p1azmer.engine.utils.Colors2.*;

public class Lang extends EngineLang {
    public static final LangKey COMMAND_EDITOR_DESC = LangKey.of("Command.Editor.Desc", "Open the editor.");

    public static final LangKey Editor_Trade_Item_Enter_Create = new LangKey("Editor.Trade_Item.Enter.Create", GRAY + "Enter " + GREEN + "unique " + GRAY + "trade item " + GREEN + "identifier" + GRAY + "...");
    public static final LangKey Editor_Trade_Item_Error_Exist = new LangKey("Editor.Trade_Item.Error.Exist", RED + "Trade item already exists!");
    public static final LangKey Editor_Error_Items_Limit = new LangKey("Editor.Error.Items_Limit", RED + "The number of items cannot exceed 2");
    public static final LangKey Editor_Error_Items_Already_Has = new LangKey("Editor.Error.Items_Already", RED + "Such material has already been added to this recipe!");

    public static final LangKey Editor_Recipe_Enter_Create = new LangKey("Editor.Recipe.Enter.Create", GRAY + "Enter " + GREEN + "unique " + GRAY + "recipe " + GREEN + "identifier" + GRAY + "...");
    public static final LangKey Editor_Recipe_Error_Exist = new LangKey("Editor.Recipe.Error.Exist", RED + "Recipe already exists!");
    public static final LangKey Editor_Recipe_Enter_Profession = new LangKey("Editor.Recipe.Enter.Profession", GRAY + "Enter " + GREEN + "Villager Profession" + GRAY + "...");
    public static final LangKey Generic_Write_Value = LangKey.of("Editor.Generic.Write.Value", GRAY + "Enter " + GREEN + "value" + GRAY + "...");
}