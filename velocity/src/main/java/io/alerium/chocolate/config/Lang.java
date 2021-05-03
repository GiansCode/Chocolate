package io.alerium.chocolate.config;

import io.alerium.chocolate.config.loader.EnumConfigLoader;
import io.alerium.chocolate.utils.StringUtils;

public enum Lang implements EnumConfigLoader.EnumConfig {

    GENERIC$NO_PERMISSIONS("&c&lChocolate &8» &cYou cannot use this command."),
    GENERIC$WRONG_USAGE("&c&lChocolate &8» &cWrong usage, usage: &f%usage%"),
    GENERIC$PLAYER_NOT_FOUND("&c&lChocolate &8» &f%player% &ccannot be found."),
    GENERIC$PROXY_NOT_FOUND("&c&lChocolate &8» &cCannot find a proxy with the name: &f%proxy%&c."),
    GENERIC$MUST_BE_A_PLAYER("&c&lChocolate &8» &cYou must be a player to use this command"),

    STARTING$JOIN("&c&lChocolate &8» &cCannot join while the server hasn't been fully started yet."),

    COMMAND$FIND$FOUND("&c&lChocolate &8» &f%player% &7is found in &f%server% &7in proxy &f%proxy%&7."),

    COMMAND$GLIST$GLOBAL("&c&lChocolate &8» &7There are currently &f%online% &7players globally connected."),
    COMMAND$GLIST$PROXY("&c&lChocolate &8» &7There are currently &f%online% &7players connected to proxy &f%proxy%."),

    COMMAND$IP$FOUND("&c&lChocolate &8» &f%player% &7his ip is &f%ip%"),

    COMMAND$LAST_SEEN$FOUND("&c&lChocolate &8» &f%player% &7was last seen &f%seen% &7ago"),

    COMMAND$PROXY_ID$ID("&c&lChocolate &8» &7You are on proxy &f%proxyId%"),

    COMMAND$PROXY_ID_LIST$LIST("&c&lChocolate &8» &7Proxies: &f%proxies%"),

    COMMAND$ALERT$FORMAT("&8[&cAlert&8] &f%message%"),

    COMMAND$CHOCOLATE$COMMAND_FORMAT("&c%usage% &8- &f%description%"),
    COMMAND$CHOCOLATE$MSG("" +
            "&8&m--------------&r&8{ &c&lChocolate &8}&m--------------\n" +
            " &f* &cAuthors: &f%authors%\n" +
            " &f* &cGithub: &fhttps://github.com/GiansCode/Chocolate\n" +
            "\n" +
            "&c&lCommands &8»\n" +
            "%commands%" +
            "&8&m--------------&r&8{ &c&lChocolate &8}&m--------------"
    ),
    ;

    private String string;

    Lang(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    @Override
    public Object getObject() {
        return this.string;
    }

    @Override
    public void setObject(Object value) {
        this.string = (String) value;
    }

    @Override
    public String getPath() {
        return StringUtils.getInstance().replacePlaceholders(this.name().toLowerCase(), "$", ".", "_", "-");
    }
}
