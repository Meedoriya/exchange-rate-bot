package com.alibi.exchange_rates_bot.bot;

import com.alibi.exchange_rates_bot.exception.ServiceException;
import com.alibi.exchange_rates_bot.inter.ExchangeRatesInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {


    public static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);

    public static final String START = "/start";
    public static final String USD = "/usd";
    public static final String EUR = "/eur";
    public static final String HELP = "/help";

    final
    ExchangeRatesInterface exchangeRatesInterface;

    public ExchangeRatesBot(@Value("${bot.token}") String botToken, ExchangeRatesInterface exchangeRatesInterface) {
        super(botToken);
        this.exchangeRatesInterface = exchangeRatesInterface;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatID = update.getMessage().getChatId();
        switch (message) {
            case START -> {
                var userName = update.getMessage().getChat().getUserName();
                startCommand(chatID, userName);
            }
            case USD -> usdCommand(chatID);
            case EUR -> eurCommand(chatID);
            case HELP -> helpCommand(chatID);
            default -> unknownCommand(chatID);
        }
    }

    @Override
    public String getBotUsername() {
        return "alibi_er_bot";
    }

    private void usdCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesInterface.getUSDExchangeRate();
            var text = "Курс доллара на %s составляет %s рубль";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException e) {
            LOG.error("Error during sending dollar exchange rate", e);
            formattedText = "Не удалось получить текущий курс доллара. Попробуйте позже.";
        }
        sendMessage(chatId, formattedText);
    }

    private void eurCommand(Long chatId) {
        String formattedText;
        try {
            var eur = exchangeRatesInterface.getEURExchangeRate();
            var text = "Курс евро на %s составляет %s рубль";
            formattedText = String.format(text, LocalDate.now(), eur);
        } catch (ServiceException e) {
            LOG.error("Error during sending euro exchange rate", e);
            formattedText = "Не удалось получить текущий курс евро. Попробуйте позже.";
        }
        sendMessage(chatId, formattedText);
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду";
        sendMessage(chatId, text);
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот,  %s!
                                
                Здесь Вы сможете узнать официальные курсы валют на сегодня.
                                
                Для этого воспульзуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                                
                Дополнительные команды:
                /help - получение справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        var text = """
                Справочная информация по боту

                Для получения текущих курсов валют воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                """;
        sendMessage(chatId, text);
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Failed sending message", e);
        }
    }
}
