package me.baasel.f15.color;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ColorCommand extends ListenerAdapter {

    private final Guild guild;

    public ColorCommand(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("color")) {
            return;
        }

        String colorName = event.getOption("name").getAsString();
        ColorType colorType;
        try {
            colorType = ColorType.valueOf(colorName);
        } catch (IllegalArgumentException ignored) {
            event.reply(String.format("Color %s is not supported!", colorName)).queue();
            return;
        }

        Role colorRole = guild.getRoleById(colorType.getRoleId());
        if (colorRole == null) {
            event.reply("Color role is not defined!").queue();
            return;
        }

        Member member = event.getMember();
        if (member == null) {
            event.reply("I don't know how the fuck you are null, but ok").queue();
            return;
        }

        if (member.getRoles().contains(colorRole)) {
            event.reply("You are already have this color, I will remove it instead :D").queue();
            guild.removeRoleFromMember(member, colorRole).queue();
            return;
        }

        guild.addRoleToMember(member, colorRole).queue();
        event.reply("You color has been set to " + colorType.name()).queue();
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        AutoCompleteQuery completeQuery = event.getFocusedOption();
        if (!(event.getName().equals("color") && completeQuery.getName().equals("name"))) {
            return;
        }

        List<String> colorNames = Arrays.stream(ColorType.values())
                .map(Enum::name)
                .filter(name -> name.startsWith(completeQuery.getValue().toUpperCase()))
                .toList();

        event.replyChoiceStrings(colorNames).queue();
    }
}
