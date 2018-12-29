import com.github.joostvdg.dui.logging.Logger;
import com.github.joostvdg.dui.logging.impl.ColoredSTDOutLogger;
import com.github.joostvdg.dui.logging.impl.JsonSTDOutLogger;

module com.github.joostvdg.dui.logging {
    exports com.github.joostvdg.dui.logging;
    provides Logger with ColoredSTDOutLogger, JsonSTDOutLogger;
}
