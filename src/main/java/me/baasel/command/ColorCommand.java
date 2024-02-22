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
import java.util.Map;
import java.util.stream.Collectors;

public class ColorCommand extends SlashCommand {
	private final Map<String, String> colors = Map.of(
			"BLUE", "1210154587240267826",
			"GREEN", "1210154545930575922"
	);

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
		String name = option.getAsString().toUpperCase();
		String roleId = colors.get(name);
		if (roleId == null) {
			event.reply(name + " not found.").setEphemeral(true).queue();
			return;
		}
		Guild guild = event.getGuild();
		if (guild == null) return;
		Role role = guild.getRoleById(roleId);
		if (role == null) return;
		Member member = event.getMember();
		if (member == null) return;
		member.getRoles().stream()
				.filter(memberRole -> colors.values().stream().anyMatch(colorId -> colorId.equals(memberRole.getId())))
				.forEach(roleToRemove -> guild.removeRoleFromMember(member, roleToRemove).complete());
		guild.addRoleToMember(member, role).queue();
		event.reply(String.format("Your color changed to %s (%s)", role.getAsMention(), name)).setEphemeral(true).queue();
	}

	@Override
	public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
		event.replyChoiceStrings(colors.keySet().stream()
				.filter(color -> color.startsWith(event.getFocusedOption().getValue().toUpperCase())).collect(Collectors.toList())).queue();
	}
}
