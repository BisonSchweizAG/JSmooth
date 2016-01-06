// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the LGPL 3.0 license available at http://www.gnu.org/licenses/lgpl.txt

package net.charabia.jsmoothgen.pe.res.validate;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Apparently there are still a few things that haven't yet been packed into java.lang.String!
 *
 * @author David A. Solin
 * @version %I% %G%
 * @since 1.0
 */
public class StringTools {
  /**
   * Escape character (as a String).
   *
   * @since 1.1
   */
  public static final String ESCAPE = "\\";

  /**
   * Open curly-bracket. Declaring this as a constant makes it easier to use in code.
   *
   * @since 1.1
   */
  public static final String OPEN = "{";

  /**
   * Close curly-bracket. Declaring this as a constant makes it easier to use in code.
   *
   * @since 1.1
   */
  public static final String CLOSE = "}";

  /**
   * A regular expression pattern for a quantifier string (which would be enclosed by curly-brackets).
   *
   * @since 1.1
   */
  public static final String QUALIFIER_PATTERN = "[0-9]+,{0,1}[0-9]*";

  /**
   * Array containing all the regex special characters.
   *
   * @since 1.0
   */
  public static final char[] REGEX_CHARS = { '\\', '^', '.', '$', '|', '(', ')', '[', ']', '{', '}', '*', '+', '?' };

  /**
   * String equivalents of REGEX_CHARS.
   *
   * @since 1.1
   */
  public static final String[] REGEX_STRS = { ESCAPE, "^", ".", "$", "|", "(", ")", "[", "]", OPEN, CLOSE, "*", "+", "?" };

  /**
   * An ascending Comparator for Strings.
   *
   * @since 1.0
   */
  public static final Comparator<String> COMPARATOR = new StringComparator(true);

  /**
   * ASCII charset.
   *
   * @since 1.0
   */
  public static final Charset ASCII = Charset.forName("US-ASCII");

  /**
   * UTF8 charset.
   *
   * @since 1.0
   */
  public static final Charset UTF8 = Charset.forName("UTF-8");

  /**
   * UTF16 charset.
   *
   * @since 1.0
   */
  public static final Charset UTF16 = Charset.forName("UTF-16");

  /**
   * UTF16 Little Endian charset.
   *
   * @since 1.0
   */
  public static final Charset UTF16LE = Charset.forName("UTF-16LE");

  /**
   * The line separator on the local machine.
   *
   * @since 1.0
   */
  public static final String LOCAL_CR = System.getProperty("line.separator");

  /**
   * Sort the array from A->Z (ascending ordering).
   *
   * @since 1.0
   */
  public static final String[] sort(String[] array) {
    return sort(array, true);
  }

  /**
   * Arrays can be sorted ascending or descending.
   *
   * @param asc true for ascending (A->Z), false for descending (Z->A).
   *
   * @since 1.0
   */
  public static final String[] sort(String[] array, boolean asc) {
    Arrays.sort(array, new StringComparator(asc));
    return array;
  }

  /**
   * A StringTokenizer operates on single-character tokens. This acts on a delimiter that is a multi-character String.
   *
   * @since 1.0
   */
  public static Iterator<String> tokenize(String target, String delimiter) {
    return new StringTokenIterator(target, delimiter);
  }

  /**
   * Gives you an option to keep any zero-length tokens at the ends of the target, if it begins or ends with the delimiter. This guarantees
   * that you get one token for every time the delimiter appears in the target String.
   *
   * @since 1.0
   */
  public static Iterator<String> tokenize(String target, String delimiter, boolean trim) {
    return new StringTokenIterator(target, delimiter, trim);
  }

  /**
   * Convert an Iterator of Strings to a List.
   *
   * @since 1.0
   */
  public static List<String> toList(Iterator<String> iter) {
    List<String> list = new Vector<String>();
    while (iter.hasNext()) {
      list.add(iter.next());
    }
    return list;
  }

  /**
   * Convert an array of Strings to a List.
   *
   * @since 1.0
   */
  public static List<String> toList(String[] sa) {
    List<String> list = new Vector<String>(sa.length);
    for (int i = 0; i < sa.length; i++) {
      list.add(sa[i]);
    }
    return list;
  }

  /**
   * Check for ASCII values between [A-Z] or [a-z].
   *
   * @since 1.0
   */
  public static boolean isLetter(int c) {
    return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
  }

  /**
   * Check for ASCII values between [0-9].
   *
   * @since 1.1
   */
  public static boolean isNumber(int c) {
    return c >= 48 && c <= 57;
  }

  /**
   * Convert a char array to a byte array using UTF16 encoding.
   *
   * @since 1.0.1
   */
  public static byte[] toBytes(char[] chars) {
    return UTF16.encode(CharBuffer.wrap(chars)).array();
  }

  public static byte[] toSzString(String s) {
    return (s + (char)0).getBytes(UTF16LE);
  }

