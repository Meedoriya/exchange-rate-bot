package com.alibi.exchange_rates_bot.inter;

import com.alibi.exchange_rates_bot.exception.ServiceException;

public interface ExchangeRatesInterface {

    String getUSDExchangeRate() throws ServiceException;
    String getEURExchangeRate() throws ServiceException;


}
