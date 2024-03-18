package me.baasel;

import me.baasel.command.ColorCommand;
import me.baasel.listener.GhostPingListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot {
	public static void main(String[] args) {
		JDA jda = JDABuilder.createDefault(args[0])
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(
						new ColorCommand(),
						new GhostPingListener()
				).build();

		jda.updateCommands().addCommands(
				Commands.slash("color", "Changes your color")
						.addOption(OptionType.STRING, "name", "Type color name", true, true)
		).queue();
	}
}
