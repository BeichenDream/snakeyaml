package org.yaml.snakeyaml.external.biz.binaryEscape;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.Scanner;

public class BinaryEscape {

    public static String escape(byte[] buf) {
        String strHex = "";
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < buf.length; n++) {
            strHex = Integer.toHexString(buf[n] & 0xFF);
            sb.append("\\x");
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return sb.toString();
    }
    public static String escapeStr(String str) {
        return escape(str.getBytes());
    }

    public static String unescape(byte[] buf) {
        String strHex = "";
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < buf.length; n++) {
            strHex = Integer.toHexString(buf[n] & 0xFF);
            sb.append("\\x");
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return sb.toString();
    }

    public static String unescapeToStr(String str) {
        return new String(unescapeToBytes(str));
    }

    public static byte[] unescapeToBytes(String str) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StringIterator stringIterator = new StringIterator(str);
        while (stringIterator.hasNext()) {
            final char next = stringIterator.next();
            if (next == '\\' && stringIterator.hasNext()) {
                final char next2 = stringIterator.next();
                if (next2 == 'u') {
                    if (!stringIterator.hasNext(4)) {
                        throw new UnsupportedOperationException("not enough remaining characters for \\uXXXX"+stringIterator.getErrorToken());
                    }
                    else {
                        final String next3 = stringIterator.next(4);
                        try {
                            outputStream.write((char)Integer.parseInt(next3, 16));
                        }
                        catch (NumberFormatException ex) {
                            throw new UnsupportedOperationException("invalid unicode escape \\u" + next3 + " - must be hex digits" + stringIterator.getErrorToken());
                        }
                    }
                }
                else if (next2 == 'x') {
                    if (!stringIterator.hasNext(2)) {
                        throw new UnsupportedOperationException("not enough remaining characters for \\uXXXX"+stringIterator.getErrorToken());
                    }
                    else {
                        final String next4 = stringIterator.next(2);
                        try {
                            outputStream.write(Integer.parseInt(next4, 16));
                        }
                        catch (NumberFormatException ex2) {
                            throw new UnsupportedOperationException("invalid unicode escape \\x" + next4 + " - must be hex digits"+stringIterator.getErrorToken());
                        }
                    }
                }
                else if (next2 == 'n') {
                    outputStream.write('\n');
                }
                else if (next2 == 'r') {
                    outputStream.write('\r');
                }
                else if (next2 == 't') {
                    outputStream.write('\t');
                }else if (next2 == 'b'){
                    outputStream.write('\b');
                }
                else if (next2 == 'f'){
                    outputStream.write('\f');
                }
                else if (next2 == '\\') {
                    outputStream.write('\\');
                }
                else if (next2 == '\"') {
                    outputStream.write('\"');
                }
                else if (next2 == '\'') {
                    outputStream.write('\'');
                }
                else {
                    throw new UnsupportedOperationException("unknown escape \\" + next2 + stringIterator.getErrorToken());
                }
            }
            else {
                try {
                    outputStream.write(Character.toString(next).getBytes());
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }
        return outputStream.toByteArray();
    }
}
 class StringIterator {
    protected int position = 0;

    protected int lineNo;

    protected char[] text;

    protected String texts;

    protected int begin = 0;

    protected LinkedList mark1 = new LinkedList();

    protected LinkedList mark2 = new LinkedList();

    public StringIterator(String paramString) { this(paramString, 0); }

    public String toString() { return this.texts; }

    public StringIterator(String paramString, int paramInt) {
        this.texts = paramString;
        this.text = paramString.toCharArray();
        this.lineNo = paramInt;
    }

    public boolean hasNext() { return (this.position < this.text.length); }

    public boolean hasNext(int paramInt) { return (this.position + paramInt - 1 < this.text.length); }

    public int getLineNumber() { return this.lineNo; }

    public String getErrorToken() { return String.format(" EntireLine:%s LineNumber:%d LineMarker:%d",getEntireLine(), getLineNumber(), getLineMarker()); }

    public String getEntireLine() {
        int i;
        for (i = this.position; i < this.text.length && this.text[i] != '\n'; i++);
        return this.texts.substring(this.begin, i);
    }

    public int getLineMarker() { return this.position - this.begin; }

    public boolean isNextString(String paramString) { return (this.position + paramString.length() <= this.text.length && this.texts.substring(this.position, this.position + paramString.length()).equals(paramString)); }

    public boolean isNextChar(char paramChar) { return (hasNext() && this.text[this.position] == paramChar); }

    public char peek() { return hasNext() ? this.text[this.position] : Character.MIN_VALUE; }

    public void skip(int paramInt) { this.position += paramInt; }

    public String next(int paramInt) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b = 0; b < paramInt; b++)
            stringBuffer.append(next());
        return stringBuffer.toString();
    }

    public char next() {
        char c = this.text[this.position];
        if (this.position > 0 && this.text[this.position - 1] == '\n') {
            this.lineNo++;
            this.begin = this.position;
        }
        this.position++;
        return c;
    }

    public void mark() {
        this.mark1.add(0, new Integer(this.position));
        this.mark2.add(0, new Integer(this.lineNo));
    }

    public String reset() {
        Integer integer1 = (Integer)this.mark1.removeFirst();
        Integer integer2 = (Integer)this.mark2.removeFirst();
        return this.texts.substring(integer1.intValue(), this.position);
    }
}
