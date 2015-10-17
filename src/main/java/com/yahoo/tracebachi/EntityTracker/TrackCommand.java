package com.yahoo.tracebachi.EntityTracker;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

/**
 * Created by Trace Bachi (BigBossZee) on 8/19/2015.
 */
public class TrackCommand implements CommandExecutor, Listener
{
    private final HashMap<String, Class<? extends Entity>> nameToClass = new HashMap<>();
    private final HashMap<String, List<LocationCounter>> nameToCounterList = new HashMap<>();

    public TrackCommand()
    {
        addType("Item", EntityItem.class);
        addType("XPOrb", EntityExperienceOrb.class);
        addType("LeashKnot", EntityLeash.class);
        addType("Painting", EntityPainting.class);
        addType("Arrow", EntityArrow.class);
        addType("Snowball", EntitySnowball.class);
        addType("Fireball", EntityLargeFireball.class);
        addType("SmallFireball", EntitySmallFireball.class);
        addType("ThrownEnderpearl", EntityEnderPearl.class);
        addType("EyeOfEnderSignal", EntityEnderSignal.class);
        addType("ThrownPotion", EntityPotion.class);
        addType("ThrownExpBottle", EntityThrownExpBottle.class);
        addType("ItemFrame", EntityItemFrame.class);
        addType("WitherSkull", EntityWitherSkull.class);
        addType("PrimedTnt", EntityTNTPrimed.class);
        addType("FallingSand", EntityTNTPrimed.class);
        addType("FireworksRocketEntity", EntityFireworks.class);
        addType("Boat", EntityBoat.class);
        addType("MinecartRideable", EntityMinecartRideable.class);
        addType("MinecartChest", EntityMinecartChest.class);
        addType("MinecartFurnace", EntityMinecartFurnace.class);
        addType("MinecartTNT", EntityMinecartTNT.class);
        addType("MinecartHopper", EntityMinecartHopper.class);
        addType("MinecartSpawner", EntityMinecartMobSpawner.class);
        addType("MinecartCommandBlock", EntityMinecartCommandBlock.class);
        addType("Mob", EntityInsentient.class);
        addType("Monster", EntityMonster.class);
        addType("Creeper", EntityCreeper.class);
        addType("Skeleton", EntitySkeleton.class);
        addType("Spider", EntitySpider.class);
        addType("Giant", EntityGiantZombie.class);
        addType("Zombie", EntityZombie.class);
        addType("Slime", EntitySlime.class);
        addType("Ghast", EntityGhast.class);
        addType("PigZombie", EntityPigZombie.class);
        addType("Enderman", EntityEnderman.class);
        addType("CaveSpider", EntityCaveSpider.class);
        addType("Silverfish", EntitySilverfish.class);
        addType("Blaze", EntityBlaze.class);
        addType("LavaSlime", EntityMagmaCube.class);
        addType("EnderDragon", EntityEnderDragon.class);
        addType("WitherBoss", EntityWither.class);
        addType("Bat", EntityBat.class);
        addType("Witch", EntityWitch.class);
        addType("Pig", EntityPig.class);
        addType("Sheep", EntitySheep.class);
        addType("Cow", EntityCow.class);
        addType("Chicken", EntityChicken.class);
        addType("Squid", EntitySquid.class);
        addType("Wolf", EntityWolf.class);
        addType("MushroomCow", EntityMushroomCow.class);
        addType("SnowMan", EntitySnowman.class);
        addType("Ozelot", EntityOcelot.class);
        addType("VillagerGolem", EntityIronGolem.class);
        addType("EntityHorse", EntityHorse.class);
        addType("Villager", EntityVillager.class);
        addType("EnderCrystal", EntityEnderCrystal.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(EntityTracker.BAD + "This command can only be run by players.");
            return true;
        }

        if(!sender.hasPermission("EntityTracker.Use"))
        {
            sender.sendMessage(EntityTracker.BAD + "You do not have permission to run that command.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length >= 2 && args[0].equalsIgnoreCase("tp"))
        {
            Integer index = parseInteger(args[1]);
            if(index == null || index <= 0)
            {
                sender.sendMessage(EntityTracker.BAD + "Number must be greater than 0.");
                return true;
            }

            runTrackTpCommand(player, index);
        }
        else if(args.length >= 1)
        {
            Class<? extends Entity> type = nameToClass.get(args[0].toLowerCase());
            if(type == null)
            {
                sender.sendMessage(EntityTracker.BAD + args[0] + " is not a valid entity type.");
                return true;
            }

            Integer displaySize = 3;
            if(args.length >= 2)
            {
                Integer tempDisplaySize = parseInteger(args[1]);
                if(tempDisplaySize == null || tempDisplaySize <= 0)
                {
                    sender.sendMessage(EntityTracker.BAD + "Number must be greater than 0.");
                    return true;
                }
                else
                {
                    displaySize = tempDisplaySize;
                }
            }

            runTrackCommand(player, type, displaySize);
        }
        else
        {
            sender.sendMessage(EntityTracker.BAD + "Commands:");
            sender.sendMessage(ChatColor.GRAY + "  /track [type] [display size]");
            sender.sendMessage(ChatColor.GRAY + "  /track tp [number]");
            return true;
        }
        return true;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        nameToCounterList.remove(event.getPlayer().getName());
    }

    private void runTrackTpCommand(Player player, int listIndex)
    {
        List<LocationCounter> locList = nameToCounterList.get(player.getName());

        if(locList != null)
        {
            if(listIndex > locList.size() || listIndex < 1)
            {
                player.sendMessage(EntityTracker.BAD +
                    "Invalid index. It must be between 1 ~ " + locList.size());
            }
            else
            {
                LocationCounter counter = locList.get(listIndex - 1);
                Location location = counter.getLocation(player.getWorld());

                player.sendMessage(EntityTracker.GOOD +
                    "Teleporting to #" + listIndex);
                player.teleport(location);
            }
        }
        else
        {
            player.sendMessage(EntityTracker.BAD +
                "No track list found. Re-run the /track command to build it.");
        }
    }

    private void runTrackCommand(Player player, Class<? extends Entity> type, int listDisplaySize)
    {
        List<LocationCounter> counterList = new ArrayList<>();
        CraftWorld craftWorld = (CraftWorld) player.getWorld();

        for(Entity entity : (List<Entity>) craftWorld.getHandle().entityList)
        {
            if(type.equals(entity.getClass()))
            {
                LocationCounter counter = new LocationCounter(entity.locX, entity.locY, entity.locZ);
                int index = counterList.indexOf(counter);

                if(index >= 0)
                {
                    counter = counterList.get(index);
                    counter.setCount(counter.getCount() + 1);
                }
                else
                {
                    counterList.add(counter);
                }
            }
        }

        Collections.sort(counterList, new Comparator<LocationCounter>()
        {
            @Override
            public int compare(LocationCounter o1, LocationCounter o2)
            {
                return Integer.compare(o2.getCount(), o1.getCount());
            }
        });
        nameToCounterList.put(player.getName(), counterList);

        player.sendMessage(EntityTracker.GOOD + "Report for " + type.getSimpleName() + ":");
        for(int i = 0; i < listDisplaySize && i < counterList.size(); ++i)
        {
            LocationCounter counter = counterList.get(i);
            player.sendMessage(ChatColor.GRAY + " #" + ChatColor.WHITE + (i + 1) + ChatColor.GRAY +
                " : " + counter.toString());
        }
    }

    private void addType(String name, Class clazz)
    {
        nameToClass.put(name.toLowerCase(), clazz);
    }

    private Integer parseInteger(String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch(NumberFormatException ex)
        {
            return null;
        }
    }
}
