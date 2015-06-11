package engineer.carrot.warren.warren.irc.handlers.RPL.isupport;

import com.google.common.collect.Maps;
import engineer.carrot.warren.warren.UserManager;
import engineer.carrot.warren.warren.irc.handlers.MessageHandler;
import engineer.carrot.warren.warren.irc.messages.RPL.ISupportMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ISupportHandler extends MessageHandler<ISupportMessage> implements IISupportManager {
    private final Logger LOGGER = LoggerFactory.getLogger(ISupportHandler.class);
    private final Map<String, String> supports;
    private final Map<String, IISupportModule> modules;

    // Modules
    private final IPrefixSupportModule prefixSupportModule;

    public ISupportHandler(UserManager userManager) {
        this.supports = Maps.newHashMap();
        this.modules = Maps.newHashMap();

        this.prefixSupportModule = new PrefixSupportModule(userManager);
        this.modules.put("PREFIX", this.prefixSupportModule);
    }

    @Override
    public void handleMessage(ISupportMessage message) {
        LOGGER.info("Server supports: ");

        for (Map.Entry<String, String> entry : message.parameters.entrySet()) {
            String parameter = entry.getKey();
            String value = entry.getValue();

            LOGGER.info("{}: {}", parameter, value);

            if (this.modules.containsKey(parameter)) {
                boolean handled = this.modules.get(parameter).handleValue(value);
                if (!handled) {
                    LOGGER.warn("Failed to handle parameter '{}' -> '{}'", parameter, value);
                }
            }
        }

        supports.putAll(message.parameters);
    }

    // IISupportManager

    @Override
    public IPrefixSupportModule getPrefixModule() {
        return this.prefixSupportModule;
    }
}
