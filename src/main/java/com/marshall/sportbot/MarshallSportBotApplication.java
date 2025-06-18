package com.marshall.sportbot;

import com.marshall.sportbot.controller.SportBotController;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
public class MarshallSportBotApplication implements CommandLineRunner {

    private final SportBotController botController;

    public static void main(String[] args) {
        SpringApplication.run(MarshallSportBotApplication.class, args);
    }

    @Override
    public void run(String... args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(botController);
    }
}

