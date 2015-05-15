package com.xzero.refreshreply.helpers;

/**
 * Simple class to manipulate addresses
 */
public class AddressUtil {

    /**
     * @param address
     * @return
     */
    public static String getConcatenatedCityFromAddress(String address) {

        return address.replace(" ", "_").split(",")[0];
    }

    /**
     * @param address
     * @return
     */
    public static String stripCountryFromAddress(String address) {

        return address.split(",")[0];
    }
}
