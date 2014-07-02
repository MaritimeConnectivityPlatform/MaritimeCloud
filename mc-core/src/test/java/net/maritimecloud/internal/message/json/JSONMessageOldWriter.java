/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.maritimecloud.internal.message.json;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.maritimecloud.core.message.MessageEnum;
import net.maritimecloud.core.message.MessageSerializable;
import net.maritimecloud.core.message.MessageWriter;
import net.maritimecloud.util.Binary;
import net.maritimecloud.util.geometry.Position;
import net.maritimecloud.util.geometry.PositionTime;

/**
 *
 * @author Kasper Nielsen
 */
@Deprecated
public class JSONMessageOldWriter extends MessageWriter {

    /** The Unix line separator. */
    static final String LS = "\n";

    /** The current number of indents. */
    private int indent;

    /** The print writer to write to */
    private final Writer pw;

    boolean isFirst = true;

    public JSONMessageOldWriter(PrintWriter pw) {
        this.pw = requireNonNull(pw);
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        pw.close();
    }

    /** {@inheritDoc} */
    @Override
    public void flush() throws IOException {
        pw.flush();
    }

    /**
     * Adds the specified count of spaces to the specified string builder.
     *
     * @param sb
     *            the string builder to add to
     * @param count
     *            the number of spaces to add
     * @return the specified string builder
     * @throws IOException
     */
    private void indent() throws IOException {
        for (int i = 0; i < indent; i++) {
            pw.append("  ");
        }
    }

    void writeBinary(Binary binary) throws IOException {
        pw.append("\"");
        pw.write(binary.base64encode());
        pw.append("\"");
    }

    /** {@inheritDoc} */
    @Override
    public void writeBinary(int tag, String name, Binary value) throws IOException {
        if (value != null) {
            writeTag(tag, name);
            writeBinary(value);
        }
    }

    void writeBool(boolean value) throws IOException {
        pw.write(Boolean.toString(value));
    }

    /** {@inheritDoc} */
    @Override
    public void writeBoolean(int tag, String name, Boolean value) throws IOException {
        if (value != null) {
            writeTag(tag, name);
            writeBool(value);
        }
    }

    void writeDouble(double value) throws IOException {
        if (!Double.isFinite(value)) {
            throw new IOException("Cannot write double value " + value);
        }
        pw.write(Double.toString(value));
    }

    /** {@inheritDoc} */
    @Override
    public void writeDouble(int tag, String name, Double value) throws IOException {
        if (value != null) {
            writeTag(tag, name);
            writeDouble(value);
        }
    }

    void writeVarInt(BigInteger value) throws IOException {
        pw.write(value.toString());
    }

    void writeDecimal(BigDecimal value) throws IOException {
        pw.write(value.toString());
    }

    void writeTimestamp(Date value) throws IOException {
        writeInt64(value.getTime());
    }

    void writePosition(Position value) throws IOException {
        pw.write(value.toString());
    }

    void writePositionTime(PositionTime value) throws IOException {
        pw.write(value.toString());
    }


