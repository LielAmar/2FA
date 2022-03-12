package com.lielamar.auth.shared.utils;

import com.lielamar.lielsutils.groups.Pair;

public class Constants {

    public static final Pair<String, String> mainCommand = new Pair<>("2fa", "2fa.use");
    public static final Pair<String, String> enableCommand = new Pair<>("enable", "2fa.use");
    public static final Pair<String, String> loginCommand = new Pair<>("login", "2fa.use");
    public static final Pair<String, String> setupCommand = new Pair<>("setup", "2fa.use");
    public static final Pair<String, String> disableCommand = new Pair<>("disable", "2fa.remove");
    public static final Pair<String, String> disableForOthersCommand = new Pair<>("", "2fa.remove.others");
    public static final Pair<String, String> cancelCommand = new Pair<>("cancel", "2fa.cancel");
    public static final Pair<String, String> reloadCommand = new Pair<>("reload", "2fa.reload");
    public static final Pair<String, String> reportCommand = new Pair<>("report", "2fa.report");
    public static final Pair<String, String> helpCommand = new Pair<>("help", "2fa.help");

    public static final String alertsPermission = "2fa.alerts";
    public static final String demandPermission = "2fa.demand";

    public static final String PROXY_CHANNEL_NAME = "2fa:2fa";
    public static final String PROXY_SUB_CHANNEL_NAME = "ForwardToPlayer";
}