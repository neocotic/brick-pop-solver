/*
 * Copyright (C) 2017 Alasdair Mercer
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

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class CustomToStringStyle extends ToStringStyle {

    public static final CustomToStringStyle LONG_STYLE = new LongToStringStyle();
    public static final CustomToStringStyle SHORT_STYLE = new ShortToStringStyle();

    private static final class LongToStringStyle extends CustomToStringStyle {

        private static final long serialVersionUID = 1L;

        LongToStringStyle() {
            super();

            setContentStart("(");
            setUseIdentityHashCode(false);
            setUseClassName(false);
            setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
            setFieldSeparatorAtStart(true);
            setContentEnd(SystemUtils.LINE_SEPARATOR + ")");
        }

        private Object readResolve() {
            return LONG_STYLE;
        }
    }

    private static final class ShortToStringStyle extends CustomToStringStyle {

        private static final long serialVersionUID = 1L;

        ShortToStringStyle() {
            super();

            setContentStart("(");
            setUseClassName(false);
            setUseIdentityHashCode(false);
            setUseFieldNames(false);
            setContentEnd(")");
        }

        private Object readResolve() {
            return SHORT_STYLE;
        }
    }

    protected CustomToStringStyle() {
        super();
    }
}
