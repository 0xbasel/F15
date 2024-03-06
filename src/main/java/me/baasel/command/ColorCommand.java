package me.baasel.command;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ColorCommand extends SlashCommand {

	public ColorCommand() {
		this.name = "color";
		this.help = "Changes your color";
		this.options = Collections.singletonList(new OptionData(OptionType.STRING, "name", "Type color name", true, true));
	}

	@Override
	protected void execute(SlashCommandEvent event) {
		if (!event.isFromGuild()) return;

		OptionMapping colorNameOption = event.getOption("name");
		if (colorNameOption == null) return;

		String colorName = colorNameOption.getAsString().toUpperCase();

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

		event.reply(String.format("Your color changed to %s (%s)", colorRole.getAsMention(), colorName)).setEphemeral(true).queue();
	}

	@Override
	public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
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
