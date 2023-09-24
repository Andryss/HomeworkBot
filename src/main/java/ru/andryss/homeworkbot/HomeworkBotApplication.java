package ru.andryss.homeworkbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class HomeworkBotApplication {

    public static void main(String[] args) throws TelegramApiException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(HomeworkBotApplication.class, args);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(applicationContext.getBean(LongPollingBot.class));
    }

}
