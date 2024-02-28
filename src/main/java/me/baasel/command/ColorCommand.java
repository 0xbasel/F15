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

public class ColorCommand extends SlashCommand {
	public ColorCommand() {
		this.name = "color";
		this.help = "Changes your color";
		this.options = Collections.singletonList(new OptionData(OptionType.STRING, "name", "Type color name", true, true));
	}

	@Override
	protected void execute(SlashCommandEvent event) {
		if (!event.isFromGuild()) return;

		OptionMapping option = event.getOption("name");
		if (option == null) return;

		String colorName = option.getAsString().toUpperCase();

		Guild guild = event.getGuild();
		if (guild == null) return;

		List<Role> roles = guild.getRoles();
		List<Role> colorRoles = roles.stream().filter(r -> r.getName().startsWith("COLOR_")).toList();
		List<Role> colorRolesByColorName = colorRoles.stream().filter(r -> r.getName().endsWith(colorName)).toList();
		if (colorRolesByColorName.isEmpty()) return;

		Role colorRole = colorRolesByColorName.get(0);

		Member member = event.getMember();
		if (member == null) return;

		List<Role> rolesToRemove = member.getRoles().stream().filter(colorRoles::contains).toList();
		rolesToRemove.forEach(role -> guild.removeRoleFromMember(member, role).complete());

		guild.addRoleToMember(member, colorRole).queue();

		event.reply(String.format("Your color changed to %s (%s)", colorRole.getAsMention(), colorName)).setEphemeral(true).queue();
	}

	@Override
	public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
		Guild guild = event.getGuild();
		if (guild == null) return;
		
		event.replyChoiceStrings(
				guild.getRoles().stream()
						.map(Role::getName)
						.filter(rName -> rName.startsWith("COLOR_"))
						.map(rName -> rName.substring("COLOR_".length()))
						.toList()
		).queue();
	}
}
