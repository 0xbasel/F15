package me.baasel;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import me.baasel.command.ColorCommand;
import net.dv8tion.jda.api.JDABuilder;

public class Bot {
	public static void main(String[] args) {
		CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
		commandClientBuilder.setOwnerId("647887379981402118");
		commandClientBuilder.addSlashCommands(
				new ColorCommand()
		);
		CommandClient commandClient = commandClientBuilder.build();
		JDABuilder.createDefault(args[0])
				.addEventListeners(
						commandClient
				).build();
	}
}
