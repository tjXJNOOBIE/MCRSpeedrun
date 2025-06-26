package com.tjxjnoobie.logging;

public record StackTraceInfo (

    String className,
    String methodName,
    String fileName,
    int lineNumber
) {
    public StackTraceInfo(StackTraceElement element) {
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

