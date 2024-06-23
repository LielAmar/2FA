package com.lielamar.auth.utils;

import dev.sadghost.espresso.groups.Pair;

public class Constants {

    // Commands
    public static final Pair<String, String> TWO_FA_MAIN_COMMAND = new Pair<>("2fa", "2fa.use");
    public static final Pair<String, String> ENABLE_COMMAND = new Pair<>("enable", "2fa.use");
    public static final Pair<String, String> LOGIN_COMMAND = new Pair<>("login", "2fa.use");
    public static final Pair<String, String> SETUP_COMMAND = new Pair<>("setup", "2fa.use");
    public static final Pair<String, String> DISABLE_COMMAND = new Pair<>("disable", "2fa.remove");
    public static final Pair<String, String> CANCEL_COMMAND = new Pair<>("cancel", "2fa.cancel");
    public static final Pair<String, String> RELOAD_COMMAND = new Pair<>("reload", "2fa.reload");
    public static final Pair<String, String> REPORT_COMMAND = new Pair<>("report", "2fa.report");
    public static final Pair<String, String> HELP_COMMAND = new Pair<>("help", "2fa.help");

    // Permissions
    public static final String ALERTS_PERMISSION = "2fa.alerts";
    public static final String DEMAND_PERMISSION = "2fa.demand";

    // Proxy Channel Names
    public static final String PROXY_CHANNEL_NAME = "2fa:2fa";
    public static final String PROXY_SUB_CHANNEL_NAME = "ForwardToPlayer";
}
