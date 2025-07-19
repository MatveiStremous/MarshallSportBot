package com.marshall.sportbot.controller;

import com.marshall.sportbot.entity.UserEntity;
import com.marshall.sportbot.enums.Command;
import com.marshall.sportbot.enums.ExerciseType;
import com.marshall.sportbot.enums.UserState;
import com.marshall.sportbot.service.ExerciseService;
import com.marshall.sportbot.service.Helper;
import com.marshall.sportbot.service.UserService;
import com.marshall.sportbot.util.MenuUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateController {
    private final UserService userService;
    private final ExerciseService exerciseService;
    private final Map<Long, UserState> userStateMap = new ConcurrentHashMap<>();

    private SportBot sportBot;

    public void registerBot(SportBot sportBot) {
        this.sportBot = sportBot;
    }

    public void processUpdate(Update update) {
        log.info(String.valueOf(update));
        if (!isUpdateValid(update)) {
            return;
        }

        if (update.hasCallbackQuery()) {
            processCallBackQuery(update);
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            processDefaultMessage(update);
        }
    }

    public void sendMessage(SendMessage message) {
        sportBot.sendMessage(message);
    }

    private void processDefaultMessage(Update update) {
        String text = update.getMessage().getText().trim();
        String chatId = update.getMessage().getChatId().toString();
        Long userId = update.getMessage().getFrom().getId();
        UserEntity userEntity = userService.getUser(update.getMessage().getFrom(), chatId);

        UserState state = userStateMap.get(userId);

        switch (text) {
            case "/start" -> sendMessage(MenuUtil.getStartMenu(chatId));
            case "/help" -> {
                sendMessage(getHelpMessage(chatId));
                sendMessage(MenuUtil.getStartMenu(chatId));
            }
            default -> {
                if (state == null) {
                    sendMessage(new SendMessage(chatId, "Воспользуйтесь кнопками на клавиатуре!\nЕсли не сработает - перезагрузите бота с помощью команды /start"));
                    return;
                }
                switch (state) {
                    case WAITING_PUSH_UP_COUNT -> {
                        try {
                            int count = Helper.parseCount(text);
                            String answerText = exerciseService.addExercise(userEntity, ExerciseType.PUSH_UP, count);
                            sendMessage(new SendMessage(chatId, answerText));
                            String progressMessage = exerciseService.getExerciseProgressMessage(userEntity, ExerciseType.PUSH_UP);
                            sendMessage(new SendMessage(chatId, progressMessage));
                            sendMessage(MenuUtil.getPushUpMenu(chatId));
                            userStateMap.remove(userId);
                        } catch (NumberFormatException e) {
                            sendMessage(getNumberFormatReqMessage(chatId));
                        }
                    }
                    case WAITING_PUSH_UP_DAY_GOAL -> {
                        try {
                            int count = Helper.parseCount(text);
                            userService.setDayGoal(userEntity, ExerciseType.PUSH_UP, count);
                            sendMessage(new SendMessage(chatId, "Дневная цель отжиманий установлена!"));
                            sendMessage(new SendMessage(chatId, userService.getGoalsMessage(userEntity, ExerciseType.PUSH_UP)));
                            sendMessage(MenuUtil.getPushUpGoalMenu(chatId));
                            userStateMap.remove(userId);
                        } catch (NumberFormatException e) {
                            sendMessage(getNumberFormatReqMessage(chatId));
                        }
                    }
                    case WAITING_PUSH_UP_WEEK_GOAL -> {
                        try {
                            int count = Helper.parseCount(text);
                            userService.setWeekGoal(userEntity, ExerciseType.PUSH_UP, count);
                            sendMessage(new SendMessage(chatId, "Недельная цель отжиманий установлена!"));
                            sendMessage(new SendMessage(chatId, userService.getGoalsMessage(userEntity, ExerciseType.PUSH_UP)));
                            sendMessage(MenuUtil.getPushUpGoalMenu(chatId));
                            userStateMap.remove(userId);
                        } catch (NumberFormatException e) {
                            sendMessage(getNumberFormatReqMessage(chatId));
                        }
                    }
                    case WAITING_PUSH_UP_MONTH_GOAL -> {
                        try {
                            int count = Helper.parseCount(text);
                            userService.setMonthGoal(userEntity, ExerciseType.PUSH_UP, count);
                            sendMessage(new SendMessage(chatId, "Месячная цель отжиманий установлена!"));
                            sendMessage(new SendMessage(chatId, userService.getGoalsMessage(userEntity, ExerciseType.PUSH_UP)));
                            sendMessage(MenuUtil.getPushUpGoalMenu(chatId));
                            userStateMap.remove(userId);
                        } catch (NumberFormatException e) {
                            sendMessage(getNumberFormatReqMessage(chatId));
                        }
                    }
                }
            }
        }
    }

    private void processCallBackQuery(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        Long userId = update.getCallbackQuery().getFrom().getId();
        UserEntity userEntity = userService.getUser(update.getCallbackQuery().getFrom(), chatId);
        userStateMap.remove(userId);
        Command enumCommand = getCommandFromData(update.getCallbackQuery().getData());
        if (enumCommand != null) {
            switch (enumCommand) {
                case SEND_HELP_MENU -> {
                    sendMessage(getHelpMessage(chatId));
                    sendMessage(MenuUtil.getStartMenu(chatId));
                }
                case SEND_PUSH_UP_MENU -> {
                    sendMessage(MenuUtil.getPushUpMenu(chatId));
                }
                case SEND_START_MENU -> {
                    sendMessage(MenuUtil.getStartMenu(chatId));
                }
                case SEND_PUSH_UP_GOAL_MENU -> {
                    sendMessage(new SendMessage(chatId, userService.getGoalsMessage(userEntity, ExerciseType.PUSH_UP)));
                    sendMessage(MenuUtil.getPushUpGoalMenu(chatId));
                }
                case SEND_PUSH_UP_STATS_MESSAGE -> {
                    String progressMessage = exerciseService.getExerciseStatsMessage(userEntity, ExerciseType.PUSH_UP);
                    sendMessage(new SendMessage(chatId, progressMessage));
                    sendMessage(MenuUtil.getPushUpMenu(chatId));
                }
                case SEND_PUSH_UP_ADD_MESSAGE -> {
                    sendMessage(new SendMessage(chatId, "Введите выполненное количество отжиманий:"));
                    userStateMap.put(userId, UserState.WAITING_PUSH_UP_COUNT);
                }
                case SET_DAY_PUSH_UP_GOAL -> {
                    sendMessage(new SendMessage(chatId, "Введите цель отжиманий на день:"));
                    userStateMap.put(userId, UserState.WAITING_PUSH_UP_DAY_GOAL);
                }
                case SET_WEEK_PUSH_UP_GOAL -> {
                    sendMessage(new SendMessage(chatId, "Введите цель отжиманий на неделю:"));
                    userStateMap.put(userId, UserState.WAITING_PUSH_UP_WEEK_GOAL);
                }
                case SET_MONTH_PUSH_UP_GOAL -> {
                    sendMessage(new SendMessage(chatId, "Введите цель отжиманий на месяц:"));
                    userStateMap.put(userId, UserState.WAITING_PUSH_UP_MONTH_GOAL);
                }
                case SWITCH_PUSH_UP_NOTIFICATION -> {
                    sendMessage(new SendMessage(chatId, userService.switchNotification(userEntity, ExerciseType.PUSH_UP)));
                    sendMessage(MenuUtil.getPushUpMenu(chatId));
                }
                default -> {
                    sendMessage(getDevMessage(chatId));
                }
            }
        }
    }

    private boolean isUpdateValid(Update update) {
        return (update.hasMessage() && update.getMessage().hasText()) || update.hasCallbackQuery();
    }

    private Command getCommandFromData(String command) {
        try {
            return Command.valueOf(command);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private SendMessage getHelpMessage(String chatId) {
        String text = "Бот помогает вести учёт упражнений по отжиманиям и подтягиваниям.\n\n" +
                "Если нашли баг - пишите @matvei_stremous";
        return new SendMessage(chatId, text);
    }

    private SendMessage getDevMessage(String chatId) {
        String text = "В разработке...";
        return new SendMessage(chatId, text);
    }

    private SendMessage getNumberFormatReqMessage(String chatId) {
        return new SendMessage(chatId, "Пожалуйста, введите число.");
    }
}
