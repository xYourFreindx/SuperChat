package xyz.nickr.superbot.sys.skype;

import java.util.HashMap;
import java.util.Map;

import in.kyle.ezskypeezlife.EzSkype;
import in.kyle.ezskypeezlife.api.conversation.SkypeConversation;
import in.kyle.ezskypeezlife.api.skype.SkypeCredentials;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeSys implements Sys {

    private final Map<String, GroupConfiguration> configs = new HashMap<>();

    public EzSkype skype;

    public SkypeSys(String username, String password) {
        new Thread(() -> {
            long now = System.currentTimeMillis();
            System.out.println("Loading SuperBot: Skype");
            try {
                SkypeListener listener = new SkypeListener(this);
                this.skype = new EzSkype(new SkypeCredentials(username, password));
                this.skype.setErrorHandler(listener);
                this.skype.login();
                this.skype.getEventManager().registerEvents(listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Done SuperBot: Skype (" + (System.currentTimeMillis() - now) + "ms)");
        }).start();
    }

    @Override
    public String getName() {
        return "Skype";
    }

    @Override
    public String prefix() {
        return "+";
    }

    @Override
    public boolean isUIDCaseSensitive() {
        return false;
    }

    @Override
    public MessageBuilder message() {
        return new HtmlMessageBuilder();
    }

    @Override
    public String getUserFriendlyName(String uniqueId) {
        return uniqueId;
    }

    @Override
    public GroupConfiguration getGroupConfiguration(String uniqueId) {
        return this.configs.get(uniqueId);
    }

    @Override
    public void addGroupConfiguration(GroupConfiguration cfg) {
        this.configs.put(cfg.getUniqueId(), cfg);
    }

    Group wrap(SkypeConversation group) {
        return new SkypeGroup(this, group);
    }

    User wrap(in.kyle.ezskypeezlife.api.user.SkypeUser user) {
        return new SkypeUser(this, user);
    }

    Message wrap(in.kyle.ezskypeezlife.api.conversation.message.SkypeMessage message) {
        return new SkypeMessage(this, message);
    }

}
