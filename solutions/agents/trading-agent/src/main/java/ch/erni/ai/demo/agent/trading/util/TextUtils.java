package ch.erni.ai.demo.agent.trading.util;

/**
 * Text utility class for formatting console output
 */
public class TextUtils {
    
    /**
     * Wraps text at word boundaries to fit within specified line length
     * Preserves existing line breaks and handles multi-line text properly
     * 
     * @param text The text to wrap
     * @param maxLineLength Maximum characters per line (default: 80)
     * @return Wrapped text with line breaks
     */
    public static String wrapText(String text, int maxLineLength) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        if (maxLineLength <= 0) {
            maxLineLength = 80; // Default to 80 characters
        }
        
        StringBuilder wrapped = new StringBuilder();
        String[] existingLines = text.split("\n"); // Preserve existing line breaks
        
        for (int lineIndex = 0; lineIndex < existingLines.length; lineIndex++) {
            String line = existingLines[lineIndex];
            
            if (line.length() <= maxLineLength) {
                // Line is already short enough
                wrapped.append(line);
            } else {
                // Need to wrap this line
                String wrappedLine = wrapSingleLine(line, maxLineLength);
                wrapped.append(wrappedLine);
            }
            
            // Add line break except for last line
            if (lineIndex < existingLines.length - 1) {
                wrapped.append("\n");
            }
        }
        
        return wrapped.toString();
    }
    
    /**
     * Wraps text with default line length of 80 characters
     */
    public static String wrapText(String text) {
        return wrapText(text, 80);
    }
    
    /**
     * Wraps a single line at word boundaries
     */
    private static String wrapSingleLine(String line, int maxLineLength) {
        if (line.length() <= maxLineLength) {
            return line;
        }
        
        StringBuilder wrapped = new StringBuilder();
        String[] words = line.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            // Check if adding this word would exceed line length
            int neededLength = currentLine.length() + (currentLine.length() > 0 ? 1 : 0) + word.length();
            
            if (neededLength > maxLineLength && currentLine.length() > 0) {
                // Start new line
                wrapped.append(currentLine.toString().trim()).append("\n");
                currentLine = new StringBuilder();
            }
            
            // Add word to current line
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }
        
        // Add remaining content
        if (currentLine.length() > 0) {
            wrapped.append(currentLine.toString().trim());
        }
        
        return wrapped.toString();
    }
    
    /**
     * Formats text with a specific console width, adding padding and borders
     */
    public static String formatConsoleOutput(String text, int consoleWidth) {
        String wrapped = wrapText(text, consoleWidth - 4); // Leave room for padding
        StringBuilder formatted = new StringBuilder();
        
        String[] lines = wrapped.split("\n");
        for (String line : lines) {
            formatted.append("  ").append(line).append("\n");
        }
        
        return formatted.toString();
    }
    
    /**
     * Truncates text if it's too long, adding ellipsis
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        
        if (maxLength <= 3) {
            return "...";
        }
        
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Centers text within a given width
     */
    public static String centerText(String text, int width) {
        if (text == null || text.length() >= width) {
            return text;
        }
        
        int padding = (width - text.length()) / 2;
        StringBuilder centered = new StringBuilder();
        
        // Add left padding
        for (int i = 0; i < padding; i++) {
            centered.append(" ");
        }
        
        centered.append(text);
        
        // Add right padding if needed
        while (centered.length() < width) {
            centered.append(" ");
        }
        
        return centered.toString();
    }
    
    /**
     * Creates a separator line for console output
     */
    public static String createSeparator(int width, char character) {
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i < width; i++) {
            separator.append(character);
        }
        return separator.toString();
    }
    
    /**
     * Creates a separator line with default character (=)
     */
    public static String createSeparator(int width) {
        return createSeparator(width, '=');
    }
}
