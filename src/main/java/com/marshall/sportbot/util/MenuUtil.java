package com.marshall.sportbot.util;

import com.marshall.sportbot.enums.Command;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MenuUtil {
    public SendMessage getStartMenu(String chatId) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(createButton("Отжимания", Command.SEND_PUSH_UP_MENU.toString()));
        buttons.add(createButton("Подтягивания", Command.SEND_PULL_UP_MENU.toString()));
        buttons.add(createButton("Помощь", Command.SEND_HELP_MENU.toString()));
        buttons.add(createButton("Помощь", Command.SEND_HELP_MENU.toString()));

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите дальнейшее действие:");
        message.setReplyMarkup(groupButtons(buttons));
        return message;
    }

    public SendMessage getPushUpMenu(String chatId) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(createButton("Добавить подход", Command.SEND_PUSH_UP_ADD_MESSAGE.toString()));
        buttons.add(createButton("Статистика", Command.SEND_PUSH_UP_STATS_MESSAGE.toString()));
        buttons.add(createButton("Мои цели", Command.SEND_PUSH_UP_GOAL_MENU.toString()));
        buttons.add(createButton("Напоминания", Command.SWITCH_PUSH_UP_NOTIFICATION.toString()));
        buttons.add(createButton("Назад", Command.SEND_START_MENU.toString()));

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите дальнейшее действие:");
        message.setReplyMarkup(groupButtons(buttons));
        return message;
    }

    public SendMessage getPullUpMenu(String chatId) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(createButton("Добавить подход", Command.SEND_PULL_UP_ADD_MESSAGE.toString()));
        buttons.add(createButton("Статистика", Command.SEND_PULL_UP_STATS_MESSAGE.toString()));
        buttons.add(createButton("Мои цели", Command.SEND_PULL_UP_GOAL_MENU.toString()));
        buttons.add(createButton("Назад", Command.SEND_START_MENU.toString()));

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите дальнейшее действие:");
        message.setReplyMarkup(groupButtons(buttons));
        return message;
    }

    public SendMessage getPushUpGoalMenu(String chatId) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(createButton("На день", Command.SET_DAY_PUSH_UP_GOAL.toString()));
        buttons.add(createButton("На неделю", Command.SET_WEEK_PUSH_UP_GOAL.toString()));
        buttons.add(createButton("На месяц", Command.SET_MONTH_PUSH_UP_GOAL.toString()));
        buttons.add(createButton("Назад", Command.SEND_PUSH_UP_MENU.toString()));

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Вы можете установить/поменять свои цели отжиманий:");
        message.setReplyMarkup(groupButtons(buttons));
        return message;
    }

    private InlineKeyboardMarkup groupButtons(List<InlineKeyboardButton> buttons) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < buttons.size(); i += 2) {
            if (i + 1 < buttons.size()) {
                rows.add(List.of(buttons.get(i), buttons.get(i + 1)));
            } else {
                rows.add(List.of(buttons.get(i)));
            }
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}