    void writeElement(Object value) throws IOException {
        if (value instanceof MessageSerializable) {
            writeMessage((MessageSerializable) value);
        } else if (value instanceof List || value instanceof Set) {
            writeListOrSet((Collection<?>) value);
        } else if (value instanceof MessageEnum) {
            writeEnum((MessageEnum) value);
        } else if (value instanceof String) {
            writeString((String) value);
        } else if (value instanceof Binary) {
            writeBinary((Binary) value);
        } else if (value instanceof Long) {
            writeInt64((Long) value);
        } else if (value instanceof Integer) {
            writeInt32((Integer) value);
        } else if (value instanceof Double) {
            writeDouble((Double) value);
        } else if (value instanceof Float) {
            writeFloat((Float) value);
        } else if (value instanceof Boolean) {
            writeBool((Boolean) value);
        } else {
            throw new IOException("Don't know how to write instances of " + value.getClass());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void writeEnum(int tag, String name, MessageEnum value) throws IOException {
        if (value != null) {
            writeTag(tag, name);
            writeEnum(value);
        }
    }

    void writeEnum(MessageEnum enumValue) throws IOException {
        writeString(enumValue.getName());
    }

    void writeFloat(float value) throws IOException {
        if (!Float.isFinite(value)) {
            throw new IOException("Cannot write float value " + value);
        }
        pw.write(Float.toString(value));
    }

    /** {@inheritDoc} */
    @Override
    public void writeFloat(int tag, String name, Float value) throws IOException {
        if (value != null) {
            writeTag(tag, name);
            writeFloat(value);
        }
    }

    void writeInt32(int value) throws IOException {
        pw.write(Integer.toString(value));
    }

    /** {@inheritDoc} */
    @Override
    public void writeInt(int tag, String name, Integer value) throws IOException {
        if (value != null) {
            writeTag(tag, name);
            writeInt32(value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void writeInt64(int tag, String name, Long value) throws IOException {
        if (value != null) {
            writeTag(tag, name);
            writeInt64(value);
        }
    }

    void writeInt64(long value) throws IOException {
        pw.write(Long.toString(value));
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException
     */
    @Override
    public void writeList(int tag, String name, List<?> list) throws IOException {
        if (requireNonNull(list, "list is null").size() > 0) {
            writeTag(tag, name);
            writeListOrSet(list);
        }
    }

    void writeListOrSet(Collection<?> list) throws IOException {
        pw.write("[");
        indent++;
        boolean isFirst = true;
        for (Object o : list) {
            if (!isFirst) {
                pw.write(",");
            }
            pw.write(LS);
            indent();
            writeElement(o);
            isFirst = false;
        }
        pw.write(LS);
        indent--;
        indent();
        pw.append("]");
    }

    /** {@inheritDoc} */
    @Override
    public void writeMap(int tag, String name, Map<?, ?> map) {}

    /** {@inheritDoc} */
    @Override
    public void writeMessage(int tag, String name, MessageSerializable message) throws IOException {
        if (message != null) {
            writeTag(tag, name);
            writeMessage(message);
        }
    }

    public void writeMessage(MessageSerializable message) throws IOException {
        pw.write("{");
        indent++;
        boolean isFirst = this.isFirst;
        this.isFirst = true;
        message.writeTo(this);
        this.isFirst = isFirst;
        indent--;
        pw.write(LS);
        indent();
        pw.write("}");
    }

    @Override
    public void writeSet(int tag, String name, Set<?> set) throws IOException {
        if (requireNonNull(set, "set is null").size() > 0) {
            writeTag(tag, name);
            writeListOrSet(set);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void writeText(int tag, String name, String value) throws IOException {
        if (value != null) {
            writeTag(tag, name);
            writeString(value);
        }
    }

    void writeString(String value) throws IOException {
        pw.write('"');
        pw.write(escapeString(value));
        pw.write('"');
    }

    private JSONMessageOldWriter writeTag(int tag, String name) throws IOException {
        if (!isFirst) {
            pw.write(",");
        }
        isFirst = false;
        pw.write(LS);
        indent();
        pw.write("\"" + name + "\": ");
        return this;
    }

    // There are no methods for unescaping JSON strings, we leave that to javax.json
    static String escapeString(String string) {
        StringBuilder sb = new StringBuilder();
        int len = string.length();
        for (int i = 0; i < len; i++) {
            char ch = string.charAt(i);
            switch (ch) {
            case '"':
                sb.append("\\\"");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            case '/':
                sb.append("\\/");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            default:
                if (ch >= '\u0000' && ch <= '\u001F' || ch >= '\u007F' && ch <= '\u009F' || ch >= '\u2000'
                && ch <= '\u20FF') {
                    String ss = Integer.toHexString(ch);
                    sb.append("\\u");
                    for (int k = 0; k < 4 - ss.length(); k++) {
                        sb.append('0');
                    }
                    sb.append(ss.toUpperCase());
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    public static String toString(Object o) throws IOException {
        StringWriter sw = new StringWriter();
        @SuppressWarnings("resource")
        JSONMessageOldWriter w = new JSONMessageOldWriter(new PrintWriter(sw));
        try {
            w.writeElement(o);
        } finally {
            w.close();
        }
        return sw.toString();
    }

    /** {@inheritDoc} */
    @Override
    public void writeVarInt(int tag, String name, BigInteger value) throws IOException {}

    /** {@inheritDoc} */
    @Override
    public void writeDecimal(int tag, String name, BigDecimal value) throws IOException {}

    /** {@inheritDoc} */
    @Override
    public void writePosition(int tag, String name, Position value) throws IOException {}

    /** {@inheritDoc} */
    @Override
    public void writePositionTime(int tag, String name, PositionTime value) throws IOException {}

    /** {@inheritDoc} */
    @Override
    public void writeTimestamp(int tag, String name, Date value) throws IOException {}
}