package com.marshall.sportbot.controller;

import com.marshall.sportbot.entity.UserEntity;
import com.marshall.sportbot.enums.ExerciseType;
import com.marshall.sportbot.enums.UserState;
import com.marshall.sportbot.service.ExerciseService;
import com.marshall.sportbot.service.Helper;
import com.marshall.sportbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
@Slf4j
public class SportBotController extends TelegramLongPollingBot {
    private final UserService userService;
    private final ExerciseService exerciseService;
    private final Map<Long, UserState> userStateMap = new ConcurrentHashMap<>();

    @Value("${bot.name}")
    private String botUserName;
    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info(String.valueOf(update));
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText().trim();
            String chatId = update.getMessage().getChatId().toString();
            Long userId = update.getMessage().getFrom().getId();
            UserEntity userEntity = userService.getUser(update.getMessage().getFrom(), chatId);

            UserState state = userStateMap.get(userId);
            switch (text) {
                case "/start" -> {
                    sendStartKeyboard(chatId, "Добро пожаловать! Выберите действие:");
                    userStateMap.put(userId, UserState.IN_START_MENU);
                }
                case "/help", "ℹ️ Помощь" -> sendMessage(chatId, getHelpText());
                case "Отжимания" -> {
                    sendMessage(chatId, exerciseService.getExerciseProgressMessage(userEntity, ExerciseType.PUSH_UP));
                    sendPushUpsKeyboard(chatId, "Выберите действие:");
                    userStateMap.put(userId, UserState.IN_PUSH_UP_MENU);
                }
                case "Подтягивания" -> sendMessage(chatId, "В разработке");
                case "Назад" -> {
                    if (UserState.IN_PUSH_UP_MENU.equals(state)) {
                        sendStartKeyboard(chatId, "Выберите действие:");
                        userStateMap.put(userId, UserState.IN_START_MENU);
                    }
                    if (UserState.IN_PUSH_UP_GOAL_MENU.equals(state)) {
                        sendPushUpsKeyboard(chatId, "Выберите действие:");
                        userStateMap.put(userId, UserState.IN_PUSH_UP_MENU);
                    }
                }
                case "Добавить подход отжиманий" -> {
                    sendMessage(chatId, "Введите количество отжиманий:");
                    userStateMap.put(userId, UserState.WAITING_PUSH_UP_COUNT);
                }
                case "Цели отжиманий" -> {
                    sendMessage(chatId, userService.getGoalsMessage(userEntity, ExerciseType.PUSH_UP));
                    sendGoalKeyboard(chatId, "Выберите действие:");
                    userStateMap.put(userId, UserState.IN_PUSH_UP_GOAL_MENU);
                }
                case "Установить дневную цель отжиманий" -> {
                    sendMessage(chatId, "Введите дневную цель отжиманий");
                    userStateMap.put(userId, UserState.SET_PUSH_UPS_DAY_GOAL);
                }
                case "Установить недельную цель отжиманий" -> {
                    sendMessage(chatId, "Введите недельную цель отжиманий");
                    userStateMap.put(userId, UserState.SET_PUSH_UPS_WEEK_GOAL);
                }
                case "Установить месячную цель отжиманий" -> {
                    sendMessage(chatId, "Введите месячную цель отжиманий");
                    userStateMap.put(userId, UserState.SET_PUSH_UPS_MONTH_GOAL);
                }
                case "Напоминания по отжиманиям" -> {
                    sendMessage(chatId, userService.switchNotification(userEntity, ExerciseType.PUSH_UP));
                }
                case "📊 Статистика" -> sendMessage(chatId, "Пока в разработке...");

                default -> {
                    if (state != null) {
                        switch (state) {
                            case WAITING_PUSH_UP_COUNT -> {
                                try {
                                    int count = Helper.parseCount(text);
                                    sendMessage(chatId, exerciseService.addExercise(userEntity, ExerciseType.PUSH_UP, count));
                                    sendMessage(chatId, exerciseService.getExerciseProgressMessage(userEntity, ExerciseType.PUSH_UP));
                                    userStateMap.put(userId, UserState.IN_PUSH_UP_MENU);
                                } catch (NumberFormatException e) {
                                    sendMessage(chatId, "Пожалуйста, введите число.");
                                }
                            }
                            case SET_PUSH_UPS_DAY_GOAL -> {
                                try {
                                    int count = Helper.parseCount(text);
                                    userService.setDayGoal(userEntity, ExerciseType.PUSH_UP, count);
                                    sendMessage(chatId, "Дневная цель отжиманий установлена!");
                                    sendMessage(chatId, userService.getGoalsMessage(userEntity, ExerciseType.PUSH_UP));
                                    sendGoalKeyboard(chatId, "Выберите действие:");
                                    userStateMap.put(userId, UserState.IN_PUSH_UP_GOAL_MENU);
                                } catch (NumberFormatException e) {
                                    sendMessage(chatId, "Пожалуйста, введите число.");
                                }
                            }
                            case SET_PUSH_UPS_WEEK_GOAL -> {
                                try {
                                    int count = Helper.parseCount(text);
                                    userService.setWeekGoal(userEntity, ExerciseType.PUSH_UP, count);
                                    sendMessage(chatId, "Недельная цель отжиманий установлена!");
                                    sendMessage(chatId, userService.getGoalsMessage(userEntity, ExerciseType.PUSH_UP));
                                    sendGoalKeyboard(chatId, "Выберите действие:");
                                    userStateMap.put(userId, UserState.IN_PUSH_UP_GOAL_MENU);
                                } catch (NumberFormatException e) {
                                    sendMessage(chatId, "Пожалуйста, введите число.");
                                }
                            }
                            case SET_PUSH_UPS_MONTH_GOAL -> {
                                try {
                                    int count = Helper.parseCount(text);
                                    userService.setMonthGoal(userEntity, ExerciseType.PUSH_UP, count);
                                    sendMessage(chatId, "Месячная цель отжиманий установлена!");
                                    sendMessage(chatId, userService.getGoalsMessage(userEntity, ExerciseType.PUSH_UP));
                                    sendGoalKeyboard(chatId, "Выберите действие:");
                                    userStateMap.put(userId, UserState.IN_PUSH_UP_GOAL_MENU);
                                } catch (NumberFormatException e) {
                                    sendMessage(chatId, "Пожалуйста, введите число.");
                                }
                            }
                            default -> {
                                sendMessage(chatId, "Неизвестная команда. Нажмите кнопку «ℹ️ Помощь» и изучите доступные команды.");
                            }
                        }
                    } else {
                        sendMessage(chatId, "Неизвестная команда. Нажмите кнопку «ℹ️ Помощь» и изучите доступные команды.");
                    }
                }
            }
        }
    }

    private String getHelpText() {
        return "Бот помогает вести учёт упражнений:\n\n" +
                "— Отжимания\n— Подтягивания\n— 📊 Статистика\n";
    }

    private void sendStartKeyboard(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Отжимания");
        row1.add("Подтягивания");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("ℹ️ Помощь");
        row2.add("📊 Статистика");

        keyboardMarkup.setKeyboard(List.of(row1, row2));
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendPushUpsKeyboard(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Добавить подход отжиманий");
        row1.add("Статистика по отжиманиям");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Цели отжиманий");
        row2.add("Назад");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Напоминания по отжиманиям");
        row3.add("Назад");

        keyboardMarkup.setKeyboard(List.of(row1, row2, row3));
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendGoalKeyboard(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Установить дневную цель отжиманий");
        row1.add("Установить недельную цель отжиманий");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Установить месячную цель отжиманий");
        row2.add("Назад");

        keyboardMarkup.setKeyboard(List.of(row1, row2));
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}