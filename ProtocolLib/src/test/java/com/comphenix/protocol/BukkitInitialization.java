package com.comphenix.protocol;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.minecraft.server.v1_8_R1.Block;
import net.minecraft.server.v1_8_R1.Item;
import net.minecraft.server.v1_8_R1.StatisticList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemFactory;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.reflect.FieldUtils;
import com.comphenix.protocol.utility.Constants;
import com.comphenix.protocol.utility.MinecraftReflection;
// Will have to be updated for every version though

/**
 * Used to ensure that ProtocolLib and Bukkit is prepared to be tested.
 *
 * @author Kristian
 */
public class BukkitInitialization {
	private static boolean initialized;

	/**
	 * Initialize Bukkit and ProtocolLib such that we can perfrom unit testing.
	 * @throws IllegalAccessException If we are unable to initialize Bukkit.
	 */
	public static void initializeItemMeta() throws IllegalAccessException {
		if (!initialized) {
			// Denote that we're done
			initialized = true;

			initializePackage();

			try {
				Block.R(); // Block.register();
				Item.t(); // Item.register();
				StatisticList.a(); // StatisticList.register();
			} catch (Throwable ex) {
				// Swallow
				ex.printStackTrace();
			}

			// Mock the server object
			Server mockedServer = mock(Server.class);
			ItemFactory mockedFactory = mock(CraftItemFactory.class);
			ItemMeta mockedMeta = mock(ItemMeta.class);

			when(mockedServer.getItemFactory()).thenReturn(mockedFactory);
			when(mockedServer.isPrimaryThread()).thenReturn(true);
			when(mockedFactory.getItemMeta(any(Material.class))).thenReturn(mockedMeta);

			// Inject this fake server
			FieldUtils.writeStaticField(Bukkit.class, "server", mockedServer, true);

			// And the fake item factory
			FieldUtils.writeStaticField(CraftItemFactory.class, "instance", mockedFactory, true);
		}
	}

	/**
	 * Ensure that package names are correctly set up.
	 */
	public static void initializePackage() {
		// Initialize reflection
		MinecraftReflection.setMinecraftPackage(Constants.NMS, Constants.OBC);
	}
}
