package dev.vuis.ldc.erroralerts;

import io.github.lightman314.lightmansdiscord.LDIConfig;
import io.github.lightman314.lightmansdiscord.discord.listeners.console.ConsoleMessageListener;
import io.github.lightman314.lightmansdiscord.message.MessageManager;
import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;

public class ErrorCallbackAppender extends AbstractAppender {
	protected ErrorCallbackAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
		super(name, filter, layout, true, Property.EMPTY_ARRAY);
	}

	@Override
	public void append(LogEvent event) {
		if (event.getLevel() != Level.ERROR) {
			return;
		}

		String message = event.getMessage().getFormattedMessage();

		for (String keyword : LDIConfig.SERVER.errorAlertKeywords.get()) {
			if (message.contains(keyword)) {
				ConsoleMessageListener.instance.sendMessage(MessageManager.M_CONSOLEBOT_ERROR.format(message));
			}
		}
	}

	public static void init() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();

		Appender appender = new ErrorCallbackAppender("LDIErrorCallback", null, null);
		appender.start();
		config.addAppender(appender);

		config.getRootLogger().addAppender(appender, Level.ERROR, null);
		ctx.updateLoggers();
	}
}
