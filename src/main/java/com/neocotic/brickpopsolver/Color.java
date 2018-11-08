/*
 * Copyright (C) 2018 Alasdair Mercer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.neocotic.brickpopsolver;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class Color implements Comparable<Color> {

    public static final Color EMPTY = new Color();

    private static final String EMPTY_CODE = "f7efe4";
    private static final String EMPTY_CODE_REPLACEMENT = "------";

    private static String toHex(final int value) {
        return StringUtils.leftPad(Integer.toString(value, 16), 2, '0');
    }

    private final String code;

    public Color() {
        this(EMPTY_CODE);
    }

    public Color(final int red, final int green, final int blue) {
        this(toHex(red) + toHex(green) + toHex(blue));
    }

    public Color(final String code) {
        this.code = Objects.requireNonNull(code, "code");
    }

    public boolean isEmpty() {
        return EMPTY_CODE.equals(code);
    }

    public String getCode() {
        return code;
    }

    @Override
    public int compareTo(final Color o) {
        return new CompareToBuilder()
            .append(code, o.code)
            .toComparison();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        final Color other = (Color) obj;
        return new EqualsBuilder()
            .append(code, other.code)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(code)
            .hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, CustomToStringStyle.SHORT_STYLE)
            .append("code", isEmpty() ? EMPTY_CODE_REPLACEMENT : code)
            .toString();
    }
}
