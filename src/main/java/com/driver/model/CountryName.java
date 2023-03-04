package com.driver.model;

public enum CountryName {
    IND("001"),
    USA("002"),
    AUS("003"),
    CHI("004"),
    JPN("005");

    private final String code;

    private CountryName(String s) {
        code = s;
    }

    public String toCode() {
        return this.code;
    }

    public static boolean isValid(String countryName){
        boolean found = false;
        for (CountryName name : CountryName.values()) {
            if (name.name().equalsIgnoreCase(countryName)) {
                found = true;
            }
        }
        return found;
    }

    public static String getCountryName(String countryCode){
        for (CountryName name : CountryName.values()) {
            if (name.toCode().equalsIgnoreCase(countryCode)) {
                return name.name();
            }
        }
        return null;
    }


}
