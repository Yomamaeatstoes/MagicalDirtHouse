package ori.me.dirthouse;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;

public final class DirtHouse extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("dirt").setExecutor(this);
        if(!getDataFolder().exists())
            getDataFolder().mkdir();
        File file = new File(getDataFolder().getAbsolutePath() + "/schematics");
        if(!file.exists())
            file.mkdir();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if(label.equalsIgnoreCase("dirt")){
            pasteHouse(p.getLocation(), new File(getDataFolder().getAbsolutePath() + "/schematics/dirt.schematic"));
        }
        return true;
    }
    private void pasteHouse(Location loc, File schem){
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(schem);
            ClipboardReader reader = format.getReader(Files.newInputStream(schem.toPath()));

            Clipboard clipboard = reader.read();
            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(loc.getWorld());

            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);


            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).ignoreAirBlocks(true).build();

            try {
                Operations.complete(operation);
                editSession.flushQueue();

            } catch (WorldEditException e) {

                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
