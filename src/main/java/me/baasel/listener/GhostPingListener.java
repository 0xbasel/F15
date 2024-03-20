package me.baasel.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GhostPingListener extends ListenerAdapter {

	private final Map<Long, Message> messageCache = new ConcurrentHashMap<>();

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		User author = event.getAuthor();
		if (!event.isFromGuild() || author.isBot()) return;

		Message message = event.getMessage();
		List<User> mentionedUsers = message.getMentions().getUsers();
		if (mentionedUsers.isEmpty()) return;

		if (mentionedUsers.size() == 1 && mentionedUsers.get(0).equals(author)) return;

		messageCache.put(message.getIdLong(), message);
	}

	@Override
	public void onMessageDelete(@NotNull MessageDeleteEvent event) {
		long messageId = event.getMessageIdLong();
		Message cachedMessage = messageCache.get(messageId);
		if (cachedMessage == null) return;

		List<User> mentionedUsers = cachedMessage.getMentions().getUsers().stream()
				.filter(user -> !user.isBot() && !user.equals(cachedMessage.getAuthor()))
				.collect(Collectors.toList());
		if (mentionedUsers.isEmpty()) {
			messageCache.remove(messageId);
			return;
		}

		User author = cachedMessage.getAuthor();
		String mentionedUsersStr = mentionedUsers.stream()
				.map(User::getName)
				.collect(Collectors.joining(", "));

		MessageEmbed embed = new EmbedBuilder()
				.setTitle("Ghost Ping Detected!")
				.setColor(0xFF0000)
				.addField(new MessageEmbed.Field("Message ID", messageId + "", true))
				.addField(new MessageEmbed.Field("Author", author.getName(), true))
				.addField(new MessageEmbed.Field("Mentioned User(s)", mentionedUsersStr, false))
				.addField(new MessageEmbed.Field("Message Content", cachedMessage.getContentDisplay(), false))
				.build();

		event.getChannel().sendMessageEmbeds(embed).queue();

		messageCache.remove(messageId);
	}

	@Override
	public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
		Message message = event.getMessage();
		long messageId = message.getIdLong();
		Message cachedMessage = messageCache.get(message.getIdLong());
		if (cachedMessage == null) return;

		List<User> updatedMentions = message.getMentions().getUsers().stream()
				.filter(user -> !user.isBot())
				.collect(Collectors.toList());
		List<User> originalMentions = cachedMessage.getMentions().getUsers().stream()
				.filter(user -> !user.isBot())
				.collect(Collectors.toList());
		List<User> removedMentions = originalMentions.stream()
				.filter(user -> !updatedMentions.contains(user) && !user.equals(cachedMessage.getAuthor()))
				.collect(Collectors.toList());
		if (removedMentions.isEmpty()) return;

		User author = cachedMessage.getAuthor();
		String removedMentionsStr = removedMentions.stream()
				.map(User::getName)
				.collect(Collectors.joining(", "));

		MessageEmbed embed = new EmbedBuilder()
				.setTitle("Ghost Ping Detected!")
				.setColor(0xFF0000)
				.addField(new MessageEmbed.Field("Message ID", messageId + "", true))
				.addField(new MessageEmbed.Field("Author", author.getName(), true))
				.addField(new MessageEmbed.Field("Removed User(s)", removedMentionsStr, false))
				.addField(new MessageEmbed.Field("Message Content", cachedMessage.getContentDisplay(), false))
				.build();

		cachedMessage.replyEmbeds(embed).queue();

		messageCache.put(messageId, message);
	}
}
