package at.dici.shade.utils.debug;

import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;

public class SErrorHandler {

    public static void handleRestActionError(Throwable t){
        Logger.log(LogLevel.ERROR, "RestAction Error:");
        StringBuilder logBuilder = new StringBuilder("RestAction Error:");
        Throwable cause = t.getCause();
        logBuilder.append(t);
        for (StackTraceElement element : t.getStackTrace()){
            logBuilder.append(appendElement(element));
        }
        if (cause != null) {
            logBuilder.append("\nCause: ").append(cause);
            for (StackTraceElement element : cause.getStackTrace()) {
                logBuilder.append(appendElement(element));
            }
        }
        Logger.detailedLog(LogLevel.ERROR, logBuilder.toString());
    }


    private static final String origin5 = SErrorHandler.class.getName().substring(0,5);
    private static String appendElement(StackTraceElement element) {
        if (element.getClassName().replaceAll("\\[", "").startsWith(origin5)) {
            Logger.error("  -> " + element);
            return "\n -> " + element;
        } else {
            return "\n    " + element;
        }
    }

}

