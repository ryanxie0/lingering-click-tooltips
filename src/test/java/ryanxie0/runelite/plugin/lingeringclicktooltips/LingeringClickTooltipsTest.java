package ryanxie0.runelite.plugin.lingeringclicktooltips;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class LingeringClickTooltipsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(LingeringClickTooltipsPlugin.class);
		RuneLite.main(args);
	}
}