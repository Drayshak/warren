package engineer.carrot.warren.warren.event;

import engineer.carrot.warren.warren.irc.User;

public class PrivateActionEvent extends Event {
    public User fromUser;
    public String directedTo;
    public String contents;

    public PrivateActionEvent(User fromUser, String directedTo, String contents) {
        super();

        this.fromUser = fromUser;
        this.directedTo = directedTo;
        this.contents = contents;
    }
}
