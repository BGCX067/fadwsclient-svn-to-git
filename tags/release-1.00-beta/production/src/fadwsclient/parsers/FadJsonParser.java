package fadwsclient.parsers;

import fadwsclient.FadParser;
import fadwsclient.FadResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.util.logging.Logger;

/**
 * Date: Dec 23, 2008
 *
 * @author Christian Hvid
 */

public class FadJsonParser implements FadParser {
    private static Logger logger = Logger.getLogger(FadJsonParser.class.getName());

    enum TokenType { START_OBJECT, END_OBJECT, STRING, NUMBER, NULL, COLON, COMMA, START_LIST, END_LIST }

    enum ParserState {
        INITIAL,

        BUILD_OBJECT_EXPECT_KEY,
        BUILD_OBJECT_EXPECT_COLON,
        BUILD_OBJECT_EXPECT_VALUE,
        BUILD_OBJECT_EXPECT_COMMA,

        BUILD_LIST_EXPECT_VALUE,
        BUILD_LIST_EXPECT_COMMA,

        EXPECT_END
    }

    public FadResponse parse(String data) {
        logger.fine("Data: " + data);

        Matcher m = Pattern.compile("([\\{\\}]|\"[^\"]*\"|null|\\d*\\.{0,1}\\d+|:|\\,|\\[|\\])").matcher(data);

        Stack<Object> stackObject = new Stack<Object>();
        Stack<ParserState> stackState = new Stack<ParserState>();

        ParserState currentState = ParserState.INITIAL;

        String currentKey = null;

        Object result = null;

        while (m.find()) {
            Object currentObject = null;
            if (stackObject.size() > 0) currentObject = stackObject.peek();
            String group = m.group();

            TokenType tokenType = TokenType.STRING;

            if (group.equals("[")) tokenType = TokenType.START_LIST;
            if (group.equals("]")) tokenType = TokenType.END_LIST;
            if (group.equals("{")) tokenType = TokenType.START_OBJECT;
            if (group.equals("}")) tokenType = TokenType.END_OBJECT;
            if (group.equals("null")) tokenType = TokenType.NULL;
            if (group.equals(":")) tokenType = TokenType.COLON;
            if (group.equals(",")) tokenType = TokenType.COMMA;
            if (group.matches("\\d*\\.{0,1}\\d+")) tokenType = TokenType.NUMBER;
            if (group.startsWith("\"")) {
                tokenType = TokenType.STRING;
                group = group.substring(1, group.length()-1);
            }

            switch (currentState) {
                case INITIAL:
                    switch (tokenType) {
                        case START_OBJECT:
                            result = new HashMap<String, Object>();
                            stackObject.push(result);
                            stackState.push(ParserState.EXPECT_END);
                            currentState = ParserState.BUILD_OBJECT_EXPECT_KEY;
                            break;
                        case NULL:
                            result = null;
                            stackObject.push(result);
                            stackState.push(ParserState.EXPECT_END);
                            currentState = ParserState.EXPECT_END;
                            break;
                        case NUMBER:
                            // result = Integer.parseInt(group);
                            result = group;
                            stackObject.push(result);
                            stackState.push(ParserState.EXPECT_END);
                            currentState = ParserState.EXPECT_END;
                            break;
                        case STRING:
                            result = group;
                            stackObject.push(result);
                            stackState.push(ParserState.EXPECT_END);
                            currentState = ParserState.EXPECT_END;
                            break;
                        case START_LIST:
                            result = new ArrayList<Object>();
                            stackObject.push(result);
                            stackState.push(ParserState.EXPECT_END);
                            currentState = ParserState.BUILD_LIST_EXPECT_VALUE;
                            break;
                        default:
                            throw new RuntimeException("Unexpected token '"+group+"' at "+m.start());
                    }
                    break;
                case BUILD_OBJECT_EXPECT_COLON:
                    switch (tokenType) {
                        case COLON:
                            currentState = ParserState.BUILD_OBJECT_EXPECT_VALUE;
                            break;
                        default:
                            throw new RuntimeException("Unexpected token '"+group+"' at "+m.start());
                    }
                    break;
                case BUILD_OBJECT_EXPECT_KEY:
                    switch (tokenType) {
                        case STRING:
                            currentKey = group;
                            currentState = ParserState.BUILD_OBJECT_EXPECT_COLON;
                            break;
                        default:
                            throw new RuntimeException("Unexpected token '"+group+"' at "+m.start());
                    }
                    break;
                case BUILD_OBJECT_EXPECT_VALUE:
                    switch (tokenType) {
                        case START_OBJECT:
                            stackObject.push(new HashMap<String, Object>());
                            stackState.push(ParserState.BUILD_OBJECT_EXPECT_COMMA);
                            currentState = ParserState.BUILD_OBJECT_EXPECT_KEY;
                            ((Map<String, Object>)currentObject).put(currentKey, stackObject.peek());
                            break;
                        case NULL:
                            currentState = ParserState.BUILD_OBJECT_EXPECT_COMMA;
                            ((Map<String, Object>)currentObject).put(currentKey, null);
                            break;
                        case NUMBER:
                            currentState = ParserState.BUILD_OBJECT_EXPECT_COMMA;
                            // ((Map<String, Object>)currentObject).put(currentKey, Integer.parseInt(group));
                            ((Map<String, Object>)currentObject).put(currentKey, group);
                            break;
                        case STRING:
                            currentState = ParserState.BUILD_OBJECT_EXPECT_COMMA;
                            ((Map<String, Object>)currentObject).put(currentKey, group);
                            break;
                        case END_OBJECT:
                            stackObject.pop();
                            currentState = stackState.pop();
                            break;
                        case START_LIST:
                            stackObject.push(new ArrayList<Object>());
                            stackState.push(ParserState.BUILD_OBJECT_EXPECT_COMMA);
                            currentState = ParserState.BUILD_LIST_EXPECT_VALUE;
                            ((Map<String, Object>)currentObject).put(currentKey, stackObject.peek());
                            break;
                        default:
                            throw new RuntimeException("Unexpected token '"+group+"' at "+m.start());
                    }
                    break;
                case BUILD_OBJECT_EXPECT_COMMA:
                    switch (tokenType) {
                        case COMMA:
                            currentState = ParserState.BUILD_OBJECT_EXPECT_KEY;
                            break;
                        case END_OBJECT:
                            stackObject.pop();
                            currentState = stackState.pop();
                            break;
                        default:
                            throw new RuntimeException("Unexpected token '"+group+"' at "+m.start());
                    }
                    break;
                case BUILD_LIST_EXPECT_VALUE:
                    switch (tokenType) {
                        case START_OBJECT:
                            stackObject.push(new HashMap<String, Object>());
                            stackState.push(ParserState.BUILD_LIST_EXPECT_COMMA);
                            currentState = ParserState.BUILD_OBJECT_EXPECT_KEY;
                            ((List<Object>)currentObject).add(stackObject.peek());
                            break;
                        case NULL:
                            currentState = ParserState.BUILD_LIST_EXPECT_COMMA;
                            ((List<Object>)currentObject).add(null);
                            break;
                        case NUMBER:
                            currentState = ParserState.BUILD_LIST_EXPECT_COMMA;
                            //((List<Object>)currentObject).add(Integer.parseInt(group));
                            ((List<Object>)currentObject).add(group);
                            break;
                        case STRING:
                            currentState = ParserState.BUILD_LIST_EXPECT_COMMA;
                            ((List<Object>)currentObject).add(group);
                            break;
                        case END_LIST:
                            stackObject.pop();
                            currentState = stackState.pop();
                            break;
                        case START_LIST:
                            stackObject.push(new ArrayList<Object>());
                            stackState.push(ParserState.BUILD_LIST_EXPECT_COMMA);
                            currentState = ParserState.BUILD_LIST_EXPECT_VALUE;
                            ((List<Object>)currentObject).add(stackObject.peek());
                            break;
                        default:
                            throw new RuntimeException("Unexpected token '"+group+"' at "+m.start());
                    }
                    break;
                case BUILD_LIST_EXPECT_COMMA:
                    switch (tokenType) {
                        case COMMA:
                            currentState = ParserState.BUILD_LIST_EXPECT_VALUE;
                            break;
                        case END_LIST:
                            stackObject.pop();
                            currentState = stackState.pop();
                            break;
                        default:
                            throw new RuntimeException("Unexpected token '"+group+"' at "+m.start());
                    }
                    break;
                default:
                    throw new RuntimeException("State not implemented: "+currentState);

            }
        }

        return new FadResponse(result);
    }
}
