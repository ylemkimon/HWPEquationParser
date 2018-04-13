//package kim.ylem.heparser;
//
//import kim.ylem.heparser.atoms.Atom;
//import kim.ylem.heparser.atoms.Group;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class Parser {
//    private static boolean DEBUG = true;
//
//    private static Pattern WHITESPACE = Pattern.compile("[\\s`]+");
//    private static Pattern SPACE = Pattern.compile("~");
//    private static Pattern TOKEN = Pattern.compile("-[+>]|[!<=>]=|\\+-|<<<?|>>>?|<-(>|$|(?=[\\s!-={]))|\\p{Alpha}+|.", Pattern.DOTALL);
//    private static Pattern DIGIT = Pattern.compile("\\p{Digit}");
//    private static Pattern ASCII = Pattern.compile("\\p{ASCII}");
//
//    public String equation;
//    private Group root;
//    private int pos = -1;
//
//    public Parser(String equation) {
//        this.equation = equation;
//        if (DEBUG) System.out.println("Parsing equation: " + equation);
//        this.root = parseGroup();
//    }
//
//    private String getCurrent() {
//        return equation.substring(pos, pos + 1);
//    }
//
//    private String getAfter() {
//        return equation.substring(pos);
//    }
//
//    private boolean isEOF() {
//        return pos >= equation.length();
//    }
//
//    private void consumeSpaces() {
//        Matcher spaceMatcher = WHITESPACE.matcher(getAfter());
//        if (spaceMatcher.lookingAt()) {
//            pos += spaceMatcher.end();
//        }
//    }
//
//    private Group parseGroup() {
//        String expect = null;
//
//        if (pos == -1) {
//            pos++;
//            expect = "EOF";
//        } else {
//            consumeSpaces();
//            if (isEOF()) {
//                throw new ParseError("group", "EOF");
//            } else if ("{".equals(getCurrent())) {
//                pos++;
//                expect = "}";
//            }
//        }
//
//        Group group = new Group();
//        if (expect != null) {
//            if (DEBUG) System.out.println("Parsing a group of multiple at " + pos);
//            Atom child;
//            while ((child = parseAtom()) != null) {
//                group.addChild(child);
//            }
//
//            consumeSpaces();
//            if (isEOF()) {
//                if (!"EOF".equals(expect)) {
//                    throw new ParseError(expect, "EOF");
//                }
//            } else {
//                String current = getCurrent();
//                if (!current.equals(expect)) {
//                    throw new ParseError(expect, current);
//                }
//                pos++;
//            }
//        } else {
//            if (DEBUG) System.out.println("Parsing a group of single at " + pos);
//            group.addChild(parseAtom());
//        }
//
//        return group;
//    }
//
//    private Atom parseAtom() {
//        consumeSpaces();
//        if (isEOF()) {
//            return null;
//        }
//
//        Matcher tokenMatcher = TOKEN.matcher(getAfter());
//        if (!tokenMatcher.find()) {
//            throw new ParseError("atoms", "invalid");
//        }
//        String token = tokenMatcher.group(0);
//        if (DEBUG) System.out.println("Matched token: " + token);
//
//        switch (token) {
//            case "{":
//                return parseGroup();
//            case "}":
//                pos++;
//                return null;
//        }
//        return null;
//    }
//
//    public String toLaTeX() {
//        return root.toLaTeX();
//    }
//}
