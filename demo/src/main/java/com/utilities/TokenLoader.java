package com.utilities;

import io.github.cdimascio.dotenv.Dotenv;

public class TokenLoader {

    public static final String token;

    static {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
        token = dotenv.get("PDFTRON_KEY");
    }
}