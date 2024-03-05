package me.baasel.listener;

import me.baasel.Util;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GhostPingListener extends ListenerAdapter {

	private final Map<Long, CachedMessage> messageCache = new ConcurrentHashMap<>();

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!event.isFromGuild() && event.getAuthor().isBot()) return;
		
		Message message = event.getMessage();
		if (message.getMentions().getUsers().isEmpty()) return;

		CachedMessage cachedMessage = new CachedMessage(message, event.getAuthor());
		messageCache.put(message.getIdLong(), cachedMessage);
	}

	@Override
	public void onMessageDelete(@NotNull MessageDeleteEvent event) {
		long messageId = event.getMessageIdLong();
		if (!messageCache.containsKey(messageId)) return;

		CachedMessage cachedMessage = messageCache.get(messageId);
		Message message = cachedMessage.message;
		if (message.getMentions().getUsers().isEmpty()) return;

		User author = cachedMessage.author;
		String mentionedUsers = message.getMentions().getUsers().stream()
				.map(user -> user.getName() + " (" + user.getId() + ")")
				.collect(Collectors.joining(", "));

		event.getChannel().sendMessageEmbeds(Util.redEmbed(String.format("Ghost ping detected! Author: %s (%s), Mentioned Users: %s%n", author.getName(), author.getId(), mentionedUsers))).queue();
	}

	private record CachedMessage(Message message, User author) {
	}
}
