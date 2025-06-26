package com.tjxjnoobie.logging.context;

public record StackTraceContext(

    String className,
    String methodName,
    String fileName,
    int lineNumber
) {
    public StackTraceContext(StackTraceElement element) {
            this(
                    element.getClassName(),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber()
            );
        }

        @Override
        public String toString() {
            return "↳ " + className + "#" + methodName + " (" + fileName + ":" + lineNumber + ")";
        }
    }

