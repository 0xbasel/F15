package me.baasel.f15;

import me.baasel.f15.color.ColorCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class F15 {

    public static void main(String[] args) {
        JDA jda;
        try {
            jda = JDABuilder.createLight(args[0])
                    .build()
                    .awaitReady();
        } catch (InterruptedException e) {
            System.out.println("Bot failed to start!");
            return;
        }

        Guild f15Guild = null;
        for (Guild guild : jda.getGuilds()) {
            if (guild.getId().equals("868324181819949086")) {
                f15Guild = guild;
                break;
            }
        }

        if (f15Guild == null) {
            System.out.println("F15 guild not found, disabling the bot...");
            jda.shutdown();
            return;
        }

        jda.addEventListener(new ColorCommand(f15Guild));

        f15Guild.updateCommands().addCommands(
                Commands.slash("color", "Change your color")
                        .addOption(OptionType.STRING, "name", "The name of the color", true, true)
        ).queue();
    }
}
