package br.com.jorchestra.example.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.example.notification.ChatNotificationExample;

//@JOrchestra(path = "chat")
public class ChatMessageExample {

	//@Autowired
	private ChatNotificationExample chatNotificationExample;

	public void send(final String message) throws JsonProcessingException {
		chatNotificationExample.receive(message);
	}
}
