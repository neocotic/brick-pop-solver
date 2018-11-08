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
package com.neocotic.brickpopsolver.image.opencv;

import java.util.Objects;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neocotic.brickpopsolver.Color;
import com.neocotic.brickpopsolver.image.Image;
import com.neocotic.brickpopsolver.image.ImageFormat;

public final class OpenCVImage implements Image {

    private static final Logger logger = LoggerFactory.getLogger(OpenCVImage.class);

    private static final Color BLACK = new Color(0, 0, 0);

    private final ImageFormat format;
    private final Mat matrix;

    public OpenCVImage(final ImageFormat format, final Mat matrix) {
        this.format = Objects.requireNonNull(format, "format");
        this.matrix = Objects.requireNonNull(matrix, "matrix");
    }

    @Override
    public ImageFormat getFormat() {
        return format;
    }

    @Override
    public int getHeight() {
        return matrix.rows();
    }

    @Override
    public Color getPixel(final int x, final int y) {
        logger.trace("getPixel:enter(x={}, y={})", x, y);

        final UByteIndexer indexer = matrix.createIndexer();

        try {
            final int blue = indexer.get(y, x, 0);
            final int green = indexer.get(y, x, 1);
            final int red = indexer.get(y, x, 2);
            final Color color = new Color(red, green, blue);

            logger.trace("getPixel:exit(color)");
            return color;
        } finally {
            indexer.release();
        }
    }

    @Override
    public int getWidth() {
        return matrix.cols();
    }

    @Override
    public boolean isValid() {
        return matrix.channels() >= 3 && !BLACK.equals(getPixel(getWidth() / 2, getHeight() / 2));
    }
}