  /**
   * Convert a char array to a byte array using the specified encoding.
   *
   * @since 1.0.1
   */
  public static byte[] toBytes(char[] chars, Charset charset) {
    return charset.encode(CharBuffer.wrap(chars)).array();
  }

  /**
   * Convert a byte array in the specified encoding to a char array.
   *
   * @since 1.0.1
   */
  public static char[] toChars(byte[] bytes, Charset charset) {
    return toChars(bytes, 0, bytes.length, charset);
  }

  /**
   * Convert len bytes of the specified array in the specified encoding, starting from offset, to a char array.
   *
   * @since 1.0.1
   */
  public static char[] toChars(byte[] bytes, int offset, int len, Charset charset) {
    return charset.decode(ByteBuffer.wrap(bytes, offset, len)).array();
  }

  /**
   * Escape any regular expression elements in the string. This is different from Pattern.quote, which simply puts the string inside of
   * \Q...\E.
   *
   * @since 1.0
   */
  public static String escapeRegex(String s) {
    Stack<String> delims = new Stack<String>();
    for (int i = 0; i < REGEX_STRS.length; i++) {
      delims.add(REGEX_STRS[i]);
    }
    return safeEscape(delims, s);
  }

  /**
   * Returns true if the specified String contains any regular expression syntax.
   *
   * @since 1.0
   */
  public static boolean containsRegex(String s) {
    for (String ch : REGEX_STRS) {
      if (s.indexOf(ch) != -1) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if the specified String contains any regular expression syntax that is not escaped.
   *
   * @since 1.0
   */
  public static boolean containsUnescapedRegex(String s) {
    for (int i = 1; i < REGEX_STRS.length; i++) { // skip ESCAPE
      int ptr = -1;
      while ((ptr = s.indexOf(REGEX_STRS[i], ptr + 1)) != -1) {
        if (!isEscaped(s, ptr)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Compiles a Perl-style regular expression with POSIX-style character classes into a Java regular expression.
   *
   * @since 1.0.1
   */
  public static Pattern pattern(String regex) throws PatternSyntaxException {
    return pattern(regex, 0);
  }

  /**
   * Compiles a Perl-style regular expression with POSIX-style character classes into a Java regular expression, with the specified flags
   * (from java.util.regex.Pattern).
   *
   * @since 1.0.1
   */
  public static Pattern pattern(String regex, int flags) throws PatternSyntaxException {
    return Pattern.compile(regexPosix2Java(regex), flags);
  }

  /**
   * Perform a substitution of POSIX character classes to Java character classes.
   *
   * @since 1.0
   */
  public static String regexPosix2Java(String pcre) {
    //
    // Escape all curly-brackets that are not:
    // 1) part of a Java character class
    // 2) part of a qualifier
    // 3) already escaped
    //
    StringBuffer sb = new StringBuffer();
    int start = 0;
    int next = pcre.indexOf(OPEN);
    if (next == -1) {
      sb.append(escapeUnescaped(pcre, CLOSE));
    } else {
      do {
        sb.append(escapeUnescaped(pcre.substring(start, next), CLOSE));
        if (isEscaped(pcre, next)) {
          sb.append(OPEN);
          start = next + 1;
        } else {
          int p2 = pcre.indexOf(CLOSE, next);
          if (p2 == -1) {
            sb.append(escapeUnescaped(pcre.substring(next), OPEN));
            start = pcre.length();
          } else {
            if (Pattern.matches(QUALIFIER_PATTERN, pcre.substring(next + 1, p2))) {
              // Qualifier
              sb.append(pcre.substring(next, p2 + 1));
              start = p2 + 1;
            } else if (next > 1 && !isEscaped(pcre, next - 2) && pcre.substring(next - 2, next).equals("\\p")) {
              // Java character class
              sb.append(pcre.substring(next, p2 + 1));
              start = p2 + 1;
            } else {
              sb.append("\\").append(OPEN);
              start = next + 1;
            }
          }
        }
      } while ((next = pcre.indexOf(OPEN, start)) != -1);
      sb.append(escapeUnescaped(pcre.substring(start), CLOSE));
    }
    String jcre = sb.toString();

    jcre = jcre.replace("[:digit:]", "\\p{Digit}");
    jcre = jcre.replace("[:alnum:]", "\\p{Alnum}");
    jcre = jcre.replace("[:alpha:]", "\\p{Alpha}");
    jcre = jcre.replace("[:blank:]", "\\p{Blank}");
    jcre = jcre.replace("[:xdigit:]", "\\p{XDigit}");
    jcre = jcre.replace("[:punct:]", "\\p{Punct}");
    jcre = jcre.replace("[:print:]", "\\p{Print}");
    jcre = jcre.replace("[:space:]", "\\p{Space}");
    jcre = jcre.replace("[:graph:]", "\\p{Graph}");
    jcre = jcre.replace("[:upper:]", "\\p{Upper}");
    jcre = jcre.replace("[:lower:]", "\\p{Lower}");
    jcre = jcre.replace("[:cntrl:]", "\\p{Cntrl}");
    return jcre;
  }

  /**
   * Perform a substitution of POSIX character classes to Unicode character classes.
   *
   * @since 1.0.1
   */
  public static String regexPosix2Powershell(String pcre) {
    String psExpression = pcre;
    psExpression = psExpression.replace("[:digit:]", "\\d");
    psExpression = psExpression.replace("[:alnum:]", "\\p{L}\\p{Nd}");
    psExpression = psExpression.replace("[:alpha:]", "\\p{L}");
    psExpression = psExpression.replace("[:blank:]", "\\p{Zs}\\t");
    psExpression = psExpression.replace("[:xdigit:]", "a-fA-F0-9");
    psExpression = psExpression.replace("[:punct:]", "\\p{P}");
    psExpression = psExpression.replace("[:print:]", "\\P{C}");
    psExpression = psExpression.replace("[:space:]", "\\s");
    psExpression = psExpression.replace("[:graph:]", "\\P{Z}\\P{C}");
    psExpression = psExpression.replace("[:upper:]", "\\p{Lu}");
    psExpression = psExpression.replace("[:lower:]", "\\p{Ll}");
    psExpression = psExpression.replace("[:cntrl:]", "\\p{Cc}");
    return psExpression;
  }

  // Private

  /**
   * Determine whether or not the character at ptr is preceeded by an even number of escape characters.
   */
  private static boolean isEscaped(String s, int ptr) {
    int escapes = 0;
    while (ptr-- > 0) {
      if ('\\' == s.charAt(ptr)) {
        escapes++;
      } else {
        break;
      }
    }
    //
    // If the character is preceded by an even number of escapes, then it is unescaped.
    //
    if (escapes % 2 == 0) {
      return false;
    }
    return true;
  }

  /**
   * Comparator implementation for Strings.
   */
  private static final class StringComparator implements Comparator<String>, Serializable {
    boolean ascending = true;

    /**
     * @param asc Set to true for ascending, false for descending.
     */
    StringComparator(boolean asc) {
      this.ascending = asc;
    }

    @Override
    public int compare(String s1, String s2) {
      if (ascending) {
        return s1.compareTo(s2);
      } else {
        return s2.compareTo(s1);
      }
    }

    @Override
    public boolean equals(Object obj) {
      return super.equals(obj);
    }
  }

  /**
   * Escape instances of the pattern in s which are not already escaped.
   */
  private static String escapeUnescaped(String s, String pattern) {
    StringBuffer sb = new StringBuffer();
    int last = 0;
    int next = 0;
    while ((next = s.indexOf(pattern, last)) != -1) {
      sb.append(s.substring(last, next));
      if (isEscaped(s, next)) {
        sb.append(pattern);
      } else {
        sb.append("\\").append(pattern);
      }
      last = next + pattern.length();
    }
    return sb.append(s.substring(last)).toString();
  }

  private static String safeEscape(Stack<String> delims, String s) {
    if (delims.empty()) {
      return s;
    } else {
      String delim = delims.pop();
      Stack<String> copy = new Stack<String>();
      copy.addAll(delims);
      List<String> list = StringTools.toList(StringTools.tokenize(s, delim, false));
      int len = list.size();
      StringBuffer result = new StringBuffer();
      for (int i = 0; i < len; i++) {
        if (i > 0) {
          result.append(ESCAPE);
          result.append(delim);
        }
        result.append(safeEscape(copy, list.get(i)));
      }
      return result.toString();
    }
  }

  static final class StringTokenIterator implements Iterator<String> {
    private String target, delimiter, next, last = null;
    int pointer;

    StringTokenIterator(String target, String delimiter) {
      this(target, delimiter, true);
    }

    StringTokenIterator(String target, String delimiter, boolean trim) {
      if (trim) {
        //
        // Trim tokens from the beginning and end.
        //
        int len = delimiter.length();
        if (target.startsWith(delimiter)) {
          target = target.substring(len);
        }
        if (target.endsWith(delimiter)) {
          target = target.substring(0, target.length() - len);
        }
      }

      this.target = target;
      this.delimiter = delimiter;
      pointer = 0;
    }

    @Override
    public boolean hasNext() {
      if (next == null) {
        try {
          next = next();
        } catch (NoSuchElementException e) {
          return false;
        }
      }
      return true;
    }

    @Override
    public String next() throws NoSuchElementException {
      if (next != null) {
        String tmp = next;
        next = null;
        return tmp;
      }
      int i = target.indexOf(delimiter, pointer);
      if (last != null) {
        String tmp = last;
        last = null;
        return tmp;
      } else if (pointer >= target.length()) {
        throw new NoSuchElementException("No tokens after " + pointer);
      } else if (i == -1) {
        String tmp = target.substring(pointer);
        pointer = target.length();
        return tmp;
      } else {
        String tmp = target.substring(pointer, i);
        pointer = (i + delimiter.length());
        if (pointer == target.length()) {
          // special case; append an empty token when ending with the token
          last = "";
        }
        return tmp;
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Remove not supported");
    }
  }
}
