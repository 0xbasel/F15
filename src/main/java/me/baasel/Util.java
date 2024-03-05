package me.baasel;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Util {

	public static MessageEmbed greenEmbed(String description) {
		return new EmbedBuilder().setDescription(description).setColor(0x008000).build();
	}

	public static MessageEmbed redEmbed(String description) {
		return new EmbedBuilder().setDescription(description).setColor(0xFF0000).build();
	}
}
