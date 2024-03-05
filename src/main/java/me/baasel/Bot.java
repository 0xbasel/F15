package me.baasel;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import me.baasel.command.ColorCommand;
import me.baasel.listener.GhostPingListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot {
	public static void main(String[] args) {
		CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
		commandClientBuilder.setOwnerId("647887379981402118");
		commandClientBuilder.addSlashCommands(
				new ColorCommand()
		);
		CommandClient commandClient = commandClientBuilder.build();
		JDABuilder.createDefault(args[0])
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(
						commandClient,
						new GhostPingListener()
				).build();
	}
}
