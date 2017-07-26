package com.example.howoongjun.myapplication;

/**
 * Created by howoongjun on 2017. 7. 26..
 */

public class Conf {
    /**
     * Is release?
     */
    public static final boolean ISRELEASE	= false;

    /**
     * The type of config
     */
    public static final boolean ISCONFIG	= ISRELEASE ? true : false;


    /**
     * The config of dev server
     */
    private class TestConfig {
        private static final String SITE_DOMAIN_URL 		= "http://smartbookshelf.teamnexters.com";
        private static final String APPLICATION_ID          = "com.books.share.smartbookshelf";

    }

    /**
     * The config of real server
     */
    private class RealConfig {
        private static final String SITE_DOMAIN_URL 		= "http://smartbookshelf.teamnexters.com";
        private static final String APPLICATION_ID          = "com.books.share.smartbookshelf";
    }

    /**
     * Server Url
     */
    public static final String SITE_DOMAIN_URL = ISCONFIG ? RealConfig.SITE_DOMAIN_URL : TestConfig.SITE_DOMAIN_URL;
    public static final String APPLICATION_ID = ISCONFIG ? RealConfig.APPLICATION_ID : TestConfig.APPLICATION_ID;

}