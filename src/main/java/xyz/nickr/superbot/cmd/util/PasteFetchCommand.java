package xyz.nickr.superbot.cmd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class PasteFetchCommand implements Command {

    public static Pattern PASTEBIN = Pattern.compile("https?://(?:www\\.)?pastebin\\.com/(?:raw/)?([a-zA-Z0-9]+)");

    @Override
    public String[] names() {
        return new String[]{ "pastefetch" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[url]", "fetches paste from URL" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            sendUsage(sys, user, group);
            return;
        }
        MessageBuilder<?> mb = sys.message();
        String url = Joiner.join(" ", args);
        Matcher m;
        if ((m = PASTEBIN.matcher(url)).matches()) {
            String id = m.group(1);
            url = "http://pastebin.com/raw/" + id;
            try {
                URLConnection conn = new URL(url).openConnection();
                String type = conn.getContentType();
                if (type != null && type.startsWith("text/plain")) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        mb.bold(true).escaped("Paste data:").bold(false);
                        reader.lines().forEach(line -> mb.newLine().escaped(line));
                    } catch (Exception ex) {
                        mb.escaped("An error occurred: " + ex.getClass().getSimpleName());
                    }
                } else {
                    mb.escaped("Invalid pastebin ID! :(");
                }
            } catch (IOException ex) {
                mb.escaped("An error occurred: " + ex.getClass().getSimpleName());
            }
        } else {
            mb.escaped("That URL does not match any providers.");
        }
        group.sendMessage(mb);
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public boolean userchat() {
        return true;
    }

}
