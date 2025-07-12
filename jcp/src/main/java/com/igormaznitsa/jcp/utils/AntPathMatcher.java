package com.igormaznitsa.jcp.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exctracted from <a href="https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/util/AntPathMatcher.java">Spring utility Ant pattern matcher</a>.
 * removed non-used methods and fields.
 */
public class AntPathMatcher {

  public static final String DEFAULT_PATH_SEPARATOR = "/";
  private static final int CACHE_TURNOFF_THRESHOLD = 65536;
  private static final char[] WILDCARD_CHARS = {'*', '?', '{'};
  private static final String[] EMPTY_STRING_ARRAY = {};
  final Map<String, AntPathStringMatcher> stringMatcherCache = new ConcurrentHashMap<>(256);
  private final boolean caseSensitive = true;
  private final boolean trimTokens = false;
  private final Map<String, String[]> tokenizedPatternCache = new ConcurrentHashMap<>(256);
  private final String pathSeparator;
  private volatile Boolean cachePatterns;

  public AntPathMatcher() {
    this.pathSeparator = DEFAULT_PATH_SEPARATOR;
  }

  public AntPathMatcher(String pathSeparator) {
    this.pathSeparator = pathSeparator;
  }

  public static String[] tokenizeToStringArray(
      String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

    if (str == null) {
      return EMPTY_STRING_ARRAY;
    }

    StringTokenizer st = new StringTokenizer(str, delimiters);
    List<String> tokens = new ArrayList<>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if (!ignoreEmptyTokens || !token.isEmpty()) {
        tokens.add(token);
      }
    }
    return toStringArray(tokens);
  }

  public static String[] toStringArray(Collection<String> collection) {
    return (!isEmpty(collection) ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
  }

  public static boolean isEmpty(Collection<?> collection) {
    return (collection == null || collection.isEmpty());
  }

  private void deactivatePatternCache() {
    this.cachePatterns = false;
    this.tokenizedPatternCache.clear();
    this.stringMatcherCache.clear();
  }

  public boolean match(String pattern, String path) {
    return doMatch(pattern, path, true);
  }

  protected boolean doMatch(String pattern, String path, boolean fullMatch) {

    if (path == null ||
        path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
      return false;
    }

    String[] pattDirs = tokenizePattern(pattern);
    if (this.caseSensitive && !isPotentialMatch(path, pattDirs)) {
      return false;
    }

    String[] pathDirs = tokenizePath(path);
    int pattIdxStart = 0;
    int pattIdxEnd = pattDirs.length - 1;
    int pathIdxStart = 0;
    int pathIdxEnd = pathDirs.length - 1;

    while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
      String pattDir = pattDirs[pattIdxStart];
      if ("**".equals(pattDir)) {
        break;
      }
      if (!matchStrings(pattDir, pathDirs[pathIdxStart])) {
        return false;
      }
      pattIdxStart++;
      pathIdxStart++;
    }

    if (pathIdxStart > pathIdxEnd) {
      if (pattIdxStart > pattIdxEnd) {
        return (pattern.endsWith(this.pathSeparator) == path.endsWith(this.pathSeparator));
      }
      if (!fullMatch) {
        return true;
      }
      if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") &&
          path.endsWith(this.pathSeparator)) {
        return true;
      }
      for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
        if (!pattDirs[i].equals("**")) {
          return false;
        }
      }
      return true;
    } else if (pattIdxStart > pattIdxEnd) {
      return false;
    } else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
      return true;
    }

    while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
      String pattDir = pattDirs[pattIdxEnd];
      if (pattDir.equals("**")) {
        break;
      }
      if (!matchStrings(pattDir, pathDirs[pathIdxEnd])) {
        return false;
      }
      if (pattIdxEnd == (pattDirs.length - 1)
          && pattern.endsWith(this.pathSeparator) != path.endsWith(this.pathSeparator)) {
        return false;
      }
      pattIdxEnd--;
      pathIdxEnd--;
    }
    if (pathIdxStart > pathIdxEnd) {
      for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
        if (!pattDirs[i].equals("**")) {
          return false;
        }
      }
      return true;
    }

    while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
      int patIdxTmp = -1;
      for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
        if (pattDirs[i].equals("**")) {
          patIdxTmp = i;
          break;
        }
      }
      if (patIdxTmp == pattIdxStart + 1) {
        pattIdxStart++;
        continue;
      }
      int patLength = (patIdxTmp - pattIdxStart - 1);
      int strLength = (pathIdxEnd - pathIdxStart + 1);
      int foundIdx = -1;

      strLoop:
      for (int i = 0; i <= strLength - patLength; i++) {
        for (int j = 0; j < patLength; j++) {
          String subPat = pattDirs[pattIdxStart + j + 1];
          String subStr = pathDirs[pathIdxStart + i + j];
          if (!matchStrings(subPat, subStr)) {
            continue strLoop;
          }
        }
        foundIdx = pathIdxStart + i;
        break;
      }

      if (foundIdx == -1) {
        return false;
      }

      pattIdxStart = patIdxTmp;
      pathIdxStart = foundIdx + patLength;
    }

    for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
      if (!pattDirs[i].equals("**")) {
        return false;
      }
    }

    return true;
  }

  private boolean isPotentialMatch(String path, String[] pattDirs) {
    if (!this.trimTokens) {
      int pos = 0;
      for (String pattDir : pattDirs) {
        int skipped = skipSeparator(path, pos, this.pathSeparator);
        pos += skipped;
        skipped = skipSegment(path, pos, pattDir);
        if (skipped < pattDir.length()) {
          return (skipped > 0 || (!pattDir.isEmpty() && isWildcardChar(pattDir.charAt(0))));
        }
        pos += skipped;
      }
    }
    return true;
  }

  private int skipSegment(String path, int pos, String prefix) {
    int skipped = 0;
    for (int i = 0; i < prefix.length(); i++) {
      char c = prefix.charAt(i);
      if (isWildcardChar(c)) {
        return skipped;
      }
      int currPos = pos + skipped;
      if (currPos >= path.length()) {
        return 0;
      }
      if (c == path.charAt(currPos)) {
        skipped++;
      }
    }
    return skipped;
  }

  private int skipSeparator(String path, int pos, String separator) {
    int skipped = 0;
    while (path.startsWith(separator, pos + skipped)) {
      skipped += separator.length();
    }
    return skipped;
  }

  private boolean isWildcardChar(char c) {
    for (char candidate : WILDCARD_CHARS) {
      if (c == candidate) {
        return true;
      }
    }
    return false;
  }

  protected String[] tokenizePattern(String pattern) {
    String[] tokenized = null;
    Boolean cachePatterns = this.cachePatterns;
    if (cachePatterns == null || cachePatterns) {
      tokenized = this.tokenizedPatternCache.get(pattern);
    }
    if (tokenized == null) {
      tokenized = tokenizePath(pattern);
      if (cachePatterns == null && this.tokenizedPatternCache.size() >= CACHE_TURNOFF_THRESHOLD) {
        deactivatePatternCache();
        return tokenized;
      }
      if (cachePatterns == null || cachePatterns) {
        this.tokenizedPatternCache.put(pattern, tokenized);
      }
    }
    return tokenized;
  }

  protected String[] tokenizePath(String path) {
    return tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);
  }

  private boolean matchStrings(String pattern, String str) {
    return getStringMatcher(pattern).matchStrings(str);
  }

  protected AntPathStringMatcher getStringMatcher(String pattern) {
    AntPathStringMatcher matcher = null;
    Boolean cachePatterns = this.cachePatterns;
    if (cachePatterns == null || cachePatterns) {
      matcher = this.stringMatcherCache.get(pattern);
    }
    if (matcher == null) {
      matcher = new AntPathStringMatcher(pattern, this.caseSensitive);
      if (cachePatterns == null && this.stringMatcherCache.size() >= CACHE_TURNOFF_THRESHOLD) {
        deactivatePatternCache();
        return matcher;
      }
      if (cachePatterns == null || cachePatterns) {
        this.stringMatcherCache.put(pattern, matcher);
      }
    }
    return matcher;
  }

  protected static class AntPathStringMatcher {

    private static final Pattern GLOB_PATTERN =
        Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");

    private static final String DEFAULT_VARIABLE_PATTERN = "((?s).*)";

    private final String rawPattern;

    private final boolean caseSensitive;

    private final boolean exactMatch;

    private final Pattern pattern;

    public AntPathStringMatcher(String pattern, boolean caseSensitive) {
      this.rawPattern = pattern;
      this.caseSensitive = caseSensitive;
      StringBuilder patternBuilder = new StringBuilder();
      Matcher matcher = GLOB_PATTERN.matcher(pattern);
      int end = 0;
      while (matcher.find()) {
        patternBuilder.append(quote(pattern, end, matcher.start()));
        String match = matcher.group();
        if ("?".equals(match)) {
          patternBuilder.append('.');
        } else if ("*".equals(match)) {
          patternBuilder.append(".*");
        } else if (match.startsWith("{") && match.endsWith("}")) {
          int colonIdx = match.indexOf(':');
          if (colonIdx == -1) {
            patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
          } else {
            String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
            patternBuilder.append('(');
            patternBuilder.append(variablePattern);
            patternBuilder.append(')');
          }
        }
        end = matcher.end();
      }
      if (end == 0) {
        this.exactMatch = true;
        this.pattern = null;
      } else {
        this.exactMatch = false;
        patternBuilder.append(quote(pattern, end, pattern.length()));
        this.pattern = (this.caseSensitive ? Pattern.compile(patternBuilder.toString()) :
            Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE));
      }
    }

    private String quote(String s, int start, int end) {
      if (start == end) {
        return "";
      }
      return Pattern.quote(s.substring(start, end));
    }

    public boolean matchStrings(String str) {
      if (this.exactMatch) {
        return this.caseSensitive ? this.rawPattern.equals(str) :
            this.rawPattern.equalsIgnoreCase(str);
      } else if (this.pattern != null) {
        Matcher matcher = this.pattern.matcher(str);
        return matcher.matches();
      }
      return false;
    }

  }
}
