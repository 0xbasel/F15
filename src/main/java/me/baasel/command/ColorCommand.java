package me.baasel.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ColorCommand extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if (!event.isFromGuild()) return;
		if (!event.getName().equals("color")) return;

		event.deferReply().queue();

		OptionMapping colorOption = event.getOption("name");
		if (colorOption == null) return;

		String colorName = colorOption.getAsString().toUpperCase();
		Guild guild = event.getGuild();
		if (guild == null) return;

		Role colorRole = guild.getRoles().stream()
				.filter(r -> r.getName().equals("COLOR_" + colorName))
				.findFirst()
				.orElse(null);
		if (colorRole == null) return;

		Member member = event.getMember();
		if (member == null) return;

		List<Role> colorRolesToRemove = member.getRoles().stream()
				.filter(r -> r.getName().startsWith("COLOR_"))
				.collect(Collectors.toList());
		colorRolesToRemove.forEach(r -> guild.removeRoleFromMember(member, r).complete());

		guild.addRoleToMember(member, colorRole).queue();

		MessageEmbed embed = new EmbedBuilder()
				.setDescription(String.format("Your color changed to %s", colorName))
				.setColor(colorRole.getColor())
				.build();

		event.getHook().editOriginalEmbeds(embed).queue();
	}

	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		if (!event.getName().equals("color") && !event.getFocusedOption().getName().equals("name")) return;

		Guild guild = event.getGuild();
		if (guild == null) return;

		event.replyChoiceStrings(guild.getRoles().stream()
				.map(Role::getName)
				.filter(rName -> rName.startsWith("COLOR_"))
				.map(rName -> rName.substring("COLOR_".length()))
				.collect(Collectors.toList())
		).queue();
	}
}
