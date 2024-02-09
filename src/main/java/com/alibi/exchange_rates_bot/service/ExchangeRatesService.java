package com.alibi.exchange_rates_bot.service;


import com.alibi.exchange_rates_bot.client.CbClient;
import com.alibi.exchange_rates_bot.exception.ServiceException;
import com.alibi.exchange_rates_bot.inter.ExchangeRatesInterface;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import org.w3c.dom.Document;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Service
public class ExchangeRatesService implements ExchangeRatesInterface {

    final
    CbClient client;

    public ExchangeRatesService(CbClient client) {
        this.client = client;
    }

    public static final String USD_XPATH = "/ValCurs/Valute[@ID='R01235']/Value";
    public static final String EUR_XPATH = "/ValCurs/Valute[@ID='R01239']/Value";



    @Override
    public String getUSDExchangeRate() throws ServiceException {
        var xml = client.getCurrencyRatesXml();
        return extractCurrencyValueFromXML(xml, USD_XPATH);
    }

    @Override
    public String getEURExchangeRate() throws ServiceException {
        var xml = client.getCurrencyRatesXml();
        return extractCurrencyValueFromXML(xml, EUR_XPATH);
    }

    public static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServiceException("Failed xml pars", e);
        }
    }
}
