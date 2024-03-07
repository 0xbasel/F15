package me.baasel.listener;

import lombok.Getter;
import lombok.Setter;
import me.baasel.Util;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GhostPingListener extends ListenerAdapter {

	private final Map<Long, CachedMessage> messageCache = new ConcurrentHashMap<>();

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		User author = event.getAuthor();
		if (!event.isFromGuild() || author.isBot()) return;

		Message message = event.getMessage();
		List<User> mentionedUsers = message.getMentions().getUsers().stream()
				.filter(user -> !user.isBot())
				.collect(Collectors.toList());

		if (mentionedUsers.isEmpty()) return;

		if (mentionedUsers.size() == 1 && mentionedUsers.get(0).equals(author)) return;

		CachedMessage cachedMessage = new CachedMessage(message, author);
		messageCache.put(message.getIdLong(), cachedMessage);
	}

	@Override
	public void onMessageDelete(@NotNull MessageDeleteEvent event) {
		long messageId = event.getMessageIdLong();
		CachedMessage cachedMessage = messageCache.get(messageId);
		if (cachedMessage == null) return;

		List<User> mentionedUsers = cachedMessage.getMentionedUsers();

		if (mentionedUsers.size() == 1 && mentionedUsers.get(0).equals(cachedMessage.getAuthor())) {
			messageCache.remove(messageId);
			return;
		}

		User author = cachedMessage.getAuthor();
		String mentionedUserStr = mentionedUsers.stream()
				.map(user -> user.getName() + " (" + user.getId() + ")")
				.collect(Collectors.joining(", "));

		event.getChannel().sendMessageEmbeds(Util.redEmbed(String.format("Ghost ping detected! Author: %s (%s), Mentioned Users: %s%n", author.getName(), author.getId(), mentionedUserStr))).queue();

		messageCache.remove(messageId);
	}

	@Override
	public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
		long messageId = event.getMessageIdLong();
		CachedMessage cachedMessage = messageCache.get(messageId);
		if (cachedMessage == null) return;

		List<User> updatedMentions = event.getMessage().getMentions().getUsers().stream()
				.filter(user -> !user.isBot())
				.collect(Collectors.toList());

		List<User> originalMentions = new ArrayList<>(cachedMessage.getMentionedUsers());
		List<User> removedMentions = originalMentions.stream()
				.filter(user -> !updatedMentions.contains(user))
				.collect(Collectors.toList());

		cachedMessage.setMentionedUsers(updatedMentions);

		if (removedMentions.size() == 1 && removedMentions.contains(cachedMessage.getAuthor())) return;

		if (!removedMentions.isEmpty()) {
			User author = cachedMessage.getAuthor();
			String mentionedUsers = removedMentions.stream()
					.map(user -> user.getName() + " (" + user.getId() + ")")
					.collect(Collectors.joining(", "));

			event.getChannel().sendMessageEmbeds(Util.redEmbed(String.format("Ghost ping detected! Author: %s (%s), Removed Mention(s): %s", author.getName(), author.getId(), mentionedUsers))).queue();
		}
	}


	@Getter
	@Setter
	private static class CachedMessage {
		private final Message message;
		private final User author;
		private List<User> mentionedUsers;

		public CachedMessage(Message message, User author) {
			this.message = message;
			this.author = author;
			this.mentionedUsers = message.getMentions().getUsers().stream()
					.filter(user -> !user.isBot())
					.collect(Collectors.toList());
		}
	}
}
