package t.me.p1azmer.plugin.vts.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.plugin.vts.Perms;
import t.me.p1azmer.plugin.vts.VTSPlugin;
import t.me.p1azmer.plugin.vts.lang.Lang;

public class EditorCommand extends AbstractCommand<VTSPlugin> {

  public EditorCommand(@NotNull VTSPlugin plugin) {
    super(plugin, new String[]{"editor"}, Perms.COMMAND_EDITOR);
    this.setDescription(plugin.getMessage(Lang.COMMAND_EDITOR_DESC));
    this.setPlayerOnly(true);
  }

  @Override
  protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
    this.plugin.getEditor().openNextTick((Player) sender, 1);
  }
}